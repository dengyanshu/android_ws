package cn.chouchou.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;


public class DateUtil {
    
	public List<String>  getXmSelectParas(){
		Calendar  calendar=Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		SimpleDateFormat  sdf=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String nowStr=sdf.format(calendar.getTime());
		
		
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		String oneHourBefore=sdf.format(calendar.getTime());
		
		
		List<String>  res=new ArrayList<>();
		res.add(oneHourBefore.substring(0, oneHourBefore.indexOf(" ")));
		res.add(oneHourBefore.substring(oneHourBefore.indexOf(" ")+1,oneHourBefore.indexOf(":")));
		
		
		res.add(nowStr.substring(0, oneHourBefore.indexOf(" ")));
		res.add(nowStr.substring(oneHourBefore.indexOf(" ")+1,oneHourBefore.indexOf(":")));
		return res;
		
	}
	
	@Test
	public  void  TestgetXmSelectParas(){
		List<String> list=getXmSelectParas();
		System.out.println(list);
	}
	
	
}
