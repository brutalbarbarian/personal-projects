package com.lwan.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

/**
 * Class used for creating generic gradient seperators 
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class JSeperator extends JPanel{
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	public static final int OPEN_BOTH = 2;
	public static final int OPEN_LEFT = 3;
	public static final int OPEN_RIGHT = 4;
	
	public static final int SIZE_NOT_SET = -1;
	
	private static final Color[] colors = {new Color (150, 150, 150, 150), new Color (150, 150, 150, 0)};
	
	private int alignment;
	private int style;
	private boolean sizeNotSet;
	
	/**
	 * Constructor for JSeperator
	 * 
	 * @param alignment is ether HORIZONTAL or VERTICAL
	 * @param style is ether OPEN_BOTH, OPEN_LEFT, or OPEN_RIGHT
	 * @param size is ether the length of this seperator, or SIZE_NOT_SET
	 * @param thickness is the thickness of this seperator
	 */
	public JSeperator (int alignment, int style, int size, int thickness) {
		this.alignment = alignment;
		this.style = style;
		sizeNotSet = size == SIZE_NOT_SET;
		
		//setup constraints		
		if (alignment == HORIZONTAL) {
			if (sizeNotSet) size = getMaximumSize().height;
			this.setPreferredSize(new Dimension(size, thickness));
			this.setMinimumSize(new Dimension(sizeNotSet? thickness: size, thickness));
			this.setMaximumSize(new Dimension(size, thickness));
		} else if (alignment == VERTICAL) {
			if (sizeNotSet) size = getMaximumSize().height;
			this.setPreferredSize(new Dimension(thickness, size));
			this.setMinimumSize(new Dimension(thickness, sizeNotSet? thickness: size));
			this.setMaximumSize(new Dimension(thickness, size));
		}
	}
	
	public void paintComponent (Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Rectangle bounds = this.getBounds();
		//compute gradient paints
		GradientPaint[] paints = new GradientPaint[2];
		
		//setup paints
		switch (style) {
		case OPEN_BOTH:
			if (alignment == HORIZONTAL) {
				paints[0] = new GradientPaint(0, 0, colors[1], bounds.width/2,0, colors[0]);//left
				paints[1] = new GradientPaint(bounds.width/2, 0, colors[0], bounds.width,0, colors[1]);//right
			} else if (alignment == VERTICAL) {
				paints[0] = new GradientPaint(0, 0, colors[1], 0,bounds.height/2, colors[0]);//left
				paints[1] = new GradientPaint(0, bounds.height/2, colors[0], 0,bounds.height, colors[1]);//right
			}
			break;
		case OPEN_LEFT:
			if (alignment == HORIZONTAL) {
				paints[0] = new GradientPaint(0, 0, colors[1], bounds.width,0, colors[0]);//left
			} else if (alignment == VERTICAL) {
				paints[0] = new GradientPaint(0, 0, colors[1], 0,bounds.height, colors[0]);//left
			}				
			break;
		case OPEN_RIGHT:
			if (alignment == HORIZONTAL) {
				paints[0] = new GradientPaint(0, 0, colors[0], bounds.width,0, colors[1]);//left
			} else if (alignment == VERTICAL) {
				paints[0] = new GradientPaint(0, 0, colors[0], 0,bounds.height, colors[1]);//left
			}
		}

		if(alignment == HORIZONTAL) {
			if (style == OPEN_BOTH) {
				g2.setPaint(paints[0]);
				g2.fillRect(0, 0, bounds.width/2, bounds.height);
				g2.setPaint(paints[1]);
				g2.fillRect(bounds.width/2, 0, bounds.width/2, bounds.height);
			} else {
				g2.setPaint(paints[0]);
				g2.fillRect(0, 0, bounds.width, bounds.height);
			}
		} else if (alignment == VERTICAL) {
			if (style == OPEN_BOTH) {
				g2.setPaint(paints[0]);
				g2.fillRect(0, 0, bounds.width, bounds.height/2);
				g2.setPaint(paints[1]);
				g2.fillRect(0, bounds.height/2, bounds.width, bounds.height/2);				
			} else {
				g2.setPaint(paints[0]);
				g2.fillRect(0, 0, bounds.width, bounds.height);
			}
		}
	}
}
