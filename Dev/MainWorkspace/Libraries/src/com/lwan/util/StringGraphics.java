package com.lwan.util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Static class containing static methods for drawing and manipulating strings.
 * 
 * @author Brutalbarbarian
 *
 */
public final class StringGraphics {
	private static final FontRenderContext frc = new FontRenderContext(null, false, false);
	private static final String test = "Sg";
	
	/**
	 * Get the width of a string when displayed using a certain font
	 * 
	 * @param s
	 * @param f
	 * @return
	 */
	public static int getStringWidth (String s, Font f) {
		return (int)f.getStringBounds(s, frc).getWidth();
	}
	
	public static int getMaxCharWidth (Font f) {
		return (int)f.getMaxCharBounds(frc).getWidth();
	}
	
	public static int getMaxCharWidth (Font f, char[] alphabet) {
		int max = 0;
		for (int i = 0; i < alphabet.length; i++) {
			int w = (int)f.getStringBounds(alphabet, i, i + 1, frc).getWidth();
			if (w > max) max = w;
		}
		return max;
	}
	
	/**
	 * Get the height of a certain font
	 * 
	 * @param f
	 * @return
	 */
	public static int getFontHeight (Font f) {
		return (int)f.getStringBounds(test, frc).getHeight();
	}
	
	/**
	 * Draw a string which wraps itself round a set width
	 * 
	 * @param g
	 * @param s
	 * @param x
	 * @param y
	 * @param width
	 */
	public static void drawString(Graphics g, String s, int x, int y, int width){
	        FontMetrics fm = g.getFontMetrics();

	        int lineHeight = fm.getHeight();

	        int curX = x;
	        int curY = y;

	        String[] words = s.split(" ");

	        for (String word : words) {
	                // find next word's width
	                int wordWidth = fm.stringWidth(word + " ");

	                // If text exceeds the width, then move to next line.
	                if (curX + wordWidth >= x + width)
	                {
	                        curY += lineHeight;
	                        curX = x;
	                }

	                g.drawString(word, curX, curY);

	                // Move over to the right for next word.
	                curX += wordWidth;
	        }
	}
	
	/**
	 * Draw substring of a string from 0 up to a designated pos, which wraps itself at a set width.
	 * 
	 * @param g
	 * @param words
	 * @param x
	 * @param y
	 * @param width
	 * @param pos
	 */
	public static void drawString (Graphics g, String[] words, int x, int y, int width, int pos) {
		FontMetrics fm = g.getFontMetrics();

        int lineHeight = fm.getHeight();

        int curX = x;
        int curY = y;
        int i = 0;

        for (String word : words) {
                // find next word's width
                int wordWidth = fm.stringWidth(word + " ");

                // If text exceeds the width, then move to next line.
                if (curX + wordWidth >= x + width)
                {
                        curY += lineHeight;
                        curX = x;
                }
                i += word.length();
                if (i >= pos) {
                	int j = pos - (i - word.length());
                	//System.out.println (word + "," + word.length() +"," + j + "," + i + "," + pos);
                	word = j==0? "": word.substring(0, j);
                	g.drawString(word, curX, curY);
                	break;
                }
                g.drawString(word, curX, curY);

                curX += wordWidth;
                //FontMetrics = new FontMetrics (new Font("44",5,5));
                
        }

	}
	
	/**
	 * Returns a string with \n inserted at the correct positions in order
	 * for the string to wrap itself at a set width. 
	 * 
	 * @param s
	 * @param f
	 * @param width
	 * @return
	 */
	public static String wrapString (String s, Font f, int width) {
		String [] words = s.split(" ");
		String fin = "";
		int length = 0;
		for (String word : words) {
			word += " ";
			int wordLength = (int)f.getStringBounds(word, frc).getWidth();
			if (length + wordLength >= width) {
				length = 0;
				fin += "\n"; 
			}
			fin += (word);
			length += wordLength;
		}
		//System.out.println (fin);
		return fin;
	}
	
	/**
	 * Draws a string which unlike normal g.drawString(), prints \n as a new line.
	 * 
	 * @param g
	 * @param s
	 * @param x
	 * @param y
	 */
	public static void drawString (Graphics g, String s, int x, int y) {
		String [] lines = s.split("\n");
		int height = g.getFontMetrics().getHeight();
		int curY = y;
		for (String line : lines) {
			g.drawString(line, x, curY);
			curY += height;
		}
		//Toolkit.getDefaultToolkit().
	}
	
	/**
	 * Find out many lines a string would take up.
	 * 
	 * @param s
	 * @return
	 */
	public static int getlines (String s) {
		String [] lines = s.split("\n");
		return lines.length;
	}
	
	/**
	 * Draw a centred (x) string that takes newlines into consideration
	 * 
	 * @param g
	 * @param words
	 * @param font
	 */
	public static void drawCenteredStringLines (Graphics g, String s, int x, int y) {
		String [] lines = s.split("\n");
		FontMetrics fm = g.getFontMetrics(g.getFont());
		int height = fm.getHeight();
		int curY = y + height;
		for (String line : lines) {
			int curX  = x - fm.stringWidth(line)/2;
			g.drawString(line, curX, curY);
			curY += height;
		}
	}

	/**
	 * Draw a centred (x) string array where each array item is on a new line
	 * 
	 * @param g
	 * @param words
	 * @param font
	 */
	public static void drawCenteredStringLines (Graphics g, List<String> s, int x, int y) {
		FontMetrics fm = g.getFontMetrics(g.getFont());
		int height = fm.getHeight();
		int curY = y + height;
		for (String line : s) {
			int curX  = x - fm.stringWidth(line)/2;
			g.drawString(line, curX, curY);
			curY += height;
		}
	}
	
	/**
	 * Draw a centred (x) string array where each array item is on a new line
	 * 
	 * @param g
	 * @param words
	 * @param font
	 */
	public static void drawCenteredStringLines (Graphics g, String[] s, int x, int y) {
		FontMetrics fm = g.getFontMetrics(g.getFont());
		int height = fm.getHeight();
		int curY = y + height;
		for (String line : s) {
			int curX  = x - fm.stringWidth(line)/2;
			g.drawString(line, curX, curY);
			curY += height;
		}
	}
	
	/**
	 * Draws a string with it centered on coordinates x and y
	 * @param g
	 * @param words
	 * @param x
	 * @param y
	 * @param f
	 */
	public static void drawCenteredString (Graphics g, String words, int x, int y) {
		
		FontMetrics fm = g.getFontMetrics(g.getFont());
		x -= (fm.stringWidth(words)/2);
		y += (fm.getMaxAscent() + fm.getMaxDescent()) / 3;

		g.drawString(words, x, y);
		
		//g.fillRect(x, y, (int)boundingBox.getWidth(), (int)boundingBox.getHeight());
	}
	
	public static void drawCenteredCharArrayWithCurser (Graphics g, char [] words, int length, int cursorPos, int x, int y) {		
		FontMetrics fm = g.getFontMetrics(g.getFont());
		//int width = fm.charsWidth(words, 0, length);
		int height = fm.getMaxAscent() + fm.getMaxDescent();
		
		x -= fm.charsWidth(words, 0, length)/2;
		y += height / 3;
		
		int cursorX = x + fm.charsWidth(words, 0, cursorPos);

		g.drawChars(words, 0, length, x, y);
		g.drawLine(cursorX, y, cursorX, y - fm.getMaxAscent()+3);
	}
	
	public static void drawCenteredCharArray (Graphics g, char [] words, int length, int x, int y) {		
		FontMetrics fm = g.getFontMetrics(g.getFont());
		//int width = fm.charsWidth(words, 0, length);
		int height = fm.getMaxAscent() + fm.getMaxDescent();
		
		x -= fm.charsWidth(words, 0, length)/2;
		y += height / 3;

		g.drawChars(words, 0, length, x, y);
	}
	
	
	/**
	 * Find the x, y coordinates of where a string would be drawn from if it was centered in
	 * the x,y coordinate parameters, using Font f.
	 * @param g
	 * @param words
	 * @param x
	 * @param y
	 * @param f
	 */
	public static Point findCenteredString (String words, int x, int y, Font f) {		
		Rectangle2D boundingBox = f.getStringBounds(words, frc);
		x -= boundingBox.getWidth()/2;
		y += boundingBox.getHeight()/2;
		return new Point (x, y);
	}
	
	/**
	 * Draw a string up a certain width using a certain font. If the string exceeds
	 * the specified width, it will truncate the string such
	 * that it'll fit into the specified width and add
	 * '...' at the end of the truncated string to indicate its
	 * been truncated.
	 * 
	 * @param g
	 * @param s
	 * @param x
	 * @param y
	 * @param f
	 * @param width
	 */
	public static void drawTruncString (Graphics g, String s, int x, int y, int width) {
		FontMetrics fm = g.getFontMetrics(g.getFont());
		if (fm.stringWidth(s) <= width) {
			g.drawString (s, x, y);
		} else {
			String dots = "... ";
			int lengthOfDots = fm.stringWidth(dots);
			if (width > lengthOfDots) { 
				width -= lengthOfDots;
				char [] string = s.toCharArray();
				int length = string.length;
				do {
					length--;
				} while (fm.charsWidth(string, 0, length) >= width);
				g.drawString (s.substring(0, length) + dots, x, y);
			} else {
				g.drawString(dots, x, y);
			}
		}
	}
	
	/**
	 * Draw a string up a certain width using a certain font. If the string exceeds
	 * the specified width, it will truncate the string such
	 * that it'll fit into the specified width and add
	 * '...' at the end of the truncated string to indicate its
	 * been truncated.
	 * Note this only centres the x, not the y.
	 * 
	 * @param g
	 * @param s
	 * @param x
	 * @param y
	 * @param f
	 * @param width
	 */
	public static void drawCentredTruncString (Graphics g, String s, int x, int y, int width) {
		FontMetrics fm = g.getFontMetrics(g.getFont());
		int strWidth;
		if ((strWidth = fm.stringWidth(s)) <= width) {			
			g.drawString (s, x - strWidth/2, y);
		} else {
			String dots = "... ";
			int lengthOfDots = fm.stringWidth(dots);
			width -= lengthOfDots;
			char [] string = s.toCharArray();
			int length = string.length;
			do {
				length--;
			} while (fm.charsWidth(string, 0, length) >= width);
			int totalWidth = fm.charsWidth(string, 0, length) + lengthOfDots;
			g.drawString (s.substring(0, length) + dots, x - totalWidth/2, y);
		}
	}
}