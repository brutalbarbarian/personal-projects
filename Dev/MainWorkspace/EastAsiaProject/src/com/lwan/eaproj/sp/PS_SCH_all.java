package com.lwan.eaproj.sp;

import com.lwan.jdbc.StoredProc;

public class PS_SCH_all extends StoredProc{
	private static final String[] PARAMS = {};
	private static final int[] PARAM_TYPES = {};	
	private static final String[] STATEMENTS = {
		"select sch_id " +
		"from TR_SCH_school "
	};
	
	public PS_SCH_all () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
