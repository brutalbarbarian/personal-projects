import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.eaproj.bo.BOEmployee;
import com.lwan.eaproj.bo.BOEmployeePayment;
import com.lwan.eaproj.bo.cache.BOUserSet;
import com.lwan.javafx.app.App;
import com.lwan.javafx.controls.bo.BOGrid;
import com.lwan.javafx.controls.bo.BOTextField;


public class TestJFXGrid extends App{
	
	@Override
	public void start(Stage stage) throws Exception {
		BOUserSet.setActiveUser("sa", "password");
		stage.setTitle("TestJFXGrid");
		
		// TODO create grid here
		// 18
		final BOEmployee emp = new BOEmployee(null);
		emp.employeeID().setValue(18);
		emp.ensureActive();
		
//		System.out.println(emp.toStringAll());
		BOLinkEx<BOSet<BOEmployeePayment>> setLink = new BOLinkEx<>();
		
		final BOGrid<BOEmployeePayment> grid = new BOGrid<>(
				setLink,
				new String[]{"Name", "Date Paid", "Notes", "Paid Amount"}, 
				new String[]{".../.../NameFirst", "DatePaid", "notes", "PaidAmount"},
				new boolean[]{false, true, true, true});
		grid.setEditable(true);
		
		setLink.setLinkedObject(emp.employeePayments);
		
		Button add = new Button("Add");
		add.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				emp.employeePayments.createNewChild();
			}			
		});
		Button remove = new Button("Delete");
		remove.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				// delete currently selected
				grid.getSelectionModel().getSelectedItem().setActive(false);
			}			
		});
		Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				emp.employeePayments.setActive(false);
				emp.employeePayments.setActive(true);
			}			
		});
		
		BOLinkEx<BOEmployee> link = new BOLinkEx<BOEmployee>();
		link.setLinkedObject(emp);
		
		BOTextField tf = new BOTextField(link, "NameFirst");
		tf.dataBindingProperty().buildAttributeLinks();
		
		ToolBar tb = ToolBarBuilder.create().items(add, remove, cancel).build();
		
		VBox box = new VBox();
		box.getChildren().addAll(grid, tb, tf);
		
		stage.setScene(new Scene(box));
		
		
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected void initialiseStage(Stage stage) {
		// TODO Auto-generated method stub
		
	}
}
