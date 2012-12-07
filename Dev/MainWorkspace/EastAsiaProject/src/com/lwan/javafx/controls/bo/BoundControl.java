package com.lwan.javafx.controls.bo;

import javafx.scene.Node;

/**
 * A common interface used by all controls bound to a BusinessObject.
 * 
 * @author Brutalbarbarian
 *
 * @param <T>
 * 	The type of data displayed by the control, and thus the type of data
 * 	which is expected to be returned from the BoundProperty
 * @param <C>
 * 	The underlying control implementing this bound control interface
 */
public interface BoundControl <T, C extends Node> {
	/**
	 * The bound property which this bound control recieves its data from
	 * 
	 * @return
	 */
	public BoundProperty<T> dataBindingProperty();
	
	/**
	 * As nodes aren't an interface, this is a safe and cleaner way of
	 * getting the underlying control when dealing with the interface.
	 * 
	 * @return
	 */
	public C getNode();
}
