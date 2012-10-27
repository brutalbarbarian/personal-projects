package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODatabaseSet;
import com.lwan.eaproj.sp.PS_EMP_for_company;

public class BOEmployeeSet extends BODatabaseSet<BOEmployee>{

	public BOEmployeeSet(BusinessObject owner)  {
		super(owner, "EmployeeSet", "emp_id");
	}
	
	
	@Override
	protected BOEmployee createChildInstance() {
		return new BOEmployee(this);
	}

	@Override
	protected void createStoredProcs() {
		SelectStoredProc().setValue(new PS_EMP_for_company()); 
	}

}
