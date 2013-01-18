package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.sp.PS_ISH_for_invoice;

public class BOInvoiceSentHistorySet extends BODbSet<BOInvoiceSentHistory>{

	public BOInvoiceSentHistorySet(BusinessObject owner) {
		super(owner, "InvoiceSentHistory", "InvoiceSentHistoryID", "ish_id");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(new PS_ISH_for_invoice());
	}

	@Override
	protected BOInvoiceSentHistory createChildInstance(Object id) {
		return new BOInvoiceSentHistory(this);
	}

}
