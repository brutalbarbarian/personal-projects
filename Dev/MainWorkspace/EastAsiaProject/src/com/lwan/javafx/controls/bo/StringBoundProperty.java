package com.lwan.javafx.controls.bo;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOException;
import com.lwan.bo.BOLinkEx;
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
	
	public StringBoundProperty(Object owner, BOLinkEx<?> link,
			String path) {
		super(owner, link, path);
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
	
	protected boolean validate (String value) {
		BOAttribute<?> attr = getLinkedAttribute();
		if (attr == null) {
			
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
		case Currency: return StringFormatter.format("$%.2d", attr.asDouble()).getValue();
		case Boolean: return attr.asBoolean() ? "Yes" : "No";
		case Date: 
			// TODO
		case Time:
		case Unknown:
		default:
			return "";
		}
	}
	
	protected boolean requiresValidation(BOAttribute<?> attr) {		
		return attr != null && 
				attr.getAttributeType() != AttributeType.String;
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
		case Integer : 
			attr.userSetValueAsObject(Integer.parseInt(editValue), getBean());
			break;
		case Double : 
		case Currency: 
			attr.userSetValueAsObject(Double.parseDouble(editValue), getBean());
			break;
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
