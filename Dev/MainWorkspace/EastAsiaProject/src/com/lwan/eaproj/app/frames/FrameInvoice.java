package com.lwan.eaproj.app.frames;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BOSetRef;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.ModifiedEventType;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.bo.ref.BOInvoice;
import com.lwan.eaproj.bo.ref.BOInvoiceItem;
import com.lwan.eaproj.bo.ref.BOWorkItem;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.art.RecordNavigationArt;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BOTextArea;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.THBox;
import com.lwan.javafx.controls.panes.TTitledPane;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.CollectionUtil;

public class FrameInvoice extends FrameBoundBase<BOInvoice>{
	// details
	BOTextField tfInvoiceID, tfWorkID, tfCustomerName;
	BOTextField tfUserCreated, tfDateCreated;
	BOComboBox<Integer> cbInvoiceStage;
	
	AlignedControlCell accInvoiceID, accWorkID, accCustomerName,
			accUserCreated, accDateCreated, accInvoiceStage;
	
	TVBox pDetails;
	
	BOTextField tfTotalValue;
	
	AlignedControlCell accTotalValue;
	TVBox pFinance;
	
	TTitledPane tpDetails, tpFinance;
	
	THBox pMain; 
	
	protected void buildDetails() {
		BOLinkEx<BOInvoice> link = getMainLink();
		
		pDetails = new TVBox();
		
		tfInvoiceID = new BOTextField(link, "InvoiceID");
		tfWorkID = new BOTextField(link, "WorkID");
		tfCustomerName = new BOTextField(link, "Work/Customer/Name");
		tfUserCreated = new BOTextField(link, "UserCreated/UserName");
		tfDateCreated = new BOTextField(link, "DateCreated");
		cbInvoiceStage = new BOComboBox<>(link, "Stage");
		
		tfInvoiceID.setEnabled(false);
		tfWorkID.setEnabled(false);
		tfCustomerName.setEnabled(false);
		tfUserCreated.setEnabled(false);
		tfDateCreated.setEnabled(false);
		
		cbInvoiceStage.addAllItems(CollectionUtil.getIndexArray(EAConstants.INV_STAGE_CREDITED, 
						EAConstants.INV_STAGE_COMPLETED), 
				LngUtil.translateArray(EAConstants.INV_STAGE_STRINGS, EAConstants.INV_STAGE_CREDITED, 
						EAConstants.INV_STAGE_COMPLETED));
		
		accInvoiceID = new AlignedControlCell(Lng._("Invoice ID"), tfInvoiceID, pDetails);
		accWorkID = new AlignedControlCell(Lng._("Work ID"), tfWorkID, pDetails);
		accCustomerName = new AlignedControlCell(Lng._("Customer"), tfCustomerName, pDetails);
		accUserCreated = new AlignedControlCell(Lng._("Created By"), tfUserCreated, pDetails);
		accDateCreated = new AlignedControlCell(Lng._("Date Created"), tfDateCreated, pDetails);
		accInvoiceStage = new AlignedControlCell(Lng._("Stage"), cbInvoiceStage, pDetails);
		
		pDetails.getChildren().addAll(accInvoiceID, accWorkID,
				accCustomerName, accUserCreated, accDateCreated, accInvoiceStage);
		
		tpDetails = new TTitledPane(Lng._("Details"), pDetails);		
		
		pFinance = new TVBox();
			
		tfTotalValue = new BOTextField(link, "TotalValue");
		tfTotalValue.setEnabled(false);
		
		accTotalValue = new AlignedControlCell(Lng._("Total Value"), tfTotalValue, pFinance);
		
		pFinance.getChildren().addAll(accTotalValue);
		
		tpFinance = new TTitledPane(Lng._("Finances"), pFinance);
		
		pMain = new THBox();
		pMain.getChildren().addAll(tpDetails, tpFinance);
		THBox.setHgrow(tpDetails, Priority.SOMETIMES);
		THBox.setHgrow(tpFinance, Priority.SOMETIMES);
		
		setTop(pMain);
	}
	
	TabPane tpTabs;
	protected void buildTabs() {
		tpTabs = new TabPane();
		
		buildTabNotes();
		buildTabItems();
		
		tpTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tpTabs.getTabs().addAll(tabItems, tabNotes);
		
		setCenter(tpTabs);
	}
	
	Tab tabNotes;
	BOTextArea taComments;
	protected void buildTabNotes() {
		taComments = new BOTextArea(getMainLink(), "Comments");
		
		tabNotes = new Tab(Lng._("Notes"));
		tabNotes.setContent(taComments);
	}
	
	Tab tabItems;
	ToolBar tbInvoiceItemBar;
	SplitPane pInvoiceItems;
	
	TVBox pWorkItem, pInvoiceItem;
	TVBox pWorkItemDetails, pInvoiceItemDetails;
	THBox pItemGrids, pItemDetails;
	TVBox pItemButtons;
	
	BOTextField tfWorkItemProductName;
	BOTextField tfWorkItemPrice;
	BOTextField tfWorkItemAvaliable;
	BOComboBox<Integer> cbWorkItemStage;
	
	BOTextArea taWorkItemComments;
	
	AlignedControlCell accWorkItemProductName;
	AlignedControlCell accWorkItemAvaliable;
	AlignedControlCell accWorkItemPrice;
	AlignedControlCell accWorkItemStage;
	
	TTitledPane tpWorkItemDetails;
	TTitledPane tpWorkItemComments;
	
	BOTextField tfInvoiceItemQuantity;
	BOTextField tfInvoiceItemPrice;	// override price
	
	AlignedControlCell accInvoiceItemQuantity;
	AlignedControlCell accInvoiceItemPrice;
	
	BOTextArea taInvoiceItemComments;
	
	TTitledPane tpInvoiceItemDetails;
	TTitledPane tpInvoiceItemComments;
	
	BOLinkEx<BOWorkItem> linkDisplayWorkItem;
	BOLinkEx<BOSet<BOWorkItem>> linkWorkItems;
	BOLinkEx<BOSet<BOInvoiceItem>> linkInvoiceItems;
	
	GridView<BOWorkItem> gridWorkItems;
	GridView<BOInvoiceItem> gridInvoiceItems;
	
	Button bAddInvoiceItem;
	Button bRemoveInvoiceItem;
	
	BOSetRef<BOWorkItem> setWorkItems;
	
	Callback<BOWorkItem, Boolean> callbackWorkItem;
	
	protected void buildTabItems() {
		String[] stageDisplayStrings = LngUtil.translateArray(EAConstants.WKI_STAGE_SRINGS,
				EAConstants.WKI_STAGE_PENDING, EAConstants.WKI_STAGE_DELIVERED);
		Integer[] stageValues = CollectionUtil.getIndexArray(EAConstants.WKI_STAGE_PENDING,
				EAConstants.WKI_STAGE_DELIVERED);
		
		callbackWorkItem = new Callback<BOWorkItem, Boolean>() {
			public Boolean call(BOWorkItem item) {
				BOSet<BOInvoiceItem> set = linkInvoiceItems.getLinkedObject();
				return set == null || 
						set.findChildByAttribute("WorkItemID", item.workItemID().getValue()) == null;
			}
		};
		linkWorkItems = new BOLinkEx<>();
		linkWorkItems.setLinkOwner(getMainLink());
		linkWorkItems.setOwnerLinkPath("Work/WorkItems");
		linkWorkItems.setSetRefCallback(new Callback<BOSet<?>, BOSet<?>>() {
			@SuppressWarnings("unchecked")
			public BOSet<?> call(BOSet<?> arg0) {
				BOSetRef<?> set = BOSetRef.createFilteredSet((BOSet<BOWorkItem>)arg0, callbackWorkItem);
				set.ensureActive();
				return set;
			}
		});
		
		linkDisplayWorkItem = new BOLinkEx<>();
		
		bAddInvoiceItem = new Button(null, new RecordNavigationArt(16, 16, RecordNavigationArt.NAV_NEXT));
		bRemoveInvoiceItem = new Button(null, new RecordNavigationArt(16, 16, RecordNavigationArt.NAV_PREV));
		bAddInvoiceItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				addInvoiceItem();
			}			
		});
		bRemoveInvoiceItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				removeInvoiceItem();
			}			
		});
		
		linkInvoiceItems = new BOLinkEx<>();
		linkInvoiceItems.setLinkOwner(getMainLink());
		linkInvoiceItems.setOwnerLinkPath("InvoiceItems");
		
		gridWorkItems = new GridView<>("frame_invoice_work_items", linkWorkItems, 
				new String[]{"Product/Name", "AvaliableQuantity", "Status"}, 
				LngUtil.translateArray(new String[]{"Product", "Avaliable", "Stage"}), null);
		gridWorkItems.getGrid().setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.PRIMARY &&
						e.getClickCount() == 2) {
					addInvoiceItem();
				}
			}			
		});
		gridWorkItems.getSelectedLink().addListener(new ModifiedEventListener() {
			public void handleModified(ModifiedEvent event) {
				if (event.getType() == ModifiedEventType.Link) {
					linkDisplayWorkItem.setLinkedObject(gridWorkItems.getSelectedLink().getLinkedObject());
					doBuildAttributeLinks();
					doDisplayState();
				}
			}
		});
		gridWorkItems.getGrid().getColumnByField("Status").setAsComboBox(stageValues, stageDisplayStrings, false);
		
		gridInvoiceItems = new GridView<>("frame_invoice_invoice_items", linkInvoiceItems,
				new String[]{"WorkItem/Product/Name", "WorkItem/AvaliableQuantity", "WorkItem/Status", "Quantity"},
				LngUtil.translateArray(new String[]{"Product", "Avaliable", "Stage", "Quantity"}), null);
		gridInvoiceItems.getGrid().setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.PRIMARY &&
						e.getClickCount() == 2) {
					removeInvoiceItem();
				}
				
			}
		});
		gridInvoiceItems.getSelectedLink().addListener(new ModifiedEventListener() {
			public void handleModified(ModifiedEvent event) {
				if (event.getType() == ModifiedEventType.Link) {
					BOInvoiceItem item = gridInvoiceItems.getSelectedLink().getLinkedObject();
					if (item != null) {
						linkDisplayWorkItem.setLinkedObject(item.workItem());
					} else {
						linkDisplayWorkItem.setLinkedObject(null);
					}
					doBuildAttributeLinks();
					doDisplayState();
				}
			}
		});
		gridWorkItems.getSelectedLink().addListener(new ModifiedEventListener() {
			public void handleModified(ModifiedEvent event) {
				if (event.getType() == ModifiedEventType.Link) {
					linkDisplayWorkItem.setLinkedObject(gridWorkItems.getSelectedLink().getLinkedObject());
					
					doBuildAttributeLinks();
					doDisplayState();
				}
			}			
		});
		
		gridInvoiceItems.getGrid().focusedProperty().addListener(new ChangeListener<Boolean> () {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					BOInvoiceItem item = gridInvoiceItems.getSelectedLink().getLinkedObject();
					if (item != null) {
						linkDisplayWorkItem.setLinkedObject(item.workItem());
						doBuildAttributeLinks();
						doDisplayState();
					}
				}
			}			
		});
		gridWorkItems.getGrid().focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					BOWorkItem item = gridWorkItems.getGrid().getSelectionModel().getSelectedItem();
					linkDisplayWorkItem.setLinkedObject(item);
					doBuildAttributeLinks();
					doDisplayState();
				}
			}			
		});
		
		gridInvoiceItems.getGrid().getColumnByField("WorkItem/Status").setAsComboBox(stageValues, stageDisplayStrings, false);
		
		pItemButtons = new TVBox();
		pItemButtons.getChildren().addAll(bAddInvoiceItem, bRemoveInvoiceItem);
		
		pItemGrids = new THBox();
		pItemGrids.getChildren().addAll(gridWorkItems, pItemButtons, gridInvoiceItems);
		THBox.setHgrow(gridWorkItems, Priority.ALWAYS);
		THBox.setHgrow(gridInvoiceItems, Priority.ALWAYS);
		pItemButtons.setAlignment(Pos.CENTER);
		
		pWorkItemDetails = new TVBox();
		
		tfWorkItemProductName = new BOTextField(linkDisplayWorkItem, "Product/Name");
		tfWorkItemPrice = new BOTextField(linkDisplayWorkItem, "Price");
		tfWorkItemAvaliable = new BOTextField(linkDisplayWorkItem, "AvaliableQuantity");
		cbWorkItemStage = new BOComboBox<>(linkDisplayWorkItem, "Status");
		cbWorkItemStage.setEditable(true);	// just to make it look nicer
		cbWorkItemStage.addAllItems(stageValues, stageDisplayStrings);
		
		tfWorkItemProductName.setEnabled(false);
		tfWorkItemPrice.setEnabled(false);
		tfWorkItemAvaliable.setEnabled(false);
		cbWorkItemStage.setEnabled(false);
		
		accWorkItemProductName = new AlignedControlCell(Lng._("Product"), tfWorkItemProductName, pWorkItemDetails);
		accWorkItemPrice = new AlignedControlCell(Lng._("Price"), tfWorkItemPrice, pWorkItemDetails);
		accWorkItemAvaliable = new AlignedControlCell(Lng._("Avaliable"), tfWorkItemAvaliable, pWorkItemDetails);
		accWorkItemStage = new AlignedControlCell(Lng._("Stage"), cbWorkItemStage, pWorkItemDetails);
		
		pWorkItemDetails.getChildren().addAll(accWorkItemProductName, accWorkItemPrice,
				accWorkItemAvaliable, accWorkItemStage);
		
		tpWorkItemDetails = new TTitledPane(Lng._("Work Item Details"), pWorkItemDetails);

		taWorkItemComments = new BOTextArea(linkDisplayWorkItem, "Comments");
		taWorkItemComments.setEnabled(false);
		taWorkItemComments.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		taWorkItemComments.setMinSize(0, 0);
		
		tpWorkItemComments = new TTitledPane(Lng._("Work Item Comments"), taWorkItemComments);
		
		pWorkItem = new TVBox();
		pWorkItem.getChildren().addAll(tpWorkItemDetails, tpWorkItemComments);
		TVBox.setVgrow(tpWorkItemComments, Priority.ALWAYS);
		
		pInvoiceItemDetails = new TVBox();
		
		tfInvoiceItemPrice = new BOTextField(gridInvoiceItems.getSelectedLink(), "Price");
		tfInvoiceItemQuantity = new BOTextField(gridInvoiceItems.getSelectedLink(), "Quantity");
		
		accInvoiceItemPrice = new AlignedControlCell(Lng._("Price"), tfInvoiceItemPrice, pInvoiceItemDetails);
		accInvoiceItemQuantity = new AlignedControlCell(Lng._("Quantity"), tfInvoiceItemQuantity, pInvoiceItemDetails);
		
		pInvoiceItemDetails.getChildren().addAll(accInvoiceItemPrice, accInvoiceItemQuantity);
		
		tpInvoiceItemDetails = new TTitledPane(Lng._("Invoice Item Details"), pInvoiceItemDetails);
		
		taInvoiceItemComments = new BOTextArea(gridInvoiceItems.getSelectedLink(), "Comments");
		taInvoiceItemComments.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		taInvoiceItemComments.setMinSize(0, 0);
		
		tpInvoiceItemComments = new TTitledPane(Lng._("Invoice Item Comments"), taInvoiceItemComments);
		
		tpWorkItemComments.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		tpWorkItemComments.setMinHeight(0);
		tpInvoiceItemComments.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		tpInvoiceItemComments.setMinHeight(0);
		
		tpInvoiceItemComments.setMinWidth(0);
		tpWorkItemComments.setMinWidth(0);
		
		tpInvoiceItemDetails.setMinHeight(Control.USE_PREF_SIZE);
		tpWorkItemDetails.setMinHeight(Control.USE_PREF_SIZE);
		
		pInvoiceItem = new TVBox();
		pInvoiceItem.getChildren().addAll(tpInvoiceItemDetails, tpInvoiceItemComments);
		TVBox.setVgrow(tpInvoiceItemComments, Priority.ALWAYS);
		
		pItemDetails = new THBox();
		pItemDetails.getChildren().addAll(pWorkItem, pInvoiceItem);
		THBox.setHgrow(pWorkItem, Priority.ALWAYS);
		THBox.setHgrow(pInvoiceItem, Priority.ALWAYS);
		
		pInvoiceItems = new SplitPane();
		pInvoiceItems.setOrientation(Orientation.VERTICAL);
		pInvoiceItems.getItems().addAll(pItemGrids, pItemDetails);
		
		gridInvoiceItems.prefWidthProperty().bind(gridWorkItems.widthProperty());
		gridWorkItems.prefWidthProperty().bind(gridInvoiceItems.widthProperty());
		
		tabItems = new Tab(Lng._("Items"));
		tabItems.setContent(pInvoiceItems);
	}
	
	protected void addInvoiceItem() {
		BOWorkItem item = gridWorkItems.getSelectedLink().getLinkedObject();
		if (item != null) {
			BOInvoiceItem ini = getMainLink().getLinkedObject().invoiceItems().createNewChild();
			ini.workItemID().assign(item.workItemID());
			ini.price().assign(item.price());
			
			linkWorkItems.getLinkedObject().reload();
			
			gridInvoiceItems.requestFocus();
			gridInvoiceItems.getGrid().select(ini);
		}
	}
	
	protected void removeInvoiceItem() {
		BOInvoiceItem item = gridInvoiceItems.getSelectedLink().getLinkedObject();
		if (item != null) {
			BOWorkItem wit = item.workItem();
			// TODO
			item.setActive(false);
			
			linkWorkItems.getLinkedObject().reload();
			
			gridWorkItems.requestFocus();
			gridWorkItems.getGrid().select(wit);
		}
	}
	
	@Override
	protected void buildFrame() {
		buildDetails();
		buildTabs();
	}

	public FrameInvoice(BOLinkEx<BOInvoice> link) {
		super(link);
	}

	@Override
	public void doDisplayState() {
		boolean isActive = isActive();
		bAddInvoiceItem.setDisable(!isActive || gridWorkItems.getSelectedLink().getLinkedObject() == null);
		bRemoveInvoiceItem.setDisable(!isActive || gridInvoiceItems.getSelectedLink().getLinkedObject() == null);
		
		BOInvoiceItem iit = gridInvoiceItems.getSelectedLink().getLinkedObject();
		BOWorkItem wit = linkDisplayWorkItem.getLinkedObject();
		boolean showInvoiceItems = !(iit == null || iit.workItem() != wit);
		
		pInvoiceItem.setVisible(showInvoiceItems);
		pInvoiceItem.setManaged(showInvoiceItems);
	}

	@Override
	public void doBuildAttributeLinks() {
		taComments.dataBindingProperty().buildAttributeLinks();
		BOCtrlUtil.buildAttributeLinks(pDetails);
		BOCtrlUtil.buildAttributeLinks(pFinance);
		
		BOCtrlUtil.buildAttributeLinks(pWorkItemDetails);
		BOCtrlUtil.buildAttributeLinks(pInvoiceItemDetails);
		taWorkItemComments.dataBindingProperty().buildAttributeLinks();
		taInvoiceItemComments.dataBindingProperty().buildAttributeLinks();
	}
}
