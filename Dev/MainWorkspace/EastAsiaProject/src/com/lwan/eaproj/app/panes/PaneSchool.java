package com.lwan.eaproj.app.panes;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.app.EAUtils;
import com.lwan.eaproj.app.frames.FrameContactDetails;
import com.lwan.eaproj.bo.ref.BOContactDetail;
import com.lwan.eaproj.bo.ref.BOSchool;
import com.lwan.eaproj.bo.ref.BOSchoolSet;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.BOTextArea;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.CollectionUtil;
import com.lwan.util.FxUtils;
import com.lwan.util.StringUtil;
import com.lwan.util.wrappers.CallbackEx;

public class PaneSchool extends PaneGridBase<BOSchool>{
	GridPane editPane;
	BOTextField tfSchoolName, tfContactName;
	BOTextArea taNotes;
	FrameContactDetails frContactDetails;
	
	AlignedControlCell accSchoolName, accContactName;
	VBox paneGeneral, paneContact;
	
	BOLinkEx<BOContactDetail> linkCDT;
	
	
	@Override
	protected Node initEditPane() {
		editPane = new GridPane();
		
		paneGeneral = new VBox();
		tfSchoolName = new BOTextField(getSelectedLink(), "SchoolName");
		taNotes = new BOTextArea(getSelectedLink(), "Notes");
		accSchoolName = new AlignedControlCell(Lng._("School Name"), tfSchoolName, paneGeneral);
		paneGeneral.getChildren().addAll(accSchoolName, new Label(Lng._("Notes")), taNotes);
		VBox.setVgrow(taNotes, Priority.SOMETIMES);
		
		
		paneContact = new VBox();
		tfContactName = new BOTextField(getSelectedLink(), "ContactName");
		accContactName = new AlignedControlCell(Lng._("Contact"), tfContactName, paneContact, 0);
		linkCDT = new BOLinkEx<>();
		frContactDetails = new FrameContactDetails(paneContact, linkCDT);
		paneContact.getChildren().addAll(accContactName, frContactDetails);
		
		editPane.add(paneGeneral, 0, 0);
		editPane.add(paneContact, 1, 0);
		
		FxUtils.setAllColumnHGrow(editPane, Priority.SOMETIMES);
		
		return editPane;
	}
	
	@Override
	protected void onNewSelection(BOSchool selection) {
		super.onNewSelection(selection);
		if (selection == null) {
			linkCDT.setLinkedObject(null);
		} else {
			linkCDT.setLinkedObject(selection.contactDetail());
		}
	}

	@Override
	protected void initGridLink(BOLinkEx<BOSet<BOSchool>> gridLink) {
		gridLink.setLinkedObject(BOSchoolSet.getSchoolSet());
	}

	@Override
	protected GridView<BOSchool> constructGrid(
			BOLinkEx<BOSet<BOSchool>> gridLink) {
		GridView<BOSchool> result = new GridView<>("pane_school", gridLink, 
				CollectionUtil.concatArrays(new String[]{"SchoolName", "ContactName", "Notes"}, 
						EAUtils.getContactDetailFields("ContactDetail.")),
				LngUtil.translateArray(CollectionUtil.concatArrays(new String[]{"Name", "Contact", "Notes"},
						EAConstants.CONTACT_DISPLAY_STRINGS)),
				null);
		result.getGrid().setDisplayValueCallback(new CallbackEx<BOSchool, String, String>() {
			public String call(BOSchool school, String field) {
				BOContactDetail cdt = school.contactDetail();
				return StringUtil.getDelimitedString(", ", 
						cdt.address1().getValue(), cdt.address2().getValue(),
						cdt.address3().getValue());
			}
		});
		
		return result;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		linkCDT.dispose();
	}

	@Override
	protected Node initParamPane() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
