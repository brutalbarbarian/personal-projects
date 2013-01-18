package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_INI_for_invoice extends StoredProc{
	private static final String[] PARAMS = {"@inv_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select ini_id " +
		"from INI_invoice_item " +
		"where inv_id = @inv_id"
	};
	
	public PS_INI_for_invoice () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
