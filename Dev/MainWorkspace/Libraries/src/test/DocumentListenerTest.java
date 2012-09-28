package test;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.lwan.swing.UneditableDocumentFilter;

@SuppressWarnings("serial")
public class DocumentListenerTest extends JPanel implements DocumentListener {
	public DocumentListenerTest () {
		JTextArea txt = new JTextArea();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		txt.getDocument().addDocumentListener(this);
		txt.setText("asdasdoiafeioefedfedwe wwefwe rewiowqwdqwdqd q\n wqeiwqewqoejwq wqeowqhnqasdfd\n qwedqohwqodiwqqw");
		
		UneditableDocumentFilter.SetFilterOnDocument(txt.getDocument()).setEdit(false);
		
		add(txt);
	}
	
	public static void main (String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new DocumentListenerTest());
		frame.setVisible(true);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		System.out.println();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		System.out.println();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		System.out.println();
	}
}
