package com.lwan.javafx.controls.bo;

import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.StringBoundProperty;
import com.lwan.util.JavaFXUtil;
import com.lwan.util.StringUtil;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class BOTextField extends TextField implements BoundControl<String> {
	private StringBoundProperty dataBindingProperty;
	private BooleanProperty selectAllOnEditProperty;
	
	@Override
	public StringBoundProperty dataBindingProperty() {
		return dataBindingProperty;
	}
	
	public BooleanProperty selectAllOnEditProperty() {
		if (selectAllOnEditProperty == null) {
			selectAllOnEditProperty = new SimpleBooleanProperty(this, "SelectAllOnEdit", false);
		}
		return selectAllOnEditProperty;
	}
	
	private boolean actualInvalidate;	// This is to avoid triggering twice invalidation upon failure 
	public BOTextField(BOLinkEx<?> link, String path) {
		dataBindingProperty = new StringBoundProperty(this, link, path);
		textProperty().bindBidirectional(dataBindingProperty);
		disableProperty().bind(Bindings.not(dataBindingProperty.editableProperty()));
		actualInvalidate = true;
		
		// Focus Listener for managing the edit state of the textfield
		focusedProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable observable) {
				System.out.println(getParent());
				if (actualInvalidate && 
						// We only want to do anything is focus is still on the same form
						(getParent() == null ||	// Assume null parent means the window is still focused.
						getScene().getWindow().isFocused())) {
					if (isFocused()) {
						dataBindingProperty().beginEdit();
					} else {
						try {
							dataBindingProperty().endEdit(true);
						} catch (BOException e) {
							JavaFXUtil.ShowErrorDialog(getScene().getWindow(), e.getMessage());
							actualInvalidate = false;
							requestFocus();
							actualInvalidate = true;
						}
					}
				}
			}
		});
		
		dataBindingProperty().editModeProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> property,
					Boolean oldValue, Boolean newValue) {
				if (newValue && selectAllOnEditProperty().getValue()) {
					selectAll();
				}
			}
		});
	}
	
	public void replaceText(int start, int end, String text) {
		if (!dataBindingProperty().requireValidation() || 
				dataBindingProperty().validate(StringUtil.replaceString(getText(), start, end, text))) {
			super.replaceText(start, end, text);
		}
	}
	
	public void replaceSelection(String text) {
		if (!dataBindingProperty().requireValidation() ||
				dataBindingProperty	().validate(text)) {
			super.replaceSelection(text);
		}
	}
	
}
