package com.lwan.javafx.controls.bo;

import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.javafx.app.App;
import com.lwan.javafx.controls.bo.binding.BoundCellValue;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.BoundProperty;
import com.lwan.javafx.controls.bo.binding.StringBoundProperty;
import com.lwan.util.JavaFXUtil;
import com.lwan.util.wrappers.Disposable;
import com.lwan.util.wrappers.Procedure;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
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
public class BOGrid<R extends BusinessObject> extends TableView<R> implements Disposable{
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
	
	private String key;
	
	public BOGrid(String key, BOLinkEx<BOSet<R>> link, 
			String [] columnNames, String[] fieldPaths, boolean[] editable) {
		// initialize the grid columns?
		if (columnNames == null || fieldPaths == null || 
				columnNames.length != fieldPaths.length) {
			throw new RuntimeException("Invalid arguments for column details.");
		}
		
		int cols = columnNames.length;
		List<GridColumn> columns = new Vector<>();
		for (int i = 0; i < cols; i++) {
			columns.add(new GridColumn(columnNames[i], fieldPaths[i], 
					editable == null? true : editable[i]));
		}
		
		getColumns().setAll(columns);

		this.link = link;
		link.addListener(new ModifiedEventListener() {
			public void handleModified(ModifiedEvent event) {
				if (event.getType() == ModifiedEvent.TYPE_ACTIVE) {
					refresh();
				} else if (event.getType() == ModifiedEvent.TYPE_ATTRIBUTE) {
					if (event.isUserModified() && 
							// If this is the case, likely the sourceset has params,
							// as sets should never have any fields. Thus ignore.
							event.getAttributeOwner() != getSourceSet()) {
						isEditingProperty().setValue(true);
					}
				}
			}
		});
		// Not sure if this is a good idea...
		link.LinkedObjectProperty().addListener(new ChangeListener<BOSet<R>>() {
			public void changed(ObservableValue<? extends BOSet<R>> arg0,
					BOSet<R> arg1, BOSet<R> arg2) {
				refresh();
			}
		});
		refresh();
		
//		columnResizePolicyProperty().set(CONSTRAINED_RESIZE_POLICY);		
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
							} catch (final BOException e) {
								// Notify User
								JavaFXUtil.ShowErrorDialog(getScene().getWindow(), e.getMessage());
								
								// Revert back to the previous selection.
								Platform.runLater(new Runnable(){
									public void run() {
										revertingSelection = true;
										try {
											getSelectionModel().select(oldValue);
											// Attempt to focus user on the cell?...
											GridColumn col = getColumnByField(e.getSource().getName());
											if (col != null) {
												edit(getSelectionModel().getSelectedIndex() , col);
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
		final String layout = App.getKey(key);
		if (layout != null) {
			Platform.runLater(new Runnable(){
				public void run() {
					displayLayout(layout);		
				}				
			});			
		}
	}
	
	protected void displayLayout(String layout) {
		String[] cols = layout.split("%");
		int i = 0;
		for (String s : cols) {
			String[] strs = s.split(":");
			if (i == (cols.length - 1)) {
				GridColumn col = getColumnByField(s);
				if (col != null) {
					getSortOrder().add(col);
				}
			} else {
			
				String field = strs[0];
				double width = Double.parseDouble(strs[1]);
				boolean visible = Boolean.parseBoolean(strs[2]);
				String sort = strs[3];
				SortType st = sort.length() == 0? null : SortType.valueOf(sort);
				
				GridColumn column = getColumnByField(field);
				column.setPrefWidth(width);
				column.setVisible(visible);
				if (st != null) {
					column.setSortType(st);
				}
				
				getColumns().remove(column);
				getColumns().add(i, column);
				
				i++;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void saveLayout() {
		// save the order of each column and their respective widths
		StringBuilder builder = new StringBuilder();
		for (TableColumn<R, ?> c : getColumns()) {			
			GridColumn col = (GridColumn)c;
			
			double width = col.getWidth();
			String field = col.getField();
			boolean visible = col.isVisible();
			SortType st = col.getSortType();
			String sort = st == null? "" : st.toString();
			
			
			builder.append(field).append(':').append(width).append(':').
					append(visible).append(':').append(sort);
			
			builder.append('%');
		}
		
		if (getSortOrder().size() > 0) {
			builder.append(((GridColumn)getSortOrder().get(0)).getField());
		}
		
		App.putKey(key, builder.toString());
	}
	
	@Override
	public void dispose() {
		saveLayout();
	}
	
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}
	
	boolean revertingSelection;
	private R selected;
	
	public void save() throws BOException {
		// No point continuing if not in edit mode...
		if (isEditingProperty().getValue()) {
			if (gridModeProperty().getValue() == MODE_RECORD) {
				if (selected != null && selected.isActive()) {
					selected.trySave();
				}
			} else if (gridModeProperty().getValue() == MODE_SET) {
				BOSet<R> set = link.getLinkedObject();
				if (set != null) {
					set.trySave();
				}
			} else {
				throw new RuntimeException("Unknown grid mode set.");
			}
			
			// If it makes it up to this point without any exceptions,
			// set editing state to false.
			isEditingProperty().setValue(false);
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
		
		GridColumn(String caption, String fieldPath, boolean editable) {
			super(caption);
			
			this.fieldPath = fieldPath;
			params = new Hashtable<>();
			
			setEditable(editable);
			
			setCellValueFactory(new BoundCellValue<Object, R>(fieldPath));
			setAsTextField();	// Default
			
			setPrefWidth(100);	// Minimum?
			
			setOnEditCommit(new EventHandler<CellEditEvent<R, Object>>() {
				public void handle(CellEditEvent<R, Object> arg0) {
					// do nothing
				}				
			});
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
						if (sc == null || !JavaFXUtil.isChildOf(sc.getFocusOwner(), textField)) {
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
						
						if (sc == null || !JavaFXUtil.isChildOf(sc.getFocusOwner(), getTableView())) {
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
						if (sc == null || !JavaFXUtil.isChildOf(sc.getFocusOwner(), getTableView())) {
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
}
