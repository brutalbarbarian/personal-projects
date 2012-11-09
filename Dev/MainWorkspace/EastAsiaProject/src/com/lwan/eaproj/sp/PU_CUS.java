package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_CUS extends StoredProc{
	private static final String[] PARAMS = {"@cus_id", "@cus_name_first", "@cus_name_last", 
		"@cdt_id", "@cus_notes", "@cus_ref", "@cty_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR,
		Types.NUMERIC, Types.VARCHAR, Types.VARCHAR, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"update CUS_customer " +
		"set cus_name_first = @cus_name_first, " +
		"	cus_name_last = @cus_name_last, " +
		"	cdt_id = @cdt_id, " +
		"	cus_notes = @cus_notes, " +
		"	cus_ref = @cus_ref, " +
		"	cty_id = @cty_id " +
		"where cus_id = @cus_id"
	};

	public PU_CUS () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
