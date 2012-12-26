package com.lwan.javafx.controls.bo;

import javafx.beans.binding.Bindings;

import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.controls.CheckBox;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.BoundProperty;

public class BOCheckBox extends CheckBox implements BoundControl<Boolean>{
	private BoundProperty<Boolean> dataBindingProperty; 
	
	public BOCheckBox(String label, BOLinkEx<?> link, String path) {
		super(label);
		
		dataBindingProperty = new BoundProperty<>(this, link, path);
		
		disableProperty().bind(Bindings.not(dataBindingProperty.editableProperty()));
		valueProperty().bindBidirectional(dataBindingProperty);
	}

	@Override
	public BoundProperty<Boolean> dataBindingProperty() {
		return dataBindingProperty;
	}

}
