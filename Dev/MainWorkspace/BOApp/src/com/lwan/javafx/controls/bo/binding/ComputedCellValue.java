package com.lwan.javafx.controls.bo.binding;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.javafx.controls.bo.BOGrid;

public class ComputedCellValue <B extends BusinessObject> implements Callback<CellDataFeatures<B, Object>, ObservableValue<Object>>, ModifiedEventListener{
	private String field;
	private BOGrid<B> grid;
	private Set<ComputedObservable> observables;
	private Set<ComputedObservable> toRemove;
	private boolean handlingModified;
	
	public ComputedCellValue (BOGrid<B> grid, BOLinkEx<BOSet<B>> link, String field) {
		this.field = field;
		this.grid = grid;
		observables = new HashSet<>();
		toRemove = new HashSet<>();
		handlingModified = false;
		
		link.addListener(this);
	}
	
	@Override
	public void handleModified(ModifiedEvent event) {
		handlingModified = true;
		try{
			for (ComputedObservable observable : observables) {
				observable.invalidate();
			}
			observables.removeAll(toRemove);
		} finally {
			handlingModified = false;
		}
	}
	
	protected void requestRemove(ComputedObservable observable) {
		if (handlingModified) {
			toRemove.add(observable);
		} else {
			observables.remove(observable);
		}
	}
	
	public ObservableValue<Object> call(CellDataFeatures<B, Object> p) {
		return new ComputedObservable(p.getValue());
	}
	
	private class ComputedObservable implements ObservableValue<Object> {
		private List<InvalidationListener> invalidationListeners;
		private List<ChangeListener<? super Object>> changeListeners;
		private B observed;
		
		private static final int STATE_UNKNOWN = 0;
		private static final int STATE_VALID = 1;
		private static final int STATE_INVALID = 2;
		int state;
		
		String cachedValue, lastValue;
		
		ComputedObservable(B observed) {
			this.observed = observed;
			state = STATE_UNKNOWN;
		}
		
		@Override
		public Object getValue() {
			if (state == STATE_VALID) {
				return cachedValue;
			} else {
				lastValue = cachedValue;
				cachedValue = grid.getDisplayValue(observed, field);
				if (state == STATE_INVALID) {
					state = STATE_VALID;
				}
				fireChangeEvent();
				return cachedValue;
			}
		}
		
		protected void fireChangeEvent() {
			if ((state == STATE_INVALID || state == STATE_UNKNOWN) && invalidationListeners != null) {
				for (InvalidationListener listener : invalidationListeners) {
					listener.invalidated(this);
				}
			}
			
			if (state == STATE_INVALID) {
				if (changeListeners != null && !changeListeners.isEmpty()) {
					getValue();	// get the latest values
				}
			}
			
			if ((state == STATE_VALID || state == STATE_UNKNOWN) &&
					changeListeners != null) {
				for (ChangeListener<? super Object> listener : changeListeners) {
					listener.changed(this, lastValue, cachedValue);
				}
			}
		}

		protected void invalidate() {
			if (state == STATE_VALID) {
				state = STATE_INVALID;
			}
			fireChangeEvent();
		}
		
		protected void onAddListener() {
			if (state == STATE_UNKNOWN) {
				observables.add(this);
				
				state = STATE_VALID;
				invalidate();
			}
		}
		
		protected void onRemoveListener() {
			if ((invalidationListeners == null || invalidationListeners.isEmpty()) &&
					(changeListeners == null || changeListeners.isEmpty())) {
				requestRemove(this);
				
				state = STATE_UNKNOWN;
			}
		}
		
		@Override
		public void addListener(InvalidationListener arg0) {
			if (invalidationListeners == null) {
				invalidationListeners = new LinkedList<>();
			}
			invalidationListeners.add(arg0);
			
			onAddListener();
		}

		@Override
		public void removeListener(InvalidationListener arg0) {
			if (invalidationListeners != null) {
				invalidationListeners.remove(arg0);
			}
			onRemoveListener();
		}

		@Override
		public void addListener(ChangeListener<? super Object> arg0) {
			if (changeListeners == null) {
				changeListeners = new LinkedList<>();
			}
			changeListeners.add(arg0);
			
			onAddListener();
		}

		@Override
		public void removeListener(ChangeListener<? super Object> arg0) {
			if (changeListeners != null) {
				changeListeners.remove(arg0);
			}
			onRemoveListener();
		}
	}
}
