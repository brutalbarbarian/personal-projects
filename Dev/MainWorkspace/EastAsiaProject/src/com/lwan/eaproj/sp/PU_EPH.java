package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_EPH extends StoredProc{
	private static final String[] PARAMS = {"@eph_id", "@emp_id", "@eph_notes", 
		"@eph_date_paid", "@eph_paid_amount"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
		Types.DATE, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"update EPH_employee_payment " +
		"set emp_id = @emp_id, " +
		"	eph_notes = @eph_notes, " +
		"	eph_date_paid = @eph_date_paid, " +
		"	eph_paid_amount = @eph_paid_amount " +
		"where eph_id = @eph_id"
	};
	
	public PU_EPH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}