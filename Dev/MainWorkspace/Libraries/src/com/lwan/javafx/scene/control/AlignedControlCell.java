package com.lwan.javafx.scene.control;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
	
	int gridColumn;
	
	public AlignedControlCell(String caption, Node ctrl, Parent parent) {
		if (parent == null) {
			parent = this;	// gotta have a limit in case
		}
		this.parent = parent;
		this.caption = caption;
		control = ctrl;
		label = new AlignedLabel(caption);
		setSpacing(5);	// default
		
		getChildren().addAll(label, control);
		
		gridColumn = -1;
	}
	
	public AlignedControlCell(String caption, Node ctrl, Parent parent, int column) {
		this(caption, ctrl, parent);
		gridColumn = column;
	}

	private static final double MIN_CONTROL_WIDTH = 40;
	private static final double MIN_LABEL_WIDTH = 7;
	
	@Override
	protected void layoutChildren() {
		double labelWidth = getMaxPrefWidth();
		double minTotalWidth = getMaxMinWidth();
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
	
	protected double getMaxMinWidth(Node curr, Node ceiling, Node caller, boolean goingUp) {
		double result = Double.MAX_VALUE;
		if (!curr.isVisible()) {
			// do nothing.
		} else if (curr instanceof AlignedControlCell) {
			return ((AlignedControlCell) curr).getWidth(); 
		} else if (curr instanceof Parent) {
			for (Node child : ((Parent)curr).getChildrenUnmodifiable()) {
				if (child == caller) {
					continue;	// this is what called this, don't reiterate down
				}
				if (gridColumn >= 0 && curr instanceof GridPane &&
						GridPane.getColumnIndex(child) != gridColumn) {
					continue;	// we only accept the correct column
				}
				double min = getMaxMinWidth(child, ceiling, curr, false);
				if (min < result) {
					result = min;
				}
			}
		}
		if (goingUp && ceiling != null && curr != ceiling && curr.getParent() != null) {
			double min = getMaxMinWidth(curr.getParent(), ceiling, curr, true);
			if (min > result) {
				result = min;
			}
		}
		return result;
	}
	
	protected double getMaxMinWidth() {
		return getMaxMinWidth(this, parent, null, true);
	}
	
	protected double getMaxPrefWidth() {
		return getMaxPrefWidth(this, parent, null, true);
	}
	
	// idea behind both getMaxPrefWidth and getMaxMinWidth
	// the iteration is a reverse post-order traversal
	// that is, start from a leaf, and go up until you hit the ceiling (or root if ceiling isn't found),
	// and do a standard recursion ignoring the child the call came from at each step.
	// this way, even if the tree isn't fully built, i.e. the parent isn't part of the tree yet,
	// we still get a valid result.
	
	protected double getMaxPrefWidth(Node curr, Node ceiling, Node caller, boolean goingUp) {
		double result = -1;
		if (!curr.isVisible()) {
			// do nothing.
		} else if (curr instanceof AlignedControlCell) {
			AlignedControlCell alignedCell = (AlignedControlCell)curr;
			result = alignedCell.label.getComputedPrefWidth(); 
		} else if (curr instanceof Parent) {
			for (Node child : ((Parent)curr).getChildrenUnmodifiable()) {
				if (child == caller) {
					continue;	// this is what called this, don't reiterate down
				}
				if (gridColumn >= 0 && curr instanceof GridPane &&
						GridPane.getColumnIndex(child) != gridColumn) {
					continue;	// we only accept the correct column
				}
				double max = getMaxPrefWidth(child, ceiling, curr, false);
				if (max > result) {
					result = max;
				}
			}
		}
		if (goingUp && ceiling != null && curr != ceiling && curr.getParent() != null) {
			double max = getMaxPrefWidth(curr.getParent(), ceiling, curr, true);
			if (max > result) {
				result = max;
			}
		}
		return result;
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
