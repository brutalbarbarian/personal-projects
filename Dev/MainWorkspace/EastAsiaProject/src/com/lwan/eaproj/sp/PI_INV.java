package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_INV extends StoredProc{
	private static final String[] PARAMS = {"@inv_id", "@cus_id", "@com_id", "@usr_id_created", 
		"@inv_date_created", "@inv_notes", "@inv_ref", "@inv_date_required", "@inv_is_paid", "@inv_is_invalid"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.NUMERIC, Types.NUMERIC,
		Types.DATE, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.BIT, Types.BIT};
	private static final String[] STATEMENTS = {
		"insert into INV_invoice (inv_id, cus_id, com_id, usr_id_created, inv_date_created, inv_notes, " +
		"	inv_ref, inv_date_required, inv_is_paid, inv_is_invalid) " +
		"values (@inv_id, @cus_id, @com_id, @usr_id_created, @inv_date_created, @inv_notes, " +
		"	@inv_ref, @inv_date_required, @inv_is_paid, @inv_is_invalid)"
	};
	
	public PI_INV () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}