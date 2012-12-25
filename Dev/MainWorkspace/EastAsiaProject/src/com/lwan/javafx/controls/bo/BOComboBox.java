package com.lwan.javafx.controls.bo;

import javafx.beans.binding.Bindings;

import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.controls.ComboBox;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.BoundProperty;

public class BOComboBox <T> extends ComboBox<T> implements BoundControl<T> {
	private BoundProperty<T> dataBindingProperty;
	
	@Override
	public BoundProperty<T> dataBindingProperty() {
		return dataBindingProperty;
	}
	
	public BOComboBox(BOLinkEx<?> link, String path) {
		dataBindingProperty = new BoundProperty<>(this, link, path);
		
		disableProperty().bind(Bindings.not(dataBindingProperty.editableProperty()));
		selectedProperty().bindBidirectional(dataBindingProperty);
	}
}
