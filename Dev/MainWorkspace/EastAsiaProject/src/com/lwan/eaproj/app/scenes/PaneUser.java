package com.lwan.eaproj.app.scenes;

import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.eaproj.bo.BOUser;
import com.lwan.eaproj.bo.cache.BOUserSet;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.controls.bo.BOGrid;
import com.lwan.javafx.controls.bo.BOGridControl;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.FxUtils;
import com.lwan.util.wrappers.Disposable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class PaneUser extends BorderPane implements Disposable{

	public PaneUser() {
		initControls();
	}
	
	BOGrid<BOUser> grid;
	BOLinkEx<BOSet<BOUser>> gridLink;
	BOGridControl<BOUser> gridControl;
	BOTextField txtUserName;
	Button btnChangePassword, btnValidateOldPass;
	PasswordField pfValidation, pfNewPass, pfValidateNewPass;
	BOTextField txtDescription;
	
	VBox details;
	
	AlignedControlCell accUserName, accValidation, accNewPass, accValidateNewPass, accDescription;
	
	
	protected void initControls() {
		gridLink = new BOLinkEx<>();
		gridLink.setLinkedObject(BOUserSet.get());
		grid = new BOGrid<>("pane_user", gridLink, 
				new String[]{"Name", "Description"}, 
				new String[]{"UserName", "Description"}, 
				new boolean[] {false, true});
		VBox.setVgrow(grid, Priority.SOMETIMES);
		
		gridControl = new BOGridControl<BOUser>(grid) {
			@Override
			protected boolean allowDelete(BOUser item) {
				return item != BOUserSet.getActiveUser();
			}
		};
		
		details = new VBox();
		details.setSpacing(10);
		details.setPadding(new Insets(5));
		
		VBox.setVgrow(details, Priority.NEVER);
		
		txtUserName = new BOTextField(gridControl.getSelectedLink(), "UserName");
		pfValidation = new PasswordField();
		pfNewPass = new PasswordField();
		pfNewPass.setPromptText(Lng._("Type the new password."));
		pfValidateNewPass = new PasswordField();
		pfValidateNewPass.setPromptText(Lng._("Type the new password again."));
		txtDescription = new BOTextField(gridControl.getSelectedLink(), "Description");
		
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
				displayState();
			}			
		});
		btnValidateOldPass.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				if (gridControl.getSelectedLink().getLinkedObject().checkPassword(pfValidation.getText())) {
					passwordState = VALIDATED;
					displayState();
				} else {
					FxUtils.ShowErrorDialog(getScene().getWindow(), Lng._("Incorrect password."));
				}
			}
		});
		pfValidation.setOnAction(btnValidateOldPass.getOnAction());
		
		displayState();
		
		gridControl.setHotkeyControls(this);
		
		details.getChildren().addAll(
				accUserName, accDescription, btnChangePassword,
				accValidation, btnValidateOldPass,
				accNewPass, accValidateNewPass);
		
		passwordState = NOT_SHOWING;
		gridControl.getSelectedLink().linkedObjectProperty().addListener(new ChangeListener<BOUser>(){
			public void changed(ObservableValue<? extends BOUser> arg0,
					BOUser arg1, BOUser arg2) {
				if (arg2 == null || arg2.isFromDataset()) {
					passwordState = NOT_SHOWING;
				} else {
					passwordState = VALIDATED;
				}
				BOCtrlUtil.buildAttributeLinks(details);
				displayState();
			}
		});
		gridControl.getSelectedLink().addListener(new ModifiedEventListener(){
			public void handleModified(ModifiedEvent e) {
				displayState();
			}			
		});
		
		grid.setOnSave(new Callback<BOUser, Boolean>() {
			public Boolean call(BOUser result) {
				if (result != null && passwordState == VALIDATED) {
					// make sure the passwords are the same
					if (pfNewPass.getText().equals(pfValidateNewPass.getText())) {
						result.password().setValue(pfNewPass.getText());
						return true;
					} else {
						throw new RuntimeException(Lng._("Passwords do not equal."));
					}
				} else {
					return true;	// don't care
				}
			}			
		});

		VBox mainPane = new VBox();
		mainPane.getChildren().addAll(grid, details);
		
		setCenter(mainPane);
		
		ToolBar toolbar = ToolBarBuilder.create().items(
				gridControl.getPrimaryButton(), gridControl.getSecondaryButton(),
				gridControl.getRefreshButton()).build();
		setBottom(toolbar);
	}
	
	
	private static final int NOT_SHOWING = 0;
	private static final int NOT_VALIDATED = 1;
	private static final int VALIDATED = 2;
	int passwordState;
	protected void displayState() {
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
			grid.isEditingProperty().setValue(true);
		}
		
		btnChangePassword.setDisable(gridControl.getSelectedLink().getLinkedObject() == null);
		
		layout();
	}
	@Override
	public void dispose() {
		gridLink.dispose();
		grid.dispose();		
	}
	
	@Override
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}
}
