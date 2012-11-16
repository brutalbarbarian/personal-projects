package com.lwan.eaproj.cache;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.bo.BOProduct;
import com.lwan.eaproj.sp.PS_PRD;

public class GProducts extends BODbSet<BOProduct>{
	private static GProducts cache;
	public static GProducts get() {
		if (cache == null) {
			cache = new GProducts();
		}
		return cache;
	}
	
	public static BOProduct findProductByID(int id) {
		return get().findChildByID(id);
	}
	
	private GProducts() {
		super(null, "ProductsCache", "ProductID", "prd_id");
		
		LoadMode().setValue(LOADMODE_CACHE);
	}

	@Override
	protected void createStoredProcs() {
		ExistsStoredProc().setValue(new PS_PRD());
	}

	@Override
	protected BOProduct createChildInstance(Object id) {
		return new BOProduct(this);
	}
	
}
