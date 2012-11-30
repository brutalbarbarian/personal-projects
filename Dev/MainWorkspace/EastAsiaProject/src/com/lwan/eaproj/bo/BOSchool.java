package com.lwan.eaproj.bo;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_SCH;
import com.lwan.eaproj.sp.PI_SCH;
import com.lwan.eaproj.sp.PS_SCH;
import com.lwan.eaproj.sp.PU_SCH;
import com.lwan.eaproj.util.DbUtil;

public class BOSchool extends BODbObject{
	public BODbAttribute<Integer> schoolID, contactDetailsID;
	public BODbAttribute<String> schoolName;
	
	public BOContactDetails contactDetails;

	public BOSchool(BusinessObject owner) {
		super(owner, "School");
	}

	@Override
	protected void ensureIDExists() {
		if (schoolID.asInteger() == 0) {
			schoolID.setValue(DbUtil.getNextID("sch_id"));
		}
		contactDetailsID.assign(contactDetails.contactDetailsID);
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_SCH(), BOSchool.class, SP_SELECT);
		setSP(new PI_SCH(), BOSchool.class, SP_INSERT);
		setSP(new PU_SCH(), BOSchool.class, SP_UPDATE);
		setSP(new PD_SCH(), BOSchool.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		schoolID = addAsChild(new BODbAttribute<Integer>(this, "SchoolID", "sch_id", AttributeType.Integer, false, false));
		contactDetailsID = addAsChild(new BODbAttribute<Integer>(this, "ContactDetailsID", "cdt_id", AttributeType.Integer, false, false));
		schoolName = addAsChild(new BODbAttribute<String>(this, "SchoolName", "sch_name", AttributeType.String));
		
		contactDetails = addAsChild(new BOContactDetails(this));
		contactDetails.Independent().setValue(true);
	}

	@Override
	public void clearAttributes() {
		schoolName.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}

}
