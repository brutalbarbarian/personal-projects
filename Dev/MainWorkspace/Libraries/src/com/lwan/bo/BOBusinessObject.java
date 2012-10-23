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
public abstract class BOBusinessObject implements ModifiedEventListener{
	/* Property Declarations */
	
	private Property<BOBusinessObject> owner;
	private Property<Boolean> active;
	private Property<String> name;
	private Property<String> tag;
	private Property<Boolean> allow_notifications;
	private Property<Boolean> independent;
	private Property<Set<State>> state;
	
	/* Property Accessor Methods */
	public ReadOnlyProperty<BOBusinessObject> Owner() {
		return _owner();
	}
	public ReadOnlyProperty<String> Name() {
		return _name();
	}
	public ReadOnlyProperty<Set<State>> State(){
		return _state();
	}
	/** 
	 * Active represents if this objects is in use. 
	 */
	public Property<Boolean> Active() {
		if (active == null) {
			active = new SimpleObjectProperty<>(this, "Active");
		}
		return active;
	}
	public Property<String> Tag() {
		if (tag == null) {
			tag = new SimpleObjectProperty<>(this, "Tag");
		}
		return tag;
	}
	/**
	 * Independent represents if an object is independent from its parent.
	 * An independent object will be saved prior to the saving of its parent
	 * when save() of the parent is called.
	 * 
	 * @return
	 */
	public Property<Boolean> Independent() {
		if (independent == null) {
			independent = new SimpleObjectProperty<Boolean>(this, "Independent");
		}
		return independent;
	}
	
	/** 
	 * Use this flag to disable notifications if needed.
	 * This will effectively disable fireModified for this object only. 
	 */
	public Property<Boolean> AllowNotifications() {
		if (allow_notifications == null) {
			allow_notifications = new SimpleObjectProperty<>(this, "AllowNotifications");
		}
		return allow_notifications;
	}
	
	/* Private Property Accessor Methods */
	private Property<BOBusinessObject> _owner() {
		if (owner == null) {
			owner = new SimpleObjectProperty<>(this, "Owner");
		}
		return owner;
	}
	private Property<String> _name() {
		if(name == null) {
			name = new SimpleObjectProperty<>(this, "Name");
		}
		return name;
	}	
	private Property<Set<State>> _state() {
		if (state == null) {
			state = new SimpleObjectProperty<>(this, "State");
			state.setValue(new HashSet<State>());
		}
		return state;
	}

	/* Private Fields  */
	// Children of this object. This should never be exposed.
	private HashMap<String, BOBusinessObject> children;
	// List of listeners to any modification to this object.
	// Note that the owner of this object will not be in this list, and thus will always be notified.
	// i.e. cannot be removed.
	private Vector<ModifiedEventListener> listeners;
	
	
	public BOBusinessObject(BOBusinessObject owner, String name) {
		children = new HashMap<>();
		listeners = new Vector<>();
		_name().setValue(name);
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
	
	private final void initialise() {
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
	public BOBusinessObject getChildByName(String name) {
		return children.get(name);
	}
	
	/**
	 * Get the first owner traveling up the hierarchy with the same class as the passed in class
	 * 
	 * @param parentClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends BOBusinessObject> T getOwnerByClass(Class<T> parentClass) {
		if (ClassUtil.isSuperclassOf(parentClass, getClass())) {
			return (T)this;
		} else if (Owner().getValue() != null) {
			return Owner().getValue().getOwnerByClass(parentClass);
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
		if (isActive) {
			// Any modifications occurring while populating from dataset should be ignored
			AllowNotifications().setValue(false);
			State().getValue().clear();	// Reset all states upon setting active.
			if (populateAttributes()) {
				State().getValue().add(State.Dataset);
			} else {
				// If its new, then set attributes to defaults and modified is automatically triggered
				clearAttributes();
				State().getValue().add(State.Modified);
			}
			AllowNotifications().setValue(true);
		}
		// Set the active state of all children to match this
		for (BOBusinessObject child : children.values()) {
			// Active state is meaningless to BOAttributes
			if (!(child instanceof BOAttribute)) {
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
			handleModified(event);
			
			BOBusinessObject owner = Owner().getValue();
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
	 */
	public void clear() {
		// If settingActive is true, clear will be called for all children anyway. No need to loop.
		for (BOBusinessObject child : children.values()) {
			child.clear();
		}
	}
	
	/**
	 * Add a BOObject as a child of this object.
	 * This should only ever be called in createAttributes 
	 * 
	 * @param object
	 */
	protected final <T extends BOBusinessObject> T addAsChild(T object) {
		children.put(object.Name().getValue(), object);
		return object;
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
	
	/**
	 * Call to save this BOObject.
	 * This will only do anything if this object is modified. The returned value will signify if anything was actually saved.
	 * 
	 * The actual action upon calling this depends on if this object is active or not. Will call doSave if
	 * the object is active, alternatively will call doDelete if the object is not active.
	 * 
	 * Will then call save() on all non-attribute children.
	 */
	public boolean save() {
		// if modified... then do save
		if (State().getValue().contains(State.Modified) || 
				// or if loaded from a dataset but was set inactive afterwards
				(State().getValue().contains(State.Dataset) && !Active().getValue())) {
			// Save all the independent children first
			for (BOBusinessObject child : children.values()) {
				// Attributes should be managed by the owner, and shouldn't need to manage themselves
				if(!(child instanceof BOAttribute) && child.Independent().getValue()) {
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
			for (BOBusinessObject child: children.values()) {
				// Attributes should be managed by the owner, and shouldn't need to manage themselves
				if (!(child instanceof BOAttribute) && !child.Independent().getValue()) {
					child.save();
				}
			}			
			// Should be safe to assume the following state after successful saving
			State().getValue().remove(State.Modified);
			State().getValue().add(State.Dataset);
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return toString(0);
	}
	
	protected String toString(int spaces) {
		StringBuilder sb = new StringBuilder();
		// first line: Name:ClassName
		sb.append(StringUtil.getRepeatedString(" ", spaces)).append(Name().getValue()).append(':').append(getClass().getSimpleName()).
				append(' ').append(getPropertyStrings()).append('\n');
		
		// call toString on all children with spaces += 4
		for (BOBusinessObject child : children.values()) {
			sb.append(child.toString(spaces + 4));
		}
		return sb.toString();
	}
	
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
	public abstract void doDelete();
	
	/**
	 * This is used to populate all child attributes and objects.
	 * It is up to the implementation of the object to decide to populate with default values or to load values from some source. 
	 * @return 
	 * Whether or not this object is populated from a dataset. This is used to determine the state.
	 */
	protected abstract boolean populateAttributes();
	
	/**
	 * Create all child attribute objects
	 */
	protected abstract void createAttributes();
	
	/**
	 * Clear all child attribute objects. This is effectively setting default values into all attributes.
	 */
	public abstract void clearAttributes();
	
	/**
	 * Event thrown when something down the hierarchy is modified.
	 * 
	 * @param source
	 */
	public abstract void handleModified(ModifiedEvent source);
}
