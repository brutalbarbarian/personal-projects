package com.lwan.eaproj.bo;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_PRD;
import com.lwan.eaproj.sp.PI_PRD;
import com.lwan.eaproj.sp.PS_PRD;
import com.lwan.eaproj.sp.PU_PRD;
import com.lwan.eaproj.util.DbUtil;

public class BOProduct extends BODbObject{
	public BODbAttribute <Integer> productID, productCategoryID;
	public BODbAttribute <String> name, description;
	public BODbAttribute <Double> defaultPrice;
	
	public BOProduct(BusinessObject owner) {
		super(owner, "Product");
	}

	@Override
	protected void ensureIDExists() {
		if (productID.asInteger() == 0) {
			productID.setValue(DbUtil.getNextID("prd_id"));
		}
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_PRD(), BOProduct.class, SP_SELECT);
		setSP(new PI_PRD(), BOProduct.class, SP_INSERT);
		setSP(new PU_PRD(), BOProduct.class, SP_UPDATE);
		setSP(new PD_PRD(), BOProduct.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		productID = addAsChild(new BODbAttribute<Integer>(this, "ProductID", "prd_id", AttributeType.Integer, false, false));
		productCategoryID = addAsChild(new BODbAttribute<Integer>(this, "ProductCategoryID", "prc_id", AttributeType.Integer, false, true));
		name = addAsChild(new BODbAttribute<String>(this, "Name", "prd_name", AttributeType.String));
		description = addAsChild(new BODbAttribute<String>(this, "Description", "prd_description", AttributeType.String));
		defaultPrice = addAsChild(new BODbAttribute<Double>(this, "DefaultPrice", "prd_default_price", AttributeType.Currency));
	}

	@Override
	public void clearAttributes() {
		name.clear();
		description.clear();
		defaultPrice.clear();
		productCategoryID.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {}
	
}
