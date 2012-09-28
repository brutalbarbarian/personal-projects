package com.lwan.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.Vector;

import javax.swing.JViewport;

@SuppressWarnings("serial")
/**
 * Extension of LJViewport with a more detailed events system
 * 
 * @author Brutalbarbarian
 *
 */
public class LJViewport extends JViewport {
	protected int lastStateChangeEvent;
	protected boolean hasViewportNotifications;
	protected List<LViewportListener> viewportListeners;
	
	public LJViewport () {
		super();
		viewportListeners = new Vector<>();
	}
	
	public void addViewportListener (LViewportListener l) {
		viewportListeners.add(l);
	}
	
	public void removeViewportListener (LViewportListener l) {
		viewportListeners.remove(l);
	}
	
	public void setViewportNotifications (boolean isEnabled) {
		hasViewportNotifications = isEnabled;
	}
	
	@Override
	public void fireStateChanged () {
		super.fireStateChanged();
		if (hasViewportNotifications) {
			fireViewportChanged(lastStateChangeEvent);
		}
	}
	
	public LViewportListener[] getViewportListeners (){
		LViewportListener [] listeners = new LViewportListener [viewportListeners.size()];
		return viewportListeners.toArray(listeners);
	}
	
	protected void fireViewportChanged (int changeType) {
		LViewportEvent e = new LViewportEvent(this, changeType);
		for (LViewportListener l : viewportListeners) {
			l.ViewportChanged(e);
		}
	}
	
    public void setExtentSize(Dimension newExtent) {
    	lastStateChangeEvent = LViewportEvent.EXTENT_SIZE;
    	super.setExtentSize(newExtent);
    	lastStateChangeEvent = LViewportEvent.COMPONENT_RESIZE;	//in order to catch the component resize event
    }
	
    public void setView(Component view) {
		lastStateChangeEvent = LViewportEvent.VIEW;
    	super.setView(view);
    	lastStateChangeEvent = LViewportEvent.COMPONENT_RESIZE;	//in order to catch the component resize event
    }
	
	@Override
	public void setViewPosition(Point p) {
		lastStateChangeEvent = LViewportEvent.VIEW_POSITION;
		super.setViewPosition(p);
    	lastStateChangeEvent = LViewportEvent.COMPONENT_RESIZE;	//in order to catch the component resize event
	}
	
   public void reshape(int x, int y, int w, int h) {
	   lastStateChangeEvent = LViewportEvent.RESHAPE;
	   super.reshape(x, y, w, h);
	   lastStateChangeEvent = LViewportEvent.COMPONENT_RESIZE;	//in order to catch the component resize event
	}
	
	public void setViewSize(Dimension newSize) {
		lastStateChangeEvent = LViewportEvent.VIEW_SIZE;
		super.setViewSize(newSize);
    	lastStateChangeEvent = LViewportEvent.COMPONENT_RESIZE;	//in order to catch the component resize event
	}
}