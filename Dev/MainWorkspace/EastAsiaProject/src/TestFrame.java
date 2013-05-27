import java.util.Collection;

import javafx.scene.Scene;
import javafx.stage.Stage;

import com.lwan.bo.BOLinkEx;
import com.lwan.eaproj.app.frames.FrameContactDetails;
import com.lwan.eaproj.app.frames.FrameCustomer;
import com.lwan.eaproj.bo.ref.BOContactDetail;
import com.lwan.eaproj.bo.ref.BOCustomer;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.util.DbUtil;


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
//		BOLinkEx<BOContactDetail> link = new BOLinkEx<>();
//		FrameContactDetails frame = new FrameContactDetails(link);
		
//		BOContactDetail cdt = new BOContactDetail(null, "name");
//		cdt.contactDetailID().setValue(35);
//		cdt.ensureActive();
		
//		link.setLinkedObject(cdt);
		
		BOLinkEx<BOCustomer> link = new BOLinkEx<>();
		FrameCustomer frame = new FrameCustomer(link);
		
		BOCustomer cus = new BOCustomer(null);
		cus.customerID().setValue(4);
		cus.ensureActive();
		link.setLinkedObject(cus);
		
		stage.setScene(new Scene (frame));
		
		stage.show();
	}

}
