package com.lwan.eaproj.app.scenes;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.eaproj.bo.BOCustomer;
import com.lwan.eaproj.bo.cache.BOCustomersSet;
import com.lwan.javafx.controls.bo.BOGrid;
import com.lwan.jdbc.StoredProc;

import javafx.scene.layout.Pane;

class CustomersPage extends AppPage{
	private BOGrid<BOCustomer> grdCustomer;
	private BOLinkEx<BOSet<BOCustomer>> lnkCustomers;
	
	@Override
	protected Pane buildPage() {
		Pane p = new Pane();
//		p.getChildren().add(new Label("CUSTOMERS"));
		
		lnkCustomers = new BOLinkEx<>();
		
		BODbSetRef<BOCustomer> set = new BODbSetRef<>(BOCustomersSet.get(), new StoredProc(
				"select cus_id, cus_name_first, cus_name_last, cdt_id, cus_notes, cus_ref, cty_id " +
				"from CUS_customer " ));
		
		
		lnkCustomers.setLinkedObject(set);
			
		grdCustomer = new BOGrid<BOCustomer>("", lnkCustomers, 
				new String[]{"Customer First Name", "Customer Last Name"}, 
				new String[]{"", ""}, null);
		
		
		p.getChildren().add(grdCustomer);
		
		return p;
	}

	@Override
	protected void initialise(String... params) {
		lnkCustomers.ensureActive();
		grdCustomer.refresh();
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
