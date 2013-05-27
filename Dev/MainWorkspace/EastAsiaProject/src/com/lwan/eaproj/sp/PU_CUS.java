package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_CUS extends StoredProc{
	private static final String[] PARAMS = {"cus_id", "@cus_name_first", "@cus_name_last",
			"@cus_notes", "@cus_is_active", "@cus_is_student"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR,
			Types.VARCHAR, Types.BOOLEAN, Types.BOOLEAN};
	private static final String[] STATEMENTS = {
		"update TM_CUS_customer " +
		"set cus_name_first = @cus_name_first, " +
		"	cus_name_last = @cus_name_last, " +
		"	cus_notes = @cus_notes, " +
		"	cus_id_active = @cus_is_active, " +
		"	cus_is_student = @cus_is_student " +
		"where cus_id = @cus_id"
	};
	
	public PU_CUS () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}