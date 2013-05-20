package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_SCH extends StoredProc{
	private static final String[] PARAMS = {"@sch_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};	
	private static final String[] STATEMENTS = {
		"select sch_id, sch_name, cdt_id, sch_contact_name, sch_notes " +
		"from TR_SCH_school " +
		"where sch_id = @sch_id"
	};
	
	public PS_SCH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
