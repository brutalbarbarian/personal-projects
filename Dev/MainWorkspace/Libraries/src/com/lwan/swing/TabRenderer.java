package com.lwan.swing;

import java.awt.Graphics2D;

/**
 * TabRenderer interface used by JTabPane to render background of tabs.
 * 
 * @author brutalbarbarian
 *
 */
public interface TabRenderer {
	/** When the tab is selected. For menu tabs, this is when the button is pushed down */
	public static final int SELECT = 1;
	/** Only applies to menu tab, this is where the mouse is hovering over the tab */
	public static final int HOVER = 2;
	/** Defualt for tabs, when the tab isn't selected. For menu tab, this is also where the mouse isn't hovering over the tab */
	public static final int DEFAULT = 0;
	
	/**
	 * Called to draw the background of tabs for JTabPane
	 * 
	 * @param g
	 * 	Graphics object used to draw the background
	 * @param width
	 * 	The width of this tab
	 * @param height
	 * 	The height of this tab
	 * @param selected
	 * 	The state of this tab. Refer to the constants SELECT, HOVER, and DEFAULT
	 */
	public void render (Graphics2D g, int width, int height, int state);
}
