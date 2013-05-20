package com.lwan.javafx.controls.bo;

import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.StringBoundProperty;
import com.lwan.util.StringUtil;
import com.sun.glass.ui.Application;
import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import com.sun.javafx.scene.control.skin.TextFieldSkin;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class BOTextField extends TextField implements BoundControl<String> {
	private StringBoundProperty dataBindingProperty;
	private BooleanProperty selectAllOnEditProperty;
	private BooleanProperty externalControlledProperty;
	private BooleanProperty allowContextMenuProperty;
	
	public BooleanProperty allowContextMenuProperty() {
		if (allowContextMenuProperty == null) {
			allowContextMenuProperty = new SimpleBooleanProperty(this, "AllowContextMenu", true);
		}
		return allowContextMenuProperty;
	}
	public boolean allowContextMenu(){
		return allowContextMenuProperty().getValue();
	}
	public void setAllowContextMenu(boolean allowContextMenu) {
		allowContextMenuProperty().setValue(allowContextMenu);
	}
	
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
	
	public BooleanProperty externalControlledProperty() {
		if (externalControlledProperty == null) {
			externalControlledProperty = new SimpleBooleanProperty(this, "ExternalControlled", false);
		}
		return externalControlledProperty;
	}
	
	public boolean isExternalControlled() {
		return externalControlledProperty().getValue();
	}
	
	public BOTextField(BOLinkEx<?> link, String path) {
		dataBindingProperty = new StringBoundProperty(this, link, path);
		initialise();
	}
	
	protected void initialise() {
		textProperty().bindBidirectional(dataBindingProperty);
		disableProperty().bind(Bindings.not(dataBindingProperty.editableProperty()));
		actualInvalidate = true;
		
		// Focus Listener for managing the edit state of the textfield
		focusedProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable observable) {
				if (	isEditable() &&		// we don't really want to do anything if this isn't editable
						!isExternalControlled() &&	// Do nothing if externally controlled. 
						actualInvalidate &&
						// We only want to do anything is focus is still on the same form
						(getParent() == null ||	// Assume null parent means the window is still focused.
						getScene() == null ||	// Same reasoning as above 
						getScene().getWindow().isFocused())) {
					if (isFocused()) {
						dataBindingProperty().beginEdit();
					} else {
						try {
							dataBindingProperty().endEdit(true);
						} catch (BOException e) {						
							dataBindingProperty().endEdit(false);
							getStyleClass().remove(STYLE_INVALID);
//							JavaFXUtil.ShowErrorDialog(getScene().getWindow(), e.getMessage());
//							actualInvalidate = false;
//							requestFocus();
//							actualInvalidate = true;
						}
					}
				}
			}
		});
		
		dataBindingProperty().editModeProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> property,
					Boolean oldValue, Boolean newValue) {
				if (newValue && selectAllOnEditProperty().getValue()) {
					Application.invokeLater(new Runnable() {
						public void run() {
							selectAll();	
						}	
					});
				}
			}
		});
		
		allowsInvalid = false;
		isInvalid = false;
		
		// the following 3 event filters is to allow validation to still work
		// with composed keyboards i.e. Asian symbol keyboards.
		addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>(){
			public void handle(KeyEvent e) {
				// if we're getting key_typed events, we must know that the
				// keyboard is a non-composing keyboard
				allowsInvalid = false;
			}			
		});
		
		addEventFilter(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, new EventHandler<InputMethodEvent>(){
			public void handle(InputMethodEvent e) {
				// if we're getting input_method_text_changed events, we know that the 
				// keyboard is a composing keyboard
				if (e.getComposed().size() > 0) {
					allowsInvalid = true;
				}
			}			
		});
		
		addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				if (isInvalid && e.getCode() == KeyCode.TAB) {
					// if the user wants to force click away, let them
					// if they're trying to tab away however, stop them.
					e.consume();
				}
			}
		});
		
		setSkin(new TextFieldSkin(this, new Behavior()));
	}
	
	private class Behavior extends TextFieldBehavior {
		public Behavior() {
			super(BOTextField.this);
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseButton.SECONDARY && 
					!allowContextMenu()) {
				return;	// don't allow context menu to show
			}
			super.mouseReleased(e);
		}
	}
	
	private boolean actualInvalidate;	// This is to avoid triggering twice invalidation upon failure 
	public BOTextField(StringBoundProperty dataBinding) {
		dataBindingProperty = dataBinding;
		initialise();
	}
	
	boolean isInvalid = false;
	boolean allowsInvalid;
	private static final String STYLE_INVALID = "text-field-invalid";
	
	public void replaceText(int start, int end, String text) {
		if (!dataBindingProperty().requireValidation() || 
				dataBindingProperty().validate(StringUtil.replaceString(getText(), start, end, text))) {
			super.replaceText(start, end, text);
			getStyleClass().remove(STYLE_INVALID);
			isInvalid = false;
		} else if (allowsInvalid) {						
			super.replaceText(start, end, text);
			getStyleClass().add(STYLE_INVALID);
			isInvalid = true;
		}
	}

	public void replaceSelection(String text) {
		if (!dataBindingProperty().requireValidation() ||
				dataBindingProperty	().validate(text)) {
			super.replaceSelection(text);
		}
	}
}
