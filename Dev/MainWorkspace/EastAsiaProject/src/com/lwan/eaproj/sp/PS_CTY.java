package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_CTY extends StoredProc{
	private static final String[] PARAMS = {"@cty_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select cty_id, cty_name " +
		"from CTY_customer_type " +
		"where cty_id = @cty_id"
		};
	
	public PS_CTY () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
