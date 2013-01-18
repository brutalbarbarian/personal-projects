package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_TAX extends StoredProc{
	private static final String[] PARAMS = {"@tax_id"};
	private static final int[] PARAM_TYPES = {Types.INTEGER};
	private static final String[] STATEMENTS = {
		"select tax_id, tax_name, tax_percent " +
		"from TAX_tax " +
		"where tax_id = @tax_id"};
	
	public PS_TAX () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}	
}
