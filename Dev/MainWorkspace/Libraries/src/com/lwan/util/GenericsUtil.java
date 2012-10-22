package com.lwan.util;

/**
 * Utility class with methods that mainly deal with nullable values
 * and the Generics framework.
 * 
 * @author Brutalbarbarian
 *
 */
public class GenericsUtil {
	/**
	 * A safe way of comparing 2 objects using 'equals()' as opposed to
	 * '==' where either a or b may be null. 
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T> boolean Equals(T a, T b) {
		if (a == null && b == null) {
			return true;
		} else if (a == null || b == null) {
			return false;
		} else {
			return a.equals(b);
		}
	}

	/**
	 * Convenience method of choosing the first non-null value starting from the left
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	@SafeVarargs
	public static <T> T Coalice(T...values) {
		for (T item : values) {
			if (item != null) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * Convenience method for toString, which returns the passed in nullString if the value is null,
	 * or the result of toString() if the object is not null.
	 * 
	 * @param o
	 * @param nullString
	 * @return
	 */
	public static String toString(Object o, String nullString) {
		if (o == null) {
			return nullString;
		} else {
			return o.toString();
		}
	}
}
