package com.lwan.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.lwan.strcom.DiffInfo;
import com.lwan.strcom.RunnerForLines;
import com.lwan.strcom.gui.GSettings;
import com.lwan.swing.JComparisonPanel;
import com.lwan.swing.MenuLayout;
import com.lwan.util.IOUtil;
import com.lwan.util.cache.FileDialogCache;

public class StrCom {
	//components
	private JFrame fWindow;
	private JTextField txtOldFile;
	private JTextField txtNewFile;
	private JButton btnOld, btnNew, btnNavBack, btnNavFoward;
	private JComparisonPanel compPanel;
	
	//listeners
	private CompareListener compListener;
	private GetFileListener fileListener;
	private NavigationListener navListener;
	private VisibleListener visibleListener;
	private SelectionListener selectionListener;
	private IgnoreListener ignoreListener;
	
	public StrCom() {
		fWindow = new JFrame("com.lwan.strcom");
		fWindow.setIconImage(null);
		fWindow.setContentPane(initializeComponents());
		fWindow.setSize(1000, 700);
		fWindow.setMinimumSize(new Dimension(600, 400));
		fWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fWindow.setLocationRelativeTo(null);
		fWindow.setVisible(true);
	}
	
	public StrCom(String f1, String f2) {
		this();
		txtOldFile.setText(f1);
		txtNewFile.setText(f2);
		compListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "Start"));
	}
	
	private JPanel initializeComponents() {
		//setup listeners
		compListener = new CompareListener();
		fileListener = new GetFileListener();
		navListener = new NavigationListener();
		visibleListener = new VisibleListener();
		selectionListener = new SelectionListener();
		ignoreListener = new IgnoreListener();
		
		//setup content pane
		JPanel content = new JPanel();
		content.setDoubleBuffered(true);
		MenuLayout layout = new MenuLayout(MenuLayout.STYLE_FIRST, BoxLayout.Y_AXIS, content);
		content.setLayout(layout);		
		
		//setup layout
		JPanel topPanel = new JPanel (new FlowLayout(FlowLayout.LEFT));
		compPanel = new JComparisonPanel(fWindow);
		compPanel.addPropertyChangeListener(JComparisonPanel.SELECTION_PROPERTY, selectionListener);
		content.add(topPanel);
//		content.add(new BorderPanel());
		content.add(compPanel);

		
		//setup top panel
		//file loaders
		txtOldFile = new JTextField(12);
		txtNewFile = new JTextField(12);
		txtOldFile.addActionListener(compListener);
		txtNewFile.addActionListener(compListener);
		btnOld = new JButton("...");
		btnOld.addActionListener(fileListener);
		btnNew = new JButton("...");
		btnNew.addActionListener(fileListener);
		topPanel.add(new JLabel("Old File"));
		topPanel.add(txtOldFile);
		topPanel.add(btnOld);
		topPanel.add(new JLabel("New File"));
		topPanel.add(txtNewFile);
		topPanel.add(btnNew);
		//compare
		JButton btnComp = new JButton("Compare");
		btnComp.addActionListener(compListener);
		topPanel.add(btnComp);
		//navigation
		btnNavBack = new JButton("<");
		btnNavBack.setEnabled(false);
		btnNavBack.addActionListener(navListener);
		btnNavFoward = new JButton(">");
		btnNavFoward.setEnabled(false);
		btnNavFoward.addActionListener(navListener);
		topPanel.add(btnNavBack);
		topPanel.add(btnNavFoward);
		//line visible
		JCheckBox chkVisible = new JCheckBox("Show Line Numbers", true);
		chkVisible.addActionListener(visibleListener);
		topPanel.add(chkVisible);
		
		JCheckBox chkIgnore = new JCheckBox("Ignore White Space", GSettings.ignoreWhiteSpace().getValue());
		chkIgnore.addActionListener(ignoreListener);
		topPanel.add(chkIgnore);
		//search
//		JTextField search = 
		
		setDoubleBuffered(content);
		
		return content;
	}
	
	protected void setDoubleBuffered (JComponent c) {
		c.setDoubleBuffered(true);
		for (Component comp : c.getComponents()) {
			if (comp instanceof JComponent) {
				setDoubleBuffered((JComponent)comp);
			}
		}
	}
	
	private class NavigationListener implements ActionListener {
		public void actionPerformed (ActionEvent e) {
			if (e.getSource() == btnNavBack) {
				compPanel.setPrevSelected();
			} else {
				compPanel.setNextSelected();
			}
//			btnNavFoward.setEnabled(compPanel.hasNextHighlight());
//			btnNavBack.setEnabled(compPanel.hasPrevHighlight());
		}
	}
	
	private class GetFileListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = FileDialogCache.showChooser(fWindow.getContentPane(), "comp", System.getProperty("user.dir"));
			
			if (chooser != null) {
				File file = chooser.getSelectedFile();
				if (!file.exists()) {
					JOptionPane.showMessageDialog(fWindow, file.getAbsolutePath()+" cannot be found", 
							"Cannot Find File", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (e.getSource() == btnOld) {
					txtOldFile.setText(chooser.getSelectedFile().getAbsolutePath());
				} else if (e.getSource() == btnNew) {
					txtNewFile.setText(chooser.getSelectedFile().getAbsolutePath());					
				}
			}
		}
	}
	

	private class IgnoreListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GSettings.ignoreWhiteSpace().setValue(((JCheckBox)e.getSource()).isSelected());
		}
	}
	
	private class CompareListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			Path f1 = Paths.get(txtOldFile.getText());
			Path f2 = Paths.get(txtNewFile.getText());
			
			List<String> s1 = IOUtil.readAllLines(f1, IOUtil.CHARSET_DEFAULT_WINDOWS, IOUtil.CHARSET_DEFAULT_UBUNTU);
			List<String> s2 = IOUtil.readAllLines(f2, IOUtil.CHARSET_DEFAULT_WINDOWS, IOUtil.CHARSET_DEFAULT_UBUNTU);

			List<DiffInfo> res = RunnerForLines.run(s1, s2);

			//Populate results lists
			compPanel.setData(s1, s2, res);
			btnNavFoward.setEnabled(compPanel.hasNextHighlight());
			btnNavBack.setEnabled(compPanel.hasPrevHighlight());
		}
	}
	
	private class SelectionListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			btnNavFoward.setEnabled(compPanel.hasNextHighlight());
			btnNavBack.setEnabled(compPanel.hasPrevHighlight());
		}
	}
	
	private class VisibleListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			compPanel.setShowNumberLines(((JCheckBox)e.getSource()).isSelected());
		}
	}
}
