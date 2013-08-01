package com.lwan.eaproj.app.panes;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.eaproj.app.frames.FrameCustomer;
import com.lwan.eaproj.app.panes.base.PaneEditBase;
import com.lwan.eaproj.bo.cache.BOCustomerCache;
import com.lwan.eaproj.bo.ref.BOCustomer;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.ComboBox;
import javafx.scene.Node;

public class PaneCustomerEdit extends PaneEditBase<BOCustomer>{
	FrameCustomer frCustomer;
	BOCustomerSetRef setCustomerRef;
	
	@Override
	protected void initSearchFields(ComboBox<String> cb) {
		cb.addAllItems(new String[]{"CustomerID", "CustomerName", "CustomerAddress", "CustomerContact"},
				LngUtil.translateArray(new String[]{"ID", "Name", "Address", "Contact"}));
	}

	@Override
	protected void initSetLink(BOLinkEx<BOSet<BOCustomer>> link) {
		setCustomerRef = new BOCustomerSetRef(BOCustomerCache.getCache());
		link.setLinkedObject(setCustomerRef);
	}

	@Override
	protected Node initEditPane() {
		frCustomer = new FrameCustomer(getMainLink());
		return frCustomer;
	}
	
	@Override
	public void dispose() {
		setCustomerRef.dispose();
		
		super.dispose();
	} 
	
	protected class BOCustomerSetRef extends BODbSetRef<BOCustomer> {
		private BODbAttribute<Integer> customerID;
		private BODbAttribute<String> customerName;
		private BODbAttribute<String> customerAddress;
		private BODbAttribute<String> contactNumber;
		
		public BODbAttribute<Integer> customerID() {
			return customerID;
		}
		public BODbAttribute<String> customerName() {
			return customerName;
		}
		public BODbAttribute<String> customerAddress() {
			return customerAddress;
		}
		public BODbAttribute<String> contactNumber() {
			return contactNumber;
		}

		
		public BOCustomerSetRef(BOSet<BOCustomer> source) {
			super(source, DbUtil.getDbStoredProc("PS_CUS_quick_find"));
		}
		
		@Override
		protected void createAttributes() {
			super.createAttributes();
			
			customerID = addAsChild(new BODbAttribute<Integer>(this, "CustomerID", "cus_id", AttributeType.ID));
			customerName = addAsChild(new BODbAttribute<String>(this, "CustomerName", "cus_name", AttributeType.String));
			customerAddress = addAsChild(new BODbAttribute<String>(this, "CustomerAddress", "cus_address", AttributeType.String));
			contactNumber = addAsChild(new BODbAttribute<String>(this, "CustomerContact", "cus_contact", AttributeType.String));
		}
	}
}
