package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_INI extends StoredProc{
	private static final String[] PARAMS = {"@ini_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select ini_id, prd_id, inv_id, ini_notes, ini_price " +
		"from INI_invoice_item " +
		"where ini_id = @ini_id"
	};
	
	public PS_INI () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
