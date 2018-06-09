package cn.chouchou.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class LineInfo {

	
	@Test
	public  void  test(){
		String msg="<tr><td>num</td><td>1</td></tr><tr><td>card</td><td>001</td></tr>";
		String regex="^<tr>.+</tr>$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(msg);
		while(matcher.find()){
		  String row=matcher.group();
		  System.out.println(row);
		}
		
		/*String[] ss=msg.split(regex);
		for(String s:ss){
			System.out.println(s);
			s=s.substring(s.lastIndexOf("<td>")+4, s.lastIndexOf("</td>"));
			System.out.println("in the end:s="+s);
		}*/
	}
	
	
	
	@Test
	public  void  test2(){
	
		 
		 
		   Calendar  calendar=Calendar.getInstance();
		   
		   calendar.set(Calendar.HOUR_OF_DAY, 0);
		   calendar.set(Calendar.MINUTE, 0);
		   calendar.set(Calendar.SECOND, 0);
		   
		   
		   
		   
		   SimpleDateFormat  sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   String now_we_need=sdf.format(calendar.getTime());
		   System.out.println(now_we_need);
		   
		   
		   calendar.add(Calendar.HOUR_OF_DAY, -1);
		   String now_we_need2=sdf.format(calendar.getTime());
		   System.out.println(now_we_need2);
		 
		 
		 
		 
		 
		
	
	}
}
