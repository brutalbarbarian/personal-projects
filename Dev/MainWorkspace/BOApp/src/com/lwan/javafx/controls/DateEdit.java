package com.lwan.javafx.controls;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import com.lwan.javafx.app.App;
import com.lwan.javafx.art.CalendarArt;
import com.lwan.javafx.controls.panes.TStackPane;
import com.lwan.util.DateUtil;
import com.lwan.util.FxUtils;
import com.lwan.util.StringUtil;
import com.thirdparty.javafx.calender.CalendarView;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;

public class DateEdit extends TStackPane{
	private IntegerProperty dateFormatProperty;
	public IntegerProperty dateFormatProperty() {
		if (dateFormatProperty == null) {
			dateFormatProperty = new SimpleIntegerProperty(this, "DateFormat", DateFormat.SHORT); 
		}
		return dateFormatProperty;
	}
	public int getDateFormat() {
		return dateFormatProperty().get();
	}
	public void setDateFormat(int dateFormat) {
		dateFormatProperty().set(dateFormat);
	}
	
	private BooleanProperty showingProperty;
	public ReadOnlyBooleanProperty showingProperty() {
		return _showingProperty();
	}
	private BooleanProperty _showingProperty() {
		if (showingProperty == null) {
			showingProperty = new SimpleBooleanProperty(this, "Showing", false);
		}
		return showingProperty;
	}
	public boolean isShowing() {
		return showingProperty().get();
	}
	
	private ObjectProperty<Date> valueProperty;
	public ObjectProperty<Date> valueProperty() {
		if (valueProperty == null) {
			valueProperty = new SimpleObjectProperty<>(this, "Value", null);
		}
		return valueProperty;
	}
	public Date getValue() {
		return valueProperty().get();
	}
	public void setValue(Date date) {
		valueProperty().set(date);
	}
	private BooleanProperty editingProperty;
	public ReadOnlyBooleanProperty editingProperty() {
		return _editingProperty();
	}
	private BooleanProperty _editingProperty() {
		if (editingProperty == null) {
			editingProperty = new SimpleBooleanProperty(this, "Editing", false);
		}
		return editingProperty;
	}
	public boolean isEditing() {
		return editingProperty().get();
	}
	
	private BooleanProperty autoShowPopupProperty;
	public BooleanProperty autoShowPopupProperty() {
		if(autoShowPopupProperty == null) {
			autoShowPopupProperty = new SimpleBooleanProperty(this, "AutoShowPopup", false);
		}
		return autoShowPopupProperty;
	}
	public boolean isAutoShowPopup() {
		return autoShowPopupProperty().get();
	}
	public void setAutoShowPopup(boolean autoShow) {
		autoShowPopupProperty().set(autoShow);
	}
	
	private TextField textField;
	private Button btnEdit;
	private CalendarView calendarView;
	private Popup popup;
	private boolean isSetingText;
	
	public DateEdit() {
		isSetingText = false;
		textField = new TextField();
		btnEdit = new Button();
		CalendarArt graphic = new CalendarArt(16, 16);
		btnEdit.setAlignment(Pos.CENTER_RIGHT);
		
		btnEdit.setGraphic(graphic);
		
		btnEdit.setFocusTraversable(false);
		btnEdit.getStyleClass().add("datepicker-editbutton");
		
		TStackPane.setAlignment(btnEdit, Pos.CENTER_RIGHT);
		
		calendarView = new CalendarView(getLocale());
		// Don't want the calendar view to take any focus from the textfield
		FxUtils.setNodeTreeFocusable(calendarView, false);
		
		getChildren().addAll(textField, btnEdit);

		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				if (arg2) {	// will do nothing if already editing...
					startEdit();
				} else if (getParent() == null ||	// Assume null parent means the window is still focused.
						getScene().getWindow().isFocused()) {
					endEdit(true, true);
				}
			}
		});
		
		btnEdit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				showPopup();
			}			
		});
		
		calendarView.selectedDateProperty().addListener(new ChangeListener<Date>() {
			public void changed(ObservableValue<? extends Date> arg0,
					Date arg1, Date arg2) {
				hidePopup();
				endEdit(true, false);
			}			
		});
		
		textField.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				if (!isSetingText && textField.isFocused()) {
					// must be user typed...
					startEdit();
				}
			}
		});
		
		valueProperty().addListener(new ChangeListener<Date>(){
			public void changed(ObservableValue<? extends Date> arg0,
					Date arg1, Date arg2) {
				endEdit(false, false);
			}			
		});
		
		endEdit(false, false);
	}
	
	protected void setText(String text) {
		isSetingText = true;
		try {
			textField.setText(text);
		} finally {
			isSetingText = false;
		}
	}
	
	public void startEdit() {
		// No effect if already in editing mode
		if (!isEditing()) {
			_editingProperty().set(true);
			
			if (isAutoShowPopup()) {
				showPopup();
			}
		}
	}
	
	public void endEdit(boolean commit, boolean fromTextField) {
		// No effect if not in editing mode
		if (isEditing() && commit) {
			if (fromTextField) {
				// attempt to parse...if it fails, just ignore.
				// Always use short for editing.
				if (StringUtil.isNullOrBlank(textField.getText())) {
					setValue(null);
				} else {
					try {
						setValue(getActualDateFormat().parse(textField.getText()));
					} catch (ParseException e) {
						// do nothing..
					}
				}
			} else {	// from calendar view
				setValue(calendarView.selectedDateProperty().get());
			}
			
			_editingProperty().set(false);
		}
		
		hidePopup();
		setText(getDisplayValue());
	}
	
	protected DateFormat getActualDateFormat() {
		return DateFormat.getDateInstance(DateFormat.SHORT, getLocale());
	}
	
	protected String getDisplayValue() {
		if (getValue() == null) {
			return "";
		} else {
			return getActualDateFormat().format(getValue());
		}
	}
	
	protected Locale getLocale() {
//		return Locale.forLanguageTag("en-NZ");
		return App.getLocale();
	}
	
	public void hidePopup() {
		if (popup != null) {
			popup.hide();
		}
	}
	
	public TextField getTextField() {
		return textField;
	}
	
	public void showPopup() {
		if (!isShowing()) {
		
	        if (popup == null) {
	            popup = new Popup();
	            popup.setAutoHide(true);
	            popup.setHideOnEscape(true);
	            popup.setAutoFix(true);
	            popup.getContent().add(calendarView);
	            
	            popup.setOnHiding(new EventHandler<WindowEvent>() {
					public void handle(WindowEvent e) {
						popup = null;	// unassign...
						_showingProperty().set(false);
					}            	
	            });
	        }
	
	        Bounds calendarBounds = calendarView.getBoundsInLocal();
	        Bounds bounds = localToScene(textField.getBoundsInLocal());
	
	        double posX = calendarBounds.getMinX() + bounds.getMinX() + getScene().getX() + getScene().getWindow().getX();
	        double posY = calendarBounds.getMinY() + bounds.getHeight() + bounds.getMinY() + getScene().getY() + getScene().getWindow().getY();
	        
	        if (getValue() == null) {
	        	calendarView.currentDateProperty().set(DateUtil.getCurrentDate());
	        } else {
	        	calendarView.currentDateProperty().set(getValue());	
	        }
	        calendarView.reset();
	
	        popup.show(this, posX, posY);
	        
	        _showingProperty().set(true);
	        
	        if (!isEditing()) {
	        	_editingProperty().set(true);
	        }
	        
	        if (!textField.isFocused()) {
	        	textField.requestFocus();	// this will trigger edit if not already in edit
	        }
		}
	}
}
