package com.lwan.eaproj.app.scenes;

import com.lwan.eaproj.app.Constants;
import com.lwan.util.StringUtil;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public abstract class AppPage extends BorderPane{	
	public AppPage(final String...params) {
		initialiseLoadingScreen();

		final AppPage self = this;
		Thread th = new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				final Pane main = buildPage();
				initialise(params);
				
				Thread.sleep(2000);
				
				FadeTransition fadeOut = new FadeTransition(Duration.millis(Constants.FADE_DURATION), self);
				fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.play();
                
                fadeOut.onFinishedProperty().set(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent arg0) {
						FadeTransition fadeIn = new FadeTransition(Duration.millis(Constants.FADE_DURATION), self);
						setCenter(main);
						fadeIn.setFromValue(0.0);
						fadeIn.setToValue(1.0);
						fadeIn.play();								
					}		                
                });
				
				return null;
			}			
		});
		
		th.start();
	}
	
	private void initialiseLoadingScreen() {
		// TODO make a prettier loading animation...
		final StackPane grp = new StackPane();
		
		final Circle c = new Circle(50);
		c.setFill(Color.TRANSPARENT);
		c.setStrokeMiterLimit(10);
		c.getStrokeDashArray().addAll(15d, 15d);
		c.setStrokeLineCap(StrokeLineCap.ROUND);
		
		c.setSmooth(true);
		c.setStrokeWidth(4);
		c.setStroke(Color.GRAY);
		
		Thread th = new Thread(new Runnable(){
			int i;
			public void run() {
				while(getCenter() == grp) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {}
					Platform.runLater(new Runnable() {
						public void run() {
							c.setStrokeDashOffset(i);	
						}
					});
					
					i++;
				}
			}			
		});
		
		final Label label = new Label("LOADING");
		
		Thread thr = new Thread(new Runnable() {
			int i = 0;
			
			public void run() {
				while (getCenter() == grp) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {}
					i = (i + 1) % 4;
					Platform.runLater(new Runnable() {
						public void run() {
							label.setText("LOADING" + StringUtil.getRepeatedString('.', i));							
						}						
					});
					
				}
			}
			
		});
		
		grp.getChildren().addAll(label, c);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setAlignment(Pos.CENTER);
		setCenter(grp);
		
		th.start();
		thr.start();
		
	}

	/**
	 * Build the page
	 * 
	 * @return
	 */
	protected abstract Pane buildPage();
	
	/**
	 * Initialise the variables
	 */
	protected abstract void initialise(String... params);
	
	
	public abstract boolean requiresSave();
	public abstract boolean requestSave();
}
