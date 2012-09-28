package com.lwan.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BoxLayout;

/**
 * Layout where one component should take preferred/minimum width or height, 
 * while the remainder should resize freely.
 * </br>
 * This layout extends boxlayout, and thus must also pass in a boxlayout const for determining
 * the orientation. Also pass into the constructor ether STYLE_FIRST or STYLE_LAST for determining
 * whether the first or final component should be the one being frozen.
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class MenuLayout extends BoxLayout{
	public static final int STYLE_FIRST = 0;
	public static final int STYLE_LAST = 1;
	
	private int style;
	
	/**
	 * Constructs layout passing in the style and orientation
	 * 
	 * @param style
	 * @param orient
	 * @param parent
	 */
	public MenuLayout (int style, int orient, Container parent) {
		super(parent, orient);
		this.style = style;
	}
	
	@Override
	public void layoutContainer(Container parent) {
		Dimension origPref;
		Dimension origMax = null;
		Dimension origMin = null;
		Component comp = null;
		if (parent.getComponentCount() != 0) {
			switch(style) {
			case STYLE_FIRST:
				comp = parent.getComponent(0);
				break;
			case STYLE_LAST:
				comp = parent.getComponent(parent.getComponentCount()-1);
			}
			origPref = comp.getPreferredSize();
			origMax = comp.getMaximumSize();
			origMin = comp.getMinimumSize();
			switch(resolveAxis(getAxis(), parent.getComponentOrientation())) {
			case X_AXIS:
				comp.setMaximumSize(new Dimension(origPref.width, origMax.height));
				comp.setMinimumSize(new Dimension(origPref.width, origMin.height));
				break;
			case Y_AXIS:
				comp.setMaximumSize(new Dimension(origMax.width, origPref.height));
				comp.setMinimumSize(new Dimension(origMin.width, origPref.height));
			}
		}
		super.layoutContainer(parent);
		if (comp != null) {
			comp.setMaximumSize(origMax);
			comp.setMinimumSize(origMin);
		}
	}

    private int resolveAxis( int axis, ComponentOrientation o ) {
        int absoluteAxis;
        if( axis == LINE_AXIS ) {
            absoluteAxis = o.isHorizontal() ? X_AXIS : Y_AXIS;
        } else if( axis == PAGE_AXIS ) {
            absoluteAxis = o.isHorizontal() ? Y_AXIS : X_AXIS;
        } else {
            absoluteAxis = axis;
        }
        return absoluteAxis;
   }
	

}
