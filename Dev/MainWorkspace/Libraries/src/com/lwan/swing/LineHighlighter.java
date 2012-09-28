package com.lwan.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.View;

public class LineHighlighter extends LayeredHighlighter.LayerPainter{
	protected Color highlightColor, borderColor;
	protected boolean borderTop, borderBottom; 
	protected int height;
	protected boolean highlightFromBottom;
	
	public LineHighlighter (Color c) {
		this(c, -1, true); 
	}
	
	/**
	 * If color is set to null, no borders will be drawn.
	 * 
	 * @param top
	 * @param bottom
	 * @param color
	 */
	public void setBorder(boolean top, boolean bottom, Color color) {
		borderColor = color;
		borderTop = top;
		borderBottom = bottom;
	}
	
	/**
	 * Constructor which specifies the height. Passing a negative value for height is equlivant to calling
	 * LineHighlighter (Color c) 
	 * 
	 * @param c
	 * @param height
	 * @param fromBottom
	 * 		represents if to highlight from the top or bottom of the line, assuming height > 0
	 */
	public LineHighlighter (Color c, int h, boolean fromBottom) {
		highlightColor = c;
		borderColor = null;
		height = h;
		highlightFromBottom = fromBottom;
	}
	
	public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
		System.out.println("attempting painting");
	}
	
	// --- LayerPainter methods ----------------------------
	/**
	 * Paints a portion of a highlight.
	 *
	 * @param g the graphics context
	 * @param offs0 the starting model offset >= 0
	 * @param offs1 the ending model offset >= offs1
	 * @param bounds the bounding box of the view, which is not
	 *        necessarily the region to paint.
	 * @param c the editor
	 * @param view View painting for
	 * @return region drawing occured in
	 */
	public Shape paintLayer(Graphics g, int offs0, int offs1,
			Shape bounds, JTextComponent c, View view) {
		Color color = getColor();

		if (color == null) {
			g.setColor(c.getSelectionColor());
		}
		else {
			g.setColor(color);
		}
		
		Rectangle r;
		
		if (offs0 == view.getStartOffset() &&
				offs1 == view.getEndOffset()) {
			// Contained in view, can just use bounds.
			if (bounds instanceof Rectangle) {
				r = (Rectangle) bounds;
			} else {
				r = bounds.getBounds();
			}
		} else {
			// Should only render part of View.
			try {
				// --- determine locations ---
				Shape shape = view.modelToView(offs0, Position.Bias.Forward,
						offs1,Position.Bias.Backward, bounds);
				r = (shape instanceof Rectangle) ?
						(Rectangle)shape : shape.getBounds();
			} catch (BadLocationException e) {
				// can't render
				r = null;
			}
		}

		if (r != null) {
			// If we are asked to highlight, we should draw something even
			// if the model-to-view projection is of zero width (6340106).
			if (height >= 0) {
				if (highlightFromBottom) {
					r.y = r.y + r.height - height;
				}
				r.height = height;
			}
			g.fillRect(0, r.y, c.getWidth(), r.height);
			
			if (borderColor != null) {
				g.setColor(borderColor);
				if (borderTop) {
					g.drawLine(0, r.y, c.getWidth(), r.y);
				}
				if (borderBottom) {
					g.drawLine(0, r.y + r.height, c.getWidth(), r.y + r.height);
				}
			}
		}
		return r;
	}

	private Color getColor() {
		return highlightColor;
	}
}

