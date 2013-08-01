package com.lwan.eaproj.app.panes.pages;

import com.lwan.eaproj.app.panes.PaneCustomerFind;

import javafx.scene.layout.Pane;

public class PageCustomer extends PageBase{
	PaneCustomerFind pCustomerFind;

	@Override
	public void dispose() {
		pCustomerFind.dispose();
	}

	@Override
	protected Pane buildPage() {
		pCustomerFind = new PaneCustomerFind();
		return pCustomerFind;
	}

	@Override
	protected void initialise(String... params) {
		// TODO
	}

	@Override
	public boolean requiresSave() {
		return pCustomerFind.getState().isEditState();
	}

	@Override
	public boolean requestSave() {
//		if (pCustomerFind.getState().isEditState()) {
//			try {
//				pCustomerFind.getController().activate(pCustomerFind.getController().getPrimaryButton());
//			} catch (RuntimeException e) {
//				return false;
//			}
//		}
		return true;
	}

}
