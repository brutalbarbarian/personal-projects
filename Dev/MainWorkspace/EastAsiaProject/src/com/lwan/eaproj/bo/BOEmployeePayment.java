package com.lwan.eaproj.bo;

import java.util.Date;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_EPH;
import com.lwan.eaproj.sp.PI_EPH;
import com.lwan.eaproj.sp.PS_EPH;
import com.lwan.eaproj.sp.PU_EPH;
import com.lwan.eaproj.util.DbUtil;

public class BOEmployeePayment extends BODbObject{
	public BODbAttribute<Integer> employeePaymentID, employeeID;
	public BODbAttribute<Date> datePaid;
	public BODbAttribute<String> notes; 
	public BODbAttribute<Double> paidAmount;
	

	public BOEmployeePayment(BusinessObject owner) {
		super(owner, "EmployeePayment");
	}

	@Override
	protected void ensureIDExists() {
		if (employeePaymentID.asInteger() == 0) {
			employeePaymentID.setValue(DbUtil.getNextID("eph_id"));
		}
		employeeID.assign(getOwnerByClass(BOEmployee.class).employeeID);
	}

	@Override
	protected void createStoredProcs() {
		SelectStoredProc().setValue(new PS_EPH());
		InsertStoredProc().setValue(new PI_EPH());
		UpdateStoredProc().setValue(new PU_EPH());
		DeleteStoredProc().setValue(new PD_EPH());
	}

	@Override
	protected void createAttributes() {
		employeePaymentID = addAsChild(new BODbAttribute<Integer>(this, "EmployeePaymentID", "eph_id", false, null, 0));
		employeeID = addAsChild(new BODbAttribute<Integer>(this, "EmployeeID", "emp_id", false, null, 0));
		
		datePaid = addAsChild(new BODbAttribute<Date>(this, "DatePaid", "eph_date_paid"));
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "eph_notes"));
		paidAmount = addAsChild(new BODbAttribute<Double>(this, "PaidAmount", "eph_paid_amount"));
	}

	@Override
	public void clearAttributes() {
		datePaid.clear();
		notes.clear();
		paidAmount.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}

}
