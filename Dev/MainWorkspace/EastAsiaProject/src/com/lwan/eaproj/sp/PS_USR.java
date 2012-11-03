package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_USR extends StoredProc{
	private static final String[] PARAMS = {"@usr_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select usr_id, usr_name, usr_password, usr_description, usr_timestamp " +
		"from USR_user " +
		"where usr_id = @usr_id"
	};
	
	public PS_USR () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}