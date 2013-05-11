package com.lwan.eaproj.app.panes;

import com.lwan.eaproj.app.AppMain;
import com.lwan.eaproj.bo.ref.BOUserSet;
import com.lwan.javafx.app.Lng;
import com.lwan.util.FxUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class PaneLogin extends StackPane implements EventHandler<ActionEvent> {
	public PaneLogin() {
		initialiseControls();
		
	}

	protected Label lblTitle;
	protected TextField txtUser;
	protected PasswordField txtPassword;
	protected Button btnLogin, btnCancel;

	protected void initialiseControls() {
		lblTitle = new Label(Lng._("Welcome Back..."));
		lblTitle.setTextAlignment(TextAlignment.CENTER);
		lblTitle.setFont(new Font(22));
		
		GridPane grid = new GridPane();
		grid.setHgap(20);
		grid.setVgap(5);
		txtUser = new TextField();
		grid.add(new Label(Lng._("Username")), 0, 0);
		grid.add(txtUser, 1, 0);
		
		txtPassword = new PasswordField();
		grid.add(new Label(Lng._("Password")), 0, 1);
		grid.add(txtPassword, 1, 1);
		
		
//		Line line = new Line();
		
		HBox buttons = new HBox();
		buttons.setPadding(new Insets(10, 0, 0, 0));
		buttons.setSpacing(5);
		
		btnLogin = new Button(Lng._("Login"));
		btnCancel = new Button(Lng._("Cancel"));
		
		buttons.setAlignment(Pos.CENTER_RIGHT);
		buttons.getChildren().addAll(btnLogin, btnCancel);		
		
		
		VBox main = new VBox();
		
		main.getChildren().setAll(lblTitle, grid, buttons);
		main.setSpacing(5);
		main.setPadding(new Insets(10));
		main.setAlignment(Pos.CENTER);
		
		Group p = new Group();
		p.getChildren().add(main);
		getChildren().add(p);
		
		txtPassword.setOnAction(this);
		txtUser.setOnAction(this);
		btnLogin.setOnAction(this);
		btnCancel.setOnAction(this);
		
//		boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
//			public void changed(ObservableValue<? extends Bounds> arg0,
//					Bounds arg1, Bounds arg2) {
//				lblTitle.setFont(new Font(Math.max(22, (arg2.getHeight() + arg2.getWidth()) / 20)));
//			}
//		});
	}

	@Override
	public void handle(ActionEvent e) {		
		if (e.getSource() == btnCancel) {
			AppMain.notifyState(AppMain.STATE_LOGIN_CANCEL);
		} else {
			if (BOUserSet.setActiveUser(txtUser.getText(), txtPassword.getText())) {
				AppMain.notifyState(AppMain.STATE_LOGIN_SUCCESS);	
			} else {
				FxUtils.ShowErrorDialog(getScene().getWindow(), 
						Lng._("Invalid username or password."));
			}
		}
	}
	
}