package com.lwan.finproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_TRN extends StoredProc{
	private static final String[] PARAMS = {"@trn_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select trn_id, src_id, trn_amount, trn_notes, trn_date " +
		"from TM_TRN_Transactions " +
		"where trn_id = @trn_id"
	};
	
	public PS_TRN () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
