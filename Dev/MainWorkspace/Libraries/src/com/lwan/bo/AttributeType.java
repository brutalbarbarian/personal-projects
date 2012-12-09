package com.lwan.bo;

public enum AttributeType {
	Integer,		// Integer
	Double,			// Double
	Currency,		// Double
	Date,			// Date
	Time,			// Date
	String,			// String
	Boolean,		// Boolean
	Unknown;		// ???
	
	public boolean isNumeric() {
		return 	this == Integer || 
				this == Currency || 
				this == Double;
	}
}
