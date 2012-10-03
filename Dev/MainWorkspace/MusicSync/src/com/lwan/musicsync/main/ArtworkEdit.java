package com.lwan.musicsync.main;

import com.lwan.javafx.scene.control.ImageDialog;
import com.lwan.util.ImageUtil;
import com.lwan.util.wrappers.ResultCallback;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.util.Callback;

public class ArtworkEdit extends Group implements EventHandler<MouseEvent>,
		ChangeListener<Image> {
	Callback<Object, Boolean> allowEdit;
	Callback<Object, AudioInfoArtworkProperty> artworkProperty;
	CheckBox simpleGraphic;
	ImageView imgView;
	Rectangle imgShadow, nullValue;
	Label nullText;
	Property<Image> imgProperty;
	boolean isAlwaysArtwork;
	protected Popup popup;
	
	public ArtworkEdit(Property<Image> valueProperty, Callback<Object, Boolean> allowEdit, 
			Callback<Object, AudioInfoArtworkProperty> callback, boolean alwaysUseArtwork) {
		imgProperty = new SimpleObjectProperty<Image>();	// this is read only..
		artworkProperty = callback;
		this.allowEdit = allowEdit; 
		isAlwaysArtwork = alwaysUseArtwork;
		
		if (isSimpleMode()) {
			simpleGraphic = new CheckBox("Has Artwork");
			simpleGraphic.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					// Throw away the change first
					simpleGraphic.selectedProperty().set(imgProperty.getValue() != null);
					showBasicEditScreen();	// just show	
				}
			});
			getChildren().add(simpleGraphic);
			
			simpleGraphic.setOnMouseMoved(this);
			setOnMouseExited(this);
		} else {
			int size = Constants.gridImageSizeProperty().get();
			imgView = new ImageView();
			imgShadow = new Rectangle(size, size);	
			imgShadow.setEffect(new Shadow(2, Color.BLACK));
			nullValue = new Rectangle(size, size, Color.WHITE);
			nullText = new Label("No Image");
			nullText.alignmentProperty().set(Pos.CENTER);
			nullText.relocate(15, 20);
			getChildren().add(imgShadow);
			getChildren().add(imgView);
			getChildren().add(nullValue);
			getChildren().add(nullText);
			
			setOnMouseClicked(this);
		}
		
		imgProperty.addListener(this);
		imgProperty.bindBidirectional(valueProperty);
	}

	@Override
	public void handle(MouseEvent e) {
		System.out.println(e.getEventType().toString() + "::Type");
		if (e.getEventType() == MouseEvent.MOUSE_EXITED) {
			System.out.println("exited");
			if (popup != null) {
				popup.hide();
				popup = null;
			}
		} else if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
			if (e.getButton() == MouseButton.PRIMARY && !isSimpleMode() &&
					allowEdit.call(e)) {
				showBasicEditScreen();
			}
		} else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
			System.out.println("moved");
			if (popup == null && imgProperty.getValue() != null && isSimpleMode()) {
				System.out.println("isSimpleMode");
				ImageView view = new ImageView(imgProperty.getValue());
				
				popup = new Popup();
				popup.getContent().setAll(view);
				popup.show(getScene().getWindow());
			}
			if (popup != null) {
				System.out.println("moving popup");
				// offset so dosen't interfare with mouse movements
				popup.setX(e.getScreenX() + 5);
				popup.setY(e.getScreenY() + 5);
			}
		}
	}
	
	@Override
	public void changed(ObservableValue<? extends Image> arg0, Image oldValue,
			Image newValue) {
		if (!isSimpleMode()) {
			nullValue.setVisible(newValue == null);
			nullText.setVisible(newValue == null);
		}
		
		if (newValue != null) {
			if (isSimpleMode()) {
				simpleGraphic.selectedProperty().set(true);
			} else {
				imgView.setImage(newValue);
				
				imgShadow.setVisible(true);
				nullValue.setVisible(false);
			}
		} else {
			setNullValue();
		}
	}
	
	protected boolean isSimpleMode() {
		return !isAlwaysArtwork && !Constants.gridCoverArtModeProperty().getValue();
	}
	
	protected void setNullValue() {
		// if already null... ensure everything else is correctly displayed
		if (imgProperty.getValue() == null) {
			if (isSimpleMode()) {
				simpleGraphic.selectedProperty().set(false);
			} else {
				imgView.setImage(null);
			}
		} else { 
			// if not already null... set item to null which will throw a changed event
			// the changed event will call this again with property already set to null
			artworkProperty.call(this).setAsBufferedImage(null);
		}
	}
	
	public void showBasicEditScreen() {
		ImageDialog id = new ImageDialog(
				artworkProperty.call(this).getArtworkAsFullSizedImage());
		id.show(getScene().getWindow(), new ResultCallback<ImageDialog>() {
			public void call(ImageDialog result) {
				if (result.resultProperty().get() == ImageDialog.ResultOK) {
					Image img = result.imageProperty().getValue();
					artworkProperty.call(this).setAsBufferedImage(
							ImageUtil.imageFXToAWT(img));
				}
				if (isSimpleMode()) {
					// ensure the checkbox is still correct
					simpleGraphic.selectedProperty().set(imgProperty.getValue() != null);
				}
			}
		});
	}

	
	
}
