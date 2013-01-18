package com.lwan.eaproj.bo.cache;

import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.bo.BOCompany;
import com.lwan.eaproj.sp.PS_COM;

public class GCompany extends BODbSet<BOCompany>{
	private static GCompany cache;
	
	public static GCompany get() {
		if (cache == null) {
			cache = new GCompany();
		}
		return cache;
	}
	
	public static BOCompany findCompanyByID(int id) {
		return get().findChildByID(id);
	}

	private GCompany() {
		super(null, "CompanyCache", "CompanyID", "com_id");
		
		loadModeProperty().setValue(LOADMODE_CACHE);
	}

	@Override
	protected void createStoredProcs() {
		existsStoredProcProperty().setValue(new PS_COM());
	}

	@Override
	protected BOCompany createChildInstance(Object id) {
		return new BOCompany(this);
	}
}
