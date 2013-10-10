package com.lwan.eaproj.app.panes;

import java.util.Collection;
import java.util.Vector;

import com.lwan.eaproj.app.AppEastAsia;
import com.lwan.eaproj.app.panes.pages.PageAlerts;
import com.lwan.eaproj.app.panes.pages.PageCustomer;
import com.lwan.eaproj.app.panes.pages.PageInvoice;
import com.lwan.eaproj.app.panes.pages.PageWork;
import com.lwan.javafx.app.App;
import com.lwan.javafx.app.Lng;
import com.lwan.javafx.controls.pagecontrol.LoadingPage;
import com.lwan.javafx.controls.pagecontrol.PageController;
import com.lwan.javafx.controls.pagecontrol.PageData;
import com.lwan.javafx.controls.panes.TBorderPane;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PaneMain extends TBorderPane{
	
	public static final Scene createScene() {
		PaneMain mainapp = new PaneMain();
		Scene scene = new Scene(mainapp);
		scene.getStylesheets().addAll(App.getStyleshets());
		
		return scene;
	}
	
	private EventHandler<ActionEvent> menuHandler;
	
	public PaneMain() {

		initialiseControls();
	}
	
	public PageController getPageControl() {
		return pageControl;
	}
	
	private PageControl pageControl;
	
	protected void initialiseControls() {
		// create the menu bar

		mbMain = new MenuBar();
		mbMain.getMenus().addAll(initialiseMenu());
		
		setTop(mbMain);
		
		// create the navigation bar
		pageControl = new PageControl();
		PageData<?> root = pageControl.getRootData();
		root.getChildren().add(new PageAlerts(root));
		root.getChildren().add(new PageCustomer(root));		
		root.getChildren().add(new PageWork(root));
		root.getChildren().add(new PageInvoice(root));

		pageControl.initOrientation(Orientation.VERTICAL);
		pageControl.setLoadingPage(new SimpleLoadingPage());
		
		setLeft(pageControl.getControlPane());
		setCenter(pageControl.getDisplayArea());
	}
	
	protected class SimpleLoadingPage extends LoadingPage {
		ProgressIndicator prog;
		
		SimpleLoadingPage() {
			prog = new ProgressIndicator();
			
			prog.maxWidthProperty().bind(Bindings.divide(widthProperty(), 4));
	        prog.maxHeightProperty().bind(Bindings.divide(heightProperty(), 4));
			
			getChildren().add(prog);
		}
		
		public void startLoading() {}
		public void update(double offset) {}
		public void stop() {}		
	}
	
	protected class PageControl extends PageController {
		Insets insets = new Insets(0, 0, 0, 5);
		
		protected Toggle createPageButton(PageData<?> data) {
			return new ToggleButton(data.getDisplayTitle());
		}

		@Override
		protected Insets getPadding(PageData<?> data) {
			return insets;
		}		
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
	
	protected void showProductScreen() {
		Stage sch = new Stage(StageStyle.UTILITY);
		sch.initOwner(getScene().getWindow());
		sch.initModality(Modality.WINDOW_MODAL);
		sch.setTitle(Lng._("Maintain Products"));
		
		PaneProduct  pane = new PaneProduct();
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
	
	protected MenuBar mbMain;
	protected Menu muFile;
	protected MenuItem miFile_changeUsers, miFile_exit;
	protected Menu muMaintain;
	protected MenuItem miMaintainProducts, miMaintainUsers, miMaintainCompanies, miMaintainSchools;
	
	
	protected Collection<Menu> initialiseMenu() {
		menuHandler = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				MenuItem src = (MenuItem)e.getSource();
				if (src == miFile_changeUsers) {
					AppEastAsia.notifyMessage(AppEastAsia.STATE_LOGOUT, null);
				} else if (src == miFile_exit) {
					AppEastAsia.requestTerminate();
				} else if (src == miMaintainUsers) {
					showUsersScreen();
				} else if (src == miMaintainCompanies) {
					showCompanyScreen();
				} else if (src == miMaintainSchools) {
					showSchoolScreen();
				} else if (src == miMaintainProducts) {
					showProductScreen();
				}
			}
		};
		
		Vector<Menu> menu = new Vector<>();
		
		{	// muFile
			muFile = new Menu(Lng._("_Files"));
			menu.add(muFile);
			
			miFile_changeUsers = new MenuItem(Lng._("Change _Users"));
			miFile_changeUsers.setOnAction(menuHandler);
			muFile.getItems().add(miFile_changeUsers);
			
			miFile_exit = new MenuItem(Lng._("E_xit"));
			miFile_exit.setOnAction(menuHandler);
			muFile.getItems().add(miFile_exit);			
		}
		
		{	// muMaintain
			muMaintain = new Menu(Lng._("_Maintain"));
			menu.add(muMaintain);
			
			miMaintainProducts = new MenuItem(Lng._("_Products"));
			miMaintainProducts.setOnAction(menuHandler);
			muMaintain.getItems().add(miMaintainProducts);
			
			miMaintainUsers = new MenuItem(Lng._("_Users"));
			miMaintainUsers.setOnAction(menuHandler);
			muMaintain.getItems().add(miMaintainUsers);
			
			miMaintainCompanies = new MenuItem(Lng._("_Companies"));
			miMaintainCompanies.setOnAction(menuHandler);
			muMaintain.getItems().add(miMaintainCompanies);
			
			miMaintainSchools = new MenuItem(Lng._("Schools"));
			miMaintainSchools.setOnAction(menuHandler);
			muMaintain.getItems().add(miMaintainSchools);
		}

		return menu;
	}
}
