package com.lwan.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.lwan.util.GraphicsUtil;
import com.lwan.util.StringGraphics;

/**
 * A custom tabpane structure that looks better then the standard java tabs, based on the look
 * and feel of Mozilla Firefox </br>
 * Tabs can be ether closable or non closable.</br>
 * A menu tab may also be created, and will always appear on the far left</br>
 * Each tab can have its own unique right-click menu along with some standard functions</br>
 * Menu tab will also have its own left-click menu</br>
 * </br>
 * All tabs has a defualt renderer which may be overriden. This includes the menu tab</br>
 * The defualt renderer may also be overriden</br>
 * </br>
 * Tabs by defualt can be dragged, though this can be disabled</br>
 * Tabs can be looped through using 'alt-tab' and 'alt-shift-tab' for foward and backwards respectively</br>
 * </br>
 * Each tab has a panel which is the displayed contents. The entire tab pane consists of the tab panel, and the display panel.</br>
 * The display panel is where the displayed contents of each tab appears. This is passed in upon construction, and thus may</br>
 * be placed anywhere, rather then forced to be below the tab panel.</br>
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class JTabPane extends JPanel {
	//default colors
	private static final Color [] remColors = {new Color (250, 250, 250), new Color(40, 40, 40), new Color(150, 150, 150), new Color (100, 100, 100)};
	
	private List<Tab> tabs;//list of tabs
	private JPanel containerPanel;//container for all panels to be displayed
	private TabRenderer defaultRenderer;//default background renderer for tabs 
	private int tabHeight;//height of the tabs dependent on font size
	private MenuTab menuTab;//the menu tab on far left if any
	private TabRenderer menuRenderer;//default renderer for the menu tab
	private KeyDispatcher tabIntercept;
	private Color menuFontColor;
	
	private int selectedTab;//tab which is currently selected
	private long idCount;
	private boolean dragable;	//if the tabs are set to draggable. default to true
	
	private int closeSize = 14;
	
	public JTabPane (JPanel containerPanel) {
		idCount = 0;
		menuFontColor = Color.BLACK;
		tabs = new LinkedList<Tab>();
		this.containerPanel=containerPanel;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.PAGE_AXIS));
		
		defaultRenderer = new DEFAULT_TAB_RENDERER();
		menuRenderer = new DEFAULT_MENU_RENDERER();
		
		dragable = true;
	}
	
	/**
	 *	Set the font that is used to render text in all tabs. This is also used to
	 *	deterine the height of this component. 
	 * 
	 */
	public void setFont (Font f) {
		super.setFont(f);
		tabHeight = StringGraphics.getFontHeight(f) + 10;
		this.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width*2, tabHeight));
		this.setMinimumSize(new Dimension(0, tabHeight));
	}
	
	/**
	 * Get the next id for tab creation
	 * 
	 * @return
	 * 	A new unique id
	 */
	private long getNextId () {
		return idCount ++;
	}
	
	/**
	 * Get the font being used to render text in all tabs
	 * 
	 * @return
	 * 	Current set font
	 */
	protected Font getTabFont() {
		return getFont();
	}

	/**
	 * Shift focus to the next tab along. Nothing will happen if there are no tabs
	 * 
	 */
	public void shiftFocusToNext() {
		if (!tabs.isEmpty()) {
			if ((selectedTab + 1) >= tabs.size()) setTabFocus(0);
			else setTabFocus(selectedTab+1);
		}
	}
	
	/**
	 * Shift focus to the previous tab. Nothing will happen if there are no tabs
	 * 
	 */
	public void shiftFocusToPre() {
		if ((selectedTab-1) < 0) setTabFocusToLast();
		else setTabFocus(selectedTab-1);
	}

	/**
	 * Set focus to tab at index i
	 * Will throw indexOutOfBoundsException if passed in index does not exist
	 * 
	 * @param i
	 */
	public void setTabFocus(int i) {
		if (i >= 0 &&i < tabs.size()) {
			selectedTab = i;
			tabs.get(i).setFocus();
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * Set focus to the last tab
	 * 
	 */
	public void setTabFocusToLast() {
		if (!tabs.isEmpty()) setTabFocus(tabs.size()-1);
	}
	
	/**
	 * Get the index of the tab with the set id
	 * This is the only way to avoid duplication
	 * 
	 * @param id
	 * @return
	 */
	public int getIndexOf (long id) {
		for (int i = 0; i < tabs.size(); i++) {
			if (tabs.get(i).id == id) {
				return i;
			}
		}
		return -1;		
	}
	
	/**
	 * Get the index of the first tab with the set label 
	 * 
	 * @param label
	 */
	public int getIndexOf (String label) {
		for (int i = 0; i < tabs.size(); i++) {
			if (tabs.get(i).label.equals(label)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Get the index of the first tab with the set displayed panel
	 * 
	 * @param panel
	 * @return
	 */
	public int getIndexOf (JPanel content) {
		for (int i = 0; i < tabs.size(); i++) {
			if (tabs.get(i).content.equals(content)) {
				return i;
			}
		}
		return -1;		
	}
	
	/**
	 * Set tab focus to the first tab
	 * 
	 */
	public void setTabFocusToFirst() {
		if (!tabs.isEmpty()) setTabFocus(0);
	}
	
	/**
	 * Add a tab with passed in label and displayed panel.
	 * Removable dictates if this panel can or cannot be removed by the user.
	 * 
	 * This tab will also initialise with a defualt popup menu with 'view tab' and 'close tab' as an option, as well as having draggable set to true
	 * 
	 * @param panel
	 * 	The displayed Panel
	 * @param label
	 * 	The label to be displayed on the tab
	 * @param removable
	 * 	true would result in a small 'x' on the tab next to the label which the user may use to close this tab
 	 * @return
	 * 	The id of the tab that was just created. This id is guranteed to be unique for this tabPane
	 */
	public long addTab (JPanel panel, String label, boolean removable) {
		return addTab(panel, label, removable, null, null, true);
	}

	/**
	 * Add a tab with passed in label and displayed panel
	 * Removable dictates if this panel can or cannot be removed by the user.
	 * The right click menu may be dictated by the user, or set to null
	 * The renderer similarly may be dictated by the user, or set to null
	 * 
	 * @param panel
	 * 	The displayed panel
	 * @param label
	 * 	The label to be displayed on the tab
	 * @param removable
	 * 	true would result in a small 'x' on the tab next to the label which the user may use to close this tab
	 * @param menu
	 * 	Ether a JPopupMenu or null. If null, the defualt popupmenu with 'view' and 'close' will be used instead
	 * @param renderer
	 * 	The TabRenderer used to render the background of this tab. If this is null, the default renderer will be used
	 * @param allowDrag
	 * 	Whether or not this tab may be dragged
	 * @return
	 * 	The id of the tab that was just created. This id is guranteed to be unique for this tabPane 
	 */
	public long addTab (JPanel panel, String label, boolean removable, JPopupMenu menu, TabRenderer renderer, boolean allowDrag) {
		Tab t = new Tab(panel, label, removable, renderer, allowDrag);
		t.setPopupMenu (menu==null?new DEFAULT_MENU(removable, t):menu);
		
		tabs.add(t);
		add(t);
		
		return t.id;
	}
	
	/**
	 * Get the id of the tab at specified location
	 * Will throw indexOutOfBoundsException if the specified index does not exist
	 * 
	 * @param index
	 * @return
	 */
	public long getIdOf(int index) {
		if (index < 0 || index >= tabs.size()) {
			throw new IndexOutOfBoundsException ();
		}
		return tabs.get(index).id;
	}
	
	/**
	 * Set the background renderer of the tab specified by the index
	 * Will throw indexOutOfBoundsException if the specified index does not exist
	 * 
	 * @param tabIndex
	 * 	Index of the specified tab
	 * @param renderer
	 * 	The renderer to be set with. This may be null, in which case, resets the specified tab to use the default renderer 
	 */
	public void setTabRenderer (int tabIndex, TabRenderer renderer) {
		if (tabIndex < 0 || tabIndex >= tabs.size()) {
			throw new IndexOutOfBoundsException ();
		}
		tabs.get(tabIndex).renderer = renderer;
	}
	
	/**
	 * Set the Popup menu of a tab at the specified index
	 * Will throw indexOutOfBoundsException if the specified index does not exist
	 * 
	 * @param tabIndex
	 * 	Index of the specified tab
	 * @param menu
	 * 	The popup menu to be set with. This may be null, in which case, a default popup menu will be used  
	 */
	public void setPopupMenu (int tabIndex, JPopupMenu menu) {
		if (tabIndex < 0 || tabIndex >= tabs.size()) {
			throw new IndexOutOfBoundsException ();
		}
		Tab t = tabs.get(tabIndex);
		t.setPopupMenu(menu==null?new DEFAULT_MENU(t.removable, t):menu);
	}
	
	/**
	 * Get the Popup menu of a tab at the specified index
	 * Will throw indexOutOfBoundsException if the specified index does not exist
	 * 
	 * @param tabIndex
	 * 	Index of the specified tab
	 * @return
	 */
	public JPopupMenu getPopupMenu (int tabIndex) {
		if (tabIndex < 0 || tabIndex >= tabs.size()) {
			throw new IndexOutOfBoundsException ();
		}
		return tabs.get(tabIndex).menu;
	}
	
	/**
	 * Remove the tab at the specified index
	 * Will throw indexOutOfBoundsException if the specified index does not exist
	 * 
	 * @param index
	 * 	Index of the specified tab
	 */
	public void removeTab (int index) { 
		if (index >= 0 && index < tabs.size()) {
			containerPanel.remove(tabs.get(index).content);
			remove(tabs.remove(index));
			
			if (selectedTab == index) {
				if (index >= tabs.size()) {
					shiftFocusToPre();
				} else {
					this.setTabFocus(index);
				}
			}
			
			revalidate();
			repaintAllTabs();
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * Set the menu tab with the passed in popup menu, and the label to be displayed
	 * This menu tab will always appear on the far left, and by default will use a default TabRenderer to render it
	 * If any of the parameters are null, this will instead set the menu tab to be null, effectively removing it. 
	 * 
	 * @param menu
	 * @param label
	 */
	public void setMenuTab(JPopupMenu menu, String label) {
		//ensure menu tab is removed to avoid duplicates
		if (menuTab != null) {
			remove(menuTab);
		}
		if (menu == null || label == null) {
			menuTab = null;
		} else {
			menuTab = new MenuTab(menu, label);
			add(menuTab, 0);	//insert in first position
		}
		revalidate();
		repaintAllTabs();
	}
	
	/**
	 * Set the renderer used to render the menu tab
	 * If this is null, the defualt renderer will be set instead
	 * 
	 * @param renderer
	 * 	Renderer to be set. May be null in which case default will be used
	 */
	public void setMenuTabRenderer (TabRenderer renderer) {
		if (renderer == null) {
			if (renderer instanceof DEFAULT_MENU_RENDERER) {
				return;
			} else {
				menuRenderer = new DEFAULT_MENU_RENDERER();
			}
		} else {
			menuRenderer = renderer;
		}
	}
	
	/**
	 * Set the defualt renderer used to render all tabs without a specific renderer
	 * If this is null, the defualt renderer will be set instead
	 * 
	 * @param renderer
	 * 	Renderer to be set. May be null in which case default will be used
	 */
	public void setDefaultTabRenderer (TabRenderer renderer) {
		if (renderer == null) {
			if (defaultRenderer instanceof DEFAULT_TAB_RENDERER) {
				return;
			} else {
				defaultRenderer = new DEFAULT_TAB_RENDERER();
			}
		} else {
			defaultRenderer = renderer;
		}
	}
	
	/**
	 * Get the panel being displayed at the specified index
	 * Will throw indexOutOfBoundsException if the specified index does not exist
	 * 
	 * @param index
	 * 	Index of the specified tab
	 * @return
	 * 	The content Panel that is displayed for the specified tab
	 */
	public JPanel getTabPanel (int index) {
		if (index >= 0 && index < tabs.size()) {
			return tabs.get(index).content;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * Get the label of the tab at specified index.
	 * Will throw indexOutOfBoundsException if the specified index does not exist
	 * 
	 * @param index
	 * 	Index of the specified tab
	 * @return
	 * 	The label displayed for the specified tab
	 */
	public String getTabLabel (int index) {
		if (index >= 0 && index < tabs.size()) {
			return tabs.get(index).label;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * Get the number of tabs
	 * 
	 * @return
	 * 	The number of tabs
	 */
	public int getNumOfTabs () {
		return tabs.size();
	}
	
	/**
	 * Move the tab at index 'tabIndex' to the new index at 'newIndex'
	 * Will throw indexOutOfBoundsException if ether specified indecies does not exist
	 * This will cause all other tabs in between to shift ether left or right.
	 * 
	 * @param tabIndex
	 * @param newIndex
	 */
	public void moveTabTo (int tabIndex, int newIndex) {
		if (tabIndex < 0 || tabIndex >= tabs.size() || newIndex < 0 || newIndex >= tabs.size()) {
			throw new IndexOutOfBoundsException();
		} else if (tabIndex == newIndex) {
			return;
		} else {
			//find id of currently selected tab
			long id = tabs.get(selectedTab).id;
			
			Tab t = tabs.remove(tabIndex);
			tabs.add(newIndex, t);
			
			//find index of of tab
			int index = 0;
			Component [] c = this.getComponents();
			for (int i = 0; i < c.length; i++) {
				if (c[i] == t) {
					index = i;
					break;
				}
			}
			//remove tab 
			remove(index);
			//insert tab into the new index
			index = index + (newIndex-tabIndex);
			this.add(t, index);
			
			//update selected index to reflect changes
			selectedTab = getIndexOf(id);
			
			revalidate();
			repaintAllTabs();
		}
	}
	
	/**
	 * Set the state of draggable
	 * If true, tabs will be able to be dragged to reorder them
	 * If false, tabs cannot be reordered via dragging
	 * 
	 * @param enabled
	 */
	public void setDraggable(boolean enabled) {
		dragable = enabled;
	}
	
	/**
	 * Change the label of a specified tab
	 * Will throw indexOutOfBoundsException if the specified index does not exist
	 * 
	 * @param tabIndex
	 * @param label
	 */
	public void setTabLabel (int tabIndex, String label) {
		if (tabIndex < 0 || tabIndex >= tabs.size()) {
			throw new IndexOutOfBoundsException();
		}
		tabs.get(tabIndex).setLabel(label);
		repaintAllTabs();
	}
	
	/**
	 * Change the displayed contents of a specified tab
	 * Will throw indexOutOfBoundsException if the specified index does not exist
	 * 
	 * @param tabIndex
	 */
	public void setTabContent (int tabIndex, JPanel content) {
		if (tabIndex < 0 || tabIndex >= tabs.size()) {
			throw new IndexOutOfBoundsException();
		}
		
		tabs.get(tabIndex).content = content;
		if (selectedTab == tabIndex) {
			setTabFocus(tabIndex);
		}
	}
	
	/**
	 * Set the color of the font used to render the menu tab
	 * 
	 * @param c
	 * 	Color of the font to be displayed with
	 */
	public void setMenuTabFontColor (Color c) {
		menuFontColor = c;
	}
	
	public void setGlobalTabShortcuts (boolean enabled) {
		if (enabled) {
			tabIntercept = new KeyDispatcher();
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(tabIntercept);
		} else if (tabIntercept != null) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(tabIntercept);
			tabIntercept = null;
		}
	}

	/** Looparound method for force repainting */
	private void repaintAllTabs() {
		repaint();
	}
	
	
	
	@Override
	protected void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = GraphicsUtil.setAntiAliasing(g);
		GradientPaint shadow = new GradientPaint(0, tabHeight*4/5, new Color (200, 200, 200, 0), 0, tabHeight, new Color (100,100,100,150));
		g2.setPaint(shadow);
		g2.fillRect(0, tabHeight*4/5, getWidth(), tabHeight/5);
		
		g2.setColor(new Color(160, 160, 160));
		g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
	}
	
	protected void paintChildren (Graphics g) {
		super.paintChildren(g);
		
		//ensures that the selected tab is ALWAYS on top
		if (tabs.size() != 0) {
			Tab t = tabs.get(selectedTab);
			Graphics newG = g.create(t.getX(), 0, t.getWidth(), tabHeight);
			tabs.get(selectedTab).draw(newG);
		}
	}
	
	private class DEFAULT_MENU extends JPopupMenu implements ActionListener {
		private Tab tab;
		
		public DEFAULT_MENU(boolean removable, Tab tab) {
			this.tab = tab;
			
			JMenuItem view = new JMenuItem("View");
			JMenuItem close = new JMenuItem("Close");
			view.addActionListener(this);
			close.addActionListener(this);
			if (!removable) close.setEnabled(false);
			
			add(view);
			addSeparator();
			add(close);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "View":
				setTabFocus(getIndexOf(tab.id));
				break;
			case "Close":
				removeTab(getIndexOf(tab.id));
				break;
			}
		}
		
	}
	
	private class DEFAULT_TAB_RENDERER implements TabRenderer {
		final Color borderColor = new Color (160, 160, 160);
		final Color focusedColor = UIManager.getColor("Label.background");
		final Color nonFocusedColor = new Color (200, 200, 200);
		
		public void render(Graphics2D g, int width, int height, int state) {
			if (state == TabRenderer.SELECT) {
				GradientPaint focusedGradient = new GradientPaint(0, 0, Color.white, 0, height, focusedColor);
				g.setPaint(focusedGradient);
			} else {
				g.setColor (nonFocusedColor);
			}
			
			g.fillRoundRect(1, 0, width-2, height+10, 20, 20);
			g.setColor(borderColor);
			g.drawRoundRect(1, 0, width-2, height+10, 20, 20);
			if(state == TabRenderer.DEFAULT) {
				g.drawLine(0, height-1, width, height-1);
				GradientPaint shadow = new GradientPaint (0, tabHeight*4/5, new Color (200, 200, 200, 0), 0, tabHeight, new Color (100,100,100,150));
				g.setPaint(shadow);
				g.fillRect(0, height*3/4, width, height/4);
			}
		}
	}
	
	private class DEFAULT_MENU_RENDERER implements TabRenderer {
		final Color c1 =  new Color(150, 150, 240);
		final Color c2 = new Color(80, 80, 150);
		final Color high = new Color(255, 255, 255, 150);
		
		public void render(Graphics2D g, int width, int height, int state) {
			GradientPaint p;
			switch (state) {
			case TabRenderer.DEFAULT:
				p = new GradientPaint(0, 0, c1, 0, height, c2);
				g.setPaint(p);
				g.fillRoundRect(1, -10, width-2, height+8, 10, 10);
				g.setStroke(new BasicStroke(2f));
				g.setColor(high);
				g.drawRoundRect(1, -10, width-2, height+8, 10, 10);
				g.setStroke(new BasicStroke(0.5f));
				g.setColor(Color.DARK_GRAY);
				g.drawRoundRect(1, -10, width-2, height+8, 10, 10);
				break;
			case TabRenderer.HOVER:
				p = new GradientPaint(0, 0, c1, 0, height, c2);
				g.setPaint(p);
				g.fillRoundRect(1, -10, width-2, height+8, 10, 10);
				g.setStroke(new BasicStroke(3f));
				g.setColor(high);
				g.drawRoundRect(1, -10, width-2, height+8, 10, 10);
				g.setStroke(new BasicStroke(0.5f));
				g.setColor(Color.DARK_GRAY);
				g.drawRoundRect(1, -10, width-2, height+8, 10, 10);
				break;
			case TabRenderer.SELECT:
				p = new GradientPaint(0, 0, c2, 0, height, c1);
				g.setPaint(p);
				g.fillRoundRect(1, -10, width-2, height+8, 10, 10);
				g.setStroke(new BasicStroke(2f));
				g.setColor(high);
				g.drawRoundRect(1, -10, width-2, height+8, 10, 10);
				g.setStroke(new BasicStroke(0.5f));
				g.setColor(Color.DARK_GRAY);
				g.drawRoundRect(1, -10, width-2, height+8, 10, 10);
			}
		}		
	}
	
	private class Tab extends JPanel implements MouseListener, MouseMotionListener{
		static final int NORM = 0;
		static final int HOVER = 1;
		static final int PRESSED = 2;
		
		final long id;	//used to identify this tab
		JPopupMenu menu;
		TabRenderer renderer;
		JPanel content;	
		String label;	//label displayed
		boolean removable;
		boolean dragged;
		int x;
		boolean allowDrag;
		
		int remState;
		boolean focused;
		

		public Tab(JPanel panel, String label, boolean removable, TabRenderer renderer, boolean allowDrag) {
			id = getNextId();
			int width = 150;	//max width
			
			Dimension max = new Dimension(width, tabHeight);
			setMinimumSize(new Dimension(35, tabHeight));
			setMaximumSize(max);
			setPreferredSize(max);
			
			focused = false;
			this.allowDrag = allowDrag;
			this.removable = removable;
			content = panel;
			this.setOpaque(false);
			setLabel(label);
			if (panel == null) {
				panel = new JPanel();
			}
			remState = NORM;
			
			addMouseListener(this);
			addMouseMotionListener(this);
			setDoubleBuffered(true);
		}
		
		void setLabel(String label) {
			this.label = label;
			this.setToolTipText(label);
		}

		private void draw (Graphics _g) {
			Graphics2D g = GraphicsUtil.setAntiAliasing(_g);

			//draw background
			(renderer == null? defaultRenderer:renderer).render(g, getWidth(), getHeight(), focused? TabRenderer.SELECT : TabRenderer.DEFAULT);
			
			int maxWidth = this.getWidth() - 13;
			if (removable) {//draw removable cross
				int x = getWidth() - 18;
				int y = tabHeight/5;
				
				maxWidth = maxWidth - 10;
				//draw background
				switch (remState) {
				case HOVER:
					g.setColor(remColors[2]);
					g.fillOval(x, y, closeSize, closeSize);
					g.setColor(remColors[0]);
					break;
				case PRESSED:
					g.setColor(remColors[3]);
					g.fillOval(x, y, closeSize, closeSize);
					g.setColor(remColors[0]);
					break;
				case NORM:
					g.setColor(remColors[1]);
					break;
				}
				//draw x
				g.drawLine(x+closeSize/3, y+closeSize/3, x+closeSize*4/6, y+closeSize*4/6);
				g.drawLine(x+closeSize*4/6, y+closeSize/3, x+closeSize/3, y+closeSize*4/6);
			}
			//draw the label
			g.setColor(Color.black);
			g.setFont(getTabFont());
			StringGraphics.drawTruncString(g, label, 10, tabHeight - StringGraphics.getFontHeight(getTabFont())*1/2, maxWidth);
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			if (focused) return;
			draw (g);
		}
		
		void setPopupMenu(JPopupMenu menu) {
			this.menu = menu;
		}

		public void setFocus() {
			for(Tab tab : tabs) {
				if (tab.focused == true) {
					tab.focused = false;
					containerPanel.remove(tab.content);
					tab.repaint();
				}
			}
			focused = true;
			containerPanel.add(content);
			containerPanel.revalidate();
			containerPanel.repaint();
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (remState == PRESSED) {
				int x = getWidth() - 18;
				int y = tabHeight/5;
				
				if (e.getX() > x && e.getX() < x+closeSize && e.getY() > y && e.getY() < y+closeSize) {
					//do nothing
				} else if (remState != NORM){
					remState = NORM;
					repaint();
				}
			}
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (!(allowDrag && dragable)) return;
				
				if (!dragged){
					getParent().setCursor(new Cursor(Cursor.MOVE_CURSOR));
					dragged = true;
					x = e.getXOnScreen();
				}
				//move this tab somehow?
				int xOnS = e.getXOnScreen();
				final Point locOnScreen = getLocationOnScreen();
				int xx = getLocationOnScreen().x + (xOnS - x);
				Container par = getParent();				
				
				for (Component c : par.getComponents()) {
					if (c == this) continue;
					int dist = Math.abs(xx - c.getLocationOnScreen().x);
					int width;
					if (dist < (width=c.getWidth())) {	//intersect
						Tab t;
						if (c instanceof MenuTab || !(t=(Tab)c).allowDrag) {
							//move tab as close as possible without intersecting?
							
							
							return;
						}
						if (dist < width/2) {
							moveTabTo(getIndexOf(id), getIndexOf(t.id));
						}
						
						break;//may only intersect one other tab a a time
					}
				}
				x = xOnS;	//only move x if the tab moves
				locOnScreen.x = xx;
				SwingUtilities.convertPointFromScreen(locOnScreen, par);
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setLocation(locOnScreen);
					}
				});
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int x = getWidth() - 18;
			int y = tabHeight/5;
			
			if (e.getX() > x && e.getX() < x+closeSize && e.getY() > y && e.getY() < y+closeSize) {
				if (remState != HOVER) {
					remState = HOVER;
					repaint();
				}
			} else if (remState != NORM){
				remState = NORM;
				repaint();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (removable)	{	//if removable, check position of 'x'
					int x = getWidth() - 18;
					int y = tabHeight/5;
					
					if (remState == HOVER || (e.getX() > x && e.getX() < x+closeSize && e.getY() > y && e.getY() < y+closeSize)) {
						remState = PRESSED;
						repaint();
						return;
					}
				}
				setTabFocus(tabs.indexOf(this));
			} else if (SwingUtilities.isRightMouseButton(e)) {
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(remState == PRESSED) {
				removeTab(tabs.indexOf(this));
			}
			if (dragged) {
				getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				dragged = false;
				getParent().revalidate();
				repaintAllTabs();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {
			if (remState != NORM) {
				remState = NORM;
				repaint();
			}
		}
	}
	
	private class KeyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_TAB) {
				if (e.isControlDown()) {
					if (e.isShiftDown()) {
						shiftFocusToPre();
					} else {
						shiftFocusToNext();
					}
					e.consume();
					return true;
				}
			}
			return false;
		}
	}
	
	private class MenuTab extends JPanel implements MouseListener, PopupMenuListener {
		static final int HOVER = 1;
		static final int DEFAULT = 0;
		
		JPopupMenu menu;
		String label;
		
		int state;
		
		MenuTab (JPopupMenu menu, String label) {
			this.menu = menu;
			this.label = label;
			menu.addPopupMenuListener(this);
			
			setOpaque(false);
			Dimension size = new Dimension(80, tabHeight);
			setPreferredSize(size);
			setMaximumSize(size);
			setMinimumSize(size);
			
			addMouseListener(this);
		}
		
		@Override
		protected void paintComponent(Graphics _g) {
			super.paintComponent(_g);
			Graphics2D g = GraphicsUtil.setAntiAliasing(_g);
			//draw background
			int s = menu.isShowing()?TabRenderer.SELECT:state==HOVER?TabRenderer.HOVER:TabRenderer.DEFAULT;
			menuRenderer.render(g, getWidth(), getHeight(), s);
			
			//draw the label
			g.setColor(menuFontColor);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
			StringGraphics.drawCenteredString(g, label, getWidth()/2, getHeight()/2-2);
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				menu.show(this, 0, getHeight());
				repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			state = HOVER;
			repaint();
		}

		public void mouseExited(MouseEvent e) {
			state = DEFAULT;
			repaint();
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			repaint();
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {}
	}
}
