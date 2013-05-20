package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_SCH extends StoredProc{
	private static final String[] PARAMS = {"@sch_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};	
	private static final String[] STATEMENTS = {
		"delete from TR_SCH_school " +
		"where sch_id = @sch_id"
	};
	
	public PD_SCH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
