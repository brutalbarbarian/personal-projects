package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_INV extends StoredProc{
	private static final String[] PARAMS = {"inv_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from INV_invoice " +
		"where inv_id = @inv_id"
	};
	
	public PD_INV () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}