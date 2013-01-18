package com.lwan.eaproj.app.scenes;


import java.awt.Font;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;

public class Login extends VBox {
	public Login() {
		initialiseControls();
		
	}
	
	protected TextField txtUser;
	protected PasswordField txtPassword;

	protected void initialiseControls() {
		Label label = new Label("Welcome Back...");
		label.setTextAlignment(TextAlignment.CENTER);
		label.setFont(new javafx.scene.text.Font(Font.SANS_SERIF, 22));
		
		GridPane grid = new GridPane();
		grid.setHgap(20);
		grid.setVgap(5);
		txtUser = new TextField();
		grid.add(new Label("Username"), 0, 0);
		grid.add(txtUser, 1, 0);
		
		txtPassword = new PasswordField();
		grid.add(new Label("Password"), 0, 1);
		grid.add(txtPassword, 1, 1);
		
		Line line = new Line();
		
		HBox buttons = new HBox();
		
		
		getChildren().setAll(label, grid, line, buttons);
		setSpacing(5);
		setPadding(new Insets(10));
	}
	
	
}
