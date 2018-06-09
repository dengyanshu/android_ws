package com.deng.util;

import org.apache.commons.dbutils.QueryRunner;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JDBCUtil {
//	private  JDBCUtil(){}
//	private static  JDBCUtil  jdbcUtil=new JDBCUtil();
//	public  static JDBCUtil getInstance(){
//		return jdbcUtil;
//	}
	private static ComboPooledDataSource  comboPooldatasource;
	static{
		comboPooldatasource=new ComboPooledDataSource();
	}
	
	public  static QueryRunner  getQueryRunner(){
		QueryRunner  queryRunner=new QueryRunner(comboPooldatasource);
		return queryRunner;
	}

}
