package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_USR extends StoredProc{
	private static final String[] PARAMS = {"@usr_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from TM_USR_user " +
		"where usr_id = @usr_id"
	};
	
	public PD_USR () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
