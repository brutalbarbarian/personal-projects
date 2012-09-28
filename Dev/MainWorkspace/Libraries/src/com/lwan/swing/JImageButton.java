package com.lwan.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;

import com.lwan.util.GraphicsUtil;
import com.lwan.util.ImageUtil;

/**
 * Button extension of standard JButton except it renders an image (or blank if there if image is null)
 * as opposed to text.</br>
 * </br>
 * This dosen't render an icon on top of the standard icon, but is rather a standalone from
 * the standard button.
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class JImageButton extends JButton implements MouseListener {
	private static final int STATE_PUSHED = 2;
	private static final int STATE_HOVER = 1;
	private static final int STATE_NORM = 0;
	
	private static final Color hoverBorder = Color.LIGHT_GRAY;
	private static final Color pressFill = new Color (192, 192, 192, 192);
	private static final Color pressBorder = Color.GRAY;
	
	private BufferedImage enImg;
	private BufferedImage disImg;
	private int state;
	
	/**
	 * Constructor for JImageButton.
	 * img may be null, in which case nothing is rendered.
	 * name is for reference purposes only 
	 * 
	 * @param width
	 * @param height
	 * @param img
	 * @param name
	 */
	public JImageButton (int width, int height, BufferedImage img, String name) {
		super(name);
		//set preferred size = width, height
		Dimension d = new Dimension(width, height);
		setMaximumSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
		setOpaque(false);
		setBorder(null);
		setUI(null);
		
		state = STATE_NORM;
		if (img != null) {
			enImg = ImageUtil.getScaledInstance(img, width - 6, height-6, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
			disImg = ImageUtil.applyGreyTint(enImg, 1.5);
		}
		
		addMouseListener(this);
	}

	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		Rectangle bounds = this.getBounds();
		
		GraphicsUtil.setAntiAliasing(g);

		if (isEnabled()) {
			//paint background
			switch (state) {
			case STATE_HOVER:
				g.setColor(hoverBorder);
				g.drawRoundRect(0, 0, bounds.width - 1, bounds.height - 1, 10, 10);
				break;
			case STATE_PUSHED:
				g.setColor(pressFill);
				g.fillRoundRect(0, 0, bounds.width - 1, bounds.height - 1, 10, 10);
				g.setColor(pressBorder);
				g.drawRoundRect(0, 0, bounds.width - 1, bounds.height - 1, 10, 10);
				break;
			}
			if (enImg != null) g.drawImage(enImg, 3, 3, this);
		} else {
			if (disImg != null) g.drawImage(disImg, 3, 3, this);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		state = STATE_HOVER;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		state = STATE_NORM;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			state = STATE_PUSHED;
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
			if (bounds.contains(e.getPoint())) {
				state = STATE_HOVER;
				fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, this.getText()));
			} else {
				state = STATE_NORM;
			}
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

}