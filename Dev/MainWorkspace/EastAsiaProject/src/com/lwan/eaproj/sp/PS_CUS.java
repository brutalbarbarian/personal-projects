package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_CUS extends StoredProc {
	private static final String[] PARAMS = {"@cus_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select cus_id, cus_name_first, cus_name_last, cdt_id, cus_notes, " +
		"		cus_date_created, cus_is_active, cus_is_student " +
		"from TM_CUS_customer " +
		"where cus_id = @cus_id"
	};
	
	public PS_CUS () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}