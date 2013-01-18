package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_EPP_for_employee extends StoredProc{
	private static final String[] PARAMS = {"@emp_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select epp_id " +
		"from EPP_employee_payment " +
		"where emp_id = @emp_id"
	};
	
	public PS_EPP_for_employee () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
