package com.deng.jdbctest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class jdbc01 {
   
	private  String url="jdbc:mysql://localhost:3306/test";
	@Test
	public  void  test1(){
		Connection  conn=null;
		Statement  statement=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn=DriverManager.getConnection(url, "root", "sa");
			statement=conn.createStatement();
			String sql="create  table  stu(id int,name nvarchar(50))";
			statement.executeUpdate(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
		
	}
	
	
}
