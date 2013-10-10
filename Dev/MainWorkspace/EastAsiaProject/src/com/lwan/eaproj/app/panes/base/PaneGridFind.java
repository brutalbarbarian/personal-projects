package com.lwan.eaproj.app.panes.base;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.lwan.bo.BusinessObject;
import com.lwan.eaproj.app.AppEastAsia;
import com.lwan.eaproj.app.PageConstants;
import com.lwan.javafx.app.Lng;
import com.lwan.util.FxUtils;
import com.lwan.util.containers.Params;

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
			Params params;
			if (btn == bEdit) {
				params = new Params(
						PageConstants.PAGE_NAME, getPageBaseName() + PageConstants.SUBPAGE_EDIT,
						getIDField(), getID());
			} else {
				params = new Params(
						PageConstants.PAGE_NAME, getPageBaseName() + PageConstants.SUBPAGE_EDIT,
						PageConstants.PARAM_CREATE, true);
			}
			
			AppEastAsia.notifyMessage(AppEastAsia.PAGE_CHANGE_REQUEST, params);
		}
	}
	
	protected abstract String getIDField();
	protected abstract Integer getID();
	protected abstract String getEditFormName();
	protected abstract String getPageBaseName();
	
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
