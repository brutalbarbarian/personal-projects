package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PD_PRD extends StoredProc{
	private static final String[] PARAMS = {"@prd_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"delete from PRD_product " +
		"where prd_id = @prd_id"
	};
	
	public PD_PRD () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
