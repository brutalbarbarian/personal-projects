package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.javafx.app.util.DbUtil;

public class BOWorkItem extends BODbObject {
	private BODbAttribute<Integer> productID, workID, workItemID; 
	private BODbAttribute<Integer> status;
	private BODbAttribute<Double> price;
	private BODbAttribute<String> comments;
	private BODbAttribute<Integer> quantity;
	
	private BODbAttribute<Double> avaliableQuantity;
	
	private BOAttribute<Double> totalPrice;
	
	private BOLink<BOProduct> product;
	
	public BODbAttribute<Integer> productID() {
		return productID;
	}
	public BODbAttribute<Integer> workID() {
		return workID;
	}
	public BODbAttribute<Integer> workItemID() {
		return workItemID;
	}
	public BODbAttribute<Integer> status() {
		return status;
	}
	public BODbAttribute<Double> price() {
		return price;
	}
	public BODbAttribute<String> comments() {
		return comments;
	}
	public BODbAttribute<Integer> quantity() {
		return quantity;
	}
	public BOAttribute<Double> totalPrice() {
		return totalPrice;
	}
	public BOAttribute<Double> avaliableQuantity() {
		return avaliableQuantity;
	}
	
	public BOProduct getProduct() {
		return product.getReferencedObject();
	}
	public BOWork getWork() {
		return findOwnerByClass(BOWork.class);
	}
	
	public BOWorkItem(BusinessObject owner) {
		super(owner, "WorkItem");
	}

	@Override
	protected void ensureIDExists() {
		if (workItemID.isNull()) {
			workItemID.setValue(DbUtil.getNextID("wki_id"));
		}
		workID.assign(getWork().workID());
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getDbStoredProc("PS_WKI"), SP_SELECT);
		setSP(DbUtil.getDbStoredProc("PI_WKI"), SP_INSERT);
		setSP(DbUtil.getDbStoredProc("PU_WKI"), SP_UPDATE);
		setSP(DbUtil.getDbStoredProc("PD_WKI"), SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		workItemID = addAsChild(new BODbAttribute<Integer>(this, "WorkItemID", "wki_id", AttributeType.ID));
		productID = addAsChild(new BODbAttribute<Integer>(this, "ProductID", "prd_id", AttributeType.ID));
		workID = addAsChild(new BODbAttribute<Integer>(this, "WorkID", "wrk_id", AttributeType.ID));
		
		status = addAsChild(new BODbAttribute<Integer>(this, "Status", "wki_status", AttributeType.ID));
		// price each?
		price = addAsChild(new BODbAttribute<Double>(this, "Price", "wki_price", AttributeType.Currency));
		comments = addAsChild(new BODbAttribute<String>(this, "Comments", "wki_comments", AttributeType.String));
		
		quantity = addAsChild(new BODbAttribute<Integer>(this, "Quantity", "wki_quantity", AttributeType.Integer));

		product = addAsChildLink(new BOLink<BOProduct>(this, "Product"), BOProductSet.getSet(), "productID");
		
		// Calculated
		totalPrice = addAsChild(new BOAttribute<Double>(this, "TotalPrice", AttributeType.Currency));
		avaliableQuantity = addAsChild(new BODbAttribute<Double>(this, "AvaliableQuantity", "wki_avaliable", AttributeType.Double));
		
		totalPrice.triggersModifyProperty().setValue(false);
		avaliableQuantity.triggersModifyProperty().setValue(false);
	}

	@Override
	public void clearAttributes() {
		status.setValue(EAConstants.WKI_STAGE_PENDING);
		quantity().setValue(1);
		price.clear();
		comments.clear();
		product.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		if (source.isAttribute() && source.getSource() == productID) {
			price.setValue(getProduct().price().getValue());
		}
	}
	
	@Override
	protected void afterActiveHandled() {
		super.afterActiveHandled();
		
		calculatePrice();
	}
	
	protected void calculatePrice() {
		totalPrice.setValue(quantity.asInteger() * price.asDouble());
	}

}
