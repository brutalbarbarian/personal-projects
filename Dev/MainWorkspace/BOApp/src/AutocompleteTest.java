import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.lwan.javafx.app.util.AutocompleteController;
import com.lwan.util.CollectionUtil;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class AutocompleteTest extends Application{

	public static void main(String[] args) {
		launch(args);
	}
	
	protected void setSrcFiles(AutocompleteController acc) {
		File f = new File("C:\\Users\\Lu\\Documents\\final");
		final Iterator<File> children = CollectionUtil.getIterator(f.listFiles());
		acc.setSource(new Iterable<String>() {
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					File currFile;
					BufferedReader br;

					public boolean hasNext() {
						try {
							if (br != null && !br.ready()) {
								br.close();
								br = null;
							}
							return (children.hasNext()) ||
									(br != null && br.ready());
						} catch (Exception e) {}
						return false;
					}

					@Override
					public String next() {
						try {
							if (br == null || !br.ready()) {								
								currFile = children.next();
								br = new BufferedReader(new FileReader(currFile));
							}
							
							return br.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						return null;
					}

					@Override
					public void remove() {}
				};
			}
		});	
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		TextField tf = new TextField();
		final AutocompleteController acc = new AutocompleteController(tf, true);
		
		tf.focusedProperty().addListener(new ChangeListener<Boolean> () {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				acc.setEditing(arg2);
			}			
		});
		
		acc.setAllowUnique(false);
//		setSrcFiles(acc);
		setSampleWords(acc);
		
		
		Button btn = new Button("Do Something");
		
		VBox box = new VBox();
		box.getChildren().addAll(tf, btn);
		
		Scene sc = new Scene(box);
		
		stage.setScene(sc);
		stage.show();
	}
	
	public void setSampleWords(AutocompleteController acc) {
		String[] words = {"App", "Apple", "Application", "Apricot", "Appreciation", "Appall", "Apolo"};
		acc.setSource(CollectionUtil.getIterable(words));
	}
}
