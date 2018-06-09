package cn.chouchou.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import cn.chouchou.entity.LineInfo;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JDBCUtil {
	private static ComboPooledDataSource  datasorce;
	static{
		datasorce=new ComboPooledDataSource();
	}
	public static QueryRunner  getQueryRunner(){
		return  new QueryRunner(datasorce);
	}
	
	
	public void  insert(LineInfo  line,List<String> list,QueryRunner qr)throws Exception{
	   qr.update("insert into  table values(?,?)", line.get);
	}
	
}
