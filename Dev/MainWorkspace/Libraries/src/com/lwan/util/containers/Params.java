package com.lwan.util.containers;

import java.util.TreeMap;

public class Params {
	private TreeMap<String, Object> values;
	
	public Params(Object... params) {
		if (params.length%2 == 1) {
			throw new RuntimeException("Odd number of params passed in for params construction");
		}
		
		if (params.length > 0) {
			values = new TreeMap<>();
			for (int i = 0; i < params.length; i+= 2) {
				values.put(params[i].toString(), params[i + 1]);
			}
		}
	}
	
	public <T> T getValueDefault (String key, T defaultValue) {
		T value = getValue(key);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(String key) {
		if (values == null) {
			return null;
		} else {
			return (T)values.get(key);
		}
	}

}
