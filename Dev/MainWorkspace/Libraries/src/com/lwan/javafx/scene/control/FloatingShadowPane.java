package com.lwan.javafx.scene.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FloatingShadowPane extends Pane {
	private static final double DEFAULT_MARGIN = 5;
	private static final Paint DEFAULT_COLOR = Color.WHITE;
	
	private Node primaryChild;
	private Rectangle background;
	
	private DoubleProperty marginProperty;
	public DoubleProperty marginProperty() {
		if (marginProperty == null) {
			marginProperty = new SimpleDoubleProperty();
		}
		return marginProperty;
	}
	public void setMargin(double value) {
		marginProperty().set(value);
	}
	public double getMargin() {
		return marginProperty().get();
	}

	private BooleanProperty persistentProperty;
	public BooleanProperty persistentProperty() {
		if (persistentProperty == null) {
			persistentProperty = new SimpleBooleanProperty();
		}
		return persistentProperty;
	}
	public boolean isPersistent() {
		return persistentProperty().get();
	}
	public void setPersistent(boolean value) {
		persistentProperty().set(value);
	}
	
	private ObjectProperty<Paint> backgroundFillProperty;
	public ObjectProperty<Paint> backgroundFillProperty() {
		if (backgroundFillProperty == null) {
			backgroundFillProperty = new SimpleObjectProperty<Paint>(background.getFill()) {
				public void set(Paint value) {
					background.setFill(value);
				}
				public Paint get() {
					return background.getFill();
				}
			};
		} 
		return backgroundFillProperty;
	}
	public void setBackgroundFill(Paint value) {
		backgroundFillProperty().set(value);
	}
	public Paint getBackgroundFill() {
		return backgroundFillProperty().get();
	}
	
	public FloatingShadowPane(Node primaryChild, boolean isPersistent) {
		this.primaryChild = primaryChild;
		
		setPersistent(isPersistent);
		setMargin(DEFAULT_MARGIN);
		background = new Rectangle();
		setBackgroundFill(DEFAULT_COLOR);
		background.setEffect(new DropShadow());
		
		getChildren().addAll(background, primaryChild);
	}
	
	public void layoutChildren() {
		double width = primaryChild.prefWidth(-1);
		double height = primaryChild.prefHeight(-1);
		double margin = getMargin();
		
		setWidth(width + margin*2);
		setHeight(height + margin*2);
		
		background.setX(margin);
		background.setY(margin);
		background.setWidth(width);
		background.setHeight(height);
		
		layoutInArea(primaryChild, margin, margin, width, height, 0, HPos.LEFT, VPos.TOP);
		layoutInArea(background, margin, margin, width, height, 0, HPos.LEFT, VPos.TOP);
	}
	
	public static Stage createShadowedStage(Node primaryNode, Boolean isPersistent) {
		final Stage stage = new Stage();
		final FloatingShadowPane pane = new FloatingShadowPane(primaryNode, isPersistent);
		
		stage.initStyle(StageStyle.TRANSPARENT);
		
		Scene sc = new Scene(pane, Color.TRANSPARENT);
		stage.setScene(sc);
		
		stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean oldVal, Boolean newVal) {
				if (!newVal && !pane.isPersistent()) {
					stage.close();
				} 
			}
		});
		
		return stage;
	}
}