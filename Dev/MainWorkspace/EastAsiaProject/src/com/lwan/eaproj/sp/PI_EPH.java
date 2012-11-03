package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_EPH extends StoredProc{
	private static final String[] PARAMS = {"@eph_id", "@emp_id", "@eph_notes", 
		"@eph_date_paid", "@eph_paid_amount"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
		Types.DATE, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"insert into EPH_employee_payment " +
		"(eph_id, emp_id, eph_notes, eph_date_paid, eph_paid_amount) " +
		"values (@eph_id, @emp_id, @eph_notes, @eph_date_paid, @eph_paid_amount)"
	};
	
	public PI_EPH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
