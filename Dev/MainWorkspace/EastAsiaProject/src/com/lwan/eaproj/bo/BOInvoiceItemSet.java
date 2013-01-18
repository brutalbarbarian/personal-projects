package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.sp.PS_INI_for_invoice;

public class BOInvoiceItemSet extends BODbSet<BOInvoiceItem>{

	public BOInvoiceItemSet(BusinessObject owner) {
		super(owner, "InvoiceItemSet", "InvoiceItemID", "ini_id");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(new PS_INI_for_invoice());
	}

	@Override
	protected BOInvoiceItem createChildInstance(Object id) {
		return new BOInvoiceItem(this);
	}

}
