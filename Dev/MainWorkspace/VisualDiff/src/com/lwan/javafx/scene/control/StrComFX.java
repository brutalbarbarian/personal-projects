package com.lwan.javafx.scene.control;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.lwan.strcom.DiffInfo;
import com.lwan.strcom.RunnerForLines;
import com.lwan.util.IOUtil;
import com.lwan.util.JavaFXUtil;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooserBuilder;
import javafx.stage.Stage;

/**
 * Primary entry point for the application using JavaFX
 * 
 * @author Brutalbarbarian
 *
 */
public class StrComFX extends Application implements javafx.event.EventHandler<ActionEvent> {
	//controls
	Label lblOld, lblNew;
	TextField txtOld, txtNew;
	Button btnOld, btnNew, btnComp, btnPrev, btnNext;
	ComparisonPaneFX cpComp;
	Stage stgMain;
	
	//utilities
	FileChooserBuilder fcbFiles;

	public void start(Stage primaryStage) throws Exception {
		initialiseUtils();
		AnchorPane root = initialiseScene(primaryStage);
		initialiseComponents(root);
		
		// initialise stage settings
		primaryStage.setWidth(1000);
		primaryStage.setHeight(600);
		primaryStage.show();
	}
	
	protected void initialiseUtils() {
		fcbFiles = FileChooserBuilder.create();
		fcbFiles.initialDirectory(new File(System.getProperty("user.dir")));
	}
	
	protected void initialiseComponents(AnchorPane root) {
		BorderPane borderPane = new BorderPane();
		AnchorPane.setTopAnchor(borderPane, 0d);
		AnchorPane.setBottomAnchor(borderPane, 0d);
		AnchorPane.setLeftAnchor(borderPane, 0d);
		AnchorPane.setRightAnchor(borderPane, 0d);
		root.getChildren().add(borderPane);
		
		//setup toolbar
		lblOld = new Label("Old File:");
		txtOld = new TextField();
		btnOld = new Button("...");
		lblNew = new Label("New File:");
		txtNew = new TextField();
		btnNew = new Button("...");
		btnComp = new Button("Compare");
		Separator sep = new Separator(Orientation.VERTICAL);
		btnPrev = new Button("<");
		btnNext = new Button(">");
		
		//setup event handlers
		btnOld.setOnAction(this);
		btnNew.setOnAction(this);
		btnComp.setOnAction(this);
		
		ToolBar toolbar = new ToolBar(lblOld, txtOld, btnOld, 
				lblNew, txtNew, btnNew, btnComp, sep,
				btnPrev, btnNext);
		borderPane.setTop(toolbar);
		
		//setup main display pane
		cpComp = new ComparisonPaneFX();
		borderPane.setCenter(cpComp);
	}
	
	protected AnchorPane initialiseScene (Stage stage) {
		stgMain = stage;
		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		
		return root;
	}

	@Override
	public void handle(ActionEvent event) {
		Object src = event.getSource();
		if (src == btnOld || src == btnNew) {
			FileChooser chooser = fcbFiles.build();
			File choosen = chooser.showOpenDialog(stgMain);

			if (choosen != null) {
				if (src == btnOld) {
					txtOld.setText(choosen.getAbsolutePath());
				} else if (src == btnNew) {
					txtNew.setText(choosen.getAbsolutePath());
				}
				fcbFiles.initialDirectory(choosen.getAbsoluteFile().getParentFile());
			}
		} else if (src == btnComp) {
			Path f1 = Paths.get(txtOld.getText());
			Path f2 = Paths.get(txtNew.getText());

			if (f1.toFile().exists() && f2.toFile().exists()) {
				List<String> s1 = IOUtil.readAllLines(f1, IOUtil.CHARSET_DEFAULT_WINDOWS, IOUtil.CHARSET_DEFAULT_UBUNTU);
				List<String> s2 = IOUtil.readAllLines(f2, IOUtil.CHARSET_DEFAULT_WINDOWS, IOUtil.CHARSET_DEFAULT_UBUNTU);

				List<DiffInfo> res = RunnerForLines.run(s1, s2);

				//Populate results lists
				cpComp.setData(s1, s2, res);

				btnNext.setDisable(!cpComp.hasNextHighlight());
				btnPrev.setDisable(!cpComp.hasPrevHighlight());
			} else {
				JavaFXUtil.ShowErrorDialog(stgMain, "Cannot read input files");
			}
		}
	}

	public static void launchApp(String[] args) {
		launch(args);
	}
}
