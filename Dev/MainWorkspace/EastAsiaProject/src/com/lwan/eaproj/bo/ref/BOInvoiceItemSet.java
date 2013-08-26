package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.javafx.app.util.DbUtil;

public class BOInvoiceItemSet extends BODbSet<BOInvoiceItem> {

	public BOInvoiceItemSet(BusinessObject owner, String name) {
		super(owner, name, "InvoiceItemID", "ini_id");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_INI_for_invoice"));
	}

	@Override
	protected BOInvoiceItem createChildInstance(Object id) {
		return new BOInvoiceItem(this);
	}

}
