package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_CUS extends StoredProc{
	private static final String[] PARAMS = {"@cus_id", "@cus_name_first", "@cus_name_last", 
			"@cdt_id", "@cus_notes", "@cus_ref", "@cty_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR,
			Types.NUMERIC, Types.VARCHAR, Types.VARCHAR, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"insert into CUS_customer " +
		"(cus_id, cus_name_first, cus_name_last, cdt_id, cus_notes, cus_ref, cty_id) " +
		"values " +
		"(@cus_id, @cus_name_first, @cus_name_last, @cdt_id, @cus_notes, @cus_ref, @cty_id)"
	};
	
	public PI_CUS () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
