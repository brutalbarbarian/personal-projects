package com.lwan.eaproj.app.panes.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;

import com.lwan.eaproj.app.PageConstants;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.controls.pagecontrol.PageData;
import com.lwan.javafx.controls.pagecontrol.PageDataBase;
import com.lwan.util.containers.Params;

public class PageAlerts extends PageDataBase<Node>{

	public PageAlerts(PageData<?> parent) {
		super(Lng._("Alerts"), PageConstants.PAGE_ALERTS, parent, true, null);
	}

	@Override
	public Node getPageNode(Params params) {
		return new Label("ALERTS");
	}

}
