package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_PRD extends StoredProc{
	private static final String[] PARAMS = {"@prd_id", "@prc_id", "@prd_name", 
		"@prd_description", "@prd_default_price"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
		Types.VARCHAR, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"update PRD_product " +
		"set prd_id = @prd_id, " +
		"	prc_id = @prc_id, " +
		"	prd_name = @prd_name, " +
		"	prd_description = @prd_description, " +
		"	prd_default_price = @prd_default_price"
	};
	
	public PU_PRD () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
