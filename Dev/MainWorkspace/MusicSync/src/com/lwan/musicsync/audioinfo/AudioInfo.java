package com.lwan.musicsync.audioinfo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

import com.lwan.musicsync.enums.FieldKeyEx;
import com.lwan.musicsync.enums.FileBasicInfo;
import com.lwan.musicsync.main.Constants;
import com.lwan.musicsync.main.Start;
import com.lwan.util.IOUtil;
import com.lwan.util.StringUtil;
import com.lwan.util.TimeUtil;
import com.lwan.util.media.JAudioTaggerUtil;

/**
 * Record class containing all information related to a audio file.
 * Includes all properties for each in use tag, as well as several convinence/helper functions.
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("rawtypes")
public class AudioInfo {
	public Map<Enum<?>, AudioInfoProperty> properties;
	
	public Map<AudioFileInfo, Map<Enum<?>, Object>> allTags; 
	public AudioFileInfo primaryFile;

	public AudioInfo(File f, String rootDir) throws IOException, CannotReadException, 
			TagException, ReadOnlyFileException, InvalidAudioFrameException {
		AudioFile audio = AudioFileIO.read(f);
		AudioFileInfo fileInfo = new AudioFileInfo(rootDir, IOUtil.getRelativePath(rootDir, f.getAbsolutePath()));
		Map<Enum<?>, Object> tags = JAudioTaggerUtil.getAllTags(audio, true, Constants.getFieldKeyFilter());
		FieldKeyEx.populateAllNonFieldKeyTags(audio, rootDir, tags);
		
		initialise(fileInfo, tags);
	}
	
	/**
	 * 
	 * @param file
	 * @param tags
	 */
	public AudioInfo(AudioFileInfo file, Map<Enum<?>, Object> tags) {
		initialise(file, tags);
	}
	
	// create an empty audioinfo record.
	public AudioInfo() {
		// this will populate all properties with null as audio has no keys. 
		Map<Enum<?>, Object> tags = JAudioTaggerUtil.getAllTags(Constants.getFieldKeyFilter());
		FieldKeyEx.populateAllNonFieldKeyTags(tags);
		initialise(new AudioFileInfo("", ""), tags);
	}
	
	private void initialise(AudioFileInfo file, Map<Enum<?>, Object> tags) {
		allTags = new HashMap<>();
		allTags.put(file, tags);
		primaryFile = file;
	}
	
	public void resetModified() {
		for (AudioInfoProperty p : properties.values()) {
			p.modifiedProperty().set(false);
		}
	}
	
	/**
	 * Get the primary tags
	 * 
	 * @return
	 */
	public Map<Enum<?>, Object> tags(){
		return allTags.get(primaryFile);
	}
	
	public void saveAudio() throws CannotReadException, IOException, TagException, ReadOnlyFileException, 
			InvalidAudioFrameException, CannotWriteException {
		// First re-fetch the primary AudioFile
		String dir = primaryFile.getFullPath();
		
		System.out.println("Saving: " + dir);
		
		// Save the tags to the file
		AudioFile audio = AudioFileIO.read(new File(dir));
		JAudioTaggerUtil.setAllTags(audio, tags());
		AudioFileIO.write(audio);
		
		System.out.println("Tags Saved");
		
		// delete all other files that isn't the primary
		for (AudioFileInfo info : allTags.keySet()) {
			if (info != primaryFile) {
				Files.delete(Paths.get(info.getFullPath()));
			}
		}
		
		System.out.println("Non-primary files removed");
		
		// move primary file to its final position if needed
		String newDir = IOUtil.getAbsolutePath(primaryFile.rootDir, tags().get(FileBasicInfo.RELATIVE_PATH).toString());
		if (!newDir.equals(dir)) {
			Files.move(Paths.get(dir), Paths.get(newDir), StandardCopyOption.REPLACE_EXISTING, 
					StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.ATOMIC_MOVE);
		}
		
		System.out.println("Primary File moved");
		
		// Find out if another root exists. if it does.. then copy the file over too.
		String otherRt = Start.getApp().getOtherPath(primaryFile.rootDir);
		if (otherRt.length() > 0) {
			otherRt = IOUtil.getAbsolutePath(otherRt, tags().get(FileBasicInfo.RELATIVE_PATH).toString());
			Files.copy(Paths.get(newDir), Paths.get(otherRt), StandardCopyOption.REPLACE_EXISTING, 
					StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.ATOMIC_MOVE);
			
			System.out.println("Copied to other root");
		}
	}
	
	/**
	 * Return true if merge is fine...return false if they are too different.
	 * 
	 * @param ai
	 * @return
	 */
	public boolean merge(AudioInfo ai) {
		// check both files have similar play times. give a 5 second allowance
		if (TimeUtil.parseTime(ai.tags().get(FieldKeyEx.LENGTH).toString()) - 
				TimeUtil.parseTime(tags().get(FieldKeyEx.LENGTH).toString()) > 5) {
			return false;
		}
		
		AudioInfo primary;
		// check which one has higher bit rate
		int thisBit = Integer.parseInt(tags().get(FieldKeyEx.BITRATE).toString());
		int aiBit = Integer.parseInt(ai.tags().get(FieldKeyEx.BITRATE).toString());
		if (thisBit == aiBit) {
			// find out which one has more tags filled in...
			int thisTags = 0, aiTags = 0;
			for (Object o : tags().values()) {
				if (!StringUtil.isNullOrBlank(o)) {
					thisTags++;
				}
			}
			for(Object o : ai.tags().values()) {
				if (!StringUtil.isNullOrBlank(o)) {
					aiTags++;
				}				
			}
			if (thisTags >= aiTags) {
				// if both are equal...just keep using this one
				primary = this;
			} else {
				primary = ai;
			}
		} else if (thisBit < aiBit) {
			primary = this;
		} else {
			primary = ai;
		}
		// merge set of all tags
		allTags.putAll(ai.allTags);
		// ensure primary file is pointing to the best candidate
		primaryFile = primary.primaryFile;
		
		return true;
	}
	
	/**
	 * Must call this before attempting to access any of the properties.
	 * Also must call this each time the primary file changes.
	 * 
	 */
	public void setupProperties() {
		properties = new HashMap<Enum<?>, AudioInfoProperty>();
		for (Enum<?> key : tags().keySet()) {
			if (key == FieldKey.COVER_ART) {
				properties.put(key, new AudioInfoArtworkProperty(this));
			} else if (key == FieldKey.RATING) {
				properties.put(key, new AudioInfoRatingProperty(this));
			} else {
				properties.put(key, new AudioInfoStringProperty(this, key));
			}
		}
		// set all properities to default to not modified.
		resetModified();
	}

	public BufferedImage getArtworkAsBufferedImage() {
		return cover_artProperty().getAsBufferedImage();
	}
	
	public void setArtworkAsBufferedImage(BufferedImage img) {
		cover_artProperty().setAsBufferedImage(img);
	}
	
	public Image getArtworkAsFullSizedImage() {
		return cover_artProperty().getArtworkAsFullSizedImage();
	}
	
	public AudioInfoStringProperty artistProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKey.ARTIST);
	}
	public AudioInfoStringProperty titleProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKey.TITLE);
	}
	public AudioInfoStringProperty albumProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKey.ALBUM);
	}
	public AudioInfoStringProperty album_artistProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKey.ALBUM_ARTIST);
	}
	public AudioInfoStringProperty genreProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKey.GENRE);
	}
	public AudioInfoRatingProperty ratingProperty() {
		return (AudioInfoRatingProperty) properties.get(FieldKey.RATING);
	}
	public AudioInfoStringProperty trackProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKey.TRACK);
	}
	public AudioInfoStringProperty yearProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKey.YEAR);
	}
	public AudioInfoStringProperty relative_pathProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKeyEx.RELATIVE_PATH);
	}
	public AudioInfoStringProperty bitrateProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKeyEx.BITRATE);
	}
	public AudioInfoStringProperty extensionProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKeyEx.EXTENSION);
	}
	public AudioInfoStringProperty lengthProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKeyEx.LENGTH);
	}
	public AudioInfoArtworkProperty cover_artProperty() {
		return (AudioInfoArtworkProperty)properties.get(FieldKey.COVER_ART);
	}
}
