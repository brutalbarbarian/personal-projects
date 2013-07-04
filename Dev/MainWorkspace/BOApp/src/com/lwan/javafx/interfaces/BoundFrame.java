package com.lwan.javafx.interfaces;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BusinessObject;

/**
 * Interface which all frames should implement.
 * Frames are containers which are intended to be used within
 * other frames or panes.
 * The constructor should always take in a BOLinkEx as parameter
 * which is managed by the parent. The frame should always
 * add a listener to the passed in link, and call doBuildAttributeLinks()
 * when the linked object changes. 
 * 
 * @author Lu
 *
 */
public interface BoundFrame <B extends BusinessObject>{
	/**
	 * Display the state of controls in the frame depending on the 
	 * state of the data structure.
	 * This should not be setting any values or make any changes
	 * whatsoever to the data structure or the links.
	 *  
	 */
	public void doDisplayState();
	
	/**
	 * Should be called after the primary linked object is changed. 
	 * This should be setting the links of any child frames and calling
	 * buildAttributeLink on bound controls.
	 * 
	 * Usually this function should also call doDisplayState().
	 */
	public void doBuildAttributeLinks();
	
	public BOLinkEx<B> getMainLink();
}
