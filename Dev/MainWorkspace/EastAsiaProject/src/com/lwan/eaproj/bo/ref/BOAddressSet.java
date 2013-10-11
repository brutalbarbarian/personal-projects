package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;
import com.lwan.javafx.app.util.DbUtil;

public class BOAddressSet extends BODocumentItemSet<BOAddress>{

	public BOAddressSet(BusinessObject owner) {
		super(owner, "Addresses");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_ADR_for_document"));
	}

	@Override
	protected BOAddress createChildInstance(Object id) {
		return new BOAddress(this);
	}

}
