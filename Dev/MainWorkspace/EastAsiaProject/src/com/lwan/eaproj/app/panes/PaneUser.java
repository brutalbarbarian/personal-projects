package com.lwan.eaproj.app.panes;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.eaproj.app.panes.base.PaneGridBase;
import com.lwan.eaproj.bo.ref.BOUser;
import com.lwan.eaproj.bo.ref.BOUserSet;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.GridView;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.FxUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

public class PaneUser extends PaneGridBase<BOUser> {
	private BOTextField txtUserName, txtDescription;
	private Button btnChangePassword, btnValidateOldPass;
	private PasswordField pfValidation, pfNewPass, pfValidateNewPass;	
	private TVBox details;
	
	private AlignedControlCell accUserName, accValidation, accNewPass, 
			accValidateNewPass, accDescription;
	
	private static final int NOT_SHOWING = 0;
	private static final int NOT_VALIDATED = 1;
	private static final int VALIDATED = 2;
	private int passwordState;


	@Override
	protected GridView<BOUser> constructGrid(BOLinkEx<BOSet<BOUser>> gridLink) {
		GridView<BOUser> result = new GridView<>("pane_user", gridLink,
				new String[]{"UserName", "Description"},
				LngUtil.translateArray(new String[]{"User Name", "Description"}),
				new Callback<String, Boolean>() {
					public Boolean call(String arg0) {
						return !arg0.equals("UserName");
					}
				});
		
		result.getGrid().setOnSave(new Callback<BOUser, Boolean>() {
			public Boolean call(BOUser user) {
				if (user != null && passwordState == VALIDATED) {
					// make sure the passwords are the same
					if (pfNewPass.getText().equals(pfValidateNewPass.getText())) {
						user.password().setValue(pfNewPass.getText());
						return true;
					} else {
						throw new RuntimeException(Lng._("Passwords do not equal."));
					}
				} else {
					return true;	// don't care
				}
			}			
		});
		
		result.getGridControl().setAllowDeleteCallback(new Callback<BOUser, Boolean>() {
			public Boolean call(BOUser item) {
				return item != BOUserSet.getActiveUser();
			}			
		});
		
		return result;
	}
	
	@Override
	protected Node initEditPane() {
		details = new TVBox();
		details.setSpacing(10);
		details.setPadding(new Insets(5));
		
		TVBox.setVgrow(details, Priority.NEVER);
		
		txtUserName = new BOTextField(getMainLink(), "UserName");
		pfValidation = new PasswordField();
		pfNewPass = new PasswordField();
		pfNewPass.setPromptText(Lng._("Type the new password."));
		pfValidateNewPass = new PasswordField();
		pfValidateNewPass.setPromptText(Lng._("Type the new password again."));
		txtDescription = new BOTextField(getMainLink(), "Description");
		
		accUserName = new AlignedControlCell(Lng._("Username"), txtUserName, details);
		accValidation = new AlignedControlCell(Lng._("Old Password"), pfValidation, details);
		accNewPass = new AlignedControlCell(Lng._("New Password"), pfNewPass, details);
		accValidateNewPass = new AlignedControlCell("", pfValidateNewPass, details);
		accDescription = new AlignedControlCell(Lng._("Description"), txtDescription, details);
		
		btnChangePassword = new Button(Lng._("Change Password"));
		btnValidateOldPass = new Button(Lng._("Validate"));
		
		btnChangePassword.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				passwordState = NOT_VALIDATED;
				displayPaneState();
			}			
		});
		btnValidateOldPass.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				if (gridView.getSelectedLink().getLinkedObject().checkPassword(pfValidation.getText())) {
					passwordState = VALIDATED;
					displayPaneState();
				} else {
					FxUtils.ShowErrorDialog(getScene().getWindow(), Lng._("Incorrect password."));
				}
			}
		});
		pfValidation.setOnAction(btnValidateOldPass.getOnAction());
		
		details.getChildren().addAll(
				accUserName, accDescription, btnChangePassword,
				accValidation, btnValidateOldPass,
				accNewPass, accValidateNewPass);
		
		return details;
	}

	@Override
	protected void initGridLink(BOLinkEx<BOSet<BOUser>> gridLink) {
		gridLink.setLinkedObject(BOUserSet.getSet());
	}
	
	@Override
	protected void onNewSelection(BOUser selected) {
		super.onNewSelection(selected);
		
		if (selected == null || selected.isFromDataset()) {
			passwordState = NOT_SHOWING;
		} else {
			passwordState = VALIDATED;
		}
	}
	
	public void displayPaneState() {
		super.displayPaneState();
		
		if (passwordState != NOT_SHOWING) {
			FxUtils.setVisibleAndManaged(btnChangePassword, false);
		}
		if (passwordState != NOT_VALIDATED) {
			FxUtils.setVisibleAndManaged(btnValidateOldPass, false);
			FxUtils.setVisibleAndManaged(accValidation, false);
			pfValidation.clear();
		}
		if (passwordState != VALIDATED) {
			FxUtils.setVisibleAndManaged(accNewPass, false);
			FxUtils.setVisibleAndManaged(accValidateNewPass, false);
			pfNewPass.clear();
			pfValidateNewPass.clear();
		}
		
		if (passwordState == NOT_SHOWING) {
			FxUtils.setVisibleAndManaged(btnChangePassword, true);
		} else if (passwordState == NOT_VALIDATED) {
			FxUtils.setVisibleAndManaged(btnValidateOldPass, true);
			FxUtils.setVisibleAndManaged(accValidation, true);
		} else if (passwordState == VALIDATED) {
			FxUtils.setVisibleAndManaged(accNewPass, true);
			FxUtils.setVisibleAndManaged(accValidateNewPass, true);
			gridView.getGrid().isEditingProperty().setValue(true);
		}
		
		btnChangePassword.setDisable(gridView.getSelectedLink().getLinkedObject() == null);
		
		layout();
		
	}

	@Override
	protected Node initParamPane() {
		return null;
	}
}
