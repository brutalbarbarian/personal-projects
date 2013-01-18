package com.lwan.eaproj.sp;

import com.lwan.jdbc.StoredProc;

public class PS_TAX_for_set extends StoredProc{
	private static final String[] PARAMS = {};
	private static final int[] PARAM_TYPES = {};
	private static final String[] STATEMENTS = {
		"select tax_id, tax_name, tax_percent " +
		"from TAX_tax "};
	
	public PS_TAX_for_set () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}	
}
