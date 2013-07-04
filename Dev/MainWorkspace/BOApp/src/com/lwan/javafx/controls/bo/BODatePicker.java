package com.lwan.javafx.controls.bo;

import java.util.Calendar;
import java.util.Date;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.controls.DatePicker;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.BoundProperty;

public class BODatePicker extends DatePicker implements BoundControl<Date>{
	private BoundProperty<Date> dataBindingProperty;
	
	public BODatePicker (BOLinkEx<?> link, String path) {
		dataBindingProperty = new BoundProperty<>(this, link, path);
		
		initialise();
	}
	
	public BODatePicker (BoundProperty<Date> boundProperty){
		dataBindingProperty = boundProperty;
		
		initialise();
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
	
	protected void initialise() {
		setAutoShowPopup(true);
		setDateFormat(Calendar.SHORT);
		
		dataBindingProperty.editableProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable arg0) {
				updateDisableState();
			}
		});	
		updateDisableState();
		
		valueProperty().bindBidirectional(dataBindingProperty);
	}

	@Override
	public BoundProperty<Date> dataBindingProperty() {
		return dataBindingProperty;
	}
}
