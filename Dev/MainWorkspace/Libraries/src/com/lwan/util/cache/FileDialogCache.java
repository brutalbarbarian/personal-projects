package com.lwan.util.cache;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.swing.JFileChooser;
import com.lwan.util.IOUtil;

/**
 * Class for file dialog operations.
 * The important thing about this class is that all calls to
 * open a file dialog window requires a key. This key stores
 * the last used path, so future references to the same key
 * will automatically set the current path of the file dialog
 * to the stored last used path.</br>
 * </br>
 * Furthermore this class can read/write from and to an .ini file,
 * allowing all cached key/path pairs to be stored and reloaded.
 * 
 * @author Brutalbarbarian
 *
 */
public class FileDialogCache {
	protected static HashMap<String, String> filePathTable;
	
	/**
	 * Should call this in order to access filePathTable. Reason is
	 * due to filePathTable may be null. This method initialises
	 * a global HashMap into filePathTable if its null, and thus is
	 * safer.
	 * 
	 * @return
	 */
	protected static HashMap<String, String> getFilePathTable() {
		if (filePathTable == null) {
			filePathTable = new HashMap<>();
		}
		return filePathTable;
	}
	
	/**
	 * Initialises the cache with key/path pairs from an .ini file. This
	 * method dosen't need to be called in order to use other methods in
	 * this library.</br>
	 * </br>
	 * The format for the input file should be as follows:</br>
	 * <br>
	 * $Key $Path<br>
	 * </br> 
	 * This is the same format followed by unititialise.
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public static void initialise (String filePath) throws IOException {
		Path p = Paths.get(filePath);
		for (String line : IOUtil.readAllLines(p)) {
			int split = line.indexOf(' ');
			String key = line.substring(0, split);
			String val = line.substring(split + 1);
			getFilePathTable().put(key, val);
		}
	}
	
	public static void uninitialise (String filePath) throws IOException {
		//stores the filepathtable into the selected file dictated by filePath
	}
	
	/**
	 * Will show a file chooser dialog. If the dialog is returned with
	 * APPROVE_OPTION, then the file selected will be returned, and the
	 * path will be saved into the table for future references. 
	 * 
	 * TODO change so all options can be set via parameters
	 * 
	 * @param key
	 * @param defPath
	 * @return
	 */
	public static JFileChooser showChooser (Component parent, String key, String defPath) {
//		LookAndFeel laf =UIManager.getLookAndFeel();
//		try {
//			SwingUtil.setLookAndFeel("Nimbus");
			
		String path = getFilePathTable().get(key);
		if (path == null) path = defPath;

		JFileChooser chooser = new JFileChooser();
		//set options here
		chooser.setCurrentDirectory(new File(path));

		if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			getFilePathTable().put(key, chooser.getCurrentDirectory().getAbsolutePath());
			return chooser;
		} else {
			return null;
		}
//		} catch (Exception e) {		
//		} finally {
//			try {
//				UIManager.setLookAndFeel(laf);
//			} catch (UnsupportedLookAndFeelException e) {}
//		}
//		return null;
	}
}
