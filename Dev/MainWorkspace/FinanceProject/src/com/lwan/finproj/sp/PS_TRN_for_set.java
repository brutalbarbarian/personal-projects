package com.lwan.finproj.sp;

import com.lwan.jdbc.StoredProc;

public class PS_TRN_for_set extends StoredProc{
	private static final String[] PARAMS = {};
	private static final int[] PARAM_TYPES = {};
	private static final String[] STATEMENTS = {
		"select trn_id " +
		"from TM_TRN_Transactions"
	};
	
	public PS_TRN_for_set () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}

