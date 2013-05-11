package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.javafx.app.util.DbUtil;

public class BOContactDetail extends BODbObject{
	private BODbAttribute<Integer> contactDetailID;
	private BODbAttribute<String> address1, address2, address3,
			city, country, postCode, phone, mobile, fax,
			site;
	
	public BODbAttribute<Integer> contactDetailID() {
		return contactDetailID;
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
	
	public BOContactDetail(BusinessObject owner, String name) {
		super(owner, name);
	}
	
	@Override
	protected void ensureIDExists() {
		if (contactDetailID().isNull()) {
			contactDetailID().setValue(DbUtil.getNextID("cdt_id"));
		}
	}
	
	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getStoredProc("PS_CDT"), BOContactDetail.class, SP_SELECT);
		setSP(DbUtil.getStoredProc("PI_CDT"), BOContactDetail.class, SP_INSERT);
		setSP(DbUtil.getStoredProc("PU_CDT"), BOContactDetail.class, SP_UPDATE);
		setSP(DbUtil.getStoredProc("PD_CDT"), BOContactDetail.class, SP_DELETE);
	}
	
	@Override
	protected void createAttributes() {
		contactDetailID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailID", "cdt_id", AttributeType.ID, false, false));
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
}
