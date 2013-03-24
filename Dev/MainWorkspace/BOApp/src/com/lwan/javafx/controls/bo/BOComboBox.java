package com.lwan.javafx.controls.bo;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.event.EventHandler;

import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
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
		
		disableProperty().bind(Bindings.not(dataBindingProperty.editableProperty()));
		selectedProperty().bindBidirectional(dataBindingProperty);
	}
	
	public BOComboBox(BoundProperty<T> boundProperty) {
		dataBindingProperty = boundProperty;
		
		disableProperty().bind(Bindings.not(dataBindingProperty.editableProperty()));
		selectedProperty().bindBidirectional(dataBindingProperty);
	}
	
	private BOSet<?> set;
	private String attrPath, keyPath, nullDisplayValue;
	
	public <B extends BusinessObject> void setSource(BOSet<B> set, String keyPath, String attributePath, String nullDisplayValue) {
		this.set = set;
		this.attrPath = attributePath;
		this.keyPath = keyPath;
		this.nullDisplayValue = nullDisplayValue;
		
		populateFromSet();
		set.addListener(new ModifiedEventListener() {
			public void handleModified(ModifiedEvent event) {
				populateFromSet();
			}			
		});
		// Refresh on showing...not sure if this is a good idea or not but eh.
		setOnShowing(new EventHandler<Event>() {
			public void handle(Event arg0) {
				populateFromSet();
			}			
		});
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
	
	@SuppressWarnings("unchecked")
	protected void populateFromSet() {
		if (set != null && !StringUtil.isNullOrBlank(attrPath) && !StringUtil.isNullOrBlank(keyPath)) {
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
