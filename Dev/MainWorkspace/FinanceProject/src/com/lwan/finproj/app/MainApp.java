package com.lwan.finproj.app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.finproj.bo.BOSource;
import com.lwan.finproj.bo.BOTransaction;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BOGrid;
import com.lwan.javafx.controls.bo.BOTextField;

public class MainApp extends App{
	protected void initialiseStage(Stage stage) {
//		BOLinkEx<BOSet<BOSource>> link = new BOLinkEx<>();
//		BOGrid<BOSource> srcGrid = new BOGrid<>(link, new String[]{"SourceName"}, new String[]{"SourceName"}, new boolean[]{false});
//		link.setLinkedObject(BOSource.getSourceSet());		
		
		VBox pane = new VBox(2);
		
		BOLinkEx<BOSet<BOTransaction>> link = new BOLinkEx<>();
		final BOGrid<BOTransaction> tranGrid = new BOGrid<>(link, new String[]{"TransactionAmount",
				"TransactionNotes", "TransactionDate", "SourceName"}, 
				new String[]{"TransactionAmount", "TransactionNotes", "TransactionDate", 
				"SourceID"}, new boolean[]{true, true, true, true});
		tranGrid.setEditable(true);
		
//		tranGrid.getColumnByField("SourceID").setCellFactory(tranGrid.getComboBoxCellFactory(BOSource.getSourceSet()));
		
		link.setLinkedObject(BOTransaction.getTransactionSet());
		
		final BOLinkEx<BOTransaction> record = new BOLinkEx<>();

		
		final  GridPane grid = new GridPane();
		
		final BOTextField notes = new BOTextField(record, "TransactionNotes");
		final BOTextField date = new BOTextField(record, "TransactionDate");
		final BOTextField amount = new BOTextField(record, "TransactionAmount");
		final BOComboBox<Integer> name = new BOComboBox<>(record, "SourceID");
		final BOTextField txtSource = new BOTextField(record, "SourceID");
		
//		name.setAppendUniqueStrings(true);
		name.setEditable(true);
		
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

		
		tranGrid.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<BOTransaction>(){
					public void changed(
							ObservableValue<? extends BOTransaction> arg0,
							BOTransaction arg1, BOTransaction arg2) {
						record.setLinkedObject(arg2);
						
						BOCtrlUtil.buildAttributeLinks(grid);
					}
		});
		
		
		Button newRecord = new Button("create");
		Button save = new Button("Save");
		Button refresh = new Button("refresh");
		Button cancel = new Button("cancel");
		
		Button something = new Button("Do Something");
		
		something.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				record.getLinkedObject().sourceID().setValue(1);
			}			
		});
		
		refresh.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				tranGrid.refresh();
			}			
		});
		
		
		ToolBar tb = new ToolBar(something, newRecord, save, refresh, cancel);
		
		
		pane.getChildren().addAll(tranGrid, grid, tb);
		
		stage.setScene(new Scene(pane));
		
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
