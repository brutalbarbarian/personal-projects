package com.lwan.eaproj.bo;

import java.util.Date;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_EMP;
import com.lwan.eaproj.sp.PI_EMP;
import com.lwan.eaproj.sp.PS_EMP;
import com.lwan.eaproj.sp.PU_EMP;
import com.lwan.eaproj.util.DbUtil;

public class BOEmployee extends BODbObject {
	public BODbAttribute<Integer> employeeID, companyID, contactDetailsID;
	public BODbAttribute<String> nameFirst, nameLast, taxCode;
	public BODbAttribute<Double> payMonthly;
	public BODbAttribute<Date> employmentStart;
	public BODbAttribute<Boolean> isActive;
	
	public BOContactDetails contactDetails;
	
	public BOEmployeePaymentSet employeePayments;
	
	public BOEmployee(BusinessObject owner) {
		super(owner, "Employee");
		
		
	}
	
	@Override
	protected void createStoredProcs() {
		SelectStoredProc().setValue(new PS_EMP());
		InsertStoredProc().setValue(new PI_EMP());
		UpdateStoredProc().setValue(new PU_EMP());
		DeleteStoredProc().setValue(new PD_EMP());
	}

	@Override
	protected void ensureIDExists() {
		if (employeeID.asInteger() == 0) {
			employeeID.setValue(DbUtil.getNextID("emp_id"));
		}
		contactDetailsID.assign(contactDetails.contactDetailsID);
		companyID.assign(getOwnerByClass(BOCompany.class).companyID);
	}

	@Override
	protected void createAttributes() {
		employeeID = addAsChild(new BODbAttribute<Integer>(this, "EmployeeID", "emp_id", false, null, 0));
		companyID = addAsChild(new BODbAttribute<Integer>(this, "CompanyID", "com_id", false, null, 0));
		contactDetailsID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailsID", "cdt_id", false, null, 0));
		
		nameFirst = addAsChild(new BODbAttribute<String>(this, "NameFirst", "emp_name_first"));
		nameLast = addAsChild(new BODbAttribute<String>(this, "NameLast", "emp_name_last"));
		
		payMonthly = addAsChild(new BODbAttribute<Double>(this, "PaymentMonthly", "emp_payment_monthly"));
		taxCode = addAsChild(new BODbAttribute<String>(this, "TaxCode", "emp_tax_code"));
		
		employmentStart = addAsChild(new BODbAttribute<Date>(this, "EmploymentStart", "emp_employment_start"));
		isActive = addAsChild(new BODbAttribute<Boolean>(this, "IsActive", "emp_is_active", false, false, false));
		
		contactDetails = addAsChild(new BOContactDetails(this));
		contactDetails.Independent().setValue(true);
		
		employeePayments = addAsChild(new BOEmployeePaymentSet(this));
	}
	
	protected boolean populateAttributes() {
		boolean result = super.populateAttributes();
		if (result) {
			contactDetails.contactDetailsID.assign(contactDetailsID);
		} else {
			contactDetails.contactDetailsID.clear();
		}
		return result;
	}

	@Override
	public void clearAttributes() {
		nameFirst.clear();
		nameLast.clear();
		payMonthly.clear();
		taxCode.clear();
		employmentStart.clear();
		
		isActive.setValue(false);
		
		contactDetails.clear();
		
		employeePayments.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}
}
