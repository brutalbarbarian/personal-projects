package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_EPP extends StoredProc{
	private static final String[] PARAMS = {"@epp_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select epp_id, epp_paid_amount, epp_notes, epp_date_paid, emp_id " +
		"from EPP_employee_payment " +
		"where epp_id = @epp_id"
	};
	
	public PS_EPP () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
	
}
