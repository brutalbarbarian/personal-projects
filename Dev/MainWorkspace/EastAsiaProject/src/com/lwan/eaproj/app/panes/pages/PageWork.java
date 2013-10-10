package com.lwan.eaproj.app.panes.pages;

import javafx.scene.Node;

import com.lwan.eaproj.app.PageConstants;
import com.lwan.eaproj.app.panes.PaneWorkEdit;
import com.lwan.eaproj.app.panes.PaneWorkFind;
import com.lwan.eaproj.bo.ref.BOWork;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.controls.pagecontrol.PageData;
import com.lwan.javafx.controls.pagecontrol.PageDataBase;
import com.lwan.util.containers.Params;

public class PageWork extends PageDataBase<Node>{
	private PageWorkEdit workEdit;
	private PageWorkFind workFind;

	public PageWork(PageData<?> parent) {
		super(Lng._("Work"), PageConstants.PAGE_WORK, parent);
		
		workEdit = new PageWorkEdit();
		workFind = new PageWorkFind();
		
		getChildren().add(workEdit);
		getChildren().add(workFind);
	}
	
	@Override
	public PageData<?> preferredChild() {
		return workFind;
	}

	protected class PageWorkEdit extends PageEditBase<BOWork, PaneWorkEdit>{

		public PageWorkEdit() {
			super(Lng._("Edit"), PageConstants.PAGE_WORK +
					PageConstants.SUBPAGE_EDIT, PageWork.this);
		}

		@Override
		protected PaneWorkEdit getPageNodeEx() {
			return new PaneWorkEdit();
		}	
	}
	
	protected class PageWorkFind extends PageDataBase<PaneWorkFind> {
		public PageWorkFind() {
			super(Lng._("Find"), PageConstants.PAGE_WORK +
					PageConstants.SUBPAGE_FIND, PageWork.this, true, null);
		}
		
		@Override
		public PaneWorkFind getPageNode(Params params) {
			return new PaneWorkFind();
		}
	}
}
