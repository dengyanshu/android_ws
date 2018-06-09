package cn.chouchou.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import cn.chouchou.entity.Machine;

/**
 * 
 * 获取所有的机台的信息
 * */

public class MachineUtil {

	
	public  static List<Machine>  getMachines(String msg){
		
		 msg=msg.substring(msg.lastIndexOf("Production Information"));
		 msg=msg.substring(0, msg.indexOf("Stop information per machine"));
		 msg=msg.replace("</tr></tr>", "</tr>").replace("\"", "");
		 msg=msg.substring(msg.indexOf("<table"), msg.lastIndexOf("</table>")+8);
		
		//Transfer unit parts drop error Count</td><td>0</td></tr>
		List<Machine>  machines=new ArrayList<Machine>();
		String machine_flag="";//记录当前machine
		//String regex="(<tr>[</>\\[\\]%_A-Za-z0-9=: \\.]+?Transfer unit parts drop error Count</td><td>\\d+</td></tr>)";
	   String regex="<tr>.+?Transfer unit parts drop error Count</td><td>\\d+</td></tr>";
	   //此处一定要防止正则的贪婪 不然取不到
		Pattern pattern=	Pattern.compile(regex);
	   Matcher matcher= pattern.matcher(msg);
	   while(matcher.find()){
		  String machine_str=matcher.group();
		  //获取了单条数据
		 Machine machine= getMachine(machine_str);
		 String machine_name=machine.getMachine();
		 if(machine_name!=null&&!"".equals(machine_name)){
			 machine_flag=machine_name;
		 }
		 else{
			 machine.setMachine(machine_flag);
		 }
		 machines.add(machine);
	  }
		return  machines;
	}
	
   
	/**
	 * 
	 * 单条string 解析成machine
	 * 注意 html  table 的合并行
	 * 需要flag去记录machine的序列号
	 * */
	
	private static Machine getMachine(String machine_str){
		//machine_str="<tr><td rowspan=30 class=table_background_itemColor2>Machine 1</td><td rowspan=15 class=table_background_itemColor2>Stage 1</td><td class=table_background_itemColor1>Panel Count</td><td rowspan=15 width=10></td><td>48</td></tr><tr><td class=table_background_itemColor1>Pattern Count</td><td>192</td></tr><tr><td class=table_background_itemColor1>Running Rate[%]</td><td>6.38</td></tr><tr><td class=table_background_itemColor1>Running Time</td><td>0:29:47</td></tr><tr><td class=table_background_itemColor1>Real Running Time</td><td>0:01:54</td></tr><tr><td class=table_background_itemColor1>Total Stop Time</td><td>0:27:53</td></tr><tr><td class=table_background_itemColor1>Spoilage Rate [PPM]</td><td>0</td></tr><tr><td class=table_background_itemColor1>Total Pickup Count</td><td>0</td></tr><tr><td class=table_background_itemColor1>Total Error Count</td><td>0</td></tr><tr><td class=table_background_itemColor1>Pickup Error</td><td>0</td></tr><tr><td class=table_background_itemColor1>Recognition Error</td><td>0</td></tr><tr><td class=table_background_itemColor1>Thick Error</td><td>0</td></tr><tr><td class=table_background_itemColor1>Placement Error</td><td>0</td></tr><tr><td class=table_background_itemColor1>Part Drop Error</td><td>0</td></tr><tr><td class=table_background_itemColor1>Transfer unit parts drop error Count</td><td>0</td></tr>";
		
		String regex="<tr>.+?</tr>";
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(machine_str);
		int lineNumber=0;
		Machine  machine=new  Machine();
		while(matcher.find()){
			lineNumber++;
			String line=matcher.group();//依次匹配的行
			System.out.println("machine tr line="+line);
			switch (lineNumber) {
			case 1:
				String []ss=line.split("</td>");
				if(ss.length==6){
				    machine.setMachine(ss[0].substring(ss[0].lastIndexOf(">")+1));
				    machine.setStage(ss[1].substring(ss[1].indexOf(">")+1));
				    machine.setBroad_qty(Integer.parseInt((ss[4].substring(ss[4].indexOf(">")+1))));
				}
				else if(ss.length==5){
				    machine.setStage(ss[0].substring(ss[0].lastIndexOf(">")+1));
				    machine.setBroad_qty(Integer.parseInt((ss[3].substring(ss[3].indexOf(">")+1))));
				}
			break;
			case 2:
				String qty=line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>"));
				 machine.setQty(Integer.parseInt(qty));
			break;
			case 3:
				machine.setRunning_Rate(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
			break;
			case 4:
				//运行时间都是30分钟
				break;
			case 5:
				machine.setReal_Running_Time(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				break;
			case 6:
				machine.setTotal_Stop_Time(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				break;
			case 7:
				machine.setSpoilage_Rate(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				break;
			case 8:
				    machine.setTotal_Pickup_Count(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				break;
			case 9:
				machine.setTotal_Error_Count(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				break;
				
			case 10:
				machine.setPickup_Error(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				break;
			case 11:
				machine.setRecognition_Error(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				break;
			case 12:
				machine.setThick_Error(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				break;
			case 13:
				machine.setPlacement_Error(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));	
			   break;
			case 14:
				machine.setPart_Drop_Error(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				   break;	
			case 15:
				machine.setTransfer_unit_parts_drop_error_Count(line.substring(line.lastIndexOf("<td>")+4,line.lastIndexOf("</td>")));
				   break;	
			default:
				break;
			}
			
		}
		return machine;
	}
	
	
	
}
