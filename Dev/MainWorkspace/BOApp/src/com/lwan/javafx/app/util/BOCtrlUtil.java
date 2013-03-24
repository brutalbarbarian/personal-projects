package com.lwan.javafx.app.util;

import com.lwan.javafx.controls.bo.binding.BoundControl;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ToolBar;

public class BOCtrlUtil {
	public static void buildAttributeLinks(Node n) {
		if (n instanceof BoundControl<?>) {
			((BoundControl<?>)n).dataBindingProperty().buildAttributeLinks();
		}
		if (n instanceof Parent) {
			for (Node m : ((Parent)n).getChildrenUnmodifiable()) {
				buildAttributeLinks(m);
			}
		}
		if (n instanceof ToolBar) {
			for (Node m : ((ToolBar)n).getItems()) {
				buildAttributeLinks(m);
			}
		}
	}
}
