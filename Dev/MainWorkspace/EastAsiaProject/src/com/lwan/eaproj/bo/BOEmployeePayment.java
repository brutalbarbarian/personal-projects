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
		employeeID.assign(findOwnerByClass(BOEmployee.class).employeeID);
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_EPH(), BOEmployeePayment.class, SP_SELECT);
		setSP(new PI_EPH(), BOEmployeePayment.class, SP_INSERT);
		setSP(new PU_EPH(), BOEmployeePayment.class, SP_UPDATE);
		setSP(new PD_EPH(), BOEmployeePayment.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		employeePaymentID = addAsChild(new BODbAttribute<Integer>(this, "EmployeePaymentID", "eph_id", false));
		employeeID = addAsChild(new BODbAttribute<Integer>(this, "EmployeeID", "emp_id", false));
		
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
