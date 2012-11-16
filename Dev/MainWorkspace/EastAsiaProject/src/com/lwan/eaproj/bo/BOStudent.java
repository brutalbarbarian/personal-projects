package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;

public class BOStudent extends BOCustomer{
	public BOStudentRecordSet students;
	
	public BOStudent(BusinessObject owner) {
		super(owner);
		// Override the name
		Name().setValue("Student");
	}
	
	protected void createAttributes() {
		super.createAttributes();
		
		students = addAsChild(new BOStudentRecordSet(this));
	}
	
	public void clearAttributes() {
		super.clearAttributes();
		students.clear();
	}
}
