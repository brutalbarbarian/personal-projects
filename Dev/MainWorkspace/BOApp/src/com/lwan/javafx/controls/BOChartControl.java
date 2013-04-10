package com.lwan.javafx.controls;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import com.lwan.bo.Attribute;
import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.State;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.scene.control.SimpleTextInputDialog;
import com.lwan.util.CollectionUtil;
import com.lwan.util.GenericsUtil;
import com.lwan.util.JavaFXUtil;
import com.lwan.util.wrappers.Procedure;
import com.sun.javafx.collections.ObservableListWrapper;

/**
 * Takes a set, allows the creation of barGraph, lineGraph, pieGraph
 * Can store these views, against a keyword upon construction
 * 
 * Effectively this is a simplistic report that can be used with any data
 * set
 * 
 * 
 * @author Lu
 *
 */
public class BOChartControl <T extends BusinessObject> {
	private static final String CSS_TOOLBAR = "chartcontrol-toolbar";
	private static final String CSS_HBOX = "chartcontrol-hbox";
	private static final String CSS_VBOX = "chartcontrol-vbox";
	private static final String CSS_ROOTPANE = "chartcontrol-rootpane";
	private static final String CSS_OPTIONPANE = "chartcontrol-optionpane";
	
	private class ChartTemplate {
		private HashMap<String, String> properties;
		private String name;
		private int type;
		
		ChartTemplate(){
			properties = new HashMap<>();
		}
		
		public ChartTemplate(ChartTemplate other) {
			this();
			
			name = other.name;
			type = other.type;			
			properties.putAll(other.properties);
		}

		String toStoredString() {
			return "";	// TODO
		}
	}
	
	private String key;
	private BOSet<T> set;
	private List<ChartTemplate> templates;
	private List<Attribute> attributes;
	private ChartTemplate currentTemplate;
	
	public BOChartControl (BOSet<T> set, String key, Collection<Attribute> attributes) {
		this.set = set;
		this.key = key;
		
		readTemplates();
		
		this.attributes = new Vector<>(attributes.size());
		this.attributes.addAll(attributes);
	}
	
	protected void readTemplates() {
		String[] keyTemplates = GenericsUtil.Coalice(App.getKey(key), "").split("%");
		templates = new Vector<>();
		try {
			for (String str : keyTemplates) {
				// attempt to decode...
				if (str.isEmpty()) {
					continue;	// ignore empty string
				}
				ChartTemplate template = new ChartTemplate();
				String[] values = str.split(";");
				template.type = Integer.parseInt(values[0]);
				template.name = values[1];
				for (int i = 2; i < values.length; i++) {
					String[] property = values[i].split(":");
					template.properties.put(property[0], property[1]);
				}
				
				templates.add(template);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error parsing templates for key: " + key, e);
		}
	}
	
	protected void saveTemplates() {
		StringBuilder str = new StringBuilder();
		Iterator<ChartTemplate> it = templates.iterator();
		while(it.hasNext()) {
			str.append(it.next().toStoredString());
			if (it.hasNext()) {
				str.append("%");
			}
		}
		App.putKey(key, str.toString());
	}
	
	private Stage stage;	// will only be not null if shown
	
	protected static final int SCENE_OPTION = 0;
	protected static final int SCENE_DISPLAY = 1;
	
	protected static final String [] GRAPH_TYPES = {
		"Templates", "Pie", "Line", "Bar", "XY"
	};
	
	public static final int GRAPH_TEMPLATE = 0;
	public static final int GRAPH_PIE = 1;
	public static final int GRAPH_LINE = 2;
	public static final int GRAPH_BAR = 3;
	public static final int GRAPH_XY = 4;
	
	private List<String> extraStyleSheets;
	
	public void show(Window owner) {
		if (stage != null) {
			throw new RuntimeException("Cannot call show for a " +
					"BOChartControl that is already shown");
		}
		
		// construct the stage
		stage = new Stage(StageStyle.UTILITY);
		stage.initOwner(owner);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setOnHiding(new EventHandler<WindowEvent>(){
			public void handle(WindowEvent arg0) {
				saveTemplates();
				stage = null;
				extraStyleSheets = null;
			}
		});
		
		extraStyleSheets = new Vector<>();
		extraStyleSheets.addAll(owner.getScene().getStylesheets());
		
		// initialize scene
		initScene(SCENE_OPTION);

		stage.show();
	}
	
	protected void initDisplayScreen() {
		initScene(SCENE_DISPLAY);
	}
	
	protected void initScene(int scene) {
		if (stage == null) {
			throw new RuntimeException("initScene() called for " +
					"a BOChartControl that is not being shown");
		}
		
		// TODO at some point...
		// if there already is a scene... hide the existing scene				
		// after previous scene is hidden (if it is hidden), display a loading screen
		// finally display the new scene

		final BorderPane root = new BorderPane();
		root.getStyleClass().add(CSS_ROOTPANE);
		if (scene == SCENE_OPTION) {			
			ListView<String> list = new ListView<>(
					CollectionUtil.asObservableList(GRAPH_TYPES));			
			list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

			list.getSelectionModel().selectedIndexProperty().addListener(
					new ChangeListener<Number>(){
						public void changed(
								ObservableValue<? extends Number> arg0,
								Number arg1, Number arg2) {
							int index = arg2.intValue();
							Node centre = null;
							// reset template
							currentTemplate = new ChartTemplate();
							currentTemplate.type = index;

							// sets different centre depending on what the
							// index is
							switch (index) {
							case GRAPH_TEMPLATE: 
								centre = new TemplateScreen();
								break;
							case GRAPH_PIE:
								centre = new PieScreen();
								break;		
							case GRAPH_LINE:

								break;
							case GRAPH_BAR:

								break;
							case GRAPH_XY:

								break;
							default:
								throw new RuntimeException("Unknown graph mode selected");	
							}

							root.setCenter(centre);							
						}				
					});

			Pane optionPane = new Pane();
			optionPane.getChildren().add(list);
			optionPane.getStyleClass().add(CSS_OPTIONPANE);
			root.setLeft(optionPane);

			// selects the templates...as templates always the first
			list.getSelectionModel().selectFirst();
			
		} else if (scene == SCENE_DISPLAY) {
			
		}
		
		Scene sc = new Scene(root);
		
		sc.getStylesheets().addAll(extraStyleSheets);
		
		stage.setScene(sc);
	}
	
	// All attributes... no grouping TODO
	private static final int MODE_ALL = 0;
	// All attributes... numeric and date attributes are grouped into ranges
	private static final int MODE_DISCRETE = 1;
	// IsNumeric() attributes only.
	private static final int MODE_NUMERIC = 2;
	
	static final String[] ATTRIBUTE_DATE_GROUPS = {"Days", 
		"Weeks", "Fortnights", "Months", "Years", "Decades"};
	private class AttributeSelector extends VBox {		
		Attribute prevAttribute;
		int mode;
		ComboBox<Attribute> cbItems;
		List<Attribute> attrs;
		HBox mainRow;
		State owner;
		
		String extra;
		
		AttributeSelector (State owner, int mode) {			
			this.owner = owner;
			this.mode = mode;
			getStyleClass().add(CSS_VBOX);
			initialise();
		}
		
		void initialise() {
			// TODO
			// create list of attributes
			T sample = set.getExampleChild();
			attrs = new Vector<>();
			for (Attribute attr : attributes) {
				if (	(mode == MODE_ALL) ||
						(mode == MODE_DISCRETE) ||
						(mode == MODE_NUMERIC && 
						attr.getValuePath(sample).getAttributeType().isNumeric())) {
					attrs.add(attr);
				}
			}
			
			cbItems = new ComboBox<>();
			cbItems.addAllItems(attrs);
			
			cbItems.selectedProperty().addListener(new ChangeListener<Attribute>(){
				public void changed(ObservableValue<? extends Attribute> arg0,
						Attribute arg1, Attribute arg2) {
					displayState();	
					prevAttribute = arg2;
				}
				
			});
			
			mainRow = new HBox();
			cbItems.setMaxWidth(Double.MAX_VALUE);
			Label attrLabel = new Label(Lng._("Attribute:"));
			mainRow.getStyleClass().add(CSS_HBOX);
			
			mainRow.getChildren().addAll(attrLabel, cbItems);
			
			getChildren().add(mainRow);
			
			setFillWidth(true);			
		}		
		
		void displayState() {
			T sample = set.getExampleChild();
			Attribute selected = cbItems.getSelected();
			if (selected != null && 
					(prevAttribute == null || prevAttribute != selected)) {
				getChildren().clear();
				getChildren().add(mainRow);
				extra = null;	// remove whatever previous selection there was
				AttributeType type = selected.getValuePath(sample).getAttributeType();
				if (mode == MODE_DISCRETE) {
					if (type == AttributeType.Date) {
						ComboBox<Integer> cbDate = new ComboBox<>();
						cbDate.addAllItems(
								CollectionUtil.getIndexArray(ATTRIBUTE_DATE_GROUPS.length), 
								LngUtil.translate(ATTRIBUTE_DATE_GROUPS));
						cbDate.selectedProperty().addListener(new ChangeListener<Integer>(){
							public void changed(
									ObservableValue<? extends Integer> arg0,
									Integer arg1, Integer arg2) {
								extra = arg2.toString();
							}
						});
						
						cbDate.setSelected(0);
						
						HBox dateRow = new HBox();
						dateRow.getStyleClass().add(CSS_HBOX);
						dateRow.getChildren().addAll(new Label(Lng._("Group by:")), cbDate);
						
						getChildren().add(dateRow);
					} else if (type.isNumeric()) {
						// increments of
					} else {
						extra = "";	// no need for extras...
					}
				}
			}
			
			owner.displayState();
		}
		
		boolean validSelected() {
			Attribute selected = cbItems.getSelected();
			boolean result = selected != null;
			if (result && mode == MODE_DISCRETE) {
				result = extra != null;
			}
			
			return result;
		}
	}
	
	private class PieScreen extends BorderPane implements State{
		Button btnShow, btnSave, btnCancel;
		AttributeSelector discrete, numeric;
		
		PieScreen() {
			discrete = new AttributeSelector(this, MODE_DISCRETE);
			numeric = new AttributeSelector(this, MODE_NUMERIC);
					
			btnShow = new Button(Lng._("Show"));
			btnSave = new Button(Lng._("Save as Template"));
			btnCancel = new Button(Lng._("Cancel"));
			
			btnShow.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			btnSave.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent arg0) {
					SimpleTextInputDialog dialog = 
							new SimpleTextInputDialog(Lng._("Chart Name"), Lng._("Please select a chart name: "));
					dialog.show(stage, new Procedure<SimpleTextInputDialog>() {
						public void call(SimpleTextInputDialog result) {
							if (result.resultProperty().get() == SimpleTextInputDialog.ResultOK) {
								String name = result.textProperty().getValue().trim();
								// check that the name isn't empty
								if (name.isEmpty()) {
									// TODO
									return;
								}
								
								// check the name isn't already in use...
								for (ChartTemplate template : templates) {
									if (template.name.equalsIgnoreCase(name)) {
										// TODO
										return;
									}
								}								 

								currentTemplate.name = result.textProperty().getValue();
								templates.add(new ChartTemplate(currentTemplate));
							}
						}
					});
					
				}
			});
			
			btnCancel.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					stage.hide();
				}
			});

			VBox centre = new VBox();
			centre.getStyleClass().add(CSS_VBOX);
			centre.setFillWidth(true);
			centre.getChildren().addAll(discrete, numeric);			
			
			HBox toolbar = new HBox();
			toolbar.setAlignment(Pos.CENTER_RIGHT);
			toolbar.getChildren().addAll(btnShow, btnSave);
			toolbar.getStyleClass().add(CSS_TOOLBAR);
			
			setCenter(centre);
			setBottom(toolbar);
		}
		
		public void displayState() {
			// check that a valid option is selected
			boolean validSelected = discrete.validSelected() && numeric.validSelected();
			btnShow.setDisable(!validSelected);
			btnSave.setDisable(!validSelected);
		}
	}
	
	private class TemplateScreen extends BorderPane{
		ListView<String> listTypes;
		ListView<String> listTemplates;
		Button btnDelete, btnCreate;
		
		TemplateScreen(){
			// multi-list of pre-defined templates
			// sorted by type -> name								
			String[] templateTypes = Arrays.copyOf(GRAPH_TYPES, GRAPH_TYPES.length);
			templateTypes[0] = "<All>";	// replace template with all
			listTypes = new ListView<>(CollectionUtil.asObservableList(
					LngUtil.translate(templateTypes)));
			listTypes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

			listTemplates = new ListView<>();
			listTemplates.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

			HBox pane = new HBox();
			pane.getStyleClass().add(CSS_HBOX);
			pane.getChildren().addAll(listTypes, listTemplates);

			btnDelete = new Button(Lng._("Delete Template"));
			btnCreate = new Button(Lng._("Create Graph"));

			btnDelete.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent arg0) {
					String name = listTemplates.getSelectionModel().getSelectedItem();
					Iterator<ChartTemplate> it = templates.iterator();
					while (it.hasNext()) {
						if (it.next().name.equals(name)) {
							it.remove();
							break;
						}
					}
					// remove the selected item from the list too
					populateList(listTypes.getSelectionModel().getSelectedIndex());
				}									
			});
			btnCreate.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent arg0) {
					currentTemplate = null;
					for (ChartTemplate template : templates) {
						if (template.name.equals(listTemplates.getSelectionModel().getSelectedItem())) {
							currentTemplate = template;
							break;
						}
					}
					if (currentTemplate == null) {
						JavaFXUtil.ShowErrorDialog(stage, Lng._("No template selected."));
					}
					//										 
					initScene(SCENE_DISPLAY);
				}									
			});

			listTypes.getSelectionModel().selectedIndexProperty().addListener(
					new ChangeListener<Number>(){
						public void changed(ObservableValue<? extends Number> arg0,
								Number arg1, Number arg2) {
							int index = arg2.intValue();

							populateList(index);
						}									
					});

			HBox toolbar = new HBox();
			toolbar.getStyleClass().add(CSS_TOOLBAR);
			toolbar.setAlignment(Pos.CENTER_RIGHT);
			toolbar.getChildren().addAll(btnDelete, btnCreate);			

			setCenter(pane);
			setBottom(toolbar);

			// select <All> by default
			listTypes.getSelectionModel().selectFirst();
		}
		
		void populateList(int index) {
			List<String> avaliableTemplates = new Vector<>();
			for (ChartTemplate template : templates) {
				if (index == 0 || index == template.type) {
					avaliableTemplates.add(template.name);
				}
			}

			listTemplates.setItems(new ObservableListWrapper<>(
					avaliableTemplates));
			listTemplates.getSelectionModel().selectFirst();	// always select first after

			btnCreate.setDisable(avaliableTemplates.isEmpty());
			btnDelete.setDisable(avaliableTemplates.isEmpty());
		}
	}
}
