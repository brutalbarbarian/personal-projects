package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;

public class BOAddressSet extends BODocumentItemSet<BOAddress>{

	public BOAddressSet(BusinessObject owner, String name, String childIdName,
			String childIdFieldName) {
		super(owner, name, childIdName, childIdFieldName);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void createStoredProcs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected BOAddress createChildInstance(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

}
