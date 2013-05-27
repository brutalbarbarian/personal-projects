package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_CUS extends StoredProc{
	private static final String[] PARAMS = {"@cus_id", "@cus_name_first", "@cus_name_last", "@cdt_id", 
		"@cus_date_created", "@cus_notes", "@cus_is_active", "@cus_is_student"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR, Types.NUMERIC,
		Types.DATE, Types.VARCHAR, Types.BOOLEAN, Types.BOOLEAN};
	private static final String[] STATEMENTS = {
		"insert into TM_CUS_customer " +
		"(cus_id, cus_name_first, cus_name_last, cdt_id, cus_date_created, cus_notes, cus_is_active, cus_is_student) " +
		"values (@cus_id, @cus_name_first, @cus_name_last, @cdt_id, @cus_date_created, @cus_notes, @cus_is_active, @cus_is_student)"
	};
	
	public PI_CUS () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}

}
