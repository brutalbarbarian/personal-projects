package com.lwan.musicsync.enums;

import java.util.HashMap;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;

import com.lwan.util.CollectionUtil;
import com.lwan.util.EnumUtil;
import com.lwan.util.IOUtil;
import com.lwan.util.StringUtil;
import com.lwan.util.TimeUtil;

public class FieldKeyEx {
	private static Enum<?>[] ALL_VALUES;
	private static HashMap<String, Enum<?>> TITLE_ENUM_TABLE;

	public static void populateAllNonFieldKeyTags(AudioFile audio, String rootDir, Map<Enum<?>, Object> map) {
		AudioHeader header = audio.getAudioHeader();
		String relativePath = IOUtil.getRelativePath(rootDir, audio.getFile().getAbsolutePath());
//		map.put(NAME, audio.getFile().getName());
		map.put(RELATIVE_PATH, relativePath);
		map.put(BITRATE, header.getBitRate());
		map.put(EXTENSION, StringUtil.getFileExtension(audio.getFile().getName()));
		map.put(LENGTH, TimeUtil.secondsToString(header.getTrackLength()));
		map.put(PRIMARY_DIRECTORY, audio.getFile().getAbsolutePath());	// for reference only
//		map.put(ROOT_DIR, rootDir);
//		map.put(ORIG_DIR, relativePath);
	}
	
	public static void populateAllNonFieldKeyTags(Map<Enum<?>, Object> map) {
//		map.put(NAME, null);
		map.put(RELATIVE_PATH, null);
		map.put(BITRATE, null);
		map.put(EXTENSION, null);
		map.put(LENGTH, null);
		map.put(PRIMARY_DIRECTORY, null);	// for reference only
	}

	public static boolean isModifiable(Enum<?> e) {
		return isFieldKey(e) || isFileBasicInfo(e);
	}

	public static boolean isFileBasicInfo(Enum<?> e) {
		return e.getClass() == FileBasicInfo.class;
	}

	public static boolean isFieldKey(Enum<?> e) {
		return e.getClass() == FieldKey.class;
	}

	public static boolean isModifiable(String title) {
		return isModifiable(getEnumOfTitle(title));
	}

	public static Enum<?>[] values() {
		if (ALL_VALUES == null) {
			ALL_VALUES = CollectionUtil.mergeArrays(FieldKey.values(),
					FileBasicInfo.values(), FileAdvancedInfo.values());
		}
		return ALL_VALUES;
	}

	protected static HashMap<String, Enum<?>> getTitleEnumTable() {
		if (TITLE_ENUM_TABLE == null) {
			TITLE_ENUM_TABLE = new HashMap<>();
			for (Enum<?> e : values()) {
				TITLE_ENUM_TABLE.put(EnumUtil.processEnumName(e), e);
			}
		}
		return TITLE_ENUM_TABLE;
	}
//	
//	public static boolean IsPrivate(Enum<?> e) {
//		return FileAdvancedInfo.isPrivate(e);
//	}

	public static Enum<?> getEnumOfTitle(String s) {
		return getTitleEnumTable().get(s);
	}
	
	// Modifiable File info
//	public static final FileBasicInfo NAME = FileBasicInfo.NAME;
	public static final FileBasicInfo RELATIVE_PATH = FileBasicInfo.RELATIVE_PATH;

	// Unmodifiable File info
	public static final FileAdvancedInfo BITRATE = FileAdvancedInfo.BITRATE;
	public static final FileAdvancedInfo EXTENSION = FileAdvancedInfo.EXTENSION;
	public static final FileAdvancedInfo LENGTH = FileAdvancedInfo.LENGTH;
//	public static final FileAdvancedInfo ROOT_DIR = FileAdvancedInfo.ROOT_DIR;
//	public static final FileAdvancedInfo ORIG_DIR = FileAdvancedInfo.ORIG_DIR;
	public static final FileAdvancedInfo PRIMARY_DIRECTORY = FileAdvancedInfo.PRIMARY_DIRECTORY;

	// Modifiable FieldKey tag Fields
	public static final FieldKey ALBUM = FieldKey.ALBUM;
	public static final FieldKey ALBUM_ARTIST = FieldKey.ALBUM_ARTIST;
	public static final FieldKey ALBUM_ARTIST_SORT = FieldKey.ALBUM_ARTIST_SORT;
	public static final FieldKey ALBUM_SORT = FieldKey.ALBUM_SORT;
	public static final FieldKey AMAZON_ID = FieldKey.AMAZON_ID;
	public static final FieldKey ARRANGER = FieldKey.ARRANGER;
	public static final FieldKey ARTIST = FieldKey.ARTIST;
	public static final FieldKey ARTIST_SORT = FieldKey.ARTIST_SORT;
	public static final FieldKey ARTISTS = FieldKey.ARTISTS;
	public static final FieldKey BARCODE = FieldKey.BARCODE;
	public static final FieldKey BPM = FieldKey.BPM;
	public static final FieldKey CATALOG_NO = FieldKey.CATALOG_NO;
	public static final FieldKey COMMENT = FieldKey.COMMENT;
	public static final FieldKey COMPOSER = FieldKey.COMPOSER;
	public static final FieldKey COMPOSER_SORT = FieldKey.COMPOSER_SORT;
	public static final FieldKey CONDUCTOR = FieldKey.CONDUCTOR;
	public static final FieldKey COVER_ART = FieldKey.COVER_ART;
	public static final FieldKey CUSTOM1 = FieldKey.CUSTOM1;
	public static final FieldKey CUSTOM2 = FieldKey.CUSTOM2;
	public static final FieldKey CUSTOM3 = FieldKey.CUSTOM3;
	public static final FieldKey CUSTOM4 = FieldKey.CUSTOM4;
	public static final FieldKey CUSTOM5 = FieldKey.CUSTOM5;
	public static final FieldKey DISC_NO = FieldKey.DISC_NO;
	public static final FieldKey DISC_TOTAL = FieldKey.DISC_TOTAL;
	public static final FieldKey DJMIXER = FieldKey.DJMIXER;
	public static final FieldKey ENCODER = FieldKey.ENCODER;
	public static final FieldKey ENGINEER = FieldKey.ENGINEER;
	public static final FieldKey FBPM = FieldKey.FBPM;
	public static final FieldKey GENRE = FieldKey.GENRE;
	public static final FieldKey GROUPING = FieldKey.GROUPING;
	public static final FieldKey ISRC = FieldKey.ISRC;
	public static final FieldKey IS_COMPLIATION = FieldKey.IS_COMPILATION;
	public static final FieldKey KEY = FieldKey.KEY;
	public static final FieldKey LANGUAGE = FieldKey.LANGUAGE;
	public static final FieldKey LYRICIST = FieldKey.LYRICIST;
	public static final FieldKey LYRICS = FieldKey.LYRICS;
	public static final FieldKey MEDIA = FieldKey.MEDIA;
	public static final FieldKey MIXER = FieldKey.MIXER;
	public static final FieldKey MOOD = FieldKey.MOOD;
	public static final FieldKey ARTISTID = FieldKey.MUSICBRAINZ_ARTISTID;
	public static final FieldKey DISC_ID = FieldKey.MUSICBRAINZ_DISC_ID;
	public static final FieldKey MUSICBRAINZ_ORIGINAL_RELEASE_ID = FieldKey.MUSICBRAINZ_ORIGINAL_RELEASE_ID;
	public static final FieldKey MUSICBRAINZ_RELEASEARTISTID = FieldKey.MUSICBRAINZ_RELEASEARTISTID;
	public static final FieldKey MUSICBRAINZ_RELEASEID = FieldKey.MUSICBRAINZ_RELEASEID;
	public static final FieldKey MUSICBRAINZ_RELEASE_COUNTRY = FieldKey.MUSICBRAINZ_RELEASE_COUNTRY;
	public static final FieldKey MUSICBRAINZ_RELEASE_GROUP_ID = FieldKey.MUSICBRAINZ_RELEASE_GROUP_ID;
	public static final FieldKey MUSICBRAINZ_RELEASE_STATUS = FieldKey.MUSICBRAINZ_RELEASE_STATUS;
	public static final FieldKey MUSICBRAINZ_RELEASE_TYPE = FieldKey.MUSICBRAINZ_RELEASE_TYPE;
	public static final FieldKey MUSICBRAINZ_TRACK_ID = FieldKey.MUSICBRAINZ_TRACK_ID;
	public static final FieldKey MUSICBRAINZ_WORK_ID = FieldKey.MUSICBRAINZ_WORK_ID;
	public static final FieldKey MUSICBRAINZ_MUSICIP_ID = FieldKey.MUSICIP_ID;
	public static final FieldKey OCCASION = FieldKey.OCCASION;
	public static final FieldKey ORIGINAL_ALBUM = FieldKey.ORIGINAL_ALBUM;
	public static final FieldKey ORIGINAL_ARTIST = FieldKey.ORIGINAL_ARTIST;
	public static final FieldKey ORIGINAL_LYRICIST = FieldKey.ORIGINAL_LYRICIST;
	public static final FieldKey ORIGINAL_YEAR = FieldKey.ORIGINAL_YEAR;
	public static final FieldKey QUALITY = FieldKey.QUALITY;
	public static final FieldKey PRODUCER = FieldKey.PRODUCER;
	public static final FieldKey RATING = FieldKey.RATING;
	public static final FieldKey RECORD_LABEL = FieldKey.RECORD_LABEL;
	public static final FieldKey REMIXER = FieldKey.REMIXER;
	public static final FieldKey SCRIPT = FieldKey.SCRIPT;
	public static final FieldKey TAGS = FieldKey.TAGS;
	public static final FieldKey TEMPO = FieldKey.TEMPO;
	public static final FieldKey TITLE = FieldKey.TITLE;
	public static final FieldKey TITLE_SORT = FieldKey.TITLE_SORT;
	public static final FieldKey TRACK = FieldKey.TRACK;
	public static final FieldKey TRACK_TOTAL = FieldKey.TRACK_TOTAL;
	public static final FieldKey URL_DISCOGS_ARTIST_SITE = FieldKey.URL_DISCOGS_ARTIST_SITE;
	public static final FieldKey URL_DISCOGS_RELEASE_SITE = FieldKey.URL_DISCOGS_RELEASE_SITE;
	public static final FieldKey URL_LYRICS_SITE = FieldKey.URL_LYRICS_SITE;
	public static final FieldKey URL_OFFICIAL_ARTIST_SITE = FieldKey.URL_OFFICIAL_ARTIST_SITE;
	public static final FieldKey URL_OFFICIAL_RELEASE_SITE = FieldKey.URL_OFFICIAL_RELEASE_SITE;
	public static final FieldKey URL_WIKIPEDIA_ARTIST_SITE = FieldKey.URL_WIKIPEDIA_ARTIST_SITE;
	public static final FieldKey URL_WIKIPEDIA_RELEASE_SITE = FieldKey.URL_WIKIPEDIA_RELEASE_SITE;
	public static final FieldKey YEAR = FieldKey.YEAR;
	public static final FieldKey ACOUSTID_FINGERPRINT = FieldKey.ACOUSTID_FINGERPRINT;
	public static final FieldKey ACOUSTID_ID = FieldKey.ACOUSTID_ID;
	public static final FieldKey COUNTRY = FieldKey.COUNTRY;
}
