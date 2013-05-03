package com.lwan.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * 
 * 
 * @author Brutalbarbarian
 *
 */
public class JavaFXUtil {
	
	public static void printNodeTree(Node n) {
		StringBuilder sb = new StringBuilder();
		printNodeTree(sb, 0, n);
		System.out.println(sb.toString());
	}

	public static void setNodeTreeFocusable(Node n, boolean focusable) {
		n.setFocusTraversable(focusable);
		if (n instanceof Parent) {
			Parent p = (Parent)n;
			for (Node nn : p.getChildrenUnmodifiable()) {
				setNodeTreeFocusable(nn, focusable);
			}
		}
	}
	
	public static boolean isChildOf(Node child, Node parent) {
		if (child == null || parent == null) {
			return false;
		} else if (child == parent) {
			return true;
		} else {
			Node p = child.getParent();
			return p == null? false : isChildOf(p, parent);
		}
	}
	
	protected static void printNodeTree(StringBuilder sb, int level, Node n) {
		if (level > 0) {
			sb.append('\n');
		}
		sb.append(StringUtil.getRepeatedString("  ", level)).append(n == null? "null" : n.toString());
		if (n != null && n instanceof Parent) {
			Parent p = (Parent)n;
			for (Node nn : p.getChildrenUnmodifiable()) {
				printNodeTree(sb, level + 1, nn);
			}
		}
	}
	
	/**
	 * Get the absolute position of the top left corner of the passed in control
	 * 
	 * @param control
	 * @return
	 */
	public static Point2D screenPositionOf(Control control) {
		Point2D center = control.localToScene(0, 0);
		Scene s = control.getScene();
		Window w = s.getWindow();
		
		return new Point2D(center.getX() + w.getX(), center.getY() + w.getY());
	} 
	
	public static void ShowErrorDialog(Window parent, String errorMessage) {
		final Stage err = new Stage();
		err.initModality(Modality.WINDOW_MODAL);
		err.initOwner(parent);
		err.setResizable(false);
		
		VBoxBuilder<?> rt = VBoxBuilder.create();
		Label lbl = new Label(errorMessage);
		Button close = new Button("Ok");
		close.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				err.close();
			}
		});		
		rt.alignment(Pos.CENTER);
		rt.padding(new Insets(16));
		rt.children(lbl, close);
		rt.spacing(10);
		
		err.setScene(new Scene(rt.build()));
		err.getScene().getStylesheets().addAll(parent.getScene().getStylesheets());
		err.show();
	} 
}
