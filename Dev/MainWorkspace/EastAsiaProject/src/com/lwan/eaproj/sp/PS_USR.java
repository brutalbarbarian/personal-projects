package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_USR extends StoredProc{
	private static final String[] PARAMS = {"@usr_id", "@usr_name"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"select usr_id, usr_name, usr_password, usr_description, usr_timestamp " +
		"from TR_USR_user " +
		"where (not ((@usr_id is null) and (@usr_name is null))) and" +
		"	((@usr_id is null) or (usr_id = @usr_id)) and " +
		"	((@usr_name is null) or (usr_name = @usr_name))"
	};
	
	public PS_USR () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}