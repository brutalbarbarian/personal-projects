package test;

import com.lwan.javafx.scene.control.CustomSplitPane;
import com.lwan.javafx.scene.control.CustomTextAreaSkin;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CustomControlTest extends Application {
	CustomSplitPane p;
	
	public void start(Stage s) {
		// should pass in 3 parameters... left, right, knob
		Node left, right, mid;
		left = new TextArea();
		mid = new Pane();
		right = new TextArea();
		
		CustomTextAreaSkin tasl = new CustomTextAreaSkin((TextArea)left);
		CustomTextAreaSkin tasr = new CustomTextAreaSkin((TextArea)right);
		
		((TextArea)left).setSkin(tasl);
		((TextArea)right).setSkin(tasr);
//		((TextArea)left).setEditable(false);
		
		p = new CustomSplitPane(left, right, mid);
		
		Scene sc = new Scene(p);
		s.setScene(sc);
		
//		JavaFXUtil.printNodeTree(p);
		
		s.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
