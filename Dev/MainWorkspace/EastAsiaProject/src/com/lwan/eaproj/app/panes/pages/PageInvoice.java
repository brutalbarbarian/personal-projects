package com.lwan.eaproj.app.panes.pages;

import javafx.scene.Node;

import com.lwan.eaproj.app.PageConstants;
import com.lwan.eaproj.app.panes.PaneInvoiceEdit;
import com.lwan.eaproj.bo.ref.BOInvoice;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.controls.pagecontrol.PageData;
import com.lwan.javafx.controls.pagecontrol.PageDataBase;

public class PageInvoice extends PageDataBase<Node>{
	private PageInvoiceEdit invoiceEdit;
	
	public PageInvoice(PageData<?> parent) {
		super(Lng._("Invoice"), PageConstants.PAGE_INVOICES, parent);
		
		invoiceEdit = new PageInvoiceEdit();
		
		getChildren().add(invoiceEdit);
	}
	
	@Override
	public PageData<?> preferredChild() {
		// TODO change to find
		return invoiceEdit;
	}
	
	protected class PageInvoiceEdit extends PageEditBase<BOInvoice, PaneInvoiceEdit> {
		public PageInvoiceEdit() {
			super(Lng._("Edit"), PageConstants.PAGE_INVOICES +
					PageConstants.SUBPAGE_EDIT, PageInvoice.this);
		}

		@Override
		protected PaneInvoiceEdit getPageNodeEx() {
			return new PaneInvoiceEdit();
		}
		
	}

}
