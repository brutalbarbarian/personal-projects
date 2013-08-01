package com.lwan.eaproj.app.panes.pages;

import com.lwan.eaproj.app.panes.PaneWorkFind;

import javafx.scene.layout.Pane;

public class PageWork extends PageBase {
	PaneWorkFind pWorkFind;
	
	@Override
	public void dispose() {
		pWorkFind.dispose();
	}

	@Override
	protected Pane buildPage() {
		pWorkFind = new PaneWorkFind();
		return pWorkFind;
	}

	@Override
	protected void initialise(String... params) {
	}

	@Override
	public boolean requiresSave() {
		return false;
	}

	@Override
	public boolean requestSave() {
		return true;
	}

}
