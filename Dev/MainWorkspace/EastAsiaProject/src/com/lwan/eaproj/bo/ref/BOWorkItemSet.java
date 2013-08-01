package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.javafx.app.util.DbUtil;

public class BOWorkItemSet extends BODbSet<BOWorkItem> {

	public BOWorkItemSet(BusinessObject owner, String name) {
		super(owner, name, "WorkItemID", "wki_id");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_WKI_for_work"));
	}

	@Override
	protected BOWorkItem createChildInstance(Object id) {
		return new BOWorkItem(this);
	}

}
