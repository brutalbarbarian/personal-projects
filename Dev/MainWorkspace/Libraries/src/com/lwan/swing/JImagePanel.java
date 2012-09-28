package com.lwan.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.lwan.util.ImageUtil;

/**
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class JImagePanel extends JPanel {
	private BufferedImage img;
	
	/**
	 * Constructs imagePanel with dimensions equal to specified dimensions
	 * Image willbe scaled to fit
	 * 
	 * @param img
	 * @param size
	 * @param isOpaque
	 */
	public JImagePanel (BufferedImage img, Dimension size, boolean isOpaque) {
		//scale only if necessary
		if (img.getWidth() != size.width || img.getHeight() != size.height)
			this.img = (BufferedImage) ImageUtil.getScaledInstance(img, size.width, size.height, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, false);
		else this.img = img;
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
		
		if (!isOpaque) {
			setOpaque(false);
		}
	}
	
	/**
	 * Constructs imagePanel with dimensions equal to the image's dimensions
	 * 
	 * @param img
	 * @param isOpaque
	 */
	public JImagePanel (BufferedImage img, boolean isOpaque){
		this(img, new Dimension(img.getWidth(), img.getHeight()), isOpaque);
	}
	
	protected void paintComponent (Graphics g) {
		//super.paintComponent(g);
		g.drawImage(img, 0, 0, this);
	}
}
