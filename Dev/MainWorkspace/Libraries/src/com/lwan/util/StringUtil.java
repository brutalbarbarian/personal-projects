package com.lwan.util;

import java.util.List;
import java.util.Vector;

public class StringUtil {
	public static String getRepeatedString (String s, int num){
		StringBuilder sb = new StringBuilder(s.length() * num);
		for (int i = 0; i < num; i++) {
			sb.append(s);
		}
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

	public static boolean beginsWith (String s, String prefix) {
		if (s.length() < prefix.length()) {
			return false;
		}
		return s.substring(0, prefix.length()).equalsIgnoreCase(prefix);
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
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append(s);
		}
		return sb.toString();
	}
}
