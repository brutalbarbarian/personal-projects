package com.lwan.finproj.app;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.finproj.bo.BOSource;
import com.lwan.finproj.bo.BOTransaction;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.controls.bo.BOGrid;
import com.lwan.javafx.controls.bo.BOGridControl;
import com.lwan.jdbc.StoredProc;

import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public class SourcePage extends BorderPane{
	BOGrid<BOSource> srcGrid;
	BOGridControl<BOSource> gridCtrl;
	BOLinkEx<BOSource> record; 
	
	ToolBar bottomBar;
	
	public SourcePage() {
		initControls();
	}
	
	protected void initControls() {
		BOLinkEx<BOSet<BOSource>> link = new BOLinkEx<>();
		srcGrid = new BOGrid<>(link, new String[]{"Source Name"}, 
				new String[]{"SourceName"}, 
				new boolean[]{true});
		srcGrid.setEditable(true);
		
		BOSourceSetRef set = new BOSourceSetRef();
		link.setLinkedObject(set);
		set.ensureActive();
		
		gridCtrl = new BOGridControl<>(srcGrid);
		record = gridCtrl.getSelectedLink();
		
		bottomBar = new ToolBar(gridCtrl.getPrimaryButton(), gridCtrl.getSecondaryButton(), gridCtrl.getRefreshButton());
		
		setCenter(srcGrid);
		setBottom(bottomBar);
	}
	
	protected class BOSourceSetRef extends BODbSetRef<BOSource> {
		public BOSourceSetRef() {
			super(BOSource.getSourceSet(), DbUtil.getStoredProc("PS_SRC_for_set"));
		}		
	}
}
