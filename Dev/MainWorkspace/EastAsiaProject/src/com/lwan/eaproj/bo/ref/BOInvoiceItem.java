package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.GenericsUtil;

public class BOInvoiceItem extends BODbObject {
	private BODbAttribute<Integer> invoiceID, invoiceItemID, workItemID;
	private BODbAttribute<String> comments;
	private BODbAttribute<Double> quantity;
	private BODbAttribute<Double> price;
	
	private BOLink<BOWorkItem> workItem;
	
	public BODbAttribute<Integer> invoiceID() {
		return invoiceID;
	}
	public BODbAttribute<Integer> invoiceItemID() {
		return invoiceItemID;
	}
	public BODbAttribute<Integer> workItemID() {
		return workItemID;
	}
	public BODbAttribute<String> comments() {
		return comments;
	}	
	public BODbAttribute<Double> quantity() {
		return quantity;
	}
	public BODbAttribute<Double> price() {
		return price;
	}
	
	public BOInvoice getInvoice() {
		return findOwnerByClass(BOInvoice.class);
	}
	public BOWorkItem workItem() {
		return workItem.getReferencedObject();
	}

	public BOInvoiceItem(BusinessObject owner) {
		super(owner, "InvoiceItem");
	}

	@Override
	protected void ensureIDExists() {
		if (invoiceItemID.isNull()) {
			invoiceItemID.setValue(DbUtil.getNextID("ini_id"));
		}
		invoiceID.assign(getInvoice().invoiceID());
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getDbStoredProc("PS_INI"), SP_SELECT);
		setSP(DbUtil.getDbStoredProc("PI_INI"), SP_INSERT);
		setSP(DbUtil.getDbStoredProc("PU_INI"), SP_UPDATE);
		setSP(DbUtil.getDbStoredProc("PD_INI"), SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		invoiceID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceID", "inv_id", AttributeType.ID));
		invoiceItemID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceItemID", "ini_id", AttributeType.ID));
		workItemID = addAsChild(new BODbAttribute<Integer>(this, "WorkItemID", "wki_id", AttributeType.ID));
		
		comments = addAsChild(new BODbAttribute<String>(this, "Comments", "ini_comments", AttributeType.String));
		quantity = addAsChild(new BODbAttribute<Double>(this, "Quantity", "ini_quantity", AttributeType.Double));
		price = addAsChild(new BODbAttribute<Double>(this, "Price", "ini_price", AttributeType.Currency));
		
		workItem = addAsChildLink(new BOLink<BOWorkItem>(this, "workItem"), null, "WorkItemID");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends BusinessObject> T getLinkedChild(BOLink<T> link) {
		if (link == workItem) {
			return (T)getInvoice().work().workItems().findChildByID(workItemID.getValue());
		} else {
			return super.getLinkedChild(link);
		}
	}

	@Override
	public void clearAttributes() {
		quantity.setValue(0.0);
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		if (source.getSource() == quantity) {
			BOWorkItem workItem = workItem();

			workItem.avaliableQuantity().setValue(
					workItem.avaliableQuantity().asDouble() 
					+ GenericsUtil.Coalice(quantity.previousValueProperty().getValue(), 0d) 
					- quantity.asDouble());
		}
	}

}
