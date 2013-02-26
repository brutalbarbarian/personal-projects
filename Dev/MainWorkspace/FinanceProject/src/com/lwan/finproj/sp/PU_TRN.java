package com.lwan.finproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_TRN extends StoredProc{
	private static final String[] PARAMS = {"@trn_id", "@trn_amount", "@src_id",
			"@trn_notes", "@trn_date"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC,
		Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.DATE};
	private static final String[] STATEMENTS = {
		"update TM_TRN_Transactions " +
		"set trn_amount = @trn_amount, " +
		"	src_id = @src_id, " +
		"	trn_notes = @trn_notes, " +
		"	trn_date = @trn_date, " +
		"where trn_id = @trn_id"
	};

	public PU_TRN () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}