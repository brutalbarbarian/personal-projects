package com.lwan.eaproj.bo;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.app.Constants;
import com.lwan.eaproj.bo.ref.BOCustomerType;
import com.lwan.eaproj.sp.PD_CUS;
import com.lwan.eaproj.sp.PI_CUS;
import com.lwan.eaproj.sp.PS_CUS;
import com.lwan.eaproj.sp.PU_CUS;
import com.lwan.eaproj.util.DbUtil;

public class BOCustomer extends BODbObject {
	private BODbAttribute<Integer> customerID, customerTypeID, contactDetailsID;
	private BODbAttribute<String> nameFirst, nameLast, notes, reference;
	
	private BOContactDetails contactDetails;
	private BOLink<BOCustomerType> customerType;
	
	public BODbAttribute<Integer> customerID() {
		return customerID;
	}
	public BODbAttribute<Integer> customerTypeID() {
		return customerTypeID;
	}
	public BODbAttribute<Integer> contactDetailsID() {
		return contactDetailsID;
	}
	public BODbAttribute<String> nameFirst() {
		return nameFirst;
	}
	public BODbAttribute<String> nameLast() {
		return nameLast;
	}
	public BODbAttribute<String> notes() {
		return notes;
	}
	public BODbAttribute<String> reference() {
		return reference;
	}	
	public BOContactDetails contactDetails() {
		return contactDetails;
	}
	public BOCustomerType customerType() {
		return customerType.getReferencedObject();
	}
	
	public BOCustomer(BusinessObject owner) {
		super(owner, "Customer");
	}
	
	public boolean isStudent() {
		return customerTypeID.asInteger() == Constants.CTY_STUDENT; 
	}

	@Override
	protected void ensureIDExists() {
		if (customerID.asInteger() == 0) {
			customerID.setValue(DbUtil.getNextID("cus_id"));
		}
		contactDetailsID.assign(contactDetails.contactDetailsID());
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_CUS(), BOCustomer.class, SP_SELECT);
		setSP(new PI_CUS(), BOCustomer.class, SP_INSERT);
		setSP(new PU_CUS(), BOCustomer.class, SP_UPDATE);
		setSP(new PD_CUS(), BOCustomer.class, SP_DELETE);
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends BusinessObject> T getLinkedChild(BOLink<T> link) {
		if (link == customerType) {
			return (T) BOCustomerType.getCustomerType(customerTypeID.asInteger());
		}
		return null;
	}

	@Override
	protected void createAttributes() {
		customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", AttributeType.Integer, false, false));
		contactDetailsID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailsID", "cdt_id", AttributeType.Integer, false, false));
		
		customerTypeID = addAsChild(new BODbAttribute<Integer>(this, "CustomerTypeID", "cty_id", AttributeType.Integer));
		nameFirst = addAsChild(new BODbAttribute<String>(this, "NameFirst", "cus_name_first", AttributeType.String));
		nameLast = addAsChild(new BODbAttribute<String>(this, "NameLast", "cus_name_last", AttributeType.String));
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "cus_notes", AttributeType.String));
		reference = addAsChild(new BODbAttribute<String>(this, "Reference", "cus_ref", AttributeType.String));
		
		contactDetails = addAsChild(new BOContactDetails(this));
		contactDetails.independentProperty().setValue(true);
		
		customerType = new BOLink<>(this, "CustomerType");
	}

	@Override
	public void clearAttributes() {
		contactDetails.clearAttributes();
		nameFirst.clear();
		nameLast.clear();
		notes.clear();
		reference.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}

}
