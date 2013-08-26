package com.lwan.eaproj.app.panes;

import javafx.geometry.Insets;
import javafx.scene.Node;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.DbRecord;
import com.lwan.bo.db.DbRecordSet;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.app.panes.base.PaneEditBase;
import com.lwan.eaproj.app.panes.base.PaneGridFind;
import com.lwan.eaproj.bo.ref.BOUserSet;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.BOCheckBox;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BODateEdit;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.TFlowPane;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.CollectionUtil;

public class PaneWorkFind extends PaneGridFind<DbRecord>{

	@Override
	protected void setSearchField(PaneEditBase<?> pane) {
		pane.setSearchField("WorkID");
		pane.setParamValue(getMainLink().findAttributeByName("wrk_id").asInteger());
	}

	@Override
	protected String getEditFormName() {
		return Lng._("Work Edit");
	}

	@Override
	protected PaneEditBase<?> getNewEditForm() {
		return new PaneWorkEdit();
	}

	TFlowPane pTop;
	BOTextField tfCustomerName, tfValueMin, tfValueMax;
	BODateEdit deDueDateStart, deDueDateEnd;
	BOComboBox<Integer> cbUser, cbStage;
	BOCheckBox chkHasOutstanding;
	
	AlignedControlCell accCustomerName, accValueMin, accValueMax,
			accDueDateStart, accDueDateEnd, accUser, accStage;
	
	@Override
	protected Node initParamPane() {
		tfCustomerName = new BOTextField(link, "@cus_name");
		tfValueMin = new BOTextField(link, "@wrk_value_min");
		tfValueMax = new BOTextField(link, "@wrk_value_max");
		deDueDateStart = new BODateEdit(link, "@wrk_date_due_start");
		deDueDateEnd = new BODateEdit(link, "@wrk_date_due_end");
		cbUser = new BOComboBox<>(link, "@usr_id_created");
		cbStage = new BOComboBox<>(link, "@wrk_stage");
		chkHasOutstanding = new BOCheckBox(Lng._("Has Outstanding"), link, "@wrk_has_outstanding");
		
		cbUser.setSource(BOUserSet.getSet(), "UserID", "UserName", Lng._("<Any>"));
		cbStage.addItem(null, Lng._("<Any>"));
		cbStage.addAllItems(CollectionUtil.getIndexArray(EAConstants.WRK_STAGE_DECLINED, 
				EAConstants.WRK_STAGE_COMPELTED), 
		LngUtil.translateArray(EAConstants.WRK_STAGE_STRINGS, EAConstants.WRK_STAGE_DECLINED, 
				EAConstants.WRK_STAGE_COMPELTED));
		
		pTop = new TFlowPane();
		
		accCustomerName = new AlignedControlCell(Lng._("Customer Name"), tfCustomerName, pTop);
		accValueMin = new AlignedControlCell(Lng._("Min Value"), tfValueMin, pTop);
		accValueMax = new AlignedControlCell(Lng._("Max Value"), tfValueMax, pTop);
		accDueDateStart = new AlignedControlCell(Lng._("Min Due Date"), deDueDateStart, pTop);
		accDueDateEnd = new AlignedControlCell(Lng._("Max Due Date"), deDueDateEnd, pTop);
		accUser = new AlignedControlCell(Lng._("Owner"), cbUser, pTop);
		accStage = new AlignedControlCell(Lng._("Stage"), cbStage, pTop);
		
		
		accUser.prefWidthProperty().bind(accCustomerName.widthProperty());
		accStage.prefWidthProperty().bind(accCustomerName.widthProperty());
		
		pTop.setPadding(new Insets(2));
		pTop.getChildren().addAll(accCustomerName, accUser, accDueDateStart, accDueDateEnd,
				accStage, accValueMin, accValueMax, chkHasOutstanding);
		
		return pTop;
	}

	DbRecordSet setRecords;
	@Override
	protected void initGridLink(BOLinkEx<BOSet<DbRecord>> gridLink) {
		setRecords = new DbRecordSet("wrk_id", DbUtil.getDbStoredProc("PS_WRK_find"));
		setRecords.ensureActive();
		
		gridLink.setLinkedObject(setRecords);
	}

	@Override
	protected GridView<DbRecord> constructGrid(
			BOLinkEx<BOSet<DbRecord>> gridLink) {
		GridView<DbRecord> result = new GridView<DbRecord>("pane_work_find",
				gridLink, new String[] {"cus_name", "usr_name_created", "wrk_date_create", "wrk_date_due", 
				"wrk_stage", "wrk_total", "wrk_outstanding"},
				LngUtil.translateArray(
				new String[] {"Customer Name", "User Created", "Created Date", "Due Date",
				"Stage", "Total", "Outstanding"}), null);
		result.getGrid().getColumnByField("wrk_stage").setAsComboBox(
				CollectionUtil.getIndexArray(EAConstants.WRK_STAGE_DECLINED, EAConstants.WRK_STAGE_COMPELTED),
				LngUtil.translateArray(
				EAConstants.WRK_STAGE_STRINGS, EAConstants.WRK_STAGE_DECLINED, EAConstants.WRK_STAGE_COMPELTED), false);
		
		return result;
	}

	@Override
	protected String getChildName() {
		return Lng._("Work");
	}

}
