package com.lwan.javafx.controls.bo;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.controls.CheckBox;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.BoundProperty;

public class BOCheckBox extends CheckBox implements BoundControl<Boolean>{
	private BoundProperty<Boolean> dataBindingProperty; 
	
	public BOCheckBox(String label, BOLinkEx<?> link, String path) {
		super(label);
		
		dataBindingProperty = new BoundProperty<>(this, link, path);
		dataBindingProperty.editableProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable arg0) {
				updateDisableState();
			}
		});
		updateDisableState();
		
		valueProperty().bindBidirectional(dataBindingProperty);
	}

	@Override
	public BoundProperty<Boolean> dataBindingProperty() {
		return dataBindingProperty;
	}

	private BooleanProperty enabledProperty;
	public BooleanProperty enabledProperty() {
		if (enabledProperty == null) {
			enabledProperty = new SimpleBooleanProperty(this, "Enabled", true);
			enabledProperty.addListener(new InvalidationListener() {				
				public void invalidated(Observable arg0) {
					updateDisableState();
				}
			});
		}
		return enabledProperty;
	}
	
	protected void updateDisableState() {
		setDisable(BOCtrlUtil.getDisabled(this));
	}
	
	public void setEnabled(boolean enabled) {
		enabledProperty().set(enabled);
	}
	public boolean isEnabled() {
		return enabledProperty().get();
	}
}
