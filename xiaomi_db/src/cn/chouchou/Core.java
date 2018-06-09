package cn.chouchou;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import cn.chouchou.entity.LineInfo;
import cn.chouchou.entity.Machine;
import cn.chouchou.util.DateUtil;
import cn.chouchou.util.JDBCUtil;
import cn.chouchou.util.LineUtil;
import cn.chouchou.util.MachineUtil;
import cn.chouchou.util.SlotUtil;
import cn.chouchou.util.XiaomiWebUtil;

public class Core {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception{
		
		// TODO Auto-generated method stub
		Properties properties=new Properties();
		InputStream  is=Core.class.getClassLoader().getResourceAsStream("server.properties");
		properties.load(is);
		final String linename=properties.getProperty("line");
		final String ip=properties.getProperty("ip");
		System.out.println("line="+linename+",ip="+ip);
		//
		TimerTask  task=new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				 String info=null;
				 Map<String,String> list=null;
			     try {
			    	 list= DateUtil.getHalfHourSelectPara();
					 //day1 h1  day2 h2  此参数写入数据库
			    	 System.out.println("input to songxia server data:"+list.toString());
			    	 info=XiaomiWebUtil.getSlotInfo(
			    			 ip,
 list.get("day1"), list.get("day2"), list.get("h1"),list.get("h2"),
 list.get("m1"),list.get("m2"),list.get("s1"),list.get("s2")
			             //(String day1,String day2,String h1,String h2,String m1,String m2,String s1,String s2)
                     );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			     if(info==null||info.length()<=20){
			    	 return  ;
			     }
			     //解析slot信息
			     Map<String, Object> res=null;
			     List<Machine>  machines=null;
				try {
					res = SlotUtil.getSlotinfo(info);
					machines=MachineUtil.getMachines(info);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			     //插入数据库
			     try {
			    	  String info_db=list.get("start")+"-"+list.get("end")+",line="+linename;
					  JDBCUtil.insertSlots(JDBCUtil.getQueryRunner(), res, info_db);
					  JDBCUtil.insertMachines(JDBCUtil.getQueryRunner(), machines, info_db);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		
		Timer  timer=new Timer();
		timer.schedule(task, 0,1000*60*5);
		
		
		
		
		
		
	     
	    
	}
	
	
	

}
