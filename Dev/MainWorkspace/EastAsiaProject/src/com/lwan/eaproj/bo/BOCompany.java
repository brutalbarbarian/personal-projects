package com.lwan.eaproj.bo;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODatabaseObject;
import com.lwan.eaproj.sp.PD_COM;
import com.lwan.eaproj.sp.PI_COM;
import com.lwan.eaproj.sp.PS_COM;
import com.lwan.eaproj.sp.PU_COM;
import com.lwan.eaproj.util.DbUtil;

public class BOCompany extends BODatabaseObject {
	public BOAttribute<Integer> com_id, cdt_id;
	public BOAttribute<String> com_name;
	
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
		com_id = addAsChild(new BOAttribute<Integer>(this, "com_id", false, null, 0));
		cdt_id = addAsChild(new BOAttribute<Integer>(this, "cdt_id", false, null, 0));
		com_name = addAsChild(new BOAttribute<String>(this, "com_name"));
		
		contactDetails = addAsChild(new BOContactDetails(this));
		contactDetails.Independent().setValue(true);
		
		employees = addAsChild(new BOEmployeeSet(this));
	}
	
	protected boolean populateAttributes() {
		boolean result = super.populateAttributes();
		if (result) {
			contactDetails.cdt_id.assign(cdt_id);
		} else {
			contactDetails.cdt_id.clear();
		}
		return result;
	}

	@Override
	public void clearAttributes() {
		com_name.clear();
		contactDetails.clearAttributes();
	}

	@Override
	public void handleModified(ModifiedEvent event) {}

	@Override
	protected void ensureIDExists() {
		if (com_id.isNull() || com_id.getValue() == 0) {
			com_id.setValue(DbUtil.getNextID("com_id"));
		}
		cdt_id.assign(contactDetails.cdt_id);
	}

}
