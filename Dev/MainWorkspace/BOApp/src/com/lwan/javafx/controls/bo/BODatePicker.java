package com.lwan.javafx.controls.bo;

import java.util.Date;

import javafx.beans.binding.Bindings;

import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.app.App;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.BoundProperty;
import com.thirdparty.javafx.calender.DatePicker;

public class BODatePicker extends DatePicker implements BoundControl<Date>{
	private BoundProperty<Date> dataBindingProperty;
	
	public BODatePicker (BOLinkEx<?> link, String path) {
		super(App.getLocale());
		
		dataBindingProperty = new BoundProperty<>(this, link, path);
		
		disableProperty().bind(Bindings.not(dataBindingProperty.editableProperty()));
		selectedDateProperty().bindBidirectional(dataBindingProperty);
	}
	
	public BODatePicker (BoundProperty<Date> boundProperty){
		dataBindingProperty = boundProperty;
	}

	@Override
	public BoundProperty<Date> dataBindingProperty() {
		return dataBindingProperty;
	}
}
