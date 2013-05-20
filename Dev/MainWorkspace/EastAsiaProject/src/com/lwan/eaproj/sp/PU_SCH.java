package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_SCH extends StoredProc{
	private static final String[] PARAMS = {"@sch_id", "@sch_name", "@cdt_id",
		"@sch_contact_name", "@sch_notes"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.NUMERIC,
		Types.VARCHAR, Types.VARCHAR};	
	private static final String[] STATEMENTS = {
		"update TR_SCH_school " +
		"set sch_name = @sch_name, " +
		"	cdt_id = @cdt_id, " +
		"	sch_contact_name = @sch_contact_name, " +
		"	sch_notes = @sch_notes " +
		"where sch_id = @sch_id"
	};
	
	public PU_SCH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}