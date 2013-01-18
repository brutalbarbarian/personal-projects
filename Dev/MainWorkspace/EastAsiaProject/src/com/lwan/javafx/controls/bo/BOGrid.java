package com.lwan.javafx.controls.bo;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.State;
import com.lwan.javafx.controls.bo.binding.BoundCellValue;
import com.lwan.javafx.controls.bo.binding.StringBoundProperty;
import com.sun.glass.ui.Application;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
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
public class BOGrid<R extends BusinessObject> extends TableView<R>{
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
	
	public BOGrid(BOLinkEx<BOSet<R>> link, 
			String [] columnNames, String[] fieldPaths, boolean[] editable) {
		// initialize the grid columns?
		if (columnNames == null || fieldPaths == null || 
				columnNames.length != fieldPaths.length) {
			throw new RuntimeException("Invalid arguments for column details.");
		}
		
		// TODO need to create a generic te cell... how to specify custom columns???
		// don't care about custom columns for now...we just want a working grid.
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
					if (event.isUserModified()) {
						// TODO check if the attribute is actually one of the fields??
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
		
		selected = null;
		revertingSelection = false;
		getSelectionModel().selectedItemProperty().addListener(new ChangeListener<R>() {
			public void changed(ObservableValue<? extends R> arg0, R oldValue,
					R newValue) {
				if (!revertingSelection) {
					if (gridModeProperty().getValue() == MODE_RECORD) {
						if (isEditingProperty().getValue()) {
							// Attempt to trigger save...
							try {
								save();
							} catch (BOException e) {
								// Revert back to the previous selection.
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
						}
						if (newValue.stateProperty().getValue().contains(State.Modified)) {
							isEditingProperty().setValue(true);
						}
					}
				}
				selected = newValue;	// Keep a local copy of the last selected item.
			}
		});
	}
	boolean revertingSelection;
	private R selected;
	
	public void save() throws BOException {
		// No point continuing if not in edit mode...
		if (isEditingProperty().getValue()) {
			if (gridModeProperty().getValue() == MODE_RECORD) {
				if (selected != null) {
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
		BOSet<R> source = link.getLinkedObject();
		if (source == null) {
			// Make sure there aren't any items in the list.
			getItems().clear();
			return;
		}
		
		R item = getSelectionModel().getSelectedItem();
		
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
	}
	
	public class GridColumn extends TableColumn<R, Object> {
		private String fieldPath;
		
		GridColumn(String caption, String fieldPath, boolean editable) {
			super(caption);
			
			this.fieldPath = fieldPath;
			
			setEditable(editable);
			
			setCellValueFactory(new BoundCellValue<Object, R>(fieldPath));
			setCellFactory(getDefaultCellFactory());
			
			setPrefWidth(100);	// Minimum?
		}
		
		public String getField() {
			return fieldPath;
		}
	}

	private Callback<TableColumn<R, Object>, TableCell<R, Object>> defaultCellFactory;
	private Callback<TableColumn<R, Object>, TableCell<R, Object>> getDefaultCellFactory() {
		if (defaultCellFactory == null) {
			defaultCellFactory = new Callback<TableColumn<R, Object>, TableCell<R, Object>>(){
				public GridCell call(TableColumn<R, Object> arg0) {
					return new GridCell();
				}
			};
		}
		return defaultCellFactory;
	}
	
	
	public class GridCell extends TableCell<R, Object> {
		private StringBoundProperty bindingProperty;
		private BOLinkEx<R> link;
		
		private GridCell() {

		}
		
		protected void initBinding() {
			if(link == null) {
				link = new BOLinkEx<>();
			}
			if (bindingProperty == null) {
				bindingProperty = new StringBoundProperty(getTableView(), link, null);
			}
			bindingProperty.pathProperty().setValue(getColumn().getField());
			link.setLinkedObject(getRecord());
			bindingProperty.buildAttributeLinks();
		}
		
		protected void releaseBinding() {
			if (link != null && bindingProperty != null) {
				link.setLinkedObject(null);
				bindingProperty.pathProperty().setValue(null);
				bindingProperty.buildAttributeLinks();
			}
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
		
		private BOTextField textField;
		
		public void startEdit() {
			if (!getColumn().isEditable()) {
				return;	// Not sure how it even got here...
			}
			
			initBinding();
			
			if (!bindingProperty.editableProperty().get()) {
				releaseBinding();
				return;	// The bound attribute has final say on editable
			}
			
			super.startEdit();
			
			if (textField == null || link == null) {
				createTextField();
			}
			
			setText(null);
			setGraphic(textField);
			
			Application.invokeLater(new Runnable() {
				public void run() {
					if (isEditing()) {
						bindingProperty.beginEdit();
						textField.requestFocus();	// This should be selecting all..
					}
				}
			});
			
		}
		
		public void commitEdit(Object value) {
			try {
				bindingProperty.endEdit(true);
			} catch (BOException e) {
				// This is fine... don't force user. This will revert if the user was attempting
				// to move away. 
				return;	
//				JavaFXUtil.ShowErrorDialog(getScene().getWindow(), e.getMessage());  
			}
			BOAttribute<?> attr = bindingProperty.linkedAttributeProperty().getValue();
			attr.setNotifications(false);
			try {
				// This will safely trigger all necessary events without modifying
				// the attribute or throwing unnecessary notifications, as they would have
				// already been thrown from the above endEdit()
				super.commitEdit(attr.getValue());
			} finally {
				attr.setNotifications(true);	
			}
		}
		
		public void updateItem(Object item, boolean empty) {
			// Always call false for empty
			super.updateItem(item, false);
			
			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					setText(null);
					setGraphic(textField);
				} else {
					initBinding();
					String displayText = bindingProperty.getValue();
					releaseBinding();
					
					setText(displayText);
				}
			}
		}

		public void cancelEdit() {
			super.cancelEdit();
			String displayValue = null;
			try {
				// Forcibly cancel
				bindingProperty.endEdit(false);
				displayValue = bindingProperty.getValue();
				
				releaseBinding();
				textField = null;	// remove to save memory.
			} catch (BOException e) {
				e.printStackTrace();
			}
			setText(displayValue);
			setGraphic(null);
		}
		
		private void createTextField() {
			link = new BOLinkEx<>();
			textField = new BOTextField(bindingProperty);
			textField.selectAllOnEditProperty().setValue(true);
			textField.setMinWidth(getWidth() - getGraphicTextGap() * 2);
			textField.focusTraversableProperty().set(false);
			
			// TODO
			textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> arg0, 
						Boolean arg1, Boolean arg2) {
					if (!arg2 && textField != null) {
						commitEdit(textField.getText());
					}
				}
			});
			textField.setOnKeyPressed(new EventHandler<KeyEvent>(){
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.TAB) {
						commitEdit(null);
						GridColumn nextColumn = getNextColumn(!t.isShiftDown());
						if (nextColumn != null) {
							getTableView().edit(getTableRow().getIndex(), nextColumn);
						}
					}
				}
				
			});
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						commitEdit(null);	// Dosen't matter what I pass in...
					} else if (t.getCode() == KeyCode.ESCAPE) {
						// Is this needed? seems to be already implemented in...
						cancelEdit();
					}
				}
			});
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
}
