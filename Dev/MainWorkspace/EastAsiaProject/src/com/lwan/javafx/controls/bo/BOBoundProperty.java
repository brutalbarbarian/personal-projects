package com.lwan.javafx.controls.bo;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

/**
 * A property which stores the displayed value of a bound boattribute link
 * 
 * @author Brutalbarbarian
 *
 * @param <S>
 * 		The type of the attribute (stored value)
 * @param <D>
 * 		The type of the object stored by this property (display value)
 */
public class BOBoundProperty<S, D> extends SimpleObjectProperty<D> {
	private Property<BOLinkEx<?>> link;
	private Property<String> link_path;
	private Property<BOAttribute<S>> attribute_link;
	
	private Property<Callback<S, D>> display_value_map;	// map to convert stored value to display
	private Property<Callback<D, S>> stored_value_map;	// map to convert display value to stored value
	private boolean changeLock;
	
	public BOBoundProperty(BOBoundControl<S, D> parent, BOLinkEx<?> link, String path) {
		super(parent, "BOBound");
		Link().setValue(link);
		LinkPath().setValue(path);
		addListener(new ChangeListener<D>() {
			public void changed(ObservableValue<? extends D> value, D oldValue,
					D newValue) {
				onDisplayValueChange(newValue);
			}
		});
		freeLock();
	}
	
	@SuppressWarnings("unchecked")
	public BOBoundControl<S, D> getOwner() {
		return (BOBoundControl<S, D>)getBean();
	}
	
	// This is to avoid infinite loops when changes occur.
	private boolean requestLock(){
		if (changeLock) {
			return false;
		} else {
			changeLock = true;
			return true;
		}
	}
	
	private void freeLock() {
		changeLock = false;
	}
	
	@SuppressWarnings("unchecked")
	protected void onStoredValueChange(S value) {
		if (requestLock()) try {
			D displayValue = null;
			Callback<S, D> callback = getDisplayValueMap();
			if (callback != null) {
				displayValue = callback.call(value);
			} else {
				displayValue = (D)value;	// just force cast...
			}
			setValue(displayValue);
		} finally {
			freeLock();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void onDisplayValueChange(D value) {
		if (requestLock()) try {
			BOAttribute<S> attribute = getAttributeLink();
			if (attribute != null) {
				S storedValue = null;
				Callback<D, S> callback = getStoredValueMap();
				if (callback != null) {
					storedValue = callback.call(value);
				} else {
					storedValue = (S)value;	// just force cast... can be dangerous
				}
				attribute.userSetValue(storedValue, this);
			}
		} finally {
			freeLock();
		}
	}
	
	// rebuilds all attribute links 
	@SuppressWarnings("unchecked")
	public void buildAttributeLinks() {
		BusinessObject link = Link().getValue().getLinkedObject();
		BOAttribute<S> attr = getAttributeLink();
		if (attr != null) {
			attr.removeListener(attributeListener());
			attr = null;
		}
		if (link != null) {
			attr = (BOAttribute<S>) link.findChildByPath(getLinkPath());
		}
		
		if (attr != null) {
			getOwner().update(attr);	// give the owner a chance to update the maps
			
			attr.addListener(attributeListener());
			// update the display value
			onStoredValueChange(attr.getValue());
		} else {
			onStoredValueChange(null);
		}
		_attribute_link().setValue(attr);
	}
	
	
	private AttributeListener attributeListener;
	private AttributeListener attributeListener() {
		if (attributeListener == null) {
			attributeListener = new AttributeListener();
		}
		return attributeListener;
	}
	private class AttributeListener implements ModifiedEventListener {
		@SuppressWarnings("unchecked")
		public void handleModified(ModifiedEvent event) {
			onStoredValueChange((S) event.asAttribute().getValue());
		}
	}
	
	public Property<Callback<D, S>> StoredValueMap(){
		if (stored_value_map == null) {
			stored_value_map = new SimpleObjectProperty<>();
		}
		return stored_value_map;
	}
	
	public void setStoredValueMap(Callback<D, S> callback) {
		StoredValueMap().setValue(callback);
	}
	
	public Callback<D, S> getStoredValueMap() {
		return StoredValueMap().getValue();
	}
	
	public Property<Callback<S, D>> DisplayValueMap(){
		if (display_value_map == null) {
			display_value_map = new SimpleObjectProperty<>();
		}
		return display_value_map;
	}
	
	public void setDisplayValueMap(Callback<S, D> callback) {
		DisplayValueMap().setValue(callback);
	}
	
	public Callback<S, D> getDisplayValueMap() {
		return DisplayValueMap().getValue();
	}
	
	public Property<BOLinkEx<?>> Link() {
		if (link == null) {
			link = new SimpleObjectProperty<>(this, "Link");
		}
		return link;
	}
	
	public BOLinkEx<?> getLink() {
		return Link().getValue();
	}
	
	public void setLink(BOLinkEx<?> link) {
		Link().setValue(link);
	}
	
	public Property<String> LinkPath() {
		if (link_path == null) {
			link_path = new SimpleObjectProperty<>(this, "LinkPath");
		}
		return link_path;
	}
	
	public String getLinkPath() {
		return LinkPath().getValue();
	}
	
	public void setLinkPath(String path) {
		LinkPath().setValue(path);
	}
	
	/**
	 * This is the cached linked attribute. This is set using Link and LinkPath
	 * in buildAttributeLinks()
	 * 
	 * @return
	 */
	public ReadOnlyProperty<BOAttribute<S>> AttributeLink() {
		return _attribute_link();
	}
	
	public BOAttribute<S> getAttributeLink() {
		return AttributeLink().getValue();
	}
	
	private Property<BOAttribute<S>> _attribute_link() {
		if (attribute_link == null) {
			attribute_link = new SimpleObjectProperty<>(this, "AttributeLink");
		}
		return attribute_link;	
	}

}
