package com.lwan.eaproj.bo.ref;

import com.lwan.bo.db.BODbSet;
import com.lwan.javafx.app.util.DbUtil;

public class BOProductSet extends BODbSet<BOProduct>{


	public BOProductSet() {
		super(null, "ProductSet", "ProductID", "prd_id");
	}

	@Override
	protected void createStoredProcs() {
		selectStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_PRD_all"));
	}

	@Override
	protected BOProduct createChildInstance(Object id) {
		return new BOProduct(this);
	}

	private static BOProductSet productSet;
	public static final BOProductSet getSet() {
		if (productSet == null) {
			productSet = new BOProductSet();
			productSet.ensureActive();
		}
		return productSet;
	};
}
