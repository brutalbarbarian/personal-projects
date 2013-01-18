package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_TAX extends StoredProc {
	private static final String[] PARAMS = {"@tax_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from TAX_tax " +
		"where tax_id = @tax_id"
	};
	
	public PD_TAX () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
