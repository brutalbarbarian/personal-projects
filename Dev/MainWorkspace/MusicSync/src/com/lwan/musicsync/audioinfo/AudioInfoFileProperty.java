package com.lwan.musicsync.audioinfo;

import com.lwan.musicsync.enums.FieldKeyEx;
import com.lwan.util.EnumUtil;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class AudioInfoFileProperty extends StringPropertyBase implements AudioInfoProperty<String>{
	private AudioInfo info;

	public AudioInfoFileProperty(AudioInfo info) {
		this.info = info;
		ensurePropertyUpdated();
		
		addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				System.out.println(arg1 + "," + arg2);
			}
		});
	}
	
	public void set(String value) {
		System.out.println("setting: " + value);
		super.set(value);
		info.changePrimary(value);
	}
	
	
	@Override
	public Object getBean() {
		return info;
	}

	@Override
	public String getName() {
		return EnumUtil.processEnumName(FieldKeyEx.PRIMARY_DIRECTORY);
	}
	
	@Override
	public void ensurePropertyUpdated() {
		super.setValue(info.primaryFile.getFullPath());
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
					if (!newValue.equals(oldValue)) {
						nonRefProperty.set(false);
					}
				}
			});
		}
		return nonRefProperty;
	}
}
