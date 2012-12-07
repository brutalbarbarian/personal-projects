import com.lwan.bo.BOLinkEx;
import com.lwan.eaproj.bo.BOContactDetails;
import com.lwan.eaproj.bo.cache.GUsers;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.jdbc.GConnection;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class TestJFX extends Application{
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
		String driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
		String fileName = "C:/Users/Brutalbarbarian/Dropbox/EastAsiaProject/EastAsiaDB.mdb";
		String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ="+fileName;
		GConnection.initialise(driverName, url, "", "");

		
		final BOLinkEx<BOContactDetails> link = new BOLinkEx<>();
		final BOTextField txtField = new BOTextField(link, "Address1");
		final BOContactDetails cdt = new BOContactDetails(null);
		cdt.contactDetailsID.setValue(35);
		cdt.ensureActive();
		
		Button btn = new Button("Do Something!!!");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				System.out.println("Do something");
				link.setLinkedObject(cdt);				
				
				txtField.dataBindingProperty().buildAttributeLinks();
				
				System.out.println(txtField.dataBindingProperty().getValue());
			}			
		});
		Button btnPrint = new Button("Print BO");
		btnPrint.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				System.out.println(cdt.toStringAll());
			}			
		});
		
		VBox box = new VBox();
		box.getChildren().addAll(txtField, HBoxBuilder.create().children(btn, btnPrint).build());
		Scene sc = new Scene(box);
		stage.setScene(sc);
//		stage.setWidth(400);
//		stage.setHeight(400);
		
		stage.show();
	}
	
	public void stop() throws Exception {
		GUsers.clearActiveUser();
		GConnection.uninitialise();
		
		super.stop();
	}
}
