import java.sql.SQLException;
import java.util.Collection;

import javafx.scene.Scene;
import javafx.stage.Stage;

import com.lwan.bo.BOLinkEx;
import com.lwan.eaproj.app.frames.FrameCustomer;
import com.lwan.eaproj.bo.ref.BOCustomer;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.jdbc.GConnection;
import com.lwan.jdbc.StoredProc;


public class TestFrame extends App{
	@Override
	public void init() throws Exception {
		super.init();
		
		DbUtil.setRootPackage("com.lwan.eaproj.sp");
	}
	
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	protected void initStylesheets(Collection<String> stylesheets) {
		// Do nothing
	}

	@Override
	protected void initialiseStage(Stage stage) {
//		PS_CUS_find sp = new PS_CUS_find();
		StoredProc sp = DbUtil.getDbStoredProc("PS_CUS_find");
		try {
			sp.getParamByName("@allow_inactive").set(false);
			sp.getParamByName("@student").set(null);
			sp.execute(GConnection.getConnection());
			DbUtil.printResultSet(sp.getResult());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
//		BOLinkEx<BOContactDetail> link = new BOLinkEx<>();
//		FrameContactDetails frame = new FrameContactDetails(link);
		
//		BOContactDetail cdt = new BOContactDetail(null, "name");
//		cdt.contactDetailID().setValue(35);
//		cdt.ensureActive();
		
//		link.setLinkedObject(cdt);
		
		BOLinkEx<BOCustomer> link = new BOLinkEx<>();
		FrameCustomer frame = new FrameCustomer(link);
		
		BOCustomer cus = new BOCustomer(null);
		cus.customerID().setValue(2);
		cus.ensureActive();
//		cus.firstName().setValue("Bob");
//		cus.lastName().setValue("Dillen");
//		cus.notes().setValue("Long term mental patiant");
//		cus.contactDetail().address1().setValue("88 Canon St");
//		cus.contactDetail().address2().setValue("Greenlane");
//		cus.contactDetail().city().setValue("Auckland");
//		cus.contactDetail().country().setValue("New Zealand");
//		cus.trySave();
		link.setLinkedObject(cus);
		
		stage.setScene(new Scene (frame));
		
		stage.show();
	}

}
