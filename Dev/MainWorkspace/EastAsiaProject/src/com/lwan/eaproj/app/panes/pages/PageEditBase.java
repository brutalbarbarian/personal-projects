package com.lwan.eaproj.app.panes.pages;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog.Actions;

import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.eaproj.app.PageConstants;
import com.lwan.eaproj.app.panes.base.PaneEditBase;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.DialogUtils;
import com.lwan.javafx.controls.pagecontrol.PageData;
import com.lwan.javafx.controls.pagecontrol.PageDataBase;
import com.lwan.util.containers.Params;

public abstract class PageEditBase <B extends BusinessObject, T extends PaneEditBase<B>> extends PageDataBase<T>{
	public PageEditBase(String displayTitle, String UID, PageData<?> parent) {
		super(displayTitle, UID, parent, true, null);
	}
	
	@Override
	public boolean allowClosePageNode(T editPane) {
		if (editPane.inEditState()) {
			// offer the user to save, not save but continue, or cancel
			Action action = DialogUtils.showMessage(
					Lng._("You are about to navagate away from this page. Would you like to save?"), 
					Lng._("Saving"), DialogUtils.Warning, DialogUtils.YesNoCancel);
			if (action == Actions.YES) {
				// Only continue if the save succeeded
				return editPane.getController().activate(editPane.getController().getPrimaryButton());
			} else if (action == Actions.NO) {
				return true;	// User didn't care.
			} else {	// Actions.Cancel
				return false;
			}			
		} else {
			// Not in edit state. should be safe.
			return true;
		}
	}
	
	@Override
	/**
	 * Gets the page node. This method should only make any adjustments
	 * due to the params. Implement getPageNodeEx to return the
	 * actual page node.
	 * 
	 */
	public T getPageNode(Params params) {
		T editPane = getPageNodeEx();
		
		if (params != null) {
			if (params.getValueDefault(PageConstants.PARAM_CREATE, false)) {
				BOSet<?> set = editPane.getSetLink().getLinkedObject();
				if (set != null) {
					set.createNewChild();
					// there really should only be one.
					editPane.select(0);
				}
			} else {
				// Populate the search field if any of the search fields are in the 
				// params
				for (String fieldName : editPane.getSearchFieldNames()) {
					Object value = params.getValue(fieldName);
					if (value != null) {
						editPane.setSearchField(fieldName);
						editPane.setParamValue(value);
						
						break;
					}
				}
				editPane.reopenDataset();
			}
		}
		return editPane;
	}
	
	/**
	 * Gets the page node. Params are dealt with in getPageNode().
	 * This method should only return the page node and nothing else.
	 * 
	 * @param params
	 * @return
	 */
	protected abstract T getPageNodeEx();
}
