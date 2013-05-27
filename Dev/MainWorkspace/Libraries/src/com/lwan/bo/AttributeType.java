package com.lwan.bo;

import java.sql.Types;

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
	
	
	public static AttributeType typeForSQLType(int type) {
		// no way to get id, currency
		// i suppose i can make currency have 2dp...??
		
		switch (type) {
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.INTEGER:
			return Integer;
		case Types.NUMERIC:	// assume double??
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.REAL:
			return Double;
		case Types.BOOLEAN:
		case Types.BIT:
			return Boolean;
		case Types.DATE:
			return Date;
		case Types.CHAR:
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.NCHAR:
		case Types.NVARCHAR:
		case Types.VARCHAR:
			return String;
		case Types.TIME:
			return Time;
		default:
			return Unknown;
		}
	}
}
