package com.lwan.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	private static Calendar cal;
	public static Date getCurrentDate() {
		if (cal == null) {
			cal = Calendar.getInstance();
		}
		return cal.getTime();
	}
	
	public static Calendar floor(Calendar c, int mode) {
		Calendar result = (Calendar) c.clone();
		// Floor means we want all fields lower then this to go to its minimum
		int nextMode = mode;
		while ((nextMode = getBelowMode(nextMode)) >= 0) {
			result.set(nextMode, result.getMinimum(nextMode));
		}
		
		return result;
	}
	
	public static Calendar ceil(Calendar c, int mode) {
		Calendar result = floor((Calendar)c.clone(), mode);
		if (result.before(c)) {	
			result.add(mode, 1);
		}
		return result;
	}
	
	public static int getAboveMode(int mode) {
		switch(mode) {
		case Calendar.MONTH:
			return Calendar.YEAR;
		case Calendar.WEEK_OF_MONTH:
		case Calendar.WEEK_OF_YEAR:
		case Calendar.DAY_OF_MONTH:
		case Calendar.DAY_OF_WEEK:
		case Calendar.DAY_OF_YEAR:
			return Calendar.MONTH;
		case Calendar.HOUR:
		case Calendar.HOUR_OF_DAY:
			return Calendar.DATE;
		case Calendar.MINUTE:
			return Calendar.HOUR_OF_DAY;
		case Calendar.SECOND:
			return Calendar.MINUTE;
		case Calendar.MILLISECOND:
			return Calendar.SECOND;
		}
		// ERA and YEAR is ignored
		return -1;
	}
	
	public static int getBelowMode(int mode) {
		switch(mode) {
		case Calendar.YEAR:
			return Calendar.MONTH;
		case Calendar.MONTH:
		case Calendar.WEEK_OF_MONTH:
		case Calendar.WEEK_OF_YEAR:
			return Calendar.DAY_OF_MONTH;
		case Calendar.DAY_OF_MONTH:
		case Calendar.DAY_OF_WEEK:
		case Calendar.DAY_OF_WEEK_IN_MONTH:
		case Calendar.DAY_OF_YEAR:
			return Calendar.HOUR_OF_DAY;
		case Calendar.HOUR:
		case Calendar.HOUR_OF_DAY:
			return Calendar.MINUTE;
		case Calendar.MINUTE:
			return Calendar.SECOND;
		case Calendar.SECOND:
			return Calendar.MILLISECOND;
		}		
		return -1;
	}
}
