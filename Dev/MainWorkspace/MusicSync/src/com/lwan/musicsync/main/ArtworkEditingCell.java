package com.lwan.musicsync.main;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
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
				int selectedNum = getTableView().getSelectionModel().getSelectedItems().size();
				return selectedNum == 0 || (selectedNum == 1 && isSelected());
			}
		}, new Callback<Object, AudioInfoArtworkProperty>() {
			public AudioInfoArtworkProperty call(Object arg0) {
				return getAudioInfo().cover_artProperty();
			}
		});

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
