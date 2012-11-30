package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_ISH extends StoredProc{
	private static final String[] PARAMS = {"@ish_id", "@inv_id", "@ish_sent_date", "@ish_paid_amount"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.DATE, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"update ISH_invoice_sent_history " +
		"set inv_id = @inv_id, " +
		"	ish_sent_history = @ish_sent_date, " +
		"	ish_paid_amount = @ish_paid_amount " +
		"where ish_id = @ish_id"
	};
	
	public PU_ISH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
