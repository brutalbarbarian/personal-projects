package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_INV extends StoredProc{
	private static final String[] PARAMS = {"@inv_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select inv_id, cus_id, com_id, usr_id_created, inv_date_created, inv_notes, " +
		"	inv_ref, inv_date_required, inv_is_paid, inv_is_invalid " +
		"from INV_invoice " +
		"where inv_id = @inv_id"
	};
	
	public PS_INV () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}