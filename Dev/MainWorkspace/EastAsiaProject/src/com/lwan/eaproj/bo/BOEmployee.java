package com.lwan.eaproj.bo;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODatabaseObject;

public class BOEmployee extends BODatabaseObject {
	BOAttribute<Integer> emp_id, com_id, cdt_id;
	BOAttribute<String> emp_name_first, emp_name_last;
//	BOAttribute<Curren>
	
	
	public BOEmployee(BusinessObject owner, String name) {
		super(owner, name);

	}

	@Override
	protected void ensureIDExists() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createAttributes() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAttributes() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// TODO Auto-generated method stub

	}

}
