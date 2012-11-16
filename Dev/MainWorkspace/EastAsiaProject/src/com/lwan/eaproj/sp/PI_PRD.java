package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_PRD extends StoredProc{
	private static final String[] PARAMS = {"@prd_id", "@prc_id", "@prd_name", 
		"@prd_description", "@prd_default_price"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC, Types.NUMERIC, Types.VARCHAR,
		Types.VARCHAR, Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"insert into PRD_product " +
		"(prd_id, prc_id, prd_name, prd_description, prd_default_price) " +
		"values (@prd_id, @prc_id, @prd_name, @prd_description, @prd_default_price)"
	};
	
	public PI_PRD () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
