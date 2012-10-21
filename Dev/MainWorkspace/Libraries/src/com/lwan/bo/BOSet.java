package com.lwan.bo;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.lwan.util.GenericsUtil;
import com.lwan.util.StringUtil;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;

public class BOSet<T extends BOObject> extends BOObject implements Iterable<T>{
	
	// name of the BOattribute which is a direct descendant of the child BOobject
	// if this is empty, calling 'findByID' will always return with null
	private Property<String> child_id_name;
	private Property<Callback<BOSet<T>, T>> instance_factory;
	
	/* public property accessors */
	public ReadOnlyProperty<String> ChildIDName() {
		return _child_id_name();
	}
	public ReadOnlyProperty<Callback<BOSet<T>, T>> InstanceFactory () {
		return _instance_factory();
	}
	
	/* private property accessors */
	private Property<String> _child_id_name() {
		if (child_id_name == null) {
			child_id_name = new SimpleObjectProperty<String>(this, "ChildIDName");
		}
		return child_id_name;
	}
	private Property<Callback<BOSet<T>, T>> _instance_factory () {
		if (instance_factory == null) {
			instance_factory = new SimpleObjectProperty<Callback<BOSet<T>,T>>(this, "InstanceFactory");
		}
		return instance_factory;
	}
	
	// private fields
	private List<T> children;
	
	/**
	 * Construct a set of BOObjects.
	 * 
	 * @param owner
	 * @param name
	 * @param childIdName
	 */
	public BOSet(BOObject owner, String name, String childIdName, Callback<BOSet<T>, T> instanceFactory) {
		super(owner, name);
		
		children = new Vector<T>();
		_child_id_name().setValue(childIdName);
		_instance_factory().setValue(instanceFactory);
	}

	public T findChildByID(Object id) {
		String childAttr = ChildIDName().getValue();
		if (!StringUtil.isNullOrBlank(childAttr)) {
			for (T child : children) {
				BOObject attr = child.getChildByName(childAttr);
				// If the attribute can be found, check the values are equal
				if (attr != null && attr instanceof BOAttribute<?> && 
						GenericsUtil.Equals(((BOAttribute<?>)attr).getValue(), id)) {
					// If they are equal... just return it. Assume there's only
					// one child with the same 'id'
					return child;
				}
			}
		}
		return null;
	}
	
	public int getActiveCount() {
		int count = 0;
		for (T child : children) {
			if (child.Active().getValue()) {
				count++;
			}
		}
		return count;
	}
	
	public T getActive(int index) {
		int active = -1;
		for (T child : children) {
			if (child.Active().getValue()) {
				active++;
			}
			if (active == index) {
				return child;
			}
		}
		return null;	// likely index out of bounds of reached here
	}
	
	public int getCount() {
		return children.size();
	}
	
	public T get(int index) {
		if (index < 0 || index >= children.size()) {
			return null;
		} else {
			return children.get(index);
		}
	}
	
	/**
	 * Ensures a child object with the value within it's attribute by name declared in ChildIDName
	 * is equal to 'id' exists and is set to active.  
	 * 
	 * @param id
	 */
	public void ensureActive(Object id) {
		T child = findChildByID(id);
		if (child == null) {
			child = InstanceFactory().getValue().call(this);
			BOObject idAttr = child.getChildByName(ChildIDName().getValue());
			if (idAttr != null) {
				((BOAttribute<?>)idAttr).setAsObject(id);
			}
			children.add(child);
		}
		child.ensureActive();
	}

	@Override
	/**
	 * Iterate over active objects
	 * 
	 */
	public Iterator<T> iterator() {
		return new ActiveIterator();
	}
	
	private class ActiveIterator implements Iterator<T>{
		int activePassed;
		int activeCount;
		Iterator<T> iterator;
		
		private ActiveIterator() {
			activePassed = 0;
			activeCount = getActiveCount();
			iterator = children.iterator();
		}
		
		public boolean hasNext() {
			return activePassed < activeCount;
		}

		@Override
		public T next() {
			while(iterator.hasNext()) {
				T object = iterator.next();
				if (object.Active().getValue()) {
					activePassed++;
					return object;
				}
			}
			// Shouldn't ever reach here unless next is called after hasNext returned false.
			return null;
		}

		@Override
		public void remove() {
			iterator.remove();
		}
	}
	
	/**
	 * Will pass the active handling down to all the children of this set as well.
	 * 
	 * @param isActive
	 */
	public void handleActive(boolean isActive) {
		// delete all inactive children if isActive is true
		// not sure if this is the best practice... but don't really see much better way of handling this
		if (isActive && children != null) {
			Iterator<T> it = children.iterator();
			while (it.hasNext()) {
				T child = it.next();
				if (!child.Active().getValue()) {
					it.remove();
				}
			}
		}
		
		super.handleActive(isActive);
		// Set the active state of all children to match this
		if (children != null) {
			for (T child : children) {
				child.Active().setValue(isActive);
			}
		}
	}
	
	/**
	 * If will normally save, then this will also call save() on all
	 * the children as well.
	 * 
	 */
	public boolean save() {
		if (super.save()) {
			for (T child : children) {
				child.save();
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	/**
	 * Do nothing by default. Sets usually should only be a container for a one to many relationship
	 */
	protected void doSave() {}

	@Override
	/**
	 * Do nothing by default. Sets usually should only be a container for a one to many relationship
	 */
	public void doDelete() {}

	@Override
	/**
	 * Do nothing by default. Sets usually shouldn't have any attributes
	 */
	protected boolean populateAttributes() {return false;}

	@Override
	/**
	 * Do nothing by default. Sets usually shouldn't have any attributes
	 */
	protected void createAttributes() {}

	@Override
	/**
	 * Do nothing by default. Sets usually shouldn't have any attributes
	 */
	protected void clearAttributes() {}

	@Override
	/**
	 * Do nothing by default. 
	 * 
	 */
	public void handleModified(ModifiedEvent source) {}
}
