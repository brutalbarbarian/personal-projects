package com.lwan.javafx.controls.bo;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.javafx.controls.bo.binding.BoundCellValue;
import com.lwan.util.GenericsUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

public class BOGrid<R extends BusinessObject> extends TableView<R>{
	private BOSet<R> source;
	
	public BOGrid(BOSet<R> source, String [] columnNames, String[] fieldPaths) {
		// initialise the grid columns?
		if (columnNames == null || fieldPaths == null || 
				columnNames.length != fieldPaths.length) {
			throw new RuntimeException("Invalid arguments for column details.");
		}
		
		// TODO need to create a generic te cell... how to specify custom columns???
		// don't care about custom columns for now...we just want a working grid.
		int cols = columnNames.length;
		List<GridColumn> columns = new Vector<>();
		for (int i = 0; i < cols; i++) {
			columns.add(new GridColumn(columnNames[i], fieldPaths[i]));
		}
		
		getColumns().setAll(columns);

		this.source = source;
//		source.addListener(new ModifiedEventListener() {
//			public void handleModified(ModifiedEvent event) {
//				if (event.getType() == ModifiedEvent.TYPE_SET) {
//					
//				}
//			}
//		});
		refresh();
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
	}
	
	public class GridColumn extends TableColumn<R, Object> {
		private String fieldPath;
		
		GridColumn(String caption, String fieldPath) {
			super(caption);
			
			this.fieldPath = fieldPath;
			
			setCellValueFactory(new BoundCellValue<Object, R>(fieldPath));
			setCellFactory(getDefaultCellFactory());
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
	
	
	public class GridCell extends TableCell<R, Object>{
		private GridCell() {
			
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
		private BOLinkEx<R> link;
		
		public void startEdit() {
			System.out.println("start");
			
			super.startEdit();
			
			if (textField == null || link == null) {
				createTextField();
			}
			
			// initialise link
			
			link.setLinkedObject(getRecord());
			textField.dataBindingProperty().buildAttributeLinks();
			
			setText(null);
			setGraphic(textField);
//			textField.requestFocus();
			textField.selectAll();	// Get rid of maybe?
		}
		
		public void updateItem(Object item, boolean empty) {
			System.out.println("update");
			super.updateItem(item, empty);
//			setText(item == null || empty ? "" : getItem().toString());
			
			if (empty) {
				
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					setText(null);
					setGraphic(textField);
				} else {
					// Get the actual shown text
					if (textField == null || link == null) {
						createTextField();
					}
					if (!GenericsUtil.Equals(link.getLinkedObject(), getRecord())) {
						link.setLinkedObject(getRecord());
						textField.dataBindingProperty().buildAttributeLinks();
					}
					setGraphic(textField);
					setGraphic(null);
					
					String displayText = textField.getText();					
					
					setText(displayText);
				}
			}
			
			
		}
	
		
		public void cancelEdit() {
			System.out.println("Cancel");
			super.cancelEdit();
			String value = null;
			try {
				// Forcibly cancel
				textField.dataBindingProperty().endEdit(false);
				value = textField.getText();	// This should be the display value.
				// This will clear away any connections
				link.setLinkedObject(null);
				textField.dataBindingProperty().buildAttributeLinks();
			} catch (BOException e) {
				e.printStackTrace();
			}
			setText(value == null? "" : value);
			setGraphic(null);
		}
		
		private void createTextField() {
			link = new BOLinkEx<>();
			textField = new BOTextField(link, getColumn().getField());
			textField.setMinWidth(getWidth() - getGraphicTextGap() * 2);
			textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> arg0, 
						Boolean arg1, Boolean arg2) {
					if (!arg2) { 
						commitEdit(textField.getText());
					}
				}
			});
			textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						String value = textField.getText();
						if (value != null) { commitEdit(value); } else { commitEdit(null); }
					} else if (t.getCode() == KeyCode.ESCAPE) {
						cancelEdit();
					}
				}
			});
		}
		
	}
}
