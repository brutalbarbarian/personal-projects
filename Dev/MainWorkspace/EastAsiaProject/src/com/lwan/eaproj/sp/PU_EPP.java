package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_EPP extends StoredProc{
	private static final String[] PARAMS = {"@epp_id", "@emp_id", "@epp_notes", 
		"@epp_date_paid", "@epp_paid_amount"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
		Types.DATE, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"update epp_employee_payment " +
		"set emp_id = @emp_id, " +
		"	epp_notes = @epp_notes, " +
		"	epp_date_paid = @epp_date_paid, " +
		"	epp_paid_amount = @epp_paid_amount " +
		"where epp_id = @epp_id"
	};
	
	public PU_EPP () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}