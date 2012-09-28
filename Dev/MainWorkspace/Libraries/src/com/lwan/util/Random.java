package com.lwan.util;

public final class Random { 
	private static java.util.Random src = new java.util.Random();
	
	/**
	 * Generate a random alphanumeric string of specified length
	 * Note that the letters will all be lower case
	 * 
	 * @param length
	 * @return
	 */
	public static String stringAN (int length) {
		char [] chars = new char [length];
		for (int i = 0; i < length; i++) {
			chars[i] = alphanumeric ();
		}
		return new String(chars);
	}
	
	
	/**
	 * Generate a random boolean
	 * 
	 * @return
	 */
	public static boolean bool () {
		return src.nextBoolean();
	}
	
	/**
	 * Generate a random alphanumeric character (note only lower letters) 
	 * 
	 * @return
	 */
	public static char alphanumeric () {
		if (bool()) {
			return charS();
		} else {
			return charInt();
		}
	}
	
	/**
	 * Generate a random lower case char
	 * 
	 * @return
	 */
	public static char charS() {
		return (char)integer (97, 122);
	}
	
	/**
	 * Generate a random character representing 0-9
	 * 
	 * @return
	 */
	public static char charInt() {
		return (char)integer (48, 57);
	}
	
	/**
	 * Generate a random integer between (inclusive) min and max
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int integer (int min, int max) {
		return (int)(src.nextDouble() * (max-min+1) + min);
	}
	
	/**
	 * Generate a random long integer between (inclusive) min and max
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static long longInt (long min, long max) {
		return (long)(src.nextDouble() * (max-min+1)) + min;
	}
}
