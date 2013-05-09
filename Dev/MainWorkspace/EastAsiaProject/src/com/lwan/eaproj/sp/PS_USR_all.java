package com.lwan.eaproj.sp;

import com.lwan.jdbc.StoredProc;

public class PS_USR_all extends StoredProc {
	private static final String[] PARAMS = {};
	private static final int[] PARAM_TYPES = {};
	private static final String[] STATEMENTS = {
		"select usr_id, usr_name, usr_password, usr_description, usr_timestamp " +
		"from TM_USR_user "
	};
	
	public PS_USR_all () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
