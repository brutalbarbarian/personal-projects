package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_SCH extends StoredProc{
	private static final String[] PARAMS = {"@sch_id", "@sch_name", "@cdt_id",
		"@sch_contact_name", "@sch_notes"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.NUMERIC,
		Types.VARCHAR, Types.VARCHAR};	
	private static final String[] STATEMENTS = {
		"insert into TR_SCH_school " +
		"(sch_id, sch_name, cdt_id, sch_contact_name, sch_notes) " +
		"values (@sch_id, @sch_name, @cdt_id, @sch_contact_name, @sch_notes)"
	};
	
	public PI_SCH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}