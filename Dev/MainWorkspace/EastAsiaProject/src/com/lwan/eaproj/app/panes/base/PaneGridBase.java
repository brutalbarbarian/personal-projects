package com.lwan.eaproj.app.panes.base;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.ModifiedEventType;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.TBorderPane;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.javafx.interfaces.BoundBasePane;
import com.lwan.javafx.interfaces.PaneState;
import com.lwan.util.wrappers.Disposable;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;

public abstract class PaneGridBase <T extends BusinessObject> extends TBorderPane implements Disposable, BoundBasePane<T>,
		ModifiedEventListener{
	protected BOLinkEx<BOSet<T>> link;
	protected GridView<T> gridView;
	protected ToolBar toolbar;
	
	private TVBox mainPane;	
	private Node editPane;
	private Node paramPane;
	
	public PaneGridBase () {
		stateProperty = new SimpleObjectProperty<PaneState>(this, "State", PaneState.Inactive);
		stateProperty.addListener(new ChangeListener<PaneState>() {
			public void changed(ObservableValue<? extends PaneState> arg0,
					PaneState arg1, PaneState arg2) {
				displayPaneState();
			}
		});
		
		initControls();
		initGridLink(link);
		
		onNewSelection(null);
		displayPaneState();
	}
	
	private void initControls() {
		initGrid();
		editPane = initEditPane();
		paramPane = initParamPane();
		initToolbar();
		
		TVBox.setVgrow(gridView, getGridGrowth());
		mainPane = new TVBox();
		mainPane.getChildren().add(gridView);
		if (editPane != null) {
			mainPane.getChildren().add(editPane);
			// by default
			TVBox.setVgrow(editPane, getEditPaneGrowth());
		}
		
		BOCtrlUtil.buildAttributeLinks(paramPane);
		
		setTop(paramPane);
		setCenter(mainPane);
		setBottom(toolbar);
	}
	
	protected Priority getEditPaneGrowth() {
		return Priority.NEVER;
	}
	
	protected Priority getGridGrowth() {
		return Priority.SOMETIMES;
	}
	
	protected void initToolbar() {
		toolbar = new ToolBar();
		toolbar.getItems().addAll(gridView.getGridControl().getPrimaryButton(), 
				gridView.getGridControl().getSecondaryButton(), 
				gridView.getGridControl().getRefreshButton());
	}
	
	protected void initGrid() {
		link = new BOLinkEx<>();
		gridView = constructGrid(link);
		gridView.getGridControl().setHotkeyControls(this);

		link.addListener(this);
		getMainLink().addListener(this);
		
		getMainLink().linkedObjectProperty().addListener(new ChangeListener<T>(){
			public void changed(ObservableValue<? extends T> arg0,
					T arg1, T arg2) {
				if (editPane != null) {
					buildAttributeLinks();
				}
				onNewSelection(arg2);
				displayPaneState();
			}			
		});
		
	}
	
	protected void onNewSelection(T selection) {}
	public void displayPaneState() {}
	
	// init menu?
	// init filters?
	protected abstract Node initEditPane();
	protected abstract Node initParamPane();
	protected abstract void initGridLink(BOLinkEx<BOSet<T>> gridLink);	
	protected abstract GridView<T> constructGrid(BOLinkEx<BOSet<T>> gridLink);

	@Override
	public void dispose() {
		gridView.dispose();
		link.dispose();
	}
	
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}
	
	@Override
	public void handleModified(ModifiedEvent event) {
		if (event.getCaller() == link) {
			if (event.getType() == ModifiedEventType.Link) {
				buildAttributeLinks();
			}
		} else {
			if (event.getType() == ModifiedEventType.Attribute) {
				if (!getState().isEditState()) {
					stateProperty().setValue(PaneState.Editing);
				}
			} else if (event.getType() == ModifiedEventType.Save) {
				stateProperty().setValue(PaneState.Browsing);
			} else if (event.getType() == ModifiedEventType.Link || 
					event.getType() == ModifiedEventType.Active) {
				T selected = getMainLink().getLinkedObject();
				if (selected == null || !selected.isActive()) {
					stateProperty().setValue(PaneState.Inactive);
				} else if (selected.isFromDataset()) {
					stateProperty().setValue(PaneState.Browsing);
				} else {
					stateProperty().setValue(PaneState.Inserting);
				}
				onNewSelection(selected);
			}
		}
	}
	
	private Property<PaneState> stateProperty;
	public Property<PaneState> stateProperty() {
		return stateProperty;
	}

	@Override
	public PaneState getState() {
		return stateProperty().getValue();
	}

	@Override
	public BOLinkEx<T> getMainLink() {
		return gridView.getSelectedLink();
	}

	@Override
	public void buildAttributeLinks() {
		if (paramPane != null) {
			BOCtrlUtil.buildAttributeLinks(paramPane);
		}
		if (editPane != null) {
			BOCtrlUtil.buildAttributeLinks(editPane);
		}
	}
}
