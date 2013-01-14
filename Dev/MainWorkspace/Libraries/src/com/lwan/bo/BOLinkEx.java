package com.lwan.bo;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


/**
 * External link version of BOLink, which stores the referenced object as opposed to 
 * dynamically fetching it. This allows all modifyevents that occurs on the
 * linked object to be passed up to this link. Similarly, calling trySave on this
 * object will be automatically be passeed onto the linked child. 
 * 
 * @author Brutalbarbarian
 *
 * @param <T>
 */
public class BOLinkEx<T extends BusinessObject> extends BOLink<T>{
	private Property<T> linked_object_property;
	
	public Property<T> LinkedObjectProperty () {
		if (linked_object_property == null) {
			linked_object_property = new SimpleObjectProperty<T>(this, "LinkedObject");
		}
		return linked_object_property;
	}
	
	public T getLinkedObject() {
		return LinkedObjectProperty().getValue();
	}
	
	public void setLinkedObject(T object) {
		LinkedObjectProperty().setValue(object);
	}
	
	public BOLinkEx() {
		super(null, "");
		LinkedObjectProperty().addListener(new ChangeListener<T>(){
			public void changed(ObservableValue<? extends T> observable,
					T oldValue, T newValue) {
				onLinkedObjectChange(oldValue, newValue);
			}
		});
	}
	
	/**
	 * Equivalent as calling getLinkedObject().trySave()
	 * This function will do nothing if no child is currently set.
	 * 
	 * @throws BOException
	 */
	public void trySave() throws BOException {
		T child = getLinkedObject();
		 if (child != null) {
			 child.trySave();
		 }
	}
	
	protected void onLinkedObjectChange(T oldLink, T newLink) {
		if (oldLink != null) {
			oldLink.removeListener(this);
		}
		if (newLink != null) {
			newLink.addListener(this);
		}
		fireModified(new ModifiedEvent(this, ModifiedEvent.TYPE_LINK));
	}
	
	@Override
	public void handleModified(ModifiedEvent source) {
		fireModified(new ModifiedEvent(source, this));
	}
	
	@Override
	public T getReferencedObject() {
		return getLinkedObject();
	}
	
	public BusinessObject findChildByPath(String path) {
		// Unlike BOLink... just pass this call directly to child.
		return getLinkedObject() == null? null : getLinkedObject().findChildByPath(path);
	}
}
