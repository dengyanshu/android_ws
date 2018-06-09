package com.zowee.mes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 * @description 日期操作辅助类
 */
public class DateUtils 
{

	/**
	 * @param date
	 * @param regex
	 * @return
	 * @description 把日期转换成指定的字符形式   例如  regex yyyy-MM-dd hh-mm-ss = 2012-09-14 12:30:49
	 */
	public static String conDateToStr(Date date,String regex)
	{
		if(null==date||StringUtils.isEmpty(regex))
				return null;
		SimpleDateFormat sdf = new SimpleDateFormat(regex);
		
		return sdf.format(date);
	}
	
	/**
	 *  
	 * @description 日期验证表达式  验证格式为 yyyy/-MM/-dd 或 yyyy-MM-dd 
	 */
	public static boolean  subDate(String datetime)
	{
		if(StringUtils.isEmpty(datetime)) return false;
		String regex = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(datetime);
		boolean isLegal = matcher.matches();
		
		return isLegal;
	}
	
	/**
	 * @param datetime
	 * @return
	 * @description 完整的日期格式验证   yyyy/-MM/-dd hh：mm:ss 
	 */
	public static boolean  subFullDate(String datetime)
	{
		if(StringUtils.isEmpty(datetime)) return false;
		String regex = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(datetime);
		boolean isLegal = matcher.matches();
		
		return isLegal;
	}
	
	/**
	 * @param datetime
	 * @return
	 * @description 对用户输入的日期进行补全 如 2011-8-1 补全后为2011-08-01 
	 */
	public static String repairDate(String datetime)
	{
		String regex = "[/-]\\d{2}[/-]\\d{2}";
		//String repairedRegex = "-\\d{1,2}-";
		//List<int[]> matInds = null;
		if(StringUtils.findStr(datetime, regex))
			return datetime;
		String tempRegex = "-";
		int first = -1;
		int last = -1;
		if(-1==(first=datetime.indexOf(tempRegex)))
			first=datetime.indexOf("/");
		if(-1==(last=datetime.lastIndexOf(tempRegex)))
			last=datetime.lastIndexOf("/");
		first +=1;
		last +=1;
		if(2==last-first&&1==datetime.length()-last)
		{
			datetime = datetime.substring(0,first)+"0"+datetime.substring(first);
			datetime = datetime.substring(0,last+1)+"0"+datetime.substring(last+1); 
		}
		else if(2==last-first)
			datetime = datetime.substring(0,first)+"0"+datetime.substring(first); 
		else if(1==datetime.length()-last)
			datetime = datetime.substring(0,last)+"0"+datetime.substring(last); 
			
		return datetime;
	}
	
	
}
