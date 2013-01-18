import java.util.Locale;

import com.lwan.bo.BOLinkEx;
import com.lwan.eaproj.app.App;
import com.lwan.eaproj.app.Lng;
import com.lwan.eaproj.bo.BOEmployee;
import com.lwan.eaproj.bo.cache.GUsers;
import com.lwan.eaproj.util.LngUtil;
import com.lwan.javafx.controls.CheckBox;
import com.lwan.javafx.controls.ComboBox;
import com.lwan.javafx.controls.MaskedTextField;
import com.lwan.javafx.controls.bo.BOCheckBox;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.jdbc.GConnection;
import com.lwan.util.CollectionUtil;
import com.sun.org.apache.bcel.internal.generic.ALOAD;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;


public class TestJFX extends App{
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		
//		App.initaliseApplication();
//		String driverName = "sun.jdbc.odbc.JdbcOdbcDriver";
//		String fileName = "C:/Users/Brutalbarbarian/Dropbox/EastAsiaProject/EastAsiaDB.mdb";
//		String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ="+fileName;
//		GConnection.initialise(driverName, url, "", "");

		final BOLinkEx<BOEmployee> link = new BOLinkEx<>();
		final BOTextField txtField = new BOTextField(link, "PayMonthly");
		
//		txtField.focusedProperty().addListener(new ChangeListener<Boolean>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Boolean> arg0,
//					Boolean arg1, Boolean arg2) {
//				// TODO Auto-generated method stub
//				System.out.println("Changed");
//			}
//			
//		});
//		txtField.focusedProperty().addListener(new InvalidationListener() {
//			
//			@Override
//			public void invalidated(Observable arg0) {
//				// TODO Auto-generated method stub
//				System.out.println("Invalidated");
//			}
//		});
//		
		
		final BOComboBox<Boolean> cb = new BOComboBox<>(link, "IsActive");
//		cb.addAllItems(LngUtil.translate(new String[] {"<No Value>", "One", "Two", "Three", "Four"}));
//		cb.setAppendUniqueStrings(true);
//		cb.uniqueStringConverterProperty().setValue(new Callback<String, String>() {
//			public String call(String arg0) {
//				System.out.println(arg0);
//				return arg0;
//			}			
//		});
		cb.addAllItems(new Boolean[]{true, false}, 
				LngUtil.translate(new String[]{"Yes", "No"}));
		
//		cb.addAllItems(new Integer[]{null, 1, 2, 3, 4}, 
//				LngUtil.translate(new String[] {"<No Value>", "One", "Two", "Three", "Four"}));
		
		final BOCheckBox chk = new BOCheckBox("TEST BOX", link, "IsActive");
//		chk.setAllowIndeterminate(true);
		
//		final BOContactDetails cdt = new BOContactDetails(null);
//		cdt.contactDetailsID.setValue(35);
//		cdt.ensureActive();
//		final BOCustomer cus = new BOCustomer(null);
//		cus.customerID.setValue(4);
//		cus.ensureActive();
		final BOEmployee emp = new BOEmployee(null);
		emp.employeeID().setValue(20);
		emp.ensureActive();
		
		
		Button btn = new Button("Do Something!!!");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				System.out.println("Do something");
				
//				cb.setSelected(null);
				System.out.println(cb.getSelected());
				
				link.setLinkedObject(emp);
				
				txtField.dataBindingProperty().buildAttributeLinks();
				cb.dataBindingProperty().buildAttributeLinks();
				chk.dataBindingProperty().buildAttributeLinks();
				
//				System.out.println(txtField.dataBindingProperty().getValue());
			}			
		});
		Button btnPrint = new Button("Print BO");
		btnPrint.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				System.out.println(emp.toStringAll());
			}			
		});
		
//		MaskedTextField tf = new MaskedTextField();
//		tf.actualValueProperty().addListener(new ChangeListener<String>(){
//			public void changed(ObservableValue<? extends String> arg0,
//					String arg1, String arg2) {
//				System.out.println(arg2);
//			}			
//		});
		
		VBox box = new VBox();
		box.getChildren().addAll(chk, cb, txtField, HBoxBuilder.create().children(btn, btnPrint).build());
		Scene sc = new Scene(box);
		stage.setScene(sc);
//		stage.setWidth(400);
//		stage.setHeight(400);
		
		stage.show();
	}
	
	public void stop() throws Exception {
		GUsers.clearActiveUser();
//		GConnection.uninitialise();
		
		super.stop();
	}
}
