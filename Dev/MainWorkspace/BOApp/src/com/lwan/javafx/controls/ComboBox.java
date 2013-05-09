package com.lwan.javafx.controls;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.util.StringConverter;

import com.lwan.javafx.app.util.AutocompleteController;
import com.lwan.util.CollectionUtil;
import com.lwan.util.CollectionUtil.MapRunner;
import com.lwan.util.GenericsUtil;
import com.lwan.util.FxUtils;
import com.lwan.util.StringUtil;
import com.lwan.util.containers.TrieMap;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

/**
 * Extension of the default ComboBox with support for a display/value map.
 * 
 * @author Brutalbarbarian
 *
 * @param <T>
 */
public class ComboBox <T> extends javafx.scene.control.ComboBox<ComboBoxItem<T>> {
	protected List<ComboBoxItem<T>> items;
	protected TrieMap<ComboBoxItem<T>> autocompleteList;	// String as this is for editing only
	private Property<T> selectedProperty;
	private Property<Boolean> appendUniqueStringsProperty;
	private Property<Callback<String, T>> uniqueStringConverterProperty;
	private AutocompleteController autoCompleteController;
	 
	private int bulkUpdateState; 	// To avoid unnecessary syncing while bulk adding
	private boolean invalidating;	// To avoid infinite loops
	
	/**
	 * The value which is currently selected. This is not necessarily the same
	 * as calling getValue().getValue() if getValue() returns null, representing
	 * a value that dosen't have a corresponding ComboBoxItem created for it.
	 * 
	 * @return
	 */
	public Property<T> selectedProperty() {
		return selectedProperty;
	}
	
	/**
	 * Will attempt to append unique strings to the list of possible items if this is
	 * set to true.
	 * If T is not of type String, make sure to add a callback to the uniqueStringConverterProperty,
	 * as this combobox will assume T is of type String and force cast it.
	 * 
	 * @return
	 */
	public Property<Boolean> appendUniqueStringsProperty() {
		if (appendUniqueStringsProperty == null) {
			appendUniqueStringsProperty = new SimpleBooleanProperty(this, "AppendUniqueStrings", false);
		}
		return appendUniqueStringsProperty;
	}
	
	/**
	 * Callback which converts unique strings to type T. If this is not set, and appendUniqueStringsProperty
	 * is set to true, then T is assumed to be of type String and is force cast.
	 * 
	 * @return
	 */
	public Property<Callback<String, T>> uniqueStringConverterProperty (){
		if (uniqueStringConverterProperty == null) {
			uniqueStringConverterProperty = new SimpleObjectProperty<Callback<String,T>>(this, "UniqueStringConverter", null);
		}
		return uniqueStringConverterProperty;
	}
	
	public Callback<String, T> getUniqueStringConverter() {
		return uniqueStringConverterProperty().getValue();
	}
	
	public void setUniqueStringConverter(Callback<String, T> converter) {
		uniqueStringConverterProperty().setValue(converter);
	}
	
	public ComboBox() {
//		editableProperty().set(true);		// Shouldn't be editable by default..
		converterProperty().setValue(new StringConverter<ComboBoxItem<T>>() {
			public ComboBoxItem<T> fromString(String arg0) {
				// Use prefix matching... match to the most likely item (% of total item matched).
				ComboBoxItem<T> closestItem = null;
				double closestMatch = 0;
				double length = arg0.length();
				for (ComboBoxItem<T> item : items) {
					if (StringUtil.beginsWith(item.toString(), arg0, true)) {
						double match = (length == 0 && item.toString().length() == 0) ?
								1 : (length / item.toString().length());
						if (match > closestMatch) {
							closestItem = item;
							closestMatch = match;
							if (match == 1) {
								break;	// No point continuing... found perfect match.
							}
						}
					}
				}
				if (appendUniqueStrings() && closestMatch < 1) {
					// Must be unique... create a new item
					if (StringUtil.isNullOrBlank(arg0)) {
						// Don't create a new item if the item is empty...
						closestItem =  null;
					} else {
						closestItem =  createFromUniqueString(arg0);
					}
				}
				
				if (closestItem == null) {
					// Rather revert then commit null...
					closestItem = getValue();
					if (closestItem == null) {
						getEditor().setText("");	
					} else {
						getEditor().setText(closestItem.toString());
					}
				}
				return closestItem;
			}

			@Override
			public String toString(ComboBoxItem<T> item) {
				return item == null? "" : item.toString();
			}
		});
				
		items = new Vector<ComboBoxItem<T>>();
		itemsProperty().set(new ObservableListWrapper<ComboBoxItem<T>>(items));
		
		invalidating = false;
		bulkUpdateState = 0;
		// Custom bidirectional binding
		selectedProperty = new SimpleObjectProperty<T>(this, "Selected", null);
		selectedProperty.addListener(new ChangeListener<T>() {
			public void changed(ObservableValue<? extends T> arg0, 
					T oldValue, T newValue) {
				if (!invalidating) {
					invalidating = true;
					ensureSyncSelection();
					invalidating = false;
				}
			}
		});
		valueProperty().addListener(new ChangeListener<ComboBoxItem<T>>() {
			public void changed(ObservableValue<? extends ComboBoxItem<T>> arg0,
					ComboBoxItem<T> oldValue, ComboBoxItem<T> newValue) {
				if (!invalidating) {
					invalidating = true;
					if (newValue == null) {
						setSelected(null);
						// Attempt to find out if theres a null value to map to instead?
						for (ComboBoxItem<T> item : items) {
							if (item.getValue() == null) {
								getSelectionModel().select(newValue);
								break;
							}
						}
					} else {
						setSelected(newValue.getValue());
					}
					invalidating = false;
				}
			}
		
		});
		
		autoCompleteController = new AutocompleteController(getEditor(), true);
		autoCompleteController.allowUniqueProperty().bind(appendUniqueStringsProperty());

		ensuringEditingFocus = false;
		focusRunner = new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				if (getSkin() == null) {
					Platform.runLater(focusRunner);
				} else {
					ensuringEditingFocus = true;
					try {
						// Horrendus workaround...issue is caused by the skin not being created
						// upon the combobox gaining focus. 						
						ComboBoxListViewSkin<T> skin = (ComboBoxListViewSkin<T>)getSkin();
						skin.getDisplayNode().requestFocus();
						requestFocus();
					} finally {
						ensuringEditingFocus = false;
					}
				}
			}

		};
		focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				if (!ensuringEditingFocus && arg2) {
					Platform.runLater(focusRunner);
				}
			}
		});
		
		getEditor().textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				if (!autoCompleteController.isEditing()) {
					ComboBoxItem<T> item = getSelectionModel().getSelectedItem();
					
					String effectiveSelectedValue = item == null? "" : item.toString();
					String effectiveText = arg2 == null? "" : arg2;
					if (!effectiveSelectedValue.equalsIgnoreCase(effectiveText)) {
						autoCompleteController.editingProperty().set(true);
					}
				}
			}
		});
		
		FxUtils.setNodeTreeFocusable(getEditor(), false);
		
		selectedProperty().addListener(new ChangeListener<T>() {
			public void changed(ObservableValue<? extends T> arg0, T arg1,
					T arg2) {
				autoCompleteController.editingProperty().set(false);
			}			
		});
	}
	
	private boolean ensuringEditingFocus;
	private Runnable focusRunner;
	
	@SuppressWarnings("unchecked")
	protected ComboBoxItem<T> createFromUniqueString(String s) {
		if (getUniqueStringConverter() != null) {
			T item = getUniqueStringConverter().call(s);
			return addItem(item, s);
		} else {
			// Assume T is of type string... since user didn't provide details...
			return addItem((T)s, null);
		}
	}
	
	public void forceCommit() {
		if (isShowing()) {
			hide();
		} else if (isEditable() && getEditor().isFocused()) {
			String sel = getEditor().getText();
			ComboBoxItem<T> selected = getConverter().fromString(sel);
			if (selected != null) {
				getSelectionModel().select(selected);
			}
		}
	}
	
	public boolean appendUniqueStrings() {
		return appendUniqueStringsProperty().getValue();
	}
	
	public void setAppendUniqueStrings(boolean value) {
		appendUniqueStringsProperty().setValue(value);
	}
	
	/**
	 * Get the selected value. This is the actual value selected, not the 
	 * value displayed as there may be no displayed value present to
	 * reflect the selected value.
	 * 
	 * @return
	 */
	public T getSelected() {
		return selectedProperty().getValue();
	}
	
	/**
	 * Set the selected value. This is the recommended way
	 * of setting the selected item.
	 * 
	 * @param value
	 */
	public void setSelected(T value) {
		selectedProperty().setValue(value);
	}
	
	/**
	 * Call to begin the bulk update process.
	 * Make sure to call endBulkupdate() when the bulk updating is finished.
	 * Safest to do this in a try...finally statement.
	 * 
	 */
	public void beginBulkUpdate() {
		bulkUpdateState++;		
	}
	
	protected boolean isBulkUpdating() {
		return bulkUpdateState > 0;
	}
	
	/**
	 * Call to end the bulk update process. 
	 * 
	 */
	public void endBulkUpdate() {
		bulkUpdateState--;
		if (bulkUpdateState == 0) {
			ensureSyncSelection();
		} else if (bulkUpdateState < 0) {
			throw new RuntimeException("endBulkUpdate() while not end bulk update state");
		}
	}
	
	/**
	 * Add an item to the ComboBox list.
	 * It is recommended to call the bulk update functions
	 * addAllItems() as opposed to this, or alternatively use
	 * beginBulkUpdate() and endBulkUpdate() in order to use in an
	 * external loop. This is to avoid ensureSyncSelection() being
	 * called multiple time.
	 * 
	 * @param value
	 * @param display
	 */
	public ComboBoxItem<T> addItem(T value, String display) {
		ComboBoxItem<T> item = new ComboBoxItem<T>(display, value);
		items.add(item);
		ensureSyncSelection();
		return item;
	}
	
	/**
	 * This method ensures the selected ComboBoxItem matches
	 * the selected value. This is optimized to only run once at 
	 * the end of all bulk updates.
	 * 
	 */
	protected void ensureSyncSelection() {
		if (bulkUpdateState == 0) {
			refreshItems();
			// update the selection
			ComboBoxItem<T> item = getValue();
			T sel = getSelected();
			if (item == null || !GenericsUtil.Equals(sel, item.getValue())) {
				// The values don't match up...
				boolean found = false;
				for (ComboBoxItem<T> i : items) {
					if (GenericsUtil.Equals(i.getValue(), sel)) {
						getSelectionModel().select(i);
						found = true;
						break;
					}
				}
				if (!found) {
					// Can't find... just select nothing.
					getSelectionModel().select(null);
				}
			}
			// update the autocomplete controller			
			autoCompleteController.setSource(CollectionUtil.Map(items,
					new MapRunner<ComboBoxItem<T>, List<String>>(){
						public void run(ComboBoxItem<T> item, List<String> result) {
							result.add(item.toString());
						}
						public List<String> getBaseInstance() {
							return new Vector<>();
						}
					}));
		}
	}
	
	/**
	 * The popup list won't actually refresh unless the observable list is 
	 * set to a new value. Setting it to null and resetting the previous value
	 * seems to solve this.
	 * 
	 */
	protected void refreshItems() {
		getSelectionModel().clearSelection();	// Must call this first..
		ObservableList<ComboBoxItem<T>> list = getItems();
		setItems(null);
		setItems(list);
	}

	/**
	 * Clear all items from the ComboBox list
	 * 
	 */
	public void clearItems() {
		items.clear();
		ensureSyncSelection();
	}
	
	public void addAllItems(Map<T, String> items) {
		beginBulkUpdate();
		try {
			if (items != null) {
				for (Entry<T, String> item : items.entrySet()) {
					addItem(item.getKey(), item.getValue());
				}
			}
		} finally {
			endBulkUpdate();
		}
	}
	
	public void addAllItems(T[] values, String[] displayValues) {
		addAllItems(CollectionUtil.getIterable(values), 
				CollectionUtil.getIterable(displayValues));
	}
	
	public void addAllItems(Iterable<T> values, Iterable<String> displayValues) {
		beginBulkUpdate();
		try {
			Iterator<T> itV = values.iterator();
			Iterator<String> itD = displayValues.iterator();
			int count = 0;
			
			while(itV.hasNext() && itD.hasNext()) {
				addItem(itV.next(), itD.next());
				count++;
			}
			
			if (itV.hasNext() || itD.hasNext()) {
				// first revert all changes...
				for (int i = 0; i < count; i++) {
					items.remove(items.size() - 1);
				}
					
				throw new RuntimeException("Item count in values and displayValues do not match.");
			}
		} finally {
			endBulkUpdate();
		}
	}
	
	public void addAllItems(Iterable<T> values, String[] displayValues) {
		addAllItems(values, CollectionUtil.getIterable(displayValues));
	}
	
	public void addAllItems(T[] values, Iterable<String>displayValues) {
		addAllItems(CollectionUtil.getIterable(values), displayValues);
	}
	
	/**
	 * Add in a list of keys without display value.
	 * The display value will be taken from the toString of the item.
	 * 
	 * @param values
	 */
	public void addAllItems(Iterable<T> values) {
		beginBulkUpdate();
		try {
			for (T item : values) {
				addItem(item, null);
			}
		} finally {
			endBulkUpdate();	
		}
		
		
	}

	/**
	 * Add in a list of keys without display value.
	 * The display value will be taken from the toString of the item.
	 * 
	 * @param values
	 */
	public void addAllItems(T[] values) {
		addAllItems(CollectionUtil.getIterable(values));
	}
	
}
