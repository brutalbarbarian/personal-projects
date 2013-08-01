package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.javafx.app.util.DbUtil;

public class BOStudentSet extends BODbSet<BOStudent>{
	public BOStudentSet(BusinessObject owner) {
		super(owner, "Students", "StudentID", "stu_id");
	}

	@Override
	protected void createStoredProcs() {
		// Owner should choose the stored proc
		selectStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_STU_for_grid"));
	}

	@Override
	protected BOStudent createChildInstance(Object id) {
		return new BOStudent(this);
	}

}
