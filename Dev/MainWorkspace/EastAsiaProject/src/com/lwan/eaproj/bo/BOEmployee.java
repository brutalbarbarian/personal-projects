package com.lwan.eaproj.bo;

import java.util.Date;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODatabaseObject;
import com.lwan.eaproj.sp.PI_EMP;
import com.lwan.eaproj.sp.PS_EMP;
import com.lwan.eaproj.util.DbUtil;

public class BOEmployee extends BODatabaseObject {
	public BOAttribute<Integer> emp_id, com_id, cdt_id;
	public BOAttribute<String> emp_name_first, emp_name_last, emp_tax_code;
	public BOAttribute<Double> emp_payment_monthly;
	public BOAttribute<Date> emp_employment_start;
	public BOAttribute<Boolean> emp_is_active;
	
	BOContactDetails contactDetails;
	
	// set of payment history...TODO
	
	public BOEmployee(BusinessObject owner) {
		super(owner, "Employee");
		
		
	}
	
	@Override
	protected void createStoredProcs() {
		SelectStoredProc().setValue(new PS_EMP());
		InsertStoredProc().setValue(new PI_EMP());
		// TODO
	}

	@Override
	protected void ensureIDExists() {
		if (emp_id.isNull() || emp_id.getValue() == 0) {
			emp_id.setValue(DbUtil.getNextID("emp_id"));
		}
		cdt_id.assign(contactDetails.cdt_id);
		// TODO assign company
//		com_id.assign(getOwnerByClass(BOCompany.class).com_id);
	}

	@Override
	protected void createAttributes() {
		emp_id = addAsChild(new BOAttribute<Integer>(this, "emp_id", false, null, 0));
		com_id = addAsChild(new BOAttribute<Integer>(this, "com_id", false, null, 0));
		cdt_id = addAsChild(new BOAttribute<Integer>(this, "cdt_id", false, null, 0));
		
		emp_name_first = addAsChild(new BOAttribute<String>(this, "emp_name_first"));
		emp_name_last = addAsChild(new BOAttribute<String>(this, "emp_name_last"));
		
		emp_payment_monthly = addAsChild(new BOAttribute<Double>(this, "emp_payment_monthly"));
		emp_tax_code = addAsChild(new BOAttribute<String>(this, "emp_tax_code"));
		
		emp_employment_start = addAsChild(new BOAttribute<Date>(this, "emp_employment_start"));
		emp_is_active = addAsChild(new BOAttribute<Boolean>(this, "emp_is_active", false, false, false));
		
		contactDetails = addAsChild(new BOContactDetails(this));
		contactDetails.Independent().setValue(true);
		
		// TODO create payments set
	}
	
	protected boolean populateAttributes() {
		boolean result = super.populateAttributes();
		if (result) {
			contactDetails.cdt_id.assign(cdt_id);
			// TODO assign child payments set
		} else {
			contactDetails.cdt_id.clear();
		}
		return result;
	}

	@Override
	public void clearAttributes() {
		emp_name_first.clear();
		emp_name_last.clear();
		emp_payment_monthly.clear();
		emp_tax_code.clear();
		emp_employment_start.clear();
		
		emp_is_active.setValue(false);
		
		contactDetails.clear();
		
		// TODO clear child payments set
	}

	@Override
	public void handleModified(ModifiedEvent source) {}
}