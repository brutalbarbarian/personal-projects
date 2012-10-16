package com.lwan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeUtil {
	/**
	 * Parse date to a long format
	 * 
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static long parseDate (String date, String format) throws ParseException {
		return new SimpleDateFormat(format).parse(date).getTime();
	}
	
	public static String secondsToString (int seconds) {
		seconds = Math.abs(seconds);
		
		if (seconds < 60) {
			return timeToString(0, 0, seconds);
		}
		// display in hh:mm:ss
		// find number of minutes
		int minutes = seconds / 60;
		seconds = seconds % 60;
		
		if (minutes < 60) {
			return timeToString(0, minutes, seconds);
		}
		
		// find number of hours
		int hours = minutes / 60;
		minutes = minutes % 60;
		
		return timeToString(hours, minutes, seconds);
	}
	
	/**
	 * 
	 */
	public static int parseTime(String time) {
		int result = 0;
		String[] t = time.split(":");
		for(int i = 0; i < t.length; i++) {
			result = Integer.parseInt(t[t.length - 1 - i]) * MathUtil.pow(60, i); 
		}
		return result;
	}
	
	public static String timeToString(int hours, int minutes, int seconds) {
		StringBuilder sb = new StringBuilder();
		if (hours != 0) {
			sb.append(hours).append(':');
		}
		if (minutes != 0) {
			if (hours != 0 && minutes < 10) {
				sb.append(0);
			}
			sb.append(minutes).append(':');
		}
		if (seconds != 0) {
			if (minutes != 0 && seconds < 10) {
				sb.append(0);
			}
			sb.append(seconds);
		}
		return sb.toString();
	}
}
