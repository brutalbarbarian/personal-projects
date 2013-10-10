package com.lwan.javafx.controls.pagecontrol;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Toggle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

// A record class connecting the visual components for a controller
// to the page data
public class Page{
	@SuppressWarnings("rawtypes")
	PageData pageData;
	PageController controller;
	Pane childrenPane;
	Toggle toggleControl;

	public void createChildPane(Orientation orientation) {
		if (orientation == Orientation.VERTICAL) {
			childrenPane = new VBox();
		} else {
			childrenPane = new HBox();
		}
	}
	
	private static final int DEFAULT_DURATION = 200;

	Rectangle visibleRect;
	protected void ensureChildrenShowing() {
		if (childrenPane == null || childrenPane.isVisible()) {
			return;
		}
		
		double cacheHeight = childrenPane.prefHeight(Region.USE_COMPUTED_SIZE);
		
		visibleRect = new Rectangle(0, cacheHeight, childrenPane.getWidth(), cacheHeight);
		childrenPane.setClip(visibleRect);
		childrenPane.setMinHeight(0);
		childrenPane.setMaxHeight(0);
		childrenPane.setTranslateY(-cacheHeight);
		
		childrenPane.setVisible(true);
		childrenPane.setManaged(true);
		childrenPane.setCache(true);
		
//		new KeyValue(PropertyToChange, PropertyTarget) 
		
		Timeline tl = new Timeline();
		KeyValue kv1 = new KeyValue(childrenPane.translateYProperty(), 0, Interpolator.EASE_BOTH);
		KeyValue kv2 = new KeyValue(visibleRect.yProperty(), 0, Interpolator.EASE_BOTH);
		KeyValue kv3 = new KeyValue(childrenPane.minHeightProperty(), cacheHeight, Interpolator.EASE_BOTH);
		KeyValue kv4 = new KeyValue(childrenPane.maxHeightProperty(), cacheHeight, Interpolator.EASE_BOTH);	
		
		
		KeyFrame kf = new KeyFrame(new Duration(DEFAULT_DURATION), new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				childrenPane.setClip(null);
				childrenPane.minHeight(Region.USE_PREF_SIZE);
				childrenPane.maxHeight(Region.USE_PREF_SIZE);
				childrenPane.setCache(false);
				
				visibleRect = null;
			}
		}, kv1, kv2, kv3, kv4);
		tl.getKeyFrames().add(kf);
		tl.setCycleCount(1);
		tl.setAutoReverse(true);
		
		tl.play();		
	}

	protected void ensureChildrenHiding() {
		if (childrenPane == null || !childrenPane.isVisible()) {
			return;
		}
		
		double cacheHeight = childrenPane.getHeight();
		
		visibleRect = new Rectangle(0, 0, childrenPane.getWidth(), cacheHeight);
		childrenPane.setClip(visibleRect);
		childrenPane.setMinHeight(cacheHeight);
		childrenPane.setMaxHeight(cacheHeight);
		childrenPane.setTranslateY(0);
		childrenPane.setCache(true);
		
		Timeline tl = new Timeline();
		KeyValue kv1 = new KeyValue(childrenPane.translateYProperty(), -cacheHeight, Interpolator.EASE_BOTH);
		KeyValue kv2 = new KeyValue(visibleRect.yProperty(), cacheHeight, Interpolator.EASE_BOTH);
		KeyValue kv3 = new KeyValue(childrenPane.minHeightProperty(), 0, Interpolator.EASE_BOTH);
		KeyValue kv4 = new KeyValue(childrenPane.maxHeightProperty(), 0, Interpolator.EASE_BOTH);
		
		KeyFrame kf = new KeyFrame(new Duration(DEFAULT_DURATION), new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				childrenPane.setVisible(false);
				childrenPane.setManaged(false);
				childrenPane.setCache(false);
				
				childrenPane.setClip(null);
				childrenPane.minHeight(Region.USE_PREF_SIZE);
				childrenPane.maxHeight(Region.USE_PREF_SIZE);
				
				visibleRect = null;
			}			
		}, kv1, kv2, kv3, kv4);
		tl.getKeyFrames().add(kf);
		tl.setCycleCount(1);
		tl.setAutoReverse(true);
		
		tl.play();
	}

	boolean processed;
}
