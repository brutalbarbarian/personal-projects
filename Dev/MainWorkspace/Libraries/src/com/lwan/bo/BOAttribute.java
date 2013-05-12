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
	private Property<Boolean> allowNullsProperty;
	private Property<Boolean> allowUserModifyProperty;
	private Property<Boolean> userSetProperty;
	private Property<Object> userSetSourceProperty;
	private Property<AttributeType> attributeTypeProperty;
	
	private ValidatedProperty<T> valueProperty;
	private Property<T> previousValueProperty;
	
	// Specific for numerics... note these only impact UI input validation
	private Property<Boolean> allowNegativeProperty;
	private Property<Integer> percisionProperty;
	
	
	/* Property accessors */
	/**
	 * Get the property containing the value represented by this attribute. 
	 * 
	 * @return
	 */
	public ValidatedProperty<T> valueProperty() {
		if (valueProperty == null) {
			valueProperty = new ValidatedProperty<>(this, "Value");
		}
		return valueProperty;
	}
	
	public ReadOnlyProperty<T> previousValueProperty() {
		return _previousValueProperty();
	}
	
	private Property<T> _previousValueProperty() {
		if (previousValueProperty == null) {
			previousValueProperty = new SimpleObjectProperty<>(this, "PreviousValue", null);
		}
		return previousValueProperty;
	}
	
	/**
	 * This is only relevant for numeric type attributes.
	 * 
	 * @return
	 */
	public Property<Boolean> allowNegativeProperty() {
		if (allowNegativeProperty == null) {
			allowNegativeProperty = new SimpleObjectProperty<Boolean>(this, "AllowNegative", true);
		}
		return allowNegativeProperty;
	}
	
	public Property<Integer> percisionProperty() {
		if (percisionProperty == null) {
			percisionProperty = new SimpleObjectProperty<Integer>(this, "Percision", 
					// Default to 2 if is currency
					getAttributeType() == AttributeType.Currency? 2 : 10);
		}
		return percisionProperty;
	}
	
	public ReadOnlyProperty<AttributeType> attributeTypeProperty () {
		return _attributeTypeProperty();
	}
	
	public Property<Boolean> allowUserModifyProperty() {
		if (allowUserModifyProperty == null) {
			allowUserModifyProperty = new SimpleObjectProperty<Boolean>(this, "AllowUserModify", true);
		}
		return allowUserModifyProperty;
	}
	
	public ReadOnlyProperty<Object> userSetSourceProperty() {
		return _userSetSourceProperty();
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
	public ReadOnlyProperty<Boolean> userSetProperty() {
		return _userSetProperty();
	}
	
	/* Private properties */
	private Property<Boolean> _userSetProperty() {
		if (userSetProperty == null) {
			userSetProperty = new SimpleObjectProperty<Boolean>(this, "UserSet", false);
		}
		return userSetProperty;
	}
	
	private Property<AttributeType> _attributeTypeProperty() {
		if (attributeTypeProperty == null) {
			attributeTypeProperty = new SimpleObjectProperty<AttributeType>(this, "AttributeType", AttributeType.Unknown);
		}
		return attributeTypeProperty;
	}
	
	private Property<Object> _userSetSourceProperty() {
		if (userSetSourceProperty == null) {
			userSetSourceProperty = new SimpleObjectProperty<Object>(this, "UserSetSource", null);
		}
		return userSetSourceProperty;
	}
	
	protected String doVerifyState() {
		// if allow null state is satisfied.
		// Shouldn't need to check if the value itself is valid, as it should have 
		// already been validated when the value was set
		if ((isNull() || AllowNulls().getValue())) {
			return null;
		} else {
			return "Attribute '" + nameProperty().getValue() + "' is invalid";
		}
	}
	
	public void dispose() {
		// unbind all listeners
		valueProperty().unbind();
		
		super.dispose();
	}
	
	/**
	 * Represents when verifying if a datastructure is valid for saving, if this
	 * attribute is allowed to be null. 
	 * 
	 * @return
	 */
	public Property<Boolean> AllowNulls() {
		if (allowNullsProperty == null) {
			allowNullsProperty = new SimpleObjectProperty<>(this, "AllowNulls");
		}
		return allowNullsProperty;
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
	
	public boolean equalValue(BOAttribute<?> other) {
		return GenericsUtil.Equals(getValue(), other.getValue());
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
			return (Boolean)val;
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
	
	public BOAttribute(BusinessObject parent, String name, AttributeType type) {
		this(parent, name, type, true, true);
	}
	
	
	public BOAttribute(BusinessObject parent, String name, AttributeType type, boolean allowNulls, boolean allowUserModify) {
		super(parent, name);
		
		_attributeTypeProperty().setValue(type);
		AllowNulls().setValue(allowNulls);
		allowUserModifyProperty().setValue(allowUserModify);
		
		valueProperty().addListener(new ChangeListener<T>(){
			public void changed(ObservableValue<? extends T> observable,
					T oldValue, T newValue) {
				doChanged(oldValue, newValue);
			}
		});
	}
	
	public void addValidationListener(ValidationListener<T> listener) {
		valueProperty().addListener(listener);
	}
	
	public void addChangeListener(ChangeListener<T> listener) {
		valueProperty().addListener(listener);
	}
	
	public void setBeforeChangeListener(Callback<T, T> callback) {
		valueProperty().setBeforeSetValue(callback);
	}
	
	
	/**
	 * Will be called upon value changing.
	 * 
	 * @param oldValue
	 * @param newValue
	 */
	public void doChanged(T oldValue, T newValue) {
		_previousValueProperty().setValue(oldValue);	// reference...
		fireModified(new ModifiedEvent(this, ModifiedEvent.TYPE_ATTRIBUTE));
	}	
	
	/**
	 * Convenience method for getting the value. 
	 * This is the same as calling Value().get()
	 * 
	 * @return
	 */
	public T getValue() {
		return valueProperty().getValue();
	}
	

	/**
	 * Convenience method for setting the value.
	 * This is the same as calling Value().set()
	 * 
	 * @param val
	 */
	public void setValue(T val) {
		valueProperty().setValue(val);
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
		if (allowUserModifyProperty().getValue()) {
			_userSetProperty().setValue(true);
			_userSetSourceProperty().setValue(source);
			setValue(val);
			_userSetSourceProperty().setValue(null);
			_userSetProperty().setValue(false);
		} else {
			throw new RuntimeException("Attempted user modification of value in " +
					getClass().getName() + " from source: " + source.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void userSetValueAsObject(Object val, Object source) {
		userSetValue((T)val, source);
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
		return userSetProperty().getValue();
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
	
	public AttributeType getAttributeType() {
		return attributeTypeProperty().getValue();
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
	
	
	protected void handleActive(boolean isActive) {
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
