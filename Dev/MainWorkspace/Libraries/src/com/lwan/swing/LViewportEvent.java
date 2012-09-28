package com.lwan.swing;

import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")
public class LViewportEvent extends ChangeEvent{
	public static final int EXTENT_SIZE = 0;
	public static final int VIEW = 1;
	public static final int VIEW_POSITION = 2;
	public static final int RESHAPE = 3;
	public static final int VIEW_SIZE = 4;
	public static final int COMPONENT_RESIZE = 5;
	
	public final int changeType;
	
	public LViewportEvent (Object source, int type) {
		super(source);
		changeType = type;
	}
}
