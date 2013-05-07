import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.javafx.controls.bo.BOTextField;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.Stage;


public class KeyboardTest extends Application{
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		final BOAttribute<?> attr = new BOAttribute<>(null, "", AttributeType.Integer);
		BOLinkEx<BOAttribute<?>> link = new BOLinkEx<>();
		link.setLinkedObject(attr);
		
		BOTextField tf = new BOTextField(link, "");
		tf.dataBindingProperty().buildAttributeLinks();
		
		Button btn = new Button("do something");
		btn.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				System.out.println(attr.asString());
			}			
		});
		
		Scene sc = new Scene(VBoxBuilder.create().children(tf, btn).build());
		sc.getStylesheets().add("resource/boapp.css");
		
		
		primaryStage.setScene(sc);
		primaryStage.show();
		
		
	}
}
