package com.lwan.eaproj.app.panes.pages;

import javafx.scene.Node;

import com.lwan.eaproj.app.PageConstants;
import com.lwan.eaproj.app.panes.PaneCustomerEdit;
import com.lwan.eaproj.app.panes.PaneCustomerFind;
import com.lwan.eaproj.bo.ref.BOCustomer;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.controls.pagecontrol.PageData;
import com.lwan.javafx.controls.pagecontrol.PageDataBase;
import com.lwan.util.containers.Params;

public class PageCustomer extends PageDataBase<Node> {
	private PageCustomerEdit customerEdit;
	private PageCustomerFind customerFind;
	
	public PageCustomer(PageData<?> parent) {
		super(Lng._("Customer"), PageConstants.PAGE_CUSTOMERS, parent);
		
		customerEdit = new PageCustomerEdit();
		customerFind = new PageCustomerFind();
		
		getChildren().add(customerEdit);
		getChildren().add(customerFind);
	}
	
	@Override
	public PageData<?> preferredChild() {
		return customerFind;
	}
	
	protected class PageCustomerEdit extends PageEditBase<BOCustomer, PaneCustomerEdit> {
		public PageCustomerEdit(){
			super(Lng._("Edit"), PageConstants.PAGE_CUSTOMERS +
					PageConstants.SUBPAGE_EDIT, PageCustomer.this);
		}

		@Override
		protected PaneCustomerEdit getPageNodeEx() {
			return new PaneCustomerEdit();
		}
	}
	
	protected class PageCustomerFind extends PageDataBase<PaneCustomerFind> {
		public PageCustomerFind() {
			super(Lng._("Find"), PageConstants.PAGE_CUSTOMERS +
					PageConstants.SUBPAGE_FIND, PageCustomer.this, true, null);
		}
		
		@Override
		public PaneCustomerFind getPageNode(Params params) {
			return new PaneCustomerFind();
		}
	}
}
