package com.lwan.musicsync.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.stage.Stage;

import com.lwan.util.CollectionUtil;
import com.lwan.util.EnumUtil;
import com.lwan.util.StringUtil;
import com.sun.javafx.collections.ObservableListWrapper;

public class Start extends Application {
	Map<String, AudioInfo> allMusic;	// title, audio info
	Map<FieldKey, TableColumn<AudioInfo, String>> columns;
	
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
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		allMusic = new HashMap<String, AudioInfo>();
//		populateMusic(allMusic, "D:\\User Files\\Brutalbarbarian\\Music");
		populateMusic(allMusic, "C:\\Users\\Brutalbarbarian\\Music");
//		populateMusic(allMusic, "C:\\Users\\Brutalbarbarian\\Desktop\\test");
		
		StackPaneBuilder<?> spb = StackPaneBuilder.create();
		
		TableView<AudioInfo> table = new TableView<>();
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		table.getSelectionModel().setCellSelectionEnabled(true);
		ObservableList<AudioInfo> audioInfos = getObservableList();
		table.setItems(audioInfos);
		table.setEditable(true);
		
		List<TableColumn<AudioInfo, ?>> cols = new Vector<>();
		Enum<?>[] keys = CollectionUtil.removeAll(FieldKeyEx.values(), Constants.getFieldKeyFilter(), false);
		for (Enum<?> fk : keys) {
			if (fk != FieldKey.COVER_ART) {
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
		
		spb.children(table);
		
		
		StackPane pane = spb.build();
		
		Scene s = new Scene(pane);
		
		primaryStage.setScene(s);
		

//		for (Entry<FieldKey,Object> e : allTags.entrySet()) {
//			System.out.println(EnumUtil.processEnumName(e.getKey()) + ": " + e.getValue());
//		}
//		
//		BufferedImage img = ImageIO.read(new File("C:\\21.jpg"));
//		Artwork art = JAudioTaggerUtil.createArtwork(img);
//		allTags.put(FieldKey.COVER_ART, art);
//		
//		JAudioTaggerUtil.setAllTags(a, allTags);
//		
//		AudioFileIO.write(a);
		
		primaryStage.show();
	}
}
