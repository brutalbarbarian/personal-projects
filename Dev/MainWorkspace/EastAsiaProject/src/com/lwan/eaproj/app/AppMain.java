package com.lwan.eaproj.app;

import com.lwan.eaproj.app.scenes.LoginScene;
import com.lwan.eaproj.app.scenes.MainScene;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AppMain extends App {
	public static final int STATE_LOGIN_SUCCESS = 1;
	public static final int STATE_LOGIN_CANCEL = 2;
	public static final int STATE_LOGOUT = 3;


	@Override
	public void start(Stage s) throws Exception {
		super.start(s);		
		
		s.setWidth(Constants.INI_WIDTH);
		s.setHeight(Constants.INI_HEIGHT);
		s.show();
	}
	
	protected void initialiseStage(Stage s) {
		s.setTitle(Lng._("East Asia Management System"));
		LoginScene login = new LoginScene();
		Scene mainScene = new Scene(login);
		s.setScene(mainScene);
	}
	
	
	protected void processState(int state) throws Exception {
		
		super.processState(state);

		switch (state) {
		case STATE_LOGIN_SUCCESS :
			// Change to main view
			// fade out...
			FadeTransition fade = new FadeTransition(Duration.millis(Constants.FADE_DURATION), 
					getMainStage().getScene().getRoot());
			fade.setFromValue(1.0);
			fade.setToValue(0.0);
			fade.setOnFinished(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					Scene scene = MainScene.createScene();
					System.out.println(scene);
					getMainStage().setScene(scene);
					Task<Void> t = new Task<Void>() {
						protected Void call() throws Exception {
							// Resize and centre on screen
							Stage stage = getMainStage();
							stage.setWidth(Constants.MAIN_WIDTH);
							stage.setHeight(Constants.MAIN_HEIGHT);
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

	public static void main(String[] args) {
		AppMain.launch(args);
	}
}
