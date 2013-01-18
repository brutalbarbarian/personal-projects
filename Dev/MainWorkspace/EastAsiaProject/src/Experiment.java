import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Experiment extends Application{
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage s) throws Exception {
		final TextArea txtIn = new TextArea();
		final TextArea txtOut = new TextArea();
		txtOut.setEditable(false);
		
		HBox txt = new HBox();
		txt.getChildren().addAll(txtIn, txtOut);
		
		Button btn = new Button("Generate Properties");
		
		ToolBar tb = ToolBarBuilder.create().items(btn).build();
		VBox box = new VBox();
		box.getChildren().addAll(txt, tb);
		
		s.setScene(new Scene(box));
		
		btn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				String in = txtIn.getText();
				String outString = "";
				String createString = "";
				// look for keyword 'BODbAttribute'
				int nxtIndex = 0;
				while(nxtIndex >= 0) {
					nxtIndex = in.indexOf("BODbAttribute", nxtIndex);
					if (nxtIndex >= 0) {
						// find the end of '>'
						int declEnd = in.indexOf('>', nxtIndex);
						String declaration = in.substring(nxtIndex, declEnd + 1);
						int end = in.indexOf(';', declEnd);
						String attrs = in.substring(declEnd + 1, end + 1);
						String curString = ""; 
						boolean started = false;
						boolean inComment = false;
						boolean prevIsSlash = false;
						for (char c : attrs.toCharArray()) {
							if (!inComment && !started && Character.isLetter(c)) {
								// start
								started = true;
								curString += c;
							} else if (inComment) {
								// check  for newline character
								if (c == '\n') {
									inComment = false;
								}
							} else if (started) {
								// check for 
								if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
									curString += c;
								} else {
									// end.
									started = false;
									outString += "public " + declaration + " " + curString + "() {\n" +
											"	return " + curString + ";\n" +
											"}\n";
									createString += "addAsChild(new " + declaration + "(this, \"" + ");";
									curString = "";
								}
							}
							if (!inComment && prevIsSlash && c == '/') {
								inComment = true;
							}
							prevIsSlash = c == '/';
						}
						
						
						nxtIndex = end;
					}
				}
				txtOut.setText(outString);
			}			
		});
		
		s.show();
	}
}
