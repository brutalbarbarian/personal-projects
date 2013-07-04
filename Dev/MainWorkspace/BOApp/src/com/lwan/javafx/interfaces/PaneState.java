package com.lwan.javafx.interfaces;

public enum PaneState {
	Inserting,
	Editing,
	Browsing,
	Inactive;
	
	public boolean isActive() {
		return this != Inactive;
	}

	public boolean isEditState() {
		return this == Editing || this == Inserting;
	}
}
