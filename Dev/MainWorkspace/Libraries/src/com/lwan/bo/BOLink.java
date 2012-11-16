package com.lwan.bo;

/**
 * Business Object representing a weak link between two objects,
 * where the referenced object isn't technically a child of the owner of this
 * object. 
 * Any events the referenced object throws will not be passed up the
 * Hierarchy.
 * Similarly, when saving, the referenced child will not be saved as
 * part of the traversal. 
 * 
 * Therefore, it is not recommended to modify the referenced child in any
 * way through this referenced link.
 * 
 * @author Brutalbarbarian
 *
 */
public class BOLink<T extends BusinessObject> extends BusinessObject{
	
	public BOLink(BusinessObject owner, String name) {
		super(owner, name);
	}
	
	public T getReferencedObject() {
		if (Owner().getValue() == null) {
			return null;
		} else {
			return Owner().getValue().getLinkedChild(this);
		}
	}

	@Override
	protected void doSave() {
		// Do nothing
	}

	@Override
	protected void doDelete() {
		// Do nothing
	}

	@Override
	protected boolean populateAttributes() {
		// Nothing to populate
		return true;
	}

	@Override
	protected void createAttributes() {
		// Nothing to create
	}

	@Override
	public void clearAttributes() {
		// Nothing to clear
		
	}
	
	public String toString(int spaces) {
		T child = getReferencedObject();
		if (child == null) {
			return super.toString(spaces);
		}
		return child.toString(spaces);
	}

	@Override
	public void handleModified(ModifiedEvent source) {
		// No direct children... this will never be called
	}

}
