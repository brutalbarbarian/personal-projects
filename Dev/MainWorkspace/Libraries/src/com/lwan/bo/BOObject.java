package com.lwan.bo;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.Validator;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class BOObject implements ChangeListener<BOObject>, InvalidationListener {
	
	// properties
	// active
	// change listener
	// parent
	
	// property declarations
	private Property<BOObject> owner;
	private Property<Boolean> active;
	private Property<String> name;
	
	private HashMap<String, BOObject> children;
	private List<ChangeListener> changeListeners;	// the parent is always a change listener
	
	
	
	public BOObject(BOObject owner, String name) {
		children = new HashMap<>();
	}
	
	// Add a BOObject as a child of this BOOBject.
	// when a child throws a notification (i.e. change),
	// it will notify its parent.
	public void addAsChild(BOObject object) {
		children.put(object.name().getValue(), object);
	}

	
	// property accessors
	public Property<BOObject> owner() {
		return (owner = BOUtils.getProperty(owner));
	}
	public Property<String> name() {
		return (name = BOUtils.getProperty(name));
	}
	
	@Override
	public void changed(ObservableValue<? extends BOObject> observableValue,
			BOObject newValue, BOObject oldValue) {
		fireChanged(observableValue, newValue, oldValue);
	}
	
	protected void fireChanged(ObservableValue<? extends BOObject> observableValue,
			BOObject newValue, BOObject oldValue) {
		owner().getValue().changed(observableValue, newValue, oldValue);
//		for(ChangeListener)
	}

	@Override
	public void invalidated(Observable arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
