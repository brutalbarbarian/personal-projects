package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_EPP extends StoredProc{
	private static final String[] PARAMS = {"@epp_id", "@emp_id", "@epp_notes", 
		"@epp_date_paid", "@epp_paid_amount"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
		Types.DATE, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"insert into EPP_employee_payment " +
		"(epp_id, emp_id, epp_notes, epp_date_paid, epp_paid_amount) " +
		"values (@epp_id, @emp_id, @epp_notes, @epp_date_paid, @epp_paid_amount)"
	};
	
	public PI_EPP () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
