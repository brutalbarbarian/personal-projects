package com.lwan.musicsync.grid;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import com.lwan.musicsync.audioinfo.AudioFileInfo;
import com.lwan.musicsync.audioinfo.AudioInfo;
import com.lwan.musicsync.main.Constants;
import com.lwan.util.CollectionUtil;

public class FileEditingCell extends BaseEditingCell<String> {
private static Callback<TableColumn<AudioInfo, String>, TableCell<AudioInfo, String>> factory;
	public static Callback<TableColumn<AudioInfo, String>, TableCell<AudioInfo, String>> 
			getFileEditingCellFactory() {
		if (factory == null) {
			factory = new Callback<TableColumn<AudioInfo, String>, TableCell<AudioInfo, String>>() {
				public TableCell<AudioInfo, String> call(TableColumn<AudioInfo, String> p) {
					return new FileEditingCell();
				}
			};
        }
		return factory;
	}

	protected FileEditingCell() {
		super(false);
		setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e) {
				// Only auto-start edit if its a single cell being selected.
				System.out.println("onMouseClick");
				if (e.getButton().equals(MouseButton.PRIMARY) && isFocused() && 
						getTableView().getSelectionModel().getSelectedCells().size() == 1) {
					startEdit();	
				}
			}
		});
		Tooltip tip = new Tooltip();
		tip.textProperty().bind(textProperty());
		setTooltip(tip);
	}
	
	private ComboBox<AudioFileInfo> comboBox;
	private boolean actualCancel = false;
	
	@Override
	public void startEdit() {
		// ensure this is being edited so commitEdit and updateItem will work properly
//			TableRow<?> row = getTableRow();
		super.startEdit();

		if (comboBox == null) {
			createTextField();
		}
		// re-populate the combobox
		// make sure its set to the latest selection
		comboBox.setItems(CollectionUtil.asObservableList(
				CollectionUtil.toArray(getAudioInfo().allTags.keySet(), AudioFileInfo.class)));
		comboBox.setStyle(Constants.getTextCellStyle());
		comboBox.getSelectionModel().select(getAudioInfo().primaryFile);

		setGraphic(comboBox);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

		comboBox.requestFocus();
//			setFocused(true);
	}
	
	@Override
	public void cancelEdit() {
		System.out.println("canceling: " + actualCancel);
		if (actualCancel) {
			super.cancelEdit();
			setText(getString());
			setContentDisplay(ContentDisplay.TEXT_ONLY);
		} else {
			commitEdit(comboBox.getSelectionModel().getSelectedItem().toString());
		}
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		System.out.println("updating: " + item + ":" + empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (comboBox != null) {
					comboBox.getSelectionModel().select(getAudioInfo().primaryFile);
				}
				setGraphic(comboBox);
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			} else {
				setText(getString());
				setContentDisplay(ContentDisplay.TEXT_ONLY);
			}
		}
	}
	
	public void commitEdit(String text) {
		actualCancel = true;
		System.out.println("Committing");
		super.commitEdit(text);
		setContentDisplay(ContentDisplay.TEXT_ONLY);
//		cancelEdit();
		actualCancel = false;
	}
	
	protected void createTextField() {
		comboBox = new ComboBox<>();
		comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
		
		comboBox.selectionModelProperty().get().selectedItemProperty().addListener(new ChangeListener<AudioFileInfo>() {
			public void changed(ObservableValue<? extends AudioFileInfo> arg0,
					AudioFileInfo oldValue, AudioFileInfo newValue) {
				System.out.println("selection changed");
				if (newValue != null && !newValue.toString().equals(getString())) {
					if (oldValue != null) System.out.println("old: " + oldValue.toString());
					System.out.println("new: " + newValue.toString());
					commitEdit(newValue.toString());
				}
			}
			
		});
	}

	@Override
	public boolean allowsCellEdit() {
		return false;
	}
	
	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}

}
