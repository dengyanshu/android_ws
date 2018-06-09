package cn.chouchou;

import org.apache.commons.dbutils.QueryRunner;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JDBCUtil {
	
	private  static  QueryRunner  qr;
	static{
		qr=new QueryRunner(new ComboPooledDataSource());
	}
	
	public static QueryRunner  getQr(){
		//¥”≈‰÷√ƒ¨»œ∂¡»°
		
		return qr;
	}

}
