package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_COM extends StoredProc{
	private static final String[] PARAMS = {"@com_id", "@cdt_id", "@com_name"};
	private static final int[] PARAM_TYPES = {Types.INTEGER, Types.INTEGER, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"update COM_company " +
		"set cdt_id = @cdt_id, " +
		"	com_name = @com_name " +
		"where com_id = @com_id"
	};
	
	public PU_COM () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
