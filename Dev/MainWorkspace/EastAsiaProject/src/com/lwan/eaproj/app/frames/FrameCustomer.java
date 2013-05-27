package com.lwan.eaproj.app.frames;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.eaproj.bo.ref.BOContactDetail;
import com.lwan.eaproj.bo.ref.BOCustomer;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.controls.bo.BOCheckBox;
import com.lwan.javafx.controls.bo.BOTextArea;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.FxUtils;
import com.lwan.util.wrappers.Disposable;

public class FrameCustomer extends GridPane implements Disposable, ModifiedEventListener{
	private BOLinkEx<BOCustomer> link;
	private BOLinkEx<BOContactDetail> cdtLink;
	VBox pGeneral;
	VBox pDetails;
	FrameContactDetails pContactDetail;
	
	BOTextField txtFirstName, txtLastName, txtDateCreated;
	BOCheckBox chkActive, chkStudent;
	BOTextArea taNotes;
	AlignedControlCell accFirstName, accLastName, accDateCreated, accActive, accStudent;
	
	TabPane pTabs;
	Tab tNotes, tStudentDetail;
	
	public FrameCustomer(BOLinkEx<BOCustomer> link) {
		this.link = link;
		
		pGeneral = new VBox();
		
		txtFirstName = new BOTextField(link, "FirstName");
		txtLastName = new BOTextField(link, "LastName");
		txtDateCreated = new BOTextField(link, "DateCreated");
		chkActive = new BOCheckBox(Lng._("Is Active"), link, "Active");
		
		accFirstName = new AlignedControlCell(Lng._("First Name"), txtFirstName, this, 0);
		accLastName = new AlignedControlCell(Lng._("Last Name"), txtLastName, this, 0);
		accDateCreated = new AlignedControlCell(Lng._("Created on"), txtDateCreated, this, 0);
		accActive = new AlignedControlCell("", chkActive, this, 0);
		pGeneral.getChildren().addAll(accFirstName, accLastName, accDateCreated, accActive);
		
		pDetails = new VBox();
		chkStudent = new BOCheckBox(Lng._("Is Student"), link, "IsStudent");
		
		accStudent = new AlignedControlCell("", chkStudent, this, 0);
		pDetails.getChildren().addAll(accStudent);
		
		cdtLink = new BOLinkEx<>();
		pContactDetail = new FrameContactDetails(cdtLink);
		
		pTabs = new TabPane();
		pTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		tNotes = new Tab(Lng._("Notes"));
		taNotes = new BOTextArea(link, "Notes");
		tNotes.setContent(taNotes);
		
		tStudentDetail = new Tab(Lng._("Student Details"));
		
		pTabs.getTabs().addAll(tNotes, tStudentDetail);
		
		link.addListener(this);
		handleModified(null);
		
		add(pGeneral, 0, 0);
		add(pDetails, 0, 1);
		add(pContactDetail, 1, 0, 1, 2);
		add(pTabs, 0, 2, 2, 1);
		
		FxUtils.setAllColumnHGrow(this, Priority.SOMETIMES);
		setVgrow(pTabs, Priority.SOMETIMES);
	}
	
	@Override
	public void handleModified(ModifiedEvent event) {
		if (event == null || event.getType() == ModifiedEvent.TYPE_LINK) {
			doDisplayState();
		} else if (event.getType() == ModifiedEvent.TYPE_ATTRIBUTE) {
			System.out.println(event.getSource().getName());
			switch (event.getSource().getName()) {
			case "IsStudent":
				doDisplayState();
			case "Active" :
				// TODO
				// need to warn the user that this is effectively same as deleting the user
				// ... is that really what 'active' means though?
				// alternatively, warn the user when they attempt to save it instead.
			}
		}
	}
	
	protected void doDisplayState() {
		if (link.getLinkedObject() == null) {
			cdtLink.setLinkedObject(null);
			tStudentDetail.setDisable(true);
		} else {
			cdtLink.setLinkedObject(link.getLinkedObject().contactDetail());
			tStudentDetail .setDisable(!link.getLinkedObject().isStudent().getValue());
		}
		BOCtrlUtil.buildAttributeLinks(pGeneral);
		BOCtrlUtil.buildAttributeLinks(pDetails);
		taNotes.dataBindingProperty().buildAttributeLinks();
	}

	@Override
	public void dispose() {
		link.removeListener(this);
		
		cdtLink.dispose();
	}
}
