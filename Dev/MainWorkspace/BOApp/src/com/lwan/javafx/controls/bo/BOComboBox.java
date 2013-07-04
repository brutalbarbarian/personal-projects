package com.lwan.javafx.controls.bo;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.controls.ComboBox;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.javafx.controls.bo.binding.BoundProperty;
import com.lwan.util.StringUtil;

public class BOComboBox <T> extends ComboBox<T> implements BoundControl<T> {
	private BoundProperty<T> dataBindingProperty;
	
	@Override
	public BoundProperty<T> dataBindingProperty() {
		return dataBindingProperty;
	}
	
	public BOComboBox(BOLinkEx<?> link, String path) {
		dataBindingProperty = new BoundProperty<>(this, link, path);
		
		initialise();
	}
	
	public BOComboBox(BoundProperty<T> boundProperty) {
		dataBindingProperty = boundProperty;
		
		initialise();
	}
	
	protected void initialise() {
		dataBindingProperty.editableProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable arg0) {
				updateDisableState();
			}
		});
		updateDisableState();
		
		selectedProperty().bindBidirectional(dataBindingProperty);
	}
	
	private BooleanProperty enabledProperty;
	public BooleanProperty enabledProperty() {
		if (enabledProperty == null) {
			enabledProperty = new SimpleBooleanProperty(this, "Enabled", true);
			enabledProperty.addListener(new InvalidationListener() {				
				public void invalidated(Observable arg0) {
					updateDisableState();
				}
			});
		}
		return enabledProperty;
	}
	
	protected void updateDisableState() {
		setDisable(BOCtrlUtil.getDisabled(this));
	}
	
	public void setEnabled(boolean enabled) {
		enabledProperty().set(enabled);
	}
	public boolean isEnabled() {
		return enabledProperty().get();
	}
	
	private BOSet<?> set;
	private String attrPath, keyPath, nullDisplayValue;
	
	public <B extends BusinessObject> void setSource(BOSet<B> set, String keyPath, String attributePath, String nullDisplayValue) {
		this.set = set;
		this.attrPath = attributePath;
		this.keyPath = keyPath;
		this.nullDisplayValue = nullDisplayValue;
		
		populateFromSet();
		
		// not as clean perhaps... but at least its better then 
		// adding a listener to the source set right?... or is it.
		dataBindingProperty.addListener(new ChangeListener<T>() {
			public void changed(ObservableValue<? extends T> arg0, T arg1,
					T arg2) {
				populateFromSet();
			}
		});
		setOnShowing(new EventHandler<Event>() {
			public void handle(Event arg0) {
				populateFromSet();
			}			
		});
		
		lastEventRefreshed = -1;
	}
	
	/**
	 * Call this instead of setEditable()
	 * 
	 * @param editable
	 */
	public void setEditableEx(boolean editable) {
		selectedProperty().unbindBidirectional(dataBindingProperty);
		try {
			setEditable(editable);
		} finally {
			selectedProperty().bindBidirectional(dataBindingProperty);
		}
		
	}
	
	private long lastEventRefreshed;
	@SuppressWarnings("unchecked")
	protected void populateFromSet() {
		if (set != null && !StringUtil.isNullOrBlank(attrPath) && !StringUtil.isNullOrBlank(keyPath)) {
			if (lastEventRefreshed == set.getLastEventTimestamp()) {
				return;
			}
			
			Map<T, String> values = new HashMap<>();
			for (BusinessObject bo : set) {
				BOAttribute<?> attr = bo.findAttributeByPath(attrPath);
				BOAttribute<T> key = (BOAttribute<T>) bo.findAttributeByPath(keyPath);
				if (attr!= null && key != null) {
					values.put(key.getValue(), attr.asString());
				}
			}
			beginBulkUpdate();
			try {
				clearItems();
				if (nullDisplayValue != null) {
					addItem(null, nullDisplayValue);
				}
				addAllItems(values);
			} finally {
				endBulkUpdate();
				lastEventRefreshed = set.getLastEventTimestamp();
			}
		}
	}
	
	public void beginBulkUpdate() {
		if (!isBulkUpdating()) {
			selectedProperty().unbindBidirectional(dataBindingProperty);
		}
		super.beginBulkUpdate();
	}
	
	public void endBulkUpdate() {
		super.endBulkUpdate();
		if (!isBulkUpdating()) {
			selectedProperty().bindBidirectional(dataBindingProperty);
		}
	}
	
	
}
