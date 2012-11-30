package com.lwan.eaproj.bo;

import java.util.Date;

import com.lwan.bo.AttributeType;
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
		setSP(new PS_EMP(), BOEmployee.class, SP_SELECT);
		setSP(new PI_EMP(), BOEmployee.class, SP_INSERT);
		setSP(new PU_EMP(), BOEmployee.class, SP_UPDATE);
		setSP(new PD_EMP(), BOEmployee.class, SP_DELETE);
	}

	@Override
	protected void ensureIDExists() {
		if (employeeID.asInteger() == 0) {
			employeeID.setValue(DbUtil.getNextID("emp_id"));
		}
		contactDetailsID.assign(contactDetails.contactDetailsID);
		companyID.assign(findOwnerByClass(BOCompany.class).companyID);
	}

	@Override
	protected void createAttributes() {
		employeeID = addAsChild(new BODbAttribute<Integer>(this, "EmployeeID", "emp_id", AttributeType.Integer, false, false));
		companyID = addAsChild(new BODbAttribute<Integer>(this, "CompanyID", "com_id", AttributeType.Integer, false, false));
		contactDetailsID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailsID", "cdt_id", AttributeType.Integer));
		
		nameFirst = addAsChild(new BODbAttribute<String>(this, "NameFirst", "emp_name_first", AttributeType.String));
		nameLast = addAsChild(new BODbAttribute<String>(this, "NameLast", "emp_name_last", AttributeType.String));
		
		payMonthly = addAsChild(new BODbAttribute<Double>(this, "PaymentMonthly", "emp_payment_monthly", AttributeType.Double));
		taxCode = addAsChild(new BODbAttribute<String>(this, "TaxCode", "emp_tax_code", AttributeType.String));
		
		employmentStart = addAsChild(new BODbAttribute<Date>(this, "EmploymentStart", "emp_employment_start", AttributeType.Date));
		isActive = addAsChild(new BODbAttribute<Boolean>(this, "IsActive", "emp_is_active", AttributeType.Boolean, false, true));
		
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
