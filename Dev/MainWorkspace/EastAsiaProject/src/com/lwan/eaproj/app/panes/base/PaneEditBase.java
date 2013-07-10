package com.lwan.eaproj.app.panes.base;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.ModifiedEventType;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.art.RecordNavigationArt;
import com.lwan.javafx.controls.ComboBox;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.bo.binding.StringBoundProperty;
import com.lwan.javafx.controls.other.BOSetControl;
import com.lwan.javafx.controls.other.BOSetControlTarget;
import com.lwan.javafx.interfaces.BoundBasePane;
import com.lwan.javafx.interfaces.PaneState;
import com.lwan.util.StringUtil;
import com.lwan.util.wrappers.Disposable;

public abstract class PaneEditBase <B extends BusinessObject> extends BorderPane implements BoundBasePane<B>,
		ModifiedEventListener, BOSetControlTarget<B>, EventHandler<ActionEvent>, Disposable{
	private IntegerProperty selectedIndexProperty;
	public ReadOnlyIntegerProperty selectedIndexProperty() {
		return selectedIndexProperty;
	}
	
	@Override
	public int getSelectedIndex() {
		return selectedIndexProperty().getValue();
	}
	
	private Property<PaneState> stateProperty;
	public Property<PaneState> stateProperty() {
		return stateProperty;
	}
		
	public PaneState getState() {
		return stateProperty().getValue();
	}
	
	public PaneEditBase() {
		selectedIndexProperty = new SimpleIntegerProperty(this,  "SelectedIndex", -1);
		stateProperty = new SimpleObjectProperty<PaneState>(this, "State", PaneState.Inactive);
		stateProperty.addListener(new ChangeListener<PaneState>() {
			public void changed(ObservableValue<? extends PaneState> arg0,
					PaneState arg1, PaneState arg2) {
				displayPaneState();
			}
		});
		
		initDatasets();
		initControls();
		
		reopenDataset();
	}
	
	ToolBar tbNavigation, tbControls;
	ComboBox<String> cbSearchFields;
	BOTextField tfSearchInput;
	TextField tfRecordNum, tfRecordCount;
	Button btnSearch, btnPrevious, btnNext;
	Label lRecordOf, lSearch, lSearchBy;
	
	StringBoundProperty dataBindingSearchInput;
	BOLinkEx<B> linkSelected;
	BOLinkEx<BOSet<B>> linkSet;
	
	BOSetControl<B> setControl;
	
	protected void initDatasets() {
		linkSelected = new BOLinkEx<>();
		linkSet = new BOLinkEx<>();
		dataBindingSearchInput = new StringBoundProperty(null, linkSet, "");
		
		initSetLink(linkSet);
		
		linkSelected.addListener(this);
		linkSet.addListener(this);
	}
	
	protected void initControls() {
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
	
		tfRecordNum.setPrefColumnCount(3);
		tfRecordCount.setPrefColumnCount(3);
		tfRecordCount.setDisable(true);
		
		initSearchFields(cbSearchFields);
		boolean hasSearchFields = cbSearchFields.getItems().size() > 0; 
		if (hasSearchFields) {
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
			
			tbNavigation = new ToolBar(lSearchBy, cbSearchFields, lSearch, tfSearchInput, btnSearch,
					new Separator(), btnPrevious, tfRecordNum, lRecordOf, tfRecordCount, btnNext);
		} else {
			tbNavigation = new ToolBar(btnPrevious, tfRecordNum, lRecordOf, tfRecordCount, btnNext);
		}
		setControl = new BOSetControl<>(linkSet, linkSelected, this);
		tbControls = new ToolBar(setControl.getPrimaryButton(), setControl.getSecondaryButton());
		Node center = initEditPane();
		
		if (center == null) {
			throw new RuntimeException("Invalid child of PaneEditBase - no detail node returne from initDetail()");
		}
		
		setTop(tbNavigation);
		setCenter(center);
		setBottom(tbControls);
	}
	
	protected abstract void initSearchFields(ComboBox<String> cb);
	protected abstract void initSetLink(BOLinkEx<BOSet<B>> link);
	protected abstract Node initEditPane();
	
	protected int getRecordCount() {
		if (linkSet.getLinkedObject() == null) {
			return 0;
		} else {
			return linkSet.getLinkedObject().getActiveCount();
		}
	}
	
	public void dispose() {
		linkSelected.dispose();
	}
	
	protected void userChangedRecordIndex() {
		if (getState().isEditState()) {
			throw new RuntimeException("userChangedRecordIndex called while in edit state");
		}
		
		int currentIndex = getSelectedIndex();
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
		
		select(suggestedIndex);	
	}
	
	@Override
	public void handle(ActionEvent e) {
		Object src = e.getSource();
		if (src == tfSearchInput || src == btnSearch) {
			reopenDataset();
		} else if (src == tfRecordNum) {
			userChangedRecordIndex();
		} else if (src ==  btnPrevious) {
			select(getSelectedIndex() - 1);
		} else if (src == btnNext) {
			select(getSelectedIndex() + 1);
		}
	}	
	
	@Override
	public void handleModified(ModifiedEvent event) {
		if (event.getCaller() == linkSelected) {
			if (event.getType() == ModifiedEventType.Attribute) {
				if (!getState().isEditState()) {
					stateProperty().setValue(PaneState.Editing);
				}
			} else if (event.getType() == ModifiedEventType.Save) {
				stateProperty().setValue(PaneState.Browsing);
			} else if (event.getType() == ModifiedEventType.Link || 
					event.getType() == ModifiedEventType.Active) {
				B selected = getMainLink().getLinkedObject();
				if (selected == null || !selected.isActive()) {
					stateProperty().setValue(PaneState.Inactive);
				} else if (selected.isFromDataset()) {
					stateProperty().setValue(PaneState.Browsing);
				} else {
					stateProperty().setValue(PaneState.Inserting);
				}
			}
		} else if (event.getSource() == linkSet) {
			if (event.getType() == ModifiedEventType.Link) {
				dataBindingSearchInput.buildAttributeLinks();
			}
		}
	}
	
	protected void reopenDataset() {
		select(-1);
		
		BOSet<B> set = linkSet.getLinkedObject();
		if (set != null) {
			set.reload();
		}
		
		int index = -1;
		if (getRecordCount() > 0) {
			index = 0;
		}
		select(index);
	}
	
	@Override
	public void select(int index) {
		if (getState().isEditState()) {
			throw new RuntimeException("select() called while in edit state");
		}
		
		if (index != getSelectedIndex()) {
			if (index == -1) {
				linkSelected.setLinkedObject(null);
			} else {
				linkSelected.setLinkedObject(linkSet.getLinkedObject().getActive(index));				
			}
			
			selectedIndexProperty.set(index);
			buildAttributeLinks();
		}
		
		// regardless of what happens...
		displayPaneState();	
	}

	@Override
	public BOLinkEx<B> getMainLink() {
		return linkSelected;
	}

	@Override
	public void displayPaneState() {
		int recordCount = getRecordCount();
		int selectedRecordIndex = getSelectedIndex();
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
		
		setControl.doDisplayState();
	}

	@Override
	public void buildAttributeLinks() {
		// override if anything needs to happen
	}

	@Override
	public void select(B item) {
		BOSet<B> set = linkSet.getLinkedObject();
		if (set == null) {
			throw new RuntimeException("select called when no boset is set");
		}
		int index = set.indexOfActiveChild(item);
		if (index == -1) {
			throw new RuntimeException("select called for invalid object");
		}
		select(index);	
	}

	@Override
	public boolean inEditState() {
		return getState().isEditState();
	}

	@Override
	public Window getWindow() {
		return getScene().getWindow();
	}
}
