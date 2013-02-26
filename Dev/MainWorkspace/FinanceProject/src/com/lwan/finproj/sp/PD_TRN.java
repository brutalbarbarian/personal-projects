package com.lwan.finproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_TRN extends StoredProc{
	private static final String[] PARAMS = {"@trn_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete TM_TRN_Transactions " +
		"where trn_id = @trn_id"
	};
	
	public PD_TRN () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
