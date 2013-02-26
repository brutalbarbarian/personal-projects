package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.eaproj.sp.PD_TAX;
import com.lwan.eaproj.sp.PI_TAX;
import com.lwan.eaproj.sp.PS_TAX;
import com.lwan.eaproj.sp.PS_TAX_for_set;
import com.lwan.eaproj.sp.PU_TAX;
import com.lwan.javafx.app.util.DbUtil;

public class BOTax extends BODbObject {
	public static BOTax getCustomerType(int taxID) {
		return GTax().findChildByID(taxID);
	}
	
	private static class BOTaxSet extends BODbSet<BOTax> {
		public BOTaxSet() {
			super(null, "TaxSet", "TaxID", "tax_id");
		}

		@Override
		protected void createStoredProcs() {
			selectStoredProcProperty().setValue(new PS_TAX_for_set());
		}

		@Override
		protected BOTax createChildInstance(Object id) {
			return new BOTax(this);
		}
	}
	private static BOTaxSet GTaxes;
	public static BOTaxSet GTax() {
		if (GTaxes == null) {
			GTaxes = new BOTaxSet();
		}
		return GTaxes;
	}
	
	private BODbAttribute<Integer> taxID;
	private BODbAttribute<String> taxName;
	private BODbAttribute<Double> taxPercent;
	
	public BODbAttribute<Integer> taxID() {
		return taxID;
	}
	public BODbAttribute<String> taxName() {
		return taxName;
	}
	public BODbAttribute<Double> taxPercent() {
		return taxPercent;
	}


	public BOTax(BusinessObject owner) {
		super(owner, "Tax");
		
	}

	@Override
	protected void ensureIDExists() {
		if (taxID.asInteger() == 0) {
			taxID.setValue(DbUtil.getNextID("tax_id"));
		}		
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_TAX(), BOTax.class, SP_SELECT);
		setSP(new PI_TAX(), BOTax.class, SP_INSERT);
		setSP(new PU_TAX(), BOTax.class, SP_UPDATE);
		setSP(new PD_TAX(), BOTax.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		taxID = new BODbAttribute<>(this, "TaxID", "tax_id", AttributeType.Integer, false, false);
		taxName = new BODbAttribute<>(this, "TaxName", "tax_name", AttributeType.String, false, true);
		taxPercent = new BODbAttribute<>(this, "TaxPercent", "tax_percent", AttributeType.Integer, false, true);
	}

	@Override
	public void clearAttributes() {
		taxName.clear();
		taxPercent.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// Do nothing?
	}

}
