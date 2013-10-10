package com.lwan.eaproj.bo.ref;

import java.util.function.Consumer;

import com.lwan.bo.BusinessObject;

public class BOContactSet extends BODocumentItemSet<BOContact> {

	public BOContactSet(BusinessObject owner, String name, String childIdName,
			String childIdFieldName) {
		super(owner, name, childIdName, childIdFieldName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void forEach(Consumer<? super BOContact> action) {
		// TODO Auto-generated method stub
		
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
