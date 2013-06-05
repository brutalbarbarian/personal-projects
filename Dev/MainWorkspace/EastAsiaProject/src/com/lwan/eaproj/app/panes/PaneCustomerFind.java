package com.lwan.eaproj.app.panes;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.DbRecord;
import com.lwan.bo.db.DbRecordSet;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.controls.bo.GridView;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class PaneCustomerFind extends PaneGridBase<DbRecord>{
	DbRecordSet setCustomers;

	@Override
	protected Node initEditPane() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initGridLink(BOLinkEx<BOSet<DbRecord>> gridLink) {
		setCustomers = new DbRecordSet("cus_id", DbUtil.getStoredProc("PS_CUS_find"));
		setCustomers.ensureActive();
		gridLink.setLinkedObject(setCustomers);
	}

	@Override
	protected GridView<DbRecord> constructGrid(
			BOLinkEx<BOSet<DbRecord>> gridLink) {
		GridView<DbRecord> result = new GridView<>("pane_customer_find", gridLink, 
				new String[]{"cus_name", "cdt_address", "cdt_phone", "cdt_mobile", "cus_is_active", "cus_is_student"},
				new String[]{"Name", "Address", "Phone", "Mobile", "Active", "Student"}, null);
		
		return result;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		setCustomers.dispose();
	}
}
