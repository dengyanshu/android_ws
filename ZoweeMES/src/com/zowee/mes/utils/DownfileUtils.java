package com.zowee.mes.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class DownfileUtils {
	//仅用于下载asset下的文件 内存读取流
	public  static  final  String  VERSION_UPDATE_INFO="version_update_info.txt";
	
	public  static  String   download_verison_info(Context context,String filename){
	    String s="";
		InputStream  is=null;
		ByteArrayOutputStream  baos=null;
		try {
			is=	context.getAssets().open(filename);
			baos=new ByteArrayOutputStream();
			byte[]  buffer=new byte[1024];
			int  len=0;
			while((len=is.read(buffer))!=-1){
				baos.write(buffer, 0, len);
			}
			
			s=baos.toString("GBK");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(baos!=null){
				try {
					baos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return s;
		
	}

}
