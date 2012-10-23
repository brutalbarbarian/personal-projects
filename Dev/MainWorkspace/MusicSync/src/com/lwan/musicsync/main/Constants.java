package com.lwan.musicsync.main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import org.jaudiotagger.tag.FieldKey;

import com.lwan.musicsync.enums.FieldKeyEx;
import com.lwan.musicsync.util.JAudioTaggerUtil;
import com.lwan.util.CollectionUtil;

public class Constants {
	private static Object getValueOf(Object property) {
		if (property == gridCoverArtModeProperty()) {
			return false;
		} else if (property == gridImageSizeProperty()) {
			return 60;
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
	
	private static Enum<?>[] filteredProperties;
	
	public static Enum<?>[] getFilteredProperties() {
		if (filteredProperties == null) {
			filteredProperties = CollectionUtil.removeAll(FieldKeyEx.values(), getFieldKeyFilter(), false);
		}
		return filteredProperties;
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
	
	private static IntegerProperty gridImageSize;
	public static IntegerProperty gridImageSizeProperty() {
		if (gridImageSize == null) {
			gridImageSize = new SimpleIntegerProperty();
			gridImageSize.setValue((Integer)getValueOf(gridImageSize));
		}
		return gridImageSize;
	}
}
