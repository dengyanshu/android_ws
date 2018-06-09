package cn.yanshu.dbdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqliteOpenHelper extends SQLiteOpenHelper {

	public MySqliteOpenHelper(Context context, String name) {
		super(context, name, null, 1);
		// TODO Auto-generated constructor stub
	}
    /**
     * 当此数据库第一次被创建的时候执行
     * 
     * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        
	}
     /*
      * 当此数据库版本发生变更的时候自动执行
      * */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
