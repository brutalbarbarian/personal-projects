package com.lwan.javafx.scene.control;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Control row for a labeled control.
 * Will ensure all other AlignedControllCells from
 * the same parent will also line up.
 * 
 * @author Lu
 *
 */
public class AlignedControlCell extends HBox{
	AlignedLabel label;
	Control control;
	Parent parent;
	
	public AlignedControlCell(String caption, Control ctrl, Parent parent) {
		this.parent = parent;
		control = ctrl;
		label = new AlignedLabel(caption);
		
		getChildren().addAll(label, control);
		
		HBox.setHgrow(ctrl, Priority.SOMETIMES);
	}
	
	@Override
	protected void layoutChildren() {
		double requiredWidth = getMaxPrefWidth(parent);
		label.setPrefWidth(requiredWidth);
		label.setMinWidth(requiredWidth);
		
		super.layoutChildren();
	}
	
	@Override
	protected double computeMinHeight(double width) {
		// We don't want this cell compressing.
		return super.computePrefHeight(width);
	}
	
	protected double getMaxPrefWidth(Node n) {
		if (!n.isVisible()) {
			return -1;	// Don't care... its not visible anyway
		} else if (n instanceof AlignedControlCell) {
			AlignedControlCell alignedCell = (AlignedControlCell)n;
			return alignedCell.label.getComputedPrefWidth();
		} else if (n instanceof Parent) {
			double result = -1;
			for (Node child : ((Parent) n).getChildrenUnmodifiable()) {
				double max = getMaxPrefWidth(child);
				if (max > result) {
					result = max;
				}
			}
			return result;
		} else {
			return -1;
		}
	}
	
	class AlignedLabel extends Label {
		AlignedLabel(String caption) {
			super(caption);
		}
		
		public double getComputedPrefWidth() {
			return super.computePrefWidth(-1);
		}
	}
}
