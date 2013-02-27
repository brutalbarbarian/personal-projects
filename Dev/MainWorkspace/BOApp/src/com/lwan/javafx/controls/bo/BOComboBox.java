package com.lwan.javafx.controls.bo;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
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
	
	private BOSet<?> set;
	private String attrPath, keyPath;
	
	public <B extends BusinessObject> void setSource(BOSet<B> set, String keyPath, String attributePath) {
		this.set = set;
		this.attrPath = attributePath;
		this.keyPath = keyPath;
		
		populateFromSet();
		set.addListener(new ModifiedEventListener() {
			public void handleModified(ModifiedEvent event) {
				populateFromSet();
			}			
		});
	
	}
	
	@SuppressWarnings("unchecked")
	protected void populateFromSet() {
		if (set != null && !StringUtil.isNullOrBlank(attrPath) && !StringUtil.isNullOrBlank(keyPath)) {
			Map<T, String> values = new HashMap<>();
			for (BusinessObject bo : set) {
				BOAttribute<?> attr = bo.findAttributeByPath(attrPath);
				BOAttribute<T> key = (BOAttribute<T>) bo.findAttributeByPath(keyPath);
				if (attr!= null && key != null) {
//					System.out.println(key,getValu)
					values.put(key.getValue(), attr.asString());
				}
			}
			
			addAllItems(values);
		}
	}
}
