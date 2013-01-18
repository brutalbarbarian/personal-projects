package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;

public class BOStudent extends BOCustomer{
	private BOStudentRecordSet students;
	
	public BOStudentRecordSet students() {
		return students;
	}
	
	public BOStudent(BusinessObject owner) {
		super(owner);
		// Override the name
		nameProperty().setValue("Student");
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
