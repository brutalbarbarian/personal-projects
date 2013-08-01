package com.lwan.javafx.app;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.lwan.javafx.controls.panes.THBox;
import com.lwan.javafx.controls.panes.TVBox;
import com.lwan.javafx.scene.control.AlignedControlCell;
import com.lwan.jdbc.GConnection;
import com.lwan.util.FxUtils;
import com.lwan.util.GenericsUtil;
import com.lwan.util.IOUtil;
import com.lwan.util.StringUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class App extends Application{
	// Application messages
	public static final int TERMINATE_REQUEST = 0;
	public static final int RESTART_REQUEST = 1;
	
	// Use to space out the messages so there's no collision of messages
	protected static final int APP_MESSAGE_LAST = RESTART_REQUEST;
	
	// File names
	public static final String KEY_FILENAME = "keys.ini";
	
	// Default key values
	public static final String KEY_LANGUAGE = "LANGUAGE";
	public static final String KEY_DB_PATH = "DB_ACCESS_PATH";
	public static final String KEY_DB_MYSQL_OPENSILENT = "DB_MYSQL_OPENSILENT";	// this will cause the password to be saved...
	public static final String KEY_DB_MYSQL_USER = "DB_MYSQL_USERNAME";
	public static final String KEY_DB_MYSQL_PASSWORD = "DB_MYSQL_PASSWORD";
	public static final String KEY_DB_MYSQL_SERVER = "DB_MYSQL_SERVER";
	public static final String KEY_DB_MYSQL_DATABASE = "DB_MYSQL_DATABASE";
	
	// Static values
	public static final String DB_ACCESS_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
	public static final String DB_ACCESS_CON_PREFIX = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=";	
	public static final String DB_MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_MYSQL_CON_PREFIX = "jdbc:mysql://";
	
//	jdbc:mysql://localhost:3306/
	
	// DB_CON_PREFIX + SERVERNAME + DB NAME
	
	private Map<String, String> keyMap;
	private static App app;
	private Stage mainStage;
	private Locale locale;
	private Collection<String> stylesheets;
	
	public static App getApp() {
		if (app == null) {
			throw new RuntimeException("App has not been initialised yet");
		}
		return app;
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		// save the key values
		Path p = Paths.get(KEY_FILENAME);
		IOUtil.storeMap(keyMap, "=", "", p, Charset.defaultCharset(), 
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		
		// save language file
		Lng.store();
		
		// uninitialise database
		GConnection.uninitialise();
	}
		
	@Override
	public void start(Stage s) throws Exception {
		// Initialize database
		initDatabase();
		
		mainStage = s;
		
		initialiseStage(s);
	}
	
	public static Locale getLocale() {
		return getApp().locale;
	}
	
	public static Stage getMainStage() {
		return getApp().mainStage;
	}
	
	protected abstract void initialiseStage(Stage stage);
	
	public static void notifyState(final int state) {
		System.out.println("notified:" + state);
		
		// Run in a seperate thread.
		Platform.runLater(new NotifyTask(state));
	}

	// Used for notifyState only.
	private static class NotifyTask extends Task<Void> {
		int state;
		
		NotifyTask(int state) {
			this.state = state;
		}
		
		protected Void call() throws Exception {			
			getApp().processState(state);
			
			return null;
		}
	}
	
	protected void processState(int state) throws Exception {
		switch (state) {
		case TERMINATE_REQUEST :
			if (allowTerminate()) {
//				getMainStage().close();
				Platform.exit();
			}
			break;
		case RESTART_REQUEST:
			if (allowTerminate()) {
				Platform.exit();
				Platform.runLater(new Runnable(){
					public void run() {
						launch();					
					}
				});			
			}
			break;
		}
	}
	
	protected boolean allowTerminate() {
		return true;
	}

	/**
	 * Should be run on application startup
	 * @throws Exception 
	 */
	public void init() throws Exception {
		super.init();
		// Load keyvalues
		Path p = Paths.get(KEY_FILENAME);
		keyMap = new HashMap<>();
		if (Files.exists(p)) {
			List<String> lines = Files.readAllLines(p, Charset.defaultCharset());
			for (String line : lines) {
				int index = line.indexOf('=');
				String key = line.substring(0, index);
				String value = line.substring(index + 1);
				
				keyMap.put(key, value);
			}
		} else {
			System.out.println("Keyfile dosen't exist.");
		}
		
		// Initialise language
		String localeTag = keyMap.get(KEY_LANGUAGE);
		Locale loc = null;
		if (localeTag != null) {
			loc = Locale.forLanguageTag(localeTag);
		}
		if (loc == null) {
			loc = Locale.getDefault();
			keyMap.put(KEY_LANGUAGE, loc.toLanguageTag());
			System.out.println("Unknown language. Using system default");
		}
		locale = loc;
		System.out.println("Using language: " + loc.getDisplayName());
		
		Lng.initialise(loc);
		
		stylesheets = new ArrayList<String>();
		initStylesheets(stylesheets);
		
		// Assign the global variable now that initialise has been completed
		app = this;
	}
	
	protected void initDatabase() {
		initMySQLDatbase();
	}
	
	protected void initAccessDatabase() {
		String dbpath = keyMap.get(KEY_DB_PATH);
		if (dbpath == null) {
			System.out.println("No DB path provided");
		}
		while (!GConnection.isInitialised()) {
			if (dbpath == null) {
				// Ask user to select file...
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Select database...");
				File f = chooser.showOpenDialog(null);
				if (f != null) {
					dbpath = f.getAbsolutePath();
				} else {
					System.out.println("No file selected");
					System.exit(0);	// Just exit...
				}
			}
			try {
				GConnection.initialise(DB_ACCESS_DRIVER, DB_ACCESS_CON_PREFIX + dbpath, "", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (!GConnection.isInitialised()) {
				System.out.println("Failed attempt with path: " + dbpath);
				dbpath = null;
			}
		}
		
		keyMap.put(KEY_DB_PATH, dbpath);
	}
	
	protected void initMySQLDatbase() {
		String server = GenericsUtil.Coalice(keyMap.get(KEY_DB_MYSQL_SERVER), "");
		String database = GenericsUtil.Coalice(keyMap.get(KEY_DB_MYSQL_DATABASE), "");
		String user = GenericsUtil.Coalice(keyMap.get(KEY_DB_MYSQL_USER), "");
		String password = GenericsUtil.Coalice(keyMap.get(KEY_DB_MYSQL_PASSWORD), "");
		String openSilent = keyMap.get(KEY_DB_MYSQL_OPENSILENT);
		boolean silent = openSilent == null? false : Boolean.parseBoolean(openSilent);
		boolean savePassword = password.length() > 0;
		
		while (!GConnection.isInitialised()) {
			if (!silent) {
				// bring up the dialog
				final SimpleBooleanProperty result = new SimpleBooleanProperty();
				result.set(false);
				
				final Stage stage = new Stage(StageStyle.UTILITY);
//				stage.initModality(Modality.APPLICATION_MODAL);
				stage.initOwner(null);
				stage.setTitle(Lng._("Setup Database Connection..."));
				
				TVBox pane = new TVBox(5);
				TextField txtServer = new TextField();
				TextField txtDatabase = new TextField();
				TextField txtUsername = new TextField();
				PasswordField txtPassword = new PasswordField();
				CheckBox chkSavePassword = new  CheckBox(Lng._("Save Password"));
				CheckBox chkOpenSilently = new CheckBox(Lng._("Open Silently"));
				
				chkOpenSilently.disableProperty().bind(Bindings.not(chkSavePassword.selectedProperty()));
				
				txtServer.setText(server);
				txtDatabase.setText(database);
				txtUsername.setText(user);
				txtPassword.setText(password);
				chkSavePassword.setSelected(savePassword);
				chkOpenSilently.setSelected(silent);
				
				Button btnOk = new Button(Lng._("Ok"));
				Button btnCancel = new Button(Lng._("Cancel"));
				
				THBox bottom = new THBox(5);
				bottom.getChildren().addAll(btnOk, btnCancel);
				
				Label title = new Label(Lng._("Server Detail..."));
				title.getStyleClass().add(StyleConstants.LABEL_HEADER);
				
				pane.getChildren().addAll(
						title,
						new AlignedControlCell(Lng._("Server"), txtServer, pane),
						new AlignedControlCell(Lng._("Database"), txtDatabase, pane),
						new AlignedControlCell(Lng._("Username"), txtUsername, pane),
						new AlignedControlCell(Lng._("Password"), txtPassword, pane),
						new AlignedControlCell("", chkSavePassword, pane),
						new AlignedControlCell("", chkOpenSilently, pane),
						bottom);
				
				btnOk.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent arg0) {
						result.set(true);
						stage.close();
					}				
				});
				btnCancel.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent arg0) {
						stage.close();
					}					
				});
				
				pane.setPadding(new Insets(10));
				Scene sc = new Scene(pane);
				sc.getStylesheets().addAll(getStyleshets());
				stage.setScene(sc);
				stage.setResizable(false);
				
				stage.showAndWait();
				
				if (result.get()) {
					server = txtServer.getText();
					database = txtDatabase.getText();
					user = txtUsername.getText();
					password = txtPassword.getText();
					savePassword = chkSavePassword.isSelected();
					silent = savePassword && chkOpenSilently.isSelected();
				} else {
					// User canceled
					System.exit(0);
				}
			}
			
			// attempt to open with the set parameters
			String conString = DB_MYSQL_CON_PREFIX + server + "/" + database;
			try {
				GConnection.initialise(DB_MYSQL_DRIVER, conString, user, password);
				
				System.out.print("Successfully initialised with connection string of : ");
				System.out.println(conString);
				System.out.println("with username of " + user + " and password of " + StringUtil.getRepeatedString('*', password.length()));
			} catch (Exception e) {
				FxUtils.ShowErrorDialog(null, e.getMessage());
				silent = false;	// failed...
			}
		}
		
		// open successfully if got to here
		keyMap.put(KEY_DB_MYSQL_SERVER, server);
		keyMap.put(KEY_DB_MYSQL_DATABASE, database);
		keyMap.put(KEY_DB_MYSQL_USER, user);
		keyMap.put(KEY_DB_MYSQL_OPENSILENT, Boolean.toString(silent));
		keyMap.put(KEY_DB_MYSQL_PASSWORD, savePassword ? password : "");
	}
	
	protected abstract void initStylesheets(Collection<String> stylesheets);

	public static void putKey(String key, String value) {
		getApp().keyMap.put(key, value);
	}
	
	public static String getKey(String key) {
		return getApp().keyMap.get(key);
	}
	
	public static void requestTerminate() {
		notifyState(TERMINATE_REQUEST);
	}

	public static void requestRestart() {
		notifyState(RESTART_REQUEST);
	}
	
	public static Collection<String> getStyleshets() {
		return getApp().stylesheets;		
	}
}
