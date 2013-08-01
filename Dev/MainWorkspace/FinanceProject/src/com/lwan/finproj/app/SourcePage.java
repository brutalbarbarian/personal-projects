package com.lwan.finproj.app;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BOSetRef;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.ModifiedEventType;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.finproj.bo.BOSource;
import com.lwan.finproj.bo.BOTransaction;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.other.BOGrid;
import com.lwan.javafx.controls.other.BOGridControl;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.TBorderPane;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.util.wrappers.CallbackEx;
import com.lwan.util.wrappers.Disposable;

import javafx.scene.control.ToolBar;
import javafx.util.Callback;

public class SourcePage extends TBorderPane implements Disposable{
	GridView<BOSource> srcGridView;
	BOLinkEx<BOSet<BOSource>> gridLink;
	BOGridControl<BOSource> gridCtrl;
	BOSourceSetRef gridSetRef;
	BOLinkEx<BOSource> record;
	
	BOLinkEx<BOSet<BOTransaction>> tranSetLink;
	BOTransactionSubSet transactionSet;
	GridView<BOTransaction> tranGridView;
	
	ToolBar bottomBar;
	
	public SourcePage() {
		initControls();
	}
	
	protected void initControls() {
		gridLink = new BOLinkEx<>();
		srcGridView = new GridView<>("SourcePageSourceGrid", gridLink,
				new String[]{"SourceName", BOGrid.PREFIX_CALCULATED + "TransactionCount"},
				LngUtil.translateArray(new String[]{"Source Name", "Transactions Count"}), 
				null);
		
		srcGridView.getGrid().setDisplayValueCallback(new CallbackEx<BOSource, String, String>(){
			public String call(BOSource a, String b) {
				int num = 0;
				for (BOTransaction trans : BOTransaction.getTransactionSet()) {
					if (trans.sourceID().equalValue(a.sourceID())) {
						num ++;
					}
				}
				
				return Integer.toString(num);
			}			
		});
		srcGridView.setGridEditable(true);
		
		gridSetRef = new BOSourceSetRef();
		gridLink.setLinkedObject(gridSetRef);
		gridSetRef.ensureActive();
		
		gridCtrl = srcGridView.getGridControl();
		gridCtrl.setAllowDeleteCallback(new Callback<BOSource, Boolean>() {

			@Override
			public Boolean call(BOSource item) {
				return BOTransaction.getTransactionSet().
						findChildByAttribute("SourceID", item.sourceID().getValue()) == null;
			}
		});
		
		record = gridCtrl.getSelectedLink();
		
		tranSetLink = new BOLinkEx<>();
		tranGridView = new GridView<>("SourcePageTranGrid", tranSetLink, 
				new String[]{"TransactionAmount", "TransactionNotes", "TransactionDate"},
				LngUtil.translateArray(new String[]{"Amount", "Notes", "Date"}),
				null);
		tranGridView.getGridControl().setEditable(false);
		
		transactionSet = new BOTransactionSubSet();
		tranSetLink.setLinkedObject(transactionSet);
		
		record.addListener(new ModifiedEventListener(){
			public void handleModified(ModifiedEvent event) {
				if (event.getType() == ModifiedEventType.Link) {
					transactionSet.reload();
					tranGridView.refreshGrid();
//					tranGrid.refresh();
				}
			}			
		});
		
		TVBox main = new TVBox();
		main.getChildren().addAll(srcGridView, tranGridView);
		
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
		
		srcGridView.dispose();
		tranGridView.dispose();
	}
}
