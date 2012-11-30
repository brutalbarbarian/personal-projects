package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_ISH extends StoredProc{
	private static final String[] PARAMS = {"@ish_id", "@inv_id", "@ish_sent_date", "@ish_paid_amount"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.DATE, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"insert into ISH_invoice_sent_history " +
		"(ish_id, inv_id, ish_sent_date, ish_paid_amount) " +
		"values (@ish_id, @inv_id, @ish_sent_date, @ish_paid_amount)"
	};
	
	public PI_ISH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
