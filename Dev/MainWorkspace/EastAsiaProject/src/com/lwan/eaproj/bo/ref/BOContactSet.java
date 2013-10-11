package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;
import com.lwan.javafx.app.util.DbUtil;

public class BOContactSet extends BODocumentItemSet<BOContact> {
	public BOContactSet(BusinessObject owner) {
		super(owner, "Contacts");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_CON_for_document"));
	}

	@Override
	protected BOContact createChildInstance(Object id) {
		return new BOContact(this);
	}

}
