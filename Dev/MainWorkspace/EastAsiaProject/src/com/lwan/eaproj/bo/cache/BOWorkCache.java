package com.lwan.eaproj.bo.cache;

import com.lwan.bo.LoadMode;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.bo.ref.BOWork;
import com.lwan.javafx.app.util.DbUtil;

public class BOWorkCache extends BODbSet<BOWork>{

	public BOWorkCache() {
		super(null, "WorkCache", "WorkID", "wrk_id");
		loadModeProperty().setValue(LoadMode.CACHE);
	}

	@Override
	protected void createStoredProcs() {
		existsStoredProcProperty().setValue(DbUtil.getDbStoredProc("PS_WRK"));
	}

	@Override
	protected BOWork createChildInstance(Object id) {
		return new BOWork(this);
	}

	private static BOWorkCache cache;

	public static BOWorkCache getCache() {
		if (cache == null) {
			cache = new BOWorkCache();
		}
		cache.ensureActive();
		return cache;
	}

}
