package com.lwan.finproj.app;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.util.wrappers.Disposable;

public class MainApp extends App{
	ToggleButton btnTrans, btnSource;
	ToggleGroup toggleGroup;
	boolean toggling;
	BorderPane mainPane;
	
	protected void initialiseStage(Stage stage) {
		btnTrans = new ToggleButton("Transactions");
		btnSource = new ToggleButton("Source");
		
		toggleGroup = new ToggleGroup();
		
		ToolBar bar = ToolBarBuilder.create().items(btnTrans, btnSource).build();
		
		mainPane = new BorderPane();
		
		BorderPane bp = new BorderPane();
		bp.setTop(bar);
		bp.setCenter(mainPane);

		btnTrans.setToggleGroup(toggleGroup);
		btnSource.setToggleGroup(toggleGroup);
		
		toggling = false;
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){

			@Override
			public void changed(ObservableValue<? extends Toggle> arg0,
					Toggle arg1, Toggle arg2) {
				if (!toggling) {
					toggling = true;
					try {
						if (arg2 == null) {
							toggleGroup.selectToggle(arg1);
						} else if (arg2 != arg1) {
							// change page
							if (mainPane.getCenter() != null) {
								((Disposable)mainPane.getCenter()).dispose();
							}
							if (arg2 == btnSource) {
								mainPane.setCenter(new SourcePage());
							} else if (arg2 == btnTrans) {
								mainPane.setCenter(new TransactionPage());
							}							
						} else {
							// ignore...
						}
					} finally {						
						toggling = false;
					}
				}
			}
			
		});
		
		toggleGroup.selectToggle(btnTrans);
		
		
		Scene scene = new Scene(bp);

		scene.getStylesheets().add("resource/calendarstyle.css");
		scene.getStylesheets().add("resource/chartcontrol.css");
		
		stage.setScene(scene);
		
		stage.show();
	}
	
	public void init() throws Exception {
		super.init();
		
		DbUtil.setRootPackage("com.lwan.finproj.sp");
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void stop() throws Exception {
		if (mainPane.getCenter() != null) {
			((Disposable)mainPane.getCenter()).dispose();
		}
		
		super.stop();
	}
}
