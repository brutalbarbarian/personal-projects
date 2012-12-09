package com.lwan.javafx.controls.bo;

import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
import com.lwan.util.JavaFXUtil;
import com.lwan.util.StringUtil;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.TextField;

public class BOTextField extends TextField implements BoundControl<String, BOTextField> {
	private StringBoundProperty dataBindingProperty;
	
	@Override
	public StringBoundProperty dataBindingProperty() {
		return dataBindingProperty;
	}
	
	public BOTextField(BOLinkEx<?> link, String path) {
		dataBindingProperty = new StringBoundProperty(this, link, path);
		textProperty().bindBidirectional(dataBindingProperty);
		editableProperty().bind(dataBindingProperty.editableProperty());
		
		// Focus Listener for managing the edit state of the textfield
		focusedProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable observable) {
				if (isFocused()) {
					dataBindingProperty().beginEdit();
				} else {
					try {
						dataBindingProperty().endEdit(true);
					} catch (BOException e) {
						JavaFXUtil.ShowErrorDialog(getScene().getWindow(), e.getMessage());
						requestFocus();
					}
				}
			}
		});
	}
	
	public void setMasked(char maskChar) {
		
	}
	
	public void replaceText(int start, int end, String text) {
		if (!dataBindingProperty().requireValidation() || 
				dataBindingProperty().validate(StringUtil.replaceString(getText(), start, end, text))) {
			super.replaceText(start, end, text);
		}
	}
	
	public void replaceSelection(String text) {
		if (!dataBindingProperty().requireValidation() ||
				dataBindingProperty().validate(text)) {
			super.replaceSelection(text);
		}
	}

	@Override
	public BOTextField getNode() {
		return this;
	}
	
}
