package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_EMP_for_company extends StoredProc{
	private static final String[] PARAMS = {"@com_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select emp_id from EMP_employee where com_id = @com_id"
	};
	
	public PS_EMP_for_company() {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
