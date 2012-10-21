package com.lwan.javafx.property;

import javafx.beans.value.ObservableValue;

public interface ValidationListener<T> {
	/**
	 * Called prior to setting a new value into a property.
	 * If return false, then the value will be deemed invalid and thus will not be set to the property.
	 * 
	 * @param value
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	public boolean validate(ObservableValue<T> value, T oldValue, T newValue);
}
