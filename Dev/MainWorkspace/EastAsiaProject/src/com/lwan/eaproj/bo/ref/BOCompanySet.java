package com.lwan.eaproj.bo.ref;

import com.lwan.bo.db.BODbSet;
import com.lwan.javafx.app.util.DbUtil;

public class BOCompanySet extends BODbSet<BOCompany>{
	private BOCompanySet() {
		super(null, "CompanySet", "CompanyID", "com_id");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(DbUtil.getStoredProc("PS_COM_all"));
	}

	@Override
	protected BOCompany createChildInstance(Object id) {
		return new BOCompany(this);
	}

	private static BOCompanySet companySet;
	public static final BOCompanySet getSet() {
		if (companySet == null) {
			companySet = new BOCompanySet();
			companySet.ensureActive();
		}
		return companySet;
	}
}
