package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_STU extends StoredProc {
	private static final String[] PARAMS = {"@stu_id", "@cus_id", "@stu_start_date", "@stu_end_date", 
		"@sch_id", "@stu_notes"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.DATE, Types.DATE,
		Types.NUMERIC, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"update STU_student " +
		"set cus_id = @cus_id, " +
		"	stu_start_date = @stu_start_date, " +
		"	stu_end_date = @stu_end_date, " +
		"	sch_id = @sch_id, " +
		"	stu_notes = @stu_notes " +
		"where stu_id = @stu_id"
	};
	
	public PU_STU () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
