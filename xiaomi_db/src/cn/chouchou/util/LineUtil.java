package cn.chouchou.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import cn.chouchou.entity.LineInfo;

import com.sun.org.apache.xerces.internal.util.XML11Char;

/**
 * 已废弃！
 * 
 * 把string  xml  解析成一个LineInfo  实体对象
 * 简单字符串截取  后续优化pullPurser
 * 
 * **/


//table frame="box" class="left"><tr><td class="table_background_itemColor1">Spoilage Rate [PPM]</td><td>627</td></tr><tr><td class="table_background_itemColor1">Total Pickup Count</td><td>233001</td></tr><tr><td class="table_background_itemColor1">Total Error Count</td><td>146</td></tr><tr><td class="table_background_itemColor1">Pickup Error</td><td>28</td></tr><tr><td class="table_background_itemColor1">Recognition Error</td><td>118</td></tr><tr><td class="table_background_itemColor1">Thick Error</td><td>0</td></tr><tr><td class="table_background_itemColor1">Placement Error</td><td>0</td></tr><tr><td class="table_background_itemColor1">Part Drop Error</td><td>0</td></tr><tr><td class="table_background_itemColor1">Transfer unit parts drop error Count</td><td>0</td></tr></table><p class=">

public class LineUtil {
    
	public static LineInfo getIineInfo(String msg){
		 LineInfo  line=new LineInfo();
		 // String  msg="<tr><td class='table_background_itemColor1'>Spoilage Rate [PPM]</td><td>627</td></tr><tr><td class='table_background_itemColor1'>Total Pickup Count</td><td>233001</td></tr><tr><td class='table_background_itemColor1'>Total Error Count</td><td>146</td></tr><tr><td class='table_background_itemColor1'>Pickup Error</td><td>28</td></tr><tr><td class='table_background_itemColor1'>Recognition Error</td><td>118</td></tr><tr><td class='table_background_itemColor1'>Thick Error</td><td>0</td></tr><tr><td class='table_background_itemColor1'>Placement Error</td><td>0</td></tr><tr><td class='table_background_itemColor1'>Part Drop Error</td><td>0</td></tr><tr><td class='table_background_itemColor1'>Transfer unit parts drop error Count</td><td>0</td>";
	     String []ss=msg.split("</tr>");
	     for(int x=0;x<ss.length;x++){
	    	  String s=ss[x];
	    	  s=s.substring(s.lastIndexOf("<td>")+4, s.lastIndexOf("</td>"));
	    	  if(x==0){
	    		  line.setSpoilage_Rate(Integer.parseInt(s.trim()));
	    	  }
	    	  else if(x==1){
	    		  line.setTotal_Pickup_Count(Integer.parseInt(s.trim()));
	    	  }
	    	  else if(x==2){
	    		  line.setThick_Error(Integer.parseInt(s.trim()));
	    	  }
	    	  else if(x==3){
	    		  line.setPickup_Error(Integer.parseInt(s.trim()));
	    	  }
	    	  else if(x==4){
	    		  line.setRecognition_Error(Integer.parseInt(s.trim()));
	    	  }
	    	  else if(x==5){
	    		  line.setThick_Error(Integer.parseInt(s.trim()));
	    	  }
	    	  else if(x==6){
	    		  line.setPlacement_Error(Integer.parseInt(s.trim()));
	    	  }
	    	  else if(x==7){
	    		  line.setPart_Drop_Error(Integer.parseInt(s.trim()));
	    	  }
	    	  else if(x==8){
	    		  line.setTransfer_unit_parts_drop_error_Count(Integer.parseInt(s.trim()));
	    	  }
	    	 
	     }
	    // System.out.println(line);
		 return  line;
	}
	  
	
}
