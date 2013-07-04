package com.lwan.eaproj.app.panes;

import java.util.Arrays;
import java.util.List;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.ModifiedEventType;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.eaproj.app.frames.FrameCustomer;
import com.lwan.eaproj.bo.cache.BOCustomerCache;
import com.lwan.eaproj.bo.ref.BOCustomer;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.art.RecordNavigationArt;
import com.lwan.javafx.controls.ComboBox;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.bo.binding.StringBoundProperty;
import com.lwan.javafx.interfaces.BoundBasePane;
import com.lwan.javafx.interfaces.PaneState;
import com.lwan.util.CollectionUtil;
import com.lwan.util.StringUtil;
import com.lwan.util.wrappers.Disposable;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public class PaneCustomerEdit extends BorderPane implements Disposable, EventHandler<ActionEvent>, 
		ModifiedEventListener, BoundBasePane<BOCustomer>{
	
	public PaneCustomerEdit() {
		selectedRecordIndex = -1;
		
		stateProperty();
		
		initDatasets();
		initControls();
		
		displayPaneState();
	}
	
	ToolBar tbNavigation, tbControls;
	ComboBox<String> cbSearchFields;
	BOTextField tfSearchInput;
	Button btnSearch;
	Button btnPrevious, btnNext;//, btnFirst, btnLast;
	TextField tfRecordNum;
	TextField tfRecordCount;
	Label lRecordOf, lSearch, lSearchBy;
	
	FrameCustomer frCustomer;
	
	StringBoundProperty dataBindingSearchInput;
	BOLinkEx<BOCustomer> linkSelected;
	BOCustomerSetRef setCustomerRef;
	BOLinkEx<BOSet<BOCustomer>> linkSet;
	
	private int selectedRecordIndex;
	
	public int getSelectedRecordIndex() {
		return selectedRecordIndex;
	}
	
	protected void initDatasets() {
		linkSelected = new BOLinkEx<>();
		linkSet = new BOLinkEx<>();
		
		linkSelected.addListener(this);
		
		setCustomerRef = new BOCustomerSetRef(BOCustomerCache.getCache());
		linkSet.setLinkedObject(setCustomerRef);
	}
	
	protected void initControls() {
		
		// Toolbar at top with quicksearch and record selection set
		// quicksearch is 1 combobox + 1 textfield
		// 
		// Toolbar at bottom for managing edit state
		
		dataBindingSearchInput = new StringBoundProperty(null, linkSet, "");
		
		lSearch = new Label(Lng._("using"));
		lSearchBy = new Label(Lng._("Search"));
		lRecordOf = new Label(Lng._("of"));
		cbSearchFields = new ComboBox<>();
		tfSearchInput = new BOTextField(dataBindingSearchInput);
		tfSearchInput.setOnAction(this);
		tfRecordNum = new TextField();
		tfRecordNum.setOnAction(this);
		tfRecordCount = new TextField();
		btnSearch = new Button(Lng._("Find"));
		btnSearch.setOnAction(this);
		btnPrevious = new Button(null, new RecordNavigationArt(16, 16, RecordNavigationArt.NAV_PREV));
		btnPrevious.setOnAction(this);
		btnNext = new Button(null, new RecordNavigationArt(16, 16, RecordNavigationArt.NAV_NEXT));
		btnNext.setOnAction(this);
		
		tfRecordCount.setDisable(true);
		
		cbSearchFields.addAllItems(getSearchFields(), getSearchDisplay());
		cbSearchFields.selectedProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0,
					String oldField, String newField) {
				Object previousValue = null;
				AttributeType type = AttributeType.Unknown;
				BOAttribute<?> oldAttr = dataBindingSearchInput.getLinkedAttribute();
				if (oldAttr != null) {
					previousValue = oldAttr.getValue();
					type = oldAttr.getAttributeType();
					oldAttr.clear();
				}
				dataBindingSearchInput.pathProperty().setValue(newField);
				dataBindingSearchInput.buildAttributeLinks();
				
				BOAttribute<?> newAttr = dataBindingSearchInput.getLinkedAttribute();
				if (type != AttributeType.Unknown && type == newAttr.getAttributeType()) {
					newAttr.setAsObject(previousValue);
				}
			}
		});
		cbSearchFields.getSelectionModel().select(0);
		
		tfRecordNum.setPrefColumnCount(3);
		tfRecordCount.setPrefColumnCount(3);
		
		tbNavigation = new ToolBar(lSearchBy, cbSearchFields, lSearch, tfSearchInput, btnSearch,
				new Separator(), btnPrevious, tfRecordNum, lRecordOf, tfRecordCount, btnNext);
		
		tbControls = new ToolBar();
		
		frCustomer = new FrameCustomer(linkSelected);
		
		setTop(tbNavigation);
		setCenter(frCustomer);
		setBottom(tbControls);
	}
	
	protected int getRecordCount() {
		if (linkSet.getLinkedObject() == null) {
			return 0;
		} else {
			return linkSet.getLinkedObject().getActiveCount();
		}
	}
	
	protected List<String> getSearchDisplay() {
		return CollectionUtil.toList(LngUtil.translate(Arrays.asList("Customer ID", "Name", "Address", "Contact")));
	}
	
	protected List<String> getSearchFields() {
		return Arrays.asList("CustomerID", "CustomerName", "CustomerAddress", "CustomerContact");
	}

	public void dispose() {
		linkSelected.dispose();
	}

	@Override
	public void handle(ActionEvent e) {
		Object src = e.getSource();
		if (src == tfSearchInput || src == btnSearch) {
			reopenDataset();
		} else if (src == tfRecordNum) {
			userChangedRecordIndex();
		} else if (src ==  btnPrevious) {
			setNewSelected(selectedRecordIndex - 1);
		} else if (src == btnNext) {
			setNewSelected(selectedRecordIndex + 1);
		}
	}
	
	protected void userChangedRecordIndex() {
		if (getState().isEditState()) {
			throw new RuntimeException("userChangedRecordIndex called while in edit state");
		}
		
		int currentIndex = selectedRecordIndex;
		int count = getRecordCount();
		int suggestedIndex = StringUtil.validateInt(tfRecordNum.getText(), 10) ?
				Integer.parseInt(tfRecordCount.getText()) - 1 : -1;
		
		// Make sure its within range
		if (suggestedIndex < 0 || suggestedIndex >= count) {
			suggestedIndex = -1;
		}
		
		if (suggestedIndex == -1) {
			suggestedIndex = currentIndex;
		}
		
		setNewSelected(suggestedIndex);
	}
	
	protected void reopenDataset() {
		setNewSelected(-1);
		
		setCustomerRef.reload();
		
		if (getRecordCount() > 0) {
			setNewSelected(0);
		}
	}
	
	protected void setNewSelected(int index) {
		if (getState().isEditState()) {
			throw new RuntimeException("setNewSelected() called while in edit state");
		}
		
		if (index != selectedRecordIndex) {
			if (index == -1) {
				linkSelected.setLinkedObject(null);
			} else {
				linkSelected.setLinkedObject(linkSet.getLinkedObject().getActive(index));				
			}
			
			selectedRecordIndex = index;
			buildAttributeLinks();
		}
		
		// regardless of what happens...
		displayPaneState();
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

	@Override
	public void handleModified(ModifiedEvent event) {
		if (event.getType() == ModifiedEventType.Attribute && !getState().isEditState()) {
			stateProperty().setValue(PaneState.Editing);
		} else if (event.getType() == ModifiedEventType.Save) {
			stateProperty().setValue(PaneState.Browsing);
		} else if (event.getType() == ModifiedEventType.Link) {
			if (getMainLink().getLinkedObject() == null) {
				stateProperty().setValue(PaneState.Inactive);
			} else {
				stateProperty().setValue(PaneState.Browsing);
			}
		} else if (event.getType() == ModifiedEventType.Active) {
			if (getMainLink().getLinkedObject() == null || !getMainLink().getLinkedObject().isActive()) {
				stateProperty().setValue(PaneState.Inactive);
			} else {
				stateProperty().setValue(PaneState.Browsing);
			}
		}
	}

	private Property<PaneState> stateProperty;
	
	public Property<PaneState> stateProperty() {
		if (stateProperty == null) {
			stateProperty = new SimpleObjectProperty<PaneState>(this, "State", PaneState.Inactive);
			stateProperty.addListener(new ChangeListener<PaneState>() {
				public void changed(ObservableValue<? extends PaneState> arg0,
						PaneState arg1, PaneState arg2) {
					displayPaneState();
				}
			});
		}
		return stateProperty;
	}
	
	public PaneState getState() {
		return stateProperty().getValue();
	}

	@Override
	public BOLinkEx<BOCustomer> getMainLink() {
		return linkSelected;
	}

	@Override
	public void displayPaneState() {
		int recordCount = getRecordCount();
		boolean isEditing = getState().isEditState();

		// ensure the total count and selected displayed index is correct
		// assuming the selectedRecordIndex is maintained correctly
		tfRecordCount.setText(Integer.toString(recordCount));
		tfRecordNum.setText(Integer.toString(selectedRecordIndex + 1));

		// ensure the state of the buttons are correct
		btnPrevious.setDisable(isEditing || selectedRecordIndex <= 0);
		btnNext.setDisable(isEditing || selectedRecordIndex >= recordCount - 1);

		tfRecordNum.setDisable(isEditing || recordCount <= 1);

		tfSearchInput.setEnabled(!isEditing);
		cbSearchFields.setDisable(isEditing);
		btnSearch.setDisable(isEditing);
	}

	@Override
	public void buildAttributeLinks() {
		// no need to do anything... frCustomerEdit will rebuild its attributes 
		// upon change of linked object
	}
}
