package com.lwan.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.HashMap;

/**
 * Layout for where one of the components can be flexable
 * The layout will automatically fill up all avaliable space up
 * with this flexable component, with margins as specified by the FlowLayout.
 * It is also allowed to set a certain width or percentage for non-textfield components.
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class FlexableLayout extends FlowLayout implements LayoutManager2 {
	public static final String FLEXABLE = "@";
	protected static final char FLEXABLE_COMPONENT = '@';
	protected static final char WIDTH_COMPONENT = '$';
	protected static final char PERCENTAGE_COMPONENT = '%';

	protected HashMap<Component, String> componentMap;

	public FlexableLayout() {
		super(LEFT);
		componentMap = new HashMap<>(8);
	}

	/**
	 * Sets a set width to the component
	 * This width will always be used up. This is both the minimum and maximum size.
	 * 
	 * @param width
	 * @return
	 */
	public static String getWidthString(int width) {
		return WIDTH_COMPONENT + Integer.toString(width);
	}

	/**
	 * Sets a percentage to the component.
	 * This percentage will always be used up. This is both the minimum and maximum size.
	 * 
	 * @param percentage
	 * @return
	 */
	public static String getWidthString(float percentage) {
		return PERCENTAGE_COMPONENT + Float.toString(percentage);
	}

	public void modifySetting (String setting, Component c) {
		componentMap.put(c, setting);
		if (!checkComponentStability()) {
			throw new IllegalArgumentException("Adding component " + c.toString() + " with arguments " + setting +
					"will cause the layout to fail");	
		}
	}
	
	@Deprecated
	/**
	 * Do not use as it will have no effect
	 */
	public void setAlignment(int alignment) {}
	
	public void addLayoutComponent(String name, Component comp) {
		boolean contains = componentMap.containsKey(comp);
		if (!contains) componentMap.put(comp, name);
		if (contains || !checkComponentStability()) {
			componentMap.remove(comp);
			throw new IllegalArgumentException("Adding component " + comp.toString() + " with arguments " + name +
					"will cause the layout to fail");
		}
	}

	protected boolean checkComponentStability () {
		//check for potentially multiple textfields, and multiple
		float percentage = 0;
		int textFieldComponentFound = 0;
		for (String s : componentMap.values()) {
			switch(s.charAt(0)) {
			case FLEXABLE_COMPONENT:
				textFieldComponentFound ++;
				break;
			case PERCENTAGE_COMPONENT:
				percentage = Float.parseFloat(s.substring(1));
				break;
			}
		}

		return percentage <= 1 && textFieldComponentFound <= 1;
	}

	public void removeLayoutComponent(Component comp) {
		componentMap.remove(comp);
	}

	public Dimension preferredLayoutSize(Container target) {
		if (!checkComponentStability()) {
			throw new IllegalStateException("Cannot get Preferred Layout - Component is instable");
		}
		synchronized (target.getTreeLock()) {
			float percentage = 0;	//percentage used up by the percentage components
			int minWidth = 0;
			int minHeight = 0;
			boolean foundFirst = false;
			int comps = target.getComponentCount();
			int numOfNonWidthComponents = 0;	//number of components not calculated yet
			//calculate minimum width of all non-percentage/textfield components
			for (int i = 0; i < comps; i++) {
				Component c = target.getComponent(i);
				if (c.isVisible()) {	//only care if component is visible
					minHeight = Math.max(minHeight, c.getPreferredSize().height);
					//percentage components will be calculated in later
					String s = componentMap.get(c);
					if (s != null && s.charAt(0) == PERCENTAGE_COMPONENT) {
						//calculate total percentage too
						percentage += Float.parseFloat(s.substring(1));
						numOfNonWidthComponents ++;
						continue;
					}
					//ether component pref width or set width. use the pref size for the textfield
					if (s == null || s.charAt(0) == FLEXABLE_COMPONENT) minWidth += c.getPreferredSize().width;
					else minWidth += Integer.parseInt(s.substring(1)); 
					if (!foundFirst) {
						minWidth += getHgap();	//first gap
						foundFirst = true;
					}
					minWidth += getHgap();	//ether a last gap, or gap between this comp and next
				}
			}
			if (!foundFirst && numOfNonWidthComponents == 0) {
				minWidth = minHeight = 0;	//no components...
			} else {
				//add on all extra gaps needed
				if (!foundFirst) minWidth += getHgap();
				if (numOfNonWidthComponents > 0) {
					minWidth += getHgap() * numOfNonWidthComponents;
					minWidth = Math.round(minWidth * (1 - percentage));
				}
				minHeight += getVgap() * 2;
			}

			//add the component's insets
			Insets insets = target.getInsets();
			minWidth += insets.left + insets.right;
			minHeight += insets.top + insets.bottom;

			return new Dimension(minWidth, minHeight);
		}
	}
	
    public Dimension minimumLayoutSize(Container target) {
		if (!checkComponentStability()) {
			throw new IllegalStateException("Cannot get Preferred Layout - Component is instable");
		}
		synchronized (target.getTreeLock()) {
			float percentage = 0;	//percentage used up by the percentage components
			int minWidth = 0;
			int minHeight = 0;
			boolean foundFirst = false;
			int comps = target.getComponentCount();
			int numOfNonWidthComponents = 0;	//number of components not calculated yet
			//calculate minimum width of all non-percentage/textfield components
			for (int i = 0; i < comps; i++) {
				Component c = target.getComponent(i);
				if (c.isVisible()) {	//only care if component is visible
					minHeight = Math.max(minHeight, c.getMinimumSize().height);
					//percentage components will be calculated in later
					String s = componentMap.get(c);
					if (s != null && s.charAt(0) == PERCENTAGE_COMPONENT) {
						//calculate total percentage too
						percentage += Float.parseFloat(s.substring(1));
						numOfNonWidthComponents ++;
						continue;
					}
					//ether component pref width or set width. use the pref size for the textfield
					if (s == null || s.charAt(0) == FLEXABLE_COMPONENT) minWidth += c.getMinimumSize().width;
					else minWidth += Integer.parseInt(s.substring(1)); 
					if (!foundFirst) {
						minWidth += getHgap();	//first gap
						foundFirst = true;
					}
					minWidth += getHgap();	//ether a last gap, or gap between this comp and next
				}
			}
			if (!foundFirst && numOfNonWidthComponents == 0) {
				minWidth = minHeight = 0;	//no components...
			} else {
				//add on all extra gaps needed
				if (!foundFirst) minWidth += getHgap();
				if (numOfNonWidthComponents > 0) {
					minWidth += getHgap() * numOfNonWidthComponents;
					minWidth = Math.round(minWidth * (1 - percentage));
				}
				minHeight += getVgap() * 2;
			}

			//add the component's insets
			Insets insets = target.getInsets();
			minWidth += insets.left + insets.right;
			minHeight += insets.top + insets.bottom;

			return new Dimension(minWidth, minHeight);
		}
    }
    
    /**
     * Lays out the container. This method lets each
     * <i>visible</i> component take
     * its preferred size by reshaping the components in the
     * target container in order to satisfy the alignment of
     * this <code>FlowLayout</code> object.
     *
     * @param target the specified component being laid out
     * @see Container
     * @see       java.awt.Container#doLayout
     */
    public void layoutContainer(Container target) {
    	int width = target.getWidth();
    	int sumWidth = 0;
    	int drawnComponents = 0;
    	Component textField = null;
    	// set the minimum, maximum and prefered sizes of specific components
    	// first pass - ignore textfield components
    	int comps = target.getComponentCount();
    	synchronized (target.getTreeLock()) {
    		for (int i = 0; i < comps; i++) {
    			Component c = target.getComponent(i);
    			if (c.isVisible()) {
    				drawnComponents += 1;
    				String s = componentMap.get(c);
    				if (s != null) {
    					if (s.charAt(0) == WIDTH_COMPONENT) {
    						int cwidth = Integer.parseInt(s.substring(1));
    						sumWidth += cwidth;
    						Dimension pref = c.getPreferredSize();
    						pref.width = cwidth;
    						c.setMinimumSize(pref);
    						c.setPreferredSize(pref);
    						c.setMaximumSize(pref);
    					} else if (s.charAt(0) == PERCENTAGE_COMPONENT) {
    						int cwidth = (int) (width * Float.parseFloat(s.substring(1)));
    						sumWidth += cwidth;
    						Dimension pref = c.getPreferredSize();
    						pref.width = cwidth;
    						c.setMinimumSize(pref);
    						c.setPreferredSize(pref);
    						c.setMaximumSize(pref);
    					} else if (s.charAt(0) == FLEXABLE_COMPONENT) {
    						textField = c;
    					}
    				} else {
    					sumWidth += c.getPreferredSize().width;
    				}
    			}
    		}

    		if (drawnComponents > 0) {
    			sumWidth += getHgap() * (drawnComponents + 1);
    			if (textField != null) {
    				int cwidth = width - sumWidth;
    				Dimension pref = textField.getPreferredSize();
    				pref.width = Math.max(pref.width, cwidth);
    				textField.setMinimumSize(pref);
    				textField.setPreferredSize(pref);
    				textField.setMaximumSize(pref);
    			}
    		}
    	}
    	
    	if (sumWidth <= width) {
    		super.layoutContainer(target);
    	}
    	
    	// reset the sizes back
    	synchronized (target.getTreeLock()) {
    		for (Component c : target.getComponents()) {
    			c.setMinimumSize(null);
    			c.setPreferredSize(null);
    			c.setMaximumSize(null);
    		}
    	}
    }

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (constraints != null)
			addLayoutComponent(constraints.toString(), comp);
	}

	@Override
	// there is no such thing as a maximum size for this layout, if a flexable component exists
	public Dimension maximumLayoutSize(Container target) {
		Dimension pref = preferredLayoutSize(target);
		for (int i = 0; i < target.getComponentCount(); i++) {
			String s = componentMap.get(target.getComponent(i));
			if (s != null && s.charAt(0) == FLEXABLE_COMPONENT) {
				pref.width = Short.MAX_VALUE;
			}
		}
		return pref;
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
		// nothing is cached so ignore
	}
}
