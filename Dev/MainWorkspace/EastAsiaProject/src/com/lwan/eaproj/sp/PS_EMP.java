package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_EMP extends StoredProc{
	private static final String[] PARAMS = {"@emp_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select emp_id, emp_name_first, emp_name_last, " +
		"	emp_is_active, com_id, cdt_id " +
		"from EMP_employee " +
		"where emp_id = @emp_id"
	};
	
	public PS_EMP () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
