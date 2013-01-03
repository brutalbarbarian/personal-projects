import javafx.scene.Scene;
import javafx.stage.Stage;

import com.lwan.eaproj.app.App;
import com.lwan.eaproj.bo.BOEmployee;
import com.lwan.eaproj.bo.BOEmployeePayment;
import com.lwan.eaproj.bo.cache.GUsers;
import com.lwan.javafx.controls.bo.BOGrid;


public class TestJFXGrid extends App{
	
	@Override
	public void start(Stage stage) throws Exception {
		GUsers.setActiveUser("sa", "password");
		stage.setTitle("TestJFXGrid");
		
		// TODO create grid here
		// 18
		BOEmployee emp = new BOEmployee(null);
		emp.employeeID.setValue(18);
		emp.ensureActive();
		
//		System.out.println(emp.toStringAll());
		
		BOGrid<BOEmployeePayment> grid = new BOGrid<>(emp.employeePayments, 
				new String[]{"Notes", "Paid Amount"}, 
				new String[]{"notes", "PaidAmount"});
		grid.setEditable(true);
		
		stage.setScene(new Scene(grid));
		
		
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
