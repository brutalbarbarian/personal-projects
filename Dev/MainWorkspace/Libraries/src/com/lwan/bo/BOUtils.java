package com.lwan.bo;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class BOUtils {
	public static <T> Property<T> constructProperty () {
		// TODO maybe make more elobrate later
		return new SimpleObjectProperty<T>();
	}
	
	public static <T> Property <T> getProperty(Property<T> p) {
		if (p == null) {
			return constructProperty();
		} else {
			return p;
		}
	}
}
