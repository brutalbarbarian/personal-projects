package com.lwan.eaproj.app.panes;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.controls.bo.GridView;
import com.lwan.util.wrappers.Disposable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class PaneGridBase <T extends BusinessObject> extends BorderPane implements Disposable{
	protected BOLinkEx<BOSet<T>> link;
	protected GridView<T> gridView;
	protected ToolBar toolbar;
	
	private VBox mainPane;	
	private Node editPane;
	
	public PaneGridBase () {
		initControls();
		initGridLink(link);
		
		onNewSelection(null);
		displayState();
	}
	
	private void initControls() {
		initGrid();
		editPane = initEditPane();
		initToolbar();
		
		VBox.setVgrow(gridView, getGridGrowth());
		mainPane = new VBox();
		mainPane.getChildren().add(gridView);
		if (editPane != null) {
			mainPane.getChildren().add(editPane);
			// by default
			VBox.setVgrow(editPane, getEditPaneGrowth());
		}
		
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
//		gridControl = constructGridControl(grid);	
//		gridControl.setHotkeyControls(this);
		
		gridView.getGridControl().getSelectedLink().addListener(new ModifiedEventListener() {
			public void handleModified(ModifiedEvent event) {
				displayState();
			}			
		});
		
		gridView.getGridControl().getSelectedLink().linkedObjectProperty().addListener(new ChangeListener<T>(){
			public void changed(ObservableValue<? extends T> arg0,
					T arg1, T arg2) {
				if (editPane != null) {
					BOCtrlUtil.buildAttributeLinks(editPane);
				}
				onNewSelection(arg2);
				displayState();
			}			
		});
	}
	
	protected void onNewSelection(T selection) {}
	protected void displayState() {}
	
	protected BOLinkEx<T> getSelectedLink() {
		return gridView.getGridControl().getSelectedLink();
	}
	
	// init menu?
	// init filters?
	protected abstract Node initEditPane();
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
}
