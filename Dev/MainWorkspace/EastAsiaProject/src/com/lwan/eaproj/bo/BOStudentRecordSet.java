package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.sp.PS_STU_for_customer;

public class BOStudentRecordSet extends BODbSet<BOStudentRecord>{

	public BOStudentRecordSet(BusinessObject owner) {
		super(owner, "StudentRecordSet", "StudentID", "stu_id");
	}

	@Override
	protected void createStoredProcs() {
		SelectStoredProc().setValue(new PS_STU_for_customer());
	}

	@Override
	protected BOStudentRecord createChildInstance(Object id) {
		return new BOStudentRecord(this);
	}
}
