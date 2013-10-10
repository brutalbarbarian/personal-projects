import com.lwan.javafx.controls.ComboBox;
import com.lwan.javafx.controls.DateEdit;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.TTitledPane;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.javafx.scene.control.AlignedControlCell;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Test extends Application {
	public void start(Stage primaryStage) throws Exception {
		VBox box = new VBox();
		
		HBox h = new HBox();
		
		TextField tf1 = new TextField();
		TextField tf2 = new TextField();
		
		ComboBox cb1 = new ComboBox();
		ComboBox cb2 = new ComboBox();
		
		cb1.addAllItems(new String[]{"aaaa", "bbbb", "cccc"});

		VBox v1 = new VBox();
		VBox v2 = new VBox();
		
		AlignedControlCell atf1 = new AlignedControlCell("One", tf1, v1);
		AlignedControlCell atf2 = new AlignedControlCell("Two", tf2, v2);
		AlignedControlCell acb1 = new AlignedControlCell("One", cb1, v1);
		AlignedControlCell acb2 = new AlignedControlCell("Two", cb2, v2);
		
		v1.getChildren().addAll(atf1, acb1);
		v2.getChildren().addAll(atf2, acb2);
		
		TitledPane tp1 = new TTitledPane("One", v1);
		TTitledPane tp2 = new TTitledPane("Two", v2);
		
		h.getChildren().addAll(tp1, tp2);
		HBox.setHgrow(tp1, Priority.ALWAYS);
		HBox.setHgrow(tp2, Priority.ALWAYS);
		
		TableView tv1 = new TableView<>();
		TableView tv2 = new TableView<>();
		
		tv1.getColumns().add(new TableColumn("col1"));
		
		BorderPane bp1, bp2;
		bp1 = new BorderPane();
		bp2 = new BorderPane();
		bp1.setCenter(tv1);
		bp2.setCenter(tv2);
		
		HBox h2 = new HBox();
		h2.getChildren().addAll(bp1, bp2);
		HBox.setHgrow(bp1, Priority.ALWAYS);
		HBox.setHgrow(bp2, Priority.ALWAYS);
		
		box.getChildren().addAll(h, h2);
		
		Scene sc = new Scene(box);
//		sc.getStylesheets().add("resource/calendarstyle.css");
		
		primaryStage.setScene(sc);
		
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
