package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;
import com.lwan.javafx.app.util.DbUtil;

public class BOStudentSet extends BODocumentItemSet<BOStudent>{
	public BOStudentSet(BusinessObject owner) {
		super(owner, "Students");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_STU_for_document"));
	}

	@Override
	protected BOStudent createChildInstance(Object id) {
		return new BOStudent(this);
	}
}
