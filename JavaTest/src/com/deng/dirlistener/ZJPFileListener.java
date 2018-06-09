package com.deng.dirlistener;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
	public class ZJPFileListener implements FileAlterationListener{  
		  
	    private String dirname=null;
	    ZJPFileMonitor monitor = null;  
	    private  String  filename=null;
	    @Override  
	    public void onStart(FileAlterationObserver observer) {  
	        //System.out.println("onStart");  
	    }  
	    @Override  
	    public void onDirectoryCreate(File directory) {  
	        System.out.println("onDirectoryCreate:" +  directory.getName());  
	    }  
	  
	    @Override  
	    public void onDirectoryChange(File directory) { 
	    	dirname=directory.getName();
	        System.out.println("onDirectoryChange:" + directory.getName());  
	        
	    }  
	  
	    @Override  
	    public void onDirectoryDelete(File directory) {  
	        System.out.println("onDirectoryDelete:" + directory.getName());  
	    }  
	  
	    @Override  
	    public void onFileCreate(File file) {  
	        System.out.println("onFileCreate:" + file.getName());  
	        filename="d:/aoi/"+dirname+"/"+file.getName();
	        //1获取文件路径
	        //2、目前模式上传FTP 后续可以考虑写入数据库
	        System.out.println(filename);
	        upload2ftp(filename);
	        //写数据库
	    }  
	  
	    @Override  
	    public void onFileChange(File file) {  
	        System.out.println("onFileCreate : " + file.getName());  
	    }  
	  
	    @Override  
	    public void onFileDelete(File file) {  
	        System.out.println("onFileDelete :" + file.getName());  
	    }  
	  
	    @Override  
	    public void onStop(FileAlterationObserver observer) {  
	        //System.out.println("onStop");  
	    }  
	    
	    
	    private  void upload2ftp(String filename){
		    try {
				UploadFileUtil.connect("shuaka", "10.2.0.9", 21, "mes", "mesmes");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				UploadFileUtil.upload(new File(filename));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
	   }
	  
}
