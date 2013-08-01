package com.lwan.eaproj.bo.ref;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.bo.cache.BOCustomerCache;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.DateUtil;

public class BOStudent extends BODbObject{
	private BODbAttribute<Integer> studentID;
	private BODbAttribute<Integer> customerID;
	private BODbAttribute<Integer> schoolID;
	
	private BODbAttribute<Date> startDate;
	private BODbAttribute<Date> endDate;
	
	private BODbAttribute<String> notes;
	
	private BOLink<BOSchool> school;
	private BOLink<BOCustomer> customer;
	
	public BODbAttribute<Integer> studentID() {
		return studentID;
	}
	public BODbAttribute<Integer> customerID() {
		return customerID;
	}
	public BODbAttribute<Integer> schoolID() {
		return schoolID;
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
	public BOSchool school() {
		return school.getReferencedObject();
	}
	public BOCustomer customer() {
		return customer.getReferencedObject();
	}
	
	public BOStudent(BusinessObject owner) {
		super(owner, "Student");
		
	}

	@Override
	protected void ensureIDExists() {
		if (studentID.isNull()) {
			studentID.setValue(DbUtil.getNextID("stu_id"));	
		}
	}
	
	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getDbStoredProc("PS_STU"), BOStudent.class, SP_SELECT);
		setSP(DbUtil.getDbStoredProc("PI_STU"), BOStudent.class, SP_INSERT);
		setSP(DbUtil.getDbStoredProc("PU_STU"), BOStudent.class, SP_UPDATE);
		setSP(DbUtil.getDbStoredProc("PD_STU"), BOStudent.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		studentID = addAsChild(new BODbAttribute<Integer>(this, "StudentID", "stu_id", AttributeType.ID));
		customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", AttributeType.ID));
		schoolID = addAsChild(new BODbAttribute<Integer>(this, "SchoolID", "sch_id", AttributeType.ID));
		
		startDate = addAsChild(new BODbAttribute<Date>(this, "StartDate", "stu_start_date", AttributeType.Date));
		endDate = addAsChild(new BODbAttribute<Date>(this, "EndDate", "stu_end_date", AttributeType.Date));
		
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "stu_notes", AttributeType.String));
		
		school = addAsChildLink(new BOLink<BOSchool>(this, "School"), BOSchoolSet.getSchoolSet(),
				"SchoolID");
		customer = addAsChildLink(new BOLink<BOCustomer>(this, "Customer"), BOCustomerCache.getCache(),
				"CustomerID");
	}

	@Override
	public void clearAttributes() {
		customerID.clear();
		schoolID().clear();
		
		startDate.setValue(DateUtil.getCurrentDate());
		endDate.clear();
		notes.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {
	}
}
