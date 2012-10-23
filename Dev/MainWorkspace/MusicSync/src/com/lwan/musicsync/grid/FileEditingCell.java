package com.lwan.musicsync.grid;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import com.lwan.musicsync.audioinfo.AudioFileInfo;
import com.lwan.musicsync.audioinfo.AudioInfo;
import com.lwan.musicsync.main.Constants;
import com.lwan.util.CollectionUtil;
import com.sun.javafx.collections.ObservableListWrapper;

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
	
	class fileSplittingWindow extends Stage implements EventHandler<ActionEvent>{
		Button btnSplit, btnClose, btnPrime;
		TextField txtPrime;
		ListView<AudioFileInfo> list;
		ToolBar topBar;
		
		fileSplittingWindow() {
			// create controls
			AudioInfo info = getAudioInfo();
			list = new ListView<>(new ObservableListWrapper<AudioFileInfo>(
					CollectionUtil.toList(info.allTags.keySet())));
			list.getSelectionModel().select(info.primaryFile);
			
			btnSplit = ButtonBuilder.create().text("Split").onAction(this).build();
			btnClose = ButtonBuilder.create().text("Close").onAction(this).build();
			btnPrime = ButtonBuilder.create().text("Set Primary").onAction(this).build();

			txtPrime = new TextField();
			txtPrime.setEditable(false);
			
			topBar = ToolBarBuilder.create().items(new Label(""), txtPrime).build();
			
			ToolBar tb = ToolBarBuilder.create().items(btnClose, btnPrime, btnSplit).build();
			BorderPane pane = BorderPaneBuilder.create().top(topBar).center(list).bottom(tb).build();
			Scene scene = new Scene(pane);
			
			setScene(scene);
			
			setOnShown(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent arg0) {
					displayState();	
				}
			});
		}
		
		void displayState() {
			boolean onlyOne = list.getItems().size() == 1;
			btnPrime.setDisable(!onlyOne);
			btnSplit.setDisable(!onlyOne);
			topBar.setVisible(!onlyOne);
			
			txtPrime.setText(getAudioInfo().primaryFile.toString());
		}

		@Override
		public void handle(ActionEvent e) {
			Object src = e.getSource();
			if (src == btnPrime) {
			
			} else if (src == btnSplit) {
				
			} else if (src == btnClose) {
				
			}
		}
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
//		comboBox.show();
//		comboBox.set
//			setFocused(true);
	}
	
	@Override
	public void cancelEdit() {
		if (actualCancel) {
			super.cancelEdit();
			setText(getString());
			setContentDisplay(ContentDisplay.TEXT_ONLY);
		} else {
			commitEdit(comboBox.getSelectionModel().getSelectedItem().toString());
		}
	}
	
	@Override
	protected void HandleEdit(ActionEvent e) {
		// show a dialog for splitting tracks
		
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
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
				
				// find out how may files there are... change color depending on number of files
				AudioInfo info = getAudioInfo(); 
				int files = info.allTags.keySet().size();
				if (files == 1) {
					setStyle("-fx-background-color:GREEN");
				} else {
					// green means one will be created in one of the roots
					// yellow means there are 2.. one for each root
					// orange means there are more then 2 for at least one of the roots
					int rt1Count = 0, rt2Count = 0;
					String rt1 = null, rt2 = null;
					for (AudioFileInfo file : info.allTags.keySet()) {
						if (rt1 == null) {
							rt1 = file.rootDir;
							rt1Count++;
						} else if (file.rootDir.equals(rt1)) {
							rt1Count++;
						} else if (rt2 == null){
							rt2 = file.rootDir;
							rt2Count++;
						} else if (file.rootDir.equals(rt2)) {
							rt2Count++;
						}
					}
					
					if (rt1Count == 1 && rt2Count == 1) {
						setStyle("-fx-background-color:YELLOW");	
					} else {
						setStyle("-fx-background-color:ORANGE");	
					}
				}
			}
		}
	}
	
	public void commitEdit(String text) {
		actualCancel = true;
		super.commitEdit(text);
		cancelEdit();
		actualCancel = false;
	}
	
	protected void createTextField() {
		comboBox = new ComboBox<>();
		comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
		
		comboBox.selectionModelProperty().get().selectedItemProperty().addListener(new ChangeListener<AudioFileInfo>() {
			public void changed(ObservableValue<? extends AudioFileInfo> arg0,
					AudioFileInfo oldValue, AudioFileInfo newValue) {
				if (newValue != null && !newValue.toString().equals(getString())) {
					commitEdit(newValue.toString());
				}
			}
			
		});
	}

	@Override
	public boolean allowsCellEdit() {
		return true;
	}
	
	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}

}
