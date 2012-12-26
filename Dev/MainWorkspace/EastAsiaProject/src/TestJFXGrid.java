import javafx.stage.Stage;

import com.lwan.eaproj.app.App;
import com.lwan.eaproj.bo.cache.GUsers;


public class TestJFXGrid extends App{
	
	@Override
	public void start(Stage stage) throws Exception {
		GUsers.setActiveUser("sa", "password");
		stage.setTitle("TestJFXGrid");
		
		// TODO create grid here
		
		
		
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
