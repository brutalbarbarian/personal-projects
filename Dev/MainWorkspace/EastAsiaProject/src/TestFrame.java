import java.util.Collection;

import javafx.scene.Scene;
import javafx.stage.Stage;

import com.lwan.bo.BOLinkEx;
import com.lwan.eaproj.app.frames.FrameWork;
import com.lwan.eaproj.bo.ref.BOUserSet;
import com.lwan.eaproj.bo.ref.BOWork;
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
		
		BOLinkEx<BOWork> link = new BOLinkEx<>();
		BOWork work = new BOWork(null);
		work.ensureActive();
		

		FrameWork frame = new FrameWork(link);
		
		link.setLinkedObject(work);
		
		stage.setScene(new Scene (frame));
		
		stage.show();
	}

}
