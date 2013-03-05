package com.lwan.finproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_TRN extends StoredProc{
	private static final String[] PARAMS = {"@trn_id", "@trn_amount", "@src_id",
			"@trn_notes", "@trn_date"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, 
			Types.VARCHAR, Types.DATE};
	private static final String[] STATEMENTS = {
		"insert into TM_TRN_Transactions " +
		"(trn_id, trn_amount, src_id, trn_notes, trn_date) " +
		"values (@trn_id, @trn_amount, @src_id, @trn_notes, @trn_date)"
	};
	
	public PI_TRN () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
