package com.lwan.util;

public class EnumUtil {
	
//	public static EnumExist
	
	/**
	 * Get a more human readable string representing a enum constant by
	 * replacing '_' with ' ' and making all words start with upper case
	 * followed by lower case letters.
	 * 
	 * @param e
	 * @return
	 */
	public static String processEnumName (Enum<?> e) {
		char[] chars = e.toString().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '_') {
				chars[i] = ' ';
			} else if (Character.isLetter(chars[i])) {
				if (i == 0 || chars[i-1] == ' ') {
					chars[i] = Character.toUpperCase(chars[i]);
				} else {
					chars[i] = Character.toLowerCase(chars[i]);
				}
			}
		}
		return new String(chars);
	}
	
	/** 
	 * Get the original enum from a processed enum name.
	 * See processEnumName
	 * 
	 * @param enumType
	 * @param s
	 * @return
	 */
	public static <T extends Enum<T>> Enum<T> getEnum (Class<T>enumType, String s) {
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == ' ') {
				chars[i] = '_';
			} else if (Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
			}
		}
		return Enum.valueOf(enumType, new String(chars));
	}
}
