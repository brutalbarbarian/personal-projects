package com.lwan.eaproj.app.panes;

import javafx.scene.Node;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.eaproj.app.frames.FrameWork;
import com.lwan.eaproj.app.panes.base.PaneEditBase;
import com.lwan.eaproj.bo.cache.BOWorkCache;
import com.lwan.eaproj.bo.ref.BOWork;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.ComboBox;

public class PaneWorkEdit extends PaneEditBase<BOWork> {

	@Override
	protected void initSearchFields(ComboBox<String> cb) {
		cb.addAllItems(new String[]{"WorkID", "CustomerName"}, 
				LngUtil.translateArray(new String[] {"Work ID", "Customer Name"}));
	}

	BOWorkSetRef setWork;
	@Override
	protected void initSetLink(BOLinkEx<BOSet<BOWork>> link) {
		setWork = new BOWorkSetRef();
		setWork.ensureActive();
		
		link.setLinkedObject(setWork);
	}

	FrameWork frWork;
	@Override
	protected Node initEditPane() {
		frWork = new FrameWork(getMainLink());
		return frWork;
	}

	@Override
	public void dispose() {
		frWork.dispose();
		super.dispose();
	}
	
	protected class BOWorkSetRef extends BODbSetRef<BOWork> {
		protected BODbAttribute<Integer> workID;
		protected BODbAttribute<String> customerName;
		
		@Override
		protected void createAttributes() {
			super.createAttributes();
			
			workID = addAsChild(new BODbAttribute<Integer>(this, "WorkID", "wrk_id", AttributeType.ID));
			customerName = addAsChild(new BODbAttribute<String>(this, "CustomerName", "cus_name", AttributeType.String));
		}
		
		public BOWorkSetRef() {
			super(BOWorkCache.getCache(), DbUtil.getDbStoredProc("PS_WRK_quick_find"));
		}		
	}
}
