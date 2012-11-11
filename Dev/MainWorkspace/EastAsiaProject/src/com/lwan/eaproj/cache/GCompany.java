package com.lwan.eaproj.cache;

import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.bo.BOCompany;

public class GCompany extends BODbSet<BOCompany>{
	private static GCompany cache;
	
	public static GCompany get() {
		if (cache == null) {
			cache = new GCompany();
		}
		return cache;
	}

	private GCompany() {
		super(null, "CompanyCache", "CompanyID", "com_id");
		
		
	}

	@Override
	protected void createStoredProcs() {
//		Exists
	}

	@Override
	protected BOCompany createChildInstance(Object id) {
		return new BOCompany(this);
	}

}
