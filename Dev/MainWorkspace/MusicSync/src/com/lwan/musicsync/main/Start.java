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
	
	String path1, path2;	// the 2 paths that are set upon load
	static Start app;	// this class for global access
	
	public static void main(String[] args) {		
		Application.launch(args);
	}
	
	public static Start getApp() {
		return app;
	}
	
	public String getOtherPath(String root) {
		if (root.equals(path1)) {
			return path2;
		} else if (root.equals(path2)){
			return path1;
		} else {
			// Invalid root
			return "";
		}
	}
	
	protected void populateMusic(Map<String, AudioInfo> map, String rootDir) throws IOException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
		File f = new File(rootDir);
		if (f.exists() && f.isDirectory()) {
			populateMusic(map, rootDir, rootDir);
		}
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
				// determine conflicts using a combination of title, artist and album
				String key =
						StringUtil.getDelimitedString("||", 
								(String) ai.tags().get(FieldKey.TITLE), 
								(String) ai.tags().get(FieldKey.ARTIST),
								(String) ai.tags().get(FieldKey.ALBUM));
				AudioInfo conflictAI = map.get(key);
				if (conflictAI == null) {
					map.put(key, ai);
				} else {
					// Problem with this method...
					// If the user decides 2 files should be separate instead of linked...
					// They lose all info
					// Answer... still store the audio info?... could really bloat memory though.
					
					
					// If title, artist and album are all equal... most likely we're talking about
					// the same file here.
					if (!conflictAI.merge(ai)) {
						// Attach current time at end... not really elegant and means this file
						// is pretty much guaranteed to never conflict
						key += System.currentTimeMillis();
						map.put(key, ai);
					}
				}
			}
		}
	}
	
	private ObservableList<AudioInfo> getObservableList() {
		return new ObservableListWrapper<AudioInfo>(CollectionUtil.toList(allMusic.values()));
	}
	
	TextField txtRoot;
	Button btnFindRoot, btnLoadRoot, btnSaveRoot;
	TextField txtRoot2;
	Button btnFindRoot2;
	TableView<AudioInfo> table;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		app = this;
		mainWindow = primaryStage;
		
		allMusic = new HashMap<String, AudioInfo>();
		
		// Build table
		table = new TableView<>();
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.getSelectionModel().setCellSelectionEnabled(true);
		table.setEditable(true);
		
		List<TableColumn<AudioInfo, ?>> cols = new Vector<>();
		for (Enum<?> fk : Constants.getFilteredProperties()) {
			// We don't want private columns in here
			if (fk == FieldKey.RATING) {
				TableColumn<AudioInfo, Integer> col = new TableColumn<>(EnumUtil.processEnumName(fk));
				col.setCellValueFactory(new PropertyValueFactory<AudioInfo, Integer>(fk.name().toLowerCase()));
				col.setCellFactory(RatingEditingCell.getRatingEditingCellFactory(true));
				cols.add(col);
			} else if (fk == FieldKey.COVER_ART) {
				TableColumn<AudioInfo, Image> col = new TableColumn<>(EnumUtil.processEnumName(FieldKey.COVER_ART));
				col.setCellValueFactory(new PropertyValueFactory<AudioInfo, Image>(FieldKey.COVER_ART.name().toLowerCase()));
				col.setCellFactory(ArtworkEditingCell.getArtworkEditingCellFactory(true));
				cols.add(col);
			} else {
				TableColumn<AudioInfo, String> col = new TableColumn<>(EnumUtil.processEnumName(fk));
				col.setCellValueFactory(new PropertyValueFactory<AudioInfo, String>(fk.name().toLowerCase()));
				col.setCellFactory(StringEditingCell.getStringEditingCellFactory(true));
				cols.add(col);
			}
		}
		
		table.getColumns().setAll(cols);
		table.setTableMenuButtonVisible(true);
		
		// Build ToolBar
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
		
		txtRoot2 = new TextField();
		txtRoot2.setOnAction(this);
		txtRoot2.prefColumnCountProperty().set(15);
		btnFindRoot2 = new Button("...");
		btnFindRoot2.setOnAction(this);
		
		
		ToolBar tb = ToolBarBuilder.create().items(
				lblRoot, txtRoot, btnFindRoot, 
				new Separator(), txtRoot2, btnFindRoot2,
				new Separator(), btnLoadRoot, btnSaveRoot).build();
		
		// Build Container
		BorderPane pane = new BorderPane();
		pane.setCenter(table);
		pane.setTop(tb);
		
		Scene s = new Scene(pane);

		primaryStage.setScene(s);
		
		primaryStage.show();
		
		// do after initialised
		
//		txtRoot.setText("D:\\User Files\\Brutalbarbarian\\Music");
//		txtRoot.setText("C:\\Users\\Brutalbarbarian\\Music");
		txtRoot.setText("C:\\TEST\\root1");
		txtRoot2.setText("C:\\TEST\\root2");
	}

	@Override
	public void handle(ActionEvent e) {
		Object src = e.getSource();
		if (src == txtRoot || src == btnLoadRoot || src == txtRoot2) {
			try {
				path1 = txtRoot.getText();
				path2 = txtRoot2.getText();
				
				allMusic.clear();
				populateMusic(allMusic, txtRoot.getText());
				populateMusic(allMusic, txtRoot2.getText());
				ObservableList<AudioInfo> audioInfos = getObservableList();
				for (AudioInfo ai : audioInfos) {
					ai.setupProperties();
					ai.resetModified();
				}
				table.setItems(audioInfos);
				
			} catch (IOException | CannotReadException | TagException
					| ReadOnlyFileException | InvalidAudioFrameException ex) {
				ex.printStackTrace();
			}
		} else if (src == btnFindRoot || src == btnFindRoot2) {
			// Note this will return null if user attempts to choose a windows library
			File file = DirectoryChooserBuilder.create().title("Choose root dir...").
					build().showDialog(mainWindow);
			if (file != null) {
				if (src == btnFindRoot) {
					txtRoot.setText(file.getAbsolutePath());
				} else {
					txtRoot2.setText(file.getAbsolutePath());
				}
			}
		} else if (src == btnSaveRoot) {
			for (AudioInfo info : allMusic.values()) {
				try {
					info.saveAudio();
					System.out.println("Successful");
				} catch (CannotWriteException | CannotReadException | IOException | 
						TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
					System.out.println("Failed");
					ex.printStackTrace();
				}
			}
		}
	}
}
