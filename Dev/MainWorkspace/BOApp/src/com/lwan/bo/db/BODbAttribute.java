package com.lwan.bo.db;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.AttributeType;

public class BODbAttribute <T> extends BOAttribute<T> {
	private Property<String> fieldNameProperty;
	
	public ReadOnlyProperty<String> fieldNameProperty() {
		return _fieldNameProperty();
	}
	
	private Property<String> _fieldNameProperty() {
		if (fieldNameProperty == null) {
			fieldNameProperty = new SimpleObjectProperty<String>(this, "FieldName");
		}
		return fieldNameProperty;
	}

	public BODbAttribute(BusinessObject parent, String name, String field, AttributeType type) {
		this(parent, name, field, type, true, true);
	}
	
	public BODbAttribute(BusinessObject parent, String name, String field, AttributeType type,
			boolean allowNulls, boolean allowUserModify) {
		super(parent, name, type, allowNulls, allowUserModify);
		
		_fieldNameProperty().setValue(field);
	}

}
