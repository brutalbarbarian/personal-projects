package com.lwan.bo;

import com.lwan.javafx.property.ValidatedProperty;
import com.lwan.javafx.property.ValidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

public class BOAttribute <T> extends BusinessObject {
	/* Properties Declarations */
	private Property<Boolean> allow_nulls;
	private Property<T> default_value;

	// value is the valid contained by the attribute
	private ValidatedProperty<T> value;
	
	// This is the value that should be returned when get() is called and the value is null
	private Property<T> null_value;
	
	
	/* Property accessors */
	/**
	 * Get the property containing the value represented by this attribute. 
	 * 
	 * @return
	 */
	public ValidatedProperty<T> Value() {
		if (value == null) {
			value = new ValidatedProperty<>(this, "Value");
		}
		return value;
	}
	
	protected String doVerifyState() {
		// if allow null state is satisfied.
		// Shouldn't need to check if the value itself is valid, as it should have 
		// already been validated when the value was set
		if ((isNull() || AllowNulls().getValue())) {
			return null;
		} else {
			return "Attribute '" + Name().getValue() + "' is invalid";
		}
	}
	
	public Property<Boolean> AllowNulls() {
		if (allow_nulls == null) {
			allow_nulls = new SimpleObjectProperty<>(this, "AllowNulls");
		}
		return allow_nulls;
	}
	
	public Property<T> DefaultValue() {
		if (default_value == null) {
			default_value = new SimpleObjectProperty<>(this, "DefaultValue");
		}
		return default_value;
	}
	
	public Property<T> NullValue() {
		if (null_value == null) {
			null_value = new SimpleObjectProperty<>(this, "NullValue");
		}
		return null_value;
	}
	
	/**
	 * Effectively 0 if the value is null, or cast as integer if the value
	 * is of type integer. If the value is not null and is not integer,
	 * a NumberFormatException is thrown.
	 * 
	 * @return
	 */
	public int asInteger() {
		T val = getValue();
		if (val == null) {
			return 0;
		} else if (val instanceof Integer) {
			return (Integer)val;
		} else {
			throw new NumberFormatException("Cannot convert object of type " + val.getClass().getName() + 
					" into an integer."); 
		}
	}
	
	public long asLong() {
		T val = getValue();
		if (val == null) {
			return 0L;
		} else if (val instanceof Long || val instanceof Integer) {
			return (Long)val;
		} else {
			throw new IllegalArgumentException("Cannot convert object of type " + val.getClass().getName() +
					" into a Long");
		}
	}
	
	public boolean asBoolean() {
		T val = getValue();
		if (val == null) {
			return false;
		} else if (val instanceof Boolean) {
			return true;
		} else {
			throw new IllegalArgumentException("Cannot convert object of type " + val.getClass().getName() +
					" into a boolean");
		}
	}
	
	/**
	 * Effectively an empty string if the value is null, or the toString()
	 * of the value otherwise. This should only be used on String type
	 * attributes as the toString is unlikely a true reflection of
	 * the stored value.
	 * 
	 * @return
	 */
	public String asString() {
		T val = getValue();
		if (val == null) {
			return "";
		} else {
			return val.toString();
		}
	}
	
	public BOAttribute(BusinessObject parent, String name) {
		this(parent, name, true, null, null);
	}
	
	
	public BOAttribute(BusinessObject parent, String name, boolean allowNulls, T defaultValue, T nullValue) {
		super(parent, name);
		
		AllowNulls().setValue(allowNulls);
		DefaultValue().setValue(defaultValue);
		NullValue().setValue(nullValue);
		
		Value().addListener(new ChangeListener<T>(){
			public void changed(ObservableValue<? extends T> observable,
					T oldValue, T newValue) {
				doChanged(oldValue, newValue);
			}
		});
	}
	
	public void addValidationListener(ValidationListener<T> listener) {
		Value().addListener(listener);
	}
	
	public void addChangeListener(ChangeListener<T> listener) {
		Value().addListener(listener);
	}
	
	public void setBeforeChangeListener(Callback<T, T> callback) {
		Value().setBeforeSetValue(callback);
	}
	
	
	/**
	 * Will be called upon value changing.
	 * 
	 * @param oldValue
	 * @param newValue
	 */
	public void doChanged(T oldValue, T newValue) {
		fireModified(new ModifiedEvent(this));
	}	
	
	/**
	 * Convenience method for getting the value. 
	 * This is the same as calling Value().get()
	 * 
	 * @return
	 */
	public T getValue() {
		return Value().getValue();
	}
	

	/**
	 * Convenience method for setting the value.
	 * This is the same as calling Value().set()
	 * 
	 * @param val
	 */
	public void setValue(T val) {
		Value().setValue(val);
	}
	
	/**
	 * Less safe way but allowing universal setting without
	 * knowing type of T beforehand
	 * 
	 * @param val
	 */
	@SuppressWarnings("unchecked")
	public void setAsObject(Object val) {
		setValue((T)val);
	}
	
	/**
	 * Check if this attribute is null
	 * 
	 * @return
	 */
	public boolean isNull() {
		return getValue() == null;
	}

	/**
	 * Sets the attribute back to null.
	 * Equivalent to calling setValue(null)
	 * 
	 */
	public void clear() {
		setValue(null);
	}
	
	/**
	 * Sets the attribute back to default, or null if no default is set.
	 * 
	 */
	public void clearAttributes() {
		setValue(DefaultValue().getValue());
	}
	
	
	protected void handleActive(Boolean isActive) {
		// Do nothing... active state is meaningless to attributes
	}
	
	protected String getPropertyStrings() {
		return "Value:" + getValue();
	}
	
	public void assign(BOAttribute<T> otherAttribute) {
		setAsObject(otherAttribute.getValue());
	}
	
	/* Not relevant for attributes */ 
	protected void doSave() {}
	protected void doDelete() {}
	protected boolean populateAttributes() {return false;}
	protected void createAttributes() {}
	public void handleModified(ModifiedEvent source) {}
}
