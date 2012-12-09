package com.lwan.javafx.property;

import java.util.List;
import java.util.Vector;

import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;

public class ValidatedProperty <T> extends SimpleObjectProperty<T> {
	List<ValidationListener<T>> validationListeners;
	Callback<T, T> beforeSetValue;
	
	
	public ValidatedProperty (Object parent, String name) {
		super(parent, name);
		
		validationListeners = new Vector<>();
	}
	
	public void setBeforeSetValue(Callback<T, T> callback) {
		beforeSetValue = callback;
	}
	
	public Callback<T, T> getBeforeSetValue() {
		return beforeSetValue;
	}

	public void addListener(ValidationListener<T> listener) {
		validationListeners.add(listener);
	}
	
	public void removeListener(ValidationListener<T> listener) {
		validationListeners.remove(listener);
	}
	
	public void set(T value) {
		if (validate(get(), value)) {
			// Allow validation first??? or validation after...
			if (beforeSetValue != null) {
				value = beforeSetValue.call(value);
			}
			super.set(value);
		}
	}
	
	public boolean requiresValidation() {
		return !validationListeners.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public boolean validateAsObject(Object newValue) {
		return validate(get(), (T)newValue);
	}
	
	protected boolean validate(T oldValue, T newValue) {
		// if any of the validationListeners return false, assume this value is invalid
		for (ValidationListener<T> listener : validationListeners) {
			if (!listener.validate(this, oldValue, newValue)) return false;
		}
		// either no listeners or all listeners returned true... return true
		return true;
	}
}
