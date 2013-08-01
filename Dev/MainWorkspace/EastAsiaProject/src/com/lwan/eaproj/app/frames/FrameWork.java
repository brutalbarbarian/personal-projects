package com.lwan.eaproj.app.frames;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.app.panes.PaneCustomerFind;
import com.lwan.eaproj.bo.ref.BOCompanySet;
import com.lwan.eaproj.bo.ref.BOProductSet;
import com.lwan.eaproj.bo.ref.BOWork;
import com.lwan.eaproj.bo.ref.BOWorkItem;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BODateEdit;
import com.lwan.javafx.controls.bo.BOTextArea;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.BOGrid;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.TBorderPane;
import com.lwan.javafx.controls.panes.THBox;
import com.lwan.javafx.controls.panes.TTitledPane;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.CollectionUtil;

public class FrameWork extends FrameBoundBase<BOWork>{
	// details	
	BOTextField tfCustomer;	// this is read only
	Button bFindCustomer;
	THBox pCustomerFind, pWorkDetail;
	
	BOTextField tfWorkID;	// or display id?
	BOComboBox<Integer> cbCompany;
	BOTextField tfUserCreated;
	BOTextField tfDateCreated;
	
	BODateEdit deDateRequired;
	BOComboBox<Integer> cbWorkStage;
	
	AlignedControlCell accWorkID, accCompany,
			accCustomer, accUserCreated, accDateCreated,
			accDateRequired, accWorkStage;
	
	// calculated
	BOTextField tfTotalValue;
	BOTextField tfRemainingValue;
	BOTextField tfPaidValue;
	AlignedControlCell accTotalValue, accRemainingValue, accPaidValue;
	
	
	THBox pTopPane;
	TVBox pDetails;
	TVBox pMoney;
	TTitledPane tpGeneral;
	TTitledPane tpMoney;
	
	protected void buildDetails() {
		BOLinkEx<BOWork> link = getMainLink();
		
		pDetails = new TVBox();
		pMoney = new TVBox();
		
		tfUserCreated = new BOTextField(link, "UserCreated.UserName");
		tfUserCreated.setEnabled(false);
		tfWorkID = new BOTextField(link, "WorkID");
		tfWorkID.setPrefColumnCount(4);
		tfWorkID.setEnabled(false);
		accUserCreated = new AlignedControlCell(Lng._("Owner"), tfUserCreated, pDetails);
		accWorkID = new AlignedControlCell(Lng._("Work ID"), tfWorkID, pDetails);
		
		
		pWorkDetail = new THBox();
		pWorkDetail.getChildren().addAll(accUserCreated, accWorkID);

		tfCustomer = new BOTextField(link, "Customer/Name");
		tfCustomer.setEnabled(false);
		accCustomer = new AlignedControlCell(Lng._("Customer"), tfCustomer, pDetails);
		
		bFindCustomer = new Button("...");
		bFindCustomer.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				Stage cus = new Stage(StageStyle.UTILITY);
				cus.initOwner(getScene().getWindow());
				cus.initModality(Modality.WINDOW_MODAL);
				cus.setTitle(Lng._("Find Customer"));
				
				PaneCustomerFind pane = new PaneCustomerFind();
				pane.setAllowSelect(true);
				try {
					Scene sc = new Scene(pane);
					sc.getStylesheets().addAll(App.getStyleshets());
					
					cus.setScene(sc);
					cus.setWidth(800);
					cus.setHeight(500);
					cus.showAndWait();
					
					if (pane.getResult()) {
						getMainLink().getLinkedObject().customerID().assign(
								pane.getMainLink().findAttributeByName("cus_id"));
					}
				} finally {
					pane.dispose();
				}
			}			
		});
		pCustomerFind = new THBox(0);
		pCustomerFind.getChildren().addAll(accCustomer, bFindCustomer);
		THBox.setHgrow(accCustomer, Priority.ALWAYS);
		
		cbCompany = new BOComboBox<>(link, "CompanyID");
		cbCompany.setEditable(true);
		cbCompany.setSource(BOCompanySet.getSet(), "CompanyID", "CompanyName", null);
		accCompany = new AlignedControlCell(Lng._("Company"), cbCompany, pDetails);
		
		tfDateCreated = new BOTextField(link, "DateCreated");
		tfDateCreated.setEnabled(false);
		deDateRequired = new BODateEdit(link, "DateRequired");
		accDateCreated = new AlignedControlCell(Lng._("Created On"), tfDateCreated, pDetails);
		accDateRequired = new AlignedControlCell(Lng._("Required By"), deDateRequired, pDetails);
		
		cbWorkStage = new BOComboBox<>(link, "workStage");
		cbWorkStage.addAllItems(CollectionUtil.getIndexArray(EAConstants.WRK_STAGE_DECLINED, 
						EAConstants.WRK_STAGE_COMPELTED), 
				LngUtil.translateArray(EAConstants.WRK_STAGE_STRINGS, EAConstants.WRK_STAGE_DECLINED, 
						EAConstants.WRK_STAGE_COMPELTED));
		accWorkStage = new AlignedControlCell(Lng._("Status"), cbWorkStage, pDetails);
		
		pDetails.getChildren().addAll(pWorkDetail, accDateCreated, pCustomerFind, accCompany, 
				accDateRequired, accWorkStage);
		
		tpGeneral = new TTitledPane(Lng._("General"), pDetails);
		tpGeneral.setContent(pDetails);
		
		tfTotalValue = new BOTextField(link, "totalValue");
		tfPaidValue = new BOTextField(link, "paidValue");
		tfRemainingValue = new BOTextField(link, "remainingValue");
		accTotalValue = new AlignedControlCell(Lng._("Total Amount"), tfTotalValue, pMoney);
		accPaidValue = new AlignedControlCell(Lng._("Paid Amount"), tfPaidValue, pMoney);
		accRemainingValue = new AlignedControlCell(Lng._("Outstanding"), tfRemainingValue, pMoney);
		
		pMoney.getChildren().addAll(accTotalValue, accPaidValue, accRemainingValue);
		
		tpMoney = new TTitledPane(Lng._("Money"), pMoney);
		tpMoney.setContent(pMoney);
		
		pTopPane = new THBox();
		pTopPane.getChildren().addAll(tpGeneral, tpMoney);
		THBox.setHgrow(tpGeneral, Priority.NEVER);
		THBox.setHgrow(tpMoney, Priority.SOMETIMES);
		
		setTop(pTopPane);
	}
	
	TabPane tpTabs;
	protected void buildTabs() {
		tpTabs = new TabPane();
		
		buildNotes();
		buildItems();
		
		tpTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tpTabs.getTabs().addAll(tabItems, tabNotes);
		
		setCenter(tpTabs);
	}
	
	Tab tabNotes;
	BOTextArea taPrivateComments;
	protected void buildNotes() {
		taPrivateComments = new BOTextArea(getMainLink(), "PrivateComment");
		
		tabNotes = new Tab(Lng._("Notes"));
		tabNotes.setContent(taPrivateComments);
	}
	
	Tab tabItems;
	ToolBar tbWorkItemBar;
	TBorderPane pWorkItems;
	BOLinkEx<BOSet<BOWorkItem>> linkWorkItems;
	BOComboBox<Integer> cbWorkItemStage;
	BOComboBox<Integer> cbWorkItemProduct;
	BOTextField tfWorkItemQuantity, tfWorkItemPrice;
	BOTextArea taWorkItemComment;
	TVBox pWorkItemDetail;
	TVBox pWorkItemPanes;
	TTitledPane tpWorkItemDetail, tpWorkItemComments;
	AlignedControlCell accWorkItemStage, accWorkItemProduct, 
			accWorkItemQuantity, accWorkItemPrice;
	
	GridView<BOWorkItem> gridWorkItems;
	
	protected void buildItems() {
		linkWorkItems = new BOLinkEx<>();
		linkWorkItems.setLinkOwner(getMainLink());
		linkWorkItems.setOwnerLinkPath("WorkItems");
		
		String[] stageDisplayStrings = LngUtil.translateArray(EAConstants.WKI_STAGE_SRINGS,
				EAConstants.WKI_STAGE_PENDING, EAConstants.WKI_STAGE_DELIVERED);
		Integer[] stageValues = CollectionUtil.getIndexArray(EAConstants.WKI_STAGE_PENDING,
				EAConstants.WKI_STAGE_DELIVERED);
		
		gridWorkItems = new GridView<>("frame_work_items", linkWorkItems, 
				new String[]{"ProductID", "Price", "Quantity", "Status"}, 
				new String[]{"Name", "Price", "Quantity", "Status"}, null);
		gridWorkItems.getGrid().getColumnByField("Status").setAsComboBox(
				stageValues, stageDisplayStrings, false);
		gridWorkItems.getGrid().getColumnByField("ProductID").setAsCombobox(
				BOProductSet.getSet(), "ProductID", "Name", true);
		gridWorkItems.getGrid().gridModeProperty().setValue(BOGrid.MODE_SET);
		
		BOLinkEx<BOWorkItem> linkWorkItem = gridWorkItems.getSelectedLink();
		
		cbWorkItemProduct = new BOComboBox<>(linkWorkItem, "productID");
		cbWorkItemProduct.setSource(BOProductSet.getSet(), "productID", "name", null);
		cbWorkItemStage = new BOComboBox<>(linkWorkItem, "status");
		cbWorkItemStage.addAllItems(stageValues, stageDisplayStrings);
		tfWorkItemPrice = new BOTextField(linkWorkItem, "price");
		tfWorkItemQuantity = new BOTextField(linkWorkItem, "quantity");
		
		pWorkItemDetail = new TVBox();
		accWorkItemProduct = new AlignedControlCell(Lng._("Product"), cbWorkItemProduct, pWorkItemDetail);
		accWorkItemQuantity = new AlignedControlCell(Lng._("Quantity"), tfWorkItemQuantity, pWorkItemDetail);
		accWorkItemStage = new AlignedControlCell(Lng._("Stage"), cbWorkItemStage, pWorkItemDetail);
		accWorkItemPrice = new AlignedControlCell(Lng._("Price"), tfWorkItemPrice, pWorkItemDetail);
		
		pWorkItemDetail.getChildren().addAll(accWorkItemProduct, accWorkItemStage, accWorkItemPrice, accWorkItemQuantity);
		tpWorkItemDetail = new TTitledPane(Lng._("Detail"), pWorkItemDetail);
		
		taWorkItemComment = new BOTextArea(linkWorkItem, "comments");
		tpWorkItemComments = new TTitledPane(Lng._("Comments"), taWorkItemComment);
		
		pWorkItemPanes = new TVBox();
		pWorkItemPanes.getChildren().addAll(tpWorkItemDetail, tpWorkItemComments);
		TVBox.setVgrow(tpWorkItemComments, Priority.ALWAYS);
		TVBox.setVgrow(tpWorkItemDetail, Priority.NEVER);
		taWorkItemComment.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		taWorkItemComment.setPrefHeight(0);
		tpWorkItemComments.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		tbWorkItemBar = new ToolBar();
		tbWorkItemBar.getItems().addAll(gridWorkItems.getGridControl().getPrimaryButton(),
				gridWorkItems.getGridControl().getSecondaryButton());
		gridWorkItems.getGridControl().newLabel = Lng._("Add Item");
		gridWorkItems.getGridControl().deleteLabel = Lng._("Remove Item");
		
		linkWorkItem.linkedObjectProperty().addListener(new ChangeListener<BOWorkItem>() {
			@Override
			public void changed(ObservableValue<? extends BOWorkItem> arg0,
					BOWorkItem arg1, BOWorkItem arg2) {
				BOCtrlUtil.buildAttributeLinks(pWorkItemDetail);
				taWorkItemComment.dataBindingProperty().buildAttributeLinks();
			}
		});
		
		pWorkItems = new TBorderPane();
		pWorkItems.setTop(tbWorkItemBar);
		pWorkItems.setLeft(gridWorkItems);
		pWorkItems.setCenter(pWorkItemPanes);
		
		tabItems = new Tab(Lng._("Items"));
		tabItems.setContent(pWorkItems);
	}
	
	@Override
	protected void buildFrame() {
		buildDetails();
		buildTabs();
	}
	
	public FrameWork(BOLinkEx<BOWork> link) {
		super(link);
	}

	@Override
	public void doDisplayState() {
		boolean isActive = isActive();
		
		bFindCustomer.setDisable(!isActive);
	}

	@Override
	public void doBuildAttributeLinks() {
		BOCtrlUtil.buildAttributeLinks(pDetails);
		BOCtrlUtil.buildAttributeLinks(pMoney);
		
		taPrivateComments.dataBindingProperty().buildAttributeLinks();
	}
}
