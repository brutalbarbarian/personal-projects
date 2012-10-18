package com.lwan.musicsync.grid;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.jaudiotagger.tag.FieldKey;

import com.lwan.javafx.scene.control.FloatingShadowPane;
import com.lwan.musicsync.audioinfo.AudioInfo;
import com.lwan.musicsync.audioinfo.AudioInfoArtworkProperty;
import com.lwan.musicsync.audioinfo.AudioInfoProperty;
import com.lwan.musicsync.audioinfo.AudioInfoRatingProperty;
import com.lwan.musicsync.enums.FieldKeyEx;
import com.lwan.musicsync.main.Constants;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBoxBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public abstract class BaseEditingCell <T> extends TableCell<AudioInfo, T> {
	private ContextMenu menu;
	private static final int A_EDIT = 0, A_CLEAR = 1, A_ROW = 2, A_COLUMN = 3,
			A_TRACK = 4;

	private BaseEditingCell<T> self() {
		return this;
	}
	
	@SuppressWarnings("rawtypes")
	public AudioInfo getAudioInfo() {
		TableRow row = getTableRow();
		if (row != null) {
			return (AudioInfo) row.getItem();
		} else {
			return null;
		}
	}
	
	public abstract boolean allowsCellEdit();
	
	MenuItem iEdit, iClear, iRow, iColumn,
		iSelectTracks;	// kinda pointless... exactly same as iColumn + iEdit
	
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
					builder.text("Edit Track(s)");
					builder.onAction(new ContextHandler(A_TRACK));
					iSelectTracks = builder.build();
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
					menu = new ContextMenu(iSelectTracks, iEdit, iClear, 
							new SeparatorMenuItem(), iRow, iColumn);
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
			case A_TRACK: HandleEditTrack(e); break;
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
		
		void HandleEditTrack(ActionEvent e) {
			showEditingPopup(false);
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
		
		final Stage stage = FloatingShadowPane.createShadowedStage(pane, true);
		stage.initModality(Modality.APPLICATION_MODAL);
		pane.initialise();
		
		stage.initOwner(getScene().getWindow());
		Point2D cent = JavaFXUtil.screenPositionOf(self());
		stage.setX(cent.getX());
		stage.setY(cent.getY());
		
		// This will ensure the popup window isn't positioned off screen.
		stage.onShownProperty().set(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent arg0) {
				Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
				double maxWidth = ss.getWidth();
				double maxHeight = ss.getHeight();
				if (stage.getX() + stage.getWidth() > maxWidth) {
					stage.setX(maxWidth - stage.getWidth() - 20);
				}
				if (stage.getY() + stage.getHeight() > maxHeight) {
					stage.setY(maxHeight - stage.getHeight() - 20);
				}
			}
			
		});
		
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
			
			if (useCells) {
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
			} else {
				// Get a list of all unique audio info
				List<AudioInfo> infos = new Vector(CollectionUtil.getAllDistinct(selectedRecords));
				for (Enum<?> key : Constants.getFilteredProperties()) {
					selected.put(key, infos);
				}
			}
			
			info = new AudioInfo();
			info.setupProperties();	// So we can start accessing properties immediately 
			int row = 0;
			GridPane grid = new GridPane();
			
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
				
				grid.add(new Label(EnumUtil.processEnumName(key)), 0, row);
				
				if (key == FieldKey.COVER_ART) {
					ArtworkEdit edit = new ArtworkEdit(info.cover_artProperty(), 
						new Callback<Object, Boolean>() {
							public Boolean call(Object o) {
								return true;
							}
						}, 	
						new Callback<Object, AudioInfoArtworkProperty>() {
							public AudioInfoArtworkProperty call(Object arg0) {
								return info.cover_artProperty();
							}
						}, true);
					
					grid.add(edit, 1, row);

				} else if (key == FieldKey.RATING) {
					RatingsEdit edit = new RatingsEdit(info.ratingProperty(), 
						new Callback<Object, Boolean>() {
							public Boolean call(Object arg0) {
								return true;
							}
						},
						new Callback<Object, AudioInfoRatingProperty>() {
							public AudioInfoRatingProperty call(Object arg0) {
								return info.ratingProperty();
							}								
						});

					grid.add(edit, 1, row);
					
				} else {
					TextField field = new TextField();
					field.textProperty().bindBidirectional(info.properties.get(key));
					field.editableProperty().set(FieldKeyEx.isModifiable(key));
					
					grid.add(field, 1, row);
				}
				
				info.properties.get(key).nonRefProperty().set(value == notCommonRef);
				if (value != notCommonRef) {
					if (key == FieldKey.COVER_ART){
						// we know they're all equal 
						info.cover_artProperty().setAsBufferedImage(
								 entry.getValue().get(0).cover_artProperty().getAsBufferedImage());
					} else {
						info.properties.get(key).setValue(value);
					}
				}
				
				if (FieldKeyEx.isModifiable(key)) {
					CheckBox chk = new CheckBox("Override");
					chk.selectedProperty().bindBidirectional(info.properties.get(key).modifiedProperty());
					grid.add(chk, 2, row);
				}
				
				row++;
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
						for(Entry<Enum<?>, List<AudioInfo>> entry : selected.entrySet()) {
							Enum<?> key = entry.getKey();
							AudioInfoProperty<?> property = info.properties.get(key);
							if (property.modifiedProperty().get()) {
								Object value = property.getValue();
								for (AudioInfo ai : entry.getValue()) {
									ai.properties.get(key).setValue(value);
								}
							}
						}
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
					} else {
						boolean modified = false;
						for (AudioInfoProperty p : info.properties.values()) {
							if (p.modifiedProperty().get()) {
								modified = true;
								break;
							}
						}
						btnSet.setVisible(modified);
					}
				}
			};
						
			for (AudioInfoProperty p :info.properties.values()) {
				// Reset modified and add modified listener
				p.modifiedProperty().set(false);
				p.modifiedProperty().addListener(cl);
			}
			
			builder.children(btnCancel, btnSet, btnClearAll);
			builder.spacing(5);
			builder.padding(new Insets(5, 0, 0, 0));
			
			BorderPane pane = new BorderPane();
			
			pane.setBottom(builder.build());
			pane.setCenter(grid);
			pane.paddingProperty().set(new Insets(10));
			
			getChildren().add(pane);
		}
		
		// initialise anything that needs getScene().getWindow() to 
		// not be null.
		public void initialise() {
			
		}
	}
}
