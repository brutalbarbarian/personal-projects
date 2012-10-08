package com.lwan.musicsync.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooserBuilder;
import javafx.stage.Stage;

import com.lwan.musicsync.audioinfo.AudioInfo;
import com.lwan.musicsync.enums.FieldKeyEx;
import com.lwan.musicsync.enums.FileAdvancedInfo;
import com.lwan.musicsync.grid.ArtworkEditingCell;
import com.lwan.musicsync.grid.RatingEditingCell;
import com.lwan.musicsync.grid.StringEditingCell;
import com.lwan.util.CollectionUtil;
import com.lwan.util.EnumUtil;
import com.lwan.util.StringUtil;
import com.sun.javafx.collections.ObservableListWrapper;

public class Start extends Application implements EventHandler<ActionEvent> {
	Map<String, AudioInfo> allMusic;	// title, audio info
	Map<FieldKey, TableColumn<AudioInfo, String>> columns;
	Stage mainWindow;
	
	public static void main(String[] args) {		
		Application.launch(args);
	}
	
	protected void populateMusic(Map<String, AudioInfo> map, String rootDir) throws IOException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		populateMusic(map, rootDir, rootDir);
	}
	
	protected void populateMusic(Map<String, AudioInfo> map, String path, String rootDir) throws IOException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		Path file = Paths.get(path);
		if (Files.isDirectory(file)) {
			for (Path f : Files.newDirectoryStream(file)) {
				populateMusic(map, f.toString(), rootDir);
			}
		} else {
			if (StringUtil.endsWith(path, ".mp3")) {
				AudioInfo ai = new AudioInfo(file.toFile(), rootDir);
				// use title as key...
				String title = (String) ai.tags.get(FieldKey.TITLE);
				if (StringUtil.isNullOrBlank(title)) {
					title = StringUtil.trimFileExtension(file.getName(file.getNameCount() - 1).toString());
				}
				map.put(title, ai);
			}
		}
	}
	
	private ObservableList<AudioInfo> getObservableList() {
		return new ObservableListWrapper<AudioInfo>(CollectionUtil.toList(allMusic.values()));
	}
	
	TextField txtRoot;
	Button btnFindRoot, btnLoadRoot, btnSaveRoot;
	TableView<AudioInfo> table;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		mainWindow = primaryStage;
		
		allMusic = new HashMap<String, AudioInfo>();
		
		// Build table
		table = new TableView<>();
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.getSelectionModel().setCellSelectionEnabled(true);
		table.setEditable(true);
		
		List<TableColumn<AudioInfo, ?>> cols = new Vector<>();
		Enum<?>[] keys = CollectionUtil.removeAll(FieldKeyEx.values(), Constants.getFieldKeyFilter(), false);
		for (Enum<?> fk : keys) {
			if (fk != FieldKey.COVER_ART && fk != FileAdvancedInfo.ROOT_DIR) {
				if (fk == FieldKey.RATING) {
					TableColumn<AudioInfo, Integer> col = new TableColumn<>(EnumUtil.processEnumName(fk));
					col.setCellValueFactory(new PropertyValueFactory<AudioInfo, Integer>(fk.name().toLowerCase()));
					col.setCellFactory(RatingEditingCell.getRatingEditingCellFactory(true));
					cols.add(col);
				} else {
					TableColumn<AudioInfo, String> col = new TableColumn<>(EnumUtil.processEnumName(fk));
					col.setCellValueFactory(new PropertyValueFactory<AudioInfo, String>(fk.name().toLowerCase()));
					col.setCellFactory(StringEditingCell.getStringEditingCellFactory(true));
					cols.add(col);
				}
			}
		}
		TableColumn<AudioInfo, Image> col = new TableColumn<>(EnumUtil.processEnumName(FieldKey.COVER_ART));
		col.setCellValueFactory(new PropertyValueFactory<AudioInfo, Image>(FieldKey.COVER_ART.name().toLowerCase()));
		col.setCellFactory(ArtworkEditingCell.getArtworkEditingCellFactory(true));
		cols.add(col);
		
		table.getColumns().setAll(cols);
		table.setTableMenuButtonVisible(true);
		
		// Build toolbar
		Label lblRoot = new Label("Root:");
		txtRoot = new TextField();
		txtRoot.setOnAction(this);
		txtRoot.prefColumnCountProperty().set(15);
		btnFindRoot = new Button("...");
		btnFindRoot.setOnAction(this);
		btnLoadRoot = new Button("Open");
		btnLoadRoot.setOnAction(this);
		btnSaveRoot = new Button("Commit");
		btnSaveRoot.setOnAction(this);
		
		
		ToolBar tb = ToolBarBuilder.create().items(lblRoot, txtRoot, btnFindRoot, btnLoadRoot, 
				new Separator(), btnSaveRoot).build();
		
		// Build Container
		BorderPane pane = new BorderPane();
		pane.setCenter(table);
		pane.setTop(tb);
		
		Scene s = new Scene(pane);

		primaryStage.setScene(s);
		
		primaryStage.show();
		
		// do after initialised
		
//		txtRoot.setText("D:\\User Files\\Brutalbarbarian\\Music");
		txtRoot.setText("C:\\Users\\Brutalbarbarian\\Music");
	}

	@Override
	public void handle(ActionEvent e) {
		Object src = e.getSource();
		if (src == txtRoot || src == btnLoadRoot) {
			try {
				allMusic.clear();
				populateMusic(allMusic, txtRoot.getText());
				ObservableList<AudioInfo> audioInfos = getObservableList();
				table.setItems(audioInfos);
				
			} catch (IOException | CannotReadException | TagException
					| ReadOnlyFileException | InvalidAudioFrameException e1) {
				e1.printStackTrace();
			}
		} else if (src == btnFindRoot) {
			// Note this will return null if user attempts to choose a windows library
			File file = DirectoryChooserBuilder.create().title("Choose root dir...").
					build().showDialog(mainWindow);
			if (file != null) {
				txtRoot.setText(file.getAbsolutePath());
			}
		} else if (src == btnSaveRoot) {
			for (AudioInfo info : allMusic.values()) {
				try {
					System.out.println("Writing to audio :" + info.nameProperty().get()+ " ...");
					info.saveAudio();
					System.out.println("Successful");
				} catch (CannotWriteException | CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e1) {
					System.out.println("Failed");
				}
			}
		}
	}
}
