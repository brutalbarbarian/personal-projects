package com.lwan.eaproj.app;

import java.util.Collection;

import com.lwan.eaproj.app.panes.PaneLogin;
import com.lwan.eaproj.app.panes.PaneMain;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.DbUtil;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AppEastAsia extends App {
	public static final int STATE_LOGIN_SUCCESS = 1 + APP_MESSAGE_LAST;
	public static final int STATE_LOGIN_CANCEL = 2 + APP_MESSAGE_LAST;
	public static final int STATE_LOGOUT = 3 + APP_MESSAGE_LAST;


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
		s.setScene(mainScene);
	}	
	
	protected void processState(int state) throws Exception {
		
		super.processState(state);

		switch (state) {
		case STATE_LOGIN_SUCCESS :
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
							stage.centerOnScreen();
							stage.show();
							
							return null;
						}				
					};
					Platform.runLater(t);
				}
			});
			fade.play();			
			
			break;				
		case STATE_LOGIN_CANCEL :
			requestTerminate();			
			break;
		case STATE_LOGOUT :
			
			break;
		}
	}
	
	public void init() throws Exception {
		super.init();
		
		DbUtil.setRootPackage("com.lwan.eaproj.sp");
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
