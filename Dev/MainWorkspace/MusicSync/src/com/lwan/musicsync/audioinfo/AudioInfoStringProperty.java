package com.lwan.musicsync.audioinfo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AudioInfoStringProperty extends StringPropertyBase implements AudioInfoProperty<String>{
	private AudioInfo parent;
	private Enum<?> fkey;
	private String name;
	
	public AudioInfoStringProperty (AudioInfo info, Enum<?> key) {
		parent = info;
		fkey = key;
		name = key.name().toLowerCase();
		super.setValue((String) parent.tags.get(fkey));	//ensure the stored value is correct
	}
	
	@Override
	public Object getBean() {
		return parent;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setValue(String val) {
		if (val == null) {
			val = "";
		} else {
			val = val.trim();	//we don't want excess white spaces	
		}
		modifiedProperty().set(true);
		if (!val.equals(getValue())) {
			parent.tags.put(fkey, val);
			super.setValue(val);
		}
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
