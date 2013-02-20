package com.lwan.bo;


public class BOException extends RuntimeException{
	
	BusinessObject source;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BOException(String message, BusinessObject source) {
		super(message);
		this.source = source;
	}
	
	public BOException(Throwable origin, BusinessObject source) {
		super(origin);
		this.source = source;
	}
	
	public BusinessObject getSource() {
		return source;
	}
	

}
