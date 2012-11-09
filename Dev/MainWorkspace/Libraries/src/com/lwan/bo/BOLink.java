package com.lwan.bo;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;

/**
 * Business Object representing a weak link between two objects,
 * where the referenced object isn't technically a child of the owner of this
 * object. 
 * Any events the referenced object throws will not be passed up the
 * Hierarchy.
 * Similarly, when saving, the referenced child will not be saved as
 * part of the traversal. 
 * 
 * Therefore, it is not recommended to modify the referenced child in any
 * way through this referenced link.
 * 
 * @author Brutalbarbarian
 *
 */
public class BOLink<T extends BusinessObject> extends BusinessObject{
	private Property<Callback<BOLink<T>, T>> link_caller;
	
	public ReadOnlyProperty<Callback<BOLink<T>, T>> LinkCaller() {
		return _link_caller();
	}
	
	private Property<Callback<BOLink<T>, T>> _link_caller() {
		if (link_caller == null) {
			link_caller = new SimpleObjectProperty<Callback<BOLink<T>, T>> (this, "LinkCaller", null);
		}
		return link_caller;
	}
	
	public BOLink(BusinessObject owner, String name, Callback<BOLink<T>, T> linkCaller) {
		super(owner, name);
		if (linkCaller == null) {
			throw new IllegalArgumentException("Cannot create link with null linkCaller");
		}
		_link_caller().setValue(linkCaller);
	}
	
	public T getReferencedObject() {
		return LinkCaller().getValue().call(this);
	}

	@Override
	protected void doSave() {
		// Do nothing
	}

	@Override
	protected void doDelete() {
		// Do nothing
	}

	@Override
	protected boolean populateAttributes() {
		// Nothing to populate
		return true;
	}

	@Override
	protected void createAttributes() {
		// Nothing to create
	}

	@Override
	public void clearAttributes() {
		// Nothing to clear
		
	}
	
	public String toString(int spaces) {
		T child = getReferencedObject();
		if (child == null) {
			return super.toString(spaces);
		}
		return child.toString(spaces);
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// No direct children... this will never be called
	}

}
