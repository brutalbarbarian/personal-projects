package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;

public abstract class BOPersonalDocument extends BODocument {
	private BODbAttribute<String> name;
	public BODbAttribute<String> name() {
		return name;
	}
	
	public BOPersonalDocument(BusinessObject owner, String name) {
		super(owner, name);
	}

	@Override
	protected void createAttributes() {
		super.createAttributes();
		
		name = addAsChild(new BODbAttribute<String>(this, "Name", "doc_name", AttributeType.String));
	}
}
