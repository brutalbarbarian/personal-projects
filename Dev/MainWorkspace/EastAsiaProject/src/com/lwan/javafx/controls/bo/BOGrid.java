package com.lwan.javafx.controls.bo;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.javafx.controls.bo.binding.BoundCellValue;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
		// TODO
	}
}
