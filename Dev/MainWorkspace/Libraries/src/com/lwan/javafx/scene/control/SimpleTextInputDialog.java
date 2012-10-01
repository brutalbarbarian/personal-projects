package com.lwan.javafx.scene.control;

import com.lwan.util.wrappers.ResultCallback;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class SimpleTextInputDialog extends BorderPane implements EventHandler<ActionEvent>{
	/* Result status constants */
	public static final int ResultUnknown = 0;
	public static final int ResultOK = 1;
	public static final int ResultCancel = 2;
	
	private static final int margin = 10;
	
	// properties
	private Property<String> labelProperty;
	private Property<String> hintProperty;	// hint displayed when mouse is hovered 
	private Property<String> titleProperty;	// title displayed in titlebar
	private Property<String> textProperty;	// value in the textbox
	private Property<Orientation> orientationProperty;	// where the textfield is relative to the label 
	private IntegerProperty resultProperty;	// result of showing this dialog
	private BooleanProperty shownProperty;	// if this dialog is still being shown
	private IntegerProperty columnCountProperty;	// the pref column count of the textfield
	
	// public property accessors
	public Property<String> labelProperty() {
		if (labelProperty == null) {
			labelProperty = new SimpleStringProperty();
		}
		
		return labelProperty;
	}
	
	public Property<String> hintProperty() {
		if (hintProperty == null) {
			hintProperty = new SimpleStringProperty();
		}
		return hintProperty;
	}
	
	public Property<String> titleProperty(){
		if (titleProperty == null) {
			titleProperty = new SimpleStringProperty();
		}
		return titleProperty;
	}
	
	public Property<String> textProperty() {
		if (textProperty == null) {
			textProperty = new SimpleStringProperty();
		}
		return textProperty;
	}
	
	public IntegerProperty columnCountProperty() {
		if (columnCountProperty == null){
			columnCountProperty = new SimpleIntegerProperty();
		}
		return columnCountProperty;
	}
	
	public ReadOnlyProperty<Orientation> orientationProperty() {
		return _orientationProperty();
	}
	
	public ReadOnlyIntegerProperty resultProperty() {
		return _resultProperty();
	}
	
	public ReadOnlyBooleanProperty shownProperty() {
		return _shownProperty();
	}
	
	// internal accessors
	protected Property<Orientation> _orientationProperty() {
		if (orientationProperty == null) {
			orientationProperty = new SimpleObjectProperty<>();
		}
		return orientationProperty;
	}
	
	protected IntegerProperty _resultProperty() {
		if (resultProperty == null) {
			resultProperty = new SimpleIntegerProperty(ResultUnknown);
		}
		return resultProperty;
	}
	
	protected BooleanProperty _shownProperty() {
		if (shownProperty == null) {
			shownProperty = new SimpleBooleanProperty(false);
		}
		return shownProperty;
	}

	// controls
	protected Label label;
	protected TextField textField;
	protected Button btnOK, btnCancel;
	protected Stage stage;
	
	public SimpleTextInputDialog(String title, String label) {
		this(title, label, Orientation.HORIZONTAL, "", "", 10);
	}
	
	public SimpleTextInputDialog(String title, String label, Orientation orientation, String hint, String initialText, int initColumnCount) {
		_orientationProperty().setValue(orientation);
		textProperty().setValue(initialText);
		hintProperty().setValue(hint);
		titleProperty().setValue(label);
		labelProperty().setValue(label);
		columnCountProperty().set(initColumnCount);
		
		buildControls();
		
		// bind properties to the actual controls
		textField.textProperty().bindBidirectional(textProperty());
		textField.tooltipProperty().get().textProperty().bind(hintProperty());
		textField.prefColumnCountProperty().bind(columnCountProperty());
		this.label.textProperty().bind(labelProperty());
		
	}
	
	protected void buildControls() {
		Pane centralPane;
		if (orientationProperty().getValue() == Orientation.HORIZONTAL) {
			// horizontal
			centralPane = new HBox(5);
		} else {
			// vertical
			centralPane = new VBox();
		}
		
		label = new Label();
		textField = new TextField();
		textField.tooltipProperty().set(new Tooltip());
		
		centralPane.getChildren().addAll(label, textField);
		centralPane.setStyle("-fx-background-insets:10");
		
		btnOK = new Button("OK");
		btnCancel = new Button("Cancel");
		btnOK.setOnAction(this);
		btnCancel.setOnAction(this);
		
		setCenter(centralPane);
		setMargin(centralPane, new Insets(margin));
		setBottom(ToolBarBuilder.create().items(btnOK, btnCancel).build());
	}
	
	@Override
	public void handle(ActionEvent e) {
		Object src = e.getSource();
		if (src == btnOK) {
			_resultProperty().set(ResultOK);
			stage.close();
		} else if (src == btnCancel) {
			stage.close();
		}
	}
	
	protected void onClose() {
		if (_resultProperty().get() == ResultUnknown) {
			_resultProperty().set(ResultCancel);
		}
		_shownProperty().set(false);
		stage = null;
	}
	
	protected void displayDetailState() {
		// Do nothing
	}
	
	public void show(Window owner, final ResultCallback<SimpleTextInputDialog> result) {
		// setup stage
		stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(owner);
		stage.initStyle(StageStyle.UTILITY);
		stage.setTitle("Select Image...");
		final SimpleTextInputDialog resValue = this;
		stage.setOnHidden(new EventHandler<WindowEvent>() {
			public void handle (WindowEvent arg0) {
				onClose();
				result.call(resValue);
			}
		});
		stage.setScene(new Scene(this));
		
		// initialise variables
		_resultProperty().set(ResultUnknown);
		_shownProperty().set(true);
		
		// position and show window
		displayDetailState();
//		stage.setWidth(400);
//		stage.setHeight(400);
		stage.show();
		stage.centerOnScreen();
	}
}
