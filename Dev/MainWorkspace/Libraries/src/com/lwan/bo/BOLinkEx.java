package com.lwan.bo;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
	private Property<BOLinkEx<?>> owner_link_property;
	private Property<String> owner_link_path_property;
	private ChangeListener<BusinessObject> ownerChangeListener; 
	
	public Property<T> linkedObjectProperty () {
		if (linked_object_property == null) {
			linked_object_property = new SimpleObjectProperty<T>(this, "LinkedObject");
		}
		return linked_object_property;
	}
	
	public Property<BOLinkEx<?>> ownerLinkProperty () {
		if (owner_link_property == null) {
			owner_link_property = new SimpleObjectProperty<>(this, "OwnerLink");
			ownerChangeListener = new ChangeListener<BusinessObject>() {
				@SuppressWarnings("unchecked")
				public void changed(
						ObservableValue<? extends BusinessObject> arg0,
						BusinessObject oldValue, BusinessObject newValue) {
					if (newValue != null && owner_link_path_property != null) {
						setLinkedObject((T)newValue.findChildByPath(ownerLinkPathProperty().getValue()));
					} else {
						setLinkedObject(null);
					}
				}
			};
			owner_link_property.addListener(new ChangeListener<BOLinkEx<?>>() {
				public void changed(
						ObservableValue<? extends BOLinkEx<?>> arg0,
						BOLinkEx<?> oldValue, BOLinkEx<?> newValue) {
					if (oldValue != null) {
						oldValue.linkedObjectProperty().removeListener(ownerChangeListener);
					}
					if (newValue != null) {
						newValue.linkedObjectProperty().addListener(ownerChangeListener);
					}
				}				
			});
		}
		return owner_link_property;
	}
	
	public Property<String> ownerLinkPathProperty() {
		if (owner_link_path_property == null) {
			owner_link_path_property = new SimpleStringProperty(this, "OwnerLinkPath");

		}
		return owner_link_path_property;
	}
	
	public BusinessObject getLinkedOwner() {
		if (owner_link_property == null || owner_link_path_property.getValue() == null) {
			return null;
		} else {
			return owner_link_property.getValue();
		}
	}
	
	public void setLinkOwner(BOLinkEx<?> ownerLink) {
		ownerLinkProperty().setValue(ownerLink);
	}
	
	public T getLinkedObject() {
		return linkedObjectProperty().getValue();
	}
	
	public void setLinkedObject(T object) {
		linkedObjectProperty().setValue(object);
	}
	
	public void setOwnerLinkPath(String path) {
		ownerLinkPathProperty().setValue(path);
	}
	
	public BOLinkEx() {
		super(null, "");
		linkedObjectProperty().addListener(new ChangeListener<T>(){
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
		fireModified(new ModifiedEvent(this, ModifiedEventType.Link));
	}
	
	public void dispose() {
		T link = getLinkedObject();
		if (link != null) {
			// remove reference to this link
			link.removeListener(this);
		}
		// clear link
		linkedObjectProperty().setValue(null);
		
		// clear listener to parent if needed
		if (owner_link_property != null && owner_link_property.getValue() != null) {
			owner_link_property.getValue().linkedObjectProperty().removeListener(ownerChangeListener);
		}
		
		super.dispose();
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
	
	protected boolean populateAttributes() {
		return getLinkedObject() == null ? false : 
			getLinkedObject().populateAttributes();
	}
}
