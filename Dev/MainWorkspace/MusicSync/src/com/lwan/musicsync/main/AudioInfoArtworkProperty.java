package com.lwan.musicsync.main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.images.Artwork;

import com.lwan.util.media.JAudioTaggerUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

public class AudioInfoArtworkProperty extends ObjectPropertyBase<Image> implements AudioInfoProperty <Image> {
	BufferedImage imgAWT;	// temp cache for the image if required
	AudioInfo bean;
	String name;
	
	public AudioInfoArtworkProperty(AudioInfo audioInfo){
		bean = audioInfo;
		name = FieldKey.COVER_ART.name().toLowerCase();
		setImage();
	}

	@Override
	public Object getBean() {
		return bean;
	}

	@Override
	public String getName() {
		return name;
	}
	
	protected void setImage() {
		Artwork aw = (Artwork)bean.tags.get(FieldKey.COVER_ART);
		byte[] bytes = null;
		Image img = null;
		if (aw != null) {
			bytes = aw.getBinaryData();
			if (bytes != null) {
				img = new CustomImageFX(new ByteArrayInputStream(bytes), 100, 100, true, true);
			}
		}
		// clear imgFX
		if (aw == null || bytes == null) {
			img = null;
		}
		
		setValue(img);
	}
	
	public void setAsBufferedImage(BufferedImage img) {
		imgAWT = img;
		if (img == null) {
			bean.tags.put(FieldKey.COVER_ART, null);
		} else {
			bean.tags.put(FieldKey.COVER_ART, JAudioTaggerUtil.createArtwork(img));
		}
		setImage();
	}
	
	public BufferedImage getAsBufferedImage() {
		if (imgAWT == null) {
			Artwork aw = (Artwork)bean.tags.get(FieldKey.COVER_ART);
			if (aw != null) {
				Object image = null;
				try {
					image = aw.getImage();
				// if there are any issues while loading...just ignore. 
				} catch (IOException e) {}
				if (image != null) {
					imgAWT = (BufferedImage)image;
				}
			}
		}
		return imgAWT;
	}
	
	/**
	 * Do not call set for this property as this won't actually set
	 * the image property.</br>
	 * Call setAsBufferedImage instead.
	 * 
	 */
	public void setValue (Image value) {
		// in order to still throw the correct events
		// too hard to check?... just assume its a different image..
		modifiedProperty().set(true);
		super.setValue(value);
	}

	public Image getArtworkAsFullSizedImage() {
		Artwork aw = (Artwork)bean.tags.get(FieldKey.COVER_ART);
		byte[] bytes = null;
		Image img = null;
		if (aw != null) {
			bytes = aw.getBinaryData();
			if (bytes != null) {
				img = new CustomImageFX(new ByteArrayInputStream(bytes));
			}
		}
		return img;
	}

	private BooleanProperty modifiedProperty;
	@Override
	public BooleanProperty modifiedProperty() {
		if (modifiedProperty == null) {
			modifiedProperty = new SimpleBooleanProperty();
		}
		return modifiedProperty;
	}

	private BooleanProperty nonRefProperty;
	@Override
	public BooleanProperty nonRefProperty() {
		if(nonRefProperty == null) {
			nonRefProperty = new SimpleBooleanProperty(false);
			modifiedProperty().addListener(new ChangeListener<Boolean>(){
				public void changed(ObservableValue<? extends Boolean> arg0,
						Boolean oldValue, Boolean newValue) {
					if (newValue) {
						nonRefProperty.set(false);
					}
				}
			});
		}
		return nonRefProperty;
	}
}
