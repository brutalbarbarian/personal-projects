package com.lwan.eaproj.bo.ref;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.DateUtil;

/**
 * BODocument is the root of all main tables, including Customers,
 * Schools, Work and Invoices.
 * Anything that can have contact/address info is considered a document.
 * 
 * @author Brutalbarbarian
 *
 */
public abstract class BODocument extends BODbObject{
	public static final int DOC_TYPE_CUSTOMER = 1;
	public static final int DOC_TYPE_WORK = 2;
	public static final int DOC_TYPE_INVOICE = 3;
	public static final int DOC_TYPE_SCHOOL = 4;
	public static final int DOC_TYPE_COMPANY = 5;
	
	
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
		if (documentID.isNull()) {
			documentID.setValue(DbUtil.getNextID("doc_id"));
		}
	}

	@Override
	protected void createAttributes() {
		documentID = addAsChild(new BODbAttribute<Integer>(this, "DocumentID", "doc_id", AttributeType.ID, false, false));
		documentType = addAsChild(new BODbAttribute<Integer>(this, "DocumentType", "dty_id", AttributeType.ID, false, false));
		userIDCreated = addAsChild(new BODbAttribute<Integer>(this, "UserIDCreated", "usr_id_created", AttributeType.ID));
		userIDChanged = addAsChild(new BODbAttribute<Integer>(this, "UserIDChanged", "usr_id_changed", AttributeType.ID));
		userIDOwner = addAsChild(new BODbAttribute<Integer>(this, "UserIDOwner", "usr_id_owner", AttributeType.ID));
		
		dateCreated = addAsChild(new BODbAttribute<Date>(this, "DateCreated", "doc_date_created", AttributeType.Date));
		dateChanged = addAsChild(new BODbAttribute<Date>(this, "DateChanged", "doc_date_changed", AttributeType.Date));
		
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "doc_notes", AttributeType.String));
		
		addresses = addAsChild(new BOAddressSet(this));
		contacts = addAsChild(new BOContactSet(this));
		
		userCreated = addAsChildLink(new BOLink<BOUser>(this, "UserCreated"), BOUserSet.getSet(), "UserIDCreated");
		userChanged = addAsChildLink(new BOLink<BOUser>(this, "UserChanged"), BOUserSet.getSet(), "UserIDChanged");
		userOwner = addAsChildLink(new BOLink<BOUser>(this, "UserOwner"), BOUserSet.getSet(), "UserIDOwner");
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
		super.clearAttributes();
		
		BOUser user = BOUserSet.getActiveUser();
		userIDCreated.assign(user.userID());
		userIDOwner.assign(user.userID());
		
		dateCreated.setValue(DateUtil.getCurrentDate());
		documentType.setValue(getDocumentType());
	}
	
	protected abstract int getDocumentType();
}