package com.lwan.eaproj.bo.cache;

import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.bo.ref.BOInvoice;
import com.lwan.javafx.app.util.DbUtil;

public class BOInvoiceCache extends BODbSet<BOInvoice>{

	public BOInvoiceCache() {
		super(null, "InvoiceCache", "InvoiceID", "inv_id");
	}

	@Override
	protected void createStoredProcs() {
		existsStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_INV"));
	}

	@Override
	protected BOInvoice createChildInstance(Object id) {
		return new BOInvoice(this);
	}

	private static BOInvoiceCache cache;
	public static BOInvoiceCache getCache() {
		if (cache == null) {
			cache = new BOInvoiceCache();
		}
		return cache;
	}
}
