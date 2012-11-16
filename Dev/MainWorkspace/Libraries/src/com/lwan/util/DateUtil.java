package com.lwan.util;

import java.util.Calendar;
import java.util.Date;;

public class DateUtil {
	
	private static Calendar cal;
	public static Date getCurrentDate() {
		if (cal == null) {
			cal = Calendar.getInstance();
		}
		return cal.getTime();
	}
}
