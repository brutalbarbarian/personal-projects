package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_COM extends StoredProc{
	private static final String[] PARAMS = {"@com_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from TR_COM_company " +
		"where com_id = @com_id"
	};
	
	public PD_COM () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}