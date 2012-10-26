package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_EMP extends StoredProc{
	private static final String[] PARAMS = {"@emp_id", "@emp_name_first", "@emp_name_last", "@emp_payment_monthly",
		"@emp_tax_code", "@emp_employment_start", "@emp_is_active", "@com_id", "@cdt_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR, Types.DOUBLE,
		Types.VARCHAR, Types.DATE, Types.BIT, Types.NUMERIC, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"update EMP_employee " +
		"set emp_name_first = @emp_name_first, " +
		"	emp_name_last = @emp_name_last, " +
		"	emp_payment_monthly = @emp_payment_monthly, " +
		"	emp_tax_code = @emp_tax_code, " +
		"	emp_employment_start = @emp_employment_start, " +
		"	emp_is_active = @emp_is_active, " +
		"	com_id = @com_id, " +
		"	cdt_id = @cdt_id " +
		"where emp_id = @emp_id"
	};
	
	public PU_EMP () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
