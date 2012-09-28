package com.lwan.util;

import java.awt.Color;

public final class ColorUtil {
	public static final Color TRANSPARENT = new Color (0, 0, 0, 255); 
	
	public static Color blendRGBColors (Color c1, Color c2) {
		return new Color (	(c1.getRed() + c2.getRed())/2, 
							(c1.getGreen() + c2.getGreen())/2, 
							(c1.getBlue() + c2.getBlue())/2);
		
	}
	
	public static Color blendHSBColors (Color c1, Color c2) {
		float h, s, b;
		float [] tmp = new float [3];
		Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), tmp);
		h = tmp[0];
		s = tmp[1];
		b = tmp[2];
		Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), tmp);
		h = (tmp[0] + h)/2;
		s = (tmp[1] + s)/2;
		b = (tmp[2] + b)/2;
		
		int a = (c1.getAlpha() + c2.getAlpha())/2;
		int c = Color.HSBtoRGB(h,s,b) + (a<<24);
		return getColor(c);
	}
	
	public static Color addColors (Color c1, Color c2) {
		return new Color (	Math.min(c1.getRed()+c2.getRed(), 255),
							Math.min(c1.getGreen() + c2.getGreen(), 255),
							Math.min(c1.getBlue() + c2.getBlue(), 255));
	}
	
	/**
	 * Transform color c into a 32 bit ARGB value
	 * 
	 * @param c
	 * @return
	 */
	public static int getARGBInt(Color c) {
		return (c.getAlpha() << 24)+(c.getRed() << 16) + (c.getGreen() << 8) + c.getBlue();		
	}
	
	/**
	 * Get 32 bit rgb value (ignoring alpha) based ints representing r,g and b
	 * each in range (0-255)
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static int getRGB(int r, int g, int b) {
		return (r << 16) + (g << 8) + b;	
	}
	
	/**
	 * Get 32 bit ARGB value based ints representing r,g, b, and a
	 * each in range (0-255)
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static int getARGB (int r, int g, int b, int a) {
		return (a<<24)+getRGB(r,g,b);
	}

	/**
	 * Get the 32 bit grayscale ARGB value of gray (0-255)
	 * 
	 * @param gray
	 * @return
	 */
	public static int getGrayARGB (int gray, int a) {
		return (a<<24)+getGrayRGB(gray);
	}
	
	/**
	 * Get the 32 bit grayscale rgb value of gray (0-255) ignoring alpha
	 * 
	 * @param gray
	 * @return
	 */
	public static int getGrayRGB (int gray) {
		return (gray << 16) + (gray << 8) + gray;	
	}
	
	/**
	 * Get the gray value using the luminance(brightness) formula
	 * intensity = 0.2989*red + 0.5870*green + 0.1140*blue
	 * 
	 * @param rgb
	 * @return
	 */
	public static int getGray (int rgb) {
		int r = (int)(((rgb >> 16) & 255)*0.2989);
		int g = (int)(((rgb >> 8) & 255)*0.5870);
		int b = (int)((rgb & 255)*0.1140);
		return (r + g + b);
	}
	
	/**
	 * Get the gray value by taking the average R, G and B values
	 * grey = (red + green + blue)/3
	 * 
	 * @param rgb
	 * @return
	 */
	public static int getGrayAvg (int rgb) {
		int r = (rgb >> 16) & 255;
		int g = (rgb >> 8) & 255;
		int b = rgb & 255;
		return (r + g + b)/3;
	}
	
	/**
	 * Get the alpha value of a 32-bit ARGB color
	 * 
	 * @param rgb
	 * @return
	 */
	public static int getAlpha (int argb) {
		return argb>>24;
	}
	
	/**
	 * Get the color from a ARGB color value
	 * 
	 * @param rgb
	 * @return
	 */
	public static Color getColor (int argb) {
		int a = (argb >> 24) & 255;
		int r = argb >> 16;
		int g = (argb >> 8) & 255;
		int b = argb & 255;
		return new Color (r, g, b, a);
	}
	
	/**
	 * Get the RGB colors from a RGB color value and store into an array (ignores alpha)
	 * 
	 * @param rgb
	 * @return
	 */
	public static int [] getColorArray (int rgb) {
		int [] c = new int [3];
		c[0] = rgb >> 16;
		c[1] = (rgb >> 8) & 255;
		c[2] = rgb & 255;
		return c;
	}
	
	/**
	 * Get the 32 bit grayscale rgb value of gray (0-1.0)
	 * 
	 * @param gray
	 * @return
	 */
	public static int getGrayRGB (double gray) {
		int val = (int)(gray * 255);
		return (val << 16) + (val << 8) + val;	
	}
	
	public static Color scale (Color c, float s) {
		return new Color (Math.min((int)(c.getRed()*s), 255), Math.min((int)(c.getGreen()*s), 255), Math.min((int)(c.getBlue()*s), 255));
	}
}
