package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_STU extends StoredProc {
	private static final String[] PARAMS = {"@stu_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select stu_id, cus_id, stu_start_date, stu_end_date, sch_id, stu_notes " +
		"from STU_student " +
		"where stu_id = @stu_id"
	};
	
	public PS_STU () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
