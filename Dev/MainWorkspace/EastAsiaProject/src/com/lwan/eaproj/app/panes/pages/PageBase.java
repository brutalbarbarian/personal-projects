package com.lwan.eaproj.app.panes.pages;

import com.lwan.eaproj.app.EAConstants;
import com.lwan.javafx.controls.panes.TBorderPane;
import com.lwan.javafx.controls.panes.TStackPane;
import com.lwan.util.wrappers.Disposable;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public abstract class PageBase extends TBorderPane implements Disposable{	
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
		final TStackPane grp = new TStackPane();
		
        ProgressIndicator prog = new ProgressIndicator();
        
        prog.maxWidthProperty().bind(Bindings.divide(grp.widthProperty(), 4));
        prog.maxHeightProperty().bind(Bindings.divide(grp.heightProperty(), 4));

		grp.getChildren().addAll(prog);
        
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
