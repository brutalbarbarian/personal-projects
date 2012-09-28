package com.lwan.musicsync.main;

import org.jaudiotagger.tag.FieldKey;

import com.lwan.util.GenericsUtil;
import com.lwan.util.media.JAudioTaggerUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AudioInfoRatingProperty extends IntegerPropertyBase implements AudioInfoProperty<Number>{
	private AudioInfo parent;
	private String name;
	
	public AudioInfoRatingProperty (AudioInfo info) {
		parent = info;
		name = FieldKey.COVER_ART.name().toLowerCase();
		String item = GenericsUtil.Coalice(info.tags.get(FieldKey.RATING), "").toString();
		if (item.length() > 0) {
			super.setValue(Integer.parseInt(item));	
		} else {
			super.setValue(null);
		}
		
	}

	public void setValue(Number value) {
		if (!getValue().equals(GenericsUtil.Coalice(value, -1))) {
			if (value != null) {
				int val = JAudioTaggerUtil.RatingStars[value.intValue() - 1];
				parent.tags.put(FieldKey.RATING, val);
				super.setValue(val);
			} else {
				// clears value
				parent.tags.put(FieldKey.RATING, null);
				super.setValue(-1);
			}
			modifiedProperty().set(true);
		}
	}
	
	public Integer getValue () {
		return JAudioTaggerUtil.getRating(super.getValue());
	}
	
	@Override
	public Object getBean() {
		return parent;
	}

	@Override
	public String getName() {
		return name;
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
