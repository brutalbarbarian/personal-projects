package com.lwan.eaproj.bo;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.eaproj.sp.PD_ISH;
import com.lwan.eaproj.sp.PI_ISH;
import com.lwan.eaproj.sp.PS_ISH;
import com.lwan.eaproj.sp.PU_ISH;
import com.lwan.eaproj.util.DbUtil;
import com.lwan.util.DateUtil;

public class BOInvoiceSentHistory extends BODbObject {
	public BODbAttribute<Integer> invoiceID, invoiceSentHistoryID;
	public BODbAttribute<Date> sentDate;
	public BODbAttribute<Double> paidAmount;
	
	public BOInvoiceSentHistory(BusinessObject owner) {
		super(owner, "InvoiceSentHistoryRecord");
	}

	@Override
	protected void ensureIDExists() {
		if (invoiceSentHistoryID.asInteger() == 0) {
			invoiceSentHistoryID.setValue(DbUtil.getNextID("ish_id"));
		}
		invoiceID.assign(findOwnerByClass(BOInvoice.class).invoiceID);
	}

	@Override
	protected void createStoredProcs() {
		setSP(new PS_ISH(), BOInvoiceSentHistory.class, SP_SELECT);
		setSP(new PI_ISH(), BOInvoiceSentHistory.class, SP_INSERT);
		setSP(new PU_ISH(), BOInvoiceSentHistory.class, SP_UPDATE);
		setSP(new PD_ISH(), BOInvoiceSentHistory.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		invoiceID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceID", "inv_id", AttributeType.Integer, false, false));
		invoiceSentHistoryID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceSentHistoryID", "ish_id", AttributeType.Integer, false, false));
		sentDate = addAsChild(new BODbAttribute<Date>(this, "SentDate", "ish_sent_date", AttributeType.Date, false, false));
		paidAmount = addAsChild(new BODbAttribute<Double>(this, "PaidAmount", "ish_paid_amount", AttributeType.Currency, false, true));
	}
	
	@Override
	public boolean populateAttributes() {
		if (super.populateAttributes()) {
			return true;
		} else {
			// Set the sentdate upon creation
			sentDate.setValue(DateUtil.getCurrentDate());
			return false;	
		}
	}

	@Override
	public void clearAttributes() {
		// reset the paid amount
		paidAmount.userSetValue(0d, this);
	}

	@Override
	public void handleModified(ModifiedEvent source) {}

}
