package com.lwan.eaproj.app.frames;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventType;
import com.lwan.eaproj.bo.ref.BOContactDetail;
import com.lwan.eaproj.bo.ref.BOCustomer;
import com.lwan.eaproj.bo.ref.BOSchoolSet;
import com.lwan.eaproj.bo.ref.BOStudent;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.BOCheckBox;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BODateEdit;
import com.lwan.javafx.controls.bo.BOTextArea;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.BOGrid;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.TBorderPane;
import com.lwan.javafx.controls.panes.TGridPane;
import com.lwan.javafx.controls.panes.THBox;
import com.lwan.javafx.controls.panes.TTitledPane;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.javafx.scene.control.AlignedControlCell;

public class FrameCustomer extends FrameBoundBase<BOCustomer> {
	private BOLinkEx<BOContactDetail> linkCDT;
	TVBox pDetail;
	THBox pMain;
	FrameContactDetails pContactDetail;
	TTitledPane tpGeneral, tpContactDetail;
	
	BOTextField tfCustomerID, tfName, tfDateCreated;
	BOCheckBox chkStudent;
	AlignedControlCell accCustomerID, accName, accDateCreated, accStudent;
	
	TabPane pTabs;
	Tab tNotes, tStudentDetail;
	
	protected void buildMainDetail() {
		BOLinkEx<BOCustomer> link = getMainLink();
		
		pDetail = new TVBox();
		
		tfCustomerID = new BOTextField(link, "CustomerID");
		tfName = new BOTextField(link, "Name");
		tfDateCreated = new BOTextField(link, "DateCreated");
		chkStudent = new BOCheckBox(Lng._("Is Student"), link, "IsStudent");
		
		tfDateCreated.setEnabled(false);
		tfCustomerID.setEnabled(false);
		
		accCustomerID = new AlignedControlCell(Lng._("ID"), tfCustomerID, this, 0);
		accName = new AlignedControlCell(Lng._("Name"), tfName, this, 0);
		accDateCreated = new AlignedControlCell(Lng._("Created on"), tfDateCreated, this, 1);
		accStudent = new AlignedControlCell("", chkStudent, this, 0);
		
		THBox pGeneralLine = new THBox(5);
		pGeneralLine.getChildren().addAll(accDateCreated, accCustomerID);
		pDetail.getChildren().addAll(pGeneralLine, accName, accStudent);
		
		tfCustomerID.setPrefColumnCount(3);
		THBox.setHgrow(accCustomerID, Priority.NEVER);
		accCustomerID.minWidthProperty().bind(accCustomerID.widthProperty());
		
		linkCDT = new BOLinkEx<>();
		linkCDT.setLinkOwner(link);
		linkCDT.setOwnerLinkPath("ContactDetail");
		pContactDetail = new FrameContactDetails(linkCDT);
		
		pMain = new THBox();
		
		tpGeneral = new TTitledPane(Lng._("General"), pDetail);
		tpContactDetail = new TTitledPane(Lng._("Contact Details"), pContactDetail);
		
		pMain.getChildren().addAll(tpGeneral, tpContactDetail);
		
		THBox.setHgrow(tpContactDetail, Priority.SOMETIMES);
		THBox.setHgrow(tpGeneral, Priority.SOMETIMES);
		
		pMain.setPadding(new Insets(5));
		
		// the tabs should be the priority when expanding.
		setTop(pMain);
	}
	
	protected void buildTabs() {
		pTabs = new TabPane();
		pTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		tNotes = new Tab(Lng._("Notes"));
		tStudentDetail = new Tab(Lng._("Student Details"));
		
		buildNotesTab(tNotes);
		buildStudentsTab(tStudentDetail);
		
		pTabs.getTabs().addAll(tNotes, tStudentDetail);
		
		setCenter(pTabs);
	}
	
	BOTextArea taNotes;
	protected void buildNotesTab(Tab tab) {
		taNotes = new BOTextArea(getMainLink(), "Notes");
		tNotes.setContent(taNotes);
		
		tab.setContent(taNotes);
	}
	
	BOLinkEx<BOSet<BOStudent>> linkStudents;
	GridView<BOStudent> gridStudents;
	TBorderPane pStudentsMain;
	ToolBar tbStudents;
	BOTextArea taStudentNotes;
	BODateEdit deStudentStartDate, deStudentEndDate;
	BOComboBox<Integer> cbStudentSchool;
	AlignedControlCell accStudentStartDate, accStudentEndDate, accStudentSchool;
	TGridPane pStudents;
	TVBox pStudentDetails;
	TVBox pStudentMainDetails;
	TTitledPane tpStudentDetails, tpStudentNotes;
	
	protected void buildStudentsTab(Tab tab) {
		linkStudents = new BOLinkEx<>();
		linkStudents.setLinkOwner(getMainLink());
		linkStudents.setOwnerLinkPath("Students");
		
		pStudentsMain = new TBorderPane();
		gridStudents = new GridView<>("frame_customer_students", linkStudents,
				new String[]{"SchoolID", "StartDate", "EndDate", "Notes"}, 
				LngUtil.translateArray(new String[]{
						"School", "Start Date", "End Date", "Notes"}),
				null);
		gridStudents.getGrid().gridModeProperty().setValue(BOGrid.MODE_SET);
		gridStudents.getGrid().setEditable(true);
		gridStudents.getGrid().getColumnByField("SchoolID").setAsCombobox(BOSchoolSet.getSchoolSet(), "schoolID", "schoolName", true);
		gridStudents.getGrid().getColumnByField("StartDate").setAsDatePicker();
		gridStudents.getGrid().getColumnByField("EndDate").setAsDatePicker();
		
		gridStudents.getGridControl().newLabel = Lng._("Add Enrollment");
		gridStudents.getGridControl().deleteLabel = Lng._("Remove");
			
		tbStudents = new ToolBar();
		tbStudents.getItems().addAll(gridStudents.getGridControl().getPrimaryButton(),
				gridStudents.getGridControl().getSecondaryButton());
		
		BOLinkEx<BOStudent> linkStudent = gridStudents.getSelectedLink();
		linkStudent.linkedObjectProperty().addListener(new ChangeListener<BOStudent>() {
			public void changed(ObservableValue<? extends BOStudent> arg0,
					BOStudent arg1, BOStudent arg2) {
				BOCtrlUtil.buildAttributeLinks(pStudentMainDetails);
				taStudentNotes.dataBindingProperty().buildAttributeLinks();
			}			
		});
		pStudentMainDetails = new TVBox();
		
		deStudentStartDate = new BODateEdit(linkStudent, "StartDate");
		deStudentEndDate = new BODateEdit(linkStudent, "EndDate");
		cbStudentSchool = new BOComboBox<>(linkStudent, "SchoolID");
		taStudentNotes = new BOTextArea(linkStudent, "Notes");
		
		cbStudentSchool.setSource(BOSchoolSet.getSchoolSet(), "schoolID", "schoolName", null);
		cbStudentSchool.setEditable(true);
		
		accStudentStartDate = new AlignedControlCell(Lng._("Start Date"), deStudentStartDate, pStudentMainDetails);
		accStudentEndDate = new AlignedControlCell(Lng._("End Date"), deStudentEndDate, pStudentMainDetails);
		accStudentSchool = new AlignedControlCell(Lng._("School"), cbStudentSchool, pStudentMainDetails);
		
		pStudentMainDetails.getChildren().addAll(accStudentSchool, accStudentStartDate, accStudentEndDate);
		
		tpStudentDetails = new TTitledPane(Lng._("Details"), pStudentMainDetails);
		tpStudentNotes = new TTitledPane(Lng._("Notes"), taStudentNotes);
		
		pStudentDetails = new TVBox();
		pStudentDetails.getChildren().addAll(tpStudentDetails, tpStudentNotes);
		
		TVBox.setVgrow(tpStudentNotes, Priority.ALWAYS);
		tpStudentNotes.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		pStudents = new TGridPane();
		pStudents.add(gridStudents, 0, 0);
		pStudents.add(pStudentDetails, 1, 0);
		TGridPane.setHgrow(pStudentDetails, Priority.ALWAYS);
		TGridPane.setVgrow(gridStudents, Priority.ALWAYS);
		TGridPane.setVgrow(pStudentDetails, Priority.ALWAYS);
		
		pStudentsMain.setCenter(pStudents);
		pStudentsMain.setTop(tbStudents);
		
		tab.setContent(pStudentsMain);
	}
	
	protected void buildFrame() {
		buildMainDetail();
		buildTabs();
	}
	
	public FrameCustomer(BOLinkEx<BOCustomer> link) {
		super(link);
	}
	
	@Override
	public void handleModified(ModifiedEvent event) {
		super.handleModified(event);
		
		if (event != null) {
			if (event.getType() == ModifiedEventType.Attribute) {
				switch (event.getSource().getName()) {
				case "IsStudent":
					doDisplayState();
				case "Active" :
					// Do nothing...
				}
			}
		}
	}
	
	@Override
	public boolean isActive() {
		return super.isActive() && getMainLink().getLinkedObject().active().asBoolean();
	}
	
	public void doDisplayState() {
		boolean isActive = isActive();
		boolean isStudent;
		
		if (getMainLink().getLinkedObject() == null) {
			isStudent = false;
		} else {
			isStudent = getMainLink().getLinkedObject().isStudent().asBoolean();
		}
		
		tStudentDetail.setDisable(!isStudent);
		taNotes.setEnabled(isActive);
		taStudentNotes.setEnabled(isActive);
		tfName.setEnabled(isActive);
		cbStudentSchool.setEnabled(isActive);
		deStudentEndDate.setEnabled(isActive);
		deStudentStartDate.setEnabled(isActive);
		gridStudents.getGrid().setEditable(isActive);
		gridStudents.getGridControl().setEditable(isActive);
	}
	
	public void doBuildAttributeLinks() {
		BOCtrlUtil.buildAttributeLinks(pDetail);
		taNotes.dataBindingProperty().buildAttributeLinks();
		
//		doDisplayState();
	}

	@Override
	public void dispose() {
		super.dispose();
		
		linkStudents.dispose();
		linkCDT.dispose();
		
		gridStudents.dispose();
	}
}
