package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_COM extends StoredProc{
	private static final String[] PARAMS = {"@com_id", "@com_name", "@cdt_id"};	
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"insert into TR_COM_company " +
		"(com_id, com_name, cdt_id) " +
		"values (@com_id, @com_name, @cdt_id)"
	};
	
	public PI_COM () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
