package com.lwan.bo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

public class BOSet<T extends BOObject> extends BOObject{
	private boolean isMap;
	private Collection<T> c;
	private Map<Object, T> m;
	
	// name of the boattribute which is a direct descendent of the child boobject
	// if this is empty, calling 'findByID' will always return with null
	private Property<String> childIdNameProperty;
	
	public Property<String> childIdName () {
		return (childIdNameProperty = BOUtils.getProperty(childIdNameProperty));
	}
//	public Iterable<T> iterator() {
////		if()
//	}
	
	/**
	 * Construct a set of BOObjects.
	 * 
	 * @param owner
	 * @param name
	 * @param childIdName
	 * @param enforceUniqueId
	 */
	public BOSet(BOObject owner, String name, String childIdName, boolean enforceUniqueId) {
		super(owner, name);
		childIdName().setValue(childIdName);
		isMap = enforceUniqueId;
		if (enforceUniqueId) {
			m = new HashMap<Object, T>();
		} else {
			c = new Vector<T>();
		}
	}
	
	public T ensureActive(Object id) {
		return null;
	}
	
	public int getActiveCount() {
		return 0;
	}
	
	public int getTotalCount() {
		return 0;
	}
	
	public T getActive(int i) {
		return null;
	}
	
	public T get(int i) {
		return null;
	}
	
	public T findByID(Object key) {
		return null;
	}
	
	public void add(BOObject o) {
		
	}
}
