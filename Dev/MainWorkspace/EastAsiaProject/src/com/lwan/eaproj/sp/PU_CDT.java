package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PU_CDT extends StoredProc {
	private static final String[] PARAMS = {"@cdt_id", "@cdt_address_1", "@cdt_address_2", 
		"@cdt_address_3", "@cdt_city", "@cdt_country", "@cdt_postcode", "@cdt_phone", "@cdt_mobile", "@cdt_fax", "@cdt_site"};
	private static final int [] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR,
		Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"update TR_CDT_contact_details " +
		"set cdt_address_1 = @cdt_address_1, " +
		"	cdt_address_2 = @cdt_address_2, " +
		"	cdt_address_3 = @cdt_address_3, " +
		"	cdt_city = @cdt_city, " +
		"	cdt_country = @cdt_country, " +
		"	cdt_postcode = @cdt_postcode, " +
		"	cdt_phone = @cdt_phone, " +
		"	cdt_mobile = @cdt_mobile, " +
		"	cdt_fax = @cdt_fax, " +
		"	cdt_site = @cdt_site " +
		"where cdt_id = @cdt_id"};
	
	public PU_CDT() {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
