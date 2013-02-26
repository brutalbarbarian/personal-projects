package com.lwan.javafx.controls;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class CheckBox extends javafx.scene.control.CheckBox {
	private Property<Boolean> valueProperty;
	
	public Property<Boolean> valueProperty() {
		if (valueProperty == null) {
			// Using this instead of BooleanProperty as this allows null values.
			valueProperty = new SimpleObjectProperty<Boolean>(this, "Value", null);
		}
		return valueProperty;
	}
	
	public void setValue(Boolean value) {
		valueProperty().setValue(value);
	}
	
	/**
	 * Note that this can return null, representing indeterminate
	 * 
	 * @return
	 */
	public Boolean getValue() {
		return valueProperty().getValue();
	}
	
	boolean isInvalidating;
	
	public CheckBox(String label) {
		super(label);
		
		isInvalidating = false;
		
		ChangeListener<Boolean> listener = new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> property,
					Boolean oldValue, Boolean newValue) {
				if (!isInvalidating) {
					isInvalidating = true;
					if (property == valueProperty()) {
						if (newValue == null) {
							setIndeterminate(true);
						} else {
							setIndeterminate(false);
							setSelected(newValue);
						}
					} else if (property == indeterminateProperty()) {
						if (newValue) {
							setValue(null);
						}
					} else if (property == selectedProperty()) {
						setValue(newValue);
					}
					
					isInvalidating = false;
				}
			}
		};
		
		valueProperty().addListener(listener);
		selectedProperty().addListener(listener);
		indeterminateProperty().addListener(listener);

		valueProperty().setValue(null);
	}
}
