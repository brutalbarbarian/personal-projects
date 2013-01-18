package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_EPP extends StoredProc{
	private static final String[] PARAMS = {"@epp_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from epp_employee_payment " +
		"where epp_id = @epp_id"
	};
	
	public PD_EPP () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
	
}
