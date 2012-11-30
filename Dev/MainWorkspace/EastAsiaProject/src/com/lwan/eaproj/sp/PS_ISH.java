package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_ISH extends StoredProc{
	private static final String[] PARAMS = {"@ish_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select ish_id, inv_id, ish_sent_date, ish_paid_amount " +
		"from ISH_invoice_sent_history " +
		"where ish_id = @ish_id"
	};
	
	public PS_ISH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
