package com.lwan.eaproj.bo;

import java.util.Date;

import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.cache.GSchools;
import com.lwan.eaproj.sp.PD_STU;
import com.lwan.eaproj.sp.PI_STU;
import com.lwan.eaproj.sp.PS_STU;
import com.lwan.eaproj.sp.PU_STU;
import com.lwan.eaproj.util.DbUtil;

public class BOStudentRecord extends BODbObject {
	public BODbAttribute<Integer> customerID, studentID, schoolID;
	public BODbAttribute<Date> startDate, endDate;
	public BODbAttribute<String> notes;
	
	private BOLink<BOSchool> school;

	public BOStudentRecord(BusinessObject owner) {
		super(owner, "StudentRecord");
	}
	
	public BOCustomer getCustomer() {
		return findOwnerByClass(BOCustomer.class);
	}
	
	public BOSchool getSchool() {
		return school.getReferencedObject();
	}
	
	protected void createAttributes() {
		customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", false, false));
		studentID = addAsChild(new BODbAttribute<Integer>(this, "StudentID", "stu_id", false, false));
		schoolID = addAsChild(new BODbAttribute<Integer>(this, "SchoolID", "sch_id", false, true));
		
		startDate = addAsChild(new BODbAttribute<Date>(this, "StartDate", "stu_start_date"));
		endDate = addAsChild(new BODbAttribute<Date>(this, "EndDate", "stu_end_date"));
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "stu_notes"));
		
		school = addAsChild(new BOLink<BOSchool>(this, "School"));
	}
	
	protected void createStoredProcs() {
		setSP(new PS_STU(), BOStudentRecord.class, SP_SELECT);
		setSP(new PI_STU(), BOStudentRecord.class, SP_INSERT);
		setSP(new PU_STU(), BOStudentRecord.class, SP_UPDATE);
		setSP(new PD_STU(), BOStudentRecord.class, SP_DELETE);
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends BusinessObject> T getLinkedChild(BOLink<T> link) {
		if (link == school) {
			return (T) GSchools.findSchoolByID(schoolID.getValue());
		} else {
			return super.getLinkedChild(link);
		}
	}
	
	public void clearAttributes() {
		startDate.clear();
		endDate.clear();
		notes.clear();
		schoolID.clear();
	}

	protected void ensureIDExists() {
		if (studentID.asInteger() == 0) {
			studentID.setValue(DbUtil.getNextID("stu_id"));
		}
		customerID.assign(getCustomer().customerID);
	}

	@Override
	public void handleModified(ModifiedEvent source) {}
}