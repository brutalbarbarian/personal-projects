package com.lwan.musicsync.grid;


import com.lwan.musicsync.audioinfo.AudioInfo;
import com.lwan.musicsync.enums.FieldKeyEx;
import com.lwan.musicsync.main.Constants;

import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class StringEditingCell extends BaseEditingCell<String> {
	private static Callback<TableColumn<AudioInfo, String>, TableCell<AudioInfo, String>> factory;
	
	public static Callback<TableColumn<AudioInfo, String>, TableCell<AudioInfo, String>> 
			getStringEditingCellFactory(final boolean allowContextMenu) {
		if (factory == null) {
			factory = new Callback<TableColumn<AudioInfo, String>, TableCell<AudioInfo, String>>() {
				public TableCell<AudioInfo, String> call(TableColumn<AudioInfo, String> p) {
					return new StringEditingCell(allowContextMenu);
				}
			};
        }
		return factory;
	}
	
	private TextField textField;
	private boolean actualCancel = false;
	
	protected StringEditingCell (boolean allowContextMenu) {
		super(allowContextMenu);
		setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e) {
				// Only auto-start edit if its a single cell being selected.
				if (e.getButton().equals(MouseButton.PRIMARY) && isFocused() && 
						getTableView().getSelectionModel().getSelectedCells().size() == 1) {
					startEdit();	
				}
			}
		});
		Tooltip tip = new Tooltip();
		tip.textProperty().bind(textProperty());
		setTooltip(tip);
	}

	@Override
	public void startEdit() {
		if (FieldKeyEx.isModifiable(getTableColumn().getText())) {
			// ensure this is being edited so commitEdit and updateItem will work properly
//			TableRow<?> row = getTableRow();
			super.startEdit();

			if (textField == null) {
				createTextField();
			}
			// always get the latest text
			textField.setText(getString());

			setGraphic(textField);
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

			textField.requestFocus();
//			setFocused(true);
		}
	}
	
	@Override
	public void cancelEdit() {
		if (actualCancel) {
			super.cancelEdit();
			setText(String.valueOf(getItem()));
			setContentDisplay(ContentDisplay.TEXT_ONLY);
		} else {
			commitEdit(textField.getText());
		}
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setGraphic(textField);
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			} else {
				setText(getString());
				setContentDisplay(ContentDisplay.TEXT_ONLY);
			}
		}
	}
	
	public void commitEdit(String text) {
		actualCancel = true;
		super.commitEdit(text);
		actualCancel = false;
	}
	
	protected void createTextField() {
		textField = new TextField(getString());
		textField.setStyle(Constants.getTextCellStyle());
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit(textField.getText());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					actualCancel = true;
					cancelEdit();
					actualCancel = false;
				} else if (t.getCode() == KeyCode.TAB) {
					// commit then select the next cell.
					commitEdit(textField.getText());
					selectCellOnRight();
				}
			}
		});
	}

	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}

	@Override
	public boolean allowsCellEdit() {
		return true;
	}
}