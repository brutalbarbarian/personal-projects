package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_CDT extends StoredProc {
	private static final String[] PARAMS = {"@cdt_id"};
	private static final int[] PARAM_TYPES = {Types.NUMERIC};
	private static final String[] STATEMENTS = {
		"select cdt_id, cdt_address_1, cdt_address_2, cdt_address_3, cdt_city, cdt_country, cdt_postcode, cdt_phone, cdt_mobile, cdt_fax, cdt_site " +
		"from TR_CDT_contact_details " +
		"where @cdt_id = cdt_id"};

	public PS_CDT() {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
