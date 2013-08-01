package com.lwan.javafx.controls.panes;

import javafx.scene.layout.GridPane;

public class TGridPane extends GridPane {
	public static final int DEFAULT_HORIZ_SPACING = 5;
	public static final int DEFAULT_VERT_SPACING = 2;

	public TGridPane() {
		// TODO Auto-generated constructor stub
		setHgap(DEFAULT_HORIZ_SPACING);
		setVgap(DEFAULT_VERT_SPACING);
	}

}
