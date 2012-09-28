package com.lwan.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import com.lwan.strcom.DiffInfo;
import com.lwan.strcom.PairLocale;
import com.lwan.strcom.RunnerForChars;
import com.lwan.strcom.gui.Constants;
import com.lwan.util.CollectionUtil;
import com.lwan.util.GraphicsUtil;
import com.lwan.util.SwingUtil;

/**
 * 
 * @author Brutalbarbarian
 *
 */
@SuppressWarnings("serial")
public class JComparisonPanel extends JPanel implements AdjustmentListener, MouseWheelListener, 
		LViewportListener, PropertyChangeListener, ComponentListener, MouseListener, KeyEventDispatcher{
	public static final String SELECTION_PROPERTY = "Selected";
	
	protected static final int DIVIDER_WIDTH = 60;
	protected static final int SCROLL_UNIVERSAL = 0;
	protected static final int SCROLL_LEFT = 1;
	protected static final int SCROLL_RIGHT = 2;
	protected static final double MOUSE_WHEEL_MULTI = 20;
	protected static final int SCROLL_UNIT_MULTI = 20;
	protected static final int SCROLL_BLOCK_MULTI = 100;	//5 times normal
	
	//highlights
	protected LineHighlighter lhUpdate;
	protected LineHighlighter lhInsert;
	protected LineHighlighter lhDelete;
	protected LineHighlighter lhInsertLine;
	protected LineHighlighter lhDeleteLine;
	protected LineHighlighter lhSelectTop;
	protected LineHighlighter lhSelectBottom;
	protected LineHighlighter lhLineSelect;
	protected DefaultHighlightPainter lhDiff;

	//components
	private JSplitPane innerPanel;
	private JScrollBar vertScroll, horiScroll;
	private JTextArea oldText, newText;
	private UneditableDocumentFilter oldFilter, newFilter;
	private String oldFileCached, newFileCached;	// cached for searching
	private List<Integer> oldPos, newPos;	//positions of the new lines
	private List<DiffInfo> diffInfo;	//stored diffInfo
	private LJViewport oldView, newView;	//viewports for the textareas
	private LTextAreaLineNumbers oldLines, newLines; 
	private JPanel oldPane, newPane;
	private SearchDialog searchDialog;
	
	//fields used for navigation
	private int txtYMargin;
	private int txtXMargin;
	private int totalLines;	//total number of liens
	private int lineHeight;
	private int oldPrefWidth, newPrefWidth;
		
	private int selectedDiff;
	private boolean showLineNumbers;
	private double dividerPercent;
	private boolean selectedRight;

	public JComparisonPanel(Component parent) {
		//so diffInfo isn't null for adjustVScrolling
		diffInfo = new Vector<>();

		initialiseHighlights();
		initialiseSettings();
		initialiseComponents();
		initialiseSearch(parent);
		
		addComponentListener(this);
		setDoubleBuffered(true);
		
		
	}
	
	protected void initialiseSettings() {
		showLineNumbers = true;
		selectedDiff = -1;	//none selected
		dividerPercent = 0.5;
	}
	
	protected void initialiseSearch(Component parent) {
		if (parent != null && parent instanceof Window) {
			searchDialog = new SearchDialog((Window)parent);
		}
	}

	protected void initialiseHighlights() {
		lhUpdate = new LineHighlighter(Constants.UPDATE_COLOR);              
		lhInsert = new LineHighlighter(Constants.INSERT_COLOR);              
		lhDelete = new LineHighlighter(Constants.DELETE_COLOR);              
		lhInsertLine = new LineHighlighter(Constants.INSERT_COLOR, 1, false);
		lhDeleteLine = new LineHighlighter(Constants.DELETE_COLOR, 1, false);
		lhSelectTop = new LineHighlighter(Constants.SELECTED_COLOR, 0, false);
		lhSelectTop.setBorder(true, false, Constants.SELECTED_COLOR);
		lhSelectBottom = new LineHighlighter(Constants.SELECTED_COLOR, 0, false);
		lhSelectBottom.setBorder(false, true, Constants.SELECTED_COLOR);
		lhLineSelect = new LineHighlighter(Constants.SELECTED_COLOR, 1, false);
		
		lhDiff = new DefaultHighlightPainter(Constants.DIFF_COLOR);
	}
	
	protected void initialiseComponents() {
		setLayout(new GridLayout(1,1));
		oldText = new JTextArea();
		newText = new JTextArea();
		oldText.setFont(Constants.TEXT_FONT);
		newText.setFont(Constants.TEXT_FONT);
		innerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, oldText, newText);
		vertScroll = new JScrollBar (JScrollBar.VERTICAL, 0, 0, 0, 0);
		horiScroll = new JScrollBar (JScrollBar.HORIZONTAL, 0, 0, 0, 0);
		vertScroll.setBlockIncrement(SCROLL_BLOCK_MULTI);
		vertScroll.setUnitIncrement(SCROLL_UNIT_MULTI);
		horiScroll.setBlockIncrement(SCROLL_BLOCK_MULTI);
		horiScroll.setUnitIncrement(SCROLL_UNIT_MULTI);
		
		//set innerUIPane
		JPanel innerUIPane = new JPanel();
		innerUIPane.setLayout(new BoxLayout(innerUIPane, BoxLayout.Y_AXIS));
		innerUIPane.add(new BorderPanel());
		innerUIPane.add(innerPanel);
		
		JPanel scrollPane = new JPanel();
		scrollPane.setLayout(new ScrollBarLayout());
		scrollPane.add(innerUIPane, ScrollBarLayout.CENTRAL_PANE);
		scrollPane.add(vertScroll, ScrollBarLayout.VERTICAL_BAR);
		scrollPane.add(horiScroll, ScrollBarLayout.HORIZONTAL_BAR);
		
		add(scrollPane);

		Dimension emptyDim = new Dimension(0,0);
		//text cursor is misleading and harder to select with
		Cursor txtCursor = new Cursor (Cursor.DEFAULT_CURSOR);
		oldFilter = UneditableDocumentFilter.SetFilterOnDocument(oldText.getDocument());
		newFilter = UneditableDocumentFilter.SetFilterOnDocument(newText.getDocument());
		oldText.setMinimumSize(emptyDim);
		newText.setMinimumSize(emptyDim);
		oldText.setCursor(txtCursor);
		newText.setCursor(txtCursor);
		oldText.setBorder(BorderFactory.createMatteBorder(1, 4, 1, 4, new Color(0,0,0,0)));
		newText.setBorder(BorderFactory.createMatteBorder(1, 4, 1, 4, new Color(0,0,0,0)));
		//set the selection color
		newText.setSelectionColor(Constants.TEXT_SELECTION_BACKGROUND);
		newText.setSelectedTextColor(Constants.TEXT_SELECTION_FOREGROUND);
		oldText.setSelectionColor(Constants.TEXT_SELECTION_BACKGROUND);
		oldText.setSelectedTextColor(Constants.TEXT_SELECTION_FOREGROUND);
		
		//setup margins and line heights
		JTextArea tmpTxt = new JTextArea();
		tmpTxt.setFont(Constants.TEXT_FONT);
		tmpTxt.setBorder(BorderFactory.createMatteBorder(1, 4, 1, 4, new Color(0,0,0,0)));
		Dimension margins = tmpTxt.getPreferredScrollableViewportSize();
		txtXMargin = margins.width;
		txtYMargin = margins.height;
		tmpTxt.setText("\n");
		lineHeight = tmpTxt.getPreferredScrollableViewportSize().height - txtYMargin;
		
		vertScroll.setMaximum(txtYMargin);
		horiScroll.setMaximum(txtXMargin);
		
		//setup viewports
		oldView = new LJViewport();
		oldView.setView(oldText);
		oldView.setBackground(Constants.TXT_BACKGROUND);
		newView = new LJViewport();
		newView.setView(newText);
		newView.setBackground(Constants.TXT_BACKGROUND);
		
		//setup panel with line numbers
		oldPane = new JPanel();
		newPane = new JPanel();
		oldPane.setBackground(Constants.TXT_BACKGROUND);
		newPane.setBackground(Constants.TXT_BACKGROUND);
		oldLines = new LTextAreaLineNumbers(oldText, oldView, true);
		newLines = new LTextAreaLineNumbers(newText, newView, true);
		oldLines.setBackground(Constants.TXT_BACKGROUND);
		newLines.setBackground(Constants.TXT_BACKGROUND);
		oldLines.setPreferredSize(new Dimension(50, 50));
		newLines.setPreferredSize(new Dimension(50, 50));
		oldLines.setFont(Constants.TEXT_FONT);
		newLines.setFont(Constants.TEXT_FONT);
		oldLines.getTextPane().setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0,0,0,0)));
		newLines.getTextPane().setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0,0,0,0)));
		oldPane.setLayout(new MenuLayout(MenuLayout.STYLE_FIRST, MenuLayout.X_AXIS, oldPane));
		newPane.setLayout(new MenuLayout(MenuLayout.STYLE_FIRST, MenuLayout.X_AXIS, newPane));
		oldPane.add(oldLines);
		oldPane.add(oldView);
		newPane.add(newLines);
		newPane.add(newView);
		
		//set innerUI
		innerPanel.setUI(new SliderUI());
		innerPanel.setDividerSize(DIVIDER_WIDTH);
		innerPanel.setBackground(Constants.TXT_BACKGROUND);
		innerPanel.setBorder(BorderFactory.createEmptyBorder());
		
		innerPanel.setLeftComponent(oldPane);
		innerPanel.setRightComponent(newPane);
		innerPanel.setDividerLocation(0.5);
		
		//add listeners
		innerPanel.addPropertyChangeListener("dividerLocation", this);
		vertScroll.addAdjustmentListener(this);
		horiScroll.addAdjustmentListener(this);
		oldText.addMouseWheelListener(this);
		newText.addMouseWheelListener(this);
		innerPanel.addMouseWheelListener(this);
		oldText.addMouseListener(this);
		newText.addMouseListener(this);
		oldLines.getTextPane().addMouseListener(this);
		newLines.getTextPane().addMouseListener(this);
		
		newView.addViewportListener(this);
		oldView.addViewportListener(this);
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}

	public void setShowNumberLines(boolean showLines){
		if (showLines != showLineNumbers) {
			showLineNumbers = showLines;
			oldLines.setVisible(showLines);
			newLines.setVisible(showLines);
			propertyChange(null);
		}
	}
	
	public boolean getShowNumberLines(){
		return showLineNumbers;
	}
	
	public boolean setNextSelected() {
		if (!hasNextHighlight()) {
			return false;
		}
		highlightSelected(selectedDiff + 1);
		centreOnSelected();
		return true;
	}
	
	public boolean hasNextHighlight() {
		return selectedDiff < diffInfo.size() - 1;
	}
	
	public boolean setPrevSelected() {
		if (!hasPrevHighlight()) {
			return false;
		}
		highlightSelected(selectedDiff - 1);
		centreOnSelected();
		return true;
	}
	
	public void centreOnSelected() {
		adjustVScrolling(diffInfo.get(selectedDiff).getUniLocale().Start * lineHeight + txtYMargin/2, SCROLL_UNIVERSAL);
		adjustHScrolling(0, SCROLL_UNIVERSAL);	//not sure if this is necesary?
	}
	
	public boolean hasPrevHighlight() {
		return selectedDiff > 0;
	}
	
	public void setData (List<String> oldFile, List<String> newFile, List<DiffInfo> diff) {		
		//to create an extra line on the bottom
		oldFile.add("");
		newFile.add("");
		oldPos = new Vector<>(oldFile.size());
		newPos = new Vector<>(newFile.size());
		StringBuffer sb = new StringBuffer();
		for (String s : oldFile) {
			oldPos.add(sb.length());
			sb.append(s);
			sb.append('\n');
		}
		oldFilter.setEdit(true);
		oldFileCached = sb.toString();
		oldText.setText(oldFileCached);
		oldFilter.setEdit(false);
		sb = new StringBuffer();
		
		for (String s : newFile) {
			newPos.add(sb.length());
			sb.append(s);
			sb.append('\n');
		}
		newFilter.setEdit(true);
		newFileCached = sb.toString();
		newText.setText(newFileCached);
		newFilter.setEdit(false);
		
		//ensure line numbers are correct
		oldLines.updateText();
		newLines.updateText();
		
		//clump all the diff together into updates where possible
		processResults(diff, oldFile, newFile);
		
		//add highlights
		setupHighlights();
		
		//set new maximum sizes for textFields
		newText.setPreferredSize(null);
		oldText.setPreferredSize(null);
		Dimension oldSize = oldText.getPreferredSize();
		Dimension newSize = newText.getPreferredSize();
		oldPrefWidth = oldSize.width;
		newPrefWidth = newSize.width;
//		int maxY = Math.max(oldSize.height, newSize.height) + 10;	// in order to fix the case where stuttering V scrolling occurs with small files... not sure why
		//x is short.max to ensure that the viewport can stretch appropriatly when the screen becomes too wide
		Dimension maxSize = new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
		newText.setPreferredSize(maxSize);
		oldText.setPreferredSize(maxSize);
		
		resetScroll();
		//reset carat positions for searching
		oldText.setCaretPosition(0);
		newText.setCaretPosition(0);
	}
	
	protected void resetScroll () {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				vertScroll.setMaximum(totalLines * lineHeight + txtYMargin);
				vertScroll.setVisibleAmount(Math.min(oldView.getHeight(), vertScroll.getMaximum()));
				adjustHScrollBar();
				adjustVScrolling(0, SCROLL_UNIVERSAL);
			}
		});
	}
	
	protected void processResults (List<DiffInfo> diff, List<String> oldFile, List<String> newFile) {
		diffInfo.clear();
		DiffInfo cur = null;
		totalLines = 0;
		
//		CollectionUtil.printV(diff, "\n");
//		System.out.println("------");
		
		for(DiffInfo d : diff) {
			if (cur == null) {
				cur = d;
				cur.getUniLocale().Start = totalLines;
			}
			if (d.ChangeType == DiffInfo.TYPE_NO_CHANGE) {
				if (cur != d) {
					cur.getUniLocale().End = totalLines;
					diffInfo.add(cur);
				}
				cur = null;
			} else if (cur != d) {
				//remove any incorrect positioning due to changing to TYPE_UPDATE
				if (cur.ChangeType == DiffInfo.TYPE_INSERT) cur.oldLocale.Start ++;
				else if (cur.ChangeType == DiffInfo.TYPE_DELETE) cur.newLocale.Start ++;
				
				cur.ChangeType = DiffInfo.TYPE_UPDATE;
				
				cur.newLocale.End = d.newLocale.End;
				cur.oldLocale.End = d.oldLocale.End;
			}
			totalLines += Math.max(d.oldLocale.End - d.oldLocale.Start,
					d.newLocale.End - d.newLocale.Start) + 1;
		}		
		if (cur != null) {
			cur.getUniLocale().End = totalLines;
			diffInfo.add(cur);
		}
		
		
		//add in specific character diff
		int offset;
		for (DiffInfo d : diffInfo) {
			switch (d.ChangeType) {
			case DiffInfo.TYPE_INSERT:
				offset = 0;
				for (int i = d.newLocale.Start; i <= d.newLocale.End; i++) {
					offset += newFile.get(i).length() + 1;
				}
				d.getNewUpdateDiffs().add(new PairLocale(0, offset));
				break;
			case DiffInfo.TYPE_DELETE:
				offset = 0;
				for (int i = d.oldLocale.Start; i <= d.oldLocale.End; i++) {
					offset += oldFile.get(i).length() + 1;
				}
				d.getOldUpdateDiffs().add(new PairLocale(0, offset));
				break;
			case DiffInfo.TYPE_UPDATE:
				//get list of diff
				List<DiffInfo> upDiff = RunnerForChars.run(
						CollectionUtil.CollapseStringList(oldFile, d.oldLocale.Start, d.oldLocale.End + 1, "\n"), 
						CollectionUtil.CollapseStringList(newFile, d.newLocale.Start, d.newLocale.End + 1, "\n"));
				for (DiffInfo ud : upDiff) {
					switch (ud.ChangeType) {
					case DiffInfo.TYPE_UPDATE:
						d.getNewUpdateDiffs().add(ud.newLocale);
						d.getOldUpdateDiffs().add(ud.oldLocale);	
						break;
					case DiffInfo.TYPE_INSERT:
						d.getNewUpdateDiffs().add(ud.newLocale);
						break;
					case DiffInfo.TYPE_DELETE:
						d.getOldUpdateDiffs().add(ud.oldLocale);
						break;
					}
				}
			}
		}
		
		CollectionUtil.printV(diffInfo, "\n");
	}
	
	protected void highlightSelected (int index) {
		//set the selected highlight
		if (selectedDiff != index) {
			int oldSelected = selectedDiff;
			selectedDiff = index;

			//resetup the highlights
			setupHighlights();
			firePropertyChange(SELECTION_PROPERTY, oldSelected, selectedDiff);
			repaint();
		}
	}
	
	protected void tryHighlightSelected (Point point, boolean isNew) {
		//try find the diff underneath the point. if one can be found, it'll call
		//highlightSelected with that as parameter
		//use binary search to find diff
		lowerD = upperD = null;	//reset to null
		findUniPos(0, diffInfo.size()-1, point.y, isNew, lineHeight/2);
		if (lowerD == upperD && lowerD != null) {
			highlightSelected(diffInfo.indexOf(lowerD));
		}
	}

	private class SliderUI extends BasicSplitPaneUI {
		protected void installDefaults(){
			super.installDefaults();
			getDivider().setBorder(BorderFactory.createEmptyBorder());
		}
		
		public void paint(Graphics _g, JComponent jc) {
			super.paint(_g, jc);
			Graphics2D g = GraphicsUtil.setAntiAliasing(_g);
			int x = getDividerLocation(splitPane);
			int xpw = x+DIVIDER_WIDTH;
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(x, 0, x, jc.getHeight());
			g.drawLine(xpw-1, 0, xpw-1, jc.getHeight());
			g.setColor(new Color(240,240,240,150));
			g.fillRect(x, 0, DIVIDER_WIDTH, jc.getHeight());
			
			if (diffInfo == null) return;
			
			Rectangle tmpR;
			int oTop = 0, oBot = 0, nTop = 0, nBot = 0;
			TextUI oUI = oldText.getUI();
			TextUI nUI = newText.getUI();
			int i = 0;
			try {
				for (DiffInfo d : diffInfo) {
					//System.out.println();
					switch (d.ChangeType) {
					case DiffInfo.TYPE_UPDATE:
						//set color
						g.setColor(Constants.UPDATE_COLOR);
						//setup left y pos
						tmpR =  oUI.modelToView(oldText, oldPos.get(d.oldLocale.Start));
						oTop = tmpR.y;
						tmpR = oUI.modelToView(oldText, oldPos.get(d.oldLocale.End));
						oBot = tmpR.y + tmpR.height;
						//setup right y pos
						tmpR = nUI.modelToView(newText, newPos.get(d.newLocale.Start));
						nTop = tmpR.y;
						tmpR = nUI.modelToView(newText, newPos.get(d.newLocale.End));
						nBot = tmpR.y + tmpR.height;
						break;
					case DiffInfo.TYPE_INSERT:
						//set color
						g.setColor(Constants.INSERT_COLOR);
						//setup left y pos
						tmpR =  oUI.modelToView(oldText, oldPos.get(d.oldLocale.Start+1));
						oTop = tmpR.y;
						tmpR = oUI.modelToView(oldText, oldPos.get(d.oldLocale.End+1));
						oBot = tmpR.y;
						//setup right y pos
						tmpR = nUI.modelToView(newText, newPos.get(d.newLocale.Start));
						nTop = tmpR.y;
						tmpR = nUI.modelToView(newText, newPos.get(d.newLocale.End));
						nBot = tmpR.y + tmpR.height;
						break;
					case DiffInfo.TYPE_DELETE:
						//set color
						g.setColor(Constants.DELETE_COLOR);
						//setup left y pos
						tmpR =  oUI.modelToView(oldText, oldPos.get(d.oldLocale.Start));
						oTop = tmpR.y;
						tmpR = oUI.modelToView(oldText, oldPos.get(d.oldLocale.End));
						oBot = tmpR.y + tmpR.height;
						//setup right y pos
						tmpR = nUI.modelToView(newText, newPos.get(d.newLocale.Start+1));
						nTop = tmpR.y;
						tmpR = nUI.modelToView(newText, newPos.get(d.newLocale.End+1));
						nBot = tmpR.y;
						break;
					}
					//draw connector
					oTop -= oldView.getViewPosition().y;
					oBot -= oldView.getViewPosition().y;
					nTop -= newView.getViewPosition().y;
					nBot -= newView.getViewPosition().y;
					GraphicsUtil.fillPolygon(g, x, oTop, x, oBot, xpw, nBot, xpw, nTop);
					
					//draw highlight line if needed
					if (i == selectedDiff) {
						g.setColor(Constants.SELECTED_COLOR);
						g.drawLine(x, oTop, xpw, nTop);
						g.drawLine(x, oBot, xpw, nBot);
					}
						
					i++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void setupHighlights () {
		oldText.getHighlighter().removeAllHighlights();
		newText.getHighlighter().removeAllHighlights();
		oldLines.removeAllHighlights();
		newLines.removeAllHighlights();
		//add primary diff highlights
		int i = 0;
		LineHighlighter lh;
		try {
			for (DiffInfo d : diffInfo) {
				switch (d.ChangeType) {
				case DiffInfo.TYPE_UPDATE:
					if (i == selectedDiff) {
						oldText.getHighlighter().addHighlight(oldPos.get(d.oldLocale.Start), 
								oldPos.get(d.oldLocale.Start) + 1, lhSelectTop);
						oldText.getHighlighter().addHighlight(oldPos.get(d.oldLocale.End + 1), 
								oldPos.get(d.oldLocale.End + 1) + 1, lhSelectBottom);
						oldLines.addHighlightLine(d.oldLocale.Start, lhSelectTop);
						oldLines.addHighlightLine(d.oldLocale.End + 1, lhSelectBottom);
						newText.getHighlighter().addHighlight(newPos.get(d.newLocale.Start),
								newPos.get(d.newLocale.Start) + 1, lhSelectTop);
						newText.getHighlighter().addHighlight(newPos.get(d.newLocale.End + 1),
								newPos.get(d.newLocale.End + 1) + 1, lhSelectBottom);
						newLines.addHighlightLine(d.newLocale.Start, lhSelectTop);
						newLines.addHighlightLine(d.newLocale.End + 1, lhSelectBottom);
					}
					oldText.getHighlighter().addHighlight(oldPos.get(d.oldLocale.Start),
							oldPos.get(d.oldLocale.End)+1, lhUpdate);
					oldLines.addHighlightLines(d.oldLocale.Start, d.oldLocale.End, lhUpdate);
					newText.getHighlighter().addHighlight(newPos.get(d.newLocale.Start), 
							newPos.get(d.newLocale.End)+1, lhUpdate);
					newLines.addHighlightLines(d.newLocale.Start, d.newLocale.End, lhUpdate);
					break;
				case DiffInfo.TYPE_INSERT:
					if (i == selectedDiff) {
						newText.getHighlighter().addHighlight(newPos.get(d.newLocale.Start), 
								newPos.get(d.newLocale.Start) + 1, lhSelectTop);
						newText.getHighlighter().addHighlight(newPos.get(d.newLocale.End + 1),
								newPos.get(d.newLocale.End + 1) + 1, lhSelectBottom);
						newLines.addHighlightLine(d.newLocale.Start, lhSelectTop);
						newLines.addHighlightLine(d.newLocale.End + 1, lhSelectBottom);
						lh = lhLineSelect;
					} else {
						lh = lhInsertLine;
					}
					newText.getHighlighter().addHighlight(newPos.get(d.newLocale.Start), 
							newPos.get(d.newLocale.End)+1, lhInsert);
					newLines.addHighlightLines(d.newLocale.Start, d.newLocale.End, lhInsert);
					oldText.getHighlighter().addHighlight(oldPos.get(d.oldLocale.Start+1),
							oldPos.get(d.oldLocale.Start+1)+1, lh);
					oldLines.addHighlightLines(d.oldLocale.Start+1, d.oldLocale.Start+1, lh);
					break;
				case DiffInfo.TYPE_DELETE:
					if (i == selectedDiff) {
						oldText.getHighlighter().addHighlight(oldPos.get(d.oldLocale.Start),
								oldPos.get(d.oldLocale.Start) + 1, lhSelectTop);
						oldText.getHighlighter().addHighlight(oldPos.get(d.oldLocale.End + 1), 
								oldPos.get(d.oldLocale.End + 1) + 1, lhSelectBottom);
						oldLines.addHighlightLine(d.oldLocale.Start, lhSelectTop);
						oldLines.addHighlightLine(d.oldLocale.End + 1, lhSelectBottom);
						lh = lhLineSelect;
					} else {
						lh = lhDeleteLine;
					}
					oldText.getHighlighter().addHighlight(oldPos.get(d.oldLocale.Start),
							oldPos.get(d.oldLocale.End)+1, lhDelete);
					oldLines.addHighlightLines(d.oldLocale.Start, d.oldLocale.End, lhDelete);
					newText.getHighlighter().addHighlight(newPos.get(d.newLocale.Start+1),
							newPos.get(d.newLocale.Start+1)+1, lh);
					newLines.addHighlightLines(d.newLocale.Start+1, d.newLocale.Start+1, lh);
					break;
				}
				//add in specific diff highlights
				int offset;
				offset = d.ChangeType == DiffInfo.TYPE_INSERT ? 0 : oldPos.get(d.oldLocale.Start);
				for (PairLocale p : d.getOldUpdateDiffs()) {
					oldText.getHighlighter().addHighlight(p.Start + offset, p.End + offset, lhDiff);
				}
				offset = d.ChangeType == DiffInfo.TYPE_DELETE ? 0 : newPos.get(d.newLocale.Start);
				for (PairLocale p : d.getNewUpdateDiffs()) {
					newText.getHighlighter().addHighlight(p.Start + offset, p.End + offset, lhDiff);
				}
				//for keeping track of which highlight is selected
				i++;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * new value ether represents the new pixel position of the view ports,
	 * or the new position of the universal scroll </br>
	 * </br>
	 * universal position means the position of where the centreline is in
	 * relation to the total height of the max(diffs) added together
	 * 
	 * @param newValue
	 * @param type
	 */
	protected void adjustVScrolling (int newValue, int type) {
		int centMvt = vertScroll.getVisibleAmount()/2;
		int uniPos = 0;
		lowerD = upperD = null;	//should reset to null first
		if (type != SCROLL_UNIVERSAL) {
			LJViewport view = type == SCROLL_LEFT? oldView : newView;
			int centPos = view.getViewPosition().y + centMvt;
			findUniPos(0, diffInfo.size()-1, centPos, type == SCROLL_RIGHT, 0);
			//find uniPos
			if (lowerD == null) {
				uniPos = centPos;
			} else if (upperD != lowerD) {
				PairLocale loc = type == SCROLL_LEFT? lowerD.oldLocale : lowerD.newLocale;
				uniPos = centPos - lineHeight * (loc.End - lowerD.getUniLocale().End - 1); 
			} else {
				PairLocale loc = type == SCROLL_LEFT? lowerD.oldLocale : lowerD.newLocale;
				
				int low = lowerD.getUniLocale().Start * lineHeight + txtYMargin/2;
				int high = lowerD.getUniLocale().End * lineHeight + txtYMargin/2;
				
				uniPos = (int)((((double)(centPos - txtYMargin/2))/lineHeight - loc.Start) * 
						(high - low) /((double)(loc.End + 1 - loc.Start)) + low);
			}
		} else {
			uniPos = newValue;
		}
		if (uniPos < centMvt) {
			uniPos = centMvt;
		} else if (uniPos > vertScroll.getMaximum() - centMvt) {
			uniPos = vertScroll.getMaximum() - centMvt;
		}
		//find and set position for both left and right viewports
		if (upperD == null && lowerD == null) {	//don't need to refind uniPos if already found.
			findDiff(0, diffInfo.size()-1, uniPos);
		}
		//System.out.println(newValue + "," + upperD + "," + lowerD);
		int leftPos, rightPos;
		if (lowerD == null) {	//including if upperD is also null
			//equal movement. viewports will adjust as necessary
			leftPos = rightPos = uniPos - centMvt;	
		} else if (upperD != lowerD) {	//if lower is defined, but not same as upper
			int mvtReq = uniPos - ((lowerD.getUniLocale().End-1) 
									* lineHeight + txtYMargin/2) - centMvt;
			leftPos = lowerD.oldLocale.End  * lineHeight + txtYMargin/2 + mvtReq;
			rightPos = lowerD.newLocale.End  * lineHeight + txtYMargin/2 + mvtReq;
		} else {	//centre line is in between diffs
			int low = lowerD.getUniLocale().Start * lineHeight + txtYMargin/2;
			int high = lowerD.getUniLocale().End * lineHeight + txtYMargin/2;
			double movePercent = ((double)(uniPos - low))/(high-low);
			//System.out.println(movePercent + "," + low + "," + high + "," + lowerD.getUniLocale().Start + "," + lowerD.getUniLocale().End + uniPos);
			leftPos = (int)	(lineHeight * (lowerD.oldLocale.Start + 
							(lowerD.oldLocale.End + 1 - lowerD.oldLocale.Start) * 
							movePercent)) - centMvt + txtYMargin/2;
			rightPos = (int)(lineHeight * (lowerD.newLocale.Start + 
							(lowerD.newLocale.End + 1 - lowerD.newLocale.Start) * 
							movePercent)) - centMvt + txtYMargin/2;
		}
		
		upperD = lowerD = null;
		
		//remove listeners to stop recursive events from occuring
		vertScroll.removeAdjustmentListener(this);
		oldView.setViewportNotifications(false);
		newView.setViewportNotifications(false);
		//adjust everything as needed
		oldView.setViewPosition(new Point(oldView.getViewPosition().x, leftPos));
		newView.setViewPosition(new Point(newView.getViewPosition().x, rightPos));
		vertScroll.setValue(uniPos - centMvt);
		//re-add listeners
		oldView.setViewportNotifications(true);
		newView.setViewportNotifications(true);
		vertScroll.addAdjustmentListener(this);
		
		//unfortunaly this is necessary to keep the splitpane's look synchronised with viewports
		//repaint would be prefered but the painting event dosen't actually occur after the viewports
		//has been painted and leaves a split second of incorrect view
//		innerPanel.paintImmediately(innerPanel.getBounds()); 
		innerPanel.repaint();
		
	}
	
	public void selectSide(boolean sideIsRight) {
		if (sideIsRight != selectedRight) {
			selectedRight = sideIsRight;
//			if (selectedRight) {
//				oldPane.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
//				newPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Constants.PANEL_SELECTION_BORDER));
//			} else {
//				oldPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Constants.PANEL_SELECTION_BORDER));
//				newPane.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
//			}
		}
	}
	
	protected DiffInfo upperD, lowerD;//used for findDiff() and findUniPos()
	
	protected void findUniPos (int lowerLim, int upperLim, int centPos, boolean isNew, int allowMargin) {
		if (lowerLim > upperLim) return;
		
		int pos = (upperLim + lowerLim)/2;
		DiffInfo curDiff = diffInfo.get(pos);
		int startD, endD;
		PairLocale loc = isNew? curDiff.newLocale : curDiff.oldLocale;
		startD = txtYMargin/2 + loc.Start * lineHeight - allowMargin;
		endD = txtYMargin/2 + loc.End * lineHeight + allowMargin;
		if (centPos < startD) {
			upperD = curDiff;
			findUniPos(lowerLim, pos-1, centPos, isNew, allowMargin);
		} else if (centPos <= endD) {
			upperD = lowerD = curDiff;
		} else {
			lowerD = curDiff;
			findUniPos(pos+1, upperLim, centPos, isNew, allowMargin);
		}
	}
	
	protected void findDiff (int lowerLim, int upperLim, int uniPos) {
		if (lowerLim > upperLim) return;
		
		int pos = (upperLim + lowerLim)/2;
		DiffInfo curDiff = diffInfo.get(pos);
		int startD, endD;
		startD = txtYMargin/2 + curDiff.getUniLocale().Start * lineHeight;
		endD = txtYMargin/2 + curDiff.getUniLocale().End * lineHeight;
		if (uniPos < startD) {
			upperD = curDiff;
			findDiff(lowerLim, pos-1, uniPos);
		} else if (uniPos <= endD) {
			upperD = lowerD = curDiff;
		} else {
			lowerD = curDiff;
			findDiff(pos+1, upperLim, uniPos);
		}
	}
	
	//Adjust the maximum and visible area of the horizontal scroll bar. 
	//Makes the scrollbar take the length of the side with the greater visible amount
	protected void adjustHScrollBar() { 
		int leftScrollable = Math.max(0, oldPrefWidth - oldView.getVisibleRect().width);
		int rightScrollable = Math.max(0, newPrefWidth - newView.getVisibleRect().width);
		
		if (leftScrollable > rightScrollable) {
			int horiPos = oldView.getViewPosition().x;
			horiScroll.setMaximum(oldPrefWidth);
			horiScroll.setVisibleAmount(Math.min(oldPrefWidth, oldView.getVisibleRect().width));
			adjustHScrolling(horiPos, SCROLL_UNIVERSAL);
		} else {
			int horiPos = newView.getViewPosition().x;
			horiScroll.setMaximum(newPrefWidth);
			horiScroll.setVisibleAmount(Math.min(newPrefWidth, newView.getVisibleRect().width));
			adjustHScrolling(horiPos, SCROLL_UNIVERSAL);
		}
		
	}

	protected void adjustHScrolling (int newValue, int type){
		int horiPos;

		int leftScrollable = Math.max(oldPrefWidth - oldView.getVisibleRect().width, 0);
		int rightScrollable = Math.max(newPrefWidth - newView.getVisibleRect().width, 0);
		//if the scroll values are that of the left side or the right side
		boolean isLeft = leftScrollable > rightScrollable;
		int leftPos, rightPos;

		if (type != SCROLL_UNIVERSAL) {	//find the universal horiPos
			if ((isLeft && type == SCROLL_LEFT) || (!isLeft && type == SCROLL_RIGHT)) {
				horiPos = newValue;				
			} else if (isLeft) {
				horiPos = (int) ((newValue / (double)leftScrollable) * rightScrollable);
			} else {	//isRight
				horiPos = (int) ((newValue / (double)rightScrollable) * leftScrollable);				
			}
		} else {
			horiPos = newValue;
		}

		//ensure horiPos is within bounds
		if (horiPos < 0) {	
			horiPos = 0;
		} else if (horiPos + horiScroll.getVisibleAmount() > horiScroll.getMaximum()) {
			horiPos = horiScroll.getMaximum() - horiScroll.getVisibleAmount();
		}

		if (isLeft) {
			leftPos = horiPos;
			rightPos = (int) ((horiPos / (double)leftScrollable) * rightScrollable);
		} else {
			rightPos = horiPos;
			leftPos = (int) ((horiPos / (double)rightScrollable) * leftScrollable);
		}

		//ensure both left and right pos are within range of itself
		leftPos = Math.min(leftScrollable, Math.max(0, leftPos));
		rightPos = Math.min(rightScrollable, Math.max(0, rightPos));
		
		//remove listeners to stop recursive events from occuring
		horiScroll.removeAdjustmentListener(this);
		oldView.setViewportNotifications(false);
		newView.setViewportNotifications(false);
		//adjust everything as needed
		oldView.setViewPosition(new Point(leftPos, oldView.getViewPosition().y));
		newView.setViewPosition(new Point(rightPos, newView.getViewPosition().y));
		horiScroll.setValue(horiPos);
		//re-add listeners
		oldView.setViewportNotifications(true);
		newView.setViewportNotifications(true);
		horiScroll.addAdjustmentListener(this);

		repaint();	//repaint
	}
	
	//called whenever the scrollbar value is changed
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource() == vertScroll) {
			adjustVScrolling(e.getValue() + vertScroll.getVisibleAmount()/2, SCROLL_UNIVERSAL);
		} else {
			adjustHScrolling(e.getValue(), SCROLL_UNIVERSAL);
		}
	}

	//called whenever the mouse wheel is moved
	public void mouseWheelMoved(MouseWheelEvent e) {
		adjustVScrolling(vertScroll.getValue() + vertScroll.getVisibleAmount()/2 +
				(int)(e.getWheelRotation() * MOUSE_WHEEL_MULTI), SCROLL_UNIVERSAL);
	}

	//called when either viewport's view is changed (i.e scrolling from dragging)
	public void ViewportChanged(LViewportEvent e) {
		if (e.changeType == LViewportEvent.RESHAPE) return;
		
		int type = e.getSource() == newView? SCROLL_RIGHT : SCROLL_LEFT;
		Point p = ((LJViewport)e.getSource()).getViewPosition();
		adjustVScrolling(p.y, type);
		adjustHScrolling(p.x, type);
		repaint();
	}
	
	//Called when divider location is changed
	public void propertyChange(PropertyChangeEvent e) {
		//only effects horizontal
		if (innerPanel.getWidth() > 0) {
			//reason for this is the dimensions after resizing a window may not be correct
			//biggest problem with this is that there will be a delay when resizing
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					adjustHScrollBar();
					dividerPercent = ((double)innerPanel.getDividerLocation() +
							DIVIDER_WIDTH/2)/innerPanel.getWidth();
				}
			});			
		}
	
	}

	//when the entire component is resized
	public void componentResized(ComponentEvent e) {
		//universal position = value + extent/2
		int uniPos = vertScroll.getValue() + vertScroll.getVisibleAmount()/2;
		//setup Y scrollbar extent
		vertScroll.setVisibleAmount(Math.min(oldView.getHeight(), vertScroll.getMaximum()));
		//move centreline to last known mid line
		innerPanel.setDividerLocation(dividerPercent);	//this will also trigger propertyChange
//		adjustHScrollBar();
		//setup X scrollbar and scrolling
		adjustVScrolling(uniPos, SCROLL_UNIVERSAL);
	}
	
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			boolean isRight = e.getSource() == newText || e.getSource() == newLines.getTextPane();
			//highlighted the diff (if any) selected
			tryHighlightSelected(e.getPoint(), isRight);
			//select the left or right
			selectSide(isRight);
		}
	}
	
	protected class SearchDialog extends JDialog implements ActionListener {
		//components
		JTextField txtFind;
		JRadioButton rbspStart, rbspDiff, rbspCursor, rbscpAll, rbscpOld, rbscpNew;
		ButtonGroup rbgDirection, rbgScope;
		JCheckBox cbCase, cbWhole, cbWrap, cbExtended, cbReg, cbDiffOnly;
		JButton btnFind, btnClose;
		
		//cached results from previous search. this should be cleared if anything is changed 
		List<Object> cachedResults;
		
		SearchDialog(Window win) {
			super(win, "Find");
			initialise();
		}
		
		//TODO
		void initialise() {
			//initialise settings
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			JPanel content = new JPanel();
			content.setDoubleBuffered(true);
			setContentPane(content);
			
			//initialise components
			setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
			//first line
			JPanel top = new JPanel();
			FlexableLayout topLayout = new FlexableLayout();
			topLayout.setHgap(5);
			top.setLayout(topLayout);
			top.add(new JLabel("Find What:"));
			txtFind = new JTextField();
			top.add(txtFind, FlexableLayout.FLEXABLE);
			top.setAlignmentX(0.5f);
			add(top);
			
			//options
			JPanel options = new JPanel(new GridLayout(1, 2));
			JPanel direction = new JPanel();
			direction.setBorder(BorderFactory.createTitledBorder("Start Point"));
			direction.setLayout(new BoxLayout(direction, BoxLayout.Y_AXIS));
			rbgDirection = new ButtonGroup();
			rbspStart = new JRadioButton("Start of File");
			rbspDiff = new JRadioButton("Selected Diff");
			rbspCursor = new JRadioButton("Caret Position");
			rbspStart.setMnemonic(KeyEvent.VK_S);
			rbspDiff.setMnemonic(KeyEvent.VK_D);
			rbspCursor.setMnemonic(KeyEvent.VK_P);
			rbgDirection.add(rbspStart);
			rbgDirection.add(rbspDiff);
			rbgDirection.add(rbspCursor);
			direction.add(rbspStart);
			direction.add(rbspDiff);
			direction.add(rbspCursor);
			
			JPanel scope = new JPanel();
			scope.setBorder(BorderFactory.createTitledBorder("Scope"));
			scope.setLayout(new BoxLayout(scope, BoxLayout.Y_AXIS));
			rbgScope = new ButtonGroup();
			rbscpAll = new JRadioButton("All");
			rbscpOld = new JRadioButton("Old File");
			rbscpNew = new JRadioButton("New File");
			rbscpAll.setMnemonic(KeyEvent.VK_A);
			rbscpOld.setMnemonic(KeyEvent.VK_O);
			rbscpNew.setMnemonic(KeyEvent.VK_N);
			rbgScope.add(rbscpAll);
			rbgScope.add(rbscpOld);
			rbgScope.add(rbscpNew);
			scope.add(rbscpAll);
			scope.add(rbscpOld);
			scope.add(rbscpNew);
			
			options.add(direction, 0);
			options.add(scope, 1);
			add(options);
			
			JPanel option = new JPanel();
			option.setBorder(BorderFactory.createTitledBorder("Options"));
			option.setLayout(new GridLayout(3, 2));
			
			cbCase = new JCheckBox("Case sensitive");
			cbWhole = new JCheckBox("Whole word");
			cbWrap = new JCheckBox("Wrap search");
			cbExtended = new JCheckBox("Extended");
			cbReg = new JCheckBox("Regular expressions");
			cbDiffOnly = new JCheckBox("Diff Only");
			cbCase.setMnemonic(KeyEvent.VK_C);
			cbWhole.setMnemonic(KeyEvent.VK_H);
			cbWrap.setMnemonic(KeyEvent.VK_R);
			cbExtended.setMnemonic(KeyEvent.VK_X);
			cbReg.setMnemonic(KeyEvent.VK_E);
			cbDiffOnly.setMnemonic(KeyEvent.VK_F);
			
			option.add(cbCase, 0);
			option.add(cbWhole, 1);
			option.add(cbWrap, 2);
			option.add(cbExtended, 3);
			option.add(cbDiffOnly, 4);
			option.add(cbReg, 5);
			
			add(option);
			
			JPanel controls = new JPanel(new GridLayout(1,2,10,0));
			controls.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			btnFind = new JButton("Find");
			btnClose = new JButton("Close");
			controls.add(btnFind, 0);
			controls.add(btnClose, 1);
			
			add(controls);
			
			//set defaults
			rbspStart.setSelected(true);
			rbscpAll.setSelected(true);
			
			//initialise tooltips
			rbspStart.setToolTipText("Search from start of file");
			rbspDiff.setToolTipText("Search from selected Diff");
			rbspCursor.setToolTipText("Search from text caret position");
			
			//initialise listeners
			cbReg.addActionListener(this);	//regular exp disables other checkboxes
			txtFind.addActionListener(this);
			btnClose.addActionListener(this);
			btnFind.addActionListener(this);
			
			//finalise initialisation
			pack();
			setResizable(false);
			setLocationRelativeTo(null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == cbReg) {
				cbWhole.setEnabled(!cbReg.isSelected());
				cbExtended.setEnabled(!cbReg.isSelected());
				resetSearch();
			} else if (src == txtFind || src == btnFind) {
				if (cachedResults != null) {
					//select next..essentially a search from cursor position.
					
				} else if (txtFind.getText().length() > 0) {
					doSearch();
					
//					System.out.println(oldText.getCaret().getDot());
//					System.out.println(newText.getCaret().getDot());
					// how to deal with all...
					
					//need some definition of 'current location'
					//... how does case sensetive work with regular exp
					//whole word... start is diff from first char.
					//end is diff from last char...
					//extended makes all java escape characters to work as intended
					//case senseitive...
					
					//after searching...
					if (cachedResults.size() > 0) {
						btnFind.setText("Find Next...");
					}
				}
			} else if (src == btnClose) {
				setVisible(false);
			} else {
				resetSearch();
			}
		}
		
		void resetSearch() {
			if (cachedResults != null) {
				cachedResults = null;
				btnFind.setText("Find");
			}
		}
		
		//TODO
		void doSearch() {
			
		}
		
		class SearchRecord {
			int start, end;
			int isNew;	//belong to the new or old
		}
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	// this is the top border panel which casts a shadow like effect on the component
	private class BorderPanel extends JPanel {
		BorderPanel () {
			setMaximumSize(new Dimension(Short.MAX_VALUE,10));
			setMinimumSize(new Dimension(0,5));
			setPreferredSize(new Dimension(0,5));
		}
		
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = GraphicsUtil.setAntiAliasing(g);
			g2.setPaint(new GradientPaint(0,0,Color.GRAY, 0,5, Constants.TXT_BACKGROUND));
			g2.fillRect(0, 0, getWidth(), 5);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (searchDialog != null && e.getKeyCode() == KeyEvent.VK_F && e.isControlDown() &&
				(SwingUtil.DesecendsFromSameFrame((Component) e.getSource(), this)
				|| SwingUtilities.getRoot((Component)e.getSource()) == searchDialog)) {
			searchDialog.setVisible(true);
			searchDialog.txtFind.requestFocus();
			return true;	//event has been processed
		}
		return false;
	}
}