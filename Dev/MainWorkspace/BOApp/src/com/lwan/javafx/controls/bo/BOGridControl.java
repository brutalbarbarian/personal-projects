package com.lwan.javafx.controls.bo;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.javafx.app.Lng;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class BOGridControl <T extends BusinessObject> implements EventHandler<ActionEvent> {
	Button btnPrimary, btnSecondary, btnRefresh;
	BOGrid<T> grid;
	BOLinkEx<T> selectedLink;
	
	public BOGridControl(BOGrid<T> grid) {
		this.grid = grid;
		
		selectedLink = new BOLinkEx<>();
		
		btnPrimary = new Button();
		btnSecondary = new Button();
		btnRefresh = new Button(Lng._("Refresh"));
		
		btnPrimary.setOnAction(this);
		btnSecondary.setOnAction(this);
		btnRefresh.setOnAction(this);
		
		grid.isEditingProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				doDisplayState();
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
		if (inEditState) {
			btnPrimary.setText(Lng._("Save"));
			btnSecondary.setText(Lng._("Cancel"));
			
			btnSecondary.setDisable(false);
		} else {
			btnPrimary.setText(Lng._("Create"));
			btnSecondary.setText(Lng._("Delete"));
			
			btnSecondary.setDisable(item == null);
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

	@Override
	public void handle(ActionEvent e) {
		Button src = (Button) e.getSource();
		boolean inEditState = inEditState();
		if (src == btnPrimary) {
			if (inEditState) {
				grid.save();
			} else {
				BOSet<T> set = grid.getSourceSet();
				if (set != null) {
					T bo = set.createNewChild();
					grid.getSelectionModel().select(bo);
				}
			}			
		} else if (src == btnSecondary) {
			T selected = grid.getSelectionModel().getSelectedItem();
			int index = grid.getSelectionModel().getSelectedIndex();
			if (inEditState && selected.isFromDataset()) {
				// reload selected
				selected.setActive(false);
				selected.ensureActive();
				// keep the same selected...
				grid.getSelectionModel().select(index);
//				grid.getSelectionModel()
//				grid.getSelectionModel().select(selected);
			} else {	// either canceling a new record...or deleting an existing one
				
				selected.setActive(false);
				if (selected.isFromDataset()) {
					selected.trySave();	// this should delete the record...
				}
				if (index >= grid.getSourceSet().getActiveCount()) {
					index = grid.getSourceSet().getActiveCount() - 1;
				}
				// select whatever is still avaliable
				grid.getSelectionModel().select(index);
			}
		} else if (src == btnRefresh) {
			grid.refresh();
		}
	}
}
