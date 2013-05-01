package com.lwan.javafx.art;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public abstract class ArtBase extends Pane{
	private static final int START_X = 0;
	private static final int START_Y = 1;
	private static final int END_X = 2;
	private static final int END_Y = 3;
	private static final int X = 4;
	private static final int Y = 5;
	private static final int WIDTH = 6;
	private static final int HEIGHT = 7;
	
	private class ArtShape {
		Shape shape;
		HashMap<Integer, Double> properties;
		
		ArtShape(Shape shp, Bounds bounds) {
			properties = new HashMap<>();
			shape = shp;
			
			if (shp instanceof Line) {
				Line line = (Line)shp;
				put(START_X, (line.getStartX() - bounds.getMinX())/bounds.getWidth());
				put(START_Y, (line.getStartY() - bounds.getMinY())/bounds.getHeight());
				put(END_X, (line.getEndX() - bounds.getMinX())/bounds.getWidth());
				put(END_Y, (line.getEndY() - bounds.getMinY())/bounds.getHeight());
			} else if (shp instanceof Rectangle) {
				Rectangle rect = (Rectangle)shp;
				put(X, (rect.getX() - bounds.getMinX())/bounds.getWidth());
				put(Y, (rect.getY() - bounds.getMinY())/bounds.getHeight());
				put(WIDTH, rect.getWidth() / bounds.getWidth());
				put(HEIGHT, rect.getHeight() / bounds.getHeight());
			} else {
				// Do nothing
			}
		}
		
		double get(int key) {
			return properties.get(key);
		}
		
		void put(int key, double multiplier) {
			properties.put(key, multiplier);
		}
	}
	
	private List<ArtShape> shapes;
	private double defaultAspect;	// width over height
	
	private BooleanProperty retainAspectProperty;
	public BooleanProperty retainAspectProperty() {
		return retainAspectProperty;
	}
	public boolean retainAspect() {
		return retainAspectProperty().get();
	}
	public void setRetainAspect(boolean retainAspect) {
		retainAspectProperty().set(retainAspect);
	}
	
	ArtBase() {
		retainAspectProperty = new SimpleBooleanProperty(this, "RetainAspect", true);
		
		draw();
	}
	
	private void draw() {
		Bounds bounds = getDrawCanvasBounds();
		if (bounds == null || bounds.getWidth() == 0 || bounds.getHeight() == 0) {
			throw new RuntimeException("Bounds must be defined and not be 0");
		}
		
		List<Shape> drawnShapes = doDraw();
		
		defaultAspect = bounds.getWidth() / bounds.getHeight();
		
		shapes = new Vector<>();
		for (Shape shp : drawnShapes) {
			ArtShape shape = new ArtShape(shp, bounds);
			shapes.add(shape);
			getChildren().add(shp);			
		}
	}
	
	protected abstract Bounds getDrawCanvasBounds();
	
	protected abstract List<Shape> doDraw();
	
	public void layoutChildren() {
		double width = getWidth();
		double height = getHeight();
		
		if (width == 0 || height == 0) return;
		
		if (retainAspect()) {
			double aspect = width/height;
			if (aspect > defaultAspect) {
				// aspect's width is higher then default aspect
				// use height as baseline, reduce the width
				width = defaultAspect * height;
			} else if (aspect < defaultAspect) {
				// aspect's width is lower then default's aspect
				// use width as baseline, reduce the height
				height = width / defaultAspect;
			} // else they're already equal
		}
		for (ArtShape shape : shapes) {
			Shape shp = shape.shape;
			if (shp instanceof Line) {
				Line line = (Line)shp;
				line.setStartX(width * shape.get(START_X));
				line.setStartY(height * shape.get(START_Y));
				line.setEndX(width * shape.get(END_X));
				line.setEndY(height * shape.get(END_Y));
			} else if (shp instanceof Rectangle) {
				Rectangle rect = (Rectangle)shp;
				rect.setX(width * shape.get(X));
				rect.setY(height * shape.get(Y));
				rect.setWidth(width * shape.get(WIDTH));
				rect.setHeight(height * shape.get(HEIGHT));
			} else {
				// Do nothing...
			}
		}
	}
}
