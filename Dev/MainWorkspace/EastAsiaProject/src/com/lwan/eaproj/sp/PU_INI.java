package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_INI extends StoredProc{
	private static final String[] PARAMS = {"@ini_id", "@prd_id", "@inv_id", "@ini_notes", "@ini_price"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.DOUBLE};
	private static final String[] STATEMENTS = {
		"update INI_invoice_item " +
		"set prd_id = @prd_id, " +
		"	inv_id = @inv_id, " +
		"	ini_notes = @ini_notes, " +
		"	ini_price = @ini_price " +
		"where ini_id = @ini_id"
	};
	
	public PU_INI () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
