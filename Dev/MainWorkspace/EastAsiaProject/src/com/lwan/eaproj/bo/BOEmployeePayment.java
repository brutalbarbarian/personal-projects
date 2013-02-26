package com.lwan.eaproj.bo;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_EPP;
import com.lwan.eaproj.sp.PI_EPP;
import com.lwan.eaproj.sp.PS_EPP;
import com.lwan.eaproj.sp.PU_EPP;
import com.lwan.javafx.app.util.DbUtil;

public class BOEmployeePayment extends BODbObject{
	private BODbAttribute<Integer> employeePaymentID, employeeID;
	private BODbAttribute<Date> datePaid;
	private BODbAttribute<String> notes; 
	private BODbAttribute<Double> paidAmount;
	
	public BODbAttribute<Integer> employeePaymentID() {
		return employeePaymentID;
	}
	public BODbAttribute<Integer> employeeID() {
		return employeeID;
	}
	public BODbAttribute<Date> datePaid() {
		return datePaid;
	}
	public BODbAttribute<String> notes() {
		return notes;
	}
	public BODbAttribute<Double> paidAmount() {
		return paidAmount;
	}


	public BOEmployeePayment(BusinessObject owner) {
		super(owner, "EmployeePayment");
	}

	@Override
	protected void ensureIDExists() {
		if (employeePaymentID.asInteger() == 0) {
			employeePaymentID.setValue(DbUtil.getNextID("epp_id"));
		}
		employeeID.assign(findOwnerByClass(BOEmployee.class).employeeID());
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_EPP(), BOEmployeePayment.class, SP_SELECT);
		setSP(new PI_EPP(), BOEmployeePayment.class, SP_INSERT);
		setSP(new PU_EPP(), BOEmployeePayment.class, SP_UPDATE);
		setSP(new PD_EPP(), BOEmployeePayment.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		employeePaymentID = addAsChild(new BODbAttribute<Integer>(this, "EmployeePaymentID", "epp_id", AttributeType.Integer, false, false));
		employeeID = addAsChild(new BODbAttribute<Integer>(this, "EmployeeID", "emp_id", AttributeType.Integer, false, false));
		
		datePaid = addAsChild(new BODbAttribute<Date>(this, "DatePaid", "epp_date_paid", AttributeType.Date));
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "epp_notes", AttributeType.String));
		paidAmount = addAsChild(new BODbAttribute<Double>(this, "PaidAmount", "epp_paid_amount", AttributeType.Currency));
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
