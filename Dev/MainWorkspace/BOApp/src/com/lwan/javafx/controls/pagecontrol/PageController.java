package com.lwan.javafx.controls.pagecontrol;

import java.util.LinkedList;
import java.util.Queue;

import com.lwan.util.containers.Params;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Page controller is what controls the pages. That is, it manages how the 
 * page buttons should be drawn, 
 * 
 * @author Lu
 *
 */
public abstract class PageController {
	private PageData<?> rootPageData;
	private Orientation controlOrientation;
	/**
	 * Set the orientation. Orientation determines if children of a 
	 * a node are horizontal or vertical
	 * 
	 * @param orientation
	 */
	public void initOrientation(Orientation orientation) {
		if (rootPage != null) {
			throw new RuntimeException("Cannot init orientation if pane has already been built.");
		}
		controlOrientation = orientation;
	}
	public Orientation getOrientation() {
		return controlOrientation;
	}
	
	
	public PageController() {
		rootPageData = new PageDataBase<Node>(null, null, null, false, null) {};
		controlOrientation = Orientation.HORIZONTAL;
		activePage = null;
		toggleGroup = new ToggleGroup();
	}
	
	public PageData<?> getRootData() {
		return rootPageData;
	}
	
	/**
	 * Create the toggle control
	 * 
	 * @param data
	 * @return
	 */
	protected abstract Toggle createPageButton(PageData<?> data);
	
	/**
	 * Get the padding of all children.
	 * 
	 * @return
	 */
	protected abstract Insets getPadding(PageData<?> data);
	
	public Pane getControlPane() {
		if (rootPage == null) {
			rootPage = buildPageControl(rootPageData);
		}
		return rootPage.childrenPane;
	}
	
	BorderPane displayArea;
	public BorderPane getDisplayArea() {
		if (displayArea == null) {
			displayArea = new BorderPane();
		}
		return displayArea;
	}
	
	public Node getDisplayedNode() {
		return getDisplayArea().getCenter();
	}
	
	Page activePage;
	Page rootPage;
	ToggleGroup toggleGroup;
	protected Page buildPageControl(final PageData<?> data) {
		Page page = new Page();
		page.controller = this;
		page.pageData = data;
		page.toggleControl = createPageButton(data);
		page.toggleControl.setToggleGroup(toggleGroup);
		page.toggleControl.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					trySetActivePage(data, null);
				} else {
					if (toggleGroup.getSelectedToggle() == null) {
						activePage.toggleControl.setSelected(true);
					}
				}
			}			
		});
		
		data.setPage(page);
		
		if (data.hasChildren()) {
			page.createChildPane(controlOrientation);
			if (data.getParent() != null) {
				page.childrenPane.setPadding(getPadding(data));
			}
			
			for (PageData<?> child : data.getChildren()) {
				Page childPage = buildPageControl(child);
				page.childrenPane.getChildren().add((Node)childPage.toggleControl);
				if (child.hasChildren()) {
					page.childrenPane.getChildren().add(childPage.childrenPane);
				}
			}
		}
		
		return page;
	}
	
	private LoadingPage loadingPage;
	public void setLoadingPage(LoadingPage page) {
		loadingPage = page;
	}
	
	public LoadingPage getLoadingPage() {
		return loadingPage;
	}
	
	public PageData<?> findPageDataByID(String ID) {
		Queue<PageData<?>> queue = new LinkedList<>();
		queue.add(getRootData());
		while (!queue.isEmpty()) {
			PageData<?> data = queue.remove();
			if (ID.equals(data.getUID())) {
				return data;
			} else if (data.hasChildren()) {
				queue.addAll(data.getChildren());
			}
		}
		return null;	// Not found
	}
	
	public void trySetActivePage(String pageID, Params params) {
		trySetActivePage(findPageDataByID(pageID), params);
	}

	boolean settingActivePage = false;
	
	@SuppressWarnings("unchecked")
	public void trySetActivePage(final PageData<?> page, final Params params) {
		if (settingActivePage	// Don't try loading if we're still in the middle of animations for prev page 
			|| activePage != null && getPrefSetPage(page) == activePage.pageData) {
			activePage.toggleControl.setSelected(true);	// ensure the correct control is still toggled
			((Node)activePage.toggleControl).requestFocus();
			return;	// do nothing?
		}
		settingActivePage = true;
		
		if (activePage == null || activePage.pageData.hasPageNode() && 
				activePage.pageData.allowClosePageNode(getDisplayedNode())) {
			final Node currentPage = getDisplayedNode();

			final FadeTransition fadeActiveOut, fadeLoadingIn, fadeLoadingOut, fadeNewIn;
			final Runnable loadRunner;
			
			fadeActiveOut = new FadeTransition(Duration.millis(
					currentPage == null? 0 : 200), displayArea);
			fadeActiveOut.setFromValue(1.0);
			fadeActiveOut.setToValue(0.0);
			
			fadeLoadingIn = new FadeTransition(Duration.millis(200), displayArea);
			fadeLoadingIn.setFromValue(0.0);
			fadeLoadingIn.setToValue(1.0);
			
			fadeLoadingOut = new FadeTransition(Duration.millis(200), displayArea);
			fadeLoadingOut.setFromValue(1.0);
			fadeLoadingOut.setToValue(0.0);
			
			fadeNewIn = new FadeTransition(Duration.millis(200), displayArea);
			fadeNewIn.setFromValue(0.0);
			fadeNewIn.setToValue(1.0);
			
			loadRunner = new Runnable() {
				public void run() {
					if (currentPage != null) {
						activePage.pageData.closePageNode(currentPage);
					}
					PageData<?> loadPage = getPrefSetPage(page);
					final Node newNode;
					if (loadPage != null) {
						activePage = loadPage.getPage();
						displayControlState();
						loadPage.getPage().toggleControl.setSelected(true);
						newNode = loadPage.getPageNode(params);
					} else {
						newNode = null;
					}
					fadeLoadingOut.setOnFinished(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							displayArea.setCenter(newNode);
							settingActivePage = false;
							fadeNewIn.play();
						}						
					});
					
					fadeLoadingOut.play();
				}
			};
			
			fadeActiveOut.setOnFinished(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					// fade in the loading page
					loadingPage.startLoading();
					
					getDisplayArea().setCenter(loadingPage);
					fadeLoadingIn.play();					
										
					Platform.runLater(loadRunner);
				}
			});
			fadeActiveOut.play();
		} else if (activePage != null) {
			activePage.toggleControl.setSelected(true);
		}
	}
	
	protected PageData<?> getPrefSetPage(PageData<?> requestedPage) {
		if (requestedPage == null) {
			return null;
		} else if (requestedPage.preferredChild() != null) {
			return getPrefSetPage(requestedPage.preferredChild());
		} else {
			return requestedPage;
		}
	}
	
	protected void displayControlState() {
		 setAllNotProcessed(rootPage);
		 Page page = activePage;
		 while (page != null) {
			 page.ensureChildrenShowing();
			 page.processed = true;
			 PageData<?> parent = page.pageData.getParent();
			 if (parent == null) {
				 page = null;
			 } else {
				 page = parent.getPage();
			 }
		 }
		 displayPageState(rootPage);
	}
	
	protected void displayPageState(Page page) {
		page.toggleControl.setSelected(page == activePage);
		if (!page.processed) {
			page.ensureChildrenHiding();
		}
		if (page.pageData.hasChildren()) {
			for (Object child : page.pageData.getChildren()) {
				displayPageState(((PageData<?>)child).getPage());
			}
		}
	}
	
	protected void setAllNotProcessed(Page page) {
		page.processed = false;
		if (page.pageData.hasChildren()) {
			for (Object child : page.pageData.getChildren()) {
				setAllNotProcessed(((PageData<?>)child).getPage());
			}
		}
	}
}
