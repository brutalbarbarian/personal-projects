package com.lwan.finproj.sp;

import com.lwan.jdbc.StoredProc;

public class PS_SRC_for_set extends StoredProc{
	private static final String[] PARAMS = {};
	private static final int[] PARAM_TYPES = {};
	private static final String[] STATEMENTS = {
		"select src_id " +
		"from TM_SRC_source"
	};
	
	public PS_SRC_for_set () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
