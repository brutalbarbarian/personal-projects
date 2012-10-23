package com.lwan.musicsync.grid;

import com.lwan.musicsync.audioinfo.AudioInfo;
import com.lwan.musicsync.audioinfo.AudioInfoRatingProperty;

import javafx.beans.property.Property;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class RatingEditingCell extends BaseEditingCell<Integer> implements  Callback<Object, Boolean> {
	private static Callback<TableColumn<AudioInfo, Integer>, TableCell<AudioInfo, Integer>> factory;
	
	public static Callback<TableColumn<AudioInfo, Integer>, TableCell<AudioInfo, Integer>> 
			getRatingEditingCellFactory(final boolean allowContextMenu) {
		if (factory == null) {
			factory = new Callback<TableColumn<AudioInfo, Integer>, TableCell<AudioInfo, Integer>>() {
				public TableCell<AudioInfo, Integer> call(TableColumn<AudioInfo, Integer> p) {
					return new RatingEditingCell(allowContextMenu);
				}
			};
        }
		return factory;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private RatingEditingCell (boolean allowContextMenu) {
		super(allowContextMenu);
		
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		
		setGraphic(new RatingsEdit((Property)itemProperty(), this,
				new Callback<Object, AudioInfoRatingProperty>() {
					public AudioInfoRatingProperty call(Object arg0) {
						return getAudioInfo().ratingProperty();
					}
		}));
	}
	
	protected void updateItem(Integer item, boolean empty) {
		itemProperty().set(item);
	}
	
	public void startEdit() {
		// do nothing.
	}

	@Override
	public boolean allowsCellEdit() {
		return false;
	}

	@Override
	public Boolean call(Object arg0) {
		// Don't want clicking to do anything if
		// other things are being selected.
		// This is to mainly stop anything from happening if user is
		// attempting to select multiple cells (e.g. holding shift).
		int selected = getTableView().getSelectionModel().getSelectedItems().size();
		return selected == 0 || (selected == 1 && isSelected());
	}
}

