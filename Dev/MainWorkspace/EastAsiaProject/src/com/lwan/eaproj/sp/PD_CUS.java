package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_CUS extends StoredProc{
	private static final String[] PARAMS = {"@cus_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from CUS_customer " +
		"where cus_id = @cus_id"
	};
	
	public PD_CUS () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
