package com.lwan.strcom.gui;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;

public class GSettings {
	private static Property<Boolean> ignoreWhiteSpace;
	public static Property<Boolean> ignoreWhiteSpace() {
		if (ignoreWhiteSpace == null) {
			ignoreWhiteSpace = new SimpleBooleanProperty(true);	//TODo
		}
		return ignoreWhiteSpace;
	}
	
}
