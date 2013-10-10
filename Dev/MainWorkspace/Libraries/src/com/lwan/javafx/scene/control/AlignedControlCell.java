package com.lwan.javafx.scene.control;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

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
		setMinHeight(USE_PREF_SIZE);
		
		getChildren().addAll(label, control);
		
		HBox.setHgrow(control, Priority.ALWAYS);
		
		gridColumn = -1;
	}
	
	public AlignedControlCell(String caption, Node ctrl, Parent parent, int column) {
		this(caption, ctrl, parent);
		gridColumn = column;
	}
	
	@Override
	protected double computeMaxWidth(double height) {
		return Double.MAX_VALUE;
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
			
			setMinWidth(USE_PREF_SIZE);
			setTextAlignment(TextAlignment.RIGHT);
			setAlignment(Pos.BASELINE_RIGHT);
		}
		
		public double getComputedPrefWidth() {
			return super.computePrefWidth(USE_COMPUTED_SIZE);
		}
		
		@Override
		protected double computePrefWidth(double height) {
			return getMaxPrefWidth();
		}
	}
}
