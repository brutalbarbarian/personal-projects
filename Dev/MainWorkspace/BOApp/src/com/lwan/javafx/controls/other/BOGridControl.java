package com.lwan.javafx.controls.other;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BOTextField;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

public class BOGridControl<T extends BusinessObject> extends BOSetControl<T> {
	private BOGrid<T> grid;
	
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
	
	public BOGridControl(BOGrid<T> grid) {
		super(grid.getLink(), grid.getSelectedLink(), grid);
		
		this.grid = grid;
		
		autoRefreshProperty = new SimpleBooleanProperty(this, "AutoRefresh", true);
				
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
	}
	
	@Override
	public void handleModified(ModifiedEvent event) {
		// set attributes changed... most likely a param.
		if (event.isAttribute() && event.getAttributeOwner() == setLink.getLinkedObject() 
				&& isAutoRefresh()) {
			activate(getRefreshButton());
		} else {
			super.handleModified(event);
		}
	}
	
	public BOLinkEx<T> getSelectedLink() {
		return selectedLink;
	}
	
	@Override
	protected void operationSave() {
		// let grid handle the saving
		grid.save();
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
}
