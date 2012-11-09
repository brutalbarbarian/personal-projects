package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_SCH extends StoredProc{
	private static final String[] PARAMS = {"@sch_id", "@sch_name", "@cdt_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"insert into SCH_school " +
		"(sch_id, sch_name, cdt_id) " +
		"values (@sch_id, @sch_name, @cdt_id)"
	};
	
	public PI_SCH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
