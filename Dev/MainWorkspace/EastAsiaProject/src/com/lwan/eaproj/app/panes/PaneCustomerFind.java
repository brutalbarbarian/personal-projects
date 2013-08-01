package com.lwan.eaproj.app.panes;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.DbRecord;
import com.lwan.bo.db.DbRecordSet;
import com.lwan.eaproj.app.panes.base.PaneEditBase;
import com.lwan.eaproj.app.panes.base.PaneGridFind;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.controls.bo.BOCheckBox;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.scene.control.AlignedControlCell;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;

public class PaneCustomerFind extends PaneGridFind<DbRecord>{	
	DbRecordSet setCustomers;
	BOTextField tfParamName, tfParamAddress, tfParamNumber;
	BOCheckBox chkParamStudent, chkParamAllowInactive;
	AlignedControlCell accParamName, accParamAddress, accParamNumber;
	
	@Override
	protected Node initParamPane() {
		tfParamName = new BOTextField(link, "@cus_name");
		tfParamAddress = new BOTextField(link, "@cus_address");
		tfParamNumber = new BOTextField(link, "@cus_number");
		chkParamStudent = new BOCheckBox(Lng._("Student"), link, "@student");
		chkParamAllowInactive = new BOCheckBox(Lng._("Active Only"), link, "@allow_inactive");
		
		accParamName = new AlignedControlCell(Lng._("Name"), tfParamName, null);
		accParamAddress = new AlignedControlCell(Lng._("Address"), tfParamAddress, null);
		accParamNumber = new AlignedControlCell(Lng._("Number"), tfParamNumber, null);
		
		chkParamStudent.setAllowIndeterminate(true);
		
		ToolBar tb = new ToolBar(accParamName, accParamAddress, accParamNumber,
				chkParamStudent, chkParamAllowInactive);
		
		return tb;
	}

	@Override
	protected void initGridLink(BOLinkEx<BOSet<DbRecord>> gridLink) {
		setCustomers = new DbRecordSet("cus_id", DbUtil.getDbStoredProc("PS_CUS_find"));
		setCustomers.findAttributeByName("@allow_inactive").setAsObject(Boolean.FALSE);
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

	@Override
	protected void setSearchField(PaneEditBase<?> pane) {
		pane.setSearchField("CustomerID");
		pane.setParamValue(getMainLink().findAttributeByName("cus_id").asInteger());
	}

	@Override
	protected String getEditFormName() {
		return Lng._("Customer Edit");
	}

	@Override
	protected PaneEditBase<?> getNewEditForm() {
		return new PaneCustomerEdit();
	}

}
