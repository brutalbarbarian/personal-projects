package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_TAX extends StoredProc{
	private static final String[] PARAMS = {"@tax_id", "@tax_name", "@tax_percent"};
	private static final int[] PARAM_TYPES = {Types.INTEGER, Types.VARCHAR, Types.INTEGER};
	private static final String[] STATEMENTS = {
		"update TAX_tax " +
		"set tax_name = @tax_name, " +
		"	tax_percent = @tax_percent " +
		"where tax_id = @tax_id"
	};
	
	public PU_TAX () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
