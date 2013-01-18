package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_EMP extends StoredProc{
	private static final String[] PARAMS = {"@emp_id", "@emp_name_first", "@emp_name_last", 
		"@emp_is_active", "@com_id", "@cdt_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR, 
		Types.BIT, Types.NUMERIC, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"insert into EMP_employee " +
		"(emp_id, emp_name_first, emp_name_last, emp_is_active, com_id, cdt_id) " +
		"values (@emp_id, @emp_name_first, @emp_name_last, @emp_is_active, @com_id, @cdt_id)"
	};
	
	public PI_EMP () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
