package com.lwan.javafx.scene.control;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

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
	Node control;
	Parent parent;
	
	String caption;
	
	public AlignedControlCell(String caption, Node ctrl, Parent parent) {
		this.parent = parent;
		this.caption = caption;
		control = ctrl;
		label = new AlignedLabel(caption);
		setSpacing(5);	// default
		
		getChildren().addAll(label, control);
	}

	private static final double MIN_CONTROL_WIDTH = 40;
	private static final double MIN_LABEL_WIDTH = 7;
	
	@Override
	protected void layoutChildren() {
		double labelWidth = getMaxPrefWidth(parent);
		double minTotalWidth = getMaxMinWidth(parent);
		Insets padding = getPadding();
		double actualWidth = getWidth();
		double actualHeight = getHeight();
		
		double usedArea;
		
		double ctrlWidth = minTotalWidth - labelWidth -  padding.getLeft() - padding.getRight() - getSpacing();		
		if (ctrlWidth < MIN_CONTROL_WIDTH) {
			labelWidth = minTotalWidth - MIN_CONTROL_WIDTH - padding.getLeft() - padding.getRight() - getSpacing();
		}
		
		if (labelWidth < MIN_LABEL_WIDTH) {
			label.setVisible(false);
			usedArea = 0;
		} else {
			label.setVisible(true);
			usedArea = labelWidth + getSpacing();
			layoutInArea(label, padding.getLeft(), padding.getTop(), labelWidth,
					actualHeight - padding.getBottom() - padding.getTop(), 0, HPos.LEFT, VPos.TOP);
		}
		
		layoutInArea(control, padding.getLeft() + usedArea, padding.getTop(), actualWidth - padding.getRight() -
				padding.getLeft() - usedArea, actualHeight - padding.getBottom() - padding.getTop(), 0, HPos.LEFT, VPos.TOP);
	}
	
	@Override
	protected double computeMinHeight(double width) {
		// We don't want this cell compressing.
		return super.computePrefHeight(width);
	}
	
	protected double getMaxMinWidth(Node n) {
		if (!n.isVisible()) {
			return Double.MAX_VALUE;	// Don't care... its not visible anyway
		} else if (n instanceof AlignedControlCell) {
			return ((AlignedControlCell) n).getWidth();
		} else if (n instanceof Parent) {
			double result = Double.MAX_VALUE;
			for (Node child : ((Parent) n).getChildrenUnmodifiable()) {
				double min = getMaxMinWidth(child);
				if (min < result) {
					result = min;
				}
			}
			return result;
		} else {
			return Double.MAX_VALUE;
		}
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
