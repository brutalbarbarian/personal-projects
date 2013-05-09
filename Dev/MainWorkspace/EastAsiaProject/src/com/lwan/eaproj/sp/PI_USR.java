package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_USR extends StoredProc{
	private static final String[] PARAMS = {"@usr_id", "@usr_name", "@usr_password", 
		"@usr_description", "@usr_timestamp"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR,
		Types.VARCHAR, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"insert into TM_USR_user " +
		"(usr_id, usr_name, usr_password, usr_description, usr_timestamp) " +
		"values (@usr_id, @usr_name, @usr_password, @usr_description, @usr_timestamp)"
	};
	
	public PI_USR () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
