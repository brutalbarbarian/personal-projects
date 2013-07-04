package com.lwan.javafx.app.util;

import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.util.wrappers.Procedure;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ToolBar;

public class BOCtrlUtil {
	public static boolean getDisabled(BoundControl<?> ctrl) {
		if (ctrl.isEnabled()) {
			return !ctrl.dataBindingProperty().isEditable();
		} else {
			return true;
		}
	}
	
	public static void iterateControls(Node n, Procedure<Node> callback) {
		callback.call(n);
		
		if (n instanceof Parent) {
			for (Node m : ((Parent)n).getChildrenUnmodifiable()) {
				iterateControls(m, callback);
			}
		}
		if (n instanceof ToolBar) {
			for (Node m : ((ToolBar)n).getItems()) {
				iterateControls(m, callback);
			}
		}
	}
	
	public static void buildAttributeLinks(Node n) {
		if (n != null) {
			iterateControls(n, new Procedure<Node>(){
				public void call(Node node) {
					if (node instanceof BoundControl<?>) {
						((BoundControl<?>)node).dataBindingProperty().buildAttributeLinks();
					}
				}			
			});
		}
	}
}
