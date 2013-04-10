package com.lwan.bo;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.lwan.util.GenericsUtil;
import com.lwan.util.StringUtil;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;

public abstract class BOSet<T extends BusinessObject> extends BusinessObject implements Iterable<T>{
	public static final int LOADMODE_ACTIVE = 0;	// Implemented from BOSet
	public static final int LOADMODE_PASSIVE = 1;	// Implemented from BOSet
	// Will need to be implemented inside implementation of this in populate attribute
	public static final int LOADMODE_CACHE = 2;		
	
	Property<Integer> loadModeProperty;
	
	// name of the BOattribute which is a direct descendant of the child BOobject
	// if this is empty, calling 'findByID' will always return with null
	private Property<String> childIdNameProperty;
	
	
	/**
	 * <p>
	 * Property related to how this set is used.
	 * Can be set to one of three values.
	 * </p>
	 * <h1>LOADMODE_ACTIVE</h1>
	 * <p>Upon populate attributes, will fetch a list of all children, then for each
	 * child, will ensure an instance of that child exists, and will then set each of
	 * those children active, thus populating them</p>
	 * <h1>LOADMODE_PASSIVE</h1>
	 * <p>Upon populate attributes, will fetch a list of all children, then for each
	 * child, will ensure an instance of that child exists. The child will be considered active,
	 * but won't actually be populated until a call is made which would result in that child
	 * being returned.</p>
	 * <h1>LOADMODE_CACHE</h1>
	 * <p>Populate Attributes will do nothing. Rather, when a child is requested (by id),
	 * the set will attempt to find that child. If that child exists, will proceed to
	 * load and populate that child prior to returning it, or otherwise will return null.</p>
	 * <p>
	 * By default, all sets should have this set to LOADMODE_ACTIVE. It is recommended to 
	 * change this depending on the requirements of the set, in order to minimize work
	 * required upon populating.</p>
	 * TODO change default to LOADMODE_PASSIVE maybe?  
	 * 
	 * @return
	 */
	public Property<Integer> loadModeProperty() {
		if (loadModeProperty == null) {
			loadModeProperty = new SimpleObjectProperty<Integer>(this, "LoadMode", LOADMODE_ACTIVE);
		}
		return loadModeProperty;
	}
	/* public property accessors */
	public ReadOnlyProperty<String> childIDNameProperty() {
		return _childIdNameProperty();
	}
	
	/* private property accessors */
	private Property<String> _childIdNameProperty() {
		if (childIdNameProperty == null) {
			childIdNameProperty = new SimpleObjectProperty<String>(this, "ChildIDName");
		}
		return childIdNameProperty;
	}
	
	// private fields
	protected List<Entry> children;
	
	protected class Entry {
		protected T child;
		protected boolean loaded;
		
		protected Entry (T child) {
			this.child = child;
			loaded = false;
		}
	}
	
	/**
	 * Construct a set of BOObjects.
	 * 
	 * @param owner
	 * @param name
	 * @param childIdName
	 */
	public BOSet(BusinessObject owner, String name, String childIdName) {
		super(owner, name);
		
		children = new Vector<Entry>();
		_childIdNameProperty().setValue(childIdName);
	}

	/**
	 * Reinitialises this set as the type specified.
	 * 
	 * @param loadMode
	 */
	public void initialiseAs(int loadMode) {
		allowNotificationsProperty().setValue(false);
		try {
			children.clear();
			setActive(false);
			loadModeProperty().setValue(loadMode);
			ensureActive();
		} finally {
			allowNotificationsProperty().setValue(true);
		}
	}
	
	protected void removeChild(BusinessObject child) {
		super.removeChild(child);
		
		// the child might not be a child of this object, but rather, a child of this set
		if (children != null) {
			Iterator<Entry> it = children.iterator();
			while (it.hasNext()) {
				if (it.next().child == child) {
					it.remove();
					break;
				}
			}
		}
	}
	
	public void free() {			
		// free any children who's parents = this
		Vector<T> toFree = new Vector<>();
		for (Entry e : children) {
			if (e.child.getOwner() == this) {
				toFree.add(e.child);
			}
		}
		for (T child : toFree) {
			child.free();
		}
			
		children.clear();
		
		super.free();
	}
	
	
	
	/**
	 * Find a child of this set, where the child contains an attribute with the specified name,
	 * and the attribute has the same value as the passed in value.
	 * Will return the 'childNum' child found which satisfies the above.
	 * i.e. childNum = 1 will return the first child which satisfies the above.
	 * Will return null if cannot find the above properties.
	 * Will throw IllegalArgumentException if the child of this set doesn't contain the specified
	 * attribute.
	 * 
	 * @param attrName
	 * @param value
	 * @return
	 */
	public T findChildByAttribute(String attrName, Object value, int childNum, boolean forceString) {
		int found = 0;
		for (T child : childIterable()) {
			Object attr = child.findChildByName(attrName);
			if (attr == null) {
				throw new IllegalArgumentException("Cannot find child by name '" + attrName + "'");
			} else if (!(attr instanceof BOAttribute)) {
				throw new IllegalArgumentException(attr.getClass().getName() + " is not a BOAttribute");
			} else {
				BOAttribute<?> attribute = (BOAttribute<?>)attr;
				if ((forceString && GenericsUtil.Equals(value, attribute.asString())) ||  
						(!forceString && GenericsUtil.Equals(value, attribute.getValue()))) {
					found++;
					if (found == childNum) {
						return child;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Clears all items from this set. 
	 * Note this is unsafe, as the set will likely be 
	 * no longer synchronized with the datastructure.
	 * Recommended to call clear() instead.
	 */
	public void clearChildren() {
		children.clear();
	}
	
	public BusinessObject findChildByPath(String path) {
		BusinessObject result = super.findChildByPath(path);
		if (result == null && path != null) {
			int pos = path.indexOf('/');
			String child, remainder;
			if (pos == -1) {
				child = path;
				remainder = "";
			} else {
				child = path.substring(0, pos);
				remainder = path.substring(pos + 1);
			}
			BusinessObject next = null;
			int startIndex = child.indexOf('[');
			int endIndex = child.indexOf(']');
			if (startIndex >= 0 && endIndex > startIndex) {
				int activeChild = Integer.parseInt(child.substring(startIndex + 1, endIndex));
				next = getActive(activeChild);
			} else {
				startIndex = child.indexOf('{');
				endIndex = child.indexOf('}');
				pos = child.indexOf(':');
				if (startIndex >= 0 && pos > startIndex && endIndex > pos) {
					String id = child.substring(startIndex + 1, pos);
					String value = child.substring(pos + 1, endIndex);
					if (id.equalsIgnoreCase("id")) {
						next = findChildByID(Integer.parseInt(value));
					} else if (id.indexOf('@') == 0) {
						next = findChildByAttributeString(id.substring(1), value);
					}
				}
			}
			if (next != null) {
				return next.findChildByPath(remainder);
			}
		}
		
		return result;
	}
	
	public boolean isSet() {
		return true;
	}
	
	/**
	 * Find the first child, where that child's attribute by name 'attrName'
	 * is equal to the value passed in.
	 * Same as calling findChildByAttribute(attrName, value, 1)
	 * 
	 * @param attrName
	 * @param value
	 * @return
	 */
	public T findChildByAttribute(String attrName, Object value) {
		return findChildByAttribute(attrName, value, 1, false);
	}
	
	public T findChildByAttributeString(String attrName, String str) {
		return findChildByAttribute(attrName, str, 1, true);
	}
	
	public T findChildByID(Object id) {
		return findChildByID(id, true, false);
	}
	
	protected T findChildByID (Object id, boolean ensureActive, boolean datasetOnly) {
		String childAttr = childIDNameProperty().getValue();
		if (!StringUtil.isNullOrBlank(childAttr)) {
			for (Entry e : children) {
				T child = e.child;
				BusinessObject attr = child.findChildByName(childAttr);
				// If the attribute can be found, check the values are equal
				if (attr != null && attr.isAttribute() && 
						GenericsUtil.Equals(((BOAttribute<?>)attr).getValue(), id)) {
					// If they are equal... just return it. Assume there's only
					// one child with the same 'id'
					
					// ensure the child is loaded if it isn't populating.
					if (!e.loaded && ensureActive) {	
						child.ensureActive();
						e.loaded = true;
					}
					return child;
				}
			}
			// Dosen't exist in current set... but in cache mode so might still exist in
			// the actual dataset...
			if (!datasetOnly && loadModeProperty().getValue() == LOADMODE_CACHE && childExists(id)) {
				return populateChild(id, ensureActive);
			}
		}
		return null;
	}
	
	/**
	 * Same as calling ensureChildActive(null)
	 * 
	 * @return
	 */
	public T createNewChild() {
		return ensureChildActive(null);
	}
	
	public int getActiveCount() {
		int count = 0;
		for (Entry e : children) {
			// Assume not loaded means its technically active
			if (!e.loaded || e.child.activeProperty().getValue()) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Reloads the set
	 */
	public void reload() {
		setActive(false);
		ensureActive();
	}
	
	protected boolean verifyState() throws BOException {
		super.verifyState();
		// no need to verify the state of unloaded children
		for(Entry e : children) {
			if (e.loaded) {
				e.child.verifyState();
			}
		}
		return true;
	}
	
	public T getActive(int index) {
		int active = -1;
		for (T child : childIterable()) {
			if (child.activeProperty().getValue()) {
				active++;
			}
			if (active == index) {
				return child;
			}
		}
		return null;	// likely index out of bounds of reached here
	}
	
	protected int getCount() {
		return children.size();
	}
	
	protected T get(int index) {
		if (index < 0 || index >= children.size()) {
			return null;
		} else {
			Entry e = children.get(index);
			if (!e.loaded) {
				// Activate the child
				e.child.ensureActive();
				e.loaded = true;
			}
			return e.child;
		}
	}
	
	protected Iterable<T> childIterable() {
		return new Iterable<T>(){
			public Iterator<T> iterator() {
				return new Iterator<T>(){
					int i = 0;
					@Override
					public boolean hasNext() {
						return i < getCount();
					}
					@Override
					public T next() {
						return get(i++);
					}
					@Override
					public void remove() {
						// not supported
						throw new UnsupportedOperationException("Remove not supported");
					}					
				};
			}
			
		};
	}
	
	public boolean equivalentTo (BusinessObject other, 
			Callback<BusinessObject, Boolean> ignoreFields) {
		boolean result = super.equivalentTo(other, ignoreFields);
		if (result && other != this) {
			BOSet<?> otherSet = (BOSet<?>)other;
			if (getActiveCount() != otherSet.getActiveCount()) return false;
			for (T child : this) {
				if (ignoreFields != null && ignoreFields.call(child)) continue;	// seems a little silly...
				BOAttribute<?> attr = (BOAttribute<?>)child.findChildByName(childIDNameProperty().getName());
				if (attr == null) return false;	// wtf?
				BusinessObject otherChild = otherSet.findChildByID(attr.getValue());
				if (otherChild == null || !child.equivalentTo(otherChild, ignoreFields)) return false;
			}
		}
		return result;
	}
	
	/**
	 * Ensures a child object with the value within it's attribute by name declared in ChildIDName
	 * is equal to 'id' exists and is set to active.
	 * 
	 * Calling this with null as parameter is effectively the same as creating a new child
	 * instance (thats not from the dataset)
	 * 
	 * @param id
	 */
	public T ensureChildActive(Object id) {
		T child = populateChild(id, true);
		return child;
	}
	
	protected T populateChild(Object id) {
		return populateChild(id, false);
	}
	
	/**
	 * This should only be called from populateAttributes and
	 * nowhere else.
	 * Creates an inactive child with the id value set to the passed
	 * in id, but only if there isn't already a child with that same id.
	 * 
	 * @param id
	 */
	protected T populateChild(Object id, boolean ensureActive) {
		T child;
		if (id == null || id.equals(0)) {
			child = null;
		} else {
			child = findChildByID(id, ensureActive, true);
		}
		if (child == null) {
			child = createChildInstance(id);
			BusinessObject idAttr = child.findChildByName(childIDNameProperty().getValue());
			if (idAttr != null) {
				((BOAttribute<?>)idAttr).setAsObject(id);
			} else {
				throw new RuntimeException("Cannot find child id property by name '" + childIDNameProperty().getValue() + "'");
			}
			Entry e = new Entry(child);
			children.add(e);
			
			if (ensureActive) {
				child.ensureActive();
				e.loaded = true;
			}
			
			// Let the child fire its own event...
//			fireModified(new ModifiedEvent(child, ModifiedEvent.TYPE_SET));
		}
		return child;
	}

	@Override
	/**
	 * Iterate over active objects
	 * 
	 */
	public Iterator<T> iterator() {
		return new ActiveIterator();
	}
	
	private class ActiveIterator implements Iterator<T>{
		int activePassed;
		int activeCount;
		Iterator<T> iterator;
		
		private ActiveIterator() {
			activePassed = 0;
			activeCount = getActiveCount();
			iterator = childIterable().iterator();
		}
		
		public boolean hasNext() {
			return activePassed < activeCount;
		}

		@Override
		public T next() {
			while(iterator.hasNext()) {
				T object = iterator.next();
				if (object.activeProperty().getValue()) {
					activePassed++;
					return object;
				}
			}
			// Shouldn't ever reach here unless next is called after hasNext returned false.
			return null;
		}

		@Override
		public void remove() {
			iterator.remove();
		}
	}
		
	private T exampleChild;
	public T getExampleChild(){
		if (exampleChild == null) {
			exampleChild = createChildInstance(null);
			exampleChild.initialiseAsExample();
		}
		return exampleChild;
	}
	
	/**
	 * Will pass the active handling down to all the children of this set as well.
	 * 
	 * @param isActive
	 */
	@Override
	protected void handleActive(boolean isActive) {
		// delete all inactive children if isActive is true
		// not sure if this is the best practice... but don't really see much better way of handling this
		if (isActive && children != null) {
			Iterator<Entry> it = children.iterator();//childIterable().iterator();
			Vector<T> removed = new Vector<>();
			while (it.hasNext()) {
				Entry e = it.next();
				// If child isn't loaded, we can assume its supposed to be active
				if (e.loaded && !e.child.isActive()) {
					removed.add(e.child);
					it.remove();
				}
			}
			// free the removed children
			for (T child : removed) {
				child.free();
			}
		}
		
		super.handleActive(isActive);
	}

	protected void setActiveChildren(boolean isActive) {
		super.setActiveChildren(isActive);
		
		// Set the active state of all children to match this
		if (children != null) {
			for (Entry e : children) {
				// Only actively load upon setActive() if loadmode is active
				if (isActive) {
					if (loadModeProperty().getValue() == LOADMODE_ACTIVE) {
						e.child.ensureActive();
						e.loaded = true;
					}
				} else {
					if (!e.loaded) {
						// Need to fully load that child prior
						// to setting it to inactive
						// This way any child of that child will
						// also be set to inactive
						e.child.ensureActive();
						e.loaded = true;
					}
					e.child.activeProperty().setValue(false);
				}
			}
		}
	}
	
	protected String toString(int spaces, int expansion) {
		StringBuilder sb = new StringBuilder();
		// first line: Name:ClassName
		sb.append(super.toString(spaces, expansion));
		
//		sb.append(StringUtil.getRepeatedString(" ", spaces) + "children\n");
		
		// call toString on all children with spaces += 4
		if (expansion != 0) {
			for (BusinessObject child : childIterable()) {
				sb.append(child.toString(spaces + 2, expansion - 1));
			}
		}
		return sb.toString();
	}
	
	/**
	 * If will normally save, then this will also call save() on all
	 * the children as well.
	 * 
	 * This will also remove all inactive children after saving.
	 * 
	 */
	public boolean save() {
		if (super.save()) {
			for (Entry e : children) {
				if (e.loaded) {
					e.child.save();
				}
			}
			// remove all inactive children here.
			Iterator<Entry> it = children.iterator();
			while (it.hasNext()) {
				Entry e = it.next();
				if (e.loaded && !e.child.isActive()) {
					it.remove();
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public void clear() {
		super.clear();
		// This will effectively remove all children.
		if (children != null) {
			for (Entry e : children) {
				if (!e.loaded) {
					e.child.ensureActive();
				}
				e.child.activeProperty().setValue(false);
			}
		}
	}
	
	/**
	 * Force clear this set... this may break synchronization as it
	 * clears the children instead of marking them inactive. Calling save at 
	 * a later stage will not delete from the underlying dataset the children
	 * that was cleared using this method.
	 * 
	 */
	public void forceClear() {
		if (children != null) {
			children.clear();
		}
	}
	
	/**
	 * Called under cache mode when findChildByID() is called.
	 * 
	 * @param id
	 * @return
	 */
	protected abstract boolean childExists(Object id);
	
	/**
	 * Must be implemented to create a new instance of a child. Will be called upon populateChild();
	 * 
	 * @return
	 */
	protected abstract T createChildInstance(Object id);
	
	@Override
	/**
	 * Do nothing by default. Sets usually should only be a container for a one to many relationship
	 */
	protected void doSave() {}

	@Override
	/**
	 * Do nothing by default. Sets usually should only be a container for a one to many relationship
	 */ 
	protected void doDelete() {}

	@Override
	/**
	 * This method should be used to populate the child datasets.
	 * The timing of this will be such that the parent has already loaded,
	 * and before the loading of any children.
	 */
	protected abstract boolean populateAttributes();

	@Override
	/**
	 * Do nothing by default. Sets usually shouldn't have any attributes
	 */
	protected void createAttributes() {}

	@Override	
	/**
	 * Do nothing by default. Sets usually shouldn't have any attributes
	 */ 
	public void clearAttributes() {}

	@Override
	/**
	 * Do nothing by default. 
	 * 
	 */
	public void handleModified(ModifiedEvent source) {}
}
