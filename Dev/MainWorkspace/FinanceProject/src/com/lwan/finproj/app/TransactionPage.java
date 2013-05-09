package com.lwan.finproj.app;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.lwan.bo.Attribute;
import com.lwan.bo.AttributeType;
import com.lwan.bo.BOLinkEx;
import com.lwan.bo.BOSet;
import com.lwan.bo.db.BODbAttribute;
import com.lwan.bo.db.BODbSetRef;
import com.lwan.finproj.bo.BOSource;
import com.lwan.finproj.bo.BOTransaction;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.app.util.BOCtrlUtil;
import com.lwan.javafx.app.util.DbUtil;
import com.lwan.javafx.app.util.LngUtil;
import com.lwan.javafx.controls.bo.BOChartControl;
import com.lwan.javafx.controls.bo.BOComboBox;
import com.lwan.javafx.controls.bo.BODatePicker;
import com.lwan.javafx.controls.bo.BOGrid;
import com.lwan.javafx.controls.bo.BOGridControl;
import com.lwan.javafx.controls.bo.BOTextField;
import com.lwan.javafx.controls.bo.binding.BoundControl;
import com.lwan.util.wrappers.Disposable;
import com.lwan.util.wrappers.Procedure;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.util.Callback;

public class TransactionPage extends BorderPane implements Disposable{
	BOGrid<BOTransaction> tranGrid;
	BOLinkEx<BOSet<BOTransaction>> gridLink;
	BOTransactionSetRef gridSetRef;
	BOGridControl<BOTransaction> gridCtrl;
	BOLinkEx<BOTransaction> record; 
	
	BOComboBox<BOSource> param_src;
	BODatePicker param_minDate, param_maxDate;
	BOTextField param_minAmount, param_maxAmount;
	ToolBar paramBar;
	
	GridPane grid;
	BOTextField notes, amount;
	BODatePicker date;
	BOComboBox<Integer> name;
	
	Procedure<BoundControl<?>> initSource;
	
	ToolBar bottomBar;
	
	protected TransactionPage() {		
		initControls();		
		tranGrid.refresh();
	}
	
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	@Override
	public void dispose() {
		gridLink.dispose();
		gridSetRef.dispose();
		record.dispose();
		BOCtrlUtil.buildAttributeLinks(paramBar);
		BOCtrlUtil.buildAttributeLinks(grid);
		tranGrid.refresh();	// remove?
		tranGrid.dispose();
	}
	
	protected void initControls() {
		// init grid
		gridLink = new BOLinkEx<>();
		tranGrid = new BOGrid<>("TransactionPageTranGrid", gridLink, 
				LngUtil.translateArray(new String[]{"TransactionAmount",
				"TransactionNotes", "TransactionDate", "SourceName"}), 
				new String[]{"TransactionAmount", "TransactionNotes", "TransactionDate", 
				"SourceID"}, new boolean[]{true, true, true, true});
		tranGrid.setEditable(true);
		
		gridSetRef = new BOTransactionSetRef();
		gridLink.setLinkedObject(gridSetRef);
		gridSetRef.ensureActive();
	
		gridCtrl = new BOGridControl<>(tranGrid);
		gridCtrl.setHotkeyControls(this);
		record = gridCtrl.getSelectedLink();
		
		// init param fields
		param_src = new BOComboBox<>(gridLink, "SourceID");
		param_src.setEditable(true);
		param_src.setSource(BOSource.getSourceSet(), "SourceID", "SourceName", "");
		param_src.setAppendUniqueStrings(false);
		
		param_minDate = new BODatePicker(gridLink, "DateStart");
		param_maxDate = new BODatePicker(gridLink, "DateEnd");
		
		param_minAmount = new BOTextField(gridLink, "AmountMin");
		param_maxAmount = new BOTextField(gridLink, "AmountMax");
		
		paramBar = ToolBarBuilder.create().items(
				new Label(Lng._("Source")), param_src,
				new Separator(),				
				new Label(Lng._("From")), param_minDate, new Label(Lng._("To")), param_maxDate,
				new Separator(),
				new Label(Lng._("Min")), param_minAmount, new Label(Lng._("To")), param_maxAmount,
				new Separator(),
				gridCtrl.getClearButton()).build();
		BOCtrlUtil.buildAttributeLinks(paramBar);
		
		// init record fields
		grid = new GridPane();
		
		notes = new BOTextField(record, "TransactionNotes");
		date = new BODatePicker(record, "TransactionDate");
		amount = new BOTextField(record, "TransactionAmount");
		name = new BOComboBox<>(record, "SourceID");
		
		initSource = new Procedure<BoundControl<?>>() {
			@SuppressWarnings("unchecked")
			public void call(BoundControl<?> result) {
				final BOComboBox<Integer> cb = (BOComboBox<Integer>)result;
				cb.setEditableEx(true);
				cb.setAppendUniqueStrings(true);
				
				// Dynamically create sources on the fly
				cb.setUniqueStringConverter(new Callback<String, Integer>() {
					public Integer call(String name) {
						if (name.length() > 0 && 
								BOSource.getSourceSet().findChildByAttribute("SourceName", name) == null) {
							BOSource src = BOSource.getSourceSet().createNewChild();
							src.sourceName().setValue(name);
							src.trySave();
							return src.sourceID().getValue();
						} else {
							// just return whatever was already selected...
							return cb.getSelected();
						}
					}					
				});
			}
		};
		
		initSource.call(name);
		
		// set dynamic source		
		name.setSource(BOSource.getSourceSet(), "SourceID", "SourceName", null);
		
		grid.add(new Label(Lng._("Date")), 0, 0);
		grid.add(new Label(Lng._("Source")), 0, 1);
		grid.add(new Label(Lng._("Amount")), 0, 2);
		grid.add(new Label(Lng._("Notes")), 0, 3);
		
		grid.add(date, 1, 0);
		grid.add(name, 1, 1);
		grid.add(amount, 1, 2);
		grid.add(notes, 1, 3);
		
		record.linkedObjectProperty().addListener(new ChangeListener<BOTransaction>() {
			public void changed(ObservableValue<? extends BOTransaction> arg0,
					BOTransaction arg1, BOTransaction arg2) {
				BOCtrlUtil.buildAttributeLinks(grid);
			}			
		});
		
		tranGrid.getColumnByField("SourceID").setAsCombobox(BOSource.getSourceSet(), "SourceID", "SourceName");
		tranGrid.getColumnByField("SourceID").setCtrlPropertySetter(initSource);
		tranGrid.getColumnByField("TransactionDate").setAsDatePicker();
	
		Button btnGraph = new Button("Graph");
		btnGraph.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				// Populate the attributes for the 
				List<Attribute> attributes = new Vector<>();
				attributes.add(new Attribute("SourceID", "Source/SourceName", "Source"));
				attributes.add(new Attribute("TransactionDate", "Date"));
				attributes.add(new Attribute("TransactionAmount", "Amount"));
				
				BOChartControl<BOTransaction> chartControl = new BOChartControl<>(gridSetRef, "TransactionGraphs", attributes);
				chartControl.show(getScene().getWindow());
			}
		});
		
		bottomBar = new ToolBar(gridCtrl.getPrimaryButton(), gridCtrl.getSecondaryButton(), gridCtrl.getRefreshButton(),
				btnGraph);
		
		setTop(paramBar);
		setCenter(VBoxBuilder.create().children(tranGrid, grid).spacing(2).build());		
		setBottom(bottomBar);
		
		VBox.setVgrow(tranGrid, Priority.SOMETIMES);
		VBox.setVgrow(grid, Priority.NEVER);
	}
	
	protected class BOTransactionSetRef extends BODbSetRef<BOTransaction>{
		private BODbAttribute<Integer> sourceID;	// do we really want to restrict by only ONE source?
		private BODbAttribute<Date> dateStart;
		private BODbAttribute<Date> dateEnd;
		private BODbAttribute<Double> amountMin;
		private BODbAttribute<Double> amountMax;
		
		public BODbAttribute<Integer> sourceID() {
			return sourceID;
		}
		public BODbAttribute<Date> dateStart() {
			return dateStart;
		}
		public BODbAttribute<Date> dateEnd() {
			return dateEnd;
		}
		public BODbAttribute<Double> amountMin() {
			return amountMin;
		}
		public BODbAttribute<Double> amountMax() {
			return amountMax;
		}
		
		protected BOTransactionSetRef() {
			super(BOTransaction.getTransactionSet(), DbUtil.getStoredProc("PS_TRN_for_set"));
		}
		
		protected void createAttributes() {
			sourceID = addAsChild(new BODbAttribute<Integer>(this, "SourceID", "src_id", AttributeType.Integer));
			dateStart = addAsChild(new BODbAttribute<Date>(this, "DateStart", "date_start", AttributeType.Date));
			dateEnd = addAsChild(new BODbAttribute<Date>(this, "DateEnd", "date_end", AttributeType.Date));
			amountMin = addAsChild(new BODbAttribute<Double>(this, "AmountMin", "trn_amount_min", AttributeType.Currency));
			amountMax = addAsChild(new BODbAttribute<Double>(this, "AmountMax", "trn_amount_max", AttributeType.Currency));
		}
	}
}
