package com.lwan.javafx.controls.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.bo.ModifiedEvent;
import com.lwan.bo.ModifiedEventListener;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.other.BOGrid.GridColumn;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.util.FxUtils;
import com.lwan.util.wrappers.Disposable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class GridView <B extends BusinessObject> extends BorderPane implements Disposable{
	private BOGrid<B> grid;
	private GridFooter footer;
	
	private static final Callback<String, Boolean> DEFAULT_EDITABLE_CALLBACK = new Callback<String, Boolean>() {
		public Boolean call(String arg0) {
			return true;	// assume true
		}		
	};
	
	public GridView(String key, BOLinkEx<BOSet<B>> link, String[] fields, 
			String[] displayValues, Callback<String, Boolean> editableCallback) {
		if (editableCallback == null) {
			editableCallback = DEFAULT_EDITABLE_CALLBACK;
		}
		boolean[] editable = new boolean[fields.length];
		
		for (int i = 0; i < fields.length; i++) {
			editable[i] = editableCallback.call(fields[i]);
		}
		
		grid = new BOGrid<B>(key, this, link, displayValues, fields, editable);
		
		gridControl = new BOGridControl<>(grid);
		
		grid.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>(){
			@SuppressWarnings("unchecked")
			public void handle(ContextMenuEvent arg0) {
				// get rid of the existing menu if its still showing
				if (gridMenu != null) {
					gridMenu.hide();
				}
				if (gridMenuHandler == null) {
					gridMenuHandler = new EventHandler<ActionEvent>(){
						public void handle(ActionEvent arg0) {
							MenuItem item = (MenuItem) arg0.getSource();
							if (item.getUserData().equals("showFooter")) {
								footer.setVisible(((CheckMenuItem)item).isSelected());
							} else if (item.getUserData().equals("primary")) {
								gridControl.activate(gridControl.getPrimaryButton());
							} else if (item.getUserData().equals("secondary")) {
								gridControl.activate(gridControl.getSecondaryButton());
							} else if (item.getUserData().equals("refresh")) {
								gridControl.activate(gridControl.getRefreshButton());
							} else if (item.getUserData().toString().startsWith("#")) {
								grid.getColumnByField(item.getUserData().toString().substring(1)).setVisible(
										((CheckMenuItem)item).isSelected());
							}
						}						
					};
					
				}
				
				// check if the target is the child of a textfield
				if (!FxUtils.NodeIsChildOfClass((Node)arg0.getTarget(), TextInputControl.class)) {
					gridMenu = new ContextMenu();
					gridMenu.setOnHidden(new EventHandler<WindowEvent>() {
						public void handle(WindowEvent arg0) {
							gridMenu = null;
						}						
					});
					gridMenu.setAutoHide(true);
					
					// add primary button
					MenuItem cmiPrimary = new MenuItem(gridControl.getPrimaryButton().getText());
					cmiPrimary.setDisable(gridControl.getPrimaryButton().isDisabled());
					cmiPrimary.setOnAction(gridMenuHandler);
					cmiPrimary.setUserData("primary");
					gridMenu.getItems().add(cmiPrimary);
					
					// add secondary button
					MenuItem cmiSecondary = new MenuItem(gridControl.getSecondaryButton().getText());
					cmiSecondary.setDisable(gridControl.getSecondaryButton().isDisabled());
					cmiSecondary.setOnAction(gridMenuHandler);
					cmiSecondary.setUserData("secondary");
					gridMenu.getItems().add(cmiSecondary);
					
					gridMenu.getItems().add(new SeparatorMenuItem());
					
					// add refresh
					MenuItem cmiRefresh = new MenuItem(gridControl.getRefreshButton().getText());
					cmiRefresh.setDisable(gridControl.getRefreshButton().isDisabled());
					cmiRefresh.setOnAction(gridMenuHandler);
					cmiRefresh.setUserData("refresh");
					gridMenu.getItems().add(cmiRefresh);
					
					gridMenu.getItems().add(new SeparatorMenuItem());
					
					// initialise the menu items
					CheckMenuItem cmiFooter = new CheckMenuItem(Lng._("Show Footer"));
					cmiFooter.setSelected(footer.isVisible());
					cmiFooter.setUserData("showFooter");
					cmiFooter.setOnAction(gridMenuHandler);
					gridMenu.getItems().add(cmiFooter);
					
					// initialise customise submenu
					Menu customise = new Menu(Lng._("Customise"));
					for (TableColumn<B, ?> column  : grid.getColumns()) {
						BOGrid<B>.GridColumn col = (BOGrid<B>.GridColumn)column;
						CheckMenuItem item = new CheckMenuItem(col.getText());
						item.setSelected(col.isVisible());
						item.setUserData("#" + col.getField());
						item.setOnAction(gridMenuHandler);
						customise.getItems().add(item);
					}
					gridMenu.getItems().add(customise);
					
					gridMenu.show(grid, arg0.getScreenX(), arg0.getScreenY());
				}
				grid.setOnMouseClicked(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent arg0) {
						if (gridMenu != null && arg0.getButton() == MouseButton.PRIMARY) {
							gridMenu.hide();
						}
					}					
				});
			}
		});
		
		footer = new GridFooter();
		footer.setVisible(false);	// default set to false?
		
		grid.displayLayout();
		
		setCenter(grid);
		setBottom(footer);
	}
	
	private ContextMenu gridMenu;
	private EventHandler<ActionEvent> gridMenuHandler;
	
	public BOLinkEx<B> getSelectedLink() {
		return getGridControl().getSelectedLink();
	}
	
	public BOGrid<B> getGrid() {
		return grid;
	}
	
	protected void updateLayout() {
		footer.updateLayout();
	}
	
	protected GridFooter getFooter() {
		return footer;
	}
	
	private static final Comparator<GridView<?>.FooterColumn> LAYOUT_COMPARATOR = new Comparator<GridView<?>.FooterColumn>() {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public int compare(GridView<?>.FooterColumn o1, GridView<?>.FooterColumn o2) {
			GridColumn g1 = o1.getColumn();
			GridColumn g2 = o2.getColumn();
			
			if (g1.isVisible() && g2.isVisible()) {
				return g1.getTableView().getVisibleLeafIndex((TableColumn<?, ?>) g1) -
						g2.getTableView().getVisibleLeafIndex((TableColumn<?, ?>) g2);
			} else if (g1.isVisible()) {
				return -1;	// g2 is invisible
			} else if (g2.isVisible()) {
				return 1;	// g1 is visible
			} else {
				return 0;	// considered the same if both are invisible
			}
		}
		
	};
	
	// the footer may be several layers deep.
	// there can be a field for everything.
	protected class GridFooter extends Pane implements Disposable{
		List<FooterColumn> columns;
		
		@SuppressWarnings("unchecked")
		GridFooter() {
			columns = new ArrayList<>();
			for (TableColumn<B, ?> column : grid.getColumns()) {
				BOGrid<B>.GridColumn col = (BOGrid<B>.GridColumn)column;
				columns.add(new FooterColumn(col.getField()));
			}
			
			getChildren().addAll(columns);
			visibleProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> arg0,
						Boolean arg1, Boolean arg2) {
					GridView.this.requestLayout();
				}
			});
		}
		
		private void updateLayout() {
			requestLayout();
		}
		
		protected FooterColumn getColumnForField(String field) {
			for (FooterColumn col : columns) {
				if (col.field.equals(field)) {
					return col;
				}
			}
			return null;
		}
		
		@Override
		protected void layoutChildren() {
			Collections.sort(columns, LAYOUT_COMPARATOR);
			double currentStart = 0;
			double height = getHeight();
			for (FooterColumn column : columns) {
				BOGrid<B>.GridColumn col = column.getColumn();
				column.setVisible(col.isVisible());
				if(column.isVisible()) {
					double width = col.getWidth();
					layoutInArea(column, currentStart, 0, width, height, 0, HPos.LEFT, VPos.TOP);
					currentStart += width;
				}
			}
		}
		
		@Override
		protected double computePrefWidth(double height) {
			return grid.getWidth();
		}
		
		@Override
		protected double computeMinHeight(double width) {
			if (isVisible()) {
				double min = 25;
				for (FooterColumn column : columns) {
					min = Math.max(min, column.prefHeight(-1));
				}
				return min;
			} else {
				return 0;
			}
		}
		
		@Override
		protected double computePrefHeight(double width) {
			return computeMinHeight(width);
		}
		
		protected double computeMaxHeight(double width) {
			return computeMinWidth(-1);
		}

		@Override
		public void dispose() {
			for (FooterColumn col : columns) {
				col.dispose();
			}
			columns.clear();
		}
	}
	
	// all
	private static final int FOOTER_COUNT = 0;
	// datetime and numeric
	private static final int FOOTER_MIN = 1;
	private static final int FOOTER_MAX = 2;
	// numeric
	private static final int FOOTER_SUM = 3;
	private static final int FOOTER_AVERAGE = 4;
	
	private static final String[] FOOTER_DISPLAY =
		{"Count", "Minimum", "Maximum", "Sum", "Average"};
	private static final String[] FOOTER_DISPLAY_SYMBOL = 
		{"#", "⌋", "⌈", "Σ", "μ"};
	
	
	class FooterColumn extends VBox implements EventHandler<ContextMenuEvent>, Disposable {
		String field;
		List<FooterColumnRow> rows;
		EventHandler<ActionEvent> menuHandler;
		ContextMenu menu;
		int menuRow;
		
		FooterColumn(String field) {
			this.field = field;
			setPadding(new Insets(4, 1, 4, 1));
			setOnContextMenuRequested(this);
			rows = new LinkedList<>();
		}
		
		protected BOGrid<B>.GridColumn getColumn() {
			return grid.getColumnByField(field);
		}

		@Override
		public void handle(ContextMenuEvent arg0) {
			if (menuHandler == null) {
				menuHandler = new EventHandler<ActionEvent>() {
					public void handle(ActionEvent e) {
						MenuItem item = (MenuItem)e.getSource();
						if (item.getUserData().equals("hide")) {
							FxUtils.setVisibleAndManaged(footer, false);
						} else if (item.getUserData().equals("copy")) {
							FooterColumnRow row = rows.get(menuRow);
							row.field.selectAll();
							row.field.copy();
						} else {
							int mode = (Integer)item.getUserData();
							if (footerRowExists(mode)) {
								FooterColumnRow row = getFooterRow(mode);
								getChildren().remove(row.controlCell);
								rows.remove(row);
								row.dispose();
							} else {	// not selected
								ensureFooterColumnRowExists(mode);
							}
							if (menu != null) {
								menu.hide();
							}
						}
					}					
				};
			}
			
			if (menu != null) {
				menu.hide();
			}
			
			List<Integer> values = new ArrayList<Integer>();
			
			// populate possible items
			values.add(FOOTER_COUNT);
			
			AttributeType type = getColumn().getAttributeType();
			if (type.isDateTime() || type.isNumeric()) {
				values.add(FOOTER_MIN);
				values.add(FOOTER_MAX);
			}
			
			if (type.isNumeric()) {
				values.add(FOOTER_SUM);
				values.add(FOOTER_AVERAGE);
			}
			
			// rebuild every time
			menu = new ContextMenu();
			menu.setAutoHide(true);
			menu.setOnHidden(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent arg0) {
					menu = null;
				}				
			});
			for (Integer mode : values) {
				CheckMenuItem item = new CheckMenuItem(FOOTER_DISPLAY_SYMBOL[mode] + 
						" - " + Lng._(FOOTER_DISPLAY[mode]));
				item.setOnAction(menuHandler);
				item.setUserData(mode);
				item.setSelected(footerRowExists(mode));
				menu.getItems().add(item);
			}
			
			// add text control items if needed
			double h = 0;
			menuRow = -1;
			for (int i = 0; i < rows.size(); i++) {
				FooterColumnRow row = rows.get(i);
				h += row.controlCell.getHeight();
				if (arg0.getY() <= h) {
					menuRow = i;
				}
			}
			
			if (menuRow >= 0) {
				menu.getItems().add(new SeparatorMenuItem());
				MenuItem copy = new MenuItem(Lng._("Copy"));
				copy.setOnAction(menuHandler);
				copy.setUserData("copy");
				menu.getItems().add(copy);
			}
			
			// add hide footer
			menu.getItems().add(new SeparatorMenuItem());
			MenuItem hideFooter = new MenuItem(Lng._("Hide Footer"));
			hideFooter.setOnAction(menuHandler);
			hideFooter.setUserData("hide");
			menu.getItems().add(hideFooter);
			
			menu.show(FooterColumn.this, arg0.getScreenX(), arg0.getScreenY());
		}
		
		protected void ensureFooterColumnRowExists(int mode) {
			if (footerRowExists(mode)) {
				return;	// do nothing
			}
			FooterColumnRow row = new FooterColumnRow(mode, FooterColumn.this);
			rows.add(row);
			getChildren().add(row.controlCell);
		}
		
		protected FooterColumnRow getFooterRow(int mode) {
			for (FooterColumnRow row : rows) {
				if (row.footerType == mode) {
					return row;
				}
			}
			return null;
		}
		
		protected boolean footerRowExists(int mode) {
			return getFooterRow(mode) != null;
		}

		@Override
		public void dispose() {
			for (FooterColumnRow row : rows) {
				row.dispose();
			}
			rows.clear();
			menuHandler = null;
			menu = null;
		}
	}
	
	class FooterColumnRow implements Disposable, ModifiedEventListener{
		AlignedControlCell controlCell;
		int footerType;
		FooterColumn owner;
		BOTextField field;
		BOAttribute<?> value;
		BOLinkEx<BOAttribute<?>> link;
		
		FooterColumnRow(int mode, FooterColumn column) {
			footerType = mode;
			owner = column;
			
			AttributeType type;
			if (mode == FOOTER_COUNT) {
				type = AttributeType.Integer;
			} else {
				type = column.getColumn().getAttributeType();
			}
			value = new BOAttribute<>(null, "", type);
			link = new BOLinkEx<>();
			link.setLinkedObject(value);			
			field = new BOTextField(link, "");
			field.setEditable(false);
			field.setFocusTraversable(false);
			field.setTooltip(new Tooltip(FOOTER_DISPLAY_SYMBOL[mode] + " - " + Lng._(FOOTER_DISPLAY[mode])));
			field.dataBindingProperty().buildAttributeLinks();
			
			controlCell = new AlignedControlCell(Lng._(FOOTER_DISPLAY_SYMBOL[mode]), field, column);
			controlCell.setOnContextMenuRequested(column);
			field.setOnContextMenuRequested(column);
		
			handleModified(null);
			grid.getLink().addListener(this);
			
			field.setAllowContextMenu(false);
		}

		@Override
		public void dispose() {
			grid.getLink().removeListener(this);
			link.dispose();
			value.dispose();
			field.dataBindingProperty().buildAttributeLinks();
			
			owner = null;
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void handleModified(ModifiedEvent event) {
			BOSet<B> set = grid.getSourceSet();
			if (set == null) {
				value.clear();
			} else {
				if (value.getAttributeType().equals(AttributeType.Unknown)) {
					// update the attribute type
					if (footerType == FOOTER_COUNT) {
						value.setAttributeType(AttributeType.Integer);
					} else {
						value.setAttributeType(owner.getColumn().getAttributeType());
					}
				}
				
				if (footerType == FOOTER_COUNT) {
					value.setAsObject(set.getActiveCount());
				} else {
					Object o = null;
					
					for (B record : set) {
						BOAttribute<?> attr = record.findAttributeByPath(owner.field);
						Object val = attr.getValue();	
						if (val != null) {	// ignore values with null values?
							if (o == null) {
								o = val;
							} else if (footerType == FOOTER_SUM || footerType == FOOTER_AVERAGE) {
								o = ((Number)val).doubleValue() + ((Number)o).doubleValue();
							} else if (footerType == FOOTER_MIN || footerType == FOOTER_MAX) {
								int comp = ((Comparable)o).compareTo((Comparable)val);
								if (footerType == FOOTER_MAX && comp < 0) {
									o = val;
								} else if (footerType == FOOTER_MIN && comp  > 0) {
									o = val;
								}
							}
						}
					}
					
					if (o != null) {
						if (footerType == FOOTER_AVERAGE) {
							o = ((Number)o).doubleValue() / set.getActiveCount();
						}
					}
					
					value.setAsObject(o);
				}
			}
		}	
	}

	@Override
	public void dispose() {
		grid.dispose();
		footer.dispose();
		gridControl.dispose();
		
		grid = null;
		footer = null;
		gridControl = null;
		gridMenuHandler = null;
		gridMenu = null;
		
		getChildren().clear();
	}

	public void refreshGrid() {
		grid.refresh();
	}

	/**
	 * Equivalent to calling getGrid().setEditable(editable);
	 */
	public void setGridEditable(boolean editable) {
		grid.setEditable(true);
	}
	
	/**
	 * Equivalent to calling getGrid().getEditable();
	 */
	public boolean isGridEditable() {
		return grid.isEditable();
	}

	private BOGridControl<B> gridControl;
	public BOGridControl<B> getGridControl() {
		return gridControl;
	}
}
