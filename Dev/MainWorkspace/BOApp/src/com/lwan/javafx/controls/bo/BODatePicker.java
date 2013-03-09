package com.lwan.javafx.controls.bo;

import java.util.Calendar;
import java.util.Date;

import javafx.beans.binding.Bindings;

import com.lwan.bo.BOLinkEx;
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
	
	protected void initialise() {
		setAutoShowPopup(true);
		setDateFormat(Calendar.SHORT);
		
		disableProperty().bind(Bindings.not(dataBindingProperty.editableProperty()));
		valueProperty().bindBidirectional(dataBindingProperty);
	}

	@Override
	public BoundProperty<Date> dataBindingProperty() {
		return dataBindingProperty;
	}
}
