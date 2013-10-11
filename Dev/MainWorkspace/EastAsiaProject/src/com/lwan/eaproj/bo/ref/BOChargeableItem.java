package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.db.BODbAttribute;

public class BOChargeableItem <D extends BOChargeableDocument<? extends BOChargeableItem<D>>> extends BODocumentItem<D>{
	private BODbAttribute<Double> unitPrice;	// alternatively, rate per quantity
	private BODbAttribute<Double> quantity;
	
	public BODbAttribute<Double> unitPrice() {
		return unitPrice;
	}
	public BODbAttribute<Double> quantity() {
		return quantity;
	}
	
	@Override
	protected void createAttributes() {
		super.createAttributes();
		
		unitPrice = addAsChild(new BODbAttribute<Double>(this, "UnitPrice", getTableCode() + "_price", AttributeType.Currency));
		quantity = addAsChild(new BODbAttribute<Double>(this, "Quantity", getTableCode() + "_quantity", AttributeType.Double));
	}
	
	public BOChargeableItem(BusinessObject owner, String name) {
		super(owner, name);
	}

}
