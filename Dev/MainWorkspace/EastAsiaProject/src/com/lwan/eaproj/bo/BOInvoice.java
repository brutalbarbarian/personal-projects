package com.lwan.eaproj.bo;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.bo.cache.GCompany;
import com.lwan.eaproj.bo.cache.GCustomers;
import com.lwan.eaproj.bo.cache.GUsers;
import com.lwan.eaproj.sp.PD_INV;
import com.lwan.eaproj.sp.PI_INV;
import com.lwan.eaproj.sp.PS_INV;
import com.lwan.eaproj.sp.PU_INV;
import com.lwan.eaproj.util.DbUtil;
import com.lwan.util.DateUtil;

public class BOInvoice extends BODbObject{
	public BODbAttribute<Integer> invoiceID, customerID, companyID, userCreatedID;
	public BODbAttribute<Date> dateCreated, dateRequired;
	public BODbAttribute<Double> totalPrice, paidAmount;
	public BODbAttribute<String> notes, ref;
	public BODbAttribute<Boolean> isPaid, isInvalid;
	
	// TODO
	 public BOInvoiceItemSet invoiceItems;
	 public BOInvoiceSentHistorySet invoiceSentHistory;
	
	// Links to reference objects
	private BOLink<BOCompany> company;
	private BOLink<BOCustomer> customer;
	private BOLink<BOUser> userCreated;
	
	public BOCompany getCompany(){
		return company.getReferencedObject();
	}
	
	public BOCustomer getCustomer() {
		return customer.getReferencedObject();
	}
	
	public BOUser getUserCreated() {
		return userCreated.getReferencedObject();
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends BusinessObject> T getLinkedChild(BOLink<T> link) {
		if (link == company) {
			return (T) GCompany.findCompanyByID(companyID.getValue());
		} else if (link == customer) {
			return (T) GCustomers.findCustomerByID(customerID.getValue());
		} else if (link == userCreated) {
			return (T) GUsers.findUserByID(userCreatedID.getValue());
		} else {
			return super.getLinkedChild(link);
		}
	}

	public BOInvoice(BusinessObject owner) {
		super(owner, "Invoice");
	}

	@Override
	protected void ensureIDExists() {
		if (invoiceID.asInteger() == 0) {
			invoiceID.setValue(DbUtil.getNextID("inv_id"));
		}
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_INV(), BOInvoice.class, SP_SELECT);
		setSP(new PI_INV(), BOInvoice.class, SP_INSERT);
		setSP(new PU_INV(), BOInvoice.class, SP_UPDATE);
		setSP(new PD_INV(), BOInvoice.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		invoiceID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceID", "inv_id", AttributeType.Integer, false, false));
		// This is set upon creation
		userCreatedID = addAsChild(new BODbAttribute<Integer>(this, "UserCreatedID", "usr_id_created", AttributeType.Integer, false, false));
		dateCreated = addAsChild(new BODbAttribute<Date>(this, "DateCreated", "inv_date_created", AttributeType.Date, false, false));
		
		// User may modify these
		customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", AttributeType.Integer, false, true));
		companyID = addAsChild(new BODbAttribute<Integer>(this, "CompanyID", "com_id", AttributeType.Integer, false, true));
		
		// These 2 are calculated fields...
		totalPrice = addAsChild(new BODbAttribute<Double>(this, "TotalPrice", "inv_total_price", AttributeType.Currency, true, false));
		paidAmount = addAsChild(new BODbAttribute<Double>(this, "PaidAmount", "inv_paid_amount", AttributeType.Currency, true, false));
		
		dateRequired = addAsChild(new BODbAttribute<Date>(this, "DateReqiured", "inv_date_required", AttributeType.Date));
		
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "inv_notes", AttributeType.String));
		ref = addAsChild(new BODbAttribute<String>(this, "Ref", "inv_ref", AttributeType.String));
		
		isPaid = addAsChild(new BODbAttribute<Boolean>(this, "IsPaid", "inv_is_paid", AttributeType.Boolean));
		isInvalid = addAsChild(new BODbAttribute<Boolean>(this, "IsInvalid", "inv_is_invalid", AttributeType.Boolean));
		
		company = addAsChild(new BOLink<BOCompany>(this, "Company"));
		customer = addAsChild(new BOLink<BOCustomer>(this, "Customer"));
		userCreated = addAsChild(new BOLink<BOUser>(this, "UserCreated"));
		
		invoiceItems = addAsChild(new BOInvoiceItemSet(this));
		invoiceSentHistory = addAsChild(new BOInvoiceSentHistorySet(this));
	}

	@Override
	public void clearAttributes() {
		dateRequired.clear();
		notes.clear();
		ref.clear();
		isPaid.clear();
		isInvalid.clear();
		
		companyID.clear();
		customerID.clear();
		
		invoiceItems.clear();
		invoiceSentHistory.clear();
	}
	
	protected boolean populateAttributes() {
		boolean result = super.populateAttributes();
		if (!result) {
			// This is a new invoice... populate these here
			dateCreated.setValue(DateUtil.getCurrentDate());
			userCreatedID.assign(GUsers.getActiveUser().userID);
		}
		return result;
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// This should automatically update the total price owed... 
		if (source.checkSource(BOInvoiceItem.class, "Price") || 
				source.checkSource(BOInvoiceSentHistory.class, "PaidAmount")) {
			calculateCosts();
		};
	}
	
	protected void calculateCosts() {
		setNotifications(false);
		totalPrice.setValue(0d);
		paidAmount.setValue(0d);
		for (BOInvoiceItem item : invoiceItems) {
			totalPrice.setValue(totalPrice.getValue() + item.price.getValue());
		}
		for (BOInvoiceSentHistory record : invoiceSentHistory) {
			paidAmount.setValue(paidAmount.getValue() + record.paidAmount.getValue());
		}
		setNotifications(true);
		// fire the events off
		fireModified(new ModifiedEvent(totalPrice, ModifiedEvent.TYPE_ATTRIBUTE));
		fireModified(new ModifiedEvent(paidAmount, ModifiedEvent.TYPE_ATTRIBUTE));
	}
}
