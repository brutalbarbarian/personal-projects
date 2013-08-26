package com.lwan.eaproj.bo.ref;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.bo.cache.BOWorkCache;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.DateUtil;

public class BOInvoice extends BODbObject {
	private BODbAttribute<Integer> invoiceID;
	private BODbAttribute<Integer> workID;
	private BODbAttribute<Integer> userIDCreated;
	private BODbAttribute<Date> dateCreated;
	private BODbAttribute<String> comments;
	private BODbAttribute<Integer> stage;
	
	private BOInvoiceItemSet invoiceItems;
	
	private BOLink<BOUser> userCreated;
	private BOLink<BOWork> work;
	
	private BOAttribute<Double> totalValue;
	
	public BODbAttribute<Integer> invoiceID() {
		return invoiceID;
	}
	public BODbAttribute<Integer> workID() {
		return workID;
	}
	public BODbAttribute<Integer> userIDCreated() {
		return userIDCreated;
	}
	public BODbAttribute<Date> dateCreated() {
		return dateCreated;
	}
	public BODbAttribute<String> comments() {
		return comments;
	}
	public BODbAttribute<Integer> stage() {
		return stage;
	}
	public BOInvoiceItemSet invoiceItems() {
		return invoiceItems;
	}
	public BOUser userCreated() {
		return userCreated.getReferencedObject();
	}
	public BOWork work() {
		return work.getReferencedObject();
	}
	public BOAttribute<Double> totalValue() {
		return totalValue;
	}
	
	public BOInvoice(BusinessObject owner) {
		super(owner, "Invoice");
	}

	@Override
	protected void ensureIDExists() {
		if (invoiceID.isNull()) {
			invoiceID.setValue(DbUtil.getNextID("inv_id"));
		}
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getDbStoredProc("PS_INV"), SP_SELECT);
		setSP(DbUtil.getDbStoredProc("PI_INV"), SP_INSERT);
		setSP(DbUtil.getDbStoredProc("PU_INV"), SP_UPDATE);
		setSP(DbUtil.getDbStoredProc("PD_INV"), SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		invoiceID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceID", "inv_id", AttributeType.ID));
		workID = addAsChild(new BODbAttribute<Integer>(this, "WorkID", "wrk_id", AttributeType.ID));
		userIDCreated = addAsChild(new BODbAttribute<Integer>(this, "UserIDCreated", "usr_id_created", AttributeType.ID));
		dateCreated = addAsChild(new BODbAttribute<Date>(this, "DateCreated", "inv_date_create", AttributeType.Date));
		comments = addAsChild(new BODbAttribute<String>(this, "Comments", "inv_comments", AttributeType.String));
		stage = addAsChild(new BODbAttribute<Integer>(this, "Stage", "inv_stage", AttributeType.ID));
		
		invoiceItems = addAsChild(new BOInvoiceItemSet(this, "InvoiceItems"));
		
		userCreated = addAsChildLink(new BOLink<BOUser>(this, "userCreated"), BOUserSet.getSet(), "UserIDCreated");
		work = addAsChildLink(new BOLink<BOWork>(this, "work"), BOWorkCache.getCache(), "WorkID");
		
		totalValue = addAsChild(new BOAttribute<Double>(this, "TotalValue", AttributeType.Currency));
	}

	@Override
	public void clearAttributes() {
		stage.setValue(EAConstants.INV_STAGE_PENDING);
		userIDCreated.setValue(BOUserSet.getActiveUser().userID().getValue());
		dateCreated.setValue(DateUtil.getCurrentDate());
	}
	
	protected void computeTotalValue(){
		double value = 0;
		for (BOInvoiceItem item : invoiceItems()) {
			value = value + item.price().getValue() * item.quantity().getValue();
		}
	}

	@Override
	protected void afterActiveHandled() {
		super.afterActiveHandled();
		
		computeTotalValue();
	}
	
	@Override
	public void handleModified(ModifiedEvent source) {

	}

}
