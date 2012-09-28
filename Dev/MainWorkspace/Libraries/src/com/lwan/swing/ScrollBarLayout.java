package com.lwan.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JScrollBar;

/**
 * Layout that will always place the horizontal and vertical scroll bars in correct position,
 * and force size the remaining component to take up the entire central space.</br>
 * Do note that only the first component will be laid out. All remaining components will be ignored.</br>
 * The preferred sizes of the container will be the same as the inner component + scroll bar sizes. 
 * 
 * 
 * @author Brutalbarbarian
 *
 */
public class ScrollBarLayout implements LayoutManager {
	public static final String HORIZONTAL_BAR = "h";
	public static final String VERTICAL_BAR = "v";
	public static final String CENTRAL_PANE = "c";
	private JScrollBar hBar, vBar;
	private Component centPane;
	
	@Override
	public void addLayoutComponent(String name, Component comp) {
		if(name == HORIZONTAL_BAR && comp instanceof JScrollBar) {
			hBar = (JScrollBar)comp;
		} else if (name == VERTICAL_BAR && comp instanceof JScrollBar) {
			vBar = (JScrollBar)comp;
		} else if (name == CENTRAL_PANE) {
			centPane = comp;
		}
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		if (comp == hBar) hBar = null;
		if (comp == vBar) vBar = null;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		//assuming there is no other components other then hbar, vbar, and centPane
		Dimension d = centPane.getPreferredSize();
		int w = d.width + (vBar == null? 0 : vBar.getPreferredSize().width); 
		int h = d.height + (hBar == null? 0 : hBar.getPreferredSize().height);
		
		return new Dimension(w,h);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		//assuming there is no other components other then hbar, vbar, and centPane
		Dimension d = centPane.getMinimumSize();
		int w = d.width + (vBar == null? 0 : vBar.getPreferredSize().width); 
		int h = d.height + (hBar == null? 0 : hBar.getPreferredSize().height);

		return new Dimension(w,h);
	}

	@Override
	public void layoutContainer(Container parent) {
		Dimension d = parent.getSize();
		
		int negW = (vBar == null? 0 : vBar.getPreferredSize().width);
		int negH = (hBar == null? 0 : hBar.getPreferredSize().height);
		
		centPane.setBounds(0, 0, d.width-negW, d.height-negH);
		if (vBar != null) vBar.setBounds(d.width-negW, 0, negW, d.height-negH);
		if (hBar != null) hBar.setBounds(0, d.height-negH, d.width-negW, negH);
	}
	
}
