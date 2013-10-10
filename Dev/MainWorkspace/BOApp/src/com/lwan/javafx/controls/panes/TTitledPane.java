package com.lwan.javafx.controls.panes;

import javafx.scene.Node;
import javafx.scene.control.TitledPane;

public class TTitledPane extends TitledPane {
//	public boolean enforceMinHeight;

	public TTitledPane() {
		// TODO Auto-generated constructor stub
	}

	public TTitledPane(String title, Node content) {
		super(title, content);
		// TODO Auto-generated constructor stub
		
//		enforceMinHeight = false;
		setFocusTraversable(false);
	}
//
//	@Override
//	protected double computeMinHeight(double width) {
//		if (enforceMinHeight) {
//			return computePrefHeight(width);
//		} else {
//			// TODO Auto-generated method stub
//			return super.computeMinHeight(width);
//		}
//	}
}
