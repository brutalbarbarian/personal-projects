package com.lwan.musicsync.main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.lwan.util.ImageUtil;
import com.lwan.util.JavaFXUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class ArtworkEditingCell extends BaseEditingCell<Image> {
	private static Callback<TableColumn<AudioInfo, Image>, TableCell<AudioInfo, Image>> factory;
	
	public static Callback<TableColumn<AudioInfo, Image>, TableCell<AudioInfo, Image>> 
			getArtworkEditingCellFactory(final boolean allowContextMenu) {
		if (factory == null) {
			factory = new Callback<TableColumn<AudioInfo, Image>, TableCell<AudioInfo, Image>>() {
				public TableCell<AudioInfo, Image> call(TableColumn<AudioInfo, Image> p) {
					return new ArtworkEditingCell(allowContextMenu);
				}
			};
        }
		return factory;	
	}
	
	protected ArtworkEdit artworkEdit;
	
	protected ArtworkEditingCell(boolean allowContextMenu) {
		super(allowContextMenu);

		artworkEdit = new ArtworkEdit(itemProperty(), 
				new Callback<Object, Boolean>() {
			@Override
			public Boolean call(Object arg0) {
				// always allow edit? TODO
				return true;
			}
		}, new Callback<Object, AudioInfoArtworkProperty>() {
			public AudioInfoArtworkProperty call(Object arg0) {
				return getAudioInfo().cover_artProperty();
			}
		});

		setGraphic(artworkEdit);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}
	
//	
//	Image img;	// temp cache
//	
//	// the following used if isSimpleMode() == true
//	protected CheckBox simpleGraphic;
//	protected boolean checkIsSysChange;
//	protected Popup popup;
//	
//	protected ArtworkEditingCell(boolean allowContextMenu) {
//		super(allowContextMenu);
//		setOnMouseExited(new EventHandler<MouseEvent>() {
//			public void handle(MouseEvent e){
//				if (popup != null) {
//					popup.hide();
//					popup = null;
//				}
//			}
//		});
//		setOnMouseClicked(new EventHandler<MouseEvent>() {
//			public void handle(MouseEvent e) {
//				if (e.getButton() == MouseButton.PRIMARY && !isSimpleMode() && 
//						getTableView().getSelectionModel().getSelectedCells().size() == 1) {
//					showBasicEditScreen();
//				}
//			}
//		});
//		setOnMouseMoved(new EventHandler<MouseEvent>() {
//			public void handle(MouseEvent e) {
//				if (popup == null && img != null && isSimpleMode()) {
//					ImageView view = new ImageView(img);
//
//					popup = new Popup();
//					popup.getContent().setAll(view);						
//					popup.show(getScene().getWindow());
//				}
//				if (popup != null) {
//					// offset so dosen't interfare with mouse movements
//					popup.setX(e.getScreenX() + 5);
//					popup.setY(e.getScreenY() + 5);
//				}
//			}
//		});
//		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//		checkIsSysChange = false;
//		itemProperty().addListener(new ChangeListener<Image>() {
//			public void changed(ObservableValue<? extends Image> arg0,
//					final Image oldValue, final Image newValue) {
//				img = newValue;
//				if (newValue != null) {
//					setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//					if (isSimpleMode()) {
//						createSimpleGraphicIfNull();
//						checkIsSysChange = true;
//						simpleGraphic.setSelected(true);
//						checkIsSysChange = false;
//					} else {
//						ImageView graphic = new ImageView(newValue);
//						setGraphic(graphic);
//					}
//				} else {
//					setNullValue();
//				}
//			}
//		});
//		
//		setNullValue();
//	}
//	
//	protected void setNullValue() {
//		if (isSimpleMode()) {
//			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//			createSimpleGraphicIfNull();
//			checkIsSysChange = true;
//			simpleGraphic.setSelected(false);
//			checkIsSysChange = false;
//		} else {
//			setGraphic(null);
//			setContentDisplay(ContentDisplay.TEXT_ONLY);
//			setText("No Cover-Art");
//		}
//	}
//	
//	protected void createSimpleGraphicIfNull() {
//		if (simpleGraphic == null) {
//			checkIsSysChange = false;
//			simpleGraphic = new CheckBox("Has Cover-Art");
//			setGraphic(simpleGraphic);
//			simpleGraphic.selectedProperty().addListener(new ChangeListener<Boolean>(){
//				public void changed(ObservableValue<? extends Boolean> arg0,
//						Boolean oldValue, Boolean newValue) {
//					if (!checkIsSysChange) {	// don't care if its a sys change
//						if (newValue) {
//							showBasicEditScreen();
//						} else {
//							// clear artwork
//							getAudioInfo().setArtworkAsBufferedImage(null);
//						}
//					}
//				}
//			});
//		}
//	}
//	
//	protected void showBasicEditScreen() {
//		AudioInfo audioInfo = (AudioInfo)getTableRow().getItem();
//		
//		Stage stage = new Stage();
//		stage.initModality(Modality.WINDOW_MODAL);
//		stage.initOwner(getScene().getWindow());
//		stage.initStyle(StageStyle.UTILITY);
//		stage.setTitle("Select Image...");
//		stage.setOnHidden(new EventHandler<WindowEvent>() {
//			public void handle(WindowEvent arg0) {
//				if(isSimpleMode() && img == null) {
//					setNullValue();
//				}
//			}
//		});
//		
//		Scene scene = new Scene(new EditScreen(audioInfo));
//		stage.setScene(scene);
//		
//		stage.setWidth(400);
//		stage.setHeight(400);
//		stage.show();
//		stage.centerOnScreen();
//		
//	}
//	
//	protected class EditScreen extends javafx.scene.layout.BorderPane implements EventHandler<ActionEvent> {
//		BufferedImage img;
//		Image imgFX;
//		ImageView imgView;
//		
//		Button btnCancel, btnSet, btnLoadFromURL, btnLoadFromClipboard, btnClear, btnLoadFromFile;
//		ToolBar toolbar;
//		
//		boolean hasChanged;
//		
//		AudioInfo info;
//		
//		public EditScreen(AudioInfo audioInfo) {
//			super();
//			info = audioInfo;
//			
//			//initialize components
//			imgView = new ImageView();
////			imgView.setStyle("-fx-background-color:#444444");
//			imgView.setPreserveRatio(true);
//			imgView.setSmooth(true);
//			
//			ToolBarBuilder<?> builder = ToolBarBuilder.create();
//			btnCancel = new Button("Cancel");	//
//			btnCancel.setOnAction(this);
//			
//			btnSet = new Button("Set Art");
//			btnSet.setOnAction(this);
//			
//			btnClear = new Button("Clear");
//			btnClear.setOnAction(this);
//			
//			btnLoadFromFile = new Button("From File");
//			btnLoadFromFile.setOnAction(this);
//			
//			btnLoadFromURL = new Button("From URL");
//			btnLoadFromURL.setOnAction(this);
//			
//			btnLoadFromClipboard = new Button("From Clipboard");
//			btnLoadFromClipboard.setOnAction(this);
//			
//			builder.items(btnCancel, btnSet, btnClear, btnLoadFromFile, btnLoadFromURL, btnLoadFromClipboard);
//			toolbar = builder.build();
//			
//			setCenter(imgView);
//			setBottom(toolbar);
//			
//			// listeners
//			imgView.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
//				public void changed(ObservableValue<? extends Bounds> arg0,
//						Bounds oldValue, Bounds newValue) {
//					imgView.setFitWidth(getWidth());
//					imgView.setFitHeight(getHeight() - toolbar.getHeight());
//				}
//			});
//			
//			// set data
//			imgFX = audioInfo.getArtworkAsFullSizedImage();
//			hasChanged = false;
//			
//			displayDetailState();
//		}
//		
//		protected void displayDetailState() {
//			if (imgFX != null) {
//				imgView.setImage(imgFX);
//			} else {
//				imgView.setImage(null);
//			}
//			btnSet.setVisible(hasChanged);
//			btnClear.setVisible(imgFX != null);
//			if (hasChanged) {
//				btnCancel.setText("Cancel");
//			} else {
//				btnCancel.setText("Close");
//			}
//			toolbar.requestLayout();
//		}
//
//		@Override
//		public void handle(ActionEvent e) {
//			Object src = e.getSource();
//			if (src == btnCancel) {
//				if (isSimpleMode()) {
//					setNullValue();
//				}
//				getScene().getWindow().hide();
//			} else if (src == btnClear) {
//				imgFX = null;
//				img = null;
//				hasChanged = true;
//				displayDetailState();
//			} else if (src == btnLoadFromFile) {
//				FileChooser chooser = new FileChooser();
//				File f = chooser.showOpenDialog(getScene().getWindow());
//				if (f != null) {
//					BufferedImage i;
//					try {
//						i = ImageIO.read(f);
//						if (i != null) {
//							img = i;
//							imgFX = new Image(new ByteArrayInputStream(ImageUtil.imageToByteArray(img, "png")));
//							hasChanged = true;
//							displayDetailState();
//						}
//					} catch (IOException ex) {
//						JavaFXUtil.ShowErrorDialog(getScene().getWindow(), "File is not a valid image");
//						ex.printStackTrace();
//					}
//				}
//			} else if (src == btnLoadFromClipboard) {
//				if (Clipboard.getSystemClipboard().hasImage()) {
//					imgFX = Clipboard.getSystemClipboard().getImage();
//					img = ImageUtil.imageFXToAWT(imgFX);
//					
//					displayDetailState();
//				} else {
//					JavaFXUtil.ShowErrorDialog(getScene().getWindow(), "Clipboard contains no valid images");
//				}
//			} else if (src == btnLoadFromURL) {
//				final TextField urlField = new TextField();
//				final Button btnCloseURL = new Button("Cancel");
//				final Button btnOKURL = new Button("OK");
//				
//				final Stage urlStage = new Stage();
//				urlStage.initOwner(getScene().getWindow());
//				urlStage.initModality(Modality.WINDOW_MODAL);
//				urlStage.initStyle(StageStyle.UTILITY);
//				urlStage.centerOnScreen();
//				
//				BorderPane pURL = new BorderPane();
//				ToolBar tbURL = new ToolBar(btnCloseURL, btnOKURL);
//				
//				pURL.setCenter(urlField);
//				urlField.setAlignment(Pos.CENTER);
//				pURL.setBottom(tbURL);
//				
//				urlStage.setScene(new Scene(pURL));
//				
//				EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
//					public void handle(ActionEvent e) {
//						Object src = e.getSource();
//						if (src == urlField || src == btnOKURL) {
//							try {
//								imgFX = new Image(urlField.getText());
//								img = ImageUtil.imageFXToAWT(imgFX);
//								displayDetailState();
//							} catch (Exception ex) {
//								JavaFXUtil.ShowErrorDialog(urlStage, "Cannot load image from the requested URL");
//							}
//						} else if (src == btnCloseURL) {
//							urlStage.close();
//						}
//					}
//				};
//				
//				urlField.setOnAction(handler);
//				btnCloseURL.setOnAction(handler);
//				btnOKURL.setOnAction(handler);
//				
//				urlField.textProperty().addListener(new ChangeListener<String>() {
//					public void changed(ObservableValue<? extends String> arg0,
//							String arg1, String arg2) {
//						btnOKURL.setVisible(!urlField.getText().isEmpty());
//					}
//				});
//				
//				urlStage.show();
//			} else if (src == btnSet) {
//				if (img == null) {
//					setNullValue();
//				} else {
//					info.setArtworkAsBufferedImage(img);
//				}
//				getScene().getWindow().hide();
//			}
//		}
//	}
//	
//	protected boolean isSimpleMode() {
//		return Constants.gridCoverArtModeProperty().getValue();
//	}
//
	@Override
	public void startEdit() {
//		showBasicEditScreen();
	}

	@Override
	public boolean allowsCellEdit() {
		return true;
	}
}
