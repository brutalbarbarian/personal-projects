package com.lwan.eaproj.bo;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.cache.GProducts;

public class BOInvoiceItem extends BODbObject {
	public BODbAttribute<Integer> invoiceItemID, invoiceID, productID;
	public BODbAttribute<String> notes;
	public BODbAttribute<Double> price;
	
	public BOAttribute<Double> actualPrice;
	
	public BOLink<BOProduct> product;
	
	public BOProduct getProduct() {
		return product.getReferencedObject();
	}

	public BOInvoiceItem(BusinessObject owner, String name) {
		super(owner, name);
	}

	@Override
	protected void ensureIDExists() {
		// TODO... this doesn't have an id..
	}

	@Override
	protected void createStoredProcs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createAttributes() {
		invoiceItemID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceItemID", "ini_id", false, false));
		invoiceID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceID", "inv_id", false, false));
		productID = addAsChild(new BODbAttribute<Integer>(this, "ProductID", "prd_id", false, false));
		
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "ini_notes"));
		price = addAsChild(new BODbAttribute<Double>(this, "Price", "ini_price"));
		
		actualPrice = addAsChild(new BOAttribute<Double>(this, "ActualPrice", false, false));
		
		product = addAsChild(new BOLink<BOProduct>(this, "Product"));
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends BusinessObject> T getLinkedChild(BOLink<T> link) {
		if (link == product) {
			return (T) GProducts.findProductByID(productID.asInteger());
		} else {
			return super.getLinkedChild(link);
		}
	}

	@Override
	public void clearAttributes() {
		notes.clear();
		price.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		Object src = source.getSource();
		if (src == productID || src == price) {
			if (!price.isNull()) {
				actualPrice.assign(price);
			} else {
				BOProduct product = getProduct();
				if (product == null) {
					actualPrice.setValue(0d);
				} else {
					actualPrice.assign(product.defaultPrice);
				}
			}
		}
	}

}
