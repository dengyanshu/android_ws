package cn.chouchou.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;



public class DateUtil {
    
	
	/**
	 * ԭʼ���Ե�ǰʱ��ǰ��10����~5����ǰ ����
	 *   ����̫��
	 * */
	public static Map<String,String>  getXmSelectParas(){
		Calendar  calendar=Calendar.getInstance();
		
		calendar.add(Calendar.MINUTE, -5);
		SimpleDateFormat  sdf=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String nowStr=sdf.format(calendar.getTime());
		
		
		calendar.add(Calendar.MINUTE, -5);
		String fiveMinuteBefore=sdf.format(calendar.getTime());
		
		
		Map<String,String> map=new HashMap<String, String>();
		
		map.put("start", fiveMinuteBefore);
		map.put("end", nowStr);
		
		map.put("day1", nowStr.substring(0, nowStr.indexOf(" ")));
		map.put("h1", nowStr.substring(nowStr.indexOf(" ")+1,nowStr.indexOf(":")));
		map.put("m1", nowStr.substring(nowStr.indexOf(":")+1,nowStr.lastIndexOf(":")));
		map.put("s1", nowStr.substring(nowStr.lastIndexOf(":")+1));
		
		map.put("day2", fiveMinuteBefore.substring(0, fiveMinuteBefore.indexOf(" ")));
		map.put("h2", fiveMinuteBefore.substring(fiveMinuteBefore.indexOf(" ")+1,fiveMinuteBefore.indexOf(":")));
		map.put("m2", fiveMinuteBefore.substring(fiveMinuteBefore.indexOf(":")+1,fiveMinuteBefore.lastIndexOf(":")));
		map.put("s2", fiveMinuteBefore.substring(fiveMinuteBefore.lastIndexOf(":")+1));
		
		
	
		return map;
		
	}
	
	
	
	/**
	 * �������Сʱ�� �������ݻ�ȡ�Ͳ��ķ���
	 * 
	 * */
	public static Map<String,String>  getHalfHourSelectPara(){
		SimpleDateFormat  sdf=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		
		String nowStr=null;
		String halfHourBefore = null;
		
		Calendar  calendar=Calendar.getInstance();
		//��ǰʱ��ǰ��5���� ��Ϊ����ԭʼ�����������ռ���5������ʱ
		calendar.add(Calendar.MINUTE, -5);
		
		
		
		//ǰ��5���� �����ǰʱ����ڵ���30   h:00:00  h:30:00
		if(calendar.get(Calendar.MINUTE)>=30){
			   calendar.set(Calendar.MINUTE, 30);
			   calendar.set(Calendar.SECOND, 0);
			   nowStr=sdf.format(calendar.getTime());
			   
			   calendar.add(Calendar.MINUTE, -30);
			   halfHourBefore=sdf.format(calendar.getTime());
		}
		//����   h-1:30:00  h:00:00
		else{
			 calendar.set(Calendar.MINUTE, 00);
			 calendar.set(Calendar.SECOND, 0);
			 nowStr=sdf.format(calendar.getTime());
			 
			 calendar.add(Calendar.MINUTE, -30);
			 halfHourBefore=sdf.format(calendar.getTime());
		}
		
		Map<String,String> map=new HashMap<String, String>();
		
		map.put("start", halfHourBefore);
		map.put("end", nowStr);
		
		map.put("day1", nowStr.substring(0, nowStr.indexOf(" ")));
		map.put("h1", nowStr.substring(nowStr.indexOf(" ")+1,nowStr.indexOf(":")));
		map.put("m1", nowStr.substring(nowStr.indexOf(":")+1,nowStr.lastIndexOf(":")));
		map.put("s1", nowStr.substring(nowStr.lastIndexOf(":")+1));
		
		map.put("day2", halfHourBefore.substring(0, halfHourBefore.indexOf(" ")));
		map.put("h2", halfHourBefore.substring(halfHourBefore.indexOf(" ")+1,halfHourBefore.indexOf(":")));
		map.put("m2", halfHourBefore.substring(halfHourBefore.indexOf(":")+1,halfHourBefore.lastIndexOf(":")));
		map.put("s2", halfHourBefore.substring(halfHourBefore.lastIndexOf(":")+1));
		
		
	
		return map;
		
	}
	
	@Test
	public  void test(){
		 System.out.println(getHalfHourSelectPara().toString());
	}

	
	
	
}
