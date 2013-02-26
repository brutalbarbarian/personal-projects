package com.lwan.finproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_SRC extends StoredProc{
	private static final String[] PARAMS = {"@src_id", "@src_name"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"update TM_SRC_source " +
		"set src_name = @src_name " +
		"where src_id = @src_id"
	};
	
	public PU_SRC () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
