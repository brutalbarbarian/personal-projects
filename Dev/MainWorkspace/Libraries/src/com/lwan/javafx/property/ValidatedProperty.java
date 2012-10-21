package com.lwan.javafx.property;

import java.util.List;
import java.util.Vector;

import javafx.beans.property.SimpleObjectProperty;

public class ValidatedProperty <T> extends SimpleObjectProperty<T> {
	List<ValidationListener<T>> validationListeners;
	
	
	public ValidatedProperty (Object parent, String name) {
		super(parent, name);
		
		validationListeners = new Vector<>();
	}

	public void addListener(ValidationListener<T> listener) {
		validationListeners.add(listener);
	}
	
	public void removeListener(ValidationListener<T> listener) {
		validationListeners.remove(listener);
	}
	
	public void set(T value) {
		if (validate(get(), value)) {
			super.set(value);
		}
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
