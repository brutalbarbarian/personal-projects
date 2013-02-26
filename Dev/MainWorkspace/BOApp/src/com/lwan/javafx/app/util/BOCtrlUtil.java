package com.lwan.javafx.app.util;

import com.lwan.javafx.controls.bo.binding.BoundControl;

import javafx.scene.Node;
import javafx.scene.Parent;

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
	}
}
