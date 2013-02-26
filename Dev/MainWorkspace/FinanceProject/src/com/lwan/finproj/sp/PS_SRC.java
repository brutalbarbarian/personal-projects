package com.lwan.finproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_SRC extends StoredProc{
	private static final String[] PARAMS = {"@src_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select src_id, src_name " +
		"from TM_SRC_source " +
		"where src_id = @src_id"
	};
	
	public PS_SRC () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
