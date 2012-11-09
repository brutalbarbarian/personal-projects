package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_STU_for_customer extends StoredProc{
	private static final String[] PARAMS = {"@cus_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select stu_id " +
		"from STU_student " +
		"where cus_id = @cus_id"
	};
	
	public PS_STU_for_customer () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
