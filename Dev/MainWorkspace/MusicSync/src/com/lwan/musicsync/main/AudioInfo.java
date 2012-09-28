package com.lwan.musicsync.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

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
	public Map<Enum<?>, Object> tags;
	public Map<Enum<?>, AudioInfoProperty> properties;
	public AudioFile audio;
	
	public String root;
	
	public AudioInfo(File f, String rootDir) throws IOException, CannotReadException, 
			TagException, ReadOnlyFileException, InvalidAudioFrameException {
		audio = AudioFileIO.read(f);
		tags = JAudioTaggerUtil.getAllTags(audio, true, Constants.getFieldKeyFilter());
		root = rootDir;
		FieldKeyEx.populateAllNonFieldKeyTags(this, tags);
		
		// setup properties
		setupProperties();
	}
	
	public void resetModified() {
		for (AudioInfoProperty p : properties.values()) {
			p.modifiedProperty().set(false);
		}
	}
	
	// create an empty audioinfo record.
	public AudioInfo() {
		// this will populate all properties with null as audio has no keys. 
		tags = JAudioTaggerUtil.getAllTags(Constants.getFieldKeyFilter());
		root = "";
		FieldKeyEx.populateAllNonFieldKeyTags(tags);
		
		// setup properties
		setupProperties();
	}
	
	protected void setupProperties() {
		properties = new HashMap<Enum<?>, AudioInfoProperty>();
		for (Enum<?> key : tags.keySet()) {
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
//	public AudioInfoStringProperty ratingProperty() {
//		return (AudioInfoStringProperty) properties.get(FieldKey.RATING);
//	}
	public AudioInfoRatingProperty ratingProperty() {
		return (AudioInfoRatingProperty) properties.get(FieldKey.RATING);
	}
	public AudioInfoStringProperty trackProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKey.TRACK);
	}
	public AudioInfoStringProperty yearProperty() {
		return (AudioInfoStringProperty) properties.get(FieldKey.YEAR);
	}
	public AudioInfoStringProperty nameProperty() {
		return (AudioInfoStringProperty)properties.get(FieldKeyEx.NAME);
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
