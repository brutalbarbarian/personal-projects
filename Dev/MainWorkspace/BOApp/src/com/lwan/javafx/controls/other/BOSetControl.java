package com.lwan.javafx.controls.other;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.util.Callback;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.javafx.app.Lng;

public abstract class BOSetControl<T extends BusinessObject> implements EventHandler<ActionEvent>, ModifiedEventListener {
	private Button btnPrimary, btnSecondary, btnRefresh, btnClearParams;
	private BOLinkEx<T> selectedLink;
	private BOLinkEx<BOSet<T>> setLink;
	private BOSetControlTarget<T> target;
	
	public BOSetControl(BOLinkEx<BOSet<T>> setLink, BOLinkEx<T> selectedLink,
			BOSetControlTarget<T> target) {
		this.target = target;
		this.setLink = setLink;
		this.selectedLink = selectedLink; 
		
		btnPrimary = new Button();
		btnSecondary = new Button();
		btnRefresh = new Button(Lng._("Refresh"));
		btnClearParams = new Button(Lng._("Clear"));
		
		btnPrimary.setOnAction(this);
		btnSecondary.setOnAction(this);
		btnRefresh.setOnAction(this);
		btnClearParams.setOnAction(this);
		
		// add listener to editing property
	
		setLink.addListener(this);
		
		doDisplayState();
	}
	
	private BooleanProperty editableProperty;
	/**
	 * The default value for allowCreate, allowDelete and allowSave
	 * if they are not overriden by a callback.
	 * The callbacks override this property's value.
	 * 
	 * @return
	 */
	public BooleanProperty editableProperty() {
		if (editableProperty == null) {
			editableProperty = new SimpleBooleanProperty(this, "Editable", true);
		}
		return editableProperty;
	}
	public void setEditable(boolean editable) {
		editableProperty().set(editable);
	}
	public boolean isEditable() {
		return editableProperty().get();
	}
	
	/*
	 * These callbacks allows user to choose when the grid should be editable.
	 * They override whatever isEditable() says.
	 * 
	 */
	private Callback<BOSet<T>, Boolean> allowCreateCallback;
	private Callback<T, Boolean> allowDeleteCallback;
	private Callback<T, Boolean> allowSaveCallback;
	
	public void setAllowCreateCallback(Callback<BOSet<T>, Boolean> callback) {
		allowCreateCallback = callback;
	}
	
	public void setAllowDeleteCallback(Callback<T, Boolean> callback) {
		allowDeleteCallback = callback;
	}
	
	public void setAllowSaveCallback(Callback<T, Boolean> callback) {
		allowSaveCallback = callback;
	}
	
	protected boolean allowCreate() {
		BOSet<T> set = setLink.getLinkedObject();
		if (set == null || !set.allowInsert()) {
			return false;
		}
		
		if (allowCreateCallback != null) {
			return allowCreateCallback.call(setLink.getLinkedObject());
		} else {
			return isEditable();
		}
	}
	
	protected boolean allowDelete(T item) {
		BOSet<T> set = setLink.getLinkedObject();
		if (set == null || !set.allowDelete()) {
			return false;
		}
		
		if (allowDeleteCallback != null) {
			return allowDeleteCallback.call(item);
		} else {
			return isEditable();
		}
	}
	
	protected boolean allowSave(T item) {
		if (allowSaveCallback != null) {
			return allowSaveCallback.call(item);
		} else {
			return isEditable();
		}
	}
	
	protected void doDisplayState() {
		boolean inEditState = true;//inEditState();
		
		T item = selectedLink.getLinkedObject();
		
		btnRefresh.setDisable(inEditState);
		btnClearParams.setDisable(inEditState);
		if (inEditState) {
			btnPrimary.setText(Lng._("Save"));
			btnSecondary.setText(Lng._("Cancel"));
			
			btnPrimary.setDisable(!allowSave(item));
			btnSecondary.setDisable(false);
		} else {
			btnPrimary.setText(Lng._("Create"));
			btnSecondary.setText(Lng._("Delete"));
			
			btnPrimary.setDisable(!allowCreate());
			btnSecondary.setDisable(item == null || !allowDelete(item));
		}
		
		// disable all params if in edit state
		BOSet<T> set = setLink.getLinkedObject();
		if (set != null) {
			for (BusinessObject attr : set.getChildren()) {
				if (attr.isAttribute()) {
					((BOAttribute<?>)attr).allowUserModifyProperty().setValue(!inEditState);
				}
			}
		}
	}

	public Button getPrimaryButton() {
		return btnPrimary;
	}
	
	public Button getSecondaryButton() {
		return btnSecondary;
	}
	
	public Button getRefreshButton() {
		return btnRefresh;
	}
	
	public Button getClearButton() {
		return btnClearParams;
	}

	
	@Override
	public void handleModified(ModifiedEvent event) {
		doDisplayState();
	}
	
	@Override
	public void handle(ActionEvent arg0) {
		activate((Button)arg0.getSource());
	}
	
	protected void operationSave() {
		selectedLink.getLinkedObject().trySave();
	}
	
	protected int getSelectedIndex() {
		return target.getSelectedIndex();
	}
	protected void select(T item) {
		target.select(item);
	}
	protected void select(int index) {
		target.select(index);
	}
	protected boolean inEditState() {
		return target.inEditState();
	}
	
	public void activate(Button btn) {
		try {
			if (btn.isDisabled()) {
				return;	// Do nothing
			}
			boolean inEditState = inEditState();
			if (btn == btnPrimary) {
				if (inEditState) {
					operationSave();
				} else {
					BOSet<T> set = setLink.getLinkedObject();
					if (set != null) { 
						select(set.createNewChild());
					}
				}			
			} else if (btn == btnSecondary) {
				T selected = selectedLink.getLinkedObject();
				int index = getSelectedIndex();
				if (inEditState && selected.isFromDataset()) {
					// reload selected
					selected.setActive(false);
					selected.ensureActive();
					// keep the same selected...
					select(index);
				} else {	// either canceling a new record...or deleting an existing one
					
					selected.setActive(false);
					if (selected.isFromDataset()) {
						selected.trySave();	// this should delete the record...
					}
					if (index >= setLink.getLinkedObject().getActiveCount()) {
						index = setLink.getLinkedObject().getActiveCount() - 1;
					}
					// select whatever is still avaliable
					select(index);
				}
			} else if (btn == btnRefresh) {
				setLink.getLinkedObject().setActive(false);
				setLink.getLinkedObject().ensureActive();
			} else if (btn == btnClearParams) {
				setLink.getLinkedObject().allowNotificationsProperty().setValue(false);
				try {
					for (BusinessObject bo : setLink.getLinkedObject().getChildren()) {
						if (bo.isAttribute()) {
							bo.clear();
						}
					}
				} finally {
					setLink.getLinkedObject().allowNotificationsProperty().setValue(true);
					activate(btnRefresh);
				}
			}
		} catch (RuntimeException e) {
//			FxUtils.ShowErrorDialog(grid.getScene().getWindow(), e.getMessage());
//			System.out.println("Error:" + e.getMessage());
		}
	}
}
