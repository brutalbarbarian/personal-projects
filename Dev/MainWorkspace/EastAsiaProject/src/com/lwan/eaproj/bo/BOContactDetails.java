package com.lwan.eaproj.bo;

import com.lwan.bo.AttributeType;
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
	private BODbAttribute<Integer> contactDetailsID;
	private BODbAttribute<String> address1, address2, address3, city, country,
			postCode, phone, mobile, fax, site;
	public BODbAttribute<Integer> contactDetailsID() {
		return contactDetailsID;
	}
	public BODbAttribute<String> address1() {
		return address1;
	}
	public BODbAttribute<String> address2() {
		return address2;
	}
	public BODbAttribute<String> address3() {
		return address3;
	}
	public BODbAttribute<String> city() {
		return city;
	}
	public BODbAttribute<String> country() {
		return country;
	}
	public BODbAttribute<String> postCode() {
		return postCode;
	}
	public BODbAttribute<String> phone() {
		return phone;
	}
	public BODbAttribute<String> mobile() {
		return mobile;
	}
	public BODbAttribute<String> fax() {
		return fax;
	}
	public BODbAttribute<String> site() {
		return site;
	}
	
	public BOContactDetails(BusinessObject owner) {
		super(owner, "ContactDetails");
	}
	
	protected void createStoredProcs() {
		setSP(new PS_CDT(), BOContactDetails.class, SP_SELECT);
		setSP(new PI_CDT(), BOContactDetails.class, SP_INSERT);
		setSP(new PU_CDT(), BOContactDetails.class, SP_UPDATE);
		setSP(new PD_CDT(), BOContactDetails.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		contactDetailsID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailsID", "cdt_id", AttributeType.Integer, false, false));
		address1 = addAsChild(new BODbAttribute<String>(this, "Address1", "cdt_address_1", AttributeType.String));
		address2 = addAsChild(new BODbAttribute<String>(this, "Address2", "cdt_address_2", AttributeType.String));
		address3 = addAsChild(new BODbAttribute<String>(this, "Address3", "cdt_address_3", AttributeType.String));
		city = addAsChild(new BODbAttribute<String>(this, "City", "cdt_city", AttributeType.String));
		country = addAsChild(new BODbAttribute<String>(this, "Country", "cdt_country", AttributeType.String));
		postCode = addAsChild(new BODbAttribute<String>(this, "PostCode", "cdt_postcode", AttributeType.String));
		phone = addAsChild(new BODbAttribute<String>(this, "Phone", "cdt_phone", AttributeType.String));
		mobile = addAsChild(new BODbAttribute<String>(this, "Mobile", "cdt_mobile", AttributeType.String));
		fax = addAsChild(new BODbAttribute<String>(this, "Fax", "cdt_fax", AttributeType.String));
		site = addAsChild(new BODbAttribute<String>(this, "Site", "cdt_site", AttributeType.String));
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
