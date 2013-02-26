package com.lwan.finproj.bo;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.javafx.app.util.DbUtil;

public class BOTransaction extends BODbObject{
	private BODbAttribute<Integer> transactionID, sourceID;
	private BODbAttribute<Double> transactionAmount;
	private BODbAttribute<String> transactionNotes;
	private BODbAttribute<Date> transactionDate;
	private BOLink<BOSource> sourceLink;
	
	public BODbAttribute<Integer> transactionID() {
		return transactionID;
	}
	public BODbAttribute<Integer> sourceID() {
		return sourceID;
	}
	public BODbAttribute<Double> transactionAmount() {
		return transactionAmount;
	}
	public BODbAttribute<String> transactionNotes() {
		return transactionNotes;
	}
	public BODbAttribute<Date> transactionDate() {
		return transactionDate;
	}
	
	public BOSource source() {
		return sourceLink.getReferencedObject();		
	}
	
	public BOTransaction(BusinessObject owner) {
		super(owner, "Transaction");
	}

	@Override
	protected void ensureIDExists() {
		if (transactionID.isNull()) {
			transactionID.setValue(DbUtil.getNextID("trn_id"));
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends BusinessObject> T getLinkedChild(BOLink<T> link) {
		if (link == sourceLink) {
			return (T) BOSource.getSourceSet().findChildByID(sourceID.getValue());
		}
		return null;
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getStoredProc("PS_TRN"), BOTransaction.class, SP_SELECT);
		setSP(DbUtil.getStoredProc("PI_TRN"), BOTransaction.class, SP_INSERT);
		setSP(DbUtil.getStoredProc("PU_TRN"), BOTransaction.class, SP_UPDATE);
		setSP(DbUtil.getStoredProc("PD_TRN"), BOTransaction.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		transactionID = addAsChild(new BODbAttribute<Integer>(
				this, "TransactionID", "trn_id", AttributeType.Integer));
		sourceID = addAsChild(new BODbAttribute<Integer>(
				this, "SourceID", "src_id", AttributeType.Integer));
		transactionAmount = addAsChild(new BODbAttribute<Double>(
				this, "TransactionAmount", "trn_amount", AttributeType.Currency));
		transactionNotes = addAsChild(new BODbAttribute<String>(
				this, "TransactionNotes", "trn_notes", AttributeType.String));
		transactionDate = addAsChild(new BODbAttribute<Date>(
				this, "TransactionDate", "trn_date", AttributeType.Date));
		
		sourceLink = addAsChild(new BOLink<BOSource>(this, "Source"));
	}

	@Override
	public void clearAttributes() {
		transactionAmount.clear();
		transactionNotes.clear();
		transactionDate.clear();
	}

	@Override
	public void handleModified(ModifiedEvent source) {

	}
	
	private static BOTransactionSet transactionSet;
	public static BOTransactionSet getTransactionSet() {
		if (transactionSet == null) {
			transactionSet = new BOTransactionSet();
			transactionSet.ensureActive();
		}
		return transactionSet;
	}
	
	protected static class BOTransactionSet extends BODbSet<BOTransaction> {

		public BOTransactionSet() {
			super(null, "TransactionCache", "TransactionID", "trn_id");
		}

		@Override
		protected void createStoredProcs() {
			selectStoredProcProperty().setValue(DbUtil.getStoredProc("PS_TRN_for_set"));
		}

		@Override
		protected BOTransaction createChildInstance(Object id) {
			return new BOTransaction(this);
		}
		
	}
}
