package com.lwan.eaproj.app;

import com.lwan.eaproj.app.scenes.Login;
import com.lwan.eaproj.app.scenes.MainApp;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppMain extends App {
	public static final int STATE_LOGIN_SUCCESS = 1;
	public static final int STATE_LOGIN_CANCEL = 2;


	@Override
	public void start(Stage s) throws Exception {
		super.start(s);
		
		Login login = new Login();
		Scene mainScene = new Scene(login);
		s.setScene(mainScene);
		
		s.show();		
	}
	
	protected void initialiseStage(Stage s) {
		s.setTitle("East Asia Management System");
	}
	
	
	protected void processState(int state) throws Exception {
//		AppMain app = (AppMain)getApp();
		super.processState(state);

		switch (state) {
		case STATE_LOGIN_SUCCESS :
			// Change to main view
			getMainStage().setScene(new Scene(new MainApp()));				
			
			break;				
		case STATE_LOGIN_CANCEL :
			terminate();
		}
	}

	public static void main(String[] args) {
		AppMain.launch(args);
	}
}
