package com.lwan.musicsync.main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.jaudiotagger.tag.FieldKey;

import com.lwan.util.CollectionUtil;
import com.lwan.util.media.JAudioTaggerUtil;

public class Constants {
	private static Object getValueOf(Object property) {
		if (property == gridCoverArtModeProperty()) {
			return true;
		}
		
		return null;
	}
	
	private static FieldKey[] fieldKeyFilter;
	
	public static FieldKey [] getFieldKeyFilter () {
		if (fieldKeyFilter == null) {
			fieldKeyFilter = CollectionUtil.mergeArrays(JAudioTaggerUtil.MusicBrainzFields,
					JAudioTaggerUtil.SortFields, JAudioTaggerUtil.CustomFields,
					JAudioTaggerUtil.EtcFields, JAudioTaggerUtil.ExtIDFields,
					JAudioTaggerUtil.UrlFields, JAudioTaggerUtil.OriginalFields,
					JAudioTaggerUtil.OtherFields);
		}
		return fieldKeyFilter;
	}
	
	public static String getTextCellStyle() {
		// faint lighter as background, no insets, no padding
		return "-fx-alignment:baseline-left;-fx-background-color:#FFFFFFAA;-fx-background-insets:0;-fx-padding:0";
	}
	
	private static BooleanProperty gridCoverArtMode;
	public static BooleanProperty gridCoverArtModeProperty() {
		if (gridCoverArtMode == null) {
			gridCoverArtMode = new SimpleBooleanProperty();
			gridCoverArtMode.setValue((Boolean)getValueOf(gridCoverArtMode));
		}
		return gridCoverArtMode;
	}
}
