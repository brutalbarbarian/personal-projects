package com.lwan.eaproj.bo.ref;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.DateUtil;

public class BOCustomer extends BODbObject{
	private BODbAttribute<Integer> customerID, contactDetailID;
	private BODbAttribute<String> firstName, lastName, notes;
	private BODbAttribute<Date> dateCreated;
	private BODbAttribute<Boolean> active, isStudent;
	private BOContactDetail contactDetail;
	
	public BODbAttribute<Integer> customerID() {
		return customerID;
	}
	public BODbAttribute<Integer> contactDetailID() {
		return contactDetailID;
	}
	public BODbAttribute<String> firstName() {
		return firstName;
	}
	public BODbAttribute<String> lastName() {
		return lastName;
	}
	public BODbAttribute<String> notes() {
		return notes;
	}
	public BODbAttribute<Date> dateCreated() {
		return dateCreated;
	}
	public BODbAttribute<Boolean> active() {
		return active;
	}
	public BODbAttribute<Boolean> isStudent() {
		return isStudent;
	}
	public BOContactDetail contactDetail() {
		return contactDetail;
	}

	public BOCustomer(BusinessObject owner) {
		super(owner, "Customer");
	}

	@Override
	protected void ensureIDExists() {
		if (customerID.isNull()) {
			customerID.setValue(DbUtil.getNextID("cus_id"));
		}
		contactDetailID.assign(contactDetail.contactDetailID());
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getStoredProc("PS_CUS"), BOCustomer.class, SP_SELECT);
		setSP(DbUtil.getStoredProc("PI_CUS"), BOCustomer.class, SP_INSERT);
		setSP(DbUtil.getStoredProc("PU_CUS"), BOCustomer.class, SP_UPDATE);
		setSP(DbUtil.getStoredProc("PD_CUS"), BOCustomer.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", AttributeType.ID));
		contactDetailID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailID", "cdt_id", AttributeType.ID));
		firstName = addAsChild(new BODbAttribute<String>(this, "FirstName", "cus_name_first", AttributeType.String));
		lastName = addAsChild(new BODbAttribute<String>(this, "LastName", "cus_name_last", AttributeType.String));
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "cus_notes", AttributeType.String));
		dateCreated = addAsChild(new BODbAttribute<Date>(this, "DateCreated", "cus_date_created", AttributeType.Date));
		active = addAsChild(new BODbAttribute<Boolean>(this, "Active", "cus_is_active", AttributeType.Boolean));
		isStudent = addAsChild(new BODbAttribute<Boolean>(this, "IsStudent", "cus_is_student", AttributeType.Boolean));
		
		contactDetail = addAsChild(new BOContactDetail(this, "ContactDetail"));
	}

	@Override
	public void clearAttributes() {
		dateCreated.setValue(DateUtil.getCurrentDate());
		active.setValue(true);
		isStudent.setValue(false);
		
		firstName.clear();
		lastName.clear();
		notes.clear();

		contactDetail.clearAttributes();
	}
	
	protected boolean populateAttributes() {
		boolean result = super.populateAttributes();
		if (result) {
			contactDetail.contactDetailID().assign(contactDetailID);
		} else {
			contactDetail.contactDetailID().clear();
		}
		return result;
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// TODO Auto-generated method stub
		
	}
	
}
