package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandler {
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static String convert(Date date) {
		return format.format(date);
	}
}
