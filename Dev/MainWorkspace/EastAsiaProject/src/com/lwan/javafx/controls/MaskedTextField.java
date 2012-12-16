package com.lwan.javafx.controls;

import com.lwan.util.StringUtil;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class MaskedTextField extends TextField{
	private Property<Character> maskCharProperty;
	private Property<String> actualValueProperty;
	
	public MaskedTextField() {
		actualValueProperty.addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0,
					String oldString, String newString) {
				
			}			
		});
	}
	
	public Property<Character> maskCharProperty() {
		if (maskCharProperty == null) {
			maskCharProperty = new SimpleObjectProperty<Character>(this, "MaskChar", '*');
		}
		return maskCharProperty;
	}
	
	public Property<String> actualValueProperty() {
		if (actualValueProperty == null) {
			actualValueProperty = new SimpleStringProperty(this, "ActualValue", "");
		}
		return actualValueProperty;
	}
	
	public String getValue() {
		return actualValueProperty().getValue();
	}
	
	public char getMaskChar() {
		return maskCharProperty.getValue();
	}
	
	public void replaceText(int start, int end, String text) {
		actualValueProperty.setValue(StringUtil.replaceString(getValue(), start, end, text));
		super.replaceText(start, end, StringUtil.getRepeatedString(getMaskChar(), text.length()));
	}
	
	public void replaceSelection(String text) {
		actualValueProperty.setValue(text);
		super.replaceSelection(StringUtil.getRepeatedString(getMaskChar(), text.length()));
	}
}
