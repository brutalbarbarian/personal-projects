package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

/**
 * Stored proc for fetching the next id used for the passed
 * in primary key name. 
 * 
 * @author Brutalbarbarian
 *
 */
public class PA_PKC_new_id extends StoredProc{
	private static final String[] PARAMS = {"@pkc_name"};
	private static final int[] PARAM_TYPES = {Types.CHAR};
	private static final String[] STATEMENTS = {
		"update PKC_primary_key_counter " +
		"set pkc_id_next = pkc_id_next + 1 " +
		"where pkc_name = @pkc_name",
		
		"select pkc_id_next " +
		"from PKC_primary_key_counter " +
		"where pkc_name = @pkc_name"
	};
	
	public PA_PKC_new_id() {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}

}
