package com.lwan.eaproj.app.panes;

import javafx.scene.Node;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.eaproj.app.frames.FrameInvoice;
import com.lwan.eaproj.app.panes.base.PaneEditBase;
import com.lwan.eaproj.bo.cache.BOInvoiceCache;
import com.lwan.eaproj.bo.ref.BOInvoice;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.ComboBox;

public class PaneInvoiceEdit extends PaneEditBase<BOInvoice>{

	@Override
	protected void initSearchFields(ComboBox<String> cb) {
		cb.addAllItems(new String[]{"inv_Id"}, 
				LngUtil.translateArray(new String[] {"Invoice ID"}));
	}

	@Override
	protected void initSetLink(BOLinkEx<BOSet<BOInvoice>> link) {
		setInvoice = new BOInvoiceSetRef();
		setInvoice.ensureActive();
		
		link.setLinkedObject(setInvoice);
	}

	BOInvoiceSetRef setInvoice;
	FrameInvoice frInvoice;
	
	@Override
	protected Node initEditPane() {
		frInvoice = new FrameInvoice(getMainLink());
		return frInvoice;
	}

	protected class BOInvoiceSetRef extends BODbSetRef<BOInvoice> {
		BODbAttribute<Integer> invoiceID;
		
		public BOInvoiceSetRef() {
			super(BOInvoiceCache.getCache(), DbUtil.getDbStoredProc("PS_INV_quick_find"));
		}
		
		@Override
		protected void createAttributes() {
			super.createAttributes();
			
			invoiceID = addAsChild(new BODbAttribute<Integer>(this, "InvoiceID", "inv_id", AttributeType.ID));
		}
	}
}
