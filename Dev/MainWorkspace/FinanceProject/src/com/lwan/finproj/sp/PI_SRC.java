package com.lwan.finproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_SRC extends StoredProc{
	private static final String[] PARAMS = {"@src_id", "@src_name"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"insert into TM_SRC_source " +
		"(src_id, src_name) " +
		"values (@src_id, @src_name)"
	};
	
	public PI_SRC () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
