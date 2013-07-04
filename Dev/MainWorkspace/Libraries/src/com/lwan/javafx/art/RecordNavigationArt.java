package com.lwan.javafx.art;

import java.util.List;
import java.util.Vector;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class RecordNavigationArt extends ArtBase{
	public static final int NAV_FIRST = 0;
	public static final int NAV_PREV = 1;
	public static final int NAV_NEXT = 2;
	public static final int NAV_LAST = 3;
	
	public RecordNavigationArt(double width, double height, int type) {
		super(width, height, type);
	}
	
	protected int getType() {
		return (int) params[0];
	}
	
	@Override
	protected Bounds getDrawCanvasBounds() {
		return new BoundingBox (0, 0, 10, 10);
	}

	@Override
	protected List<Shape> doDraw() {
		List<Shape> result = new Vector<>();
		
		int type = getType();
		if (type == NAV_FIRST) {
			Line arrowTop = new Line(7, 3, 4, 5);
			Line arrowBottom = new Line(7, 7, 4, 5);
			Line stopper = new Line(2, 3, 2, 7);
			
			result.add(arrowTop);
			result.add(arrowBottom);
			result.add(stopper);			
		} else if (type == NAV_PREV) {
			Line arrowTop = new Line(8, 3, 5, 5);
			Line arrowBottom = new Line(8, 7, 5, 5);
			
			Line arrowTop2 = new Line(3, 3, 0, 5);
			Line arrowBottom2 = new Line(3, 7, 0, 5);
			
			result.add(arrowTop);
			result.add(arrowBottom);
			result.add(arrowTop2);
			result.add(arrowBottom2);
		} else if (type == NAV_NEXT) {
			Line arrowTop = new Line(1, 3, 4, 5);
			Line arrowBottom = new Line(1, 7, 4, 5);
			
			Line arrowTop2 = new Line(6, 3, 9, 5);
			Line arrowBottom2 = new Line(6, 7, 9, 5);
			
			result.add(arrowTop);
			result.add(arrowBottom);
			result.add(arrowTop2);
			result.add(arrowBottom2);
		} else if (type == NAV_LAST) {
			Line arrowTop = new Line(2, 3, 5, 5);
			Line arrowBottom = new Line(2, 7, 5, 5);
			Line stopper = new Line(8, 3, 8, 7);
			
			result.add(arrowTop);
			result.add(arrowBottom);
			result.add(stopper);
		}
		
		return result;
	}
}
