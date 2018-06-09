package cn.yanshu.provider;

import cn.yanshu.dbdemo.MySqliteOpenHelper;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 *  /data/data/cn.yanshu/database/test
 *  test名字的数据库下 有个sms表
 *  
 *  1
 * 
 * */

public class SmsProvider extends ContentProvider {
	
	
	private  SQLiteOpenHelper sqLiteOpenHelper;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int INSERTSUCCESS = 1;
	private static final int DELETESUCCESS = 1;
	private static final int UPDATESUCCESS = 1;
	private static final int SELECTSUCCESS = 1;

    static
    {
        sURIMatcher.addURI("cn.yanshu", "sms/insert", INSERTSUCCESS);
        sURIMatcher.addURI("cn.yanshu", "sms/delete", DELETESUCCESS);
        sURIMatcher.addURI("cn.yanshu", "sms/update", UPDATESUCCESS);
        sURIMatcher.addURI("cn.yanshu", "sms/select", SELECTSUCCESS);
      
    }
	
	/**
	 * smsProvider初始化的时候 获取一个可写的database实例
	 * 
	 * getContext()  获取上下文对象  在provider中
	 * */
    
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		sqLiteOpenHelper=new MySqliteOpenHelper(getContext(), "test");
		return false;
	}
    
	
	
	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		if(sURIMatcher.match(arg0)==INSERTSUCCESS){
			long  count=sqLiteOpenHelper.getWritableDatabase().insert("sms", null, arg1);
			Uri  return_uri=Uri.parse("插入到"+count+"条");
			return return_uri;
		}
		else{
			throw  new RuntimeException("路径匹配失败 contentProvider拒绝访问");
		}
	
	}

	
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
