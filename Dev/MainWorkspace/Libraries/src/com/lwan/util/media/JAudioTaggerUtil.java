package com.lwan.util.media;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;
import org.jaudiotagger.tag.reference.PictureTypes;

import com.lwan.util.CollectionUtil;
import com.lwan.util.ImageUtil;
import com.lwan.util.StringUtil;

public class JAudioTaggerUtil {
	/* The Following fields can be considered useless */
	public static final FieldKey [] MusicBrainzFields = {FieldKey.MUSICBRAINZ_ARTISTID,
		FieldKey.MUSICBRAINZ_DISC_ID, FieldKey.MUSICBRAINZ_ORIGINAL_RELEASE_ID,
		FieldKey.MUSICBRAINZ_RELEASE_COUNTRY, FieldKey.MUSICBRAINZ_RELEASE_GROUP_ID,
		FieldKey.MUSICBRAINZ_RELEASE_STATUS, FieldKey.MUSICBRAINZ_RELEASE_TYPE,
		FieldKey.MUSICBRAINZ_RELEASEARTISTID, FieldKey.MUSICBRAINZ_RELEASEID,
		FieldKey.MUSICBRAINZ_TRACK_ID, FieldKey.MUSICBRAINZ_WORK_ID};
	public static final FieldKey [] CustomFields = {FieldKey.CUSTOM1, FieldKey.CUSTOM2,
		FieldKey.CUSTOM3, FieldKey.CUSTOM4, FieldKey.CUSTOM5};
	/* The Following fields can be considered not useful */
	public static final FieldKey [] SortFields = {FieldKey.ALBUM_ARTIST_SORT,
		FieldKey.COMPOSER_SORT, FieldKey.ARTIST_SORT, FieldKey.TITLE_SORT,
		FieldKey.ALBUM_SORT};
	public static final FieldKey [] UrlFields = {FieldKey.URL_DISCOGS_ARTIST_SITE,
		FieldKey.URL_DISCOGS_RELEASE_SITE, FieldKey.URL_LYRICS_SITE,
		FieldKey.URL_OFFICIAL_ARTIST_SITE, FieldKey.URL_OFFICIAL_RELEASE_SITE,
		FieldKey.URL_WIKIPEDIA_ARTIST_SITE, FieldKey.URL_WIKIPEDIA_RELEASE_SITE};
	public static final FieldKey [] ExtIDFields = {FieldKey.ACOUSTID_FINGERPRINT,
		FieldKey.ACOUSTID_ID, FieldKey.AMAZON_ID, FieldKey.BARCODE,
		FieldKey.CATALOG_NO, FieldKey.ISRC, FieldKey.KEY, FieldKey.MUSICIP_ID,
		FieldKey.RECORD_LABEL};
	public static final FieldKey [] EtcFields = {FieldKey.BPM, FieldKey.ARRANGER,
		FieldKey.CONDUCTOR, FieldKey.COMPOSER, FieldKey.COUNTRY, FieldKey.DJMIXER,
		FieldKey.DISC_NO, FieldKey.DISC_TOTAL, FieldKey.ENGINEER, FieldKey.ENCODER,
		FieldKey.FBPM, FieldKey.LANGUAGE, FieldKey.LYRICIST, FieldKey.IS_COMPILATION,
		FieldKey.GROUPING, FieldKey.MIXER, FieldKey.MEDIA, FieldKey.MOOD, FieldKey.ARTISTS,
		FieldKey.OCCASION, FieldKey.PRODUCER, FieldKey.QUALITY, FieldKey.REMIXER,
		FieldKey.SCRIPT, FieldKey.TAGS, FieldKey.TEMPO, FieldKey.TRACK_TOTAL};
	public static final FieldKey[] OriginalFields = {FieldKey.ORIGINAL_ALBUM,
		FieldKey.ORIGINAL_ARTIST, FieldKey.ORIGINAL_LYRICIST, FieldKey.ORIGINAL_YEAR};
	public static final FieldKey[] OtherFields = {FieldKey.COMMENT, FieldKey.LYRICS};
	
	public static final int [] RatingStars = {1, 64, 128, 196, 255}; 

	public static int getRating (AudioFile af) {
		Tag t = af.getTag();
		if (t == null) {
			return -1;
		}
		return getRating(Integer.parseInt(t.getFirstField(FieldKey.RATING).toString()));
	}
	
	public static int getRating (int value) {
		for (int i = 0; i < RatingStars.length; i++) {
			if (value == RatingStars[i]) {
				return i+1;
			}
		}
		return -1;	// not found
	}
	
	/**
	 * Get a map of field keys with no values
	 * 
	 * @param ignoredFields
	 * @return
	 */
	public static Map<Enum<?>, Object> getAllTags(FieldKey[] ignoredFields) {
		Map<Enum<?>, Object> res = new HashMap<>();
		for (FieldKey fk : FieldKey.values()){
			if (!CollectionUtil.exists(ignoredFields, fk, false)) {
				res.put(fk, null);
			}
		}
		return res;
	}
	
	public static Map<Enum<?>, Object> getAllTags(AudioFile file, boolean includeEmpty, 
			FieldKey[] ignoredFields) {
		Map<Enum<?>, Object> res = new HashMap<>();
		
		Tag t = file.getTagOrCreateDefault();
		
		for (FieldKey fk : FieldKey.values()) {
			// ignore CoverArt as it will only return number of bytes here
			if (fk != FieldKey.COVER_ART && 
					(ignoredFields == null || !CollectionUtil.exists(ignoredFields, fk, false))) {
				String val = t.getFirst(fk);
				if (includeEmpty || val.length() > 0) {
					res.put(fk, val);
				}
			}
		}
		if (ignoredFields == null || !CollectionUtil.exists(ignoredFields, FieldKey.COVER_ART, false)) {
			List<Artwork> arts = t.getArtworkList();
			Artwork art = null;
			for (Artwork a : arts) {
				try {
					byte[] binData = a.getBinaryData();
					if (binData != null) {
						Dimension size = ImageUtil.getImageSize(new ByteArrayInputStream(binData));
						if (size.width > 0 && size.height > 0) {
							art = a;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (includeEmpty || art != null) {
				res.put(FieldKey.COVER_ART, art);
			}
		}
		
		return res;
	}

	public static Artwork createArtwork (BufferedImage img) {
		Artwork art = ArtworkFactory.getNew();
		try {
			art.setBinaryData(ImageUtil.imageToByteArray(img, "jpeg"));
			// effectively the same as calling art.setImageFromData();
			// but without the overhead of recreating another BufferefImage
			art.setWidth(img.getWidth());
			art.setHeight(img.getHeight());
			art.setPictureType(PictureTypes.DEFAULT_ID);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return art;
	}
	
	public static void setAllTags (AudioFile file, Map<Enum<?>, Object> tags) {
		// create a new set of tags for the file
		Tag tag = file.getTagOrCreateAndSetDefault();
		// we don't want any of the original tags to remain
		for (FieldKey fk : FieldKey.values()) {
			tag.deleteField(fk);
		}
		
		// set all available tags
		for(FieldKey fk : FieldKey.values()) {
			if (fk == FieldKey.COVER_ART) {
				Artwork aw = (Artwork)tags.get(FieldKey.COVER_ART);
				if (aw != null) {
					try {
						tag.setField(aw);
					} catch (FieldDataInvalidException e) {
						e.printStackTrace();
					}
				}
			} else {
				Object val = tags.get(fk);
				if (!StringUtil.isNullOrBlank(val)) {
					try {
						tag.setField(fk, val.toString());
					} catch (KeyNotFoundException | FieldDataInvalidException e) {
						e.printStackTrace();
					}
				}
			}
		}
		file.setTag(tag);
	}
}
