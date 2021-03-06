package com.lwan.jdbc;

/**
 * Parameter object used by StoredProc.
 * 
 * @author Brutalbarbarian
 *
 */
public class Parameter {
	private Object value;
	private String name;
	private int type;
	
	protected Parameter(String name, int type) {
		this.name = name;
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	/**
	 * The caller should take care to make sure val is valid for the
	 * sqltype of this parameter.
	 * 
	 * @param val
	 */
	public void set(Object val) {
		value = val;
	}
	
	public Object get() {
		return value;
	}
	
	public String name() {
		return name;
	}
}
