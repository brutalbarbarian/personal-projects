package com.lwan.javafx.controls.other;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChartBuilder;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import com.lwan.bo.Attribute;
import com.lwan.bo.AttributeType;
import com.lwan.bo.BOAttribute;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.BusinessObject;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.State;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.ComboBox;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.javafx.scene.control.DateAxis;
import com.lwan.javafx.scene.control.SimpleTextInputDialog;
import com.lwan.util.CollectionUtil;
import com.lwan.util.GenericsUtil;
import com.lwan.util.FxUtils;
import com.lwan.util.StringUtil;
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
			StringBuffer sb = new StringBuffer();
			sb.append(type).append(';').append(name).append(';');
			boolean first = true;
			for (Entry<String, String> property : properties.entrySet()) {
				if (!first) {
					sb.append(';');					
				}
				sb.append(property.getKey()).append(':').append(property.getValue());
				first = false;
			}
			
			
			return sb.toString();
		}
	}
	
	private String key;
	private BOSet<T> set;
	private List<ChartTemplate> templates;
	private List<Attribute> attributes;
	private ChartTemplate currentTemplate;
	
	private static Attribute countAttribute, emptyAttribute;
	private static final String COUNT_ATTRIBUTE = "!count";
	private static final String EMPTY_ATTRIBUTE = "!empty";
	
	protected static final Attribute countAttribute() {
		if (countAttribute == null) {
			countAttribute = new Attribute(COUNT_ATTRIBUTE, Lng._("Count"));
		}
		return countAttribute;
	}
	protected static final Attribute emptyAttribute() {
		if (emptyAttribute == null) {
			emptyAttribute = new Attribute(EMPTY_ATTRIBUTE, Lng._("All"));
		}
		return emptyAttribute;
	}
	
	Attribute findAttribute(String valuePath) {
		if (valuePath.equals(COUNT_ATTRIBUTE)) {
			return countAttribute();
		} else if (valuePath.equals(EMPTY_ATTRIBUTE)) {
			return emptyAttribute();
		}
		
		for (Attribute attr : attributes) {
			if (attr.getValuePath().equals(valuePath)) {
				return attr;
			}
		}		
		return null;
	}
	
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
		"Templates", "Pie", "Line", "Bar", "Scatter"
	};
	
	public static final int GRAPH_TEMPLATE = 0;
	public static final int GRAPH_PIE = 1;
	public static final int GRAPH_LINE = 2;
	public static final int GRAPH_BAR = 3;
	public static final int GRAPH_SCATTER = 4;
	
	static final String GROUP_KEY = "GROUP";
	static final String VALUE_KEY = "VALUE";
	static final String X_KEY = "X_AXIS";
	static final String Y_KEY = "Y_AXIS";
	
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
							case GRAPH_SCATTER:
								centre = new XYScreen();
								break;
							case GRAPH_BAR:
								centre = new BarScreen();
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
			Node centre = null;
			switch (currentTemplate.type) {
			case GRAPH_PIE:
				centre = new PieGraph();
				break;		
			case GRAPH_LINE:
				centre = new LineGraph();
				break;
			case GRAPH_BAR:
				centre = new BarGraph();
				break;
			case GRAPH_SCATTER:
				centre = new ScatterGraph();
				break;
			default:
				throw new RuntimeException("Unknown graph mode selected");	
			}
			
			root.setCenter(centre);
		}
		
		Scene sc = new Scene(root);
		
		sc.getStylesheets().addAll(extraStyleSheets);
		
		stage.setScene(sc);
	}
	
	class LineGraph extends BasicXYGraph{
		XYChart<?, ?> getChart(Axis<?> xAxis, Axis<?> yAxis) {
			return new LineChart<>(xAxis, yAxis);
		}

		boolean xIsDiscrete() {
			return false;
		}
	}
	
	class BarGraph extends BasicXYGraph{
		XYChart<?, ?> getChart(Axis<?> xAxis, Axis<?> yAxis) {
			return new BarChart<>(xAxis, yAxis);
		}

		@Override
		boolean xIsDiscrete() {
			return true;
		}		
	}
	
	class ScatterGraph extends BasicXYGraph{
		XYChart<?, ?> getChart(Axis<?> xAxis, Axis<?> yAxis) {
			return new ScatterChart<>(xAxis, yAxis);
		}

		@Override
		boolean xIsDiscrete() {
			return false;
		}		
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	abstract class BasicXYGraph extends GraphBase {
		abstract XYChart<?, ?> getChart(Axis<?> xAxis, Axis<?> yAxis);
		abstract boolean xIsDiscrete();
		
		Chart initChart() {
			// fetch the attributes
			String[] xStrs = currentTemplate.properties.get(X_KEY).split("#");
			Attribute xAttr = findAttribute(xStrs[0]);
			
			String[] yStrs = currentTemplate.properties.get(Y_KEY).split("#");
			Attribute yAttr = findAttribute(yStrs[0]);
			
			String[] groupStrs = currentTemplate.properties.get(GROUP_KEY).split("#");
			Attribute groupAttr = findAttribute(groupStrs[0]);
			
			// need to find the min and max of x and y... so we construct data first
			Comparable<?> minX = null, maxX = null;
			Number minY = null, maxY = null;
			
			HashMap<Object, HashMap<Comparable, Number>> valueLines = new HashMap<>();
			HashMap<Comparable, String> displayValues = new HashMap<>();
			HashMap<Object, String> seriesDisplay = new HashMap<>();
			
			DisplayRecord record = new DisplayRecord();
			for (T child : set) {
				Object group;
				String displayLabel;
				if (groupAttr == emptyAttribute) {
					group = null;
					displayLabel = Lng._("All");
				} else {
					decodeDiscrete(groupAttr, groupStrs, child, record);
					group = record.hashValue;
					displayLabel = record.displayValue;
				}
				
				if (!seriesDisplay.containsKey(group)) {
					seriesDisplay.put(group, displayLabel);
				}
				decodeDiscrete(xAttr, xStrs, child, record);
				Comparable xValue = (Comparable)record.hashValue;
				displayValues.put(xValue, record.displayValue);
				Number yValue = (Number)yAttr.getValueAttribute(child).getValue();

				HashMap<Comparable, Number> series = valueLines.get(group);				
				if (series == null) {
					series = new HashMap<Comparable, Number>();
					valueLines.put(group, series);
				}
				
				Number num = GenericsUtil.Coalice(series.get(xValue), 0);
				
				series.put(xValue, num.doubleValue() + yValue.doubleValue());
				
				minX = minX == null? xValue : xValue.compareTo(minX) < 0 ? xValue : minX;				
				maxX = maxX == null? xValue : xValue.compareTo(maxX) > 0 ? xValue : maxX;
				minY = minY == null? yValue : yValue.doubleValue() < minY.doubleValue() ? yValue : minY;
				maxY = maxY == null? yValue : yValue.doubleValue() > maxY.doubleValue() ? yValue : maxY;
			}

			Axis<?>	yAxis = new NumberAxis();
			yAxis.setLabel(yAttr.getDisplayName());
			
			Axis<?> xAxis;
			if (xIsDiscrete()) {
				xAxis = new CategoryAxis();
			} else {
				AttributeType xType = getAttributeType(xAttr);
				if (xType.isDateTime()) {
					xAxis = new DateAxis(Calendar.DATE, (Date)minX, (Date)maxX, App.getLocale());
					xAxis.setTickLabelRotation(90);
				} else { // xType.isNumeric()
					xAxis = new NumberAxis();
				}
			}
			xAxis.setLabel(xAttr.getDisplayName());
			
		
			XYChart lineChart = getChart(xAxis, yAxis); 
			String title;
			if (StringUtil.isNullOrBlank(currentTemplate.name)) {
				title = currentTemplate.name;
			} else {
				title = "";
			}
			lineChart.setTitle(title);
			
			List<Series> data = new Vector<>();
			for(Object key : seriesDisplay.keySet()) {
				HashMap<Comparable, Number> mapSeries = valueLines.get(key);
				List dataList = new Vector<>();	// null;	// valueLines.get(key);
				for (Entry<Comparable, Number> entry : mapSeries.entrySet()) {
					dataList.add(new XYChart.Data(entry.getKey(), entry.getValue()));
				}
				
				Collections.sort(dataList, new Comparator(){
					public int compare(Object o1, Object o2) {
						javafx.scene.chart.XYChart.Data d1 = (javafx.scene.chart.XYChart.Data)o1;
						javafx.scene.chart.XYChart.Data d2 = (javafx.scene.chart.XYChart.Data)o2;
						Comparable c1 =  (Comparable)d1.getXValue();
						Comparable c2 = (Comparable)d2.getXValue();
												
						return c1.compareTo(c2);
					}					
				});
				
				
				ObservableList<XYChart.Data> list;
				if (xIsDiscrete()) {
					List<XYChart.Data> tmpList = new Vector<>();
					for (Object obj : dataList) {
						XYChart.Data d = ((XYChart.Data)obj);
						tmpList.add(new XYChart.Data(displayValues.get(d.getXValue()), d.getYValue()));
					}
					
					list = new ObservableListWrapper(tmpList);
				} else {
					list = new ObservableListWrapper(dataList);	
				}
				
				
				Series series = new Series(seriesDisplay.get(key), list);
				data.add(series);
			}
			lineChart.getData().addAll(data);
			
			return lineChart;
		}		
	}
	
	abstract class GraphBase extends Region {
		Chart chart;
		
		GraphBase() {
			chart = initChart();
			
			getChildren().add(chart);
		}
		
		protected void layoutChildren() {
			layoutInArea(chart, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, VPos.CENTER);
		}
		
		void decodeDiscrete(Attribute attr, String[] params, T child, DisplayRecord result) {
			AttributeType type = getAttributeType(attr);
			Object value = attr.getValueAttribute(child).getValue();
			if (type == AttributeType.Date) {
				Date date = (Date)value;
				Date startDate = null;
				Calendar calendar = Calendar.getInstance(App.getLocale());
				calendar.setTime(date);
				
				DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, App.getLocale());
				
				switch(Integer.parseInt(params[1])) {
				case 0:	// Day
					startDate = date;
					
					result.displayValue = formatter.format(startDate);
					break;
				case 1:	{// Week
					int firstDayOfWeek = calendar.getFirstDayOfWeek();
					int day = calendar.get(Calendar.DAY_OF_WEEK);
					int modify = day - firstDayOfWeek;
					
					calendar.add(Calendar.DATE, - modify);
					startDate = calendar.getTime();
					
					result.displayValue = formatter.format(startDate);
					
					break;
				}
				case 2:	{// Month
					int day = calendar.get(Calendar.DAY_OF_MONTH);
					
					calendar.add(Calendar.DATE, 1 - day);
					startDate = calendar.getTime();

					result.displayValue = 
							calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, App.getLocale()) + " - " +
							calendar.get(Calendar.YEAR);
					
					break;
				}
				case 3:	{// Year
					calendar.setTime(date);
					int day = calendar.get(Calendar.DAY_OF_YEAR);
					calendar.add(Calendar.DATE, 1 - day);
					startDate = calendar.getTime();
					
					result.displayValue = Integer.toString(calendar.get(Calendar.YEAR));
					
					break;
				} default :
					throw new RuntimeException("Unknown calendar range setting");
				}
				result.hashValue = startDate;
			} else if (type.isNumeric()) {
				Number min, max;
				Number range;
				Number val = (Number)value;
				
				if (params.length > 0) {
					range = Double.parseDouble(params[1]);
					if (val.doubleValue() >= 0) {
						min = Math.floor(val.doubleValue() / range.doubleValue()) * range.doubleValue();
					} else {
						min = Math.ceil(val.doubleValue() / range.doubleValue()) * range.doubleValue();
					}
					
					max = min.doubleValue() + range.doubleValue();
					
					result.hashValue = (min.doubleValue() + max.doubleValue()) / 2;
					if (type == AttributeType.Integer) {
						result.displayValue = min.intValue() + " - " + max.intValue();
					} else if (type == AttributeType.Double) {
						result.displayValue = min.doubleValue() + " - " + max.doubleValue();
					} else if (type == AttributeType.Currency) {
						result.displayValue = Lng.formatCurrency(min) + " - " + Lng.formatCurrency(max);
					}
				} else {
					result.hashValue = val;
					if (type == AttributeType.Integer) {
						result.displayValue = val.toString();
					} else if (type == AttributeType.Double) {
						result.displayValue = val.toString();
					} else if (type == AttributeType.Currency) {
						result.displayValue = Lng.formatCurrency(val);
					}
				}
			} else {
				result.hashValue = value;
				result.displayValue = attr.getDisplayAttribute(child).asString();
			}
		}
		
		abstract Chart initChart();
	}
	
	class DisplayRecord {
		String displayValue;
		Object hashValue;
	}
	
	class PieGraph extends GraphBase {
		Chart initChart() {			
			String[] groupStrs = currentTemplate.properties.get(GROUP_KEY).split("#");
			Attribute groupAttr = findAttribute(groupStrs[0]);
			
			String[] disStrs = currentTemplate.properties.get(VALUE_KEY).split("#");
			Attribute disAttr = findAttribute(disStrs[0]);
			
			DisplayRecord record = new DisplayRecord();
			
			HashMap<Object, Number> data = new HashMap<>();
			HashMap<Object, String> label = new HashMap<>();
			for (T child : set) {
				decodeDiscrete(groupAttr, groupStrs, child, record);
				Object group = record.hashValue;
				String display = record.displayValue;
				
				Number num;
				if (disAttr == countAttribute) {
					num = 1;
				} else {
					num = (Number)disAttr.getValueAttribute(child).getValue();
				}
				
				Number preNum = GenericsUtil.Coalice(data.get(group), 0);
				data.put(group, preNum.doubleValue() + num.doubleValue());
				
				if (!label.containsKey(group)) {
					label.put(group, display);
				}
			}
			List<Data> pieData = new Vector<>();
			for (Object key : data.keySet()) {
				Data d = new Data(label.get(key), data.get(key).doubleValue());
				pieData.add(d);
			}
			
			Collections.sort(pieData, new Comparator<Data>() {
				public int compare(Data o1, Data o2) {
					if (o2.getPieValue() > o1.getPieValue()) {
						return 1;
					} else if (o2.getPieValue() < o1.getPieValue()) {
						return -1;
					} else {
						return 0;
					}
				}			
			});
			
			String title = StringUtil.isNullOrBlank(currentTemplate.name) ?
					groupAttr.getDisplayName() + " - " + disAttr.getDisplayName() : currentTemplate.name;
		
			return PieChartBuilder.create().data(new ObservableListWrapper<>(pieData)).
					title(title).clockwise(true).build();
		}
	}
	
	boolean attributeIsSpecial(Attribute attr) {
		return 	attr == countAttribute || 
				attr == emptyAttribute;
	}
	
	protected AttributeType getAttributeType(Attribute attr) {
		if (attributeIsSpecial(attr)) {
			return AttributeType.Unknown;
		} else {
			T sample = set.getExampleChild();
			return attr.getValueAttribute(sample).getAttributeType();
		}
	}
	
	// All attributes... no grouping 
//	private static final int MODE_ALL = 0;
	// All attributes... numeric and date attributes are grouped into ranges
	private static final int MODE_DISCRETE = 1;
	// IsNumeric() attributes only.
	private static final int MODE_NUMERIC = 2;
	private static final int MODE_ORDERED = 3;	// Numeric and Date
	private static final int MODE_ORDERED_DISCRETE = 4;	// Same as ordered, but allow user to clump values
	
	static final String[] ATTRIBUTE_DATE_GROUPS = {"Days", 
		"Weeks", "Months", "Years"};
	private class AttributeSelector extends VBox {		
		Attribute prevAttribute;
		int mode;
		ComboBox<Attribute> cbItems;
		List<Attribute> attrs;
		HBox mainRow;
		State owner;
		
		String key;
		String extra;
		String title;
		boolean displayingState;
		boolean includeCount, includeEmpty;
		
		AttributeSelector (State owner, String title, int mode, String key, 
				boolean includeCount, boolean includeEmpty) {			
			this.owner = owner;
			this.mode = mode;
			this.key = key;
			this.title = title;
			this.includeCount = includeCount;
			this.includeEmpty = includeEmpty;
			displayingState = false;
			getStyleClass().add(CSS_VBOX);
			initialise();
		}
		
		boolean attributeIsValid(Attribute attr) {
			T sample = set.getExampleChild();
			AttributeType type = attr.getValueAttribute(sample).getAttributeType();
			switch(mode) {
//			case MODE_ALL :
//				return true;
			case MODE_DISCRETE:
				return true;
			case MODE_NUMERIC:
				return type.isNumeric();
			case MODE_ORDERED:
				return type.isNumeric() || type.isDateTime();
			case MODE_ORDERED_DISCRETE:
				return type.isNumeric() || type.isDateTime();
			default:
				return false;
			}
		}
		
		void initialise() {
			// create list of attributes
			attrs = new Vector<>();
			for (Attribute attr : attributes) {
				if (attributeIsValid(attr)) {
					attrs.add(attr);
				}
			}
			if (includeCount) {
				attrs.add(countAttribute());
			}
			if (includeEmpty) {
				attrs.add(emptyAttribute());
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
			
			cbItems.setMaxWidth(Double.MAX_VALUE);
			mainRow = new AlignedControlCell(title, cbItems, (Parent)owner);
			mainRow.getStyleClass().add(CSS_HBOX);
			
			getChildren().add(mainRow);
			
			setFillWidth(true);			
		}
		
		void displayState() {
			if (!displayingState) try {
				displayingState = true;
				
				T sample = set.getExampleChild();
				Attribute selected = cbItems.getSelected();
				if (selected != null && 
						(prevAttribute == null || prevAttribute != selected)) {
					getChildren().clear();
					getChildren().add(mainRow);
					extra = null;	// remove whatever previous selection there was
					AttributeType type = attributeIsSpecial(selected) ? 
							AttributeType.Unknown : selected.getValueAttribute(sample).getAttributeType();
					if (mode == MODE_DISCRETE || mode == MODE_ORDERED_DISCRETE) {
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
									displayState();
								}
							});
							cbDate.setSelected(0);
							
							HBox dateRow = new AlignedControlCell(Lng._("Group by:"), cbDate, (Parent)owner);
							cbDate.setMaxWidth(Double.MAX_VALUE);
							dateRow.getStyleClass().add(CSS_HBOX);
							
							getChildren().add(dateRow);
						} else if (type.isNumeric()) {
							BOAttribute<Number> dummy = new BOAttribute<>(null, "", type);
							BOLinkEx<BOAttribute<Number>> link = new BOLinkEx<>();
							link.setLinkedObject(dummy);
							BOTextField textField = new BOTextField(link, "");
							HBox.setHgrow(textField, Priority.SOMETIMES);
							if (mode == MODE_ORDERED_DISCRETE) {
								textField.setPromptText("<NONE>");
								extra = "";
							}							
							
							textField.dataBindingProperty().buildAttributeLinks();
							
							dummy.addChangeListener(new ChangeListener<Number>(){
								public void changed(
										ObservableValue<? extends Number> arg0,
										Number arg1, Number arg2) {
									if (arg2.doubleValue() == 0) {
										if (mode == MODE_ORDERED_DISCRETE) {
											extra = "";
										} else {
											extra = null;	
										}										
									} else {
										extra = arg2.toString();	
									}									
									displayState();
								}								
							});
							
							HBox rangeRow = new AlignedControlCell("Range:", textField, (Parent)owner);
							rangeRow.getStyleClass().add(CSS_HBOX);
							
							getChildren().add(rangeRow);
						} else {
							extra = "";	// no need for extras...
						}
					}
				}
				
				// update the key
				currentTemplate.properties.put(key, selected.getValuePath() + "#" + 
							GenericsUtil.Coalice(extra, ""));
				
				owner.displayState();
			} finally {
				displayingState = false;
			}
		}
		
		boolean validSelected() {
			Attribute selected = cbItems.getSelected();
			boolean result = selected != null;
			if (result && requiresExtras()) {
				result = extra != null;
			}
			
			return result;
		}
		
		boolean requiresExtras() {
			return 	mode == MODE_DISCRETE ||
					mode == MODE_ORDERED_DISCRETE;
		}
		
	}
	
	abstract class GraphSetupScreen extends BorderPane implements State {
		Button btnShow, btnSave, btnCancel;
		List<AttributeSelector> selectors;
		
		GraphSetupScreen() {
			selectors = getSelectors();
			
			btnShow = new Button(Lng._("Show"));
			btnSave = new Button(Lng._("Save as Template"));
			btnCancel = new Button(Lng._("Cancel"));
			
			btnShow.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent arg0) {
					// assume that the currenttemplate is valid
					initScene(SCENE_DISPLAY);
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
									FxUtils.ShowErrorDialog(stage, Lng._("Name cannot be empty."));
									return;
								}
								
								// check the name isn't already in use...
								for (ChartTemplate template : templates) {
									if (template.name.equalsIgnoreCase(name)) {
										FxUtils.ShowErrorDialog(
												stage, Lng._("Template %1% already exists.", name));
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
			centre.getChildren().addAll(selectors);									
			
			setCenter(centre);
			
			HBox toolbar = new HBox();
			toolbar.setAlignment(Pos.CENTER_RIGHT);
			toolbar.getChildren().addAll(btnShow, btnSave);
			toolbar.getStyleClass().add(CSS_TOOLBAR);
			
			setBottom(toolbar);
			
			displayState();
		}
		
		public abstract List<AttributeSelector> getSelectors();
		
		public void displayState() {
			// check that a valid option is selected
			boolean validSelected = true;
			for (AttributeSelector selector : selectors) {
				validSelected = selector.validSelected();
				if (!validSelected) break;
			}
			btnShow.setDisable(!validSelected);
			btnSave.setDisable(!validSelected);
		}		
	}
	
	private class PieScreen extends GraphSetupScreen {
		AttributeSelector discrete, numeric;

		@Override
		public List<AttributeSelector> getSelectors() {
			discrete = new AttributeSelector(this, Lng._("Category"), MODE_DISCRETE, GROUP_KEY, false, false);
			numeric = new AttributeSelector(this, Lng._("Series"), MODE_NUMERIC, VALUE_KEY, true, false);
			
			return Arrays.asList(discrete, numeric);
		}
	}
	
	private class BarScreen extends GraphSetupScreen{
		AttributeSelector xAttribute, yAttribute, groupAttribute;
		
		public List<AttributeSelector> getSelectors() {
			xAttribute = new AttributeSelector(this, Lng._("X-Axis"), MODE_DISCRETE, X_KEY, false, false);
			yAttribute = new AttributeSelector(this, Lng._("Y-Axis"), MODE_NUMERIC, Y_KEY, false, false);
			groupAttribute = new AttributeSelector(this, Lng._("Group By"), MODE_DISCRETE, GROUP_KEY, false, true);
//			groupAttribute.setVisible(false);
			
			return Arrays.asList(xAttribute, yAttribute, groupAttribute);
		}
	}
	
	private class XYScreen extends GraphSetupScreen {
		AttributeSelector xAttribute, yAttribute, groupAttribute;
		
		@Override
		public List<AttributeSelector> getSelectors() {
			xAttribute = new AttributeSelector(this, Lng._("X-Axis"), MODE_ORDERED_DISCRETE, X_KEY, false, false);
			yAttribute = new AttributeSelector(this, Lng._("Y-Axis"), MODE_NUMERIC, Y_KEY, false, false);
			groupAttribute = new AttributeSelector(this, Lng._("Group By"), MODE_DISCRETE, GROUP_KEY, false, true);
			
			return Arrays.asList(xAttribute, yAttribute, groupAttribute);
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
			
			final EventHandler<ActionEvent> createEvent = new EventHandler<ActionEvent>(){
				public void handle(ActionEvent arg0) {
					currentTemplate = null;
					for (ChartTemplate template : templates) {
						if (template.name.equals(listTemplates.getSelectionModel().getSelectedItem())) {
							currentTemplate = template;
							break;
						}
					}
					if (currentTemplate == null) {
						FxUtils.ShowErrorDialog(stage, Lng._("No template selected."));
					}
					//										 
					initScene(SCENE_DISPLAY);
				}									
			};
			btnCreate.setOnAction(createEvent);

			listTypes.getSelectionModel().selectedIndexProperty().addListener(
					new ChangeListener<Number>(){
						public void changed(ObservableValue<? extends Number> arg0,
								Number arg1, Number arg2) {
							int index = arg2.intValue();

							populateList(index);
						}									
					});
			listTemplates.setCellFactory(new Callback<ListView<String>, ListCell<String>>(){
				public ListCell<String> call(ListView<String> arg0) {
					ListCell<String> cell = new ListCell<String>() {
						public void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);
							setText(empty ? null : getString());
							setGraphic(null);
						}

						private String getString() {
							return getItem() == null ? "" : getItem().toString();
						}
					};
					
					cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	                    @SuppressWarnings("unchecked")
						@Override
	                    public void handle(MouseEvent event) {
	                        if (event.getClickCount() > 1) {
	                            ListCell<String> c = (ListCell<String>) event.getSource();
	                            if (c.getText() != null) {
	                            	createEvent.handle(null);
	                            }
	                        }
	                    }
	                });
						
					return cell;
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
