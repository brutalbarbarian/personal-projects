package com.lwan.javafx.scene.control;

import java.io.File;

import com.lwan.util.GenericsUtil;
import com.lwan.util.FxUtils;
import com.lwan.util.wrappers.Procedure;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;


public class ImageDialog extends BorderPane implements EventHandler<ActionEvent>, ChangeListener<Image>{
	/* Result status constants */
	public static final int ResultUnknown = 0;
	public static final int ResultOK = 1;
	public static final int ResultCancel = 2;
	
	private Property<Image> imageProperty;
	private IntegerProperty resultProperty;
	private BooleanProperty shownProperty;
	private BooleanProperty modifiedProperty;
	private Property<Object> tagProperty;
	

	/* public property accessor methods */
	public Property<Image> imageProperty() {
		if (imageProperty == null) {
			imageProperty = new SimpleObjectProperty<Image>();
		}
		return imageProperty;
	}
	
	public Property<Object> tagProperty() {
		if (tagProperty == null) {
			tagProperty = new SimpleObjectProperty<Object>();
		}
		return tagProperty;
	}
	
	public ReadOnlyIntegerProperty resultProperty() {
		return _resultProperty();
	}
	
	public ReadOnlyBooleanProperty modifiedProperty() {
		return _modifiedProperty();
	}
	
	public ReadOnlyBooleanProperty shownProperty() {
		return _shownProperty();
	}
	
	/* protected property accessor methods */
	protected BooleanProperty _modifiedProperty() {
		if (modifiedProperty == null) {
			modifiedProperty = new SimpleBooleanProperty(false);
		}
		return modifiedProperty;
	}
	
	protected IntegerProperty _resultProperty() {
		if (resultProperty == null) {
			// always default to unknown
			resultProperty = new SimpleIntegerProperty(ResultUnknown);
		}
		return resultProperty;
	}
	
	protected BooleanProperty _shownProperty() {
		if (shownProperty == null) {
			shownProperty = new SimpleBooleanProperty(false);
		}
		return shownProperty;
	}
	
	
	protected Button btnCancel, btnSet, btnLoadFromURL, btnLoadFromClipboard, 
			btnClear, btnLoadFromFile;
	protected ImageView imgView;
	protected ToolBar toolbar;
	protected Stage stage;	//only not null when showing

	// initImage is the inital image shown 
	public ImageDialog(Image initImage) {
		imageProperty().setValue(initImage);
		imageProperty().addListener(new ChangeListener<Image>() {
			public void changed(ObservableValue<? extends Image> arg0,
					Image arg1, Image arg2) {
				displayDetailState();
			}
		});
		buildControls();
	}
	
	public ImageDialog() {
		this(null);
	}

	protected void buildControls() {
		imgView = new ImageView();
		imgView.imageProperty().bindBidirectional(imageProperty);
		imgView.imageProperty().addListener(this);
		imgView.setSmooth(true);
		imgView.setPreserveRatio(true);
		boundsInLocalProperty().addListener(new ChangeListener<Bounds> () {
			public void changed(ObservableValue<? extends Bounds> arg0,
					Bounds oldValue, Bounds newValue) {
				imgView.setFitWidth(getWidth());
				imgView.setFitHeight(getHeight() - toolbar.getHeight());
			}
		});
		setStyle("-fx-background-color:GRAY");

		btnCancel = new Button("Cancel");
		btnCancel.setOnAction(this);
		btnSet = new Button("Save");
		btnSet.setOnAction(this);
		btnLoadFromURL = new Button("From URL");
		btnLoadFromURL.setOnAction(this);
		btnLoadFromClipboard = new Button("From Clipboard");
		btnLoadFromClipboard.setOnAction(this);
		btnClear = new Button("Clear");
		btnClear.setOnAction(this);
		btnLoadFromFile = new Button("From File");
		btnLoadFromFile.setOnAction(this);
		
		toolbar = ToolBarBuilder.create().items(btnCancel, btnSet, btnClear, new Separator(), 
				btnLoadFromFile, btnLoadFromClipboard, btnLoadFromURL).build();
		setBottom(toolbar);
		
		setCenter(imgView);
	}
	
	public void handle(ActionEvent e) {
		Object src = e.getSource();
		if (src == btnCancel) {
			stage.close();
		} else if (src == btnSet) {
			_resultProperty().set(ResultOK);
			stage.close();
		} else if (src == btnClear) {
			imageProperty.setValue(null);
		} else if (src == btnLoadFromFile) {
			FileChooser chooser = new FileChooser();
			File f = chooser.showOpenDialog(stage);
			
			if (f != null) {
				try {
					Image img = new Image(f.getAbsolutePath());
					if (img != null) {
						imageProperty().setValue(img);
					}
				} catch (Exception ex) {
					FxUtils.ShowErrorDialog(stage, "File is not a valid image");
					ex.printStackTrace();
				}
			}
		} else if (src == btnLoadFromClipboard) {
			if (Clipboard.getSystemClipboard().hasImage()) {
				imageProperty().setValue(Clipboard.getSystemClipboard().getImage());
			} else {
				FxUtils.ShowErrorDialog(stage, "No valid image found in clipboard");
			}
		} else if (src == btnLoadFromURL) {
			SimpleTextInputDialog dialog = new SimpleTextInputDialog("Load Image from URL...", "URL:",
					Orientation.VERTICAL, "Enter url of image", "http://", 20);
			dialog.show(getScene().getWindow(), new Procedure<SimpleTextInputDialog>() {
				public void call(SimpleTextInputDialog result) {
					if (result.resultProperty().get() == SimpleTextInputDialog.ResultOK)  {
						String url = result.textProperty().getValue();
						try {
							Image img = new Image(url);
							if (img != null) {
								imageProperty().setValue(img);
							}
						} catch (Exception ex) {
							FxUtils.ShowErrorDialog(stage, "File is not a valid image");
							ex.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	// TODO
	protected void displayDetailState() {
		// Only do anything if we're either showing or about to
		// show the dialog
		if (stage != null) {
			btnSet.setVisible(modifiedProperty().get());
			btnClear.setVisible(imageProperty().getValue() != null);
			if (modifiedProperty().get()) {
				btnCancel.setText("Cancel");
			} else {
				btnCancel.setText("Close");
			}
			// does this actually do anything??
			toolbar.requestLayout();
		}
	}
	
	/**
	 * Show this image dialog.
	 * When a result is avaliable, a callback will be made, passing this object
	 * as the parameter.
	 * Use ResultProperty(), ModifiedProperty(), tagProperty() and
	 * ImageProperty() to extract the results of the dialog showing.
	 * 
	 * @param owner
	 * @param result
	 */
	public void show(Window owner, final Procedure<ImageDialog> result) {
		// setup stage
		stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(owner);
		stage.initStyle(StageStyle.UTILITY);
		stage.setTitle("Select Image...");
		final ImageDialog resValue = this;
		stage.setOnHidden(new EventHandler<WindowEvent>() {
			public void handle (WindowEvent arg0) {
				onClose();
				result.call(resValue);
			}
		});
		stage.setScene(new Scene(this));
		
		// initialise variables
		_modifiedProperty().set(false);
		_resultProperty().set(ResultUnknown);
		_shownProperty().set(true);
		
		// position and show window
		displayDetailState();
		stage.setWidth(400);
		stage.setHeight(400);
		stage.show();
		stage.centerOnScreen();
	}
	
	protected void onClose() {
		if (_resultProperty().get() == ResultUnknown) {
			_resultProperty().set(ResultCancel);
		}
		_shownProperty().set(false);
		stage = null;
	}
	
	protected void onSave() {
		_resultProperty().set(ResultOK);
	}

	@Override
	public void changed(ObservableValue<? extends Image> arg0, Image oldValue,
			Image newValue) {
		if (shownProperty().get()) {
			if (!GenericsUtil.Equals(oldValue, newValue)) {
				_modifiedProperty().set(true);
				displayDetailState();
			}
		}
	}
}
