package com.lwan.javafx.controls.bo.binding;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.ModifiedEventType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

public class BoundProperty <T> extends SimpleObjectProperty<T> implements ModifiedEventListener{
	private Property<BOLinkEx<?>> attributeLinkProperty;
	private Property<String> pathProperty;
	private Property<BOAttribute<?>> linkedAttributeProperty;
	private BooleanProperty editableProperty;
	
	boolean handlingModified;
	
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
	
	public BoundProperty(Object owner, BOLinkEx<?> link, String path) {
		super(owner, "DataBinding");
		
		handlingModified = false;
		link.addListener(this);
		attributeLinkproperty().setValue(link);
		pathProperty().setValue(path);
		buildBindings();
	}
	
	public BoundProperty(Object owner, BoundProperty<?> existing) {
		this(owner, existing.getAttributeLink(), existing.getPath());
	}
	
	protected void buildBindings() {
		addListener(new ChangeListener<T>() {
			public void changed(ObservableValue<? extends T> arg0, T oldValue,
					T newValue) {
				if (!handlingModified && getLinkedAttribute() != null) {
					getLinkedAttribute().userSetValueAsObject(newValue, getBean());
				}
			}
		});
	}
	
	/**
	 * Will return false if the previous attribute link is the same
	 * Otherwise, will return true.
	 * 
	 * @return
	 */
	public boolean buildAttributeLinks () {
		// try find the new attribute
		BOAttribute<?> newAttr;
		BOLinkEx<?> link = getAttributeLink();
		String path = getPath();
		if (link != null && path != null) {
			newAttr = link.findAttributeByPath(path);
		} else {
			newAttr = null;
		}
		
		// check this against the old attr
		BOAttribute<?> oldAttr = getLinkedAttribute();
		if (oldAttr == newAttr) {
			return false;	// do nothing.
		}

		// Unbind previous attribute if exists
		if (oldAttr != null) {
			oldAttr.removeListener(this);
		}
		
		// remove previous links
		_editableProperty().unbind();
		
		// attempt to bind a new attribute
		_linkedAttributeProperty().setValue(newAttr);
		
		// bind editable
		if (newAttr != null) {
			_editableProperty().bind(newAttr.allowUserModifyProperty());
		} else {
			_editableProperty().setValue(false);
		}
				
		// Bind new attribute
		if (newAttr != null) {
			// Only one way bindings.
			newAttr.addListener(this);
			setModifiedValue(getEffectiveValue());
		} else {
			setValue(null);
		}
		
		return true;
	}

	public void handleModified(ModifiedEvent event) {
		if (event.getCaller() == getLinkedAttribute()) {
			setModifiedValue(getEffectiveValue());
		} else if ((event.getType() == ModifiedEventType.Link &&
				event.getSource() != getAttributeLink())) {
			buildAttributeLinks();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected T getEffectiveValue() {
		if (getLinkedAttribute() != null) {
			return ((T)getLinkedAttribute().getValue());
		}
		return null;
	}
	
	protected void setModifiedValue(T value) {
		handlingModified = true;
		set(value);
		handlingModified = false;		
	}
}
