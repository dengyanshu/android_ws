package cn.yanshu.dbdemo;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import cn.yanshu.R;

public class DBDemo extends Activity {
	
	private SQLiteDatabase  sqlitedatabase;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.dbdemo_activity);
    	
    	
    	sqlitedatabase=new MySqliteOpenHelper(this, "test").getWritableDatabase();
//    	String sql="CREATE  TABLE  sms(_id INT  PRIMARY KEY ,people NVARCHAR(50),content NVARCHAR(1000))";
//    	sqlitedatabase.execSQL(sql);
    }
    
    
    
    //�����Ϣ
    public void click(View view){
    	ContentValues  contentValues=new ContentValues();
    	
    	contentValues.put("people", "dengfeng");
    	contentValues.put("content", "today so happy!");
    	sqlitedatabase.insert("sms", "_id", contentValues);
    }
    
    
    //ɾ��
    public void click2(View view){
    	sqlitedatabase.delete("sms", "people=?", new String[]{"xiaozhang"});
    }
    
    
    //�޸�
    public void click3(View view){
    	ContentValues  contentValues=new ContentValues();
    	contentValues.put("content", "today so  sad!");
    	sqlitedatabase.update("sms", contentValues, "people=?", new String[]{"dengfeng"});
    }
    
    //��ѯ
    public void click4(View view){
    	SQLiteDatabase  db=getDataBase();
    	Cursor cursor=db.query("sms", new String[]{"people","content"}, 
    			null, null, 
    			null, null, null);
    	while (cursor.moveToNext()) {
    		System.out.println(cursor.getString(1));
		}
    }
    /*
     *  String sql="CREATE  TABLE  sms(_id INT  PRIMARY KEY ,people NVARCHAR(50),content NVARCHAR(1000))";
    	  writableDatabase.execSQL(sql);
     *   �̳���sqliteopenhelper  ������2������ һ���ǵ����ݿⴴ����ʱ�� ִ�� һ�������ݿ�汾�б��ʱִ��
     *   ���԰Ѵ˾���ڴ�����ʱ�򴴽���
     * */
    private SQLiteDatabase  getDataBase(){
    	MySqliteOpenHelper  mySqliteOpenHelper=new MySqliteOpenHelper(this,"test");
    	return  mySqliteOpenHelper.getWritableDatabase();
    }
    
}
