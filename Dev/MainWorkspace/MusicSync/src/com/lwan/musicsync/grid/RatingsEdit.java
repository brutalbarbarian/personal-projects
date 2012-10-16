package com.lwan.musicsync.grid;

import java.awt.Point;

import com.lwan.musicsync.audioinfo.AudioInfoRatingProperty;
import com.lwan.util.media.JAudioTaggerUtil;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.util.Callback;

public class RatingsEdit extends GridPane implements EventHandler<MouseEvent>{
	static final Color activeDefFill = Color.GOLD;
	static final Color inactiveDefFill = Color.AZURE;
	static final Color activeDefBorder = Color.BROWN;
	static final Color inactiveDefBorder = Color.MIDNIGHTBLUE;
	
	static final int cutoffPoint = 10;
	
	protected Star[] stars;
	protected Property<Number> ratingsProperty;
	protected boolean isMouseOver;
	protected int mouseOverRatings;
	
	protected Callback<Object, Boolean> editCheck;
	protected Callback<Object, AudioInfoRatingProperty> callback;
	
	public RatingsEdit(Property<Number> valueProperty, Callback<Object, Boolean> edit,
			Callback<Object, AudioInfoRatingProperty> callback){
		editCheck = edit;
		this.callback = callback;
		
		stars = new Star[5];
		for (int i = 0; i < 5; i++) {
			stars[i] = new Star();
			add(stars[i], i, 0);
		}
		
		setOnMouseExited(this);
		setOnMouseClicked(this);
		setOnMouseMoved(this);
		
		ratingsProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> arg0,
					Number oldValue, Number newValue) {
				doDisplayState();
			}
		});
		
		ratingsProperty().bindBidirectional(valueProperty);
	}
	
	protected void doDisplayState() {
		for (int i = 0; i < 5; i++) {
			Star s = stars[i];
			if(isMouseOver) {
				s.setMouseOver(i < Math.max(getActualStars(), mouseOverRatings));
			} else {
				s.setDefault(i < getActualStars());
			}
		}
	}
	
	protected int getActualStars () {
		return JAudioTaggerUtil.getRating(ratingsProperty().getValue().intValue());
	}
	
	public Property<Number> ratingsProperty() {
		if (ratingsProperty == null) {
			ratingsProperty = new SimpleIntegerProperty();
		}
		return ratingsProperty;
	}
	
	protected Shape createStar() {
		Polygon p = new Polygon(2, 10, 5, 0, 8, 10, 0, 4, 10, 4);
		DropShadow border = new DropShadow();
		border.setBlurType(BlurType.GAUSSIAN);
		border.setRadius(2);
		p.setEffect(border);
		p.setStrokeWidth(1);
		p.setStroke(Color.TRANSPARENT);
		
		return p;
	}
	
	protected class Star extends Group {
		Shape star;
		DropShadow border;
		
		Star () {
			star = createStar();
			border = (DropShadow)star.getEffect();
			
			getChildren().add(star);
		}
		
		void setMouseOver(boolean active) {
			if (active) {
				star.setFill(activeDefFill);
				border.setColor(activeDefBorder);
			} else {
				star.setFill(inactiveDefFill);
				border.setColor(activeDefBorder);
			}
		}
		
		void setDefault(boolean active) {
			if (active) {
				star.setFill(activeDefFill);
				border.setColor(activeDefBorder);
			} else {
				star.setFill(inactiveDefFill);
				border.setColor(inactiveDefBorder);
			}
		}
	}

	@Override
	public void handle(MouseEvent e) {
		if (e.getEventType() == MouseEvent.MOUSE_CLICKED || e.getEventType() == MouseEvent.MOUSE_MOVED) {
			double minDist = Double.MAX_VALUE;
			int minIndex = -1;
			for(int i = 0; i < 5; i++) {
				double tmpMinDist = Point.distance(e.getX(), e.getY(), stars[i].getLayoutX() + 5, stars[i].getLayoutY() + 5);
				if (tmpMinDist < minDist) {
					minDist = tmpMinDist;
					minIndex = i;
				}
			}
			if (minIndex != -1) {
				if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
					if(minDist < cutoffPoint) {
						isMouseOver = true;
						mouseOverRatings = minIndex + 1;
					} else {
						isMouseOver = false;
					}
				} else if (e.getEventType() == MouseEvent.MOUSE_CLICKED &&
						minDist < cutoffPoint && e.getButton() == MouseButton.PRIMARY) {
					if (editCheck.call(e)) {
						callback.call(this).setValue(JAudioTaggerUtil.RatingStars[minIndex]);
					} else {
						getParent().fireEvent(e);
					}
				} 
						
			}
		} else if (e.getEventType() == MouseEvent.MOUSE_EXITED) {
			isMouseOver = false;
		}
		//refresh view
		doDisplayState();
	}
}
