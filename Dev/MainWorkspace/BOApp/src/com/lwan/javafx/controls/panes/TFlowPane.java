package com.lwan.javafx.controls.panes;

import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;

public class TFlowPane extends FlowPane {
	private static final Orientation DEFAULT_ORIENTATION = Orientation.HORIZONTAL;
	private static final double DEFAULT_HGAP = 5;
	private static final double DEFAULT_VGAP = 2;
	
	public TFlowPane() {
		this(DEFAULT_ORIENTATION, DEFAULT_HGAP, DEFAULT_VGAP);
	}

	public TFlowPane(Orientation orientation) {
		this(orientation, DEFAULT_HGAP, DEFAULT_VGAP);
	}

	public TFlowPane(double hgap, double vgap) {
		this(DEFAULT_ORIENTATION, hgap, vgap);
	}

	public TFlowPane(Orientation orientation, double hgap, double vgap) {
		super(orientation, hgap, vgap);
	}

}
