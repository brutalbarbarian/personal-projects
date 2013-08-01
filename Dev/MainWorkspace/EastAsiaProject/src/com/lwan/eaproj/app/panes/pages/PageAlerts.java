package com.lwan.eaproj.app.panes.pages;

import com.lwan.javafx.controls.panes.TStackPane;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class PageAlerts extends PageBase {
	public PageAlerts(String ... params ) {
		super(params);
	}	
	
	@Override
	public boolean requiresSave() {
		return false;
	}

	@Override
	public boolean requestSave() {
		return true;
	}

	@Override
	protected Pane buildPage() {
		TStackPane sp = new TStackPane();
		sp.getChildren().add(new Label("ALERTS"));
		
		return sp;
	}

	@Override
	protected void initialise(String... params) {
		// TODO
	}

	@Override
	public void dispose() {
		// TODO
	}

}
