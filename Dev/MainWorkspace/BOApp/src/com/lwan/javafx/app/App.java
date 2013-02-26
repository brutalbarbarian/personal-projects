package com.lwan.javafx.app;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.lwan.jdbc.GConnection;
import com.lwan.util.IOUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public abstract class App extends Application{
	// Application messages
	public static final int TERMINATE_REQUEST = 0;
	
	// File names
	public static final String KEY_FILENAME = "keys.ini";
	
	// Default key values
	public static final String KEY_LANGUAGE = "LANGUAGE";
	public static final String KEY_DB_PATH = "DBPATH";
	
	// Static values
	public static final String DB_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
	public static final String DB_CON_PREFIX = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=";
	
	private Map<String, String> keyMap;
	private static App app;
	private Stage mainStage;
	
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
		mainStage = s;
		
		initialiseStage(s);
	}
	
	public static Stage getMainStage() {
		return getApp().mainStage;
	}
	
	protected void initialiseStage(Stage stage) {
		// Override for anything to happen...
	}
	
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
				getMainStage().close();
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
	 * 
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
		System.out.println("Using language: " + loc.getDisplayName());
		
		Lng.initialise(loc);
		
		// Initialise database
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
				GConnection.initialise(DB_DRIVER, buildConnectionString(dbpath), "", "");	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (!GConnection.isInitialised()) {
				System.out.println("Failed attempt with path: " + dbpath);
				dbpath = null;
			}
		}
		keyMap.put(KEY_DB_PATH, dbpath);
		
		// Assign the global variable now that initialise has been completed
		app = this;
	}
	
	private String buildConnectionString(String path) {
		return DB_CON_PREFIX + path;
	}

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
		// TODO
	}
}
