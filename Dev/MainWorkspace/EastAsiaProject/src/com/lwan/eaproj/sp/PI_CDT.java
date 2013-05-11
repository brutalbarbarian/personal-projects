package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PI_CDT extends StoredProc {
	private static final String[] PARAMS = {"@cdt_id", "@cdt_address_1", "@cdt_address_2", 
		"@cdt_address_3", "@cdt_city", "@cdt_country", "@cdt_postcode", "@cdt_phone", "@cdt_mobile", "@cdt_fax", "@cdt_site"};
	private static final int [] PARAM_TYPES = {Types.NUMERIC, Types.VARCHAR, Types.VARCHAR,
		Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
	private static final String[] STATEMENTS = {
		"insert into TR_CDT_contact_details" +
		"(cdt_id, cdt_address_1, cdt_address_2, cdt_address_3, cdt_city, cdt_country, cdt_postcode, cdt_phone, cdt_mobile, cdt_fax, cdt_site)" +
		"values(@cdt_id, @cdt_address_1, @cdt_address_2, @cdt_address_3, @cdt_city, @cdt_country, @cdt_postcode, @cdt_phone, @cdt_mobile, @cdt_fax, @cdt_site)"};
	
	
	public PI_CDT() {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}

}
