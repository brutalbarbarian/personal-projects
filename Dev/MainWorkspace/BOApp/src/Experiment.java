import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;

import com.lwan.util.IOUtil;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ToolBarBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


public class Experiment extends Application{
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(final Stage s) throws Exception {
		final TextArea txtIn = new TextArea();
		final TextArea txtOut = new TextArea();
		txtOut.setEditable(false);
		
		HBox txt = new HBox(10);
		txt.getChildren().addAll(txtIn, txtOut);
		HBox.setHgrow(txtIn, Priority.SOMETIMES);
		HBox.setHgrow(txtOut, Priority.SOMETIMES);
		
		Button btnProperties = new Button("Generate Properties");
		Button btnSQLtoJava = new Button("SQL to Java");
		Button btnJavatoSQL = new Button("Java to SQL");
		
		
		ToolBar tb = ToolBarBuilder.create().items(btnProperties, btnSQLtoJava, btnJavatoSQL).build();
		MenuBar mb = new MenuBar();
		
		Menu mSQL = new Menu("_SQL");
		MenuItem miStoredProcs = new MenuItem("_Compile Scripts");
		mSQL.getItems().addAll(miStoredProcs);
		
		Menu mProject = new Menu("_Project");
		MenuItem miSyncResources = new MenuItem("_Copy Resources");
		mProject.getItems().addAll(miSyncResources);
		
		mb.getMenus().addAll(mSQL, mProject);
		
		
		BorderPane pane = new BorderPane();
		
		pane.setTop(mb);
		pane.setCenter(txt);
		pane.setBottom(tb);
		
		s.setScene(new Scene(pane));
		
		btnProperties.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				String in = txtIn.getText();
				String outString = "";
//				String createString = "";
				// look for keyword 'BODbAttribute'
				int nxtIndex = 0;
				while(nxtIndex >= 0) {
					nxtIndex = in.indexOf("BODbAttribute", nxtIndex);
					if (nxtIndex >= 0) {
						// find the end of '>'
						int declEnd = in.indexOf('>', nxtIndex);
						String declaration = in.substring(nxtIndex, declEnd + 1);
						int end = in.indexOf(';', declEnd);
						String attrs = in.substring(declEnd + 1, end + 1);
						String curString = ""; 
						boolean started = false;
						boolean inComment = false;
						boolean prevIsSlash = false;
						for (char c : attrs.toCharArray()) {
							if (!inComment && !started && Character.isLetter(c)) {
								// start
								started = true;
								curString += c;
							} else if (inComment) {
								// check  for newline character
								if (c == '\n') {
									inComment = false;
								}
							} else if (started) {
								// check for 
								if (Character.isLetter(c) || Character.isDigit(c) || c == '_') {
									curString += c;
								} else {
									// end.
									started = false;
									outString += "public " + declaration + " " + curString + "() {\n" +
											"	return " + curString + ";\n" +
											"}\n";
//									createString += "addAsChild(new " + declaration + "(this, \"" + ");";
									curString = "";
								}
							}
							if (!inComment && prevIsSlash && c == '/') {
								inComment = true;
							}
							prevIsSlash = c == '/';
						}
						
						
						nxtIndex = end;
					}
				}
				txtOut.setText(outString);
			}			
		});
		
		btnSQLtoJava.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				String[] lines = txtIn.getText().split("\n");
				String out = "";
				boolean isFirst = true;
				for (String line : lines) {
					if (!isFirst) {
						out += " + \n";
					}
					out += '\"' + line + '\"';
					isFirst = false;
				}
				txtOut.setText(out);
			}
			
		});
		
		btnJavatoSQL.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				String [] lines = txtIn.getText().split("\n");
				String out = "";
//				boolean isFirst = true;
				
				for (String line : lines) {
					int first = line.indexOf('"');
					int last = line.indexOf('"', first + 1);
//					System.out.println(first + ":" + last);
					
					if (first >= 0 && last > first) {
						out += line.substring(first + 1, last) + "\n";
					}
				}
				
				txtOut.setText(out);
			}
		});
		
		miStoredProcs.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				DirectoryChooser dc = new DirectoryChooser();
				dc.setInitialDirectory(getInitDir());
				File f = dc.showDialog(s);
				if (f == null) return;
				
				final StringBuilder out = new StringBuilder();
				
				try {
					Files.walkFileTree(f.toPath(), new FileVisitor<Path>() {

						@Override
						public FileVisitResult preVisitDirectory(Path dir,
								BasicFileAttributes attrs) throws IOException {
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							if (file.toString().endsWith(".sql")) {
								out.append("#" + file.toString() + "\n");
								for (String line : Files.readAllLines(file, Charset.forName(IOUtil.CHARSET_DEFAULT_WINDOWS))) {
									out.append(line).append('\n');
								}
								out.append("\n\n");
							}
							
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFileFailed(Path file,
								IOException exc) throws IOException {
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir,
								IOException exc) throws IOException {
							return FileVisitResult.CONTINUE;
						}
						
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				txtOut.setText(out.toString());
			}			
		});
		
		miSyncResources.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				String outlog = "";
				try {
					DirectoryChooser dc = new DirectoryChooser();
					dc.setInitialDirectory(getInitDir());
					File f = dc.showDialog(s);
					if (f == null) return;
					
					// check the .project file exists in this folder
					Path projFile = Paths.get(f.getAbsolutePath() + "/.project");
					if (!Files.exists(projFile)) {
						outlog += "Cannot find .project file\n";
						return;
					}
					Path binDir = Paths.get(f.getAbsolutePath() + "/bin");
					if (!Files.exists(binDir)) {
						try {
							// possibly a new project?
							Files.createDirectory(binDir);
						} catch (IOException e) {
							outlog += e.getMessage() + "\n";
						}
					}
					
					String[] items = {"/resources", "/styles"};
					
					for (String p : items) {
						Path dir = Paths.get(f.getAbsolutePath() + p);
						if (!Files.exists(dir)) {
							outlog += "Cannot find folder: " + p + "\n";
							continue;	// not found
						}
						outlog += "Copying folder " + p + "\n";
						
						Path copiedDir = Paths.get(binDir.toFile().getAbsolutePath() + p);
						
						try {
							FileUtils.copyDirectory(dir.toFile(), copiedDir.toFile(), new FileFilter() {
								public boolean accept(File pathname) {
									return !pathname.toString().endsWith("Thumbs.db");
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					outlog += "Successfully completed.\n";
				} finally {
					txtOut.setText(outlog);
				}
			}			
		});
		s.show();
	}
	
	File getInitDir() {
		return new File(System.getProperty("user.dir")).getParentFile();
	}
}
