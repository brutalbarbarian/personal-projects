package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.sp.PS_EPP_for_employee;

public class BOEmployeePaymentSet extends BODbSet<BOEmployeePayment> {

	public BOEmployeePaymentSet(BusinessObject owner) {
		super(owner, "EmployeePaymentSet", "EmployeePaymentID", "epp_id");
	}

	@Override
	protected void createStoredProcs() {
		 selectStoredProcProperty().setValue(new PS_EPP_for_employee());
	}

	@Override
	protected BOEmployeePayment createChildInstance(Object id) {
		return new BOEmployeePayment(this);
	}

}
