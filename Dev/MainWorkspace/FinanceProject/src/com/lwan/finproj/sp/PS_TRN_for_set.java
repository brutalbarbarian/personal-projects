package com.lwan.finproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_TRN_for_set extends StoredProc{
	private static final String[] PARAMS = {
		"@src_id", "@date_start", "@date_end",
		"@trn_amount_min", "@trn_amount_max"
	};
	private static final int[] PARAM_TYPES = {
		Types.NUMERIC, Types.DATE, Types.DATE,
		Types.NUMERIC, Types.NUMERIC
	};
	private static final String[] STATEMENTS = {
		"select	trn_id " +
		"  from	TM_TRN_Transactions " +
		" where	(@src_id is null or @src_id = src_id) " +
		"   and	(@date_start is null or trn_date >= @date_start)" +
		"   and	(@date_end is null or trn_date <= @date_end) " +
		"   and	(@trn_amount_min is null or trn_amount >= @trn_amount_min) " +
		"   and	(@trn_amount_max is null or trn_amount <= @trn_amount_max)"
	};
	
	public PS_TRN_for_set () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}

