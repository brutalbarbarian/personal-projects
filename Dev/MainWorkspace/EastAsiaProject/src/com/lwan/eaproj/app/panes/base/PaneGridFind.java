package com.lwan.eaproj.app.panes.base;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;
import com.lwan.util.FxUtils;

public abstract class PaneGridFind <T extends BusinessObject> extends PaneGridBase<T> implements EventHandler<ActionEvent> {
	public PaneGridFind() {
		result = false;
		allowSelect = false;
		
		gridView.getGrid().setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.PRIMARY &&
						e.getClickCount() == 2) {
					// Has something selected
					if (getMainLink().getLinkedObject() != null) {
						activate(bEdit);
					}
				}
			}			
		});
	}
	
	@Override
	protected Node initEditPane() {
		return null;
	}

	Button bSelect, bCancel, bEdit, bNew;
	@Override
	protected void initToolbar() {
		bSelect = new Button(Lng._("Select"));
		bNew = new Button(Lng._("New " + getChildName()));
		bEdit = new Button(Lng._("Edit " + getChildName()));
		bCancel = new Button(Lng._("Cancel"));
		
		bSelect.setOnAction(this);
		bNew.setOnAction(this);
		bEdit.setOnAction(this);
		bCancel.setOnAction(this);
		
		toolbar = new ToolBar(bSelect, bNew, bEdit, bCancel);
	}
	
	protected abstract String getChildName();

	@Override
	public void handle(ActionEvent arg0) {
		activate((Button)arg0.getSource());
	}
	
	protected void activate(Button btn) {
		if(btn == bSelect || btn == bCancel) {
			// exit the screen
			result = btn == bSelect;
			getScene().getWindow().hide();
		} else if (btn == bEdit || btn == bNew) {
			Stage stage = new Stage(StageStyle.UTILITY);
			stage.initOwner(getScene().getWindow());
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setTitle(getEditFormName());
			
			PaneEditBase<?> pane = getNewEditForm();
			try {
				if (btn == bEdit) {
					setSearchField(pane);
					pane.reopenDataset();
				} else {
					BOSet<?> set = pane.getSetLink().getLinkedObject();
					if (set != null) {
//						BusinessObject child = 
						set.createNewChild();
						// there really should only be one.
						pane.select(0);
					}
				}
				
				Scene sc = new Scene(pane);
				sc.getStylesheets().addAll(App.getStyleshets());
				
				stage.setScene(sc);
				stage.setWidth(1000);
				stage.setHeight(800);
				stage.showAndWait();
			} finally {
				pane.dispose();
			}
			
			// refresh the grid
			link.getLinkedObject().reload();
			gridView.refreshGrid();
			
			displayPaneState();
		}
	}
	
	protected abstract void setSearchField(PaneEditBase<?> pane);
	protected abstract String getEditFormName();
	protected abstract PaneEditBase<?> getNewEditForm();
	
	
	private boolean result;
	public boolean getResult() {
		return result;
	}
	
	private boolean allowSelect;
	public boolean allowSelect() {
		return allowSelect;
	}
	public void setAllowSelect(boolean allowSelect) {
		this.allowSelect = allowSelect;  
	}
	
	@Override
	public void displayPaneState() {
		super.displayPaneState();
		
		FxUtils.setVisibleAndManaged(bSelect, allowSelect);
		FxUtils.setVisibleAndManaged(bCancel, allowSelect);
		boolean selected = getMainLink().getLinkedObject() != null;
		bSelect.setDisable(!selected);
		bEdit.setDisable(!selected);
	}
}
