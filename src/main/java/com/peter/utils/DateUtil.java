package com.peter.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static SimpleDateFormat format=new SimpleDateFormat();
	public static String Date2String(String pattern,Date date) {
		format.applyPattern(pattern);
		return format.format(date);
	}
}
