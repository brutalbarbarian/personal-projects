package com.lwan.bo;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;

/**
 * A reference set, which should be a strict subset of a designate source set.
 * The main use for this is for displaying items in a grid.
 * 
 * @author Brutalbarbarian
 *
 * @param <T>
 */
public class BOSetRef<T extends BusinessObject> extends BOSet<T> {
	private Property<BOSet<T>> source_set;
	
	// 2 Modes of defining the subset of the source.
	protected static final int MODE_FILTER = 0;
	protected static final int MODE_SUBSET = 1;
	
	protected int modeType;
	protected Callback<?, ?> filter;	
	
	@SuppressWarnings("unchecked")
	protected boolean allows(T item) {
		if (modeType == MODE_FILTER) {
			if (filter == null) {
				return true;
			} else {
				return ((Callback<T, Boolean>)filter).call(item);
			}
		} else {
			throw new RuntimeException("allows() called when mode is not MODE_FILTER");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<Integer> getSubset(){
		if(modeType == MODE_SUBSET) {
			if (filter == null) {
				throw new RuntimeException("No filter set for getSubset()");
			} else {
				return ((Callback<BOSetRef<T>, Iterable<Integer>>)filter).call(this);
			}
		} else {
			throw new RuntimeException("getSubset() called when mode is not MODE_SUBSET");
		}
	}
	
	public ReadOnlyProperty<BOSet<T>> SourceSet() {
		return _source_set();
	}
	
	public BOSet<T> getSource() {
		return SourceSet().getValue();
	}
	
	protected Property<BOSet<T>> _source_set() {
		if (source_set == null) {
			source_set = new SimpleObjectProperty<BOSet<T>>(this, "SourceSet", null);
		}
		return source_set;
	}
	
	public static <T extends BusinessObject>  BOSetRef<T> createFilteredSet(
			BOSet<T> source, Callback<T, Integer> filter) {
		return new BOSetRef<>(source, filter, MODE_FILTER);
	}
	
	public static <T extends BusinessObject> BOSetRef<T> createSubset (
			BOSet<T> source, Callback<BOSetRef<T>, Iterable<Integer>> subset) {
		
		return new BOSetRef<>(source, subset, MODE_SUBSET);		
	}
	
	protected BOSetRef(BOSet<T> source, Callback<?, ?> filter, int mode) {
		super(null, source.getName(), source.childIDNameProperty().getValue());

		modeType = mode;
		_source_set().setValue(source);
		this.filter = filter;
	}

	protected boolean childExists(Object id) {
		return getSource().childExists(id);
	}

	protected T createChildInstance(Object id) {
		return getSource().createChildInstance(id);
	}
	
	protected void addChild(T child) {
		Entry e = new Entry(child);
		e.loaded = true;
		children.add(e);
	}
	
	protected T ensureChildActive(int id) {
		T result = getSource().ensureChildActive(id);
		if (result != null) {
			boolean containsChild = false;
			for (T child : this) {
				if (child == result) {
					containsChild = true;
					break;
				}
			}
			if (!containsChild) {
				addChild(result);				
			}
		}
		return result;		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean populateAttributes() {
		if (modeType == MODE_FILTER) {
			Callback<T, Boolean> filter = (Callback<T, Boolean>)this.filter;
			for (T child : getSource()) {
				if (filter.call(child)) {
					addChild(child);
				}
			}
		} else { // MOD_SUBSET..
			BOSet<T> set = getSource();
			boolean ownerIsCache = set.loadModeProperty().getValue() == LOADMODE_CACHE; 
			for (int i : getSubset()) {
				T child;
				if (ownerIsCache) {
					child = set.ensureChildActive(i);
				} else {
					child = set.findChildByID(i);
				}
				if (child != null) {
					addChild(child);
				}
			}
		}	
		return true;
	}

}
