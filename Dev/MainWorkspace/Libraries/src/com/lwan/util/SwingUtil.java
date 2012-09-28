package com.lwan.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

import com.lwan.util.containers.Offset;

/**
 * Basic utility methods for swing
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class SwingUtil {
	
	/**
	 * Create a component buffer of width w and height h
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static Component createBufferPanel (int width, int height) {
		Container buffer = new Container ();
		buffer.setMinimumSize(new Dimension(width, height));
		buffer.setPreferredSize(new Dimension(width, height));
		buffer.setMaximumSize(new Dimension(width, height));
		
		return buffer;
	}
	
	/**
	 * Get an empty cursor
	 * 
	 * @return
	 */
	public static Cursor createEmptyCursor () {
		Image cursorImage = Toolkit.getDefaultToolkit().createImage(new byte[]{});  
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage,new Point(0,0),"cursor");  
	}
	
	/**
	 * Used to get a cell renderer for JLists that shows no visible selection
	 * 
	 * @return
	 */
	public static ListCellRenderer<?> createInvisibleCellRenderer () {
		return new DefaultListCellRenderer() {
		    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
		            boolean isSelected, boolean cellHasFocus) {
		        super.getListCellRendererComponent(list, value, index, false, false);
		 
		        return this;
		    }
		};
	}
	
	/**
	 * Add a menu item to a JPopupMenu attaching listener to it
	 * 
	 * @param menu
	 * @param label
	 * @param listener
	 */
	public static void addMenuItem (JPopupMenu menu, String label, ActionListener listener) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(listener);
		menu.add(item);
	}
	
	/**
	 * Get the start index and length of each new line
	 * 
	 * @param c
	 * @return
	 */
	public static List<Offset> getWrappedIndex (JTextComponent c) {
		int len = c.getDocument().getLength();
		int offset = 0;

		List<Offset> res = new Vector<>();

		try {
			while (offset < len) {
				int end = Utilities.getRowEnd(c, offset);
				if (end < 0) {
					break;
				}
				
				// Include the last character on the line
				end = Math.min(end+1, len);

				res.add(new Offset(offset, end-offset));

				offset = end;
			}
		} catch (BadLocationException e) {}
		
		return res;
	}
	
	/**
	 * Get the string from the passed in textComponent representing the text displayed by the
	 * component with all the soft new lines (added in from text wrapping) added in. 
	 * 
	 * @param c
	 * @return
	 * 	Returns null if comp does not have a size
	 */
	public static String getWrappedText(JTextComponent c) {
		int len = c.getDocument().getLength();
		int offset = 0;

		// Increase 10% for extra newlines
		StringBuffer buf = new StringBuffer((int)(len*1.10));

		try {
			while (offset < len) {
				int end = Utilities.getRowEnd(c, offset);
				if (end < 0) {
					break;
				}
				
				// Include the last character on the line
				end = Math.min(end+1, len);

				String s = c.getDocument().getText(offset, end-offset);
				buf.append(s);

				// Add a newline if s does not have one
				if (!s.endsWith("\n")) {
					buf.append('\n');
				}
				offset = end;
			}
		} catch (BadLocationException e) {}
		return buf.toString();
	}
	
	/**
	 * Get a list of all look and feels avaliable
	 *  
	 * @return
	 */
	public static List<String> getAllLookAndFeels () {
		List<String> lnf = new Vector<>();
		for (LookAndFeelInfo l : UIManager.getInstalledLookAndFeels()) {
			lnf.add(l.getName());
		}
		return lnf;
	}
	
	/**
	 * Set the look and feel of the application
	 * 
	 * @param lookAndFeel
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException
	 */
	public static void setLookAndFeel (String lookAndFeel) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		String className = null;
		for (LookAndFeelInfo l : UIManager.getInstalledLookAndFeels()) {
			if (l.getName().equalsIgnoreCase(lookAndFeel)) {
				className = l.getClassName();
				break;
			}
		}
		if (className != null)
			UIManager.setLookAndFeel(className);
	}
	
	public static void setSystemLookAndFeel () throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	
	/**
	 * Tests if 2 components - a and b, desecents from the same root
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean DesecendsFromSameFrame(Component a, Component b) {
		return SwingUtilities.getRoot(a) == SwingUtilities.getRoot(b);
	}
	
	/**
	 * Set the anchors of each edge of a component to each edge of the passed in parent.</br>
	 * The parent must have a {@link javax.swing.SpringLayout} as its {@link java.awt.LayoutManager}.</br>
	 * </br>
	 * Pass in -1 for any of the anchors and that edge will not be attached</br> 
	 * Note that this method will remove all pre-existing anchors attached to item.
	 * 
	 * @param top
	 */
	public static void setSpringAnchors (int top, int right, int bottom, int left, Component comp, Container parent) {
		if (parent == null || comp == null) {
			throw new IllegalArgumentException ("Parent and Component cannot be null");
		}
		if (!(parent.getLayout() instanceof SpringLayout)) {
			throw new IllegalArgumentException ("Parent container does not have SpringLayout as its LayoutManager");
		}
		SpringLayout layout = (SpringLayout)parent.getLayout();
		layout.removeLayoutComponent(comp);
		if (top >= 0) layout.putConstraint(SpringLayout.NORTH, parent, top, SpringLayout.NORTH, comp);
		if (right >= 0) layout.putConstraint(SpringLayout.EAST, parent, right, SpringLayout.EAST, comp);
		if (bottom >= 0) layout.putConstraint(SpringLayout.SOUTH, parent, bottom, SpringLayout.SOUTH, comp);
		if (left >= 0) layout.putConstraint(SpringLayout.WEST, parent, left, SpringLayout.WEST, comp);
	}
}
