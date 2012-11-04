package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbObject;

public class BOSchool extends BODbObject{

	public BOSchool(BusinessObject owner) {
		super(owner, "School");
	}

	@Override
	protected void ensureIDExists() {
		
	}

	@Override
	protected void createStoredProcs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createAttributes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearAttributes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// TODO Auto-generated method stub
		
	}

}
