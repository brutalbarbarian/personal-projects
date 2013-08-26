package com.lwan.javafx.app.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PA_SP_QUERY extends StoredProc{
	private static final String[] PARAMS = {"@sp_name", "@database_name"};
	private static final int[] PARAM_TYPES = {Types.CHAR, Types.CHAR};
	private static final String[] STATEMENTS = {
		"select parameter_name, data_type, ordinal_position, numeric_precision, numeric_scale " + 
		"from information_schema.parameters " + 
		"where SPECIFIC_NAME = @sp_name " +
		"  and SPECIFIC_SCHEMA = @database_name " +
		"order by ordinal_position"
	};
	
	public PA_SP_QUERY() {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
