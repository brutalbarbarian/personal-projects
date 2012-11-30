package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_ISH extends StoredProc{
	private static final String[] PARAMS = {"@ish_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from ISH_invoice_sent_history " +
		"where ish_id = @ish_id"
	};
	
	public PD_ISH () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
