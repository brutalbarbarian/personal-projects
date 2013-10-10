package com.lwan.javafx.app.util;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog.Actions;
import org.controlsfx.dialog.Dialogs;

import com.lwan.javafx.app.Lng;

public class DialogUtils {
	public static final SimpleDialogType Confirm = SimpleDialogType.Confirm;
	public static final SimpleDialogType Warning = SimpleDialogType.Warning;
	public static final SimpleDialogType Error = SimpleDialogType.Error;
	public static final SimpleDialogType Information = SimpleDialogType.Information;
	
	protected enum SimpleDialogType {
		Confirm, Warning, Error, Information;
	}
	
	public static final Actions[] YesNo = {Actions.YES, Actions.NO};
	public static final Actions[] YesNoCancel = {Actions.YES, Actions.NO, Actions.CANCEL};
	public static final Actions[] Ok = {Actions.OK};
	public static final Actions[] OkCancel = {Actions.OK, Actions.CANCEL};
	public static final Actions[] Close = {Actions.CLOSE};	
	
	public static Action showMessage(String message, String title, SimpleDialogType type, Actions[] actions) {
		Dialogs dialog = getDefaultDialog();
		dialog.message(message).title(title).actions(actions);
		
		switch (type){
		case Confirm:
			return dialog.showConfirm();
		case Error:
			return dialog.showError();
		case Information:
			dialog.showInformation();
			return null;
		case Warning:
			return dialog.showWarning();
		default:
			return null;
		}
	}
	
	public static Action showException(Throwable exception) {
		return showException(exception.getClass().getTypeName(), Lng._("Program has encounted an error:\n") + exception.getMessage(), Ok, exception);
	}
	
	public static Action showException(String message, String title, Actions[] actions, Throwable exception) {
		return getDefaultDialog().message(message).title(title).actions(actions).showException(exception);
	}
	
	protected static Dialogs getDefaultDialog() {
		return Dialogs.create().nativeTitleBar();
	}
}
