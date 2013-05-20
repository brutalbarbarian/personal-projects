package com.lwan.eaproj.bo.ref;

import com.lwan.bo.db.BODbSet;
import com.lwan.javafx.app.util.DbUtil;

public class BOSchoolSet extends BODbSet<BOSchool>{

	public BOSchoolSet() {
		super(null, "SchoolSet", "SchoolID", "sch_id");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(DbUtil.getStoredProc("PS_SCH_all"));
	}

	@Override
	protected BOSchool createChildInstance(Object id) {
		return new BOSchool(this);
	}

	private static BOSchoolSet schoolSet;
	public static BOSchoolSet getSchoolSet() {
		if (schoolSet == null) {
			schoolSet = new BOSchoolSet();
			schoolSet.ensureActive();
		}
		return schoolSet;
	}
}
