package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_SCH extends StoredProc{
	private static final String[] PARAMS = {"@sch_id", "@sch_name", "@cdt_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"update SCH_school " +
		"set sch_id = @sch_id, " +
		"	sch_name = @sch_name, " +
		"	cdt_id = @cdt_id"
	};
	
	public PU_SCH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
