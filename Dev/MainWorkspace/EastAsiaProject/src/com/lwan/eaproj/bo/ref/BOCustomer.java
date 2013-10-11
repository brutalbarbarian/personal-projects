package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;

public class BOCustomer extends BOPersonalDocument{
	private BODbAttribute<Boolean> isStudent;
	private BOStudentSet students;
	
	public BODbAttribute<Boolean> isStudent() {
		return isStudent;
	}
	public BOStudentSet students() {
		return students;
	}

	public BOCustomer(BusinessObject owner) {
		super(owner, "Customer");
	}

	@Override
	protected void createAttributes() {
		super.createAttributes();
		
		isStudent = addAsChild(new BODbAttribute<Boolean>(this, "IsStudent", "cus_is_student", AttributeType.Boolean));
		
		students = addAsChild(new BOStudentSet(this));		
	}

	@Override
	public void clearAttributes() {
		isStudent.setValue(false);		
	}
	
	@Override
	protected String getTableCode() {
		return "CUS";
	}
	
	@Override
	protected int getDocumentType() {
		return DOC_TYPE_CUSTOMER;
	}
}
