import org.controlsfx.dialog.Dialogs;
import org.controlsfx.dialog.Dialog.Actions;

import com.lwan.javafx.controls.pagecontrol.LoadingPage;
import com.lwan.javafx.controls.pagecontrol.PageController;
import com.lwan.javafx.controls.pagecontrol.PageData;
import com.lwan.javafx.controls.pagecontrol.PageDataBase;
import com.lwan.util.containers.Params;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class PageTest extends Application{

	public PageTest() {
		
	}

	PageData pRose, pRed, pBlood;
	PageData pSky, pJade, pBlue;
	PageData pBleach, pSand, pYellow;
	
	@Override
	public void start(Stage stage) throws Exception {
		System.out.println(Dialogs.create().actions(Actions.YES, Actions.NO, Actions.CANCEL).message("This is a message").title("Message").nativeTitleBar().masthead("masthead is what?").showConfirm());
		
		PageController pc = new PageControl();

		pRed = new PageDataBase("Red", "RED", pc.getRootData()) {			
			@Override
			public PageData preferredChild() {
				return pRose;
			}
		};
		
		pRose = new PageDataBase("Rose", "ROSE", pRed) {
			@Override
			public Node getPageNode(Params params) {
				// TODO Auto-generated method stub
				return new Label("ROSE");
			}

			@Override
			public boolean hasPageNode() {
				// TODO Auto-generated method stub
				return true;
			}
		};
		
		pBlood = new PageDataBase("Blood", "BLOOD", pRed) {
			@Override
			public Node getPageNode(Params params) {
				// TODO Auto-generated method stub
				return new Label("Blood");
			}

			@Override
			public boolean hasPageNode() {
				// TODO Auto-generated method stub
				return true;
			}	
		};
		
		
		pRed.getChildren().add(pRose);
		pRed.getChildren().add(pBlood);
		
		pBlue = new PageDataBase("Blue", "BLUE", pc.getRootData()) {
			public PageData preferredChild() {
				return pJade;
			}
		};
		
		pSky = new PageDataBase("Sky", "SKY", pBlue) {
			@Override
			public Node getPageNode(Params params) {
				return new Label("Sky");
			}
			
			@Override
			public boolean hasPageNode() {
				return true;
			}
		};
		
		pJade = new PageDataBase("Jade", "JADE", pBlue) {
			@Override
			public Node getPageNode(Params params) {
				return new Label("Jade");
			}
			
			@Override
			public boolean hasPageNode() {
				return true;
			}
		};
		
		pBlue.getChildren().add(pSky);
		pBlue.getChildren().add(pJade);
		
		pYellow = new PageDataBase("Yellow", "YELLOW", pc.getRootData()) {
			@Override
			public PageData preferredChild() {
				return pSand;
			}
		};
		
		pSand = new PageDataBase("Sand", "SAND", pYellow) {
			@Override
			public Node getPageNode(Params params) {
				return new Label("Sand");
			}
			
			@Override
			public boolean hasPageNode() {
				return true;
			}
		};
		
		pBleach = new PageDataBase("Bleach", "BLEACH", pYellow) {
			@Override
			public Node getPageNode(Params params) {
				return new Label("Bleach");
			}
			
			@Override
			public boolean hasPageNode() {
				return true;
			}
		};
		
		pYellow.getChildren().add(pSand);
		pYellow.getChildren().add(pBleach);
		
		pc.getRootData().getChildren().add(pRed);
		pc.getRootData().getChildren().add(pBlue);
		pc.getRootData().getChildren().add(pYellow);
		
		pc.initOrientation(Orientation.VERTICAL);
		
		BorderPane pane = new BorderPane();
		pane.setLeft(pc.getControlPane());
		pane.setCenter(pc.getDisplayArea());
		
		Scene sc = new Scene(pane);
		stage.setScene(sc);
		
		stage.show();
	}
	
	class PageControl extends PageController {
		Insets defaultPadding;
		
		PageControl() {
			defaultPadding = new Insets(0, 0, 0, 5);
			setLoadingPage(new LoadPage());
		}

		@Override
		protected Toggle createPageButton(PageData data) {
			return new ToggleButton(data.getDisplayTitle());
		}

		@Override
		protected Insets getPadding(PageData data) {
			return defaultPadding;
		}
		
	}
	
	class LoadPage extends LoadingPage {
		ProgressIndicator prog;
		
		LoadPage() {
			prog = new ProgressIndicator();
			
			prog.maxWidthProperty().bind(Bindings.divide(widthProperty(), 4));
	        prog.maxHeightProperty().bind(Bindings.divide(heightProperty(), 4));
			
			getChildren().add(prog);
		}
		
		@Override
		public void startLoading() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void update(double offset) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stop() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}

}
