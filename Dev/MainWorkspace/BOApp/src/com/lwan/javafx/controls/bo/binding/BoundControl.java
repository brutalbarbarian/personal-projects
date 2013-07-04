package com.lwan.javafx.controls.bo.binding;

import javafx.beans.property.BooleanProperty;

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
public interface BoundControl <T> {
	/**
	 * The bound property which this bound control recieves its data from
	 * 
	 * @return
	 */
	public BoundProperty<T> dataBindingProperty();
	
	public BooleanProperty enabledProperty();
	public boolean isEnabled();
}
