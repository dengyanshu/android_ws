package com.deng.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FileUtil {
    /**
     * ¸´Ï°×Ö½ÚÁ÷
     * */
	public static  void  copyFile(File f,File tof) {
		 FileInputStream  fis=null;
		 FileOutputStream   fos=null;
		  try{
			  fis=new FileInputStream(f);
			  fos=new FileOutputStream(tof);
			  int len=0;
			  byte[] buff=new  byte[1024];
			  while((len=fis.read(buff))!=-1){
				  fos.write(buff, 0, len);
				  fos.flush();
			  }
		  }
		  catch(Exception  e){
			  throw new  RuntimeException(e);
			  
		  }
		  finally{
			  if(fis!=null){
				  try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
			  
			  if(fos!=null){
				  try {
					  fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
			  
		  }
	}
	
	@Test
	public  void  testmyfunc(){
		copyFile(new File("d://oppo_log/001533106911023600000043.txt"),new File("d://oppo_log_bak/001533106911023600000043.txt"));
	}
	
	
	public  static  Map<String,String> toOPPOMap(File file){
		BufferedReader  br=null;
		HashMap<String, String>  map=new HashMap<String, String>();
		try{
			br=new BufferedReader(new FileReader(file));
			String line=null;
			int count=0;
			map.put(count+"",file.getName().replace(".txt", ""));
			count++;
		    while((line=br.readLine())!=null){
		    	map.put(count+"",line);
		    	count++;
		    }
		    return  map;
			
		}
		catch(Exception  e){
			throw  new RuntimeException(e);
		}
		finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
