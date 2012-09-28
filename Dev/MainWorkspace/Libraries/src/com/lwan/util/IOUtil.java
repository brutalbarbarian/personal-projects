package com.lwan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;

public class IOUtil {
	public static final String CHARSET_DEFAULT_WINDOWS = "cp1252";
	public static final String CHARSET_DEFAULT_UBUNTU = "utf-8";
	
	public static String getRelativePath(String root, String file) {
		String sep = Character.toString(File.separatorChar);
		if (sep.equals("\\")) {
			sep = "\\\\";
		}
		String[] rt = root.split(sep);
		String[] f = file.split(sep);
		
		int lastSharedPos = -1;
		for (int i = 0; i < Math.min(rt.length, f.length); i++) {
			if (rt[i].equals(f[i])) {
				lastSharedPos = i;
			} else {
				break;
			}
		}
		
		StringBuffer bf = new StringBuffer();
		// add in .../ for the diff between rt.length and lastSharedPos
		for (int i = lastSharedPos + 1; i < rt.length; i++) {
			bf.append("..").append(File.pathSeparatorChar);
		}
		
		// append the rest of file starting from lastSharedPos
		for (int i = lastSharedPos + 1; i < f.length; i++) {
			bf.append(f[i]).append(File.pathSeparatorChar);
		}
		
		return bf.toString();
	}
	
	/**
	 * Attempt to read all lines, trying out each charset that is passed in.
	 * Note that no exceptions will be thrown from this method. If none of the charsets
	 * that are passed in work, or if there are any IO issues, the returned 
	 * List will be null.
	 * 
	 * @param p
	 * @param cs
	 * @return
	 */
	public static List<String> readAllLines (Path p, String... cs) {
		List<String> file = null;
		for (String csn : cs) {
			try {
				Charset c = Charset.forName(csn);
				file = Files.readAllLines(p, c);
			} catch (Exception e) {
			} finally {
				if (file != null) break;
			}
		}
		return file;
	}
	
	/**
	 * Read all lines using a buffered reader.</br>
	 * Note that this can cause problems due to incompatible charsets,
	 * but should always work.
	 * 
	 * @param p
	 * @return
	 * @throws IOException
	 */
	public static List<String> readAllLines (Path p) throws IOException {
		File f = p.toFile();
		if (!f.exists()) {
			throw new FileNotFoundException();
		}
		BufferedReader reader = new BufferedReader (new FileReader(f));
		Vector <String> v = new Vector<> ();
		while(reader.ready()) {
			v.add(reader.readLine());
		}
		reader.close();
		return v;
	}
}
