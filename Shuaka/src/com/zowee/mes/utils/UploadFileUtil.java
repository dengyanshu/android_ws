package com.zowee.mes.utils;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class UploadFileUtil {
	  
	
	  private  UploadFileUtil(){}
	  static  FTPClient ftp=new FTPClient();;
	 
	  
	    /**  
	    *apache  开源包 FTP
	    *addr=10.2.0.9
	    *21 port  ftp默认
	    *登陆后change 到path
	    */    
	    public static boolean connect(String path,String addr,int port,String username,String password) throws Exception {      
	        boolean result = false;      
	          
	       int reply;      
	       ftp.connect(addr,port);      
	       ftp.login(username,password);      
	       ftp.setFileType(FTPClient.BINARY_FILE_TYPE);      
	       reply = ftp.getReplyCode();      
	       if (!FTPReply.isPositiveCompletion(reply)) {      
	           ftp.disconnect();      
	           return result;      
	        }      
	        ftp.changeWorkingDirectory(path);      
	        result = true;      
	        return result;      
	    }      
	    /**  
	   *   
	
	    */    
	    public static void upload(File file) throws Exception{      
	        if(file.isDirectory()){           
	            ftp.makeDirectory(file.getName());                
	            ftp.changeWorkingDirectory(file.getName());      
	            String[] files = file.list();             
	            for (int i = 0; i < files.length; i++) {      
	                File file1 = new File(file.getPath()+"\\"+files[i] );      
	               if(file1.isDirectory()){      
	                    upload(file1);      
	                    ftp.changeToParentDirectory();      
	               }else{                    
	                   File file2 = new File(file.getPath()+"\\"+files[i]);      
	                   FileInputStream input = new FileInputStream(file2);      
	                   ftp.storeFile(file2.getName(), input);      
	                   input.close();                            
	                }                 
	            }      
	        }else{      
	           File file2 = new File(file.getPath());      
	           FileInputStream input = new FileInputStream(file2);      
	           ftp.storeFile(file2.getName(), input);      
	           input.close();        
	        }      
	    }      



}
