package test;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.lwan.swing.FlexableLayout;

public class TextLayoutTest {
	public TextLayoutTest() {
		JFrame frame = new JFrame();
		JPanel pane = new JPanel();
		pane.setLayout(new FlexableLayout());
		
		pane.add(new JLabel("This is a label"));
		pane.add(new JButton("This is a JButton"), FlexableLayout.getWidthString(0.4f));
		pane.add(new JTextField(), FlexableLayout.FLEXABLE);
		
		frame.setContentPane(pane);
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new TextLayoutTest();
	}
}
