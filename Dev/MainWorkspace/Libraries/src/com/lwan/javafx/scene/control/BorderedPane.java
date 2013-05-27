package com.lwan.javafx.scene.control;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class BorderedPane extends StackPane{
	private Label label;
	private Node child;
	
	public BorderedPane (Node child, String title, HPos titlePos) {
		label = new Label(title);
		label.setAlignment(Pos.valueOf("TOP_" + titlePos.toString()));
		label.getStyleClass().add("bordered-titled-title");
		
		
		
		getChildren().addAll(child, label);
		
		
	}
}
