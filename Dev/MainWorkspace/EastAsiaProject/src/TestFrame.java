import java.util.Collection;

import javafx.scene.Scene;
import javafx.stage.Stage;

import com.lwan.bo.BOLinkEx;
import com.lwan.eaproj.app.frames.FrameInvoice;
import com.lwan.eaproj.bo.ref.BOInvoice;
import com.lwan.eaproj.bo.ref.BOUserSet;
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
		BOUserSet.setActiveUser("admin", "");
		
		BOLinkEx<BOInvoice> link = new BOLinkEx<>();
		BOInvoice invoice = new BOInvoice(null);
		invoice.ensureActive();
		
		invoice.workID().setValue(6);

		FrameInvoice frame = new FrameInvoice(link);
		
		link.setLinkedObject(invoice);
		
		stage.setScene(new Scene (frame));
		
		stage.show();
	}

}
