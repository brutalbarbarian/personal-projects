package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.javafx.app.util.DbUtil;

public class BOCompany extends BODbObject{
	private BODbAttribute<Integer> companyID, contactDetailID;
	private BODbAttribute<String> companyName;
	private BOContactDetail contactDetail;
	
	public BODbAttribute<Integer> companyID() {
		return companyID;
	}
	public BODbAttribute<Integer> contactDetailID() {
		return contactDetailID;
	}
	public BODbAttribute<String> companyName() {
		return companyName;
	}
	public BOContactDetail contactDetail(){
		return contactDetail;
	}
	
	public BOCompany(BusinessObject owner) {
		super(owner, "Company");
	}

	@Override
	protected void ensureIDExists() {
		if (companyID.isNull()) {
			companyID.setValue(DbUtil.getNextID("com_id"));
		}
		contactDetailID.assign(contactDetail.contactDetailID());
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getStoredProc("PS_COM"), BOCompany.class, SP_SELECT);
		setSP(DbUtil.getStoredProc("PI_COM"), BOCompany.class, SP_INSERT);
		setSP(DbUtil.getStoredProc("PU_COM"), BOCompany.class, SP_UPDATE);
		setSP(DbUtil.getStoredProc("PD_COM"), BOCompany.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		companyID = addAsChild(new BODbAttribute<Integer>(this, "CompanyID", "com_id", AttributeType.ID, false, false));
		contactDetailID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailID", "cdt_id", AttributeType.ID, false, false));
		companyName = addAsChild(new BODbAttribute<String>(this, "CompanyName", "com_name", AttributeType.String));
		
		contactDetail = addAsChild(new BOContactDetail(this, "ContactDetail"));
		contactDetail.setIndependent(true);
	}
	
	protected boolean populateAttributes() {
		boolean result = super.populateAttributes();
		if (result) {
			contactDetail.contactDetailID().assign(contactDetailID);
		} else {
			contactDetail.contactDetailID().clear();
		}
		return result;
	}

	@Override
	public void clearAttributes() {
		contactDetail.clearAttributes();
		companyName.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}

}
