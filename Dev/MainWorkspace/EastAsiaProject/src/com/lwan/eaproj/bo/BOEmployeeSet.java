package com.lwan.eaproj.bo;

import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.sp.PS_EMP_for_company;

public class BOEmployeeSet extends BODbSet<BOEmployee>{

	public BOEmployeeSet(BusinessObject owner)  {
		super(owner, "EmployeeSet", "EmployeeID", "emp_id");
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
