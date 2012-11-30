package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_INI extends StoredProc{
	private static final String[] PARAMS = {"@ini_id", "@prd_id", "@inv_id", "@ini_notes", "@ini_price"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.DOUBLE};
	private static final String[] STATEMENTS = {
		"insert into INI_invoice_item " +
		"(ini_id, prd_id, inv_id, ini_notes, ini_price) " +
		"values (@ini_id, @prd_id, @inv_id, @ini_notes, @ini_price)"
	};
	
	public PI_INI () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
