package com.lwan.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Element;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import com.lwan.util.GraphicsUtil;
import com.lwan.util.StringGraphics;

/**
 * A viewport containing a JTextPane with line numbers which synchronises
 * with a source JTextArea. If a viewport is also passed in, this viewport will also synchronise
 * with the source viewport.
 * 
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class LTextAreaLineNumbers extends LJViewport implements DocumentListener, ChangeListener, LViewportListener {
	protected static final char [] NUM_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'}; 
	
	protected JTextArea source;
	protected JViewport viewport;
	
	protected LTextPane lines;
	protected boolean showLine;
	protected LTextAreaLineNumbers self;
	protected int numLines;
	protected int fontWidth;
	
	public LTextAreaLineNumbers (JTextArea src, JViewport viewport, boolean showLine) {
		self = this;
		
		this.showLine = showLine;
		numLines = 0;
		
		//setup the line pane
		lines = new LTextPane();
		lines.setEditable(false);
		lines.setFocusable(false);
		StyledDocument doc = lines.getStyledDocument();
		SimpleAttributeSet att = new SimpleAttributeSet();
		StyleConstants.setAlignment(att,  StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), att, false);
		lines.setBorder(BorderFactory.createEmptyBorder());		
		
		lines.setBackground(getBackground());
		setFont(getFont());
		
		add(lines);
		
		setViewportNotifications(true);
		setSource(src);
		setViewport(viewport);
		addViewportListener(this);
		
//		setDoubleBuffered(true);
//		lines.setDoubleBuffered(true);
	}
	
	public void addHighlightLine (int line, Color c) throws BadLocationException {
		addHighlightLine(line, new LineHighlighter(c));
	}
	
	public void addHighlightLine (int line, LineHighlighter painter) throws BadLocationException {
		int pos = getLinePos(line);
		addHighlight(pos, pos+1, painter);
	}
	
	public int getLinePos(int line) {
		if (line < 10) {	//trivial and is seperate case
			return 2 * line;
		}
		
		int pos = 0;
		int preValue = 1;
		int deducted = 0;
		int value = 10;
		int digits = 2;	//has to include the '\n'
		
		while (true) {
			if (line < value) {
				pos += (line - deducted) * digits;	//end of previous line + 1 
				break;
			} else {
				pos += (value - preValue) * digits;
				deducted += (value - preValue);
				digits ++;
				preValue = value;
				value *= 10;
			}
		}
		return pos;
	}
	
	public void addHighlightLines (int start, int end, Color c) throws BadLocationException {
		addHighlightLines (start, end, new LineHighlighter(c));
	}
	
	public void addHighlightLines (int start, int end, LineHighlighter painter) throws BadLocationException {
		int startPos = getLinePos(start);
		int endPos = getLinePos(end + 1);
		addHighlight(startPos, endPos, painter);
	}
	
	public void addHighlight (int start, int end, Color c) throws BadLocationException {
		addHighlight (start, end, new DefaultHighlightPainter(c));
	}
	
	public void addHighlight (int start, int end, LayeredHighlighter.LayerPainter painter) throws BadLocationException {
		lines.getHighlighter().addHighlight(start, end, painter);
	}
	
	public void setViewport (JViewport view) {
		if (viewport != null) viewport.removeChangeListener(this);
		if (view == null) {
			setViewPosition(new Point(0,0));	//reset to starting position
			return;
		}
		viewport = view;
		viewport.addChangeListener(this);
		
		setViewPos();
	}
	
	//Paint instead of paintComponent as the line should be drawn ontop of the textPane
	public void paint (Graphics g) {
		super.paint(g);
		if (showLine) {
			Graphics2D g2 = GraphicsUtil.setAntiAliasing(g);
			g.setColor(Color.LIGHT_GRAY);
			g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
		}
	}
	
	public void setFont (Font f) {
		super.setFont(f);
		if (lines != null) lines.setFont(f);
		fontWidth = StringGraphics.getMaxCharWidth(f, NUM_ARRAY);
	}
	
	public JTextPane getTextPane () {
		return lines;
	}
	
	public void setBackground(Color c) {
		super.setBackground(c);
		if (lines != null) lines.setBackground(c);
	}
	
	public void setForeground(Color c) {
		super.setForeground(c);
		if (lines != null) lines.setForeground(c);
	}
	
	public void setSource(JTextArea src) {
		if (source != null) src.getDocument().removeDocumentListener(this);
		if (src == null) {	//setting null simply resets the linenumbers
			lines.setText("");
			return;
		}
		source = src;
		source.getDocument().addDocumentListener(this);
		
		updateText();
	}
	
	public JTextArea getSource() {
		return source;
	}
	
	protected void setViewPos () {
		if (viewport != null) {
			setViewportNotifications(false);
			setViewPosition(new Point(0, viewport.getViewPosition().y));
			setViewportNotifications(true);
		}
	}
	
	public void updateText() {
		Element root = source.getDocument().getDefaultRootElement();
		
		int count = root.getElementCount();
		
		//only bother setting if new num of lines is different
		if (count != numLines) {
			numLines = count;
			StringBuffer sb = new StringBuffer(numLines*2);
			//TODO change to use insert and delete instead of set to increase
			//efficiency when lines > 10,000.
			for (int i = 1; i <= numLines; i++) {
				sb.append(i).append('\n');
			}
			setViewportNotifications(false);	//to avoid multiple notifications
			lines.setText(sb.toString());
			setViewPos();
		}
		
	}
	
	public Dimension getPreferredSize() {
//		Dimension size = StringGraphics.getStringWidth(s, f);
		int num = numLines;
		int finWidth = 0;
		while (num > 0) {
			finWidth += fontWidth;
			num = num/10;
		}
		return new Dimension(finWidth + 16, 0);
	}
	
	public void changedUpdate(DocumentEvent e) {
		updateText();
	}
	public void insertUpdate(DocumentEvent e) {
		updateText();
	}
	public void removeUpdate(DocumentEvent e) {
		updateText();
	}

	public void stateChanged(ChangeEvent e) {
		setViewPos();	
	}

	@Override
	public void ViewportChanged(LViewportEvent e) {
		setViewPos();
	}
	
	//ensures the border is always painted
	private class LTextPane extends JTextPane {
		public void repaint(int x, int y, int w, int h) {
			self.repaint();
		}
		
		public void repaint () {
			self.repaint();
		}
	}

	public void removeAllHighlights() {
		lines.getHighlighter().removeAllHighlights();
	}
}
