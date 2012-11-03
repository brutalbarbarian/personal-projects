package test;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import com.lwan.swing.LTextAreaLineNumbers;
import com.lwan.swing.MenuLayout;

@SuppressWarnings("serial")
public class LineNumberingTest extends JPanel {
	public LineNumberingTest () {
//		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setLayout(new MenuLayout(MenuLayout.STYLE_FIRST, MenuLayout.X_AXIS, this));
		
		JTextArea txt = new JTextArea();
		JViewport view = new JViewport();
		view.add(txt);
		JViewport num = new LTextAreaLineNumbers(txt, view, true);//SwingUtil.createNumberedLines(txt, view);
		num.setMinimumSize(new Dimension(50, 0));
		num.setPreferredSize(new Dimension(50, 50));
		num.setMaximumSize(new Dimension(50, 2000));
		
		add(num);
		add(view);

	}
	
	public static void main (String[] args) {
		JFrame frame = new JFrame ();
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new LineNumberingTest());
		frame.setVisible(true);
	}
}
