package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_COM;
import com.lwan.eaproj.sp.PI_COM;
import com.lwan.eaproj.sp.PS_COM;
import com.lwan.eaproj.sp.PU_COM;
import com.lwan.eaproj.util.DbUtil;

public class BOCompany extends BODbObject {
	public BODbAttribute<Integer> companyID, contactDetailsID;
	public BODbAttribute<String> companyName;
	
	public BOContactDetails contactDetails;
	public BOEmployeeSet employees;
	
	public BOCompany(BusinessObject owner) {
		super(owner, "Company");
	}
	
	@Override
	protected void createStoredProcs() {
		SelectStoredProc().setValue(new PS_COM());
		InsertStoredProc().setValue(new PI_COM());
		UpdateStoredProc().setValue(new PU_COM());
		DeleteStoredProc().setValue(new PD_COM());
	}

	@Override
	protected void createAttributes() {
		companyID = addAsChild(new BODbAttribute<Integer>(this, "CompanyID", "com_id", false, null, 0));
		contactDetailsID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailsID", "cdt_id", false, null, 0));
		companyName = addAsChild(new BODbAttribute<String>(this, "CompanyName", "com_name"));
		
		contactDetails = addAsChild(new BOContactDetails(this));
		contactDetails.Independent().setValue(true);
		
		employees = addAsChild(new BOEmployeeSet(this));
	}
	
	protected boolean populateAttributes() {
		boolean result = super.populateAttributes();
		if (result) {
			contactDetails.contactDetailsID.assign(contactDetailsID);
		} else {
			contactDetails.contactDetailsID.clear();
		}
		return result;
	}

	@Override
	public void clearAttributes() {
		companyName.clear();
		contactDetails.clearAttributes();
	}

	@Override
	public void handleModified(ModifiedEvent event) {}

	@Override
	protected void ensureIDExists() {
		if (companyID.asInteger() == 0) {
			companyID.setValue(DbUtil.getNextID("com_id"));
		}
		contactDetailsID.assign(contactDetails.contactDetailsID);
	}

}
