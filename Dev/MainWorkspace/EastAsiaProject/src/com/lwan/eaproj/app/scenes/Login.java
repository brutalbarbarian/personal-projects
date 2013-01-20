package com.lwan.eaproj.app.scenes;



import com.lwan.eaproj.app.AppMain;
import com.lwan.eaproj.bo.cache.GUsers;
import com.lwan.util.JavaFXUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Login extends VBox implements EventHandler<ActionEvent> {
	public Login() {
		initialiseControls();
		
	}

	protected Label lblTitle;
	protected TextField txtUser;
	protected PasswordField txtPassword;
	protected Button btnLogin, btnCancel;

	protected void initialiseControls() {
		lblTitle = new Label("Welcome Back...");
		lblTitle.setTextAlignment(TextAlignment.CENTER);
		lblTitle.setFont(new Font(22));
		
		GridPane grid = new GridPane();
		grid.setHgap(20);
		grid.setVgap(5);
		txtUser = new TextField();
		grid.add(new Label("Username"), 0, 0);
		grid.add(txtUser, 1, 0);
		
		txtPassword = new PasswordField();
		grid.add(new Label("Password"), 0, 1);
		grid.add(txtPassword, 1, 1);
		
		
//		Line line = new Line();
		
		HBox buttons = new HBox();
		buttons.setPadding(new Insets(10, 0, 0, 0));
		buttons.setSpacing(5);
		
		btnLogin = new Button("Login");
		btnCancel = new Button("Cancel");
		
		buttons.setAlignment(Pos.CENTER_RIGHT);
		buttons.getChildren().addAll(btnLogin, btnCancel);		
		
		
		getChildren().setAll(lblTitle, grid, buttons);
		setSpacing(5);
		setPadding(new Insets(10));	
		
		txtPassword.setOnAction(this);
		btnLogin.setOnAction(this);
		btnCancel.setOnAction(this);
		
		boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
			public void changed(ObservableValue<? extends Bounds> arg0,
					Bounds arg1, Bounds arg2) {
				lblTitle.setFont(new Font(Math.max(22, (arg2.getHeight() + arg2.getWidth()) / 20)));
			}			
		});
	}

	@Override
	public void handle(ActionEvent e) {
		if (e.getSource() == btnCancel) {
			AppMain.notifyState(AppMain.STATE_LOGIN_CANCEL);
		} else {
			if (GUsers.setActiveUser(txtUser.getText(), txtPassword.getText())) {
				AppMain.notifyState(AppMain.STATE_LOGIN_SUCCESS);
			} else {
				JavaFXUtil.ShowErrorDialog(getScene().getWindow(), "Invalid username or password.");
			}
		}
	}
	
}
