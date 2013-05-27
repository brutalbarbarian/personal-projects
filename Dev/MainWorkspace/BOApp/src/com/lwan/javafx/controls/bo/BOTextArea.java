package com.lwan.javafx.controls.bo;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.scene.control.TextArea;

import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.StringBoundProperty;

public class BOTextArea extends TextArea implements BoundControl<String>{
	private StringBoundProperty dataBindingProperty;
	
	@Override
	public StringBoundProperty dataBindingProperty() {
		return dataBindingProperty;
	}
	
	public BOTextArea(BOLinkEx<?> link, String path) {
		dataBindingProperty = new StringBoundProperty(this, link, path);
		textProperty().bindBidirectional(dataBindingProperty);
		disableProperty().bind(Bindings.not(dataBindingProperty.editableProperty()));
		
		focusedProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable arg0) {
				if (isFocused()) {
					dataBindingProperty().beginEdit();
				} else {
					dataBindingProperty().endEdit(true);
				}
			}
		});
	}
}
