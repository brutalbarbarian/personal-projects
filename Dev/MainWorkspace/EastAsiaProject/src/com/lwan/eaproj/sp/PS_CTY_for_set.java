package com.lwan.eaproj.sp;

import com.lwan.jdbc.StoredProc;

public class PS_CTY_for_set extends StoredProc{
	private static final String[] PARAMS = {};
	private static final int[] PARAM_TYPES = {};
	private static final String[] STATEMENTS = {
		"select cty_id " +
		"from CTY_customer_type "
		};
	
	public PS_CTY_for_set () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
