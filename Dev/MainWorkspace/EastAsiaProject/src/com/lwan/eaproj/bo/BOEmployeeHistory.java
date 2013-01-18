package com.lwan.eaproj.bo;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.bo.ref.BOTax;
import com.lwan.eaproj.util.DbUtil;

public class BOEmployeeHistory extends BODbObject {
	private BODbAttribute<Integer> employeeHistoryID, employeeID, taxID;
	private BODbAttribute<Double> paymentAmount;
	private BODbAttribute<Integer> paymentMode;
	private BODbAttribute<Date> startDate, endDate;
	private BODbAttribute<String> notes;
	
	private BOLink<BOTax> tax;
	
	public BODbAttribute<Integer> employeeHistoryID() {
		return employeeHistoryID;
	}
	public BODbAttribute<Integer> employeeID() {
		return employeeID;
	}
	public BODbAttribute<Integer> taxID() {
		return taxID;
	}
	public BODbAttribute<Double> paymentAmount() {
		return paymentAmount;
	}
	public BODbAttribute<Integer> paymentMode() {
		return paymentMode;
	}
	public BODbAttribute<Date> startDate() {
		return startDate;
	}
	public BODbAttribute<Date> endDate() {
		return endDate;
	}
	public BODbAttribute<String> notes() {
		return notes;
	}
	public BOTax tax() {
		return tax.getReferencedObject();
	}

	@SuppressWarnings("unchecked")
	protected <T extends BusinessObject> T getLinkedChild(BOLink<T> link) {
		if (link == tax) {
			return (T)BOTax.getCustomerType(taxID.getValue());
		}
		return null;
	}
	
	public BOEmployeeHistory(BusinessObject owner) {
		super(owner, "EmployeeHistory");
	}

	@Override
	protected void ensureIDExists() {
		if (employeeHistoryID.asInteger() == 0) {
			employeeHistoryID.setValue(DbUtil.getNextID("eph_id"));
		}
		employeeID.assign(findOwnerByClass(BOEmployee.class).employeeID());
	}

	@Override
	protected void createStoredProcs() {

	}

	@Override
	protected void createAttributes() {
		employeeHistoryID = new BODbAttribute<>(this, "EmployeeHistoryID", "eph_id", AttributeType.Integer, false, false);
		employeeID = new BODbAttribute<>(this, "EmployeeID", "emp_id", AttributeType.Integer, false, false);
		taxID = new BODbAttribute<>(this, "TaxID", "tax_id", AttributeType.Integer);
		
		paymentAmount = new BODbAttribute<>(this, "PaymentAmount", "eph_payment_amount", AttributeType.Currency);
		paymentMode = new BODbAttribute<>(this, "PaymentMode", "eph_payment_mode", AttributeType.Integer);
		
		startDate = new BODbAttribute<>(this, "StartDate", "eph_start_date", AttributeType.Date);
		endDate = new BODbAttribute<>(this, "EndDate", "eph_end_date", AttributeType.Date);
		
		notes = new BODbAttribute<>(this, "Notes", "eph_notes", AttributeType.String);

		tax = new BOLink<BOTax>(this, "Tax");	
	}

	@Override
	public void clearAttributes() {
		paymentAmount.clear();
		paymentMode.clear();
		taxID.clear();
		startDate.clear();
		endDate.clear();
		notes.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// Do nothing?
	}

}
