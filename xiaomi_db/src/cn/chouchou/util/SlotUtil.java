package cn.chouchou.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cn.chouchou.entity.Slot;


/*
 * 传入完整的msg  返回一个map  包含qty 和槽位信息
 * 
 * **/

public class SlotUtil {
   
	
	public static Map<String,Object>  getSlotinfo(String msg)throws Exception{
		Map<String,Object>  res=new HashMap<String, Object>();
		
		
		//获取qty  main_qty
		//实际中发现 无规则  所以暂时不取这个数据
		
		
		/*String  count_str=msg.substring(msg.indexOf("Production Information"), msg.indexOf("Stop information per machine"));
		count_str=count_str.replace("\"", "'");
		String regex="<td[a-zA-Z_0-9=>'\\s]+</td>";
		
		
		 String main_qty_str=count_str.substring(count_str.indexOf("<tr"), count_str.indexOf("</tr>")+5);//板数
		 System.out.println(main_qty_str);
		 count_str=count_str.replace(main_qty_str, "");
		String qty_str=count_str.substring(count_str.indexOf("<tr"), count_str.indexOf("</tr>")+5);
		System.out.println(qty_str);
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(main_qty_str);
		matcher.find();
		matcher.find();
		matcher.find();
		matcher.find();
		matcher.find();
		matcher.find();
		String group = matcher.group();
		//获取板数
		res.put("main_qty",group.substring(group.indexOf("<td>")+4, group.indexOf("</td>")) );
		 System.out.println(group);
	
		 matcher=pattern.matcher(qty_str);
		 matcher.find();
		 matcher.find();
		 group = matcher.group();
		 res.put("qty",group.substring(group.indexOf("<td>")+4, group.indexOf("</td>")));
		 System.out.println(group);
		*/
		
		res.put("main_qty","0");
		 res.put("qty","0");
		
		//获取qty  main_qty
		
		
	    /*
	     *   ==Pickup Count per Feeder
             ===Pickup Count per Feeder Serial
	     * 
	     * */
		String slot_str=msg.substring(msg.indexOf("Pickup Count per Feeder"), 
				msg.indexOf("Pickup Count per Feeder Serial"));
		List<Slot> slots=getSlots(slot_str);
		slots.remove(0);
		res.put("slots", slots);
		return res;
	}
	
	private   static List<Slot>   getSlots(String slot_str )throws Exception{
		//去掉table 前后垃圾
		slot_str=slot_str.substring(slot_str.indexOf("<table"),slot_str.lastIndexOf("</table>")+8);
		System.out.println("parse xml="+slot_str);
	    InputStream is= new  ByteArrayInputStream(slot_str.getBytes());
		//InputStream is=new FileInputStream("E:/androidWorkspace/xiaomi_db/src/responsebak.txt");
	    SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
	    final List<Slot> slots=new ArrayList<Slot>();
	    saxParser.parse(is, new DefaultHandler(){
	    	private  String tagname="";
	    	private  int count=1;//记td的个数
	    	
	    	
	    	private Slot slot;
	    	
	    	@Override
	    	public void startDocument() throws SAXException {
	    		// TODO Auto-generated method stub
	    		super.startDocument();
	    		//System.out.println("begin ..document");
	    	}
	    	@Override
	    	public void endDocument() throws SAXException {
	    		// TODO Auto-generated method stub
	    		super.endDocument();
	    		//System.out.println("end ..document");
	    	}
	    	@Override
	    	public void startElement(String uri, String localName,
	    			String qName, Attributes attributes) throws SAXException {
	    		// TODO Auto-generated method stub
	    		//System.out.println("start element="+qName);
	    		tagname=qName;
	    		if(tagname.equals("table")){
	    			
	    		}
	    		else if(tagname.equals("tr")){
	    			slot=new Slot();
	    			count=1;
	    		}
	    		else  if(tagname.equals("td")){
	    			
	    		}
	    		
	    	}
	    	@Override
	    	public void endElement(String uri, String localName, String qName)
	    			throws SAXException {
	    		// TODO Auto-generated method stub
	    		//System.out.println("end element= "+qName);
	    		if(qName.equals("tr")){
	    			slots.add(slot);
	    		}
	    		tagname=null;
	    	}
	    	@Override
	    	public void characters(char[] ch, int start, int length)
	    			throws SAXException {
	    		// TODO Auto-generated method stub
	    		String text=new String(ch,start,length);
	    		//System.out.println("text_tagname=="+tagname);
	    		//System.out.println("text="+text);
	    		if(tagname!=null){
		    		if(tagname.equals("table")){
		    			
		    		}
		    		else if  (tagname.equals("tr")){
		    			
		    		}
		    		else  if(tagname.equals("td")){
		    			switch(count){
		    			    case 1:
		    			    	slot.setMachine(text);
		    				break;
		    			    case 2:
		    			    	slot.setSlotname(text+"-");
		    				break;
							case 3://slot
								slot.setSlotname(slot.getSlotname()+text+"-");
							 break;
							case 4://L/R
								slot.setSlotname(slot.getSlotname()+text);
							 break;
							case 5://productname
								slot.setProductname(text);
							 break;
							case 6://library
								slot.setLibrary(text);
							 break;
							case 7://Pickup Count
								slot.setPickup_Count(text);
							 break;
							 
							case 8:
								slot.setError_Count(text);
							 break;
							 
							case 9:
								slot.setSpoilage_Rate(text);
							 break; 
							 
							case 10:
								slot.setError_Rate(text);
							 break; 
							 
							case 11:
								slot.setPickup_Error(text);
							break; 
							case 12:
								slot.setRecognition_Error(text);
							break; 
							case 13:
								slot.setThick_Error(text);
							break; 
							case 14:
								slot.setPlacement_Error(text);
							break;
							case 15:
								slot.setPart_Drop_Error(text);
							break;
							case 16:
								slot.setTransfer_unit_parts_drop_error_Count(text);
							break;
		    			}
		    			count++;
	    		}
	    	  }
	    		
	    	}
	    });
		
		
		return  slots;
	}
}
