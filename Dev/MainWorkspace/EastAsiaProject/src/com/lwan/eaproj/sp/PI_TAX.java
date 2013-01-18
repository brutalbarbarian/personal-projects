package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_TAX extends StoredProc{
	private static final String[] PARAMS = {"@tax_id", "@tax_name", "@tax_percent"};
	private static final int[] PARAM_TYPES = {Types.INTEGER, Types.VARCHAR, Types.INTEGER};
	private static final String[] STATEMENTS = {
		"insert into TAX_tax (tax_id, tax_name, tax_percent) " +
		"values (@tax_id, @tax_name, @tax_percent)"
	};
	
	public PI_TAX () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
