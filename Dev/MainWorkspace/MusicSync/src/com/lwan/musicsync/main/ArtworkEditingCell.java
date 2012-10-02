package com.lwan.musicsync.main;

import com.lwan.util.GenericsUtil;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
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
	protected Property<Image> imageCache;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ArtworkEditingCell(boolean allowContextMenu) {
		super(allowContextMenu);

		imageCache = new SimpleObjectProperty<>();
		artworkEdit = new ArtworkEdit(imageCache, 
				new Callback<Object, Boolean>() {
			@Override
			public Boolean call(Object arg0) {
				int selectedNum = getTableView().getSelectionModel().getSelectedItems().size();
				return selectedNum == 0 || (selectedNum == 1 && isSelected());
			}
		}, new Callback<Object, AudioInfoArtworkProperty>() {
			public AudioInfoArtworkProperty call(Object arg0) {
				return getAudioInfo().cover_artProperty();
			}
		});
				
		setText("Image Still Loading...");
		setContentDisplay(ContentDisplay.TEXT_ONLY);
		
		
		
		itemProperty().addListener(new ChangeListener<Image>() {
			public void changed(ObservableValue<? extends Image> arg0,
					Image oldImage, Image newImage) {
				AudioInfo info = getAudioInfo();
				if (info != null) {
					if (GenericsUtil.Equals(newImage, info.cover_artProperty().get())) {
						imageCache.setValue(newImage);
					} else {
						imageCache.setValue(info.cover_artProperty().get());
					}
				}
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			}
		});

//		this.
		
		textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				System.out.println(arg1 + "," + arg2);
			}			
		});
		
//		tableRowProperty().addListener(new ChangeListener<TableRow>() {
//			public void changed(ObservableValue<? extends TableRow> arg0,
//					TableRow oldValue, TableRow newValue) {
//				System.out.println("-----");
//				System.out.println(newValue + "," + oldValue);
//				if (newValue != null) System.out.println("New: " + ((AudioInfo)newValue.getItem()).nameProperty().get());
//				if (oldValue != null) System.out.println("Old: " + ((AudioInfo)oldValue.getItem()).nameProperty().get());
//				if (newValue != null) {
//					imageCache.setValue(getAudioInfo().cover_artProperty().get());
//				} else {
//					
//				}
//			}
//		});
		
		
//		getTableView().onScrollProperty().addListener(new ChangeListener() {
//			
//			@Override
//			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
//				System.out.println("On Scroll");
//				AudioInfo info = getAudioInfo();
//				if (info != null) {
//					imageCache.setValue(info.cover_artProperty().get());			
//				}
//			}
//
//		});
		
//		cacheProperty().set(false);

		setGraphic(artworkEdit);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}
	
	@Override
	public void startEdit() {
		artworkEdit.showBasicEditScreen();
	}

	@Override
	public boolean allowsCellEdit() {
		return true;
	}
}
