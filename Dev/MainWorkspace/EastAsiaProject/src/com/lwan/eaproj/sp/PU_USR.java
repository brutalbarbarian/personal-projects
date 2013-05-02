package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_USR extends StoredProc{
	private static final String[] PARAMS = {"@usr_id", "@usr_name", "@usr_password", 
		"@usr_description", "@usr_timestamp"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR,
		Types.VARCHAR, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"update TM_USR_user " +
		"set usr_name = @usr_name, " +
		"	usr_password = @usr_password, " +
		"	usr_description = @usr_description, " +
		"	usr_timestamp - @usr_timestamp " +
		"where usr_id = @usr_id"
	};
	
	public PU_USR () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}