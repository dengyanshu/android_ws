package cn.chouchou;



import org.apache.commons.dbutils.QueryRunner;

import cn.chouchou.util.JDBCUtil;
import cn.chouchou.util.XiaomiWebUtil;

public class MyMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String info=null;
		
	     try {
	    	 info=XiaomiWebUtil.getLineInfo("20171107","20171107","00","01");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	     QueryRunner  qr=JDBCUtil.getQueryRunner();
	     
	     
	     /*Line Information (Line Machine Table Feeder FeederSerial NozzleChanger NozzleHolder NozzleSerial)
	     Spoilage Rate [PPM]	575
	     Total Pickup Count	238385
	     Total Error Count	137
	     Pickup Error	36
	     Recognition Error	101
	     Thick Error	0
	     Placement Error	0
	     Part Drop Error	0
	     Transfer unit parts drop error Count	0*/

	     
	}

}
