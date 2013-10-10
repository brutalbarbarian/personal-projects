package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;

public class BOContactSet extends BODocumentItemSet<BOContact> {

	public BOContactSet(BusinessObject owner, String name) {
		super(owner, name);
	}

	@Override
	protected void createStoredProcs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected BOContact createChildInstance(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

}
