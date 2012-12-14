package com.lwan.eaproj.bo;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.app.Constants;
import com.lwan.eaproj.sp.PD_CUS;
import com.lwan.eaproj.sp.PI_CUS;
import com.lwan.eaproj.sp.PS_CUS;
import com.lwan.eaproj.sp.PU_CUS;
import com.lwan.eaproj.util.DbUtil;

public class BOCustomer extends BODbObject {
	public BODbAttribute<Integer> customerID, customerType, contactDetailsID;
	public BODbAttribute<String> nameFirst, nameLast, notes, reference;
	
	public BOContactDetails contactDetails;
	
	public BOCustomer(BusinessObject owner) {
		super(owner, "Customer");
	}
	
	public boolean isStudent() {
		return customerType.asInteger() == Constants.CTY_STUDENT; 
	}

	@Override
	protected void ensureIDExists() {
		if (customerID.asInteger() == 0) {
			customerID.setValue(DbUtil.getNextID("cus_id"));
		}
		contactDetailsID.assign(contactDetails.contactDetailsID);
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_CUS(), BOCustomer.class, SP_SELECT);
		setSP(new PI_CUS(), BOCustomer.class, SP_INSERT);
		setSP(new PU_CUS(), BOCustomer.class, SP_UPDATE);
		setSP(new PD_CUS(), BOCustomer.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", AttributeType.Integer, false, false));
		contactDetailsID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailsID", "cdt_id", AttributeType.Integer, false, false));
		
		customerType = addAsChild(new BODbAttribute<Integer>(this, "CustomerType", "cty_id", AttributeType.Integer));
		nameFirst = addAsChild(new BODbAttribute<String>(this, "NameFirst", "cus_name_first", AttributeType.String));
		nameLast = addAsChild(new BODbAttribute<String>(this, "NameLast", "cus_name_last", AttributeType.String));
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "cus_notes", AttributeType.String));
		reference = addAsChild(new BODbAttribute<String>(this, "Reference", "cus_ref", AttributeType.String));
		
		contactDetails = addAsChild(new BOContactDetails(this));
		contactDetails.Independent().setValue(true);
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
