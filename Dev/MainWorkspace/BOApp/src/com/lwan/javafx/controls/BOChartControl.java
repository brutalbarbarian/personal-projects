package com.lwan.javafx.controls;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.util.CollectionUtil;
import com.lwan.util.GenericsUtil;
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
	private class ChartTemplate {
		private HashMap<String, String> properties;
		private String name;
		private int type;
		
		ChartTemplate(){
			properties = new HashMap<>();
		}
	}
	
	private List<ChartTemplate> templates;
	
	public BOChartControl (BOSet<T> set, String key) { 
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
	
	public void show(Window owner) {
		if (stage != null) {
			throw new RuntimeException("Cannot call show for a " +
					"BOChartControl that is already shown");
		}
		
		// construct the stage
		stage = new Stage(StageStyle.UTILITY);
		stage.initOwner(owner);
		stage.initModality(Modality.WINDOW_MODAL);
		
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
		
		final GridPane root = new GridPane();
		root.getStyleClass().add("chartcontrol-root");
		if (scene == SCENE_OPTION) {			
			ListView<String> list = new ListView<>(
					CollectionUtil.asObservableList(GRAPH_TYPES));			
			list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			
			final Pane displayRegion = new GridPane();
			
			list.getSelectionModel().selectedIndexProperty().addListener(
					new ChangeListener<Number>(){
						public void changed(
								ObservableValue<? extends Number> arg0,
								Number arg1, Number arg2) {
							int index = arg2.intValue();
							Node centre = null;
							displayRegion.getChildren().clear();
							// sets different centre depending on what the
							// index is
							switch (index) {
							case GRAPH_TEMPLATE:
								// multi-list of pre-defined templates
								// sorted by type -> name								
								String[] templateTypes = Arrays.copyOf(GRAPH_TYPES, GRAPH_TYPES.length);
								templateTypes[0] = "<All>";	// replace template with all
								ListView<String> listTypes = new ListView<>(CollectionUtil.asObservableList(
										LngUtil.translate(templateTypes)));
								listTypes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
								
								final ListView<String> listTemplates = new ListView<>();
								listTemplates.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
								
								
								HBox pane = new HBox(2);
								pane.getChildren().addAll(listTypes, listTemplates);
								
								final Button btnDelete = new Button(Lng._("Delete Template"));
								final Button btnCreate = new Button(Lng._("Create Graph"));
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
										
									}									
								});
								btnCreate.setOnAction(new EventHandler<ActionEvent>(){
									public void handle(ActionEvent arg0) {
										// TODO create graph
									}									
								});
								
								listTypes.getSelectionModel().selectedIndexProperty().addListener(
										new ChangeListener<Number>(){
											public void changed(ObservableValue<? extends Number> arg0,
													Number arg1, Number arg2) {
												int index = arg2.intValue();
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
								});
								
//								listTemplates.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
//									public void changed(ObservableValue<? extends String> arg0,
//											String arg1, String arg2) {
//										btnCreate.setDisable(arg2 == null);
//										btnDelete.setDisable(arg2 == null);
//									}											
//								});

								HBox toolbar = new HBox();
								toolbar.setAlignment(Pos.CENTER_RIGHT);
								toolbar.getChildren().addAll(btnDelete, btnCreate);
								
								BorderPane main = new BorderPane();
								main.setCenter(pane);
								main.setBottom(toolbar);
								
								// select <All> by default
								listTypes.getSelectionModel().selectFirst();
								
								centre = main;
								break;							
							case GRAPH_PIE:
								
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
							displayRegion.getChildren().add(centre);
						}				
					});
						
			root.add(list, 0, 0);
			root.add(displayRegion, 1, 0);
			
			// selects the templates...as templates always the first
			list.getSelectionModel().selectFirst();
			
		} else if (scene == SCENE_DISPLAY) {
			
		}
		
		Scene sc = new Scene(root);
		sc.getStylesheets().add("resource/chartcontrol.css");
		stage.setScene(sc);
	}
}
