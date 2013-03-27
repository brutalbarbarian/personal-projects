package com.lwan.finproj.bo;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbObject;
import com.lwan.bo.db.BODbSet;
import com.lwan.finproj.bo.BOTransaction.BOTransactionSet;
import com.lwan.javafx.app.util.DbUtil;

public class BOSource extends BODbObject{
	private BODbAttribute<Integer> sourceID;
	private BODbAttribute<String> sourceName;
	private BOAttribute<Integer> transactionCount;

	public BODbAttribute<Integer> sourceID() {
		return sourceID;
	}
	public BODbAttribute<String> sourceName() {
		return sourceName;
	}
	public BOAttribute<Integer> transactionCount() {
		return transactionCount;
	}
	
	public BOSource(BusinessObject owner) {
		super(owner, "Source");	
	}

	@Override
	protected void ensureIDExists() {
		if (sourceID.isNull()) {
			sourceID.setValue(DbUtil.getNextID(sourceID.fieldNameProperty().getValue()));
		}
	}

	@Override
	protected void createStoredProcs() {
		setSP(DbUtil.getStoredProc("PS_SRC"), BOSource.class, SP_SELECT);
		setSP(DbUtil.getStoredProc("PI_SRC"), BOSource.class, SP_INSERT);
		setSP(DbUtil.getStoredProc("PU_SRC"), BOSource.class, SP_UPDATE);
		setSP(DbUtil.getStoredProc("PD_SRC"), BOSource.class, SP_DELETE);
	}

	@Override
	protected void createAttributes() {
		sourceID = addAsChild(new BODbAttribute<Integer>(this, "SourceID", "src_id", AttributeType.Integer));
		sourceName = addAsChild(new BODbAttribute<String>(this, "SourceName", "src_name", AttributeType.String));
		
		transactionCount = addAsChild(new BOAttribute<Integer>(this, "TransactionCount", AttributeType.Integer));
		transactionCount.triggersModifyProperty().setValue(false);
	}

	@Override
	public void clearAttributes() {
		sourceName.clear();
		transactionCount.setValue(0);
	}

	@Override
	public void handleModified(ModifiedEvent source) {

	}
	
	private static BOSourceSet sourceSet;
	public static BOSourceSet getSourceSet() {
		if (sourceSet == null) {
			sourceSet = new BOSourceSet();
			sourceSet.ensureActive();
		}
		return sourceSet;
	}
	
	public static class BOSourceSet extends BODbSet<BOSource> {

		private BOSourceSet() {
			super(null, "SourceCache", "SourceID", "src_id");
		}

		@Override
		protected void createStoredProcs() {
			selectStoredProcProperty().setValue(DbUtil.getStoredProc("PS_SRC_for_set"));
		}

		@Override
		protected BOSource createChildInstance(Object id) {
			return new BOSource(this);
		}
		
	}
}
