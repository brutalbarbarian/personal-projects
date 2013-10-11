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
	private ModifiedEventType type;
	
	public ModifiedEvent(BusinessObject source, ModifiedEventType type) {
		this(source, type, null);
	}
	
	public ModifiedEvent(BusinessObject source, ModifiedEventType type, String tags) {
		this(source, source, type, tags);
	}
	
	public boolean isUserModified() {
		return isAttribute() && asAttribute().isUserSet();
	}
	
	@SuppressWarnings("unchecked")
	public <T> BOAttribute<T> asAttribute() {
		return (BOAttribute<T>)source;
	}
	
	public boolean checkSource (Class<? extends BusinessObject> parentClass, String attributeName) {
		if (parentClass == null) return false;
		BusinessObject parent = getAttributeOwner();
		if (parent == null || !parentClass.isAssignableFrom(parent.getClass())) {
			return false;
		}
		return parent.isChildByName(source, attributeName);
	}
	
	public ModifiedEventType getType() {
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
	
	public boolean isAttribute(Class<? extends BusinessObject> ownerClass, String... name) {
		if (isAttribute() && getAttributeOwner().getClass().equals(ownerClass)) {
			for (String s : name) {
				if (getSource().getName().equalsIgnoreCase(s)) {
					return true;
				}
			}
		}
		return false;
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
	
	private ModifiedEvent(BusinessObject source, BusinessObject caller, ModifiedEventType type, String tags) {
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
	public BusinessObject getCaller() {
		return directChild;
	}
	
	/**
	 * Get the name of the source
	 * Effectively the same as calling getSource().getName()
	 * Will return empty string if getSource() == null
	 * 
	 * @return
	 */
	public String getName() {
		if (source == null) {
			return "";
		} else {
			return source.getName();
		}
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
