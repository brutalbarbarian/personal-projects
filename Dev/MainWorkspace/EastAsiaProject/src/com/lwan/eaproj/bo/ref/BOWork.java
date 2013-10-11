package com.lwan.eaproj.bo.ref;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.bo.cache.BOCustomerCache;

public class BOWork extends BOChargeableDocument<BOWork> {
	private BODbAttribute<Date> dateRequired;
	private BODbAttribute<Double> paidValue;
	private BODbAttribute<Integer> customerID, companyID;
	
	// Calculated Fields
	private BOAttribute<Double> remainingValue;
	
	public BOAttribute<Double> paidValue() {
		return paidValue;
	}
	public BOAttribute<Double> remainingValue() {
		return remainingValue;
	}
	
	private BOLink<BOCustomer> customer;
	private BOLink<BOCompany> company;
	
	public BODbAttribute<Integer> customerID() {
		return customerID;
	}
	public BODbAttribute<Integer> companyID() {
		return companyID;
	}
	public BODbAttribute<Date> dateRequired() {
		return dateRequired;
	}
	
	public BOCompany company() {
		return company.getReferencedObject();
	}
	
	public BOCustomer customer() {
		return customer.getReferencedObject();
	}
	
	public BOWork(BusinessObject owner) {
		super(owner, "Work");
	}
	
	@Override
	protected String getTableCode() {
		return "WRK";
	}
	
	@Override
	protected void createAttributes() {
		customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", AttributeType.ID, false, true));
		companyID = addAsChild(new BODbAttribute<Integer>(this, "CompanyID", "com_id", AttributeType.ID, false, true));
		
		dateRequired = addAsChild(new BODbAttribute<Date>(this, "DateRequired", "wrk_date_due", AttributeType.Date));
		paidValue = addAsChild(new BODbAttribute<Double>(this, "PaidValue", "wrk_total_paid", AttributeType.Currency, true, false));
		
		customer = addAsChildLink(new BOLink<BOCustomer>(this, "Customer"), BOCustomerCache.getCache(), "CustomerID");
		company = addAsChildLink(new BOLink<BOCompany>(this, "Company"), BOCompanySet.getSet(), "CompanyID");
		
		// Create calculated field
		remainingValue = addAsChild(new BOAttribute<Double>(this, "RemainingValue", AttributeType.Currency, true, false));
	}

	@Override
	public void clearAttributes() {
		stage().setValue(EAConstants.WRK_STAGE_UNAPPROVED);
	}
	@Override
	protected int getDocumentType() {
		return DOC_TYPE_WORK;
	}
}
