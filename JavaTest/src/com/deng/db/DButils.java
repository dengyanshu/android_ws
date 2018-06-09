package com.deng.db;

import java.sql.DriverManager;

public class DButils {
   
	
	public static final int TJ=0;
	public static final int SG=1;
	
	private  String mysql="jdbc:mysql//localhost:3306/test";
	private  String sqlserver=null;
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public  void  getConnect(int n,int m){ //n 代表链接那个类型数据库 m  代表
		if(n==0){
			//DriverManager.getConnection(mysql, user, password);
		}
		
	}
	
}
