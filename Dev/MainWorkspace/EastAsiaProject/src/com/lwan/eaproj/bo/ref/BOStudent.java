package com.lwan.eaproj.bo.ref;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.DateUtil;

public class BOStudent extends BODocumentItem<BOCustomer>{
	private BODbAttribute<Integer> schoolID;
	
	private BODbAttribute<Date> startDate;
	private BODbAttribute<Date> endDate;
	
	private BODbAttribute<String> notes;
	
	private BOLink<BOSchool> school;
	
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
	
	public BOStudent(BusinessObject owner) {
		super(owner, "Student");
		
	}
	
	@Override
	protected String getTableCode() {
		return "STU";
	}

	@Override
	protected void createAttributes() {
		super.createAttributes();
		
		schoolID = addAsChild(new BODbAttribute<Integer>(this, "SchoolID", "sch_id", AttributeType.ID));
		
		startDate = addAsChild(new BODbAttribute<Date>(this, "StartDate", "stu_start_date", AttributeType.Date));
		endDate = addAsChild(new BODbAttribute<Date>(this, "EndDate", "stu_end_date", AttributeType.Date));
		
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "stu_notes", AttributeType.String));
		
		school = addAsChildLink(new BOLink<BOSchool>(this, "School"), BOSchoolSet.getSchoolSet(),
				"SchoolID");
	}

	@Override
	public void clearAttributes() {
		schoolID().clear();
		
		startDate.setValue(DateUtil.getCurrentDate());
		endDate.clear();
		notes.clear();
	}
}
