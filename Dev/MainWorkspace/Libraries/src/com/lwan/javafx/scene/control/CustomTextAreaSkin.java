package com.lwan.javafx.scene.control;



import com.sun.javafx.scene.control.skin.TextAreaSkin;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ScrollPaneBuilder;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;


public class CustomTextAreaSkin extends TextAreaSkin {
	protected ScrollPane scrollPane;
	
	public CustomTextAreaSkin(TextArea textArea) {
		super(textArea);
		
		ScrollPane sp = (ScrollPane) getChildren().get(0); 
		
		Region r = (Region)sp.contentProperty().get();
//		for (Node n : r.getChildrenUnmodifiable()) {
//			if (!(n instanceof Group)) {
//				
//			}
//		}
		
//		n.setPadding(Insets.EMPTY);
		
//		JavaFXUtil.printNodeTree(n);
		
		sp.contentProperty().set(null);
		sp.setVisible(false);	// can't remove it as its still referenced by existing code
		ScrollPaneBuilder<?> builder = ScrollPaneBuilder.create();
		builder.content(r);
		builder.hbarPolicy(ScrollBarPolicy.NEVER);
		builder.vbarPolicy(ScrollBarPolicy.NEVER);
		
		scrollPane = builder.build();
		
//		getSkinnable().setStyle("-fx-border-color:FFFFFFFF;\n");
		setStyle("-fx-border-color:FFFFFFFF;\n"+
				 "-fx-border-width:10;\n" +
				 "-fx-border-insets:0");
//		setStyle("-fx-border-width:0;\n");
		
//		getSkinnable().setStyle(".CustomTextArea(-fx-border-width:-1)");
		
//		getSkinnable().set);
//		getSkinnable().setStyle("-fx-border-width:-1");
//		setStyle("-fx-border-width:-1");
		
		
//		((Region)n).setPadding(Insets.EMPTY);
		
//		scrollPane.setVvalue(100);
		
		getChildren().add(scrollPane);
	}

	public ScrollPane getScrollPane() {
		return scrollPane;
	}
}
