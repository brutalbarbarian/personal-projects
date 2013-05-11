package com.lwan.eaproj.sp;

import com.lwan.jdbc.StoredProc;

public class PS_COM_all extends StoredProc {
	private static final String[] PARAMS = {};
	private static final int[] PARAM_TYPES = {};
	private static final String[] STATEMENTS = {
		"select com_id " +
		"from TR_COM_company"
	};
	
	public PS_COM_all () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
