package com.lwan.eaproj.app;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.lwan.util.StringUtil;


/**
 * Global Locale class.
 * Loads up the correct locale file upon initialise
 * 
 * @author Brutalbarbarian
 *
 */
public class Lng {
	private static Map<String, String> map;
	private static Locale locale;
	public static final String SEP = "!_:=>";
	public static final String EXT = ".lng"; 
	
	public static void initialise(Locale l) throws IOException {
		if (map == null) {
			map = new HashMap<String, String>();
		}
		
		if (locale != null) {
			System.out.println("Attempting to set new locale... restart required.");
			App.putKey(App.KEY_LANGUAGE, l.toLanguageTag());
			App.requestRestart();
		}
		
		map.clear();
		locale = l;
		Path p = Paths.get(locale.toLanguageTag() + EXT);
		if (Files.exists(p)) {
			List<String> lines = Files.readAllLines(p, Charset.defaultCharset());
			StringTokenizer st;
			for (String line : lines) {
				st = new StringTokenizer(line, SEP);
				String key = st.nextToken();
				String value = null;
				if (st.hasMoreTokens()) {
					value = st.nextToken();
				}
				map.put(key, value);
			}
		} else {
			System.out.println("Language file not found.");
		}
	}
	
	public static void store() throws IOException {
		Path p = Paths.get(locale.toLanguageTag() + EXT);
		Files.write(p, new Iterable<String>() {
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					Iterator<Entry<String, String>> it = map.entrySet().iterator();
					
					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public String next() {
						Entry<String, String> entry = it.next();
						if (entry == null) {
							return null;
						} else {
							String value = entry.getValue();
							if (value == null) {
								// in case its null..
								value = "";
							}
							return entry.getKey() + SEP + value;
						}
					}

					@Override
					public void remove() {
						it.remove();
					}
					
				};
			}
		}, Charset.defaultCharset(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
	}
	
	/**
	 * Standard translate function
	 * 
	 * @param s
	 * @param variables
	 * @return
	 */
	public static String _(String s, String...variables) {
		String result = map.get(s);
		if (StringUtil.isNullOrBlank(result)) {
			map.put(s, null);
			result = s;	// just use the passed in string 
		}
		
		// replace all tokens if any
		for (int i = 1; i <= variables.length; i++) {
			result.replace("%" + i + "%", variables[i-1]);
		}
		
		return result;
	}
}
