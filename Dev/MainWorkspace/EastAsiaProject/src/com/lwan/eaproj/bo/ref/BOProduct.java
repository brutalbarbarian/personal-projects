package com.lwan.eaproj.bo.ref;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.javafx.app.util.DbUtil;

public class BOProduct extends BODbObject{
	private BODbAttribute<Integer> productID;
	private BODbAttribute<String> name, description;
	private BODbAttribute<Double> price;
	private BODbAttribute<String> comments;
	
	public BODbAttribute<Integer> productID() {
		
		return productID;
	}
	public BODbAttribute<String> name() {
		return name;
	}
	public BODbAttribute<String> description() {
		return description;
	}
	public BODbAttribute<Double> price() {
		return price;
	}
	public BODbAttribute<String> comments() {
		return comments;
	}
	
	public BOProduct(BusinessObject owner) {
		super(owner, "Product");
	}

	@Override
	protected void ensureIDExists() {
		if (productID().isNull()) {
			productID().setValue(DbUtil.getNextID("prd_id"));
		}
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getDbStoredProc("PS_PRD"), SP_SELECT);
		setSP(DbUtil.getDbStoredProc("PI_PRD"), SP_INSERT);
		setSP(DbUtil.getDbStoredProc("PU_PRD"), SP_UPDATE);
		setSP(DbUtil.getDbStoredProc("PD_PRD"), SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		productID = addAsChild(new BODbAttribute<Integer>(this, "ProductID", "prd_id", AttributeType.ID));
		name = addAsChild(new BODbAttribute<String>(this, "Name", "prd_name", AttributeType.String));
		description = addAsChild(new BODbAttribute<String>(this, "Description", "prd_description", AttributeType.String));
		price = addAsChild(new BODbAttribute<Double>(this, "Price", "prd_price", AttributeType.Currency));
		comments = addAsChild(new BODbAttribute<String>(this, "Comments", "prd_comments", AttributeType.String));
	}

	@Override
	public void clearAttributes() {}

	@Override
	public void handleModified(ModifiedEvent source) {}

}
