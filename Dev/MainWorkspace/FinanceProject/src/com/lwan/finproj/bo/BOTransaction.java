package com.lwan.finproj.bo;

import java.util.Date;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOException;
import com.lwan.bo.BOLink;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.DateUtil;

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
		BusinessObject result = null;
		if (link == sourceLink) {
			result = isExample() ? BOSource.getSourceSet().getExampleChild() : 
					BOSource.getSourceSet().findChildByID(sourceID.getValue());
		}
		return (T)result;
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getStoredProc("PS_TRN"), BOTransaction.class, SP_SELECT);
		setSP(DbUtil.getStoredProc("PI_TRN"), BOTransaction.class, SP_INSERT);
		setSP(DbUtil.getStoredProc("PU_TRN"), BOTransaction.class, SP_UPDATE);
		setSP(DbUtil.getStoredProc("PD_TRN"), BOTransaction.class, SP_DELETE);
	}
	
	protected void verifyState() throws BOException{
		super.verifyState();

		// check that the source actually exists
		BOSource src = BOSource.getSourceSet().findChildByID(sourceID().getValue());
			
		if (src == null) {
			throw new BOException("Souce cannot be empty", sourceID());
		}
		if (src.isModified()) {
			// Make sure the src is saved prior to attempting to save this.
			src.trySave();
		}
	}

	@Override
	protected void createAttributes() {
		transactionID = addAsChild(new BODbAttribute<Integer>(
				this, "TransactionID", "trn_id", AttributeType.ID));
		sourceID = addAsChild(new BODbAttribute<Integer>(
				this, "SourceID", "src_id", AttributeType.ID));
		transactionAmount = addAsChild(new BODbAttribute<Double>(
				this, "TransactionAmount", "trn_amount", AttributeType.Currency));
		transactionNotes = addAsChild(new BODbAttribute<String>(
				this, "TransactionNotes", "trn_notes", AttributeType.String));
		transactionDate = addAsChild(new BODbAttribute<Date>(
				this, "TransactionDate", "trn_date", AttributeType.Date));
		
		sourceLink = addAsChild(new BOLink<BOSource>(this, "Source"));
	}
	
	protected void handleActive(boolean active) {		
		super.handleActive(active);
		
//		BOSource src = source();
//		if (src != null) {			
//			src.transactionCount().setValue(
//					src.transactionCount().asInteger() + (active? 1 : -1));
//		}
	}
	
	@Override
	public void clearAttributes() {
		transactionAmount.clear();
		transactionNotes.clear();
		transactionDate.setValue(DateUtil.getCurrentDate());
	}

	@Override
	public void handleModified(ModifiedEvent source) {
//		if (isActive()) {
//			if (source.getType() == ModifiedEvent.TYPE_ATTRIBUTE && 
//					source.getSource() == sourceID) {
//				// how do i find out the previous value?...
//				BOSource prevSrc = BOSource.getSourceSet().findChildByID(
//						sourceID.previousValueProperty().getValue());
//				if (prevSrc != null) {
//					prevSrc.transactionCount().setValue(
//							prevSrc.transactionCount().asInteger() - 1);
//				}
//				
//				BOSource src = source();
//				if (src != null) {
//					src.transactionCount().setValue(
//							prevSrc.transactionCount().asInteger() + 1);
//				}
//			}
//		}
	}
	
	private static BOTransactionSet transactionSet;
	public static BOTransactionSet getTransactionSet() {
		if (transactionSet == null) {
			transactionSet = new BOTransactionSet();
			transactionSet.ensureActive();
		}
		return transactionSet;
	}
	
	public static class BOTransactionSet extends BODbSet<BOTransaction> {
		
		private BOTransactionSet() {
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
