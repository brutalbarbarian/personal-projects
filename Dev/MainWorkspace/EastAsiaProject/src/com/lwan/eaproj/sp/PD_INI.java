package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_INI extends StoredProc{
	private static final String[] PARAMS = {"@ini_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from INI_invoice_item " +
		"where ini_id = @ini_id"
	};
	
	public PD_INI () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
