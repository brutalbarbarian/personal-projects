package com.lwan.util;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import com.sun.javafx.binding.StringFormatter;
import javafx.util.Callback;

public class StringUtil {
	public static String getRepeatedString (String s, int num){
		StringBuilder sb = new StringBuilder(s.length() * num);
		for (int i = 0; i < num; i++) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	public static boolean validateString(String s, String expression) {
		return Pattern.matches(expression, s);
	}
	
	public static boolean validateString(String s, Callback<Character, Boolean> validator) {
		for (char c : s.toCharArray()) {
			if (!validator.call(c)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean validateDouble(String value, int percision) {
		return validateString(value, "^-?\\d*\\.?\\d{0," + percision + "}$");
	}
	
	public static String formatString(String format, Object...args) {
		return StringFormatter.format(format, args).getValue();
	}
	
	public static String doubleToString(double d, int percision) {
		return formatString("%."+percision+"f", d);
	}

	/**
	 * Validate if a string can be parsed into an integer without having to rely on
	 * a try, catch model around Integer.parseInt. If this returns true, then integer.parseInt() is guaranteed to
	 * parse correctly without exception.
	 * 
	 * See Integer.parseInt();
	 * 
	 * @param s
	 * @param radix
	 * @return
	 */
	public static boolean validateInt(String s, int radix) {
		if (s == null) {
			return false;
		}

		if (radix < Character.MIN_RADIX) {
			return false;
		}

		if (radix > Character.MAX_RADIX) {
			return false;
		}

		int result = 0;
		int i = 0, len = s.length();
		int limit = -Integer.MAX_VALUE;
		int multmin;
		int digit;

		if (len > 0) {
			char firstChar = s.charAt(0);
			if (firstChar < '0') { // Possible leading "+" or "-"
				if (firstChar == '-') {
					limit = Integer.MIN_VALUE;
				} else if (firstChar != '+')
					return false;

				if (len == 1) // Cannot have lone "+" or "-"
					return false;
				i++;
			}
			multmin = limit / radix;
			while (i < len) {
				// Accumulating negatively avoids surprises near MAX_VALUE
				digit = Character.digit(s.charAt(i++),radix);
				if (digit < 0) {
					return false;
				}
				if (result < multmin) {
					return false;
				}
				result *= radix;
				if (result < limit + digit) {
					return false;
				}
				result -= digit;
			}
		} else {
			return false;
		}
		// Valid integer if it makes it up to this point...
		return true;
	}
	
	/**
	 * Replace characters between start and end of the original string with the replacement string.
	 * e.g.
	 * 
	 * @param original
	 * @param start
	 * @param end
	 * @param replacement
	 * @return
	 */
	public static String replaceString (String original, int start, int end, String replacement) {
		StringBuilder sb = new StringBuilder(original.length() + (end - start) + replacement.length());
		sb.append(original.substring(0, start)).append(replacement).append(original.substring(end));
		return sb.toString();
	}
	
	public static boolean isNotBlank (String s) {
		return s.trim().length() != 0;
	}
	
	/**
	 * Checks if an object is null. If it is not null, checks if its a 
	 * string in which case will check if the string is empty
	 * 
	 * @param o
	 */
	public static boolean isNullOrBlank(Object o) {
		return o == null || (o instanceof String && o.toString().length() == 0);
	}

	public static boolean endsWith (String s, String suffix) {
		int index = s.length() - suffix.length();
		if (index < 0) {
			return false;
		}
		return s.substring(index).equalsIgnoreCase(suffix);
	}
	
	public static boolean beginsWith (String s, String prefix, boolean ignoreCase) {
		if (s.length() < prefix.length()) {
			return false;
		}
		return ignoreCase? s.substring(0, prefix.length()).equalsIgnoreCase(prefix) :
			s.substring(0, prefix.length()).equals(prefix);
	}
	
	public static String getFileExtension(String s) {
		int dotPos = s.lastIndexOf('.');
		// ignore files with . as the first character
		// also ignore files where the . is the last character
		if (dotPos <= 0 && dotPos != s.length() - 1) {
			return "";
		} else {
			return s.substring(dotPos);
		}
	}
	
	public static String trimFileExtension(String s) {
		int dotPos = s.lastIndexOf('.');
		// ignore files with . as the first character
		// also ignore files where the . is the last character
		if (dotPos <= 0 && dotPos != s.length() - 1) {
			return s;
		} else {
			return s.substring(0, dotPos);
		}
	}
	
	public static List<Integer> indexOf (String text, String token) {
		List<Integer> res = new Vector<>();
		int curPos = 0;
		while ((curPos = text.indexOf(token, curPos)) >= 0) {
			res.add(curPos++);
		}
		return res;
	}

	public static boolean contains (String s, String value) {
		char[] str1 = s.toCharArray();
		char[] str2 = value.toCharArray();
		int n = str1.length, m = str2.length;
		int i, j;
		boolean match;

		for (i = 0; i <= n-m; i++) {
			match = true;
			for (j = 0; j < m; j++) {
				if (str1[i+j] != str2[j]) {
					match = false;
					break;
				}
			}
			if (match) return true;
		}		
		return false;
	}
	
	/**
	 * Get the edit distance between 2 strings ignoring case
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int getEditDistance (String s1, String s2) {
		int s1L = s1.length(), s2L = s2.length();
		
		if (s1L == 0) {
			return s2L;
		} else if (s2L == 0) {
			return s1L;
		}
		
		int [][] dists = new int [s1L][s2L];
		char [] c1 = s1.toCharArray();
		char [] c2 = s2.toCharArray();
		
		for (int i = 0; i < s1L; i++) {
			for (int j = 0; j < s2L; j++){
				if (i==0 && j==0) {
					dists[i][j] = 0;
				} else if (i==0) {
					dists[i][j] = dists[i][j-1] + 1;
				} else if (j==0) {
					dists[i][j] = dists[i-1][j] + 1;
				} else if (Character.toLowerCase(c1[i-1]) == Character.toLowerCase(c2[j-1])) {
					dists[i][j] = dists[i-1][j-1];
				} else {
					dists[i][j] = Math.min(Math.min(dists[i-1][j-1] + 1, dists[i-1][j]), dists[i][j-1]) + 1;//min of the 3
				}
			}
		}
		return dists[s1L-1][s2L-1];
	}
	
	public static String createString(String...strings) {
		return getDelimitedString("", strings);
	}

	public static String getDelimitedString(String delimiter, String... strings) {
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = CollectionUtil.getIterator(strings);
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}
}
