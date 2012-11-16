package com.lwan.bo;

import com.lwan.javafx.property.ValidatedProperty;
import com.lwan.javafx.property.ValidationListener;
import com.lwan.util.GenericsUtil;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

public class BOAttribute <T> extends BusinessObject {
	/* Properties Declarations */
	private Property<Boolean> allow_nulls;
	private Property<Boolean> allow_user_modify;
	private Property<Boolean> user_set;
	private Property<Object> user_set_source;
	
	private ValidatedProperty<T> value;
	
	
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
	
	public Property<Boolean> AllowUserModify() {
		if (allow_user_modify == null) {
			allow_user_modify = new SimpleObjectProperty<Boolean>(this, "AllowUserModify", true);
		}
		return allow_user_modify;
	}
	
	public ReadOnlyProperty<Object> UserSetSource() {
		return _user_set_source();
	}
	
	/**
	 * Will be set to true if userSetValue() is called as opposed to
	 * setValue() or Value().set(). All interface objects which is bound to
	 * this attribute should trigger this to be true. 
	 * This property allows code to be tailored to user changes as opposed to changes
	 * made as result of internal calculations. 
	 * 
	 * @return
	 */
	public ReadOnlyProperty<Boolean> UserSet() {
		return _user_set();
	}
	
	/* Private properties */
	private Property<Boolean> _user_set() {
		if (user_set == null) {
			user_set = new SimpleObjectProperty<Boolean>(this, "UserSet", false);
		}
		return user_set;
	}
	
	private Property<Object> _user_set_source() {
		if (user_set_source == null) {
			user_set_source = new SimpleObjectProperty<Object>(this, "UserSetSource", null);
		}
		return user_set_source;
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
	
	/**
	 * Represents when verifying if a datastructure is valid for saving, if this
	 * attribute is allowed to be null. 
	 * 
	 * @return
	 */
	public Property<Boolean> AllowNulls() {
		if (allow_nulls == null) {
			allow_nulls = new SimpleObjectProperty<>(this, "AllowNulls");
		}
		return allow_nulls;
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
	
	/**
	 * Effectively 0 if the value is null, or cast as double if the value is
	 * of type number. If the value is not null and is not a number, a
	 * NumberFormatExcption is thrown
	 * 
	 * @return
	 */
	public double asDouble() {
		T val = getValue();
		if (val == null) {
			return 0;
		} else if (val instanceof Number) {
			return ((Number)val).doubleValue();
		} else {
			throw new NumberFormatException("Cannot convert object of type " + val.getClass().getName() +
					"into a Number");
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
	
	public boolean isAttribute() {
		return true;
	}
	
	public BOAttribute(BusinessObject parent, String name) {
		this(parent, name, true, true);
	}
	
	
	public BOAttribute(BusinessObject parent, String name, boolean allowNulls, boolean allowUserModify) {
		super(parent, name);
		
		AllowNulls().setValue(allowNulls);
		AllowUserModify().setValue(allowUserModify);
		
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
	 * All interface objects setting the value of this attribute representing a user's
	 * change should call this function, passing some representation of itself in
	 * as the source. This allows listeners listening to modifications to this attribute
	 * to tailor code specific for user changes from certain sources.
	 * 
	 * 
	 * @param val
	 */
	public void userSetValue(T val, Object source) {
		if (AllowUserModify().getValue()) {
			_user_set().setValue(true);
			_user_set_source().setValue(source);
			setValue(val);
			_user_set_source().setValue(null);
			_user_set().setValue(false);
		} else {
			throw new RuntimeException("Attempted user modification of value in " +
					getClass().getName() + " from source: " + source.toString());
		}
	}
	
	/**
	 * All interface objects clearing the value of this attribute representing a user's
	 * change should call this function, passing some representation of itself as the source.
	 * This allows listeners listening to modifications to this attribute to tailor code
	 * specific for user changes from certain sources.
	 * 
	 * @param source
	 */
	public void userClear(Object source) {
		userSetValue(null, source);
	}
	
	/**
	 * Shortcut for calling UserSet().getValue()
	 * 
	 * @return
	 */
	public boolean isUserSet() {
		return UserSet().getValue();
	}
	
	/**
	 * Checks that the values contained within both BOAttributes are the same.
	 * 
	 */
	public boolean equivalentTo (BusinessObject other, Callback<BusinessObject, Boolean> ignoreFields) {
		if (super.equivalentTo(other, ignoreFields)) {
			return GenericsUtil.Equals(getValue(), ((BOAttribute<?>)other).getValue());
		} else {
			return false;
		}
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
	 * Same as calling clear
	 * 
	 */
	public void clearAttributes() {
		clear();
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
