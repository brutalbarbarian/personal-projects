package com.lwan.bo;

import com.lwan.util.GenericsUtil;

/**
 * Modified event can be thrown from either an attribute or an object.
 * 
 * @author Brutalbarbarian
 *
 */
public class ModifiedEvent {
	private BusinessObject source;
	private String tags;
	private BusinessObject directChild;
	
	public ModifiedEvent(BusinessObject source) {
		this(source, null);
	}
	
	public ModifiedEvent(BusinessObject source, String tags) {
		this(source, source, tags);
	}
	
	private ModifiedEvent(BusinessObject source, BusinessObject caller, String tags) {
		this.source = source;
		directChild = source;
		this.tags = tags;
	}
	
	/**
	 * Construct modified event from an existing event, where the caller differs from the original source.
	 * 
	 * @param sourceEvent
	 * @param caller
	 */
	protected ModifiedEvent(ModifiedEvent sourceEvent, BusinessObject caller) {
		this(sourceEvent.source, caller, sourceEvent.tags);
	}

	/**
	 * Get the direct source of this event
	 * 
	 * @return
	 */
	public BusinessObject getSource() {
		return source;
	}
	
	/**
	 * Get the direct caller for this event. When passing up the hierarchy, this will be a direct descendant.
	 * Otherwise for a listener, this will be the object which called handleModify().
	 * 
	 * @return
	 */
	public BusinessObject getDirectChild() {
		return directChild;
	}
	
	/**
	 * Get the tags associated with this event. If the value is null, will return an empty string instead.
	 * 
	 * @return
	 */
	public String getTags() {
		return GenericsUtil.Coalice(tags, "");
	}
}