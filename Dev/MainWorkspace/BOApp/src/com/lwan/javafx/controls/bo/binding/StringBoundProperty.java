package com.lwan.javafx.controls.bo.binding;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.app.Lng;
import com.lwan.util.StringUtil;

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
	
	public StringBoundProperty(Object owner, BOLinkEx<?> link,
			String path) {
		super(owner, link, path);
	}
	
	public StringBoundProperty(Object owner, BoundProperty<?> existing) {
		this(owner, existing.getAttributeLink(), existing.getPath());
	}
	
	
	public void buildAttributeLinks () {
		super.buildAttributeLinks();
		
		if (!(getEditMode() && isEditable())) {
			// Make sure we're out of edit mode
			try {
				endEdit(false);
			} catch (BOException e) {}
		}
	}
	
	protected String getEffectiveValue() {
		if (getEditMode()) {
			return getEditValue();
		} else {
			return getDisplayValue();
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
				// This will allow empty string too
				return (StringUtil.beginsWith(Lng._("Yes"), value, true) ||
						StringUtil.beginsWith(Lng._("No"), value, true));
			case Currency:
			case Double:
				if (value.length() == 0) {
					return true;
				} else if (value.equals("-")) {
					return attr.allowNegativeProperty().getValue();
				} else {
					int percision = attr.percisionProperty().getValue();
					if (value.equals(".")) {
						return percision > 0;
					} else if (StringUtil.endsWith(value, ".")) {
						// remove the final dot for validation
						value = value.substring(0, value.length() - 1);
					}
					return StringUtil.validateDouble(value, percision) && 
							attr.valueProperty().validateAsObject(Double.parseDouble(value));
				}
			case Date:
				// Not particularly restrictive validating... otherwise will choke user input
				// will be validated prior to saving anyway
				return StringUtil.validateString(value, new Callback<Character, Boolean>(){
					int dividerCount = 0;
					int digitCount = 0;
					public Boolean call(Character c) {
						if (Character.isDigit(c)) {
							digitCount++;
							return digitCount <= 8;
						} else if (c == '/' || c == '\\') {
							dividerCount++;
							return dividerCount <= 2;
						} else {
							return false;	
						}
					}				
				});
			case ID:
			case Integer:
				if (value.length() == 0) {
					return true;	// always accept empty
				} else if (value.equals("-")) {
					// No actual value set at this point...
					return attr.allowNegativeProperty().getValue();
				} else if (StringUtil.validateInt(value, 10)) {
					// Do a proper validation...as this is a proper integer
					return attr.valueProperty().validateAsObject(Integer.parseInt(value)); 
				} else {
					return false;
				}
			case String:
				return attr.valueProperty().validateAsObject(value);
			case Time:
				// TODO
				break;
			case Unknown:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// If any exceptions occur... just return false if an error occurred... though we probably should
			// still notify the developer that something went wrong?
			e.printStackTrace();
//			JavaFXUtil.ShowErrorDialog(getBoundControl().getNode().getScene().getWindow(), e.getMessage());
			return false;
		}
		
		return true;
	}
	
	protected String getDisplayValue() {
		BOAttribute<?> attr = getLinkedAttribute();
		if (attr == null || attr.isNull()) {
			return "";
		}
		AttributeType type = attr.getAttributeType();
		switch (type) {
		case String : return attr.asString();
		case ID:
		case Integer : return Integer.toString(attr.asInteger());
		case Double : return Double.toString(attr.asDouble());
		case Currency:
			double value = attr.asDouble();
			return Lng.formatCurrency(value);
//			if (value >= 0) {
//				return StringFormatter.format("$%.2f", value).getValue();
//			} else {
//				// we want the sign to be on the outside of the currency symbol
//				return StringFormatter.format("-$%.2f", -value).getValue();
//			}
		case Boolean:
			if (attr.getValue() == null) {
				return "";
			} else {
				return attr.asBoolean() ? Lng._("Yes") : Lng._("No");
			}
		case Date:
			Date d = (Date)attr.getValue();
			if (d != null) {
				return DateFormat.getDateInstance(DateFormat.SHORT).format(d);
			} else {
				return "";
			}
		case Time:
			// TODO
		case Unknown:
		default:
			return "";
		}
	}
	
	protected boolean requiresValidation(BOAttribute<?> attr) {
		if (attr == null) {
			return false;
		} else if (attr.getAttributeType() == AttributeType.String) {
			// Majority of text fields don't require validation. Attempting to validate
			// will just slow things down. Especially with larger textfields such as 
			// memos.
			return attr.valueProperty().requiresValidation();
		} else {
			return true;
		}
	}
	
	protected String getEditValue() {
		if (!isEditable()) {
			throw new IllegalAccessError("getEditValue() is called on a non-editable link");
		}
		BOAttribute<?> attr = getLinkedAttribute();
		if (attr == null || attr.isNull()) {
			return "";
		}
		AttributeType type = attr.getAttributeType();
		switch (type) {
		case String : return attr.asString();
		case ID:
		case Integer : return Integer.toString(attr.asInteger());
		case Double : return StringUtil.formatString("%f", attr.asDouble());
		case Currency: return StringUtil.formatString("%.2f", attr.asDouble());
		case Boolean: 
			if (attr.getValue() == null) {
				return "";
			} else {
				return attr.asBoolean() ? Lng._("Yes") : Lng._("No");
			}
		case Date: 
			Date d = (Date)attr.getValue();
			if (d != null) {
				return DateFormat.getDateInstance(DateFormat.SHORT).format(d);
			} else {
				return "";
			}
		case Time:
			// TODO
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
		case ID:
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
			if (editValue.length() == 0 || editValue.equals("-") || editValue.equals(".")) {
				attr.clear();
			} else {
				attr.userSetValueAsObject(Double.parseDouble(editValue), getBean());
			}
			break;
		}
		case Boolean: {
			if (editValue.isEmpty()) {
				if (!attr.isNull()) {
					return Lng._("Unknown user input");
				}
			} else if (StringUtil.beginsWith(Lng._("Yes"), editValue, true)) {
				attr.userSetValueAsObject(true, getBean());
			} else if (StringUtil.beginsWith(Lng._("No"), editValue, true)) {
				attr.userSetValueAsObject(false, getBean());
			} else {
				return Lng._("Unknown user input.");
			}
			break;
		}
		case Date: 
			// Attempt to parse...
			try {
				Date d = DateFormat.getDateInstance(DateFormat.SHORT).parse(editValue);
				attr.userSetValueAsObject(d, getBean());
			} catch (ParseException e) {
				return Lng._("Date is not entered in a recognizable format.");
			}
		case Time:
			// TODO
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
			if (commit && !(requireValidationProperty.getValue() &&
					validate(getValue()))) {
				throw new BOException(Lng._("Invalid Input"), getLinkedAttribute());
			}
				
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
	
	/**
	 * As StringBoundProperty abstracts the actual value with display and edit values,
	 * this allows easy access to the actual value.
	 * 
	 * @return
	 */
	public Object getActualValue() {
		return getLinkedAttribute() == null? null : getLinkedAttribute().getValue();
	}
	
	protected void buildBindings() {
		// Do not inherit...
	}
}
