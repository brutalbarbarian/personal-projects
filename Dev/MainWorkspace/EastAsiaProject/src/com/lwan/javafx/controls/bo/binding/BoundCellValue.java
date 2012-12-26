package com.lwan.javafx.controls.bo.binding;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import com.lwan.bo.BusinessObject;

public class BoundCellValue <T, B extends BusinessObject> implements Callback<CellDataFeatures<B, T>, ObservableValue<T>>{
	private String path;
	
	public BoundCellValue (String path) {
		this.path = path;
	}
	
	@SuppressWarnings("unchecked")
	public ObservableValue<T> call(CellDataFeatures<B, T> p) {
		return (ObservableValue<T>) p.getValue().findChildByPath(path);
	}	
}
