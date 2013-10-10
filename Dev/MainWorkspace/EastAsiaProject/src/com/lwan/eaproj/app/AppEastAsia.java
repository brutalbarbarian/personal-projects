package com.lwan.eaproj.app;

import java.util.Collection;

import com.lwan.eaproj.app.panes.PaneLogin;
import com.lwan.eaproj.app.panes.PaneMain;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;
import com.lwan.util.containers.Params;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AppEastAsia extends App {
	private enum AppMessages {
		loginSuccess, loginCancel, logout, changePage;
	}
	
	public static final AppMessages STATE_LOGIN_SUCCESS = AppMessages.loginSuccess;
	public static final AppMessages STATE_LOGIN_CANCEL = AppMessages.loginCancel;
	public static final AppMessages STATE_LOGOUT = AppMessages.logout;
	public static final AppMessages PAGE_CHANGE_REQUEST = AppMessages.changePage;


	@Override
	public void start(Stage s) throws Exception {
		super.start(s);		
		
		s.setWidth(EAConstants.INI_WIDTH);
		s.setHeight(EAConstants.INI_HEIGHT);
		s.show();
	}
	
	protected void initialiseStage(Stage s) {
		s.setTitle(Lng._("East Asia Management System"));
		PaneLogin login  = new PaneLogin();
		Scene mainScene = new Scene(login);
		mainScene.getStylesheets().addAll(getStyleshets());
		s.setScene(mainScene);		
	}	
	
	protected void processState(Enum<?> state, Params params) throws Exception {
		
		super.processState(state, params);

		if (state == STATE_LOGIN_SUCCESS) {
			// Change to main view
			// fade out...
			FadeTransition fade = new FadeTransition(Duration.millis(EAConstants.FADE_DURATION), 
					getMainStage().getScene().getRoot());
			fade.setFromValue(1.0);
			fade.setToValue(0.0);
			fade.setOnFinished(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					Scene scene = PaneMain.createScene();
					getMainStage().setScene(scene);
					Task<Void> t = new Task<Void>() {
						protected Void call() throws Exception {
							// Resize and centre on screen
							Stage stage = getMainStage();
							stage.setWidth(EAConstants.MAIN_WIDTH);
							stage.setHeight(EAConstants.MAIN_HEIGHT);
							stage.setMinWidth(EAConstants.MAIN_MIN_WIDTH);
							stage.setMinHeight(EAConstants.MAIN_MIN_HEIGHT);
							stage.centerOnScreen();
							stage.show();

							// Attempt to load the alerts page
							notifyMessage(PAGE_CHANGE_REQUEST, new Params(PageConstants.PAGE_NAME, 
									PageConstants.PAGE_ALERTS));
							return null;
						}				
					};
					Platform.runLater(t);
				}
			});
			fade.play();			
			
		} else if (state == STATE_LOGIN_CANCEL) {
			requestTerminate();			
		} else if (state == STATE_LOGOUT) {
			// TODO
		} else if (state == PAGE_CHANGE_REQUEST) {
			PaneMain pane = (PaneMain) getMainStage().getScene().getRoot();
			pane.getPageControl().trySetActivePage(
					params.getValueDefault(PageConstants.PAGE_NAME, ""), params);
		}
	}

	public static void main(String[] args) {
		AppEastAsia.launch(args);
	}

	@Override
	protected void initStylesheets(Collection<String> stylesheets) {
		stylesheets.add("styles/mainapp.css");
		stylesheets.add("styles/boapp.css");
		stylesheets.add("styles/chartcontrol.css");
		stylesheets.add("styles/calendarstyle.css");
	}
}
