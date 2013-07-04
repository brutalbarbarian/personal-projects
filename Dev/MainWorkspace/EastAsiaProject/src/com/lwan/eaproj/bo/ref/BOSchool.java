package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.bo.common.ContactDetailOwner;
import com.lwan.javafx.app.util.DbUtil;

public class BOSchool extends BODbObject implements ContactDetailOwner{
	private BODbAttribute<Integer> schoolID;
	private BODbAttribute<String> schoolName, contactName, notes;
	private BOContactDetail contactDetail;
	
	public BODbAttribute<Integer> schoolID() {
		return schoolID;
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
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getDbStoredProc("PS_SCH"), BOSchool.class, SP_SELECT);
		setSP(DbUtil.getDbStoredProc("PI_SCH"), BOSchool.class, SP_INSERT);
		setSP(DbUtil.getDbStoredProc("PU_SCH"), BOSchool.class, SP_UPDATE);
		setSP(DbUtil.getDbStoredProc("PD_SCH"), BOSchool.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		schoolID = addAsChild(new BODbAttribute<Integer>(this, "SchoolID", "sch_id", AttributeType.ID));
		schoolName = addAsChild(new BODbAttribute<String>(this, "SchoolName", "sch_name", AttributeType.String));
		contactName = addAsChild(new BODbAttribute<String>(this, "ContactName", "sch_contact_name", AttributeType.String));
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "sch_notes", AttributeType.String));
		
		contactDetail = addAsChild(new BOContactDetail(this, "ContactDetail"));
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
	
	@Override
	public int getSourceType(BOContactDetail cdt) {
		return EAConstants.CDT_SOURCE_TYPE_SCHOOL;
	}
	@Override
	public BOAttribute<Integer> getID() {
		return schoolID;
	}

}
