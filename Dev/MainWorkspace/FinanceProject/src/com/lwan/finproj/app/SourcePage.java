package com.lwan.finproj.app;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BOSetRef;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.finproj.bo.BOSource;
import com.lwan.finproj.bo.BOTransaction;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.BOGrid;
import com.lwan.javafx.controls.bo.BOGridControl;
import com.lwan.util.wrappers.Disposable;

import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class SourcePage extends BorderPane implements Disposable{
	BOGrid<BOSource> srcGrid;
	BOLinkEx<BOSet<BOSource>> gridLink;
	BOGridControl<BOSource> gridCtrl;
	BOSourceSetRef gridSetRef;
	BOLinkEx<BOSource> record;
	
	BOLinkEx<BOSet<BOTransaction>> tranSetLink;
	BOTransactionSubSet transactionSet;
	BOGrid<BOTransaction> tranGrid;
	
	ToolBar bottomBar;
	
	public SourcePage() {
		initControls();
	}
	
	protected void initControls() {
		gridLink = new BOLinkEx<>();
		srcGrid = new BOGrid<>("SourcePageSourceGrid", gridLink, 
				LngUtil.translateArray(new String[]{"Source Name"}),//, "Transaction Count"}, 
				new String[]{"SourceName"},//, "TransactionCount"}, 
				new boolean[]{true});//, false});
		srcGrid.setEditable(true);
		
		gridSetRef = new BOSourceSetRef();
		gridLink.setLinkedObject(gridSetRef);
		gridSetRef.ensureActive();
		
		gridCtrl = new BOGridControl<>(srcGrid);
		record = gridCtrl.getSelectedLink();
		
		tranSetLink = new BOLinkEx<>();
		tranGrid = new BOGrid<>("SourcePageTranGrid", tranSetLink, 
				LngUtil.translateArray(new String[]{
						"TransactionAmount", "TransactionNotes", "TransactionDate"}), 
				new String[]{"TransactionAmount", "TransactionNotes", "TransactionDate"}, 
				new boolean[]{false, false, false});
		tranGrid.setEditable(false);
		
		transactionSet = new BOTransactionSubSet();
		tranSetLink.setLinkedObject(transactionSet);
		
		record.addListener(new ModifiedEventListener(){
			public void handleModified(ModifiedEvent event) {
				if (event.getType() == ModifiedEvent.TYPE_LINK) {
					transactionSet.reload();
					tranGrid.refresh();
				}
			}			
		});
		
		VBox main = new VBox(2);
		main.getChildren().addAll(srcGrid, tranGrid);
		
		bottomBar = new ToolBar(gridCtrl.getPrimaryButton(), gridCtrl.getSecondaryButton(), gridCtrl.getRefreshButton());
		
		setCenter(main);
		setBottom(bottomBar);
	}
	
	protected class BOTransactionSubSet extends BOSetRef<BOTransaction> {
		public BOTransactionSubSet() {
			super(BOTransaction.getTransactionSet(), 
					new Callback<BOTransaction, Boolean>(){
						public Boolean call(BOTransaction transaction) {
							return record.getLinkedObject() == null ? false :
								record.getLinkedObject().sourceID().getValue().equals( 
								transaction.sourceID().getValue());
						}				
			}, BOSetRef.MODE_FILTER);
		}		
	}
	
	protected class BOSourceSetRef extends BODbSetRef<BOSource> {
		public BOSourceSetRef() {
			super(BOSource.getSourceSet(), DbUtil.getStoredProc("PS_SRC_for_set"));
		}		
	}

	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}
	
	@Override
	public void dispose() {
		gridLink.dispose();
		gridSetRef.dispose();
		record.dispose();
		
		tranSetLink.dispose();
		transactionSet.dispose();
//		BOCtrlUtil.buildAttributeLinks(paramBar);
//		BOCtrlUtil.buildAttributeLinks(grid);
		srcGrid.refresh();
		tranGrid.refresh();
		
		srcGrid.dispose();
		tranGrid.dispose();
	}
}
