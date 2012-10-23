package com.lwan.eaproj.bo;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOBusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODatabaseObject;
import com.lwan.eaproj.sp.PI_COM;
import com.lwan.eaproj.sp.PS_COM;
import com.lwan.eaproj.util.DbUtil;

public class BOCompany extends BODatabaseObject {
	public BOAttribute<Integer> com_id, cdt_id;
	public BOAttribute<String> com_name;
	public BOContactDetails contactDetails;
	
	
	public BOCompany(BOBusinessObject owner) {
		super(owner, "Company");
		
		SelectStoredProc().setValue(new PS_COM());
		InsertStoredProc().setValue(new PI_COM());
		// TODO
		
		Independent().setValue(true);
	}

	@Override
	protected void createAttributes() {
		com_id = addAsChild(new BOAttribute<Integer>(this, "com_id", false, null, 0));
		cdt_id = addAsChild(new BOAttribute<Integer>(this, "cdt_id", false, null, 0));
		com_name = addAsChild(new BOAttribute<String>(this, "com_name"));
		
		contactDetails = addAsChild(new BOContactDetails(this));
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
		com_id.clear();
		cdt_id.clear();
		contactDetails.clear();	// ensures the child is cleared too.
		com_name.setAsObject("");	// Empty string for name
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
