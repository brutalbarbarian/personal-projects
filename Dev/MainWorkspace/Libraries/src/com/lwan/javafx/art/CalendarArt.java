package com.lwan.javafx.art;

import java.util.List;
import java.util.Vector;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class CalendarArt extends ArtBase{
	public CalendarArt() {
		this(100, 100);
	}
	
	public CalendarArt(double defWidth, double defHeight) {
		super(defWidth, defHeight);
	}
	
	protected Bounds getDrawCanvasBounds() {
		return new BoundingBox(0, 0, 100, 100);
	}

	@Override
	protected List<Shape> doDraw() {
		Vector<Shape> shapes = new Vector<>();
		Rectangle top = new Rectangle(10, 10, 80, 20);		
		top.setFill(new LinearGradient(0, 0, 0, 1, true, 
				CycleMethod.NO_CYCLE, new Stop(0, new Color(0, 0, 1, 1)), 
				new Stop(0.5, new Color(0, 0, 0.5, 1)), new Stop(1, new Color(0, 0, 1, 1))));
		top.setStroke(Color.BLACK);
		
		shapes.add(top);
		
		int count = 4;
		for (int i = 0; i <= count; i++) {
			double x = 10 + 80 * i / count;
			Line line = new Line(x, 30, x, 90);
			line.setStroke(Color.DARKGRAY);
			shapes.add(line);
		}
		count = 3;
		for (int i = 0; i <= count; i++) {
			double y = 30 + 60 * i / count;
			Line line = new Line(10, y, 90, y);
			line.setStroke(Color.DARKGRAY);
			shapes.add(line);
		}
		
		return shapes;
	}
}
