package com.lwan.musicsync.audioinfo;

import org.jaudiotagger.tag.FieldKey;

import com.lwan.util.GenericsUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AudioInfoRatingProperty extends IntegerPropertyBase implements AudioInfoProperty<Number>{
	private AudioInfo info;
	private String name;
	
	public AudioInfoRatingProperty (AudioInfo info) {
		this.info = info;
		name = FieldKey.COVER_ART.name().toLowerCase();
		doSet = true;
		ensurePropertyUpdated();
	}
	
	public void set(int value) {
		if (doSet) {
			setValue(value);
		} else {
			super.set(value);
		}
	}

	boolean doSet;
	public void setValue(Number value) {
		if (!getValue().equals(GenericsUtil.Coalice(value, -1))) {
			modifiedProperty().set(true);
			doSet = false;
			if (value != null) {
				info.tags().put(FieldKey.RATING, value);
				super.setValue(value);
			} else {
				// clears value
				info.tags().put(FieldKey.RATING, null);
				super.setValue(-1);
			}
			doSet = true;
		}
	}
	
	@Override
	public Object getBean() {
		return info;
	}

	@Override
	public String getName() {
		return name;
	}

	private BooleanProperty modifiedProperty;
	@Override
	public BooleanProperty modifiedProperty() {
		if (modifiedProperty == null) {
			modifiedProperty = new SimpleBooleanProperty(false);
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

	@Override
	public void ensurePropertyUpdated() {
		String item = GenericsUtil.Coalice(info.tags().get(FieldKey.RATING), "").toString();
		if (item.length() > 0) {
			setValue(Integer.parseInt(item));	
		} else {
			setValue(null);
		}
	}
}
