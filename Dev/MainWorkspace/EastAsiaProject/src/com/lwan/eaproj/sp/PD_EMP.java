package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_EMP extends StoredProc {
	private static final String[] PARAMS = {"@emp_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from EMP_employee " +
		"where emp_id = @emp_id"
	};
	
	public PD_EMP () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
