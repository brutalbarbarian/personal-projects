package com.lwan.eaproj.app.scenes;

import com.lwan.bo.BOLink;
import com.lwan.eaproj.bo.BOCustomer;
import com.lwan.javafx.controls.bo.BOGrid;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

class CustomersPage extends AppPage{
	private BOGrid<BOCustomer> grdCustomer;
//	private BOLink<BOCustomerSet> lnkCustomers;
//	private BOL
	
	@Override
	protected Pane buildPage() {
		Pane p = new Pane();
//		p.getChildren().add(new Label("CUSTOMERS"));
		
			
//		grdCustomer = new BOGrid<>(link, columnNames, fieldPaths, editable)
		
		return p;
	}

	@Override
	protected void initialise(String... params) {
		
	}

	@Override
	public boolean requiresSave() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requestSave() {
		// TODO Auto-generated method stub
		return true;
	}

}
