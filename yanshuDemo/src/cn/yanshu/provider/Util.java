package cn.yanshu.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

/*
 * use only for this package
 * 
 * */

public class Util {
	

	/**
	 * 将短信list集合写到sd卡xml
	 * */
	
	private  static  Map<String, String> mimetype=new HashMap<String, String>();
	static{
		//暂时只封装这么多  如有需要可以把联系人其他类型封装 
		//在android 嵌入式表中是integer类型但是嵌入式都是以string 存储
		mimetype.put("1", "email");//vnd.android.cursor.item/email_v2
		mimetype.put("5", "phone");//vnd.android.cursor.item/phone_v2
		mimetype.put("7", "name");//vnd.android.cursor.item/name
		mimetype.put("8", "address");//vnd.android.cursor.item/postal-address_v2
	}
	
	
	public static void writeToxml(Context context,List<Sms> smss)throws Exception{
		 XmlSerializer newSerializer = Xml.newSerializer();
		 newSerializer.setOutput(new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"smss_back.xml"), true), "utf-8");
		 newSerializer.startDocument("utf-8", true);
		 newSerializer.startTag(null, "smss");
		 for(Sms sms:smss){
			 newSerializer.startTag(null, "sms");
			 
			 newSerializer.startTag(null, "people");
			 newSerializer.text(sms.getAddress());
			 newSerializer.endTag(null, "people");
			 
			 newSerializer.startTag(null, "content");
			 newSerializer.text(sms.getContent());
			 newSerializer.endTag(null, "content");
			 
			 newSerializer.startTag(null, "time");
			 newSerializer.text(sms.getSendTime());
			 newSerializer.endTag(null, "time");
			 
			 newSerializer.endTag(null, "sms");
		 }
		 newSerializer.endTag(null, "smss");
		 newSerializer.endDocument();
	}
	
	//获取联系人主键集合
	//content://com.android.contacts/contacts 
	//其他开发者 一般以row_contact表为主
	private  static  List<String> getContactCount(ContentResolver  contentResolver){
	    //此uri路径必须查android文档才可以
		List<String>  contactids = null;
		Uri uri=Uri.parse("content://com.android.contacts/contacts");
		Cursor cursor=contentResolver.query(uri, new  String[]{"_id"},null,null,null);
		if(cursor!=null&&cursor.getCount()>0){
			contactids=new ArrayList<String>();
			while(cursor.moveToNext()){
				contactids.add(cursor.getString(0));
			}
		}
		return   contactids;
	}
	
	//content://com.android.contacts/data 
	//mimetype_id 不能用 因为实际此uri 匹配的是view
	//row_contact 表 如果 contact_id==null 代表此联系人已删除 但是data表数据不会删除
	public static  List<Contact>  getContact(ContentResolver  contentResolver){
		List<Contact>  contacts=null;
		List<String> contactids=getContactCount(contentResolver);
		contacts=new ArrayList<Contact>();
		for(String contactid:contactids){
			Uri uri=Uri.parse("content://com.android.contacts/data");
			Cursor cursor=contentResolver.query(uri, new String[]{"mimetype","data1"}, 
					"raw_contact_id=?", new String[]{contactid}, null);
			if(cursor!=null&&cursor.getCount()>0){
				Contact contact=new Contact();
				while(cursor.moveToNext()){
					String res_mimetype= cursor.getString(0);
					if(res_mimetype.equals("vnd.android.cursor.item/email_v2")){
						contact.setEmail(cursor.getString(1));
					}
					else if(res_mimetype.equals("vnd.android.cursor.item/phone_v2")){
						contact.setPhone(cursor.getString(1));
					}
					else if(res_mimetype.equals("vnd.android.cursor.item/name")){
						contact.setName(cursor.getString(1));
					}
					else if(res_mimetype.equals("vnd.android.cursor.item/postal-address_v2")){
						contact.setAddress(cursor.getString(1));
					}
				}
				contacts.add(contact);
			}
			
		}
		return contacts;
	}
	
	
	/**
	 * 
	 * 写xml
	 * */
	
	public  static void  writeXmlContact(List<Contact> contacts)throws  Exception{
		XmlSerializer newSerializer = Xml.newSerializer();
	    newSerializer.setOutput(
					new FileOutputStream(new File
							(Environment.getExternalStorageDirectory(),UUID.randomUUID().toString()+"contact.xml")), 
					"utf-8");
		
		
		newSerializer.startDocument("utf-8", true);
		newSerializer.startTag(null, "contacts");
		for(Contact c:contacts){
			newSerializer.startTag(null, "contact");
			
			newSerializer.startTag(null, "name");
			newSerializer.text(c.getName());
			newSerializer.endTag(null, "name");
			
			newSerializer.startTag(null, "phone");
			newSerializer.text(c.getPhone());
			newSerializer.endTag(null, "phone");
			
			newSerializer.startTag(null, "address");
			newSerializer.text(c.getAddress());
			newSerializer.endTag(null, "address");
			
			newSerializer.startTag(null, "email");
			newSerializer.text(c.getEmail());
			newSerializer.endTag(null, "email");
			
			newSerializer.endTag(null, "contact");
		}
		newSerializer.endTag(null, "contacts");
		newSerializer.endDocument();
		newSerializer.flush();
	}
	
	
	/**
	 * 把联系人插入到后台数据库
	 * 
	 * */
    public  static void  insertContact(Contact contact,ContentResolver  contentResolver){
    	Uri row_contact_uri=Uri.parse("content://com.android.contacts/raw_contacts");
    	Uri data_uri=Uri.parse("content://com.android.contacts/data");
    	
    	//获取row_contact表计数
    	Cursor cursor = contentResolver.query(row_contact_uri, null, null, null, null);
    	String contactid=cursor.getCount()+1+"";
    	
    	//插入row_contact we need info
    	ContentValues  contentValues=new ContentValues();
    	contentValues.put("contact_id", contactid);
    	contentResolver.insert(row_contact_uri, contentValues);
    	
    	//插入到data 表
    	ContentValues  contentValues2=new ContentValues();
    	String name=contact.getName();
    	contentValues2.put("mimetype", "vnd.android.cursor.item/name");
    	contentValues2.put("data1", name);
    	contentValues2.put("raw_contact_id", contactid);
    	contentResolver.insert(data_uri, contentValues2);
    	
    	contentValues2=null;
    	contentValues2=new ContentValues();
    	String phone=contact.getPhone();
    	contentValues2.put("mimetype", "vnd.android.cursor.item/phone_v2");
    	contentValues2.put("data1", phone);
    	contentValues2.put("raw_contact_id", contactid);
    	contentResolver.insert(data_uri, contentValues2);
    	
    }
	
	
}
