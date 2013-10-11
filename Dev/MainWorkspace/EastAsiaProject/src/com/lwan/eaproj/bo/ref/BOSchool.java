package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;

public class BOSchool extends BOPersonalDocument{
	public BOSchool(BusinessObject owner) {
		super(owner, "School");
	}

	@Override
	protected String getTableCode() {
		return "SCH";
	}

	@Override
	protected int getDocumentType() {
		return DOC_TYPE_SCHOOL;
	}	
}
