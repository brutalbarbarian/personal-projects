package com.lwan.javafx.controls.bo;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class BOTextField <S> extends TextField implements BOBoundControl<S, String> {
	public BOTextField(BOLinkEx<?> link, String path, 
			Callback<String, S> storedMap, Callback<S, String> displayMap) {
		this(link, path);
		
		dataBindingProperty().StoredValueMap().setValue(storedMap);
		dataBindingProperty().DisplayValueMap().setValue(displayMap);
	}
	
	public BOTextField(BOLinkEx<?> link, String path) {
		dataBinding = new BOBoundProperty<S, String>(this, link, path);
		textProperty().bindBidirectional(dataBinding);
	}

	private BOBoundProperty<S, String> dataBinding;
	@Override
	public BOBoundProperty<S, String> dataBindingProperty() {
		if (dataBinding == null) {
			throw new RuntimeException("Databinding not initialised");
		}
		return dataBinding;
	}

	@Override
	public void rebuildAttributeLinks() {
		dataBindingProperty().buildAttributeLinks();
		
		BOAttribute<S> link = dataBindingProperty().getAttributeLink();
		editableProperty().setValue(link != null);
	}

	@Override
	public Node node() {
		return this;
	}

	@Override
	public void update(BOAttribute<S> attri) {
		// TODO Auto-generated method stub
		
	}
	
}
