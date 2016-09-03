package audaque.com.pbting.cache.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

	public static Date getNextDate() {
		Calendar cal = getCalendar();
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}
	
	/**
	 * 获取系统时间
	 * 
	 * @return
	 */
	public static java.util.Calendar getCalendar() {
		java.util.Calendar nowCalendar = java.util.Calendar.getInstance();
		nowCalendar.setTime(new java.util.Date());
		return nowCalendar;
	}
}
