package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_CDT;
import com.lwan.eaproj.sp.PI_CDT;
import com.lwan.eaproj.sp.PS_CDT;
import com.lwan.eaproj.sp.PU_CDT;
import com.lwan.eaproj.util.DbUtil;

public class BOContactDetails extends BODbObject {
	public BODbAttribute<Integer> contactDetailsID;
	public BODbAttribute<String> address1, address2, address3, city, country,
			postCode, phone, mobile, fax, site;
	
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
		contactDetailsID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailsID", "cdt_id", false, 0, 0));
		address1 = addAsChild(new BODbAttribute<String>(this, "Address1", "cdt_address_1"));
		address2 = addAsChild(new BODbAttribute<String>(this, "Address2", "cdt_address_2"));
		address3 = addAsChild(new BODbAttribute<String>(this, "Address3", "cdt_address_3"));
		city = addAsChild(new BODbAttribute<String>(this, "City", "cdt_city"));
		country = addAsChild(new BODbAttribute<String>(this, "Country", "cdt_country"));
		postCode = addAsChild(new BODbAttribute<String>(this, "PostCode", "cdt_postcode"));
		phone = addAsChild(new BODbAttribute<String>(this, "Phone", "cdt_phone"));
		mobile = addAsChild(new BODbAttribute<String>(this, "Mobile", "cdt_mobile"));
		fax = addAsChild(new BODbAttribute<String>(this, "Fax", "cdt_fax"));
		site = addAsChild(new BODbAttribute<String>(this, "Site", "cdt_site"));
	}

	@Override
	public void clearAttributes() {
		address1.clear();
		address2.clear();
		address3.clear();
		city.clear();
		country.clear();
		postCode.clear();
		phone.clear();
		mobile.clear();
		fax.clear();
		site.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}

	@Override
	protected void ensureIDExists() {
		if (contactDetailsID.isNull() || contactDetailsID.getValue() == 0) {
			contactDetailsID.setValue(DbUtil.getNextID("cdt_id"));
		}
	}

}
