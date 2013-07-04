package com.lwan.javafx.controls.other;

import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.ModifiedEventType;
import com.lwan.javafx.app.App;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BODatePicker;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.bo.binding.BoundCellValue;
import com.lwan.javafx.controls.bo.binding.ComputedCellValue;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.BoundProperty;
import com.lwan.javafx.controls.bo.binding.StringBoundProperty;
import com.lwan.util.FxUtils;
import com.lwan.util.StringUtil;
import com.lwan.util.wrappers.CallbackEx;
import com.lwan.util.wrappers.Disposable;
import com.lwan.util.wrappers.Procedure;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 * Grid class used to display BusinessObject records from a BOSet.
 * 
 * 
 * @author Brutalbarbarian
 *
 * @param <R>
 */
public class BOGrid<R extends BusinessObject> extends TableView<R> implements ModifiedEventListener, Disposable{
	public static final String PREFIX_CALCULATED = "$CALC$";
	
	/**
	 * Editing is per record basis.
	 * What this means is that when leaving a record, to edit a different record, this
	 * current record is saved. Similarly when calling grid.save(), only this record is
	 * saved. The edit state of this grid depends on the current selected record. 
	 * 
	 */
	public static final int MODE_RECORD = 0;
	/**
	 * Editing is per set basis.
	 * What this means is that when leaving a record, nothing is saved. All records are
	 * assumed to be part of an unified set. When calling grid.save(), trySave() is called
	 * on the set as opposed to the individual record. The edit state of this grid depends
	 * on the state of the set as opposed to any individual record.
	 * 
	 * Effectively, this is means theres nothing internal controlling the save.
	 * It is recommended to use this mode if the set is displaying reference data.
	 */
	public static final int MODE_SET = 1;
	
	
	private Property<Integer> gridModeProperty;
	private Property<Boolean> isEditingProperty;
	private BOLinkEx<BOSet<R>> link;
	
	public Property <Integer> gridModeProperty() {
		if (gridModeProperty == null) {
			gridModeProperty = new SimpleObjectProperty<Integer>(this, "GridMode", MODE_RECORD);
		}
		return gridModeProperty;
	}
	
	public Property<Boolean> isEditingProperty() {
		if (isEditingProperty == null) {
			isEditingProperty = new SimpleObjectProperty<Boolean>(this, "IsEditing", false);
		}
		return isEditingProperty;
	}
	
	public BOSet<R> getSourceSet() {
		return link.getLinkedObject();
	}
	
	private CallbackEx<R, String, String> displayValueCallback;
	public void setDisplayValueCallback(CallbackEx<R, String, String> callback) {
		displayValueCallback = callback;
	}
	public String getDisplayValue(R item, String fieldName) {
		if (displayValueCallback == null) {
			return "";
		} else {
			String value = displayValueCallback.call(item, fieldName);
			if (value == null) {
				return "";
			} else {
				return value;
			}
		}
	}
	
	private String key;
	private GridView<R> view;	// the owning GridView object
	
	protected GridView<R> getView() {
		return view;
	}
	
	@SuppressWarnings("rawtypes")
	protected BOGrid(String key, GridView<R> view, BOLinkEx<BOSet<R>> link, 
			String [] columnNames, String[] fieldPaths, boolean[] editable) {
		this.view = view;
		// initialize the grid columns?
		if (columnNames == null || fieldPaths == null || 
				columnNames.length != fieldPaths.length) {
			throw new RuntimeException("Invalid arguments for column details.");
		}
		
		this.link = link;
		int cols = columnNames.length;
		List<GridColumn> columns = new Vector<>();
		double defaultRelativeWidth = 1d / cols;
		for (int i = 0; i < cols; i++) {
			columns.add(new GridColumn(columnNames[i], fieldPaths[i], 
					editable == null? true : editable[i], defaultRelativeWidth));
		}
		
		getColumns().setAll(columns);

		link.addListener(this);
		refresh();
				
		selected = null;
		revertingSelection = false;
		getSelectionModel().selectedItemProperty().addListener(new ChangeListener<R>() {
			public void changed(ObservableValue<? extends R> arg0, final R oldValue,
					R newValue) {
				if (!revertingSelection) {
					if (gridModeProperty().getValue() == MODE_RECORD) {
						if (isEditingProperty().getValue()) {
							// Attempt to trigger save...
							try {
								save();
							} catch (final RuntimeException e) {
								// Revert back to the previous selection.
								Platform.runLater(new Runnable(){
									public void run() {
										revertingSelection = true;
										try {
											getSelectionModel().select(oldValue);
											// Attempt to focus user on the cell?...
											if (e instanceof BOException) {
												BOException ex = (BOException)e;
												GridColumn col = getColumnByField(ex.getSource().getName());
												if (col != null) {
													edit(getSelectionModel().getSelectedIndex() , col);
												}
											}
										} finally {
											revertingSelection = false;
										}
									}									
								});
								
							}
						}
						if (newValue != null && 
								newValue.isModified()) {
							isEditingProperty().setValue(true);
						}
					}
				}
				selected = newValue;	// Keep a local copy of the last selected item.
			}
		});
		
		addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>(){
			public void handle(KeyEvent e) {
				KeyCode code = e.getCode();
				if (code != KeyCode.TAB &&
						e.getText().trim().length() > 0) {
					TablePosition<R, ?> pos = editingCellProperty().get();
					int row = getSelectionModel().getSelectedIndex();
					if ((pos == null || pos.getRow() < 0) && row >= 0) {
						// start edit
						edit(row, getColumns().get(0));	// first column
					}
				}
			}
		});
		
		this.key = key;
		
		setSkin(new CustomTableViewSkin(this));

		firstRealLayout = true;
		
//		setTableMenuButtonVisible(true);
		columnResizePolicyProperty().set(new Callback<ResizeFeatures, Boolean>() {
			@SuppressWarnings({ "unchecked", "deprecation" })
			public Boolean call(ResizeFeatures rf) {
				if (firstRealLayout) {
					// don't bother respecting min and max widths here.
					
					double width = getContentWidth();
					
					if (width <= 0 || getVisibleLeafColumns().isEmpty()) {
						return true;
					}

					double totalPercent = 0;
					double minPercentage = 1 / getVisibleLeafColumns().size();
					for (TableColumn<R, ?> column : getVisibleLeafColumns()) {
						GridColumn col = (GridColumn)column;
						col.relativeWidth = Math.max(col.relativeWidth, minPercentage);
						
						totalPercent += col.relativeWidth;
					}

					double percentMultiplier = 1 / totalPercent;
					for (TableColumn<R, ?> column : getColumns()) {
						if (column.isVisible()) {
							GridColumn col = (GridColumn)column;

							double newWidth = col.relativeWidth * percentMultiplier * width;

							col.impl_setWidth(newWidth);
						}
					}

					firstRealLayout = false;
				} else {
					// need to respect min and max widths as this is likely to be a user change.
					double width = getContentWidth();
					if (width <= 0) {
						return false;	// wtf
					}
					
					
					double spaceAvaliable;
					double actualTotalPercent;
					List<GridColumn> remainingColumns = new LinkedList<>();
					
					if (rf.getColumn() != null) {
						// need to check if the new size is allowed
						double oldWidth = rf.getColumn().getWidth();
						double newWidth = oldWidth + rf.getDelta();
						if (newWidth < rf.getColumn().getMinWidth()) {
							newWidth = rf.getColumn().getMinWidth();
						} else if (newWidth > rf.getColumn().getMaxWidth()) {
							newWidth = rf.getColumn().getMaxWidth();
						}
						double delta = newWidth - oldWidth;	// this will ensure its constraints
						
						if (delta == 0) {
							return false;
						}
						
						// find out how much space is left on the right after this change
						
						boolean foundColumn = false;
						actualTotalPercent = 0;
						spaceAvaliable = width;
						for (TableColumn<R, ?> column : getVisibleLeafColumns()) {
							if (foundColumn) {
								remainingColumns.add((GridColumn) column);
								actualTotalPercent += ((GridColumn)column).relativeWidth;
							} else if (column == rf.getColumn()) {
								spaceAvaliable -= newWidth;
								foundColumn = true;
							} else {
								spaceAvaliable -= column.getWidth();
							}
						}
						if (spaceAvaliable < 0 || (spaceAvaliable > 0 && remainingColumns.isEmpty())) {
							return false;	// not enough space left, or too much space left
						}
						
						// check min and max width constraints
						double totalMin = 0, totalMax = 0;
						for (GridColumn col : remainingColumns) {
							totalMin += col.getMinWidth();
							totalMax += col.getMaxWidth();
						}
						
						if (totalMin > spaceAvaliable || totalMax < totalMax) {
							return false;	// not enough space to satisfy the space constraints
						}
						
						// from this point on, we know we've definitely got enough space
						rf.getColumn().impl_setWidth(newWidth);	// can safely do this now
						((GridColumn)rf.getColumn()).relativeWidth = newWidth / width;
					} else {
						// do total rebalancing.
						remainingColumns = new LinkedList<>();
						actualTotalPercent = 0;
						for (TableColumn<R, ?> column : getVisibleLeafColumns()) {
							GridColumn col = (GridColumn)column;
							actualTotalPercent += col.relativeWidth;
							remainingColumns.add((GridColumn)column);
						}
						spaceAvaliable = width;
					}
					
					double targetTotalPercent = spaceAvaliable / width;	
					double multiplier = targetTotalPercent / actualTotalPercent;
					
					// update all relative widths here, ignoring constraints.
					for (GridColumn col : remainingColumns) {
						col.relativeWidth = col.relativeWidth * multiplier;
					}
					
					int diff = -1;
					while (diff != 0) {
						diff = 0;
						Iterator<GridColumn> it = remainingColumns.iterator();
						while (it.hasNext()) {
							GridColumn col = it.next();
							double targetWidth = col.relativeWidth * multiplier * width;
							if (targetWidth < col.getMinWidth()) {
								targetWidth = col.getMinWidth();
							} else if (targetWidth > col.getMaxWidth()) {
								targetWidth = col.getMaxWidth();
							} else {
								continue;
							}
							spaceAvaliable -= targetWidth;
							targetTotalPercent = spaceAvaliable / width;
							actualTotalPercent -= col.relativeWidth;
							
							multiplier = targetTotalPercent / actualTotalPercent;
							
							col.impl_setWidth(targetWidth); 
							diff++;
							
							it.remove();
						}
					}
					
					// go through each of them assuming the relativeWidth is correct
					Iterator<GridColumn> it = remainingColumns.iterator();
					while (it.hasNext()) {
						if (it.hasNext()) {
							GridColumn col = it.next();
							double targetWidth;
							if (it.hasNext()) {
								targetWidth = Math.floor(col.relativeWidth * spaceAvaliable / actualTotalPercent);
								actualTotalPercent -= col.relativeWidth;
								spaceAvaliable -= targetWidth;
							} else {
								targetWidth = spaceAvaliable;
							}
							col.impl_setWidth(targetWidth);
						}
					}
					if (getView() != null) {
						getView().updateLayout();
					}
				}
				return true;
			}						
		});
	}
	
	private boolean firstRealLayout;

	private class CustomTableViewSkin extends TableViewSkin<R> {
		VirtualScrollBar vbar;
		
		public CustomTableViewSkin(TableView<R> tableView) {
			super(tableView);
			
			// find the freakin vbar... since its not exposed
			findVBar(flow);
			// assuming we've found the vbar... make sure we update the layout when vbar visiblity changes
//			vbar.visibleProperty().addListener(new ChangeListener<Boolean>(){
//				@SuppressWarnings({ "unchecked", "rawtypes" })
//				public void changed(ObservableValue<? extends Boolean> arg0,
//						Boolean arg1, Boolean arg2) {
//					getColumnResizePolicy().call(new ResizeFeatures(BOGrid.this, null, 0d));
//				}				
//			});
		}
		
		private boolean findVBar(Node n) {
			if ((n instanceof VirtualScrollBar)) {
				VirtualScrollBar scrollbar = (VirtualScrollBar)n;
				if (scrollbar.getOrientation() == Orientation.VERTICAL) {
					vbar = scrollbar;
					return true;	
				} else {
					return false;
				}
			} else if (n instanceof Parent) {
				Parent p = (Parent)n;
				for (Node child : p.getChildrenUnmodifiable()) {
					if (findVBar(child)) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		}
		
		
		protected double getContentWidth() {
			double width = getWidth();
			if (vbar.isVisible()) {
				width = width - vbar.getWidth();
			}
			if (getPadding() != null) {
				width = width - getPadding().getLeft() - getPadding().getRight();
			}
			return width;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected double getContentWidth() {
		return ((CustomTableViewSkin)getSkin()).getContentWidth();
	}
	
	protected void displayLayout() {
		String layout = App.getKey(key);
		if (StringUtil.isNullOrBlank(layout)) {
			return;	// nothing to display
		}
		
		String[] cols = layout.split("%");
		int i = 0;
		for (String s : cols) {
			String[] strs = s.split(":");
			if (i == (cols.length - 1)) {	// the last...
				GridColumn col = getColumnByField(strs[0]);
				if (col != null) {
					getSortOrder().add(col);
				}
				getView().getFooter().setVisible(Boolean.parseBoolean(strs[1]));
			} else {
				String field = strs[0];
				double width = Double.parseDouble(strs[1]);
				boolean visible = Boolean.parseBoolean(strs[2]);
				String sort = strs[3];
				SortType st = sort.length() == 0? null : SortType.valueOf(sort);
				
				GridColumn column = getColumnByField(field);
				column.relativeWidth = width;
				column.setVisible(visible);
				if (st != null) {
					column.setSortType(st);
				}
				
				GridView<R>.FooterColumn footerCol = getView().getFooter().getColumnForField(field);
				String[] footerStrs = strs[4].substring(1, strs[4].length() - 1).split(",");
				for (String footerStr : footerStrs) {
					if (footerStr.length() > 0) {
						int type = Integer.parseInt(footerStr);
						footerCol.ensureFooterColumnRowExists(type);
					}
				}
//				String footerStrs = footerStr.
				
				getColumns().remove(column);
				getColumns().add(i, column);
				
				i++;
			}
		}
	}
	
	// format for layout
	// fieldname:relativewidth:visible:sort:num_of_footer_visible_displayed, next_num_of_footer_type%**rest_of_field**%fieldname_of_sort_order:footer_visible
	
	@SuppressWarnings("unchecked")
	protected void saveLayout() {
		// save the order of each column and their respective widths
		StringBuilder builder = new StringBuilder();
		for (TableColumn<R, ?> c : getColumns()) {			
			GridColumn col = (GridColumn)c;
			
			double width = col.relativeWidth;
			String field = col.getField();
			boolean visible = col.isVisible();
			SortType st = col.getSortType();
			String sort = st == null? "" : st.toString();
			
			
			builder.append(field).append(':').append(width).append(':').
					append(visible).append(':').append(sort).append(":[");
			
			boolean isFirst = true;
			GridView<R>.FooterColumn footerColumn = view.getFooter().getColumnForField(field);
			for (GridView<R>.FooterColumnRow row : footerColumn.rows) {
				if (!isFirst) {
					builder.append(',');
				}
				builder.append(row.footerType);
				isFirst = false;
			}
			builder.append(']');
			
			builder.append('%');
		}
		
		if (getSortOrder().size() > 0) {
			builder.append(((GridColumn)getSortOrder().get(0)).getField());
		}
		builder.append(":").append(view.getFooter().isVisible());
		
		App.putKey(key, builder.toString());
	}
	
	@Override
	public void dispose() {
		saveLayout();
		
		link.removeListener(this);

		getItems().clear();
		view = null;
	}
	
	boolean revertingSelection;
	private R selected;
	
	private Property<Callback<R, Boolean>> onSaveProperty;
	public Property<Callback<R, Boolean>> onSaveProperty() {
		if (onSaveProperty == null) {
			onSaveProperty = new SimpleObjectProperty<>(this, "OnSave", null);
		}
		return onSaveProperty;		
	}
	public void setOnSave(Callback<R, Boolean> onSave) {
		onSaveProperty().setValue(onSave);
	}
	public Callback<R, Boolean> getOnSave(){
		if (onSaveProperty == null) {
			return null;
		} else {
			return onSaveProperty().getValue();
		}
	}
	
	protected void callOnSave(R item) throws RuntimeException {
		if(getOnSave() != null) {
			if (!getOnSave().call(item)) {
				throw new RuntimeException();
			}
		}
	}
	
	public void save() throws BOException {
		try {
			// No point continuing if not in edit mode...
			if (isEditingProperty().getValue()) {
				if (gridModeProperty().getValue() == MODE_RECORD) {
					if (selected != null && selected.isActive()) {
						callOnSave(selected);
						selected.trySave();
					}
				} else if (gridModeProperty().getValue() == MODE_SET) {
					BOSet<R> set = link.getLinkedObject();
					if (set != null) {
						callOnSave(selected);
						set.trySave();
					}
				} else {
					throw new RuntimeException("Unknown grid mode set.");
				}
				
				// If it makes it up to this point without any exceptions,
				// set editing state to false.
				isEditingProperty().setValue(false);
			}
		} catch (RuntimeException e) {
			FxUtils.ShowErrorDialog(getScene().getWindow(), e.getMessage());			
			throw e;
		}
	}
	
	protected BOLinkEx<BOSet<R>> getLink() {
		return link;
	}
	
	@SuppressWarnings("unchecked")
	public GridColumn getColumnByCaption(String title) {
		for (TableColumn<?, ?> col : getColumns()) {
			if (col.getText().equals(title)) {
				return (GridColumn)col;
			}
		}
		return null;	// Can't find
	}
	
	@SuppressWarnings("unchecked")
	public GridColumn getColumnByField(String fieldPath) {
		for (TableColumn<?, ?> col : getColumns()) {
			GridColumn c = (GridColumn)col;
			if (c.getField().equals(fieldPath)) {
				return c;
			}
		}
		return null;	// Can't find
	}

	public void refresh() {
		revertingSelection = true;
		try {
			BOSet<R> source = link.getLinkedObject();
			if (source == null) {
				// Make sure there aren't any items in the list.
				getItems().clear();
				return;
			}
			
			R item = getSelectionModel().getSelectedItem();
			
			// Need to force reload the grid or no?
			// don't...
			
			List<R> aItems = new Vector<R>(source.getActiveCount());
			for (R record : source) {
				aItems.add(record);
			}
			Iterator<R> existing = getItems().iterator();
			while(existing.hasNext()) {
				R record = existing.next();
				if(aItems.contains(record)) {
					aItems.remove(record);
				} else {
					existing.remove();
				}
			}
			// Any remaining items, add them all
			getItems().addAll(aItems);
			
			// Attempt to reselect what was previously selected
			if (item != null) {
				getSelectionModel().select(item);
			}
			
			// make sure editing is still in sync
			if (gridModeProperty().getValue() == MODE_RECORD) {
				if (item == null) {
					isEditingProperty().setValue(false);
				} else {
					isEditingProperty().setValue(item.isModified());
				}
			} else {
				isEditingProperty().setValue(source.isModified());
			}
		} finally {
			revertingSelection = false;
		}
	}
	
	public class GridColumn extends TableColumn<R, Object> {
		private String fieldPath;
		private Map<String, Object> params;	// Extra params for the cellFactory
		private Procedure<BoundControl<?>> ctrlPropertySetter;
		private double relativeWidth;
		
		GridColumn(String caption, String fieldPath, boolean editable, double defaultRelativeWidth) {
			super(caption);
			
			this.fieldPath = fieldPath;
			params = new Hashtable<>();
			
			if (fieldPath.startsWith(PREFIX_CALCULATED)) {
				setCellValueFactory(new ComputedCellValue<R>(BOGrid.this, getLink(), fieldPath));
				setEditable(false);
				setAsReadOnlyField();
			} else {
				setCellValueFactory(new BoundCellValue<Object, R>(fieldPath));
				setEditable(editable);
				setAsTextField();	// Default
			}
						
			setOnEditCommit(new EventHandler<CellEditEvent<R, Object>>() {
				public void handle(CellEditEvent<R, Object> arg0) {
					// do nothing
					// this is intentional as it overrides the default action
				}				
			});
			
			setPrefWidth(100);
			relativeWidth = defaultRelativeWidth;			
		}
		
		protected AttributeType getAttributeType() {
			if (fieldPath.startsWith(PREFIX_CALCULATED)) {
				return AttributeType.Unknown;
			} else if (getSourceSet() == null) {
				return AttributeType.Unknown;
			} else {
				return getSourceSet().getExampleChild().
						findAttributeByPath(fieldPath).getAttributeType();
			}
		}
		
		public Procedure<BoundControl<?>> getCtrlPropertySetter() {
			return ctrlPropertySetter;
		}
		
		public void setCtrlPropertySetter(Procedure<BoundControl<?>> setter) {
			ctrlPropertySetter = setter;
		}
		
		@SuppressWarnings("unchecked")
		protected <T> T getParam(String paramName) {
			// Just assume T is already of correct type...
			return (T)params.get(paramName);
		}
		
		public void setAsTextField() {
			params.clear();
			setCellFactory(getTextfieldCellFactory());
		}
		
		public void setAsReadOnlyField() {
			params.clear();
			setCellFactory(getReadOnlyCellFactory());
		}
		
		public <B extends BusinessObject> void setAsCombobox(BOSet<B> set, String keyPath, String attributePath) {
			params.clear();
			
			params.put("keyPath", keyPath);
			params.put("attributePath", attributePath);
			params.put("set", set);
			
			setCellFactory(getComboboxCellFactory());
		}
		
		public void setAsDatePicker() {
			params.clear();
			setCellFactory(getDatePickerCellFactory());
		}
		
		public String getField() {
			return fieldPath;
		}
	}

	private Callback<TableColumn<R, Object>, TableCell<R, Object>> textfieldCellFactory;
	private Callback<TableColumn<R, Object>, TableCell<R, Object>> getTextfieldCellFactory() {
		if (textfieldCellFactory == null) {
			textfieldCellFactory = new Callback<TableColumn<R, Object>, TableCell<R, Object>>(){
				public TextfieldGridCell call(TableColumn<R, Object> arg0) {
					return new TextfieldGridCell();
				}
			};
		}
		return textfieldCellFactory;
	}
	
	private Callback<TableColumn<R, Object>, TableCell<R, Object>> readOnlyCellFactory;
	private Callback<TableColumn<R, Object>, TableCell<R, Object>> getReadOnlyCellFactory() {
		if (readOnlyCellFactory == null) {
			readOnlyCellFactory = new Callback<TableColumn<R, Object>, TableCell<R, Object>>() {
				public TableCell<R, Object> call(TableColumn<R, Object> arg0) {
					return new ReadOnlyGridCell();
				}				
			};
		}
		return readOnlyCellFactory;
	}
	
	private Callback<TableColumn<R, Object>, TableCell<R, Object>> comboboxCellFactory;
	private Callback<TableColumn<R, Object>, TableCell<R, Object>> getComboboxCellFactory() {
		if (comboboxCellFactory == null) {
			comboboxCellFactory = new Callback<TableColumn<R, Object>, TableCell<R, Object>>(){
				public ComboboxGridCell call(TableColumn<R, Object> arg0) {
					return new ComboboxGridCell();
				}
			};
		}
		return comboboxCellFactory;
	}
	
	private Callback<TableColumn<R, Object>, TableCell<R, Object>> datePickerCellFactory;
	private Callback<TableColumn<R, Object>, TableCell<R, Object>> getDatePickerCellFactory() {
		if (datePickerCellFactory == null) {
			datePickerCellFactory = new Callback<TableColumn<R, Object>, TableCell<R, Object>>(){
				public DatePickerGridCell call(TableColumn<R, Object> arg0) {
					return new DatePickerGridCell();
				}
			};
		}
		return datePickerCellFactory;
	}
	
	public class ReadOnlyGridCell extends TableCell<R, Object>{		
		@Override
		protected void updateItem(Object item, boolean empty) {
			super.updateItem(item, empty);
			
			setText(item == null? "" : item.toString());
		}
	}
	
	public class TextfieldGridCell extends CustomGridCell {
		private BOTextField textField;
		
		protected StringBoundProperty getBindingProperty() {
			return (StringBoundProperty) super.getBindingProperty();
		}
		
		public void doCommitEdit() {
			try {
				getBindingProperty().endEdit(true);
			} catch (BOException e) {
				// This is fine... don't force user. This will revert if the user was attempting
				// to move away. 
				return;	
//				JavaFXUtil.ShowErrorDialog(getScene().getWindow(), e.getMessage());  
			}
		}
		
		private void createTextField() {
			if (!linkIsActive()) {
				throw new RuntimeException("Inactive link on createTextField()");
			}
			
			textField = new BOTextField(getBindingProperty());
			textField.selectAllOnEditProperty().setValue(true);
			textField.setMinWidth(getWidth() - getGraphicTextGap() * 2);
			textField.focusTraversableProperty().set(false);
			textField.externalControlledProperty().set(true);
			
			textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> arg0, 
						Boolean arg1, Boolean arg2) {
					if (!arg2 && textField != null) {						
						Scene sc = getScene();
						// if no owner, or the focus owner isn't a child of the textField...
						if (sc == null || !FxUtils.isChildOf(sc.getFocusOwner(), getTableView())) {
							commitEdit();
						}
					}
				}
			});
			textField.setOnKeyPressed(new EventHandler<KeyEvent>(){
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.TAB) {
						commitEdit();
						gotoNextColumn(!t.isShiftDown());
					}
				}
				
			});
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						commitEdit();
					} else if (t.getCode() == KeyCode.ESCAPE) {
						// Is this needed? seems to be already implemented in...
						cancelEdit();
					}
				}
			});
		}
		
		@Override
		protected String getDisplayValue() {
			boolean release = !linkIsActive();
			try {
				initBinding();	// Will do nothing if linkIsActive is already true
				
				return getBindingProperty().getValue();
			} finally {
				if (release) {
					releaseBinding();
				}
			}
		}

		@Override
		protected Node getEditControl() {
			if (textField == null) {
				createTextField();
			}
			return textField;
		}

		@Override
		protected void doCancelEdit() {
			try {
				// Force cancel
				if (getBindingProperty() != null) {
					getBindingProperty().endEdit(false);
				}
				
				textField = null;
			} catch (BOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void doStartEdit() {
			Platform.runLater(new Runnable() {
				public void run() {
					if (isEditing()) {
						getBindingProperty().beginEdit();
						textField.requestFocus();	// This should be selecting all..
					}
				}
			});
		}

		@Override
		protected BoundProperty<?> createBindingProperty(Object owner, BOLinkEx<R> link) {
			return new StringBoundProperty(owner, link, null);
		}
	}
	
	protected abstract class CustomGridCell extends TableCell<R, Object>{
		private BoundProperty<?> bindingProperty;
		private BOLinkEx<R> link;
		private boolean linkIsActive;
		
		protected abstract String getDisplayValue();
		protected abstract Node getEditControl();
		protected abstract void doCancelEdit();
		
		protected abstract void doStartEdit();
		
		protected abstract BoundProperty<?> createBindingProperty(Object owner,
				BOLinkEx<R> link);
		
		protected CustomGridCell() {
			linkIsActive = false;
		}
		
		protected BoundProperty<?> getBindingProperty () {
			return bindingProperty;
		}
		
		protected boolean linkIsActive() {
			return linkIsActive;
		}
		
		protected void initBinding() {
			if (!linkIsActive) {
				if (link == null) {
					link = new BOLinkEx<>();
				}
				if (bindingProperty == null) {
					bindingProperty = createBindingProperty(getTableView(), link);
				}
				bindingProperty.pathProperty().setValue(getColumn().getField());
				link.setLinkedObject(getRecord());
				bindingProperty.buildAttributeLinks();
				
				linkIsActive = true;
			}
		}
		
		protected void releaseBinding() {
			if(linkIsActive) {
				link.setLinkedObject(null);
				bindingProperty.pathProperty().setValue(null);
				bindingProperty.buildAttributeLinks();
				
				// free up memory?
				bindingProperty = null;
				link = null;
				
				linkIsActive = false;
			}
		}
		
		public void startEdit() {
			GridColumn column = getColumn();
			
			if (!column.isEditable()) {
				return;	// Not sure how it event got here...
			}
			
			initBinding();
			
			if (!getBindingProperty().editableProperty().get()) {
				releaseBinding();
				return;
			}
			
			super.startEdit();
			
			Node editControl = getEditControl();
			if ((editControl instanceof BoundControl) && 
					column.getCtrlPropertySetter() != null) {
				column.getCtrlPropertySetter().call((BoundControl<?>)editControl);
			}

			setText(null);
			setGraphic(editControl);
			
			doStartEdit();			
		}
		
		/**
		 * Commits the edit
		 * The parameter won't actually do anything.
		 * Better to just call commitEdit()
		 * 
		 */
		@Deprecated
		public final void commitEdit(Object item) {
			doCommitEdit();
			super.commitEdit(item);
			cancelEdit();
		}
		
		/**
		 * Effectively same as calling commitEdit(null)
		 * as the parameter dosen't matter anyway.
		 * 
		 */
		public final void commitEdit() {
			commitEdit(null);
		}
		
		protected abstract void doCommitEdit();
		
		public void updateItem(Object item, boolean empty) {
			if (getRecord() == null) {
				return;	// do nothing
			}
			
			if (!isEditing()) {
				super.updateItem(item,  false);
			}
			
			if (empty && !isEditing()) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					// do nothing
				} else {
					setText(getDisplayValue());
				}
			}
		}
		
		public void cancelEdit() {
			super.cancelEdit();
			doCancelEdit();
			
			releaseBinding();
			
			setText(getDisplayValue());
			setGraphic(null);
		}
		
		/**
		 * Same as (BOGrid<T>.GridColumn)getTableColumn();
		 * 
		 * @return
		 */
		public GridColumn getColumn() {
			return (GridColumn)super.getTableColumn();
		}
		
		@SuppressWarnings("unchecked")
		public R getRecord() {
			TableRow<R> row = getTableRow();
			return row == null? null : (R)row.getItem();
		}
		
		protected void gotoNextColumn(boolean foward) {
			GridColumn nextColumn = getNextColumn(foward);
			if (nextColumn != null) {
				getTableView().edit(getTableRow().getIndex(), nextColumn);
			}
		}
	
		@SuppressWarnings("unchecked")
		protected GridColumn getNextColumn(boolean foward) {
			List<GridColumn> columns = new Vector<>();
			for (TableColumn<?, ?> col : getTableView().getColumns()) {
				addColumnLeaves(columns, (GridColumn)col);
			}
			if (columns.size() < 2) {
				return null;
			}
			int currentIndex = columns.indexOf(getTableColumn());
			int nextIndex = currentIndex + (foward? 1 : -1);
			if (nextIndex < 0 || nextIndex >= columns.size()) {
				return null;
			} else {
				return columns.get(nextIndex);
			}
		}
		
		@SuppressWarnings("unchecked")
		protected void addColumnLeaves(List<GridColumn> columns, GridColumn root) {
			if (root.getColumns().isEmpty()) {
				// Only add if editable
				if (root.isEditable() && 
						getRecord().findAttributeByPath(root.getField()).allowUserModifyProperty().getValue()) {
					columns.add(root);
				}
			} else {
				// Add all child columns...
				for (TableColumn<?, ?> col : root.getColumns()) {
					addColumnLeaves(columns, (GridColumn)col);
				}
			}
		}
	}

	
	public class ComboboxGridCell extends CustomGridCell {
		private BOComboBox<?> combobox;
		
		@Override
		protected String getDisplayValue() {
			GridColumn col = getColumn();
			
			BOSet<?> set = col.getParam("set");
			boolean release = !linkIsActive();
			try {
				initBinding();
				BusinessObject bo = set.findChildByAttribute(
						(String)col.getParam("keyPath"), getBindingProperty().get());
				if (bo == null) {
					return "";
				}
				BOAttribute<?> attr = bo.findAttributeByPath(
						(String)col.getParam("attributePath"));
				if (attr == null) {
					return "";
				}
				
				return attr.asString();
			} finally {
				if (release) {
					releaseBinding();
				}
			}
		}

		@Override
		protected Node getEditControl() {
			if (combobox == null) {
				createCombobox();
			}
			
			return combobox;
		}
		
		public void doCommitEdit() {
			if (combobox != null) {
				combobox.forceCommit();
			}
		}

		private void createCombobox() {
			if (!linkIsActive()) {
				throw new RuntimeException("Inactive link on createCombobox()");
			}
			
			combobox = new BOComboBox<>(getBindingProperty());
			combobox.setMinWidth(getWidth() - getGraphicTextGap() * 2);
			
			GridColumn column = getColumn();
			BOSet<R> set = column.getParam("set");
			combobox.setSource(set, (String)column.getParam("keyPath"), 
					(String)column.getParam("attributePath"), null);
			
			combobox.focusedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> arg0,
						Boolean arg1, Boolean arg2) {
					if (!arg2) {
						Scene sc = getScene();
						
						if (sc == null || !FxUtils.isChildOf(sc.getFocusOwner(), getTableView())) {
							commitEdit();
						}
					}
				}
			});
			

			combobox.setOnKeyPressed(new EventHandler<KeyEvent>() {
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.TAB) {
						commitEdit();
						
						gotoNextColumn(!t.isShiftDown());
					} else if (t.getCode() == KeyCode.HOME || 
							t.getCode() == KeyCode.END) {
						// don't want the home and end events reaching the grid
						t.consume();
					}
				}				
			});
			
			combobox.setOnKeyReleased(new EventHandler<KeyEvent>() {
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER ||
							t.getCode() == KeyCode.ESCAPE) {
						commitEdit();
					}
				}
			});
		}

		@Override
		protected void doCancelEdit() {
			combobox = null;
		}

		@Override
		protected void doStartEdit() {
			Platform.runLater(new Runnable() {
				public void run() {
					combobox.requestFocus();
				}
			});
		}

		@Override
		protected BoundProperty<?> createBindingProperty(Object owner,
				BOLinkEx<R> link) {
			return new BoundProperty<>(owner, link, null);
		}
	}
	
	public class DatePickerGridCell extends CustomGridCell {
		private BODatePicker datePicker;
		
		@SuppressWarnings("unchecked")
		protected BoundProperty<Date> getBindingProperty() {
			return (BoundProperty<Date>)super.getBindingProperty();
		}
		
		@Override
		protected String getDisplayValue() {
			boolean release = !linkIsActive();
			try {
				initBinding();				
				return DateFormat.getDateInstance(DateFormat.SHORT, App.getLocale()).format(
						getBindingProperty().get());
			} finally {
				if (release) {
					releaseBinding();
				}
			}
		}

		@Override
		protected Node getEditControl() {
			if (datePicker == null) {
				createDatePicker();
			}
			return datePicker;
		}
		
		public void doCommitEdit() {
			if (linkIsActive()) {
				datePicker.endEdit(true, true);
			}
		}
		
		protected void createDatePicker() {
			datePicker = new BODatePicker(getBindingProperty());
			
			datePicker.getTextField().focusedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> arg0,
						Boolean arg1, Boolean arg2) {
					if (!arg2) {
						Scene sc = getScene();
						if (sc == null || !FxUtils.isChildOf(sc.getFocusOwner(), getTableView())) {
							commitEdit();
						}
					}
				}				
			});

			datePicker.getTextField().setOnKeyPressed(new EventHandler<KeyEvent>() {
				public void handle(KeyEvent e) {
					if (e.getCode() == KeyCode.TAB) {
						commitEdit();
						gotoNextColumn(!e.isShiftDown());
					} else if (e.getCode() == KeyCode.ENTER) {
						commitEdit();
					} else if (e.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					}
				}				
			});
		}

		@Override
		protected void doCancelEdit() {
			datePicker = null;
		}

		@Override
		protected void doStartEdit() {
			Platform.runLater(new Runnable() {
				public void run() {
					if (isEditing()) {
						datePicker.getTextField().requestFocus();
//						datePicker.showPopup();
					}
				}
			});
		}

		@Override
		protected BoundProperty<?> createBindingProperty(Object owner,
				BOLinkEx<R> link) {
			return new BoundProperty<Date>(owner, link, null);
		}
		
	}

	@Override
	public void handleModified(ModifiedEvent event) {
		if (event.getType() == ModifiedEventType.Active ||
				event.getType() == ModifiedEventType.Link) {
			refresh();
		} else if (event.getType() == ModifiedEventType.Attribute) {
			if (event.isUserModified() && 
					// If this is the case, likely the sourceset has params,
					// as sets should never have any fields. Thus ignore.
					event.getAttributeOwner() != getSourceSet()) {
				isEditingProperty().setValue(true);
			}
		}	
	}
}
