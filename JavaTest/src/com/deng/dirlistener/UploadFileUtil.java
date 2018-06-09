package com.deng.dirlistener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class UploadFileUtil {
	  
	
	  private  UploadFileUtil(){}
	  static  FTPClient ftp=new FTPClient();;
	    /**  
	    *apache  ��Դ�� FTP
	    *addr=10.2.0.9
	    *21 port  ftpĬ��
	    *��½��change ��path
	    */    
	    public static boolean connect(String path,String addr,int port,String username,String password) throws Exception {      
	        boolean result = false;      
	          
	       int reply;      
	       ftp.connect(addr,port);      
	       ftp.login(username,password);      
	       ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
	       ftp.enterLocalPassiveMode();
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
	    public static void upload(File file)throws Exception {      
	            
	           File file2 = new File(file.getPath());      
	           FileInputStream input=new FileInputStream(file2);		
			   ftp.storeFile(file2.getName(), input);
			   System.out.println("exec...");
	           input.close();
			   ftp.logout();
	           System.out.println("�ϴ���ϣ��ͷ���Դ");
	           if (ftp.isConnected()) {      
	             try {      
	                  ftp.disconnect();      
	              } catch (IOException ioe) {      
                      System.out.println("�ر����ӷ����쳣��");
	              }            
               }      
	    }      



}
