package com.lwan.javafx.app.util;

import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.util.wrappers.ResultCallback;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ToolBar;

public class BOCtrlUtil {
	public static void iterateControls(Node n, ResultCallback<Node> callback) {
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
		iterateControls(n, new ResultCallback<Node>(){
			public void call(Node node) {
				if (node instanceof BoundControl<?>) {
					((BoundControl<?>)node).dataBindingProperty().buildAttributeLinks();
				}				
			}			
		});
	}
}
