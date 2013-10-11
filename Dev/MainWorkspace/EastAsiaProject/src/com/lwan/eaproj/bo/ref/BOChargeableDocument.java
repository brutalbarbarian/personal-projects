package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;

public abstract class BOChargeableDocument <I extends BOChargeableItem<BOChargeableDocument<I>>> extends BODocument{
	private BODbAttribute<Double> total;
	private BODbAttribute<Integer> stage;
	
	private BODocumentItemSet<I> chargeableItems;
	
	public BODbAttribute<Double> total() {
		return total;
	}
	public BODbAttribute<Integer> stage() {
		return stage;
	}
	public BODocumentItemSet<I> chargeableItems() {
		return chargeableItems;		
	}
	
	public BOChargeableDocument(BusinessObject owner, String name) {
		super(owner, name);
	}
	
	@Override
	protected void createAttributes() {
		super.createAttributes();
		
		total = addAsChild(new BODbAttribute<Double>(this, "Total", "doc_total", AttributeType.Currency));
		stage = addAsChild(new BODbAttribute<Integer>(this, "Stage", "doc_stage", AttributeType.ID));
	}
	
	@Override
	public void clearAttributes() {
		super.clearAttributes();

	}
	
	protected void calculateTotalPrice() {
		double total = 0;
		for (I item : chargeableItems()) {
			total += item.unitPrice().getValue() * item.quantity().getValue();
		}
		// simplistic calculations for now. Will deal with
		// tax and other things later.
		total().setValue(total);
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		super.handleModified(source);
		
		if (source.isAttribute(BOChargeableItem.class, "quantity", "unitprice")) {
			calculateTotalPrice();
		}
	}	
}
