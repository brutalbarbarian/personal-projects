package com.lwan.bo;

public class Attribute {
	private String displayPath;
	private String valuePath;
	private String displayName;
	
	public Attribute(String path, BusinessObject root) {
		this(path, (String)null);
		
		initName(root);
	}
	
	public Attribute(String path, String name) {
		this(path, null, name);
	}
	
	public Attribute(String path, String displayPath, String name) {
		this.valuePath = path;
		this.displayPath = displayPath;
		this.displayName = name;
	}
	
	public String toString() {
		return getDisplayName();
	}
	
	protected void initName(BusinessObject root) {
		if (displayName == null) {
			displayName = getDisplayAttribute(root).getName();
		}
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public BOAttribute<?> getDisplayAttribute(BusinessObject root) {
		String path = displayPath == null? valuePath : displayPath;
		return root.findAttributeByPath(path);
	}
	
	public BOAttribute<?> getValueAttribute(BusinessObject root) {
		return  root.findAttributeByPath(valuePath);
	}
	
	public String getValuePath() {
		return valuePath;
	}
}
