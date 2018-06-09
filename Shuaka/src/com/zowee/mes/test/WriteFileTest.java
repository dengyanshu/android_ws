package com.zowee.mes.test;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.zowee.mes.utils.UploadFileUtil;

public class WriteFileTest extends AndroidTestCase {
	
	
	
	
	private static final String TAG = "WriteFileTest";
 //URL上传很复杂 失败
	public  void  test_write() throws Exception{
		 String info="cause  by nullpoint  at  oncreate";
		 
		 String path="http://10.2.0.9:8080/updateNotes.txt";
		 URL url=new URL(path);
		 HttpURLConnection  conn=(HttpURLConnection) url.openConnection();
		 conn.setDoOutput(true);
		 conn.setRequestMethod("POST");
		 conn.setRequestProperty("Content-Type", "text/xml");

		 OutputStream ous= conn.getOutputStream();
		 ous.write(info.getBytes());
		 ous.flush();
		 ous.close();
		 
	}
	//可行
	public  void  test_read () throws Exception{
		String path="http://10.2.0.9:8080/updateNotes.txt";
		URL url=new URL(path);
		HttpURLConnection  conn=(HttpURLConnection) url.openConnection();
		InputStream is= conn.getInputStream();
		byte[] buffer=new byte[1024];
		int len=is.read(buffer);
		
		Log.i(TAG,"info="+new String(buffer,0,len,"gbk"));
	}
	
	//可行
	public  void  test_ftpfileupload () throws Exception {
		UploadFileUtil.connect("shuaka", "10.2.0.9", 21, "mes", "mesmes");
		UploadFileUtil.upload(new  File(Environment.getExternalStorageDirectory(),"shuaka/crash-2015-08-28-09-39-11.log"));
	}
	
}
