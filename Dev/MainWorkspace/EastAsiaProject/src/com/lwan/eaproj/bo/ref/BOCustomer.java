package com.lwan.eaproj.bo.ref;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.bo.common.ContactDetailOwner;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.DateUtil;

public class BOCustomer extends BODbObject implements ContactDetailOwner{
	private BODbAttribute<Integer> customerID;
	private BODbAttribute<String> name, notes;
	private BODbAttribute<Date> dateCreated;
	private BODbAttribute<Boolean> active, isStudent;
	private BOContactDetail contactDetail;
	
	private BOStudentSet students;
	
	public BODbAttribute<Integer> customerID() {
		return customerID;
	}
	public BODbAttribute<String> name() {
		return name;
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
	public BOStudentSet students() {
		return students;
	}

	public BOCustomer(BusinessObject owner) {
		super(owner, "Customer");
	}

	@Override
	protected void ensureIDExists() {
		if (customerID.isNull()) {
			customerID.setValue(DbUtil.getNextID("cus_id"));
		}
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getDbStoredProc("PS_CUS"), SP_SELECT);
		setSP(DbUtil.getDbStoredProc("PI_CUS"), SP_INSERT);
		setSP(DbUtil.getDbStoredProc("PU_CUS"), SP_UPDATE);
		setSP(DbUtil.getDbStoredProc("PD_CUS"), SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", AttributeType.ID));
		name = addAsChild(new BODbAttribute<String>(this, "Name", "cus_name", AttributeType.String));
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "cus_notes", AttributeType.String));
		dateCreated = addAsChild(new BODbAttribute<Date>(this, "DateCreated", "cus_date_created", AttributeType.Date));
		active = addAsChild(new BODbAttribute<Boolean>(this, "Active", "cus_is_active", AttributeType.Boolean));
		isStudent = addAsChild(new BODbAttribute<Boolean>(this, "IsStudent", "cus_is_student", AttributeType.Boolean));
		
		students = addAsChild(new BOStudentSet(this));
		
		contactDetail = addAsChild(new BOContactDetail(this, "ContactDetail"));
	}

	@Override
	public void clearAttributes() {
		dateCreated.setValue(DateUtil.getCurrentDate());
		active.setValue(true);
		isStudent.setValue(false);
		
		name.clear();
		notes.clear();

		contactDetail.clearAttributes();
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public int getSourceType(BOContactDetail cdt) {
		return EAConstants.CDT_SOURCE_TYPE_CUSTOMER;
	}
	@Override
	public BOAttribute<Integer> getID() {
		return customerID;
	}
	
}
