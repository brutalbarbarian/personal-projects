package com.lwan.eaproj.sp;

import java.sql.Types;

import com.lwan.jdbc.StoredProc;

public class PS_CUS_find extends StoredProc{
	private static final String[] PARAMS = {"@cus_name", "@cus_address", "@cus_number", "@student", "@allow_inactive"};
	private static final int[] PARAM_TYPES = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BOOLEAN, Types.BOOLEAN};
	private static final String[] STATEMENTS = {
		"select cus_id, " + 
		"		(cus_name_last + ',' + cus_name_first) as cus_name, " + 
		"		(cdt_address_1 + IIf(cdt_address_2 = '', '', IIf(cdt_address_1 = '', cdt_address_2 , ', ' + cdt_address_2 )) + " + 
		"		IIf(cdt_address_3 = '', '', IIf(cdt_address_1 = '' and cdt_address_2 = '', cdt_address_3, ', ' + cdt_address_3))) AS cdt_address," + 
		"		cdt_phone, cdt_mobile, cus_is_active, cus_is_student" + 
		"  from TM_CUS_customer cus " + 
		" inner join TR_CDT_contact_details cdt " + 
		"    on	cus.cdt_id = cdt.cdt_id " + 
		" where (@cus_name is null or (cus.cus_name_first + ', ' + cus.cus_name_last) like ('*' + @cus_name + '*'))" + 
		"   and	(@cus_address is null or (cdt_address_1 + ', ' + cdt_address_2 + ', ' + cdt_address_3 + ', ' + cdt_city +" + 
		"		', ' + cdt_country + ', ' + cdt_postcode) like ('*' + @cus_address +'*'))" + 
		"   and (@cus_number is null or @cus_number like cdt_phone or @cus_number like cdt_mobile)" + 
		"   and (@student is null or @student = cus_is_student)" + 
		"   and (@allow_inactive or cus_is_active)"
	};
	
	public PS_CUS_find () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
