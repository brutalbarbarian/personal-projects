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
	private boolean replacing;	// This is to avoid triggering the change listener on replacement text
	
	public MaskedTextField() {
		replacing = false;
		actualValueProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0,
					String oldString, String newString) {
				if (!replacing) {
					replaceSelection(newString);
				}
			}			
		});
	}
	
	/**
	 * The masked character that is displayed to the user
	 * 
	 * @return
	 */
	public Property<Character> maskCharProperty() {
		if (maskCharProperty == null) {
			maskCharProperty = new SimpleObjectProperty<Character>(this, "MaskChar", '*');
		}
		return maskCharProperty;
	}
	
	/**
	 * The actual value stored by this masked text field.
	 * Do not call textProperty().get() as that will only return a string
	 * of the masked character.
	 * 
	 * @return
	 */
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
		return maskCharProperty().getValue();
	}
	
	public void replaceText(int start, int end, String text) {
		replacing = true;
		actualValueProperty.setValue(StringUtil.replaceString(getValue(), start, end, text));
		super.replaceText(start, end, StringUtil.getRepeatedString(getMaskChar(), text.length()));
		replacing = false;
	}
	
	public void replaceSelection(String text) {
		replacing = true;
		actualValueProperty.setValue(text);
		super.replaceSelection(StringUtil.getRepeatedString(getMaskChar(), text.length()));
		replacing = false;
	}
}
