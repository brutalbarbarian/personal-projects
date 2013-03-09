package com.lwan.finproj.app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.finproj.bo.BOSource;
import com.lwan.finproj.bo.BOTransaction;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BODatePicker;
import com.lwan.javafx.controls.bo.BOGrid;
import com.lwan.javafx.controls.bo.BOGridControl;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.util.wrappers.ResultCallback;

public class MainApp extends App{
	protected void initialiseStage(Stage stage) {	
		
		
		VBox pane = new VBox(2);
		
		BOLinkEx<BOSet<BOTransaction>> link = new BOLinkEx<>();
		final BOGrid<BOTransaction> tranGrid = new BOGrid<>(link, new String[]{"TransactionAmount",
				"TransactionNotes", "TransactionDate", "SourceName"}, 
				new String[]{"TransactionAmount", "TransactionNotes", "TransactionDate", 
				"SourceID"}, new boolean[]{true, true, true, true});
		tranGrid.setEditable(true);
		
		link.setLinkedObject(BOTransaction.getTransactionSet());
		
		BOGridControl<BOTransaction> gridCtrl = new BOGridControl<>(tranGrid);
		
		final BOLinkEx<BOTransaction> record = gridCtrl.getSelectedLink();

		
		final  GridPane grid = new GridPane();
		
		final BOTextField notes = new BOTextField(record, "TransactionNotes");
		final BODatePicker date = new BODatePicker(record, "TransactionDate");
		final BOTextField amount = new BOTextField(record, "TransactionAmount");
		final BOComboBox<Integer> name = new BOComboBox<>(record, "SourceID");
		final BOTextField txtSource = new BOTextField(record, "SourceID");
		
		ResultCallback<BoundControl<?>> initSource = new ResultCallback<BoundControl<?>>() {
			@SuppressWarnings("unchecked")
			public void call(BoundControl<?> result) {
				final BOComboBox<Integer> cb = (BOComboBox<Integer>)result;
				cb.setEditableEx(true);
				cb.setAppendUniqueStrings(true);
				
				// Dynamically create sources on the fly
				cb.setUniqueStringConverter(new Callback<String, Integer>() {
					public Integer call(String name) {
						if (name.length() > 0 && 
								BOSource.getSourceSet().findChildByAttribute("SourceName", name) == null) {
							BOSource src = BOSource.getSourceSet().createNewChild();
							src.sourceName().setValue(name);
							src.trySave();
							return src.sourceID().getValue();
						} else {
							// just return whatever was already selected...
							return cb.getSelected();
						}
					}					
				});
			}
		};
		
		initSource.call(name);
		
		// set dynamic source
		name.setSource(BOSource.getSourceSet(), "SourceID", "SourceName");
		
		grid.add(new Label("Date"), 0, 0);
		grid.add(new Label("Source"), 0, 1);
		grid.add(new Label("Amount"), 0, 2);
		grid.add(new Label("Notes"), 0, 3);
		grid.add(new Label("SourceID"), 0, 4);
		
		grid.add(date, 1, 0);
		grid.add(name, 1, 1);
		grid.add(amount, 1, 2);
		grid.add(notes, 1, 3);
		grid.add(txtSource, 1, 4);

		
		record.LinkedObjectProperty().addListener(new ChangeListener<BOTransaction>() {
			public void changed(ObservableValue<? extends BOTransaction> arg0,
					BOTransaction arg1, BOTransaction arg2) {
				BOCtrlUtil.buildAttributeLinks(grid);
			}			
		});
		
		tranGrid.getColumnByField("SourceID").setAsCombobox(BOSource.getSourceSet(), "SourceID", "SourceName");
		tranGrid.getColumnByField("SourceID").setCtrlPropertySetter(initSource);
		tranGrid.getColumnByField("TransactionDate").setAsDatePicker();
	
		ToolBar tb = new ToolBar(gridCtrl.getPrimaryButton(), gridCtrl.getSecondaryButton(), gridCtrl.getRefreshButton());
		
		pane.getChildren().addAll(tranGrid, grid, tb);
		
		Scene scene = new Scene(pane);
		scene.getStylesheets().add("resource/calendarstyle.css");
		
		stage.setScene(scene);
		
		tranGrid.refresh();
		
		stage.show();
	}
	
	public void init() throws Exception {
		super.init();
		
		DbUtil.setRootPackage("com.lwan.finproj.sp");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
