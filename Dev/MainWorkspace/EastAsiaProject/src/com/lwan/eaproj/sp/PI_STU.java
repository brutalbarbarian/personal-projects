package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_STU extends StoredProc {
	private static final String[] PARAMS = {"@stu_id", "@cus_id", "@stu_start_date", "@stu_end_date", 
		"@sch_id", "@stu_notes"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.DATE, Types.DATE,
		Types.NUMERIC, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"insert into STU_student " +
		"(stu_id, cus_id, stu_start_date, stu_end_date, sch_id, stu_notes) " +
		"values (@stu_id, @cus_id, @stu_start_date, @stu_end_date, @sch_id, @stu_notes)"
	};
	
	public PI_STU () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
