package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_EPH extends StoredProc {
	private static final String[] PARAMS = {"@eph_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select eph_id, emp_id, eph_payment_amount, eph_payment_mode, eph_start_date, eph_end_date, " +
		"		tax_id, eph_notes " +
		"from EPH_employee_history " +
		"where eph_id = @eph_id"
	};
	
	public PS_EPH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
