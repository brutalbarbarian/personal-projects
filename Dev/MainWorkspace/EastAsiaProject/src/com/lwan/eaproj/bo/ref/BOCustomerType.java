package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.sp.PS_CTY;
import com.lwan.eaproj.sp.PS_CTY_for_set;

public class BOCustomerType extends BODbObject{
	public static BOCustomerType getCustomerType(int customerTypeID) {
		return GCustomerType().findChildByID(customerTypeID);
	}
	
	public static CustomerTypeSet GCustomerType() {
		if (customerTypeSet == null) {
			customerTypeSet = new CustomerTypeSet();
			customerTypeSet.ensureActive();
		}
		return customerTypeSet;
	}
	
	private static CustomerTypeSet customerTypeSet;
	
	public static class CustomerTypeSet extends BODbSet<BOCustomerType> {

		public CustomerTypeSet() {
			super(null, "CustomerTypeSet", "CustomerTypeID", "cty_id");
		}

		@Override
		protected void createStoredProcs() {
			selectStoredProcProperty().setValue(new PS_CTY_for_set());
			loadModeProperty().setValue(LOADMODE_PASSIVE);
		}

		@Override
		protected BOCustomerType createChildInstance(Object id) {
			return new BOCustomerType(this);
		}		
	}
	
	private BODbAttribute<Integer> customerTypeID;
	private BODbAttribute<String> customerTypeName;
	
	public BODbAttribute<Integer> customerTypeID() {
		return customerTypeID;
	}
	public BODbAttribute<String> customerTypeName() {
		return customerTypeName;
	}
	
	public BOCustomerType(BusinessObject owner) {
		super(owner, "CustomerType");

	}

	@Override
	protected void ensureIDExists() {
		// REF TABLE do nothing
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_CTY(), BOCustomerType.class, SP_SELECT);
	}

	@Override
	protected void createAttributes() {
		customerTypeID = new BODbAttribute<>(this, "CustomerTypeID", "cty_id", 
				AttributeType.Integer, false, false);
		customerTypeName = new BODbAttribute<>(this, "CustomerTypeName", "cty_name",
				AttributeType.String, false, false);
	}

	@Override
	public void clearAttributes() {
		// REF TABLE
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// REF TABLE
	}

}
