package com.lwan.javafx.controls.other;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.util.FxUtils;
import com.lwan.util.wrappers.Disposable;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

public class BOGridControl <T extends BusinessObject> implements EventHandler<ActionEvent>, Disposable, ModifiedEventListener {
	private Button btnPrimary, btnSecondary, btnRefresh, btnClearParams;
	private BOGrid<T> grid;
	private BOLinkEx<T> selectedLink;
	
	private BooleanProperty autoRefreshProperty;
	public BooleanProperty autoRefreshProperty() {
		return autoRefreshProperty;
	}
	public boolean isAutoRefresh() {
		return autoRefreshProperty().get();
	}
	public void setAutoRefresh(boolean value) {
		autoRefreshProperty().set(value);
	}
	
	public BOGridControl(BOGrid<T> _grid) {
		this.grid = _grid;
		
		autoRefreshProperty = new SimpleBooleanProperty(this, "AutoRefresh", true);
		selectedLink = new BOLinkEx<>();
		
		btnPrimary = new Button();
		btnSecondary = new Button();
		btnRefresh = new Button(Lng._("Refresh"));
		btnClearParams = new Button(Lng._("Clear"));
		
		btnPrimary.setOnAction(this);
		btnSecondary.setOnAction(this);
		btnRefresh.setOnAction(this);
		btnClearParams.setOnAction(this);
		
		grid.isEditingProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				doDisplayState();
			}
		});
		
		grid.getLink().addListener(this);
		
		grid.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				doDisplayState();
			}			
		});
		
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
		BOSet<T> set = grid.getLink().getLinkedObject();
		if (set == null || !set.allowInsert()) {
			return false;
		}
		
		if (allowCreateCallback != null) {
			return allowCreateCallback.call(grid.getSourceSet());
		} else {
			return isEditable();
		}
	}
	
	protected boolean allowDelete(T item) {
		BOSet<T> set = grid.getLink().getLinkedObject();
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
		boolean inEditState = inEditState();
		
		T item = grid.getSelectionModel().getSelectedItem();
		selectedLink.setLinkedObject(item);
		
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
		BOSet<T> set = grid.getSourceSet();
		if (set != null) {
			for (BusinessObject attr : set.getChildren()) {
				if (attr.isAttribute()) {
					((BOAttribute<?>)attr).allowUserModifyProperty().setValue(!inEditState);
				}
			}
		}
	}
	
	@Override
	public void handleModified(ModifiedEvent event) {
		// set attributes changed... most likely a param.
		if (event.isAttribute() && event.getAttributeOwner() == grid.getSourceSet()
				&& isAutoRefresh()) {
			activate(btnRefresh);
		} else {
			doDisplayState();
		}
	}
	
	protected boolean inEditState() {
		return grid.isEditingProperty().getValue();
	}
	
	public BOLinkEx<T> getSelectedLink() {
		return selectedLink;
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
	
	protected void select(final int index) {
		Platform.runLater(new Runnable(){
			public void run() {
				grid.getSelectionModel().select(index);	
			}						
		});
	}
	
	protected void select(final T item) {
		Platform.runLater(new Runnable( ){
			public void run() {

				grid.getSelectionModel().select(item);							
			}						
		});
	}
	
	public void activate(Button btn) {
		try {
			if (btn.isDisabled()) {
				return;	// Do nothing
			}
			boolean inEditState = inEditState();
			if (btn == btnPrimary) {
				if (inEditState) {
					grid.save();
				} else {
					BOSet<T> set = grid.getSourceSet();
					if (set != null) { 
						set.createNewChild();
						// can we gurantee that the child will always be last?... makes sense if it is
						grid.getSelectionModel().selectLast();
					}
				}			
			} else if (btn == btnSecondary) {
				T selected = grid.getSelectionModel().getSelectedItem();
				int index = grid.getSelectionModel().getSelectedIndex();
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
					if (index >= grid.getSourceSet().getActiveCount()) {
						index = grid.getSourceSet().getActiveCount() - 1;
					}
					// select whatever is still avaliable
					select(index);
				}
			} else if (btn == btnRefresh) {
				grid.getSourceSet().setActive(false);
				grid.getSourceSet().ensureActive();
				grid.refresh();
			} else if (btn == btnClearParams) {
				grid.getSourceSet().allowNotificationsProperty().setValue(false);
				try {
					for (BusinessObject bo : grid.getSourceSet().getChildren()) {
						if (bo.isAttribute()) {
							bo.clear();
						}
					}
				} finally {
					grid.getSourceSet().allowNotificationsProperty().setValue(true);
					activate(btnRefresh);
				}
			}
		} catch (RuntimeException e) {
			FxUtils.ShowErrorDialog(grid.getScene().getWindow(), e.getMessage());
//			System.out.println("Error:" + e.getMessage());
		}
	}

	@Override
	public void handle(ActionEvent e) {
		activate((Button)e.getSource());
	}
	
	public void setHotkeyControls(final Node n) {
		n.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>(){
			public void handle(KeyEvent arg0) {
				if (arg0.isControlDown()) {
					switch (arg0.getText()) {
					case "n":	// new
						if (!grid.isEditingProperty().getValue()) {
							activate(getPrimaryButton());
							
							grid.requestFocus();
						}
						break;
					case "s":	// save
						if (grid.isEditingProperty().getValue()) {
							// check what's focused
							Node focused = n.getScene().getFocusOwner();
							if (focused instanceof BOTextField) {
								BOTextField txtField = (BOTextField)focused;
								txtField.dataBindingProperty().endEdit(true);
							} else if (focused instanceof BOComboBox<?>) {
								BOComboBox<?> cb = (BOComboBox<?>)focused;
								cb.forceCommit();
							}
							
							activate(getPrimaryButton());
							getPrimaryButton().requestFocus();
						}					
					}
				}
			}			
		});
	}
	@Override
	public void dispose() {
		selectedLink.dispose();
		grid.getLink().removeListener(this);
	}
}
