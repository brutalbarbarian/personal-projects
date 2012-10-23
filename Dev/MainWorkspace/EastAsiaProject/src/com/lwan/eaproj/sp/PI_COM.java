package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_COM extends StoredProc{
	private static String[] PARAMS = {"@com_id", "@cdt_id", "@com_name"};
	private static int [] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR};
	private static String[] STATEMENTS = {
		"insert into COM_company " +
		"(com_id, cdt_id, com_name) " +
		"values (@com_id, @cdt_id, @com_name)"
	};
	
	public PI_COM() {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
