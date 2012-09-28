package test;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.lwan.swing.JTabPane;
import com.lwan.swing.ToolbarPane;

public class Test  {
	JFrame frame;
	
	void setupComponents (JFrame frame) {
		this.frame = frame;
		frame.setTitle("TabTest");
		frame.setSize(400, 400);
		
		Container pane = frame.getContentPane();

		JPanel content = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		JTabPane p = new JTabPane (content);
		pane.add(p);
		pane.add(new ToolbarPane());
		pane.add(content);
		
		JPanel c1 = new JPanel ();
		c1.setBackground(Color.RED);
		JPanel c2 = new JPanel();
		c2.setBackground(Color.BLUE);
		JPanel c3 = new JPanel();
		c3.setBackground(Color.YELLOW);
		JPanel c4 = new JPanel();
		c4.setBackground(Color.GREEN);
		p.addTab(c1, "RED", false, null, null, false);
		p.addTab(c2, "BLUE", true);
		p.addTab(c3, "YELLOW", true);
		p.addTab(c4, "GREEN", true);
		
		p.setFocusTraversalKeysEnabled(false);
		//p.setBackground(Color.DARK_GRAY);
		p.setTabFocusToFirst();
		p.setGlobalTabShortcuts(true);
		
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem("Hello"));
		menu.add(new JMenuItem("World"));
		
		p.setMenuTab(menu, "Test");
	}
	
	public static void main (String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new Test();
	}
	
	private Test () {
		JFrame frame = new JFrame ();
		frame.getContentPane().setLayout(new FlowLayout());
		setupComponents(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
