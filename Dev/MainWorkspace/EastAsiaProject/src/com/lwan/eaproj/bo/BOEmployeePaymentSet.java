package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.sp.PS_EPH_for_employee;

public class BOEmployeePaymentSet extends BODbSet<BOEmployeePayment> {

	public BOEmployeePaymentSet(BusinessObject owner) {
		super(owner, "EmployeePaymentSet", "EmployeePaymentID", "eph_id");
	}

	@Override
	protected void createStoredProcs() {
		 SelectStoredProc().setValue(new PS_EPH_for_employee());
	}

	@Override
	protected BOEmployeePayment createChildInstance() {
		return new BOEmployeePayment(this);
	}

}
