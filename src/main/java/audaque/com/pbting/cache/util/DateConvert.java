package audaque.com.pbting.cache.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class DateConvert {
	
	/**
	 * Abstract: Convert a string to date
	 * @param strDate: String format date, yyyy-mm-dd hh:mm:ss
	 * @return
	 */
	public static final String YYYY_MM_DD_HH_MM_SS ="yyyy-MM-dd HH:mm:ss";
	
	public static final String YY_MM_DD = "yyyy-MM-dd";
	/**
	 * 
	 * @param format 按那种格式进行转换成Date实例
	 * @param strDate 日期格式的字符串表示
	 * @return 返回日期格式的字符串所对应的Date实例
	 * @throws ParseException
	 */
	public static Date StringToDate(String format,String strDate)throws ParseException{
		
		return new SimpleDateFormat(format).parse(strDate);
	}
	/**
	 * 
	 * @param formatDate实例转换成字符串形式的格式
	 * @param date 将要转换的日期实例
	 * @return 丹桂带指定格式的日期字符串表现形式
	 * @throws ParseException
	 */
	public static String DateToString(String format,Date date)throws ParseException{
		return new SimpleDateFormat(format).format(date);
	}
	
	public static String calculate(String stringDate) throws ParseException{
		Date temp = StringToDate(YYYY_MM_DD_HH_MM_SS, stringDate);
		
		Calendar cal1 =  Calendar.getInstance();
		cal1.setTime(temp);
		//得到年月日时分秒的数据表示
		int old_year = cal1.get(Calendar.YEAR);
		int old_mon = cal1.get(Calendar.MONTH)+1;
		int old_day_of_mon = cal1.get(Calendar.DAY_OF_MONTH);
		int old_hour_of_day = cal1.get(Calendar.HOUR_OF_DAY);
		//得到表示的分钟
		int old_min = cal1.get(Calendar.MINUTE);
		//得到表示的秒
		int old_second = cal1.get(Calendar.SECOND);
		cal1.setTime(new Date());
		
		int now_year = cal1.get(Calendar.YEAR);
		int now_mon = cal1.get(Calendar.MONTH)+1;
		int now_day_of_mon = cal1.get(Calendar.DAY_OF_MONTH);
		int now_hour_of_day = cal1.get(Calendar.HOUR_OF_DAY);
		int now_min = cal1.get(Calendar.MINUTE);
		int now_sec = cal1.get(Calendar.SECOND);
		
		//判断是否在今年之内
		int year_ = now_year - old_year;
		StringBuffer sb = new StringBuffer();
		
		if(year_ != 0){//不等于0 ，则说明是隔年,直接返回几年前就可以了
			return year_+"  年前..";
		}
		//如果在年相等的情况下，则显示是在几个月前就可以
		if((now_mon - old_mon) != 0){
			return (now_mon - old_mon)+"  月前..";
		}
		//如果在月相等的情况下，则返回是在几天前
		if((now_day_of_mon - old_day_of_mon) != 0){
			return (now_day_of_mon - old_day_of_mon)+"  天前..";
		}
		
		//如果是在日期也相等的情况下，则返回是在几分钟前
		
		if((now_hour_of_day - old_hour_of_day) != 0){
			
			return (now_hour_of_day - old_hour_of_day)+"  小时前..";
		}
		
		//如果是在小时也相等的情况下，则返回是在几分钟前
		if((now_min - old_min) != 0){
			
			return (now_min - old_min)+"  分钟前..";
		}
		
		//如果是分钟相等，则计算是在几秒钟前添加
		if((now_sec-old_second) != 0){
			return (now_sec-old_second)+"  秒钟前..";
		}
		return null;
	}
	
	public static void main(String[] args) throws ParseException {
		
		String str = "2014-01-24 14:18:12";
		System.out.println(DateConvert.calculate(str));
		
//		try {
//			Date date = DateConvert.StringToDate(DateConvert.YY_MM_DD, "2014-01-28");
//			System.out.println(date.toString());
//			String ds = DateConvert.DateToString(YY_MM_DD, date);
//			System.out.println(ds);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
	}
}
