package com.lwan.eaproj.bo;

import com.lwan.bo.BOBusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODatabaseObject;

public class BOCompany extends BODatabaseObject {

	public BOCompany(BOBusinessObject owner) {
		super(owner, "Company");
		
	}

	@Override
	protected void createAttributes() {
		
	}

	@Override
	protected void clearAttributes() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void ensureIDExists() {
		// TODO Auto-generated method stub
		
	}

}
