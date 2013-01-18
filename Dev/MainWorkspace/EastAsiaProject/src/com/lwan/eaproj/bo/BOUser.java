package com.lwan.eaproj.bo;

import javafx.util.Callback;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_USR;
import com.lwan.eaproj.sp.PI_USR;
import com.lwan.eaproj.sp.PS_USR;
import com.lwan.eaproj.sp.PU_USR;
import com.lwan.eaproj.util.DbUtil;
import com.lwan.javafx.property.Validation;

public class BOUser extends BODbObject{
	/* Attribute fields */
	private BODbAttribute<Integer> userID;
	private BODbAttribute<String> userName, description,
		// password is a hashed string, with the timestamp used to guarantee
		// uniqueness. this way even the program dosen't know what the original
		// password was.
		password;
	// Timestamp is effectively the key.
	private BODbAttribute<String> timestamp;
	
	/* Attribute accessors */
	public BODbAttribute<Integer> userID() {
		return userID;
	}
	public BODbAttribute<String> userName() {
		return userName;
	}
	public BODbAttribute<String> description() {
		return description;
	}
	public BODbAttribute<String> password() {
		return password;
	}
	public BODbAttribute<String> timestamp() {
		return timestamp;
	}
	
	public BOUser(BusinessObject owner) {
		super(owner, "User");
		
	}

	@Override
	protected void ensureIDExists() {
		if (userID.asInteger() == 0) {
			userID.setValue(DbUtil.getNextID("usr_id"));
		}
	}
	
	protected String doVerifyState() {
		if (userName.asString().length() == 0){	// cannot be empty username
			return "Username cannot have a length of 0";
		}
		return super.doVerifyState();
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_USR(), BOUser.class, SP_SELECT);
		setSP(new PI_USR(), BOUser.class, SP_INSERT);
		setSP(new PU_USR(), BOUser.class, SP_UPDATE);
		setSP(new PD_USR(), BOUser.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		userID = addAsChild(new BODbAttribute<Integer>(this, "UserID", "usr_id", AttributeType.Integer, false, false));
		userName = addAsChild(new BODbAttribute<String>(this, "UserName", "usr_name", AttributeType.String, false, true));
		userName.addValidationListener(new Validation.StringValidator(
				// Stop usage of white space for the username
				Validation.STR_NO_LIMIT, Validation.STR_NO_LIMIT, true, true, false, false, true, null));
		timestamp = addAsChild(new BODbAttribute<String>(this, "Timestamp", "usr_timestamp", AttributeType.String));
		password = addAsChild(new BODbAttribute<String>(this, "Password", "usr_password", AttributeType.String));
		description = addAsChild(new BODbAttribute<String>(this, "Description", "usr_description", AttributeType.String));		
		
		password.setBeforeChangeListener(new Callback<String, String>() {
			public String call(String newValue) {
				if (isPopulatingProperty().getValue()) {
					return newValue;	// don't interfare
				} else if (newValue == null) {
					return null;
				} else {
					long time = System.currentTimeMillis();
					timestamp.setValue(Long.toString(time));
					return hashPassword(newValue); 
				}
			}
		});
	}
	
	public boolean checkPassword(String pass) {
		return hashPassword(pass).equals(password.getValue());
	}
	
	private String hashPassword(String value) {
		return Integer.toString((value + timestamp.asString()).hashCode());
	}

	@Override
	public void clearAttributes() {
		userName.clear();
		password.clear();
		description.clear();
		timestamp.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		
	}
	
}
