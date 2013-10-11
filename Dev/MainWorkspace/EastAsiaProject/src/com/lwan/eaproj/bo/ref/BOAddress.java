package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;

public class BOAddress extends BODocumentItem<BODocument> {
	private BODbAttribute<String> address1, address2, city, postcode;
	public BODbAttribute<String> address1() {
		return address1;
	}
	public BODbAttribute<String> address2() {
		return address2;
	}
	public BODbAttribute<String> city() {
		return city;
	}
	public BODbAttribute<String> postcode() {
		return postcode;
	}
	
	public BOAddress(BusinessObject owner) {
		super(owner, "Address");
	}

	@Override
	protected void createAttributes() {
		super.createAttributes();
		
		address1 = addAsChild(new BODbAttribute<String>(this, "Address1", "adr_address1", AttributeType.String));
		address2 = addAsChild(new BODbAttribute<String>(this, "Address2", "adr_address2", AttributeType.String));
		city = addAsChild(new BODbAttribute<String>(this, "City", "adr_city", AttributeType.String));
		postcode = addAsChild(new BODbAttribute<String>(this, "PostCode", "adr_postcode", AttributeType.String));
	}
	
	@Override
	protected String getTableCode() {
		return "ADR";
	}


}
