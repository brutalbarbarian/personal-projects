package com.lwan.bo.db;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;

public class BODbAttribute <T> extends BOAttribute<T> {
	private Property<String> field_name;
	
	public ReadOnlyProperty<String> FieldName() {
		return _field_name();
	}
	
	private Property<String> _field_name() {
		if (field_name == null) {
			field_name = new SimpleObjectProperty<String>(this, "FieldName");
		}
		return field_name;
	}

	public BODbAttribute(BusinessObject parent, String name, String field) {
		this(parent, name, field, true, null, null);
	}
	
	public BODbAttribute(BusinessObject parent, String name, String field,
			boolean allowNulls, T defaultValue, T nullValue) {
		super(parent, name, allowNulls, defaultValue, nullValue);
		
		_field_name().setValue(field);
	}

}
