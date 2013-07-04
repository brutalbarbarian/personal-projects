package com.lwan.javafx.controls.bo;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextArea;

import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.app.util.BOCtrlUtil;
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
		dataBindingProperty.editableProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable arg0) {
				updateDisableState();
			}
		});
		updateDisableState();
		
		textProperty().bindBidirectional(dataBindingProperty);
		
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
