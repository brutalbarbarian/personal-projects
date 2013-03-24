package com.lwan.javafx.controls.bo;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.javafx.app.Lng;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class BOGridControl <T extends BusinessObject> implements EventHandler<ActionEvent> {
	Button btnPrimary, btnSecondary, btnRefresh, btnClearParams;
	BOGrid<T> grid;
	BOLinkEx<T> selectedLink;
	
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
		
		grid.getLink().addListener(new ModifiedEventListener() {
			public void handleModified(ModifiedEvent event) {
				// set attributes changed... most likely a param.
				if (event.isAttribute() && event.getAttributeOwner() == grid.getSourceSet()
						&& isAutoRefresh()) {
					activate(btnRefresh);
				}
			}		
		});
		
		grid.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				doDisplayState();
			}			
		});
		
		doDisplayState();
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
			
			btnSecondary.setDisable(false);
		} else {
			btnPrimary.setText(Lng._("Create"));
			btnSecondary.setText(Lng._("Delete"));
			
			btnSecondary.setDisable(item == null);
		}
		
		// disable all params if in edit state
		for (BusinessObject attr : grid.getSourceSet().getChildren()) {
			if (attr.isAttribute()) {
				((BOAttribute<?>)attr).allowUserModifyProperty().setValue(!inEditState);
			}
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
	}

	@Override
	public void handle(ActionEvent e) {
		activate((Button)e.getSource());
	}
}
