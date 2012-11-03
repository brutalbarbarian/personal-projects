package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_EPH extends StoredProc{
	private static final String[] PARAMS = {"@eph_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from EPH_employee_payment " +
		"where eph_id = @eph_id"
	};
	
	public PD_EPH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
	
}
