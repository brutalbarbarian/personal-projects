package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_COM extends StoredProc{
	private static final String[] PARAMS = {"@com_id", "@com_name"};	
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"update TR_COM_company " +
		"set com_name = @com_name " +
		"where com_id = @com_id"
	};
	
	public PU_COM () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
