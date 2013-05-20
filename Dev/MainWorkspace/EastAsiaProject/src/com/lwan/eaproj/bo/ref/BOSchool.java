package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.javafx.app.util.DbUtil;

public class BOSchool extends BODbObject{
	private BODbAttribute<Integer> schoolID, contactDetailID;
	private BODbAttribute<String> schoolName, contactName, notes;
	private BOContactDetail contactDetail;
	
	public BODbAttribute<Integer> schoolID() {
		return schoolID;
	}
	public BODbAttribute<Integer> contactDetailID() {
		return contactDetailID;
	}
	public BODbAttribute<String> schoolName() {
		return schoolName;
	}
	public BODbAttribute<String> contactName() {
		return contactName;
	}
	public BODbAttribute<String> notes() {
		return notes;
	}
	public BOContactDetail contactDetail(){
		return contactDetail;
	}
	
	public BOSchool(BusinessObject owner) {
		super(owner, "School");
	}

	@Override
	protected void ensureIDExists() {
		if (schoolID.isNull()) {
			schoolID.setValue(DbUtil.getNextID("sch_id"));
		}
		
		contactDetailID.assign(contactDetail.contactDetailID());
	}
	
	protected boolean populateAttributes() {
		boolean result = super.populateAttributes();
		if (result) {
			contactDetail.contactDetailID().assign(contactDetailID);
		} else {
			contactDetail.contactDetailID().clear();
		}
		return result;
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getStoredProc("PS_SCH"), BOSchool.class, SP_SELECT);
		setSP(DbUtil.getStoredProc("PI_SCH"), BOSchool.class, SP_INSERT);
		setSP(DbUtil.getStoredProc("PU_SCH"), BOSchool.class, SP_UPDATE);
		setSP(DbUtil.getStoredProc("PS_SCH"), BOSchool.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		schoolID = addAsChild(new BODbAttribute<Integer>(this, "SchoolID", "sch_id", AttributeType.ID));
		contactDetailID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailID", "cdt_id", AttributeType.ID));
		schoolName = addAsChild(new BODbAttribute<String>(this, "SchoolName", "sch_name", AttributeType.String));
		contactName = addAsChild(new BODbAttribute<String>(this, "ContactName", "sch_contact_name", AttributeType.String));
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "sch_notes", AttributeType.String));
		
		contactDetail = addAsChild(new BOContactDetail(this, "ContactDetail"));
		contactDetail.setIndependent(true);
	}

	@Override
	public void clearAttributes() {
		schoolName.clear();
		contactName.clear();
		notes.clear();
		contactDetail.clearAttributes();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}

}
