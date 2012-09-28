package com.lwan.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class GraphicsUtil {
	private static final RenderingHints ANTI_ALIASING = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	/**
	 * Fill a polygon using the provided graphics object.</br>
	 * </br>
	 * Pass in pairs of integers as the points in the format: </br>
	 * x1,y1,x2,y2,...
	 * 
	 * @param g
	 * @param p
	 */
	public static void fillPolygon (Graphics g, int ... p) {
		if (MathUtil.isOdd(p.length) || p.length == 0) throw new IllegalArgumentException();
		int n = p.length/2;
		int [] x = new int [n];
		int [] y = new int [n];
		
		for (int i = 0; i < p.length; i++) {
			if (MathUtil.isEven(i)) {
				x[i/2] = p[i];
			} else {
				y[i/2] = p[i];
			}
		}
		g.fillPolygon(x, y, n);
	}
	
	/**
	 * Create the shadow of a bufferedimage, returns a
	 * buffered image with the shadow directly behind it.
	 * 
	 * @param image
	 * @param extra
	 * @return
	 */
    public static BufferedImage createShadowPicture(BufferedImage image, int extra) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage splash = new BufferedImage(width + extra, height + extra, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) splash.getGraphics();

        BufferedImage shadow = new BufferedImage(width + extra, height + extra, BufferedImage.TYPE_INT_ARGB); 
        Graphics g = shadow.getGraphics();
        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.3f));
        g.fillRoundRect(6, 6, width, height, 12, 12);

        g2.drawImage(shadow, getBlurOp(7), 0, 0);
        g2.drawImage(image, 0, 0, null);
        
		return splash;
    }

    private static ConvolveOp getBlurOp(int size) {
        float[] data = new float[size * size];
        float value = 1 / (float) (size * size);
        for (int i = 0; i < data.length; i++) {
            data[i] = value;
        }
        return new ConvolveOp(new Kernel(size, size, data));
    }

	
	/**
	 * Draw a polygon using the provided graphics object.</br>
	 * </br>
	 * Pass in pairs of integers as the points in the format: </br>
	 * x1,y1,x2,y2,...
	 * 
	 * @param g
	 * @param p
	 */
	public static void drawPolygon (Graphics g, int ... p) {
		if (MathUtil.isOdd(p.length) || p.length == 0) throw new IllegalArgumentException();
		int n = p.length/2;
		int [] x = new int [n];
		int [] y = new int [n];
		
		for (int i = 0; i < p.length; i++) {
			if (MathUtil.isEven(i)) {
				x[i/2] = p[i];
			} else {
				y[i/2] = p[i];
			}
		}
		g.drawPolygon(x, y, n);
	}
	
	/**
	 * Set anti alising on the passed in graphics object.
	 * Return the graphics object back
	 * 
	 * @param g
	 */
	public static final Graphics2D setAntiAliasing(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.addRenderingHints(ANTI_ALIASING);
		return g2;
	}
}
