package com.lwan.eaproj.bo;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODatabaseObject;
import com.lwan.eaproj.sp.PD_CDT;
import com.lwan.eaproj.sp.PI_CDT;
import com.lwan.eaproj.sp.PS_CDT;
import com.lwan.eaproj.sp.PU_CDT;
import com.lwan.eaproj.util.DbUtil;

public class BOContactDetails extends BODatabaseObject {
	public BOAttribute<Integer> cdt_id;
	public BOAttribute<String> cdt_address_1, cdt_address_2, cdt_address_3, cdt_city, cdt_country,
			cdt_postcode, cdt_phone, cdt_mobile, cdt_fax, cdt_site;
	
	public BOContactDetails(BusinessObject owner) {
		super(owner, "ContactDetails");
	}
	
	protected void createStoredProcs() {
		SelectStoredProc().setValue(new PS_CDT());
		InsertStoredProc().setValue(new PI_CDT());
		UpdateStoredProc().setValue(new PU_CDT());
		DeleteStoredProc().setValue(new PD_CDT());
	}

	@Override
	protected void createAttributes() {
		cdt_id = addAsChild(new BOAttribute<Integer>(this, "cdt_id", false, 0, 0));
		cdt_address_1 = addAsChild(new BOAttribute<String>(this, "cdt_address_1"));
		cdt_address_2 = addAsChild(new BOAttribute<String>(this, "cdt_address_2"));
		cdt_address_3 = addAsChild(new BOAttribute<String>(this, "cdt_address_3"));
		cdt_city = addAsChild(new BOAttribute<String>(this, "cdt_city"));
		cdt_country = addAsChild(new BOAttribute<String>(this, "cdt_country"));
		cdt_postcode = addAsChild(new BOAttribute<String>(this, "cdt_postcode"));
		cdt_phone = addAsChild(new BOAttribute<String>(this, "cdt_phone"));
		cdt_mobile = addAsChild(new BOAttribute<String>(this, "cdt_mobile"));
		cdt_fax = addAsChild(new BOAttribute<String>(this, "cdt_fax"));
		cdt_site = addAsChild(new BOAttribute<String>(this, "cdt_site"));
	}

	@Override
	public void clearAttributes() {
		cdt_address_1.clear();
		cdt_address_2.clear();
		cdt_address_3.clear();
		cdt_city.clear();
		cdt_country.clear();
		cdt_postcode.clear();
		cdt_phone.clear();
		cdt_mobile.clear();
		cdt_fax.clear();
		cdt_site.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}

	@Override
	protected void ensureIDExists() {
		if (cdt_id.isNull() || cdt_id.getValue() == 0) {
			cdt_id.setValue(DbUtil.getNextID("cdt_id"));
		}
	}

}