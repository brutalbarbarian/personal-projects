package com.lwan.javafx.interfaces;

import javafx.beans.property.Property;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BusinessObject;

public interface BoundBasePane <B extends BusinessObject> {
	public Property<PaneState> stateProperty();
	public PaneState getState();
	
	public BOLinkEx<B> getMainLink();
	
	/**
	 * Called when changes to either the links or the data behind the
	 * links require the form to change state beyond what the links
	 * directly dictate.
	 * 
	 * This should always be called by paneOperationOpen()
	 * 
	 */
	public void displayPaneState();
	
	
	/**
	 * Called when some major change has been done to the links
	 * which will affect the bound state of controls.
	 * This should ensure all bound controls are correctly linked
	 * to its corresponding attributes. 
	 * 
	 */
	public void buildAttributeLinks();
}
