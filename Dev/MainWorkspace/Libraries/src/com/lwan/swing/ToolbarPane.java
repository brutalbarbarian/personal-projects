package com.lwan.swing;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Toolbar pane is a fixed height panel made for a sequence of JImageButtons, as well as JTextFields and
 * JLabels. Other components are untested and may cause instability. 
 * To create a button to add to the this panel, use constructButton()
 * To add a seperator, use addSeperator()
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class ToolbarPane extends JPanel{
	private static final int height = 70;
	private static final Color background = new Color(240, 240, 240);
	private static final Color borderColor = new Color (100, 100, 100);
	private static final Color endColor = new Color (180, 180, 180);
	
	private GradientPaint gradientBackground;
	
	public ToolbarPane () {
		super (new FlowLayout(FlowLayout.LEFT));
		setBackground(background);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width * 2;
		setMinimumSize (new Dimension (0, height/2));
		setPreferredSize (new Dimension (width, height/2));
		setMaximumSize (new Dimension (width, height/2));
		setDoubleBuffered(true);
		
		gradientBackground = new GradientPaint(0, 0, background, 0, height, endColor);
	}
	
	/**
	 * Safest way to construct a JImageButton. img may be null.
	 * Safer then manually constructing a JImageButton as size may differ from panel size.
	 * 
	 * @param img
	 * @param name
	 * @return
	 */
	public JImageButton constructButton (BufferedImage img, String name) {
		return new JImageButton (24, 24, img, name);
	}
	
	/**
	 * Construct and add a vertical seperator
	 * 
	 */
	public void addSeperator () {
		add(new JSeperator(JSeperator.VERTICAL, JSeperator.OPEN_BOTH, 25, 1));
	}
	
	@Override
	protected void paintComponent (Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;

		g.setPaint(gradientBackground);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(borderColor);
		g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
	}
}
