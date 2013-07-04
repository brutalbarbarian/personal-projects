package com.lwan.eaproj.bo.cache;

import com.lwan.bo.LoadMode;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.bo.ref.BOCustomer;
import com.lwan.javafx.app.util.DbUtil;

public class BOCustomerCache extends BODbSet<BOCustomer> {

	public BOCustomerCache() {
		super(null, "CustomerCache", "CustomerID", "cus_id");
		loadModeProperty().setValue(LoadMode.CACHE);

	}

	@Override
	protected void createStoredProcs() {
		existsStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_CUS"));
	}

	@Override
	protected BOCustomer createChildInstance(Object id) {
		return new BOCustomer(this);
	}

	private static BOCustomerCache cache;

	public static BOCustomerCache getCache() {
		if (cache == null) {
			cache = new BOCustomerCache();
		}
		return cache;
	}
}
