package com.lwan.eaproj.bo.ref;

import java.util.Date;

import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.util.DateUtil;

public abstract class BODocument extends BODbObject{
	private BODbAttribute<Integer> documentID, documentType;
	private BODbAttribute<Integer> userIDCreated, userIDChanged, userIDOwner;
	private BODbAttribute<Date> dateCreated, dateChanged;
	private BODbAttribute<String> notes;

	private BOAddressSet addresses;
	private BOContactSet contacts;
	
	private BOLink<BOUser> userCreated, userChanged, userOwner; 
	
	public BODbAttribute<Integer> documentID() {
		return documentID;
	}
	public BODbAttribute<Integer> documentType() {
		return documentType;
	}
	public BODbAttribute<Integer> userIDCreated() {
		return userIDCreated;
	}
	public BODbAttribute<Integer> userIDChanged() {
		return userIDChanged;
	}
	public BODbAttribute<Integer> userIDOwner() {
		return userIDOwner;
	}
	public BODbAttribute<Date> dateCreated() {
		return dateCreated;
	}
	public BODbAttribute<Date> dateChanged() {
		return dateChanged;
	}
	public BODbAttribute<String> notes() {
		return notes;
	}
	public BOUser userCreated() {
		return userCreated.getReferencedObject();
	}
	public BOUser userChanged() {
		return userChanged.getReferencedObject();
	}
	public BOUser userOwner() {
		return userOwner.getReferencedObject();
	}
	public BOContactSet contacts() {
		return contacts;
	}
	public BOAddressSet addresses() {
		return addresses;
	}
	
	public BODocument(BusinessObject owner, String name) {
		super(owner, name);		
	}

	@Override
	protected void ensureIDExists() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createAttributes() {
		
	}

	@Override
	protected void doSave() {
		BOUser user = BOUserSet.getActiveUser();
		userIDChanged.assign(user.userID());
		dateChanged.setValue(DateUtil.getCurrentDate());
		
		super.doSave();
	}
	
	@Override
	public void clearAttributes() {
		BOUser user = BOUserSet.getActiveUser();
		userIDCreated.assign(user.userID());
		userIDOwner.assign(user.userID());
		
		dateCreated.setValue(DateUtil.getCurrentDate());
		documentType.setValue(getDocumentType());
		
		addresses.clear();
		contacts.clear();
		notes.clear();
		userIDChanged.clear();
		dateChanged.clear();
	}
	
	protected abstract int getDocumentType();
}