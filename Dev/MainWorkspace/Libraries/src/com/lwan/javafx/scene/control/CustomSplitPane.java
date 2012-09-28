package com.lwan.javafx.scene.control;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class CustomSplitPane extends Pane {
	protected static final double DEFAULT_WIDTH = 100;
	
	protected Node left, right, splitter;
	protected double splitPos, splitWidth;
	
	public CustomSplitPane (Node leftCont, Node rightCont, Node splitter) { 
		super();
		
		// Setup listeners
		MouseListener mouseListener = new MouseListener();
		
		splitPos = 0.5;	// Centered
		splitWidth = DEFAULT_WIDTH;
		
		this.left = leftCont;
		this.splitter = splitter;
		this.right = rightCont;
		
		getChildren().addAll(leftCont, splitter, rightCont);
		
		setOnMousePressed(mouseListener);
		setOnMouseDragged(mouseListener);
		setOnMouseReleased(mouseListener);
		setOnMouseMoved(mouseListener);
	}
	
	/**
	 * Get the width of the splitter
	 * 
	 * @return
	 */
	public double getSplitWidth() {
		return splitWidth;
	}
	
	/**
	 * Set the width of the splitter. 
	 * 
	 * @param width
	 */
	public void setSplitWidth(double width) {
		splitWidth = width;
	}
	
	/**
	 * Set the percentage the centre of the splitter should be relative
	 * to the width of this pane. Note that values too high or low will be
	 * treated such that the splitter is fully visible within the pane.
	 * 
	 * @param pos
	 */
	public void setSplitPos (double pos) {
		splitPos = pos;
//		layoutChildren();
		requestLayout();
	}
	
	/**
	 * Set the absolute position of the centre of the splitter from the left.
	 * Note that values too high or low will be treated such that the splitter
	 * is fully visible within the pane.
	 * 
	 * @param pos
	 */
	public void setSplitPosAbs (double pos) {
		splitPos = pos/getWidth();
//		layoutChildren();
		requestLayout();
	}
	
	/**
	 * Get the percentage the centre of the splitter is relative to the width of
	 * this pane. Note that this may not be the true value as values too
	 * high or low will be treated such that the splitter is fully visible
	 * within the pane.
	 * 
	 * @return
	 */
	public double getSplitPos() {
		return splitPos;
	}
	
	/**
	 * Get the absolute position of the centre of the splitter from the left.
	 * Note that values too high or low will be treated such that the splitter
	 * is fully visible within the pane.
	 * 
	 * @return
	 */
	public double getSplitPosAbs() {
		return splitPos * getWidth();
	}
	
	
	protected class MouseListener implements EventHandler<MouseEvent> {
		MouseListener() {
			key = false;
		}
		
		boolean key;
		double lastX, width;
		@Override
		public void handle(MouseEvent e) {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
					key = splitter.contains(e.getX() - splitter.getLayoutX(), 
							e.getY() - splitter.getLayoutY());
				} else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) { 
					if (key) {
						width = getWidth();
						splitPos = (splitPos * width + (e.getX() - lastX)) / width;
//						layoutChildren();
						requestLayout();
					}
				} else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
					key = false;
				}
				if (key) {
					lastX = e.getX();
				}
			}
			if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
				if (splitter.contains(e.getX() - splitter.getLayoutX(),
						e.getY() - splitter.getLayoutY())) {
					setCursor(Cursor.H_RESIZE);
				} else {
					setCursor(Cursor.DEFAULT);
				}
			}
		}
	}
	
	@Override
	protected void layoutChildren() {
		if (left == null || right == null || splitter == null) {
			return;
		}
		
		double width = getWidth(), height = getHeight();
		if (width == 0 || height == 0) {
			return;
		}
		
		double leftStart, leftEnd, rightStart, rightEnd;
		leftStart = 0;
		leftEnd = width*splitPos - splitWidth/2;
		rightStart = width*splitPos + splitWidth/2;
		rightEnd = width;
		
		// Ensure the splitter dosen't go off the screen
		if (leftEnd < leftStart) {
			leftEnd = 0;
			rightStart = splitWidth;
		} else if (rightStart > rightEnd) {
			rightStart = rightEnd;
			leftEnd = rightStart - splitWidth;
		}
		
		
		layoutInArea(left, leftStart, 0, leftEnd - leftStart, height, 0, HPos.RIGHT, VPos.TOP);
		layoutInArea(splitter, leftEnd, 0, rightStart - leftEnd, height, 0, HPos.CENTER, VPos.TOP);
		layoutInArea(right, rightStart, 0, rightEnd - rightStart, height, 0, HPos.LEFT, VPos.TOP);
	}
}
