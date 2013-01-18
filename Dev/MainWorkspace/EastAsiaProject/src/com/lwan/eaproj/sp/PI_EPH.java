package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_EPH extends StoredProc {
	private static final String[] PARAMS = {"eph_id", "emp_id", "eph_payment_mode", 
		"eph_payment_amount", "eph_start_date", "eph_end_date", "tax_id", "eph_notes"};
	private static final int[] PARAM_TYPES = {
		Types.NUMERIC, Types.NUMERIC, Types.NUMERIC
	};
	private static final String[] STATEMENTS = {};
	
	public PI_EPH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
