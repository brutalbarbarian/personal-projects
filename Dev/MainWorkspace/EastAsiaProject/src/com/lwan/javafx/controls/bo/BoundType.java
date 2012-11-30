package com.lwan.javafx.controls.bo;

/**
 * <p>
 * Allows controls to implement default display/stored value maps,
 * depending on what the control displays, and what the stored value
 * type is.</p>
 * <p>
 * This is necessary as there is no easy way of finding out the class type
 * of a Generic. </p>
 * 
 * @author Brutalbarbarian
 *
 */
public enum BoundType {
	Integer,	// Integer 
	Currency, 	// Double (with formatting)
	Double, 	// Double
	String, 	// String
	Date,		// Date
	Custom		// Unknown... user set.
	
}
