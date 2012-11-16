package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_PRD extends StoredProc{
	private static final String[] PARAMS = {"@prd_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select prd_id, prc_id, prd_name, prd_description, prd_default_price " +
		"from PRD_product " +
		"where prd_id = @prd_id"
	};
	
	public PS_PRD () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
