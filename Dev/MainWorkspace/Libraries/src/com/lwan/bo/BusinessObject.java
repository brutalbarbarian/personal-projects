package com.lwan.bo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.lwan.util.ClassUtil;
import com.lwan.util.StringUtil;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

/**
 * <h1>****GUIDE TO BOObjects ****</h1>
 * 
 * <h3>DataStructure</h3>
 * <p>
 * BOObjects follow a clearly defined hierarchy. When any attribute is modified along the hierarchy, the owner of that attribute will be
 * notified of a change. This in turn notifies the owner of that owner until there is no more owners (i.e. hit the root of the hierarchy).
 * </p>
 * <ul>
 * <li>Each BOObject has an owner</li>
 * <li>Each BOObject can have children. These children can either be another BOObject, a BOAttribute, or a BOSet. Note that BOAttribute and
 * BOSet both extend from BOObject and thus shares similar behavior and properties to BOObject.</li>
 * <li>The hierarchy for any BOObject must be constructed within the method {@link #createAttributes()}.</li>
 * </ul>
 * 
 * <h3>Events</h3>
 * <p>When a modification occurs to any BOObject, a modification event will be fired. All BOObjects will automatically fire off a modification
 * event of its parent, which forms a recursive loop up the hierarchy. All BOObjects listen to any modification events it fires. AllowNotifications
 * can be set to false for a particular BOObject, in which case it will ignore the call to fire events. Thus it will not receive any events and won't
 * pass any events on up the hierarchy.
 * </p>
 * 
 * <h3>Attributes</h3>
 * <p>
 * Attributes are the leaves as part of the data structure. An attribute stores a simple value i.e. a String or an Integer etc. Each attribute
 * can have only one parent - BOObject it's bound to. Whenever the value of an attribute is modified, it will throw an event to its owner.
 * </p>
 * <ul>
 * <li>The field name for an attribute should always be named starting with a lower case letter and should always be private</li>
 * <li>The accessor method should always be read only, and be fully camel cased.</li>
 * </ul>
 * 
 * <h3>Property Declaration</h3>
 * <ul>
 * <li>Properties should always start with a lower case letter and be fully lower case. use underscore to separate words if needed.</li>
 * <li>Property should be accessed in 2 possible ways. A public accessor and a private accessor. The public accessor should always
 * have the public modifier, and fully camel cased.</li>
 * <li>A private accessor is only necessary if the public accessor is ReadOnly. This should always have the prefix of an underscore, and
 * otherwise follows the exact same name as the property field.</li></ul>
 * <p>
 * Properties should have nothing to do with the data structure, and thus will throw no events to the BOObjects directly. They do not
 * belong to the data hierarchy but rather are attributes associated with the object itself.
 * </p>
 * 
 * <h3>State</h3>
 * <p>State is a set of {@link com.lwan.bo.State}, where if the State is present within the set, then it can be considered to be 'true', and vice versa
 * if the State is not present. State will always be reset upon setting the object to active. Depending on how the dataset is then populated,
 * the state will immediately reflect the state of the object. Any further modifications will also affect the state of the dataset.
 * </p>
 *  
 * @author Brutalbarbarian
 *
 */
public abstract class BusinessObject implements ModifiedEventListener{
	/* Property Declarations */
	
	private Property<BusinessObject> owner;
	private Property<Boolean> active;
	private Property<String> name;
	private Property<String> tag;
	private Property<Boolean> allow_notifications;
	private Property<Boolean> independent;
	private Property<Set<State>> state;
	private Property<Boolean> is_populating;
	private Property<Boolean> is_handling_active;
	
	/* Property Accessor Methods */
	public ReadOnlyProperty<BusinessObject> Owner() {
		return _owner();
	}
	public Property<String> Name() {
		if(name == null) {
			name = new SimpleObjectProperty<>(this, "Name", "");
		}
		return name;
	}
	
	public ReadOnlyProperty<Set<State>> State(){
		return _state();
	}
	public ReadOnlyProperty<Boolean> IsPopulating() {
		return _is_populating();
	}
	
	public ReadOnlyProperty<Boolean> IsHandlingActive() {
		return _is_handling_active();
	}
	
	/** 
	 * Active represents if this objects is in use. 
	 */
	public Property<Boolean> Active() {
		if (active == null) {
			active = new SimpleObjectProperty<>(this, "Active", false);
		}
		return active;
	}
	
	/**
	 * Any miscellaneous tags attached to this object.
	 * This has no actual effect on the BusinessObject itself.
	 * 
	 * 
	 * @return
	 */
	public Property<String> Tag() {
		if (tag == null) {
			tag = new SimpleObjectProperty<>(this, "Tag", "");
		}
		return tag;
	}
	/**
	 * <p>
	 * Independent represents if an object is independent from its parent.
	 * An independent object will be saved prior to the saving of its parent
	 * when save() of the parent is called, where as dependent children will
	 * have its save() called after the parent has saved.
	 * </p>
	 * <p>
	 * When deleting an inactive object however, the order will be reversed. Dependent children
	 * will be deleted prior to deleting parent, while independent children
	 * will be deleted after the parent.
	 * </p>
	 * <p>
	 * This should never be set by an object, but rather by the parent of an object,
	 * as this property describes a child's relationship with its parent.
	 * </p>
	 * <p>
	 * Note: This property has no effect on attributes.
	 * </p>
	 * 
	 * By default this is set to false.
	 * 
	 * @return
	 */
	public Property<Boolean> Independent() {
		if (independent == null) {
			independent = new SimpleObjectProperty<Boolean>(this, "Independent", false);
		}
		return independent;
	}
	
	/**
	 * Check if this business object is a BOAttribute
	 * 
	 * @return
	 */
	public boolean isAttribute() {
		return false;
	}
	
	/**
	 * Check if this business object s a BOSet
	 * 
	 * @return
	 */
	public boolean isSet() {
		return false;
	}
	
	/** 
	 * Use this flag to disable notifications if needed.
	 * This will effectively disable fireModified for this object only. 
	 */
	public Property<Boolean> AllowNotifications() {
		if (allow_notifications == null) {
			allow_notifications = new SimpleObjectProperty<>(this, "AllowNotifications", true);
		}
		return allow_notifications;
	}
	
	/* Private Property Accessor Methods */
	private Property<BusinessObject> _owner() {
		if (owner == null) {
			owner = new SimpleObjectProperty<>(this, "Owner", null);
		}
		return owner;
	}
	
	private Property<Boolean> _is_populating() {
		if (is_populating == null) {
			is_populating = new SimpleObjectProperty<>(this, "IsPopulating", false);
		}
		return is_populating;
	}
	private Property<Set<State>> _state() {
		if (state == null) {
			state = new SimpleObjectProperty<>(this, "State", null);
			state.setValue(new HashSet<State>());
		}
		return state;
	}
	
	private Property<Boolean> _is_handling_active() {
		if (is_handling_active == null) {
			is_handling_active = new SimpleObjectProperty<>(this, "IsHandlingActive", false);
		}
		return is_handling_active;
	}

	/* Private Fields  */
	// Children of this object. This should never be exposed.
	private HashMap<String, BusinessObject> children;
	// List of listeners to any modification to this object.
	// Note that the owner of this object will not be in this list, and thus will always be notified.
	// i.e. cannot be removed.
	private Vector<ModifiedEventListener> listeners;
	
	
	public BusinessObject(BusinessObject owner, String name) {
		children = new HashMap<>();
		listeners = new Vector<>();
		Name().setValue(name);
		_owner().setValue(owner);
		AllowNotifications().setValue(true);
		Active().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> value,
					Boolean oldValue, Boolean newValue) {
				handleActive(newValue);
			}			
		});
		
		initialise();
	}
	
	protected void initialise() {
		createAttributes();
		// Ensures active state is false to begin with. This will also ensure all children are inactive as well.
		Active().setValue(false);
		clear();
	}
	
	/**
	 * Get a direct child of this object with name equal to the parameter string
	 * 
	 * @param name
	 * @return
	 */
	public BusinessObject findChildByName(String name) {
		BusinessObject child = children.get(name.toLowerCase());
		if (child == null) return null;
		return child.getReferencedObject();
	}
	
	public BOAttribute<?> findAttributeByName(String name) {
		BusinessObject attr = findChildByName(name);
		if (attr != null && attr.isAttribute()) {
			return (BOAttribute<?>)attr;
		} else {
			return null;	// Just return null...
		}
	}
	
	public BOAttribute<?> findAttributeByPath(String path) {
		BusinessObject attr = findChildByPath(path);
		if (attr != null && attr.isAttribute()) {
			return (BOAttribute<?>)attr;
		} else {
			return null;	// Just return null...
		}
	}
	
	/**
	 * Returns the object referenced by this Business object.
	 * Normally this will return this object, except with BOLinks,
	 * where the referenced object is weakly linked via getReferenceObject. 
	 * 
	 * @return
	 */
	protected BusinessObject getReferencedObject () {
		return this;
	}
	
	public BusinessObject getOwner() {
		return Owner().getValue();
	}

	public String getName() {
		return Name().getValue();
	}
	
	/**
	 * Parse the relative path to some other business object.
	 * The path uses the standard linux path syntax.
	 * 
	 * Each segment is separated by a '/'
	 * Each segment is either the name of the child, or
	 * alternatively '...' representing the owner.
	 * Sets can be interacted wtih via keyword '[##]' for activeChildren, or
	 * {id:###} for id, or {@attriName:###}.
	 * Example. Employees/[2]/.Name for 2nd active employee's name
	 * 
	 * 
	 * 
	 * This function will return null if the child requested cannot
	 * be found.
	 * 
	 * @param path
	 * @return
	 */
	public BusinessObject findChildByPath(String path) {
		if (path == null) {
			return null;
		} else if (path.length() == 0) {	// Blank... assume this is the referenced object
			return this;
		} else {
			// find the first path segment
			int pos = path.indexOf('/');
			String name, remainder;
			if (pos == -1) {
				name = path; 
				remainder = "";
			} else {
				name = path.substring(0, pos);
				remainder = path.substring(pos + 1);	
			} 
			BusinessObject next;
			if (name.equals("...")) {	// owner
				next = getOwner();
			} else {
				next = findChildByName(name);
			}
			if (next == null) return null;
			return next.findChildByPath(remainder);
		}
	}
	
	/**
	 * Effectively the same as called equivilentTo(other, null).
	 */
	public boolean equals(Object other) {
		if (other instanceof BusinessObject) {
			return equivalentTo((BusinessObject)other, null);
		} else {
			return false;
		}
	}
	
	/**
	 * Will be called by BOLinks when the links attempt to get the linked
	 * item. This should be controlled by the parent.
	 * 
	 * @param link
	 * @return
	 */
	protected <T extends BusinessObject> T getLinkedChild(BOLink<T> link) {
		// By default just return null... 
		return null;
	}
	
	/**
	 * Will call equivilentTo on all direct children of this object. This will effectively
	 * cascade down the tree, checking every object. Objects should override this if the
	 * object does any special checks.
	 * 
	 * IgnoreFields will be called on every object in the hierarchy. If ignoredFields is null
	 * or returns true, then equivalentTo will not be called on that object.
	 * 
	 * @param other
	 * @param ignoreFields
	 * @return
	 */
	public boolean equivalentTo (BusinessObject other, Callback<BusinessObject, Boolean> ignoreFields) {
		if (getClass() != other.getClass() || children.size() != other.children.size()) {
			return false;
		} 
		for (BusinessObject child : children.values()) {
			// Not going to bother comparing if callback returns true
			if (ignoreFields != null && ignoreFields.call(child)) continue;	
			String name = child.Name().getValue();
			BusinessObject otherChild = other.findChildByName(name);
			if(otherChild == null || !child.equivalentTo(otherChild, ignoreFields)) return false;
		}
		
		return true;
	}
	
	/**
	 * Get the first owner traveling up the hierarchy with the same class as the passed in class
	 * 
	 * @param parentClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends BusinessObject> T findOwnerByClass(Class<T> parentClass) {
		if (ClassUtil.isSuperclassOf(parentClass, getClass())) {
			return (T)this;
		} else if (Owner().getValue() != null) {
			return Owner().getValue().findOwnerByClass(parentClass);
		} else {
			return null;
		}
	}
	
	/**
	 * Handle active by default will do nothing if passed in false for isActive.
	 * Otherwise will proceed to call populateAttributes.
	 * This can be overridden to provide any extra functionality upon changing the active state
	 * of an object.
	 * 
	 * @param isActive
	 */
	protected void handleActive(Boolean isActive) {
		_is_handling_active().setValue(true);
		try {
			if (isActive) {
				// Any modifications occurring while populating from dataset should be ignored
				AllowNotifications().setValue(false);
				_is_populating().setValue(true);
				try {
					State().getValue().clear();	// Reset all states upon setting active.
					if (populateAttributes()) {
						State().getValue().add(State.Dataset);
					} else {
						// If its new, then set attributes to defaults and modified is automatically triggered
						clearAttributes();
						State().getValue().add(State.Modified);
					}
				} finally {
					_is_populating().setValue(false);
					AllowNotifications().setValue(true);
				}
	
			}
			
			// Ensure all children are of the same active state
			setActiveChildren(isActive);
			
			// If the owner is currently handling its active state, let the parent throw the event
			if (getOwner() == null || !getOwner().IsHandlingActive().getValue()) {
				fireModified(new ModifiedEvent(this, ModifiedEvent.TYPE_ACTIVE));
			}
		} finally {
			_is_handling_active().setValue(false);	
		}
	}
	
	protected void setActiveChildren(boolean isActive) {
		// Set the active state of all children to match this
		for (BusinessObject child : children.values()) {
			// Active state is meaningless to BOAttributes
			if (!(child.isAttribute())) {
				child.Active().setValue(isActive);
			}
		}
	}
	
	/**
	 * Safer to call this to ensure a BOObject is activated.
	 * This will activate the object if and only if the object isn't already activated.
	 * 
	 */
	public void ensureActive() {
		if (!Active().getValue()) {
			Active().setValue(true);
		}
	}
	

	/**
	 * Fire an event to this object, which will in turn pass the event up the hierarchy,
	 * as well as notifying any listeners listening to this object.
	 * 
	 * @param source
	 */
	public final void fireModified(ModifiedEvent event) {
		if (AllowNotifications().getValue()) { 
			State().getValue().add(State.Modified);
			
			// Don't handle event if this was the object which fired it last
			if (event.getDirectChild() != this) {
				handleModified(event);
			}
			
			BusinessObject owner = Owner().getValue();
			ModifiedEvent modEvent = new ModifiedEvent(event, this);
			if (owner != null) {
				Owner().getValue().fireModified(modEvent);
			}
			for(ModifiedEventListener listener : listeners) {
				listener.handleModified(modEvent);
			}
		}
	}
	
	/**
	 * This is different from clearAttributes from that this should clear away all values as opposed to
	 * setting them to default values.</br>
	 * </br>
	 * This will automatically be called on all descendents of this object.
	 */
	public void clear() {
		// If settingActive is true, clear will be called for all children anyway. No need to loop.
		for (BusinessObject child : children.values()) {
			child.clear();
		}
	}
	
	/**
	 * Add a BOObject as a child of this object.
	 * This should only ever be called in createAttributes 
	 * 
	 * @param object
	 */
	protected <T extends BusinessObject> T addAsChild(T object) {
		children.put(object.Name().getValue().toLowerCase(), object);		
		return object;
	}
	
	public boolean isChildByName(BusinessObject bo, String name) {
		if (bo == null) return false;
		return findChildByName(name) == bo;
	}
	
	/**
	 * Add a listener which listens to any changes to this object or below in the hierarchy
	 * 
	 * @param listener
	 */
	public void addListener(ModifiedEventListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes a modification listener.
	 * 
	 * @param listener
	 */
	public void removeListener(ModifiedEventListener listener) {
		listeners.remove(listener);
	}
	
	public void setNotifications(boolean allow) {
		AllowNotifications().setValue(allow);
	}
	
	/**
	 * Call this to save this business object, which in turn will 
	 * also recursively save all its children.
	 * 
	 * This object will attempt to verify prior to saving. This includes
	 * making sure that no 'non-null' attributes contain no value.
	 * 
	 * Will throw an exception if any issues occur.
	 */
	public void trySave() throws BOException {
		if (verifyState()) {
			save();
		}
	}
	
	/**
	 * Return null or empty string if no issues were found.
	 * Override this function in order to implement any verification
	 * of state prior to saving.
	 * 
	 * The returned string is the message that'll be thrown.
	 * 
	 * @return
	 */
	protected String doVerifyState() {
		return null;
	}
	
	/**
	 * Recursive call for verifying that all attributes desecending from this object,
	 * and all children from this object are valid for saving.
	 * 
	 * This should always return true, as it will throw a BOException if
	 * any issues occur.
	 * 
	 * Note this recurses in the same order as save()
	 * 
	 * @return
	 */
	protected boolean verifyState() throws BOException {
		// No need to validate inactive objects
		if (isActive()) {
			for (BusinessObject child : children.values()) {
				if (child.Independent().getValue()) {
					child.verifyState();
				}
			}
			String err = doVerifyState();
			if (!StringUtil.isNullOrBlank(err)) {
				throw new BOException("Failed to verfy child with message '" + err + "'", this);
			}
			for(BusinessObject child : children.values()) {
				if (!child.Independent().getValue()) {
					child.verifyState();
				}
			}
		}
		return true;
	}
	
	/**
	 * Recursive call for saving business objects.
	 * This will only do anything if this object is modified. The returned value will signify if anything was actually saved.
	 * 
	 * The actual action upon calling this depends on if this object is active or not. Will call doSave if
	 * the object is active, alternatively will call doDelete if the object is not active.
	 * 
	 * Will then call save() on all non-attribute children.
	 */
	protected boolean save() {
		// if modified... then do save
		if (State().getValue().contains(State.Modified) || 
				// or if loaded from a dataset but was set inactive afterwards
				(State().getValue().contains(State.Dataset) && !Active().getValue())) {
			// Save all the independent children first
			for (BusinessObject child : children.values()) {
				// Attributes should be managed by the owner, and shouldn't need to manage themselves
				if(!(child.isAttribute()) &&
						// either independent and active, or dependent and inactive
						child.Independent().getValue() == isActive()) {
					child.save();
				}
			}
			
			// We want to call doSave if the value is active, otherwise doDelete should be called
			if (Active().getValue()) {
				doSave();
			} else {
				doDelete();
			}
			
			// Save all the dependent children after this object has been saved
			for (BusinessObject child: children.values()) {
				// Attributes should be managed by the owner, and shouldn't need to manage themselves
				if (!(child.isAttribute()) &&
						// either independent and inactive, or dependent and active
						child.Independent().getValue() != isActive()) {
					child.save();
				}
			}			
			// Should be safe to assume the following state after successful saving
			State().getValue().remove(State.Modified);
			if (isActive()) {
				State().getValue().add(State.Dataset);
			} else {
				State().getValue().remove(State.Dataset);
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Get the tree representing the business object data structure tree with
	 * the current object as the root of the tree.
	 * 
	 */
	public String toString() {
		return toString(0, 1);
	}
	
	/**
	 * Shortcut for calling Active().getValue()
	 * 
	 * @return
	 */
	public boolean isActive() {
		return Active().getValue();
	}
	
	/**
	 * Shortcut for calling Active().setValue()
	 * 
	 * @param active
	 */
	public void setActive(boolean active) {
		Active().setValue(active);
	}
	
	/**
	 * Shortcut for calling Independent().getValue()
	 * 
	 * @return
	 */
	public boolean isIndependent() {
		return Independent().getValue();
	}
	
	/**
	 * Shortcut for calling Independent().setValue()
	 * 
	 * @param independent
	 */
	public void setIndependent(boolean independent) {
		Independent().setValue(independent);
	}
	
	/**
	 * Get the string representation of the entire business object tree.
	 * Note this will cause an infinite loop if there are any loops in the tree,
	 * which may be caused by BOLinks. This shouldn't usually occur unless the
	 * tree is poorly designed. 
	 * 
	 * @return
	 */
	public String toStringAll() {
		return toStringEx(-1);
	}
	
	/**
	 * Get the string representation of the business object tree up to
	 * the passed in depth.
	 * Pass in -1 is equivalent to calling toStringAll.
	 * 
	 * @param depth
	 * @return
	 */
	public String toStringEx(int depth) {
		return toString(0, depth);
	}
	
	/**
	 * Recursive function for traversing the tree, building the tree in
	 * String form.
	 * 
	 * @param spaces
	 * @param expansion TODO
	 * @return
	 */
	protected String toString(int spaces, int expansion) {
		StringBuilder sb = new StringBuilder();
		// first line: Name:ClassName
		sb.append(StringUtil.getRepeatedString(" ", spaces)).append(Name().getValue()).append(':').append(getClass().getSimpleName()).
				append(' ').append(getPropertyStrings()).append('\n');
		
		// call toString on all children with spaces += 4
		if (expansion != 0) {
			// Print attributes first
			for (BusinessObject child : children.values()) {
				if (child.isAttribute()) {
					sb.append(child.toString(spaces + 4, expansion - 1));
				}
			}
			// Print everything else
			for (BusinessObject child : children.values()) {
				if (!child.isAttribute()) {
					sb.append(child.toString(spaces + 4, expansion - 1));
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Used by toString(int) to append any useful information about
	 * the non-direct BO-children of this object such as properties.
	 * 
	 * @return
	 */
	protected String getPropertyStrings() {
		return "Active:" + Active().getValue();
	}
	
	/**
	 * Override this in order to save this object 
	 * This will be called by save() if the object is active and modified.
	 * The implementation of this may vary depending on if State.Dataset is present in State(),
	 * depending on the type of data.
	 */
	protected abstract void doSave();
	
	/**
	 * Override this in order to delete this object
	 *  This will be called by save() if the object is inactive
	 */
	protected abstract void doDelete();
	
	/**
	 * This is used to populate all child attributes and objects.
	 * It is up to the implementation of the object to decide to populate with default values or to load values from some source.
	 * If this function returns false, clearAttributes() will be called on this object. 
	 * @return 
	 * Whether or not this object is populated from a dataset. This is used to determine the state.
	 */
	protected abstract boolean populateAttributes();
	
	/**
	 * Create all child attribute objects
	 */
	protected abstract void createAttributes();
	
	/**
	 * Clear all child attribute objects to a neutral state.</br>
	 * This will not be automatically called on any children(unlike clear()), thus
	 * the parent must be responsible for all values being set.</br>
	 * This should be implemented on the assumption that we want the object to reset
	 * to its neutral state, without losing its identity or losing any connections
	 * to its parent.
	 */
	public abstract void clearAttributes();
	
	/**
	 * Event thrown when something down the hierarchy is modified.
	 * 
	 * @param source
	 */
	public abstract void handleModified(ModifiedEvent source);
}
