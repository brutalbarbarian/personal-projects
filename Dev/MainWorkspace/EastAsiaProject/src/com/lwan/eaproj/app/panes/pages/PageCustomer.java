package com.lwan.eaproj.app.panes.pages;

import com.lwan.eaproj.app.panes.PaneCustomerEdit;
import com.lwan.eaproj.app.panes.PaneCustomerFind;

import javafx.scene.layout.Pane;

public class PageCustomer extends PageBase{
//	PaneCustomerFind paneCustomerFind;
	PaneCustomerEdit pCustomerEdit;

	@Override
	public void dispose() {
		pCustomerEdit.dispose();
	}

	@Override
	protected Pane buildPage() {
		pCustomerEdit = new PaneCustomerEdit();
		return pCustomerEdit;
	}

	@Override
	protected void initialise(String... params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean requiresSave() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requestSave() {
		// TODO Auto-generated method stub
		return false;
	}

}
