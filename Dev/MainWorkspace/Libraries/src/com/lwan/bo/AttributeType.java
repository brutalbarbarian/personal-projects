package com.lwan.bo;

public enum AttributeType {
	ID,				// Integer
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
	
	public boolean isDateTime() {
		return	this == Date ||
				this == Time;
	}
}
