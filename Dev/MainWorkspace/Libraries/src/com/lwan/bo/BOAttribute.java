package com.lwan.bo;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Binding;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;

public class BOAttribute <T> extends BOObject implements Binding<T>{
	public BOAttribute(BOObject parent, String name) {
		super(parent, name);
	}

	/**
	 * Add a listener.
	 * 
	 * 
	 */
	public void addListener(ChangeListener<? super T> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T getValue() {
		
		return null;
	}

	@Override
	public void removeListener(ChangeListener<? super T> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(InvalidationListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(InvalidationListener arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setValue(T val) {
		
	}
	
	/**
	 * Clears the value
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	
	public ObservableList<?> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Is called upon setValue
	 * 
	 */
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
