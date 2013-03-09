import com.lwan.javafx.controls.DatePicker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Test extends Application {
	public void start(Stage primaryStage) throws Exception {
		DatePicker dp = new DatePicker();
		Button btn = new Button("Do something");
		
		dp.setAutoShowPopup(true);
		
		VBox vb = new VBox();
		
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
