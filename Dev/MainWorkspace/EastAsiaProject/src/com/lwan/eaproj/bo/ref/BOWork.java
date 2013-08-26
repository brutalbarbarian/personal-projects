package com.lwan.eaproj.bo.ref;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.bo.cache.BOCustomerCache;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.DateUtil;

public class BOWork extends BODbObject {
	private BODbAttribute<Integer> workID;
	private BODbAttribute<Integer> customerID;
	private BODbAttribute<Integer> companyID;
	private BODbAttribute<Integer> userIDCreated;
	
	private BODbAttribute<Integer> workStage;
	
	private BODbAttribute<Date> dateCreated;
	private BODbAttribute<Date> dateRequired;
	
	private BODbAttribute<String> privateComment;
	
	// Calculated Fields
	private BOAttribute<Double> totalValue, paidValue, remainingValue;
	
	// Sets
	private BOWorkItemSet workItems;
	
	public BOAttribute<Double> totalValue() {
		return totalValue;
	}
	public BOAttribute<Double> paidValue() {
		return paidValue;
	}
	public BOAttribute<Double> remainingValue() {
		return remainingValue;
	}
	
	private BOLink<BOCustomer> customer;
	private BOLink<BOCompany> company;
	private BOLink<BOUser> userCreated;
	
	public BODbAttribute<Integer> workID() {
		return workID;
	}
	public BODbAttribute<Integer> customerID() {
		return customerID;
	}
	public BODbAttribute<Integer> companyID() {
		return companyID;
	}
	public BODbAttribute<Integer> userIDCreated() {
		return userIDCreated;
	}
	public BODbAttribute<Integer> workStage() {
		return workStage;
	}
	public BODbAttribute<Date> dateCreated() {
		return dateCreated;
	}
	public BODbAttribute<Date> dateRequired() {
		return dateRequired;
	}
	public BODbAttribute<String> privateComment() {
		return privateComment;
	}

	public BOCompany company() {
		return company.getReferencedObject();
	}
	
	public BOCustomer customer() {
		return customer.getReferencedObject();
	}
	
	public BOUser userCreated() {
		return userCreated.getReferencedObject();
	}
	
	public BOWorkItemSet workItems() {
		return workItems;
	}
	
	public BOWork(BusinessObject owner) {
		super(owner, "Work");
	}
	

	@Override
	protected void ensureIDExists() {
		if (workID.isNull()) {
			workID.setValue(DbUtil.getNextID("wrk_id"));
		}
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getDbStoredProc("PS_WRK"), SP_SELECT);
		setSP(DbUtil.getDbStoredProc("PI_WRK"), SP_INSERT);
		setSP(DbUtil.getDbStoredProc("PU_WRK"), SP_UPDATE);
		setSP(DbUtil.getDbStoredProc("PD_WRK"), SP_DELETE);
	}
	
	@Override
	protected void afterActiveHandled() {
		recalculateTotalPrices();
	}
	
	protected void recalculateTotalPrices() {
		// TODO
		double totalPrice = 0;
		for (BOWorkItem item : workItems) {
			totalPrice = totalPrice += item.quantity().asInteger() * item.price().asDouble();
		}
		totalValue().setValue(totalPrice);
	}
	
	@Override
	public void handleModified(ModifiedEvent source) {
		if (source.isAttribute() /* && isFromChildrenSet */) {
			recalculateTotalPrices();
		}
	}	

	@Override
	protected void createAttributes() {
		workID = addAsChild(new BODbAttribute<Integer>(this, "WorkID", "wrk_id", AttributeType.ID));
		customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", AttributeType.ID));
		companyID = addAsChild(new BODbAttribute<Integer>(this, "CompanyID", "com_id", AttributeType.ID));
		userIDCreated = addAsChild(new BODbAttribute<Integer>(this, "UserIDCreated", "usr_id_created", AttributeType.ID));
		
		customerID.AllowNulls().setValue(false);
		companyID.AllowNulls().setValue(false);
		userIDCreated.AllowNulls().setValue(false);
		
		workStage = addAsChild(new BODbAttribute<Integer>(this, "WorkStage", "wrk_stage", AttributeType.Integer));
		
		dateCreated = addAsChild(new BODbAttribute<Date>(this, "DateCreated", "wrk_date_create", AttributeType.Date));
		dateRequired = addAsChild(new BODbAttribute<Date>(this, "DateRequired", "wrk_date_due", AttributeType.Date));
		
		privateComment = addAsChild(new BODbAttribute<String>(this, "PrivateComment", "wrk_notes", AttributeType.String));
		
		customer = addAsChildLink(new BOLink<BOCustomer>(this, "Customer"), BOCustomerCache.getCache(), "CustomerID");
		company = addAsChildLink(new BOLink<BOCompany>(this, "Company"), BOCompanySet.getSet(), "CompanyID");
		userCreated = addAsChildLink(new BOLink<BOUser>(this, "UserCreated"), BOUserSet.getSet(), "UserIDCreated");
		
		// Create calculated field
		totalValue = addAsChild(new BOAttribute<Double>(this, "TotalValue", AttributeType.Currency, true, false));
		paidValue = addAsChild(new BOAttribute<Double>(this, "PaidValue", AttributeType.Currency, true, false));
		remainingValue = addAsChild(new BOAttribute<Double>(this, "RemainingValue", AttributeType.Currency, true, false));
		
		// Create sets
		workItems = addAsChild(new BOWorkItemSet(this, "WorkItems"));
	}

	@Override
	public void clearAttributes() {
		dateCreated.setValue(DateUtil.getCurrentDate());
		dateRequired.clear();
		userIDCreated.assign(BOUserSet.getActiveUser().userID());
		
		workStage.setValue(EAConstants.WRK_STAGE_UNAPPROVED);
	}
}
