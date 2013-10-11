package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;

public class BOContact extends BODocumentItem<BODocument>{
	private BODbAttribute<String> name, work, home, mobile, email;
	public BODbAttribute<String> name() {
		return name;
	}
	public BODbAttribute<String> work() {
		return work;
	}
	public BODbAttribute<String> home() {
		return home;
	}
	public BODbAttribute<String> mobile() {
		return mobile;
	}
	public BODbAttribute<String> email() {
		return email;
	}
	
	public BOContact(BusinessObject owner) {
		super(owner, "Contact");
	}
	
	@Override
	protected void createAttributes() {
		super.createAttributes();
		
		name = addAsChild(new BODbAttribute<String>(this, "Name", "con_name", AttributeType.String));
		work = addAsChild(new BODbAttribute<String>(this, "Work", "con_work", AttributeType.String));
		home = addAsChild(new BODbAttribute<String>(this, "Home", "con_home", AttributeType.String));
		mobile = addAsChild(new BODbAttribute<String>(this, "Mobile", "con_mobile", AttributeType.String));
		email = addAsChild(new BODbAttribute<String>(this, "Email", "con_email", AttributeType.String));
	}
	
	@Override
	protected String getTableCode() {
		return "CON";
	}

}
