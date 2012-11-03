package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_EPH extends StoredProc{
	private static final String[] PARAMS = {"@eph_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select eph_id, eph_paid_amount, eph_notes, eph_date_paid, emp_id " +
		"from EPH_employee_payment " +
		"where eph_id = @eph_id"
	};
	
	public PS_EPH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
	
}
