package test;

import com.lwan.javafx.art.CalendarArt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXArtTest extends Application{
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(new CalendarArt()));
		primaryStage.show();
	}

}
