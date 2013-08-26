package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.bo.common.ContactDetailOwner;
import com.lwan.javafx.app.util.DbUtil;

public class BOCompany extends BODbObject implements ContactDetailOwner{
	private BODbAttribute<Integer> companyID;
	private BODbAttribute<String> companyName;
	private BOContactDetail contactDetail;
	
	public BODbAttribute<Integer> companyID() {
		return companyID;
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
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getDbStoredProc("PS_COM"), SP_SELECT);
		setSP(DbUtil.getDbStoredProc("PI_COM"), SP_INSERT);
		setSP(DbUtil.getDbStoredProc("PU_COM"), SP_UPDATE);
		setSP(DbUtil.getDbStoredProc("PD_COM"), SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		companyID = addAsChild(new BODbAttribute<Integer>(this, "CompanyID", "com_id", AttributeType.ID, false, false));
		companyName = addAsChild(new BODbAttribute<String>(this, "CompanyName", "com_name", AttributeType.String));
		
		contactDetail = addAsChild(new BOContactDetail(this, "ContactDetail"));
	}

	@Override
	public void clearAttributes() {
		contactDetail.clearAttributes();
		companyName.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}
	@Override
	public int getSourceType(BOContactDetail cdt) {
		return EAConstants.CDT_SOURCE_TYPE_COMPANY;
	}
	@Override
	public BOAttribute<Integer> getID() {
		return companyID;
	}

}
