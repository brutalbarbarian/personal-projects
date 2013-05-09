package test;
import com.lwan.javafx.scene.control.AlignedControlCell;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class AlignedLayoutTest extends Application{
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		VBox box = new VBox();
		AlignedControlCell a1 = new AlignedControlCell("short", new TextField(), box);
		AlignedControlCell a2 = new AlignedControlCell("very long", new TextField(), box);
		
		box.getChildren().addAll(a1, a2);
		
		Scene sc = new Scene(box);
		
		stage.setScene(sc);
		stage.show();
	}
	
}
