package com.lwan.javafx.controls.bo;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
import com.lwan.util.JavaFXUtil;
import com.lwan.util.StringUtil;
import com.sun.javafx.binding.StringFormatter;

public class StringBoundProperty extends BoundProperty<String>{
	private Property<Boolean> editModeProperty;
	private Property<Boolean> requireValidationProperty;
	
	public ReadOnlyProperty<Boolean> requireValidationProperty() {
		return _requireValidationProperty();
	}
	
	public boolean requireValidation() {
		return requireValidationProperty().getValue();
	}
	
	public ReadOnlyProperty<Boolean> editModeProperty() {
		return _editModeProperty();
	}
	
	public boolean getEditMode() {
		return editModeProperty().getValue();
	}
	
	protected Property<Boolean> _requireValidationProperty() {
		if (requireValidationProperty == null) {
			requireValidationProperty = new SimpleBooleanProperty(this, "RequrieValidation", false);
		}
		return requireValidationProperty;
	}
	
	protected Property<Boolean> _editModeProperty() {
		if (editModeProperty == null) {
			editModeProperty = new SimpleBooleanProperty(this, "EditMode", false);
		}
		return editModeProperty;
	}
	
	public StringBoundProperty(BoundControl<String, ?> owner, BOLinkEx<?> link,
			String path) {
		super(owner, link, path);
	}
	
	public StringBoundProperty(BoundControl<String, ?> owner, BoundProperty<?> existing) {
		this(owner, existing.getAttributeLink(), existing.getPath());
	}
	
	
	public void buildAttributeLinks () {
		doBuildAttributeLinks();
		
		// if linkedAttribute is null, isEditable() will always be false
		if (getEditMode() && isEditable()) {
			setValue(getEditValue());
		} else {
			// Make sure we're out of edit mode
			try {
				endEdit(false);
			} catch (BOException e) {}
		}
	}
	
	public boolean validate (String value) {
		BOAttribute<?> attr = getLinkedAttribute();
		if (attr == null) {
			throw new RuntimeException("validate() called when there is no attribute");
		}
		try {
			switch (attr.getAttributeType()) {
			case Boolean:
				break;
			case Currency:
			case Double:
				if (value.length() == 0) {
					return true;
				} else if (value.equals("-")) {
					return attr.AllowNegative().getValue();
				} else {
					int percision = attr.Percision().getValue();
					if (value.equals(".")) {
						return percision > 0;
					} else if (StringUtil.endsWith(value, ".")) {
						// remove the final dot for validation
						value = value.substring(0, value.length() - 1);
					}
					return StringUtil.validateDouble(value, percision) && 
							attr.Value().validateAsObject(Double.parseDouble(value));
				}
			case Date:
				break;
			case Integer:
				if (value.length() == 0) {
					return true;	// always accept empty
				} else if (value.equals("-")) {
					// No actual value set at this point...
					return attr.AllowNegative().getValue();
				} else if (StringUtil.validateInt(value, 10)) {
					// Do a proper validation...as this is a proper integer
					return attr.Value().validateAsObject(Integer.parseInt(value)); 
				} else {
					return false;
				}
			case String:
				return attr.Value().validateAsObject(value);
			case Time:
				break;
			case Unknown:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// If any exceptions occur... just return false if an error occurred... though we probably should
			// still notify the developer that something went wrong?
			JavaFXUtil.ShowErrorDialog(getBoundControl().getNode().getScene().getWindow(), e.getMessage());
			return false;
		}
		
		return true;
	}
	
	protected String getDisplayValue() {
		BOAttribute<?> attr = getLinkedAttribute();
		if (attr == null) {
			return "";
		}
		AttributeType type = attr.getAttributeType();
		switch (type) {
		case String : return attr.asString();
		case Integer : return Integer.toString(attr.asInteger());
		case Double : return Double.toString(attr.asDouble());
		case Currency:
			double value = attr.asDouble();
			if (value >= 0) {
				return StringFormatter.format("$%.2f", value).getValue();
			} else {
				// we want the sign to be on the outside of the currency symbol
				return StringFormatter.format("-$%.2f", -value).getValue();
			}
		case Boolean: return attr.asBoolean() ? "Yes" : "No";
		case Date:
			Date d = (Date)attr.getValue();
			return DateFormat.getDateInstance(DateFormat.SHORT).format(d);
		case Time:
		case Unknown:
		default:
			return "";
		}
	}
	
	protected boolean requiresValidation(BOAttribute<?> attr) {
		if (attr == null) {
			return false;
		} else if (attr.getAttributeType() == AttributeType.String) {
			return attr.Value().requiresValidation();
		} else {
			return true;
		}
	}
	
	protected String getEditValue() {
		if (!isEditable()) {
			throw new IllegalAccessError("getEditValue() is called on a non-editable link");
		}
		BOAttribute<?> attr = getLinkedAttribute();
		if (attr == null) {
			return "";
		}
		AttributeType type = attr.getAttributeType();
		switch (type) {
		case String : return attr.asString();
		case Integer : return Integer.toString(attr.asInteger());
		case Double : 
		case Currency: return Double.toString(attr.asDouble());
		case Boolean: return attr.asBoolean() ? "Yes" : "No";
		case Date: 
			// TODO
		case Time:
		case Unknown:
		default:
			return "";
		}
	}
	
	protected String setEditValue(String editValue) {
		if (!isEditable()) {
			throw new IllegalAccessError("setEditValue() is called on a non-editable link");
		}
		
		BOAttribute<?> attr = getLinkedAttribute();
		if (attr == null) {
			return "setEditValue() called with no attribute currently set";
		}
		
		AttributeType type = attr.getAttributeType();
		switch (type) {
		case String : 
			attr.userSetValueAsObject(editValue, getBean()); 
			break;
		case Integer : {
			int effectiveValue;
			if (editValue.length() == 0 || editValue.equals("-")) {
				// need to take into account invalid strings which parseInt chokes on
				effectiveValue = 0;
			} else {
				effectiveValue = Integer.parseInt(editValue);
			}
			attr.userSetValueAsObject(effectiveValue, getBean());
			break;
		}
		case Double : case Currency: {
			double effectiveValue;
			if (editValue.length() == 0 || editValue.equals("-") || editValue.equals(".")) {
				effectiveValue = 0;
			} else {
				effectiveValue = Double.parseDouble(editValue);
			}
			attr.userSetValueAsObject(effectiveValue, getBean());
			break;
		}
		case Boolean: 
			// TODO
		case Date: 
		case Time:
		case Unknown:
		default:
			return "";
		}
		
		return "";
	}
	
	// Should be called upon control gaining focus 
	public void beginEdit() {
		if (!getEditMode() && isEditable()) {
			_editModeProperty().setValue(true);
			setValue(getEditValue());
			// Only set this after edit value has been set
			_requireValidationProperty().setValue(requiresValidation(getLinkedAttribute()));
		}
	}
	
	// Should be called when the control loses focus
	public void endEdit(boolean commit) throws BOException {
		if (getEditMode()) {
			// try set value
			String err = commit? setEditValue(getValue()) : ""; 
			if (StringUtil.isNullOrBlank(err)) {
				requireValidationProperty.setValue(false);
				setValue(getDisplayValue());
				_editModeProperty().setValue(false);
			} else {
				throw new BOException(err, getLinkedAttribute());
			}
		} else if (!commit) {
			// Not in edit mode, and not trying to commit... just update display
			setValue(getDisplayValue());
		}
	}
}
