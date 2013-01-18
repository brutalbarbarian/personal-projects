package com.lwan.eaproj.app;

import com.lwan.eaproj.app.scenes.Login;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppMain extends App {
	public static final int STATE_LOGIN_SUCCESS = 0;
	public static final int STATE_LOGIN_CANCEL = 1;
	
	protected Stage mainStage;

	@Override
	public void start(Stage s) throws Exception {
		mainStage = s;
		
		Login login = new Login();
		Scene mainScene = new Scene(login);
		s.setScene(mainScene);
		
		s.show();		
	}
	
	public static void NotifyState(int state) {
		AppMain app = (AppMain)getApp();

		try {
			switch (state) {
			case STATE_LOGIN_SUCCESS :
				// Change to main view
//				app.mainStage.
				
			case STATE_LOGIN_CANCEL :
				app.stop();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		AppMain.launch(args);
	}
}
