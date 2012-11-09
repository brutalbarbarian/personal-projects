package com.lwan.eaproj.cache;

import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.bo.BOSchool;

public class GSchools extends BODbSet<BOSchool> {
	private static GSchools cachedSet;
	public static GSchools get() {
		if (cachedSet == null) {
			cachedSet = new GSchools();
		}
		return cachedSet;
	}

	private GSchools() {
		super(null, "SchoolCache", "SchoolName", "sch_id");
		LoadMode().setValue(LOADMODE_CACHE);
	}

	@Override
	protected void createStoredProcs() {
		// Does it matter for caches?... never going to load anything anyway
	}

	@Override
	protected BOSchool createChildInstance(Object id) {
		return new BOSchool(this);
	}

	public static BOSchool findSchoolByID(int value) {
		return get().findChildByID(value);
	}

}
