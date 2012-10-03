package com.lwan.musicsync.main;

import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.jaudiotagger.tag.FieldKey;

import com.lwan.javafx.scene.control.FloatingShadowPane;
import com.lwan.util.CollectionUtil;
import com.lwan.util.EnumUtil;
import com.lwan.util.GenericsUtil;
import com.lwan.util.JavaFXUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.Stage;
import javafx.util.Callback;

public abstract class BaseEditingCell <T> extends TableCell<AudioInfo, T> {
	private ContextMenu menu;
	private static final int A_EDIT = 0, A_CLEAR = 1, A_ROW = 2, A_COLUMN = 3;

	private BaseEditingCell<T> self() {
		return this;
	}
	
	@SuppressWarnings("rawtypes")
	protected AudioInfo getAudioInfo() {
		TableRow row = getTableRow();
		if (row != null) {
			return (AudioInfo) row.getItem();
		} else {
			return null;
		}
	}
	
	public abstract boolean allowsCellEdit();
	
	MenuItem iEdit, iClear, iRow, iColumn;
	
	@SuppressWarnings("rawtypes")
	protected BaseEditingCell(boolean allowContextMenu) {
		setAllowContextMenu(allowContextMenu);
		setOnContextMenuRequested(new EventHandler<ContextMenuEvent>(){
			public void handle(ContextMenuEvent e) {
				if (!allowContextMenu()) {
					return;	// Don't show context menu if not allowed
				}
				if (menu == null) {
					MenuItemBuilder<?> builder = MenuItemBuilder.create();
					builder.text("Edit Selected");
					builder.onAction(new ContextHandler(A_EDIT));
					iEdit = builder.build();
					builder.text("Clear Selected");
					builder.onAction(new ContextHandler(A_CLEAR));
					iClear = builder.build();
					builder.text("Select Row");
					builder.onAction(new ContextHandler(A_ROW));
					iRow = builder.build();
					builder.text("Select Column");
					builder.onAction(new ContextHandler(A_COLUMN));
					iColumn = builder.build();
					
					// create more buttons
					// edit track..
					// edit track(s)...
					// set 
					menu = new ContextMenu(iEdit, iClear, new SeparatorMenuItem(), iRow, iColumn);
				}
				
				TableViewSelectionModel<AudioInfo> sel = getTableView().getSelectionModel();

				List<TablePosition> selected = sel.getSelectedCells();
				
				if (selected.size() == 1 && isSelected()) {
					iClear.setDisable(!FieldKeyEx.isModifiable(getTableColumn().getText())); 
					iEdit.setDisable(!(allowsCellEdit() &&
							FieldKeyEx.isModifiable(getTableColumn().getText())));
				} else {
					// find all columns selected
					boolean isAllUneditable = true;
					
					for (TablePosition pos : selected) {
						if (FieldKeyEx.isModifiable(pos.getTableColumn().getText())){
							isAllUneditable = false;
							break;
						}
					}
					
					iEdit.setDisable(isAllUneditable);
					iClear.setDisable(isAllUneditable);
				}
				
				menu.show(self(), e.getScreenX(), e.getScreenY());
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void selectCellOnRight() {
		TableViewSelectionModel selModel = getTableView().getSelectionModel();
		int row = getTableRow().getIndex();
		TableColumn col = getTableColumn();
		selModel.select(row, col);
		selModel.selectNext();
		selModel.clearSelection(row, col);
	}
	
	class ContextHandler implements EventHandler<ActionEvent>{
		int context;
		
		ContextHandler(int item) {
			context = item;
		}
		
		public void handle(ActionEvent e) {
			switch(context) {
			case A_EDIT: HandleEdit(e); break;
			case A_CLEAR: HandleClear(e); break;
			case A_ROW: HandleRow(e); break;
			case A_COLUMN: HandleColumn(e); break;
			}
		}
		
		void HandleEdit(ActionEvent e) {
			TableViewSelectionModel<AudioInfo> sel = getTableView().getSelectionModel();
			if (sel.getSelectedItems().size() <= 1) {
				startEdit();
			} else {
				showEditingPopup(true);
			}
		}
		
		
		
		@SuppressWarnings("rawtypes")
		void HandleClear(ActionEvent e) {
			TableViewSelectionModel<AudioInfo> selectionModel = getTableView().getSelectionModel();
			ObservableList<TablePosition> selectedCells = selectionModel.getSelectedCells();
			ObservableList<AudioInfo> selectedItems = selectionModel.getSelectedItems();
			for (int i = 0; i < selectedCells.size(); i++) {
				AudioInfo info = selectedItems.get(i);
				TablePosition pos = selectedCells.get(i);
				Enum key = FieldKeyEx.getEnumOfTitle(pos.getTableColumn().getText());
				if (FieldKeyEx.isModifiable(key)) {
					if (key == FieldKey.COVER_ART) {
						AudioInfoArtworkProperty p = (AudioInfoArtworkProperty)info.properties.get(key);
						p.setAsBufferedImage(null);
					}
					else {
						StringProperty prop = (StringProperty) info.properties.get(key);
						prop.setValue(null);						
					}
				}

			}
		}
		
		void HandleRow(ActionEvent e) {
			TableViewSelectionModel<AudioInfo> selectionModel = getTableView().getSelectionModel();
			ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
			Collection<Integer> indicies = CollectionUtil.getAllDistinct(selectedIndices);
			
			for (TableColumn<AudioInfo, ?> col : getTableView().getColumns()) {
				for (int i : indicies) {
					selectionModel.select(i, col);
				}
			}
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		void HandleColumn(ActionEvent e) {
			TableViewSelectionModel<AudioInfo> selectionModel = getTableView().getSelectionModel();
			ObservableList<TablePosition> selectedCells = selectionModel.getSelectedCells();
			HashSet<TableColumn<AudioInfo, ?>> set = new HashSet<>();
			for (TablePosition pos : selectedCells) {
				set.add(pos.getTableColumn());
			}
			selectionModel.clearSelection();
			int items = getTableView().getItems().size();
			for(TableColumn<AudioInfo, ?> col : set) {
				for (int i = 0; i <= items; i++) {
					selectionModel.select(i, col);
				}
			}
		}
	}
	
	private BooleanProperty allowContextMenuProperty;
	public BooleanProperty allowContextMenuProperty(){
		if (allowContextMenuProperty == null) {
			allowContextMenuProperty = new SimpleBooleanProperty();
		}
		return allowContextMenuProperty;
	}
	public boolean allowContextMenu() {
		return allowContextMenuProperty().get();
	}
	public void setAllowContextMenu(boolean value) {
		allowContextMenuProperty().set(value);
	}
	
	public boolean hasRecord() {
		return getAudioInfo() != null;
	}
	
	public void showEditingPopup(Boolean useCells) {
		EditingPopup pane = new EditingPopup(useCells);
		
		Stage stage = FloatingShadowPane.createShadowedStage(pane, true);
		pane.initialise();
		
		stage.initOwner(getScene().getWindow());
		Point2D cent = JavaFXUtil.screenPositionOf(self());
		stage.setX(cent.getX());
		stage.setY(cent.getY());
		double maxWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		if (cent.getX() + stage.getWidth() > maxWidth) {
			stage.setX(maxWidth - stage.getWidth());
		}
		stage.show();
	}

	// arbitrary object to indicate its not the same value shared
	static final Object notCommonRef = new Object();

	class EditingPopup extends Group {
		HashMap<Enum<?>, List<AudioInfo>> selected;
		// Temp audioinfo file
		AudioInfo info;
		// Buttons
		Button btnCancel, btnSet, btnClearAll;
		
		// Alternative is use rows (tracks)
		@SuppressWarnings({ "rawtypes", "unchecked" })
		EditingPopup(boolean useCells){
			// ignore the useCells prarameter for now
			TableViewSelectionModel<AudioInfo> sel = getTableView().getSelectionModel();
			List<TablePosition> selectedCells = sel.getSelectedCells();
			List<AudioInfo> selectedRecords = sel.getSelectedItems();
			// split the cells by columns
			selected = new HashMap<>();
			for (int i = 0; i < selectedCells.size(); i++) {
				TablePosition pos = selectedCells.get(i);
				AudioInfo item = selectedRecords.get(i);
				TableColumn col = pos.getTableColumn();
				Enum key = FieldKeyEx.getEnumOfTitle(col.getText());
				// create a new list if needed
				List<AudioInfo> li = selected.get(key);
				if (li == null) {
					li = new Vector<AudioInfo>();
					selected.put(key, li);
				}
				li.add(item);
			}
			info = new AudioInfo();
			List<Node> nodes = new LinkedList<>();
			
			for (Entry<Enum<?>, List<AudioInfo>> entry : selected.entrySet()) {
				Enum<?> key = entry.getKey();
				// find if a common value exists
				
				Object value = null;
				boolean isFirst = true;
				for (AudioInfo item : entry.getValue()) {
					Object val = item.properties.get(key).getValue();
					if (isFirst) {
						value = val;
						isFirst = false;
					} else if (!GenericsUtil.Equals(val, value)) {
						value = notCommonRef;
						break;
					} 
				}
				
				if (key == FieldKey.COVER_ART) {
					HBox pane = new HBox();
					Label lbl = new Label(EnumUtil.processEnumName(key));
					lbl.setPrefWidth(100);
					
					ArtworkEdit edit = new ArtworkEdit(info.cover_artProperty(), 
							new Callback<Object, Boolean>() {
						public Boolean call(Object o) {
							return true;
						}
						}, new Callback<Object, AudioInfoArtworkProperty>() {
							public AudioInfoArtworkProperty call(Object arg0) {
								return info.cover_artProperty();
							}
						}, true);
					
					pane.getChildren().addAll(lbl, edit);
					nodes.add(pane);
				} else if (key == FieldKey.RATING) {
					HBox pane = new HBox();
					
					Label lbl = new Label(EnumUtil.processEnumName(key));
					lbl.setPrefWidth(100);
					
					RatingsEdit edit = new RatingsEdit(info.ratingProperty(), new Callback<Object, Boolean>() {
						public Boolean call(Object arg0) {
							return true;
						}
					});

					pane.getChildren().addAll(lbl, edit);
					
					nodes.add(pane);
				} else {
					HBox pane = new HBox();
					TextField field = new TextField();
					field.textProperty().bindBidirectional(info.properties.get(key));
					Label lbl = new Label(EnumUtil.processEnumName(key));
					lbl.setPrefWidth(100);
					
					pane.getChildren().addAll(lbl, field);
					
					nodes.add(pane);
				}
				
				if (value == notCommonRef) {
					info.properties.get(key).nonRefProperty().set(true);
				} else if (key == FieldKey.COVER_ART){
					// we know they're all equal 
					info.cover_artProperty().setAsBufferedImage(
							 entry.getValue().get(0).cover_artProperty().getAsBufferedImage());
				} else {
					info.properties.get(key).setValue(value);
				}
			}
			
			
			HBoxBuilder builder = HBoxBuilder.create();
			btnCancel = new Button("Cancel");
			btnSet = new Button("Set");
			btnClearAll = new Button("Clear All");
			
			EventHandler<ActionEvent> ah = new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					Object src = e.getSource();
					if (src == btnCancel) {
						getScene().getWindow().hide();	// no changes will be saved
					} else if (src == btnSet) {
						// save changes... TODO
						getScene().getWindow().hide();
					} else if (src == btnClearAll) {
						for (AudioInfoProperty<?> p : info.properties.values()) {
							p.setValue(null);
						}
					}
				}
			};
			
			btnCancel.setOnAction(ah);
			btnSet.setOnAction(ah);
			btnClearAll.setOnAction(ah);
			
			btnSet.setVisible(false);
			
			ChangeListener<Boolean> cl = new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> arg0,
						Boolean oldValue, Boolean newValue) {
					if (newValue) {
						btnSet.setVisible(true);
					}
				}
			};
			
			for (AudioInfoProperty p :info.properties.values()) {
				p.modifiedProperty().addListener(cl);
			}
			
			builder.children(btnCancel, btnSet, btnClearAll);
			builder.spacing(5);
			builder.padding(new Insets(5, 0, 0, 0));
			
			VBoxBuilder mainBuilder = VBoxBuilder.create();
			mainBuilder.children(nodes);
			
			BorderPane pane = new BorderPane();
			
			pane.setBottom(builder.build());
			pane.setCenter(mainBuilder.build());
			pane.paddingProperty().set(new Insets(10));
			
			getChildren().add(pane);
		}
		
		// initialise anything that needs getScene().getWindow() to 
		// not be null.
		public void initialise() {
			
		}
	}
}
