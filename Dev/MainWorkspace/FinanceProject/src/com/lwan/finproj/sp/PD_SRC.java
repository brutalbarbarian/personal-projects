package com.lwan.finproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_SRC extends StoredProc{
	private static final String[] PARAMS = {"@src_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete TM_SRC_source " +
		"where src_id = @src_id"
	};
	
	public PD_SRC () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
