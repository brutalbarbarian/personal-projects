package com.lwan.eaproj.bo;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_COM;
import com.lwan.eaproj.sp.PI_COM;
import com.lwan.eaproj.sp.PS_COM;
import com.lwan.eaproj.sp.PU_COM;
import com.lwan.javafx.app.util.DbUtil;

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
		setSP(new PS_COM(), BOCompany.class, SP_SELECT);
		setSP(new PI_COM(), BOCompany.class, SP_INSERT);
		setSP(new PU_COM(), BOCompany.class, SP_UPDATE);
		setSP(new PD_COM(), BOCompany.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		companyID = addAsChild(new BODbAttribute<Integer>(this, "CompanyID", "com_id", AttributeType.Integer, false, false));
		contactDetailsID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailsID", "cdt_id", AttributeType.Integer, false, false));
		companyName = addAsChild(new BODbAttribute<String>(this, "CompanyName", "com_name", AttributeType.String));
		
		contactDetails = addAsChild(new BOContactDetails(this));
		contactDetails.independentProperty().setValue(true);
		
		employees = addAsChild(new BOEmployeeSet(this));
	}
	
	protected boolean populateAttributes() {
		boolean result = super.populateAttributes();
		if (result) {
			contactDetails.contactDetailsID().assign(contactDetailsID);
		} else {
			contactDetails.contactDetailsID().clear();
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
		contactDetailsID.assign(contactDetails.contactDetailsID());
	}

}
