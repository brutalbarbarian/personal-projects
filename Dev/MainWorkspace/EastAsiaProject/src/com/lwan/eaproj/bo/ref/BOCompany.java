package com.lwan.eaproj.bo.ref;

import com.lwan.bo.BusinessObject;

public class BOCompany extends BOPersonalDocument{
	public BOCompany(BusinessObject owner) {
		super(owner, "Company");
	}

	@Override
	protected String getTableCode() {
		return "COM";
	}
	
	@Override
	protected int getDocumentType() {
		return DOC_TYPE_COMPANY;
	}
}
