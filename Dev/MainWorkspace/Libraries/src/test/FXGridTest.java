package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class FXGridTest extends Application{

	public static void main(String[]args) {
		launch(args);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage stage) throws Exception {
		TableView tv = new TableView();
		tv.getColumns().addAll(new TableColumn<>("test1"),
				new TableColumn<>("test2"),
				new TableColumn<>("test3"));
//		tv.setTableMenuButtonVisible(true);
		
		Scene sc = new Scene(tv);
		stage.setScene(sc);
		stage.show();
	}
	
}
