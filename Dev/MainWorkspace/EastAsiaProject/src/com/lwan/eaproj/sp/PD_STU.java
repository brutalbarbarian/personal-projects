package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_STU extends StoredProc {
	private static final String[] PARAMS = {"@stu_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from STU_student " +
		"where stu_id = @stu_id"
	};
	
	public PD_STU () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}

