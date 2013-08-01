package com.lwan.eaproj.app.frames;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.bo.ModifiedEventType;
import com.lwan.javafx.controls.panes.TBorderPane;
import com.lwan.javafx.interfaces.BoundFrame;
import com.lwan.util.wrappers.Disposable;

public abstract class FrameBoundBase<T extends BusinessObject> extends TBorderPane 
		implements Disposable, ModifiedEventListener, BoundFrame<T>{
	private BOLinkEx<T> link;
	
	public FrameBoundBase(BOLinkEx<T> link) {
		this.link = link;
		buildFrame();
		
		link.addListener(this);
		handleModified(null);
	}
	
	protected abstract void buildFrame();

	@Override
	public BOLinkEx<T> getMainLink() {
		return link;
	}

	@Override
	public void handleModified(ModifiedEvent event) {
		if (event == null || event.getType() == ModifiedEventType.Link) {
			doBuildAttributeLinks();
			doDisplayState();
		}
	}
	
	public boolean isActive() {
		if (getMainLink().getLinkedObject() == null) {
			return false;
		} else {
			return getMainLink().getLinkedObject().isActive();
		}
	}

	@Override
	public void dispose() {
		link.removeListener(this);
	}

}
