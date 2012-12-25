package com.lwan.javafx.controls.bo.binding;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;

public class BoundProperty <T> extends SimpleObjectProperty<T>{
	private Property<BOLinkEx<?>> attributeLinkProperty;
	private Property<String> pathProperty;
	private Property<BOAttribute<?>> linkedAttributeProperty;
	private BooleanProperty editableProperty;
	
	// This should be binded by the control
	public ObservableBooleanValue editableProperty() {
		return _editableProperty();
	}
	
	public boolean isEditable() {
		return editableProperty().getValue();
	}
	
	public Property<BOLinkEx<?>> attributeLinkproperty() {
		if (attributeLinkProperty == null) {
			attributeLinkProperty = new SimpleObjectProperty<>(this, "AttributeLink");
		}
		return attributeLinkProperty;
	}
	public BOLinkEx<?> getAttributeLink() {
		return attributeLinkproperty().getValue();
	}
	
	public Property<String> pathProperty() {
		if (pathProperty == null) {
			pathProperty = new SimpleStringProperty(this, "Path");
		}
		return pathProperty;
	}
	public String getPath() {
		return pathProperty().getValue();
	}
	
	public ReadOnlyProperty<BOAttribute<?>> linkedAttributeProperty() {
		return _linkedAttributeProperty();
	}
	
	protected BooleanProperty _editableProperty() {
		if(editableProperty == null) {
			editableProperty = new SimpleBooleanProperty(this, "Editable", false);
		}
		return editableProperty;
	}
	
	protected Property<BOAttribute<?>> _linkedAttributeProperty() {
		if (linkedAttributeProperty == null) {
			linkedAttributeProperty = new SimpleObjectProperty<>(this, "LinkedAttribute");
		}
		return linkedAttributeProperty;
	}
	public BOAttribute<?> getLinkedAttribute() {
		return linkedAttributeProperty().getValue();
	}
	
	public BoundProperty(BoundControl<T> owner, BOLinkEx<?> link, String path) {
		super(owner, "DataBinding");
		
		attributeLinkproperty().setValue(link);
		pathProperty().setValue(path);
	}
	
	public BoundProperty(BoundControl<T> owner, BoundProperty<?> existing) {
		this(owner, existing.getAttributeLink(), existing.getPath());
	}
	
	@SuppressWarnings("unchecked")
	public BoundControl<T> getBoundControl() {
		return (BoundControl<T>)getBean();
	}
	
	@SuppressWarnings("unchecked")
	public void buildAttributeLinks () {
		// Unbind previous attribute if exists
		if (getLinkedAttribute() != null) {
			unbindBidirectional((Property<T>) getLinkedAttribute().Value());
		}
		
		// Build the attributelink
		doBuildAttributeLinks();
		
		// Bind new attribute
		if (getLinkedAttribute() != null) {
			bindBidirectional((Property<T>) getLinkedAttribute().Value());
		} else {
			setValue(null);
		}
	}
	
	protected void doBuildAttributeLinks() {
		// remove previous links
		_editableProperty().unbind();
		
		// attempt to bind a new attribute
		BOLinkEx<?> link = getAttributeLink();
		String path = getPath();
		if (link != null && path != null) {
			_linkedAttributeProperty().setValue((BOAttribute<?>) link.findChildByPath(path));
		} else {
			_linkedAttributeProperty().setValue(null);
		}
		
		// bind editable
		if (getLinkedAttribute() != null) {
			_editableProperty().bind(getLinkedAttribute().AllowUserModify());
		} else {
			_editableProperty().setValue(false);
		}
	}
}
