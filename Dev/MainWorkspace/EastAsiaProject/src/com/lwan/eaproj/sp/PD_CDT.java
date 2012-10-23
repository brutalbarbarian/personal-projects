package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_CDT extends StoredProc {
	private static final String[] PARAMS = {"@cdt_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from CDT_contact_details " +
		"where @cdt_id = cdt_id"};

	public PD_CDT() {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
