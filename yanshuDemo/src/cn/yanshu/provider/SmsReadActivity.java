package cn.yanshu.provider;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import cn.yanshu.R;

public class SmsReadActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smsread_activity);
	}
   
	/*
	 * 通过短信  系统自带的smsProvider暴露的方式 读取短信 并且写到SD卡上
	 * 
	 * **/
	
	public  void  click(View view){
		//查阅api文档sms  
		/***
		UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
		{
			sUriMatcher.addURI("sms", null, code)
		}*/
		Uri uri=Uri.parse("content://sms");
		Cursor  cursor=getContentResolver().query(uri, 
				new String[]{"address","date_sent","body"}, 
				null, null, 
				null);
		Sms sms;
		if(cursor!=null&&cursor.getCount()>0){
			  List<Sms>  smss=new ArrayList<Sms>();
			  while(cursor.moveToNext()){
				  sms=new Sms();
				  sms.setAddress(cursor.getString(0));
				  sms.setSendTime(cursor.getString(1));
				  sms.setContent(cursor.getString(2));
				  smss.add(sms);
			  }
			  try {
				Util.writeToxml(this, smss);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 *虚拟插入一条sms
	 */
	public void click2(View view){
		Uri uri=Uri.parse("content://sms");
		ContentValues  contentValues=new ContentValues();
		contentValues.put("date_sent", "1505119884000");
		contentValues.put("address", "119");
		contentValues.put("body", "火警提醒!!");
		
		System.out.println(getContentResolver().insert(uri, contentValues));
	}
	
	/**
	 * 
	 * 点击把联系人全部取出来 写到sd卡 以xml保存
	 * */
	public  void  click3(View  view){
		//取contact表的数据 需要读取contact_id 表 知道有几个联系人 然后根据联系人个数  逐行遍历存储
		ContentResolver contentResolver = getContentResolver();
		List<Contact> contacts = Util.getContact(contentResolver);
         //开始写sdcard  以xml格式存储
		try {
			Util.writeXmlContact(contacts);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 插入联系人到系统
	 * 操作2个实体表
	 * **/
	
	public void  click4(View  view){
		Contact  contact=new Contact();
		contact.setName("chouchou");
		contact.setPhone("15827920033");
		
		Util.insertContact(contact, getContentResolver());
		
	}
}
