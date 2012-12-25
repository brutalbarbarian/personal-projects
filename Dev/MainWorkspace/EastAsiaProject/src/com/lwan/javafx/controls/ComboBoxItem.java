package com.lwan.javafx.controls;

public class ComboBoxItem<T> {
	private T value;
	private String displayValue;
	
	protected ComboBoxItem(String displayValue, T value) {
		this.displayValue = displayValue;
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public String toString() {
		if (displayValue == null) {
			return value.toString();
		} else {
			return displayValue;	
		}
	}
}
