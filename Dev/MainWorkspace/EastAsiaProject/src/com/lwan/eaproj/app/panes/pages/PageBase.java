package com.lwan.eaproj.app.panes.pages;

import com.lwan.eaproj.app.EAConstants;
import com.lwan.util.wrappers.Disposable;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public abstract class PageBase extends BorderPane implements Disposable{	
	public PageBase(final String...params) {
		initialiseLoadingScreen();

		final PageBase self = this;
		Runnable run = new Runnable(){
			@Override
			public void run() {
				final Pane main = buildPage();
				initialise(params);
				
				FadeTransition fadeOut = new FadeTransition(Duration.millis(EAConstants.FADE_DURATION), self);
				fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.play();
                
                fadeOut.onFinishedProperty().set(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent arg0) {
						FadeTransition fadeIn = new FadeTransition(Duration.millis(EAConstants.FADE_DURATION), self);
						setCenter(main);
						fadeIn.setFromValue(0.0);
						fadeIn.setToValue(1.0);
						fadeIn.play();								
					}		                
                });
				
				
			}			
		};
		Platform.runLater(run);
	}
	
	private void initialiseLoadingScreen() {
		// TODO make a prettier loading animation...
		final StackPane grp = new StackPane();
		
		ProgressBar bar = new ProgressBar();
		bar.setPrefWidth(200);
		
		
		grp.getChildren().addAll(bar);
		setCenter(grp);
	}
	
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
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
