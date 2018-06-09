package com.deng.instance;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class instanceMain {
	
	static People p;

	 /**
	 * @param args
	 * utf-8  gbk    iso8859-1  US-ASCII
	 */
	static HashMap<String, String> temp_map;
	public static void main(String[] args) {
		
		
		
		System.out.println(Math.random());
		Random  random=new Random();
		System.out.println(random.nextInt());
//		String res_str="";
//		String head_str="MOID";
//		
//		int num=1000;
//		
//		String  tempstr="";
//		while(num!=0)
//		{
//			int temp= num- (int)Math.floor(num/16)*16;
//			String s="";
//			switch(temp){
//			case  3:
//				s="3";
//				break;
//			case  4:
//				s="4";
//				break;
//			case  6:
//				s="6";
//				break;
//			case  8:
//				s="8";
//				break;
//			case  14:
//				s="E";
//				break;
//			}
//			tempstr=s+tempstr;
//			
//			num=(int)Math.floor(num/16);
//		}
//		tempstr=("00000000"+tempstr).substring(("00000000"+tempstr).length()-8, ("00000000"+tempstr).length());
//		res_str=head_str+tempstr;
//		System.out.println(res_str);
//				
		
		
		
//		String oldstr="ming tian wo men fang jia san tian ";
//		Pattern pattern=Pattern.compile("\\b\\S{3}\\b");
//		Matcher mathcer=pattern.matcher(oldstr);
//		while(mathcer.find()){
//			System.out.println(mathcer.group());
//		}
//		
		
	
		
//		List<HashMap<String, String>> test_list=new ArrayList<HashMap<String,String>>();
//		
//		temp_map=new HashMap<String, String> ();
//		temp_map.put("moname", "MO010215070");
//		test_list.add(temp_map);
//		
//        temp_map=new HashMap<String, String> ();
//		temp_map.put("moname", "MO010215060");
//		test_list.add(temp_map);
//		
//		temp_map=new HashMap<String, String> ();
//		temp_map.put("moname", "MO010214070");
//		test_list.add(temp_map);
//		
//		System.out.println(test_list);
//		ArrayList<Integer> index_list=new ArrayList<Integer>();
//		
//		for(int x=0;x<test_list.size();x++){
//			if(!test_list.get(x).containsValue("060")){
//				
//				continue;
//				
//			}
//			else
//				index_list.add(x);
//			
//		}
//		System.out.println(index_list);
//		for(int x=0;x<index_list.size();x++)
//		test_list.remove(x);
//		System.out.println(test_list);

	
		//原串去掉anyType{}
		//String str="anyType{SQLDataSet=anyType{moid=MOD100000VND; MOName=MOF060115030015; }; }";
		String str="anyType{anyType{anyType{moid=MOD100000VND; MOName=MOF060115030015; }; }";
		str=str.replaceAll("^(anyType\\{)\\1", "");
		str=str.replaceAll("\\}$", "");
		System.out.println(str);
		//原串的每条数据 解析成List<String>格式
		List<String>  row_string=new ArrayList<String>();
		
		String[] rows=str.split(" *\\}; *");
		for(String s:rows){
			s=s.replaceAll("SQLDataSet\\d*=anyType\\{", "");
			s=s.trim();
			System.out.println(s);
			row_string.add(s);
		}
		//List<String>每条数据解析成List<HashMap<String,String>>
		
		List<HashMap<String, String>> row_list_maps =new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		for(String s:row_string){
			map=new HashMap<String, String>();
			String[] fenhao_row=s.split(";");
			for(String s1:fenhao_row){
				String[] res_map=s1.split("=");
				if(res_map.length>=2){
				   String key=res_map[0].trim();
				   String values=res_map[1].trim();
				   map.put(key, values);
				}
				
			}
			row_list_maps.add(map);
		}
		System.out.println(row_list_maps);
	
		

	}

}
