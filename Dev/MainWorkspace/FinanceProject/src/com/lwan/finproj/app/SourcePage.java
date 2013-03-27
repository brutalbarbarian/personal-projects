package com.lwan.finproj.app;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.finproj.bo.BOSource;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.controls.bo.BOGrid;
import com.lwan.javafx.controls.bo.BOGridControl;
import com.lwan.util.wrappers.Freeable;

import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public class SourcePage extends BorderPane implements Freeable{
	BOGrid<BOSource> srcGrid;
	BOLinkEx<BOSet<BOSource>> gridLink;
	BOGridControl<BOSource> gridCtrl;
	BOSourceSetRef gridSetRef;
	BOLinkEx<BOSource> record; 
	
	ToolBar bottomBar;
	
	public SourcePage() {
		initControls();
	}
	
	protected void initControls() {
		gridLink = new BOLinkEx<>();
		srcGrid = new BOGrid<>(gridLink, new String[]{"Source Name", "Transaction Count"}, 
				new String[]{"SourceName", "TransactionCount"}, 
				new boolean[]{true, false});
		srcGrid.setEditable(true);
		
		gridSetRef = new BOSourceSetRef();
		gridLink.setLinkedObject(gridSetRef);
		gridSetRef.ensureActive();
		
		gridCtrl = new BOGridControl<>(srcGrid);
		record = gridCtrl.getSelectedLink();
		
		bottomBar = new ToolBar(gridCtrl.getPrimaryButton(), gridCtrl.getSecondaryButton(), gridCtrl.getRefreshButton());
		
		setCenter(srcGrid);
		setBottom(bottomBar);
	}
	
//	protected class BOSourceSetRef
	
	protected class BOSourceSetRef extends BODbSetRef<BOSource> {
		public BOSourceSetRef() {
			super(BOSource.getSourceSet(), DbUtil.getStoredProc("PS_SRC_for_set"));
		}		
	}

	protected void finalize() throws Throwable {
		free();
		super.finalize();
	}
	
	@Override
	public void free() {
		gridLink.free();
		gridSetRef.free();
		record.free();
//		BOCtrlUtil.buildAttributeLinks(paramBar);
//		BOCtrlUtil.buildAttributeLinks(grid);
		srcGrid.refresh();
	}
}
