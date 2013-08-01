import com.lwan.javafx.controls.DateEdit;
import com.lwan.javafx.controls.panes.TVBox;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class Test extends Application {
	public void start(Stage primaryStage) throws Exception {
		DateEdit dp = new DateEdit();
		Button btn = new Button("Do something");
		
		dp.setAutoShowPopup(true);
		
		TVBox vb = new TVBox();
		
		vb.getChildren().addAll(dp, btn);
		
		Scene sc = new Scene(vb);
		sc.getStylesheets().add("resource/calendarstyle.css");
		
		primaryStage.setScene(sc);
		
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
