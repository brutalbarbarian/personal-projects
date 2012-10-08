package com.lwan.musicsync.audioinfo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;

public interface AudioInfoProperty <T> extends Property<T>{
	
	public BooleanProperty modifiedProperty();
	public BooleanProperty nonRefProperty();
}
