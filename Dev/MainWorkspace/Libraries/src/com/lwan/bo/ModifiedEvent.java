package com.lwan.bo;

import com.lwan.util.GenericsUtil;

/**
 * Modified event can be thrown from either an attribute or an object.
 * 
 * @author Brutalbarbarian
 *
 */
public class ModifiedEvent {
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_ATTRIBUTE = 1;
//	public static final int TYPE_SET = 2;
	public static final int TYPE_ACTIVE = 2;
	public static final int TYPE_LINK = 3;
	
	private BusinessObject source;
	private String tags;
	private BusinessObject directChild;
	private int type;
	
	public ModifiedEvent(BusinessObject source, int type) {
		this(source, type, null);
	}
	
	public ModifiedEvent(BusinessObject source, int type, String tags) {
		this(source, source, type, tags);
	}
	
	public boolean isUserModified() {
		return isAttribute() && asAttribute().isUserSet();
	}
	
	public BOAttribute<?> asAttribute() {
		return (BOAttribute<?>)source;
	}
	
	public boolean checkSource (Class<? extends BusinessObject> parentClass, String attributeName) {
		if (parentClass == null) return false;
		BusinessObject parent = getAttributeOwner();
		if (parent == null || !parentClass.isAssignableFrom(parent.getClass())) {
			return false;
		}
		return parent.isChildByName(source, attributeName);
	}
	
	public int getType() {
		return type;
	}
	
	/**
	 * Check if the source which threw this event is an attribute
	 * 
	 * @return
	 */
	public boolean isAttribute() {
		if (source == null) {
			return false;
		}
		
		return source.isAttribute();
	}
	
	/**
	 * Get the owner of the source if the source is a BOAttribute.
	 * Will return null otherwise.
	 * 
	 * @return
	 */
	public BusinessObject getAttributeOwner() {
		if (isAttribute()) {
			return source.getOwner();
		} else {
			return null;
		}
	}
	
	private ModifiedEvent(BusinessObject source, BusinessObject caller, int type, String tags) {
		this.type = type;
		this.source = source;
		directChild = caller;
		this.tags = tags;
	}
	
	/**
	 * Construct modified event from an existing event, where the caller differs from the original source.
	 * 
	 * @param sourceEvent
	 * @param caller
	 */
	protected ModifiedEvent(ModifiedEvent sourceEvent, BusinessObject caller) {
		this(sourceEvent.source, caller, sourceEvent.type, sourceEvent.tags);
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
