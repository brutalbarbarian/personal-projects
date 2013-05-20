package com.lwan.eaproj.app.panes;

import java.util.Collection;
import java.util.Vector;

import com.lwan.eaproj.app.AppMain;
import com.lwan.eaproj.app.EAConstants;
import com.lwan.eaproj.app.panes.pages.PageAlerts;
import com.lwan.eaproj.app.panes.pages.PageBase;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;
import com.lwan.util.CollectionUtil;

import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuBarBuilder;
import javafx.scene.control.MenuBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class PaneMain extends BorderPane{
	public static final int PAGE_ALERTS = 0;
	public static final int PAGE_CUSTOMERS = 1;
	public static final int PAGE_WORK = 2;
	public static final int PAGE_INVOICES = 3;
	public static final int PAGE_EMPLOYEES = 4;
	
	public static final Scene createScene() {
		PaneMain mainapp = new PaneMain();
		Scene scene = new Scene(mainapp);
		scene.getStylesheets().addAll(App.getStyleshets());
		
		return scene;
	}
	
	private static String[] navButtonLabels = {"Alerts", "Customers", "Work", "Invoices", "Employees"};
	private EventHandler<ActionEvent> menuHandler, navHandler;
	private ToggleButton[] navButtons;
	private ToggleGroup navButtonGroup;
	private IntegerProperty pageProperty;
	private PageBase currentPage;
	
	/**
	 * The page the app is currently displaying.
	 * 
	 * @return
	 */
	public ReadOnlyIntegerProperty pageProperty() {
		return _pageProperty();
	}
	
	private IntegerProperty _pageProperty() {
		if (pageProperty == null) {
			pageProperty = new SimpleIntegerProperty(this, "Page", -1);
		}
		return pageProperty;
	}
	
	public void requestPage(final int page, final String... params) {
		if (page != getPage() && // only change if the page is actually different.
				// check save state of current page
				(currentPage == null || !currentPage.requiresSave() || currentPage.requestSave())) {
			// remove previous page..
			getChildren().remove(currentPage);
			
			// add new page in
			FadeTransition fadeOut = new FadeTransition(Duration.millis(
					currentPage == null? 0 : EAConstants.FADE_DURATION), currentPage);
			fadeOut.setFromValue(1.0);
			fadeOut.setToValue(0.0);
			
			fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					if (currentPage != null) {
						currentPage.dispose();
					}
					currentPage = getAppPage(page, params);
					setCenter(currentPage);
					FadeTransition fadeIn = new FadeTransition(
							Duration.millis(EAConstants.FADE_DURATION), currentPage);
					fadeIn.setFromValue(0.0);
					fadeIn.setToValue(1.0);
					fadeIn.play();
				}				
			});
			fadeOut.play();
			
			// synchronize changes
			_pageProperty().set(page);
		}
		// ensure ui button group is synchronised
		if (navButtonGroup.getSelectedToggle() != navButtons[getPage()]) {
			navButtonGroup.selectToggle(navButtons[getPage()]);
		}
	}
	
	protected PageBase getAppPage(int page, String... params) {
		// We don't cache any app pages... they're always created dynamically...
		switch(page) {
		case PAGE_ALERTS:
			return new PageAlerts();
		case PAGE_CUSTOMERS:

		case PAGE_EMPLOYEES:
			
		case PAGE_INVOICES:
			
		case PAGE_WORK:
		}
		
		return null;
	}
	
	public int getPage() {
		return pageProperty().getValue();
	}
	
	public PaneMain() {

		initialiseControls();
		
		requestPage(PAGE_ALERTS);
	}
	
	protected void initialiseControls() {
		// create the menu bar
		MenuBar menubar = MenuBarBuilder.create().menus(initialiseMenu()).build();			
		setTop(menubar);
		
		// create the navigation bar
		VBox navigationBar = new VBox();
		navigationBar.setMaxHeight(Double.MAX_VALUE);
		navigationBar.setId("navigation-bar");
		
		navHandler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				ToggleButton btn = (ToggleButton) e.getSource();
				int index = CollectionUtil.indexOf(btn, navButtons);
				
				requestPage(index);
			}
		};
		
		navButtons = new ToggleButton[navButtonLabels.length];
		navButtonGroup = new ToggleGroup();
		int i = 0;
		for (String title : navButtonLabels) {
			ToggleButton btn = new ToggleButton(Lng._(title));
			btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			btn.setId("navigation-button");
			btn.setUserData(title);
			btn.setOnAction(navHandler);
			btn.setToggleGroup(navButtonGroup);
			navButtons[i++] = btn;
			navigationBar.getChildren().add(btn);
		}
		
		setLeft(navigationBar);
	}
	
	protected void showUsersScreen() {
		Stage usr = new Stage(StageStyle.UTILITY);
		usr.initOwner(getScene().getWindow());
		usr.initModality(Modality.WINDOW_MODAL);
		usr.setTitle(Lng._("Maintain Users"));
		
		PaneUser pane = new PaneUser();
		try {
			Scene sc = new Scene(pane);
			sc.getStylesheets().addAll(App.getStyleshets());
			
			usr.setScene(sc);
			usr.setWidth(600);
			usr.setHeight(500);
			usr.showAndWait();
		} finally {
			pane.dispose();
		}
	}
	
	protected void showCompanyScreen(){
		Stage com = new Stage(StageStyle.UTILITY);
		com.initOwner(getScene().getWindow());
		com.initModality(Modality.WINDOW_MODAL);
		com.setTitle(Lng._("Maintain Companies"));
		
		PaneCompany pane = new PaneCompany();
		try {
			Scene sc = new Scene(pane);
			sc.getStylesheets().addAll(App.getStyleshets());
			
			com.setScene(sc);
			com.setWidth(600);
			com.setHeight(500);
			com.showAndWait();
		} finally {
			pane.dispose();
		}
	}
	
	protected void showSchoolScreen() {
		Stage sch = new Stage(StageStyle.UTILITY);
		sch.initOwner(getScene().getWindow());
		sch.initModality(Modality.WINDOW_MODAL);
		sch.setTitle(Lng._("Maintain Schools"));
		
		PaneSchool  pane = new PaneSchool();
		try {
			Scene sc = new Scene(pane);
			sc.getStylesheets().addAll(App.getStyleshets());
			
			sch.setScene(sc);
			sch.setWidth(600);
			sch.setHeight(500);
			sch.showAndWait();
		} finally {
			pane.dispose();
		}
	}
	
	protected Collection<Menu> initialiseMenu() {
		menuHandler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				MenuItem src = (MenuItem)e.getSource();
				switch (src.getUserData().toString()) {
				case "logoff" :
					AppMain.notifyState(AppMain.STATE_LOGOUT);
					break;
				case "exit" :
					AppMain.requestTerminate();
					break;
				case "users" :
					showUsersScreen();
					break;
				case "companies" :
					showCompanyScreen();
				case "school" :
					showSchoolScreen();
				}
			}
		};
		
		Vector<Menu> menu = new Vector<>();

		MenuItemBuilder<?> menuItemBuilder = MenuItemBuilder.create();
		
		menu.add(MenuBuilder.create().text(Lng._("_File")).items(
				menuItemBuilder.text(Lng._("Change _Users")).userData("logoff").onAction(menuHandler).build(),
				menuItemBuilder.text(Lng._("E_xit")).userData("exit").onAction(menuHandler).build()
				).build());
		
		menu.add(MenuBuilder.create().text(Lng._("_Maintain")).items(
				menuItemBuilder.text(Lng._("_Users")).userData("users").onAction(menuHandler).build(),
				menuItemBuilder.text(Lng._("_Companies")).userData("companies").onAction(menuHandler).build(),
				menuItemBuilder.text(Lng._("_Schools")).userData("school").onAction(menuHandler).build()
				).build());
		
		return menu;
	}
}
