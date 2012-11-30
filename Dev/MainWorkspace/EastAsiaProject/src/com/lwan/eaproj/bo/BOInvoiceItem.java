package com.lwan.eaproj.bo;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.bo.cache.GProducts;
import com.lwan.eaproj.sp.PD_INI;
import com.lwan.eaproj.sp.PI_INI;
import com.lwan.eaproj.sp.PS_INI;
import com.lwan.eaproj.sp.PU_INI;
import com.lwan.eaproj.util.DbUtil;

public class BOInvoiceItem extends BODbObject {
	public BODbAttribute<Integer> invoiceItemID, invoiceID, productID;
	public BODbAttribute<String> notes;
	public BODbAttribute<Double> price;
	
	public BOLink<BOProduct> product;
	
	public BOProduct getProduct() {
		return product.getReferencedObject();
	}

	public BOInvoiceItem(BusinessObject owner) {
		super(owner, "InvoiceItem");
	}

	@Override
	protected void ensureIDExists() {
		if (invoiceItemID.asInteger() == 0) {
			invoiceItemID.setValue(DbUtil.getNextID("ini_id"));
		}
		invoiceID.assign(findOwnerByClass(BOInvoice.class).invoiceID);
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_INI(), BOInvoiceItem.class, SP_SELECT);
		setSP(new PI_INI(), BOInvoiceItem.class, SP_INSERT);
		setSP(new PU_INI(), BOInvoiceItem.class, SP_UPDATE);
		setSP(new PD_INI(), BOInvoiceItem.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		invoiceItemID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceItemID", "ini_id", AttributeType.Integer, false, false));
		invoiceID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceID", "inv_id", AttributeType.Integer, false, false));
		productID = addAsChild(new BODbAttribute<Integer>(this, "ProductID", "prd_id", AttributeType.Integer, false, true));
		
		notes = addAsChild(new BODbAttribute<String>(this, "Notes", "ini_notes", AttributeType.String));
		price = addAsChild(new BODbAttribute<Double>(this, "Price", "ini_price", AttributeType.Double));
		
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
		if (src == productID && price.isNull()) {
			BOProduct product = getProduct();
			if (product != null) {
				price.assign(product.defaultPrice);
			}
		}
	}

}
