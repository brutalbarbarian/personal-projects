package test;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import com.lwan.swing.ScrollBarLayout;

public class ScrollbarLayoutTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JScrollBar hBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 0);
		JScrollBar vBar = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
		JPanel centPane = new JPanel ();
		
		JPanel content = new JPanel();
		content.setLayout(new ScrollBarLayout());
		
		content.add(hBar, ScrollBarLayout.HORIZONTAL_BAR);
		content.add(vBar, ScrollBarLayout.VERTICAL_BAR);
		content.add(centPane, ScrollBarLayout.CENTRAL_PANE);
		
		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.X_AXIS));
		outerPanel.add(new JButton("hello World"));
		outerPanel.add(content);
		
		frame.setContentPane(outerPanel);
		//frame.setContentPane(content);
		
		
		frame.pack();
		frame.setSize(500, 500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
