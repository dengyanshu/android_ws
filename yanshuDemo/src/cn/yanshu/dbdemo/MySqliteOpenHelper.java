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
     * �������ݿ��һ�α�������ʱ��ִ��
     * 
     * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        
	}
     /*
      * �������ݿ�汾���������ʱ���Զ�ִ��
      * */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
