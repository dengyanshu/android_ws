package com.deng.dirlistener;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.deng.util.FileUtil;
import com.deng.util.JDBCUtil;
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
	        String  filename=file.getName();
	        //1、复制文件到d:/oppo_log_bak
	        FileUtil.copyFile(file, new File("d:/oppo_log_bak/"+filename));
	        //2、获取原文件的数据MAP集合
	        Map<String, String> map=FileUtil.toOPPOMap(file);
	        String xiaobanString="";
	        for(int x=5;x<=map.size()-1;x++){
	        	xiaobanString=xiaobanString+map.get(x+"")+"|";
	        }
	        //3、JDBC写入10.2.0.25 orbitXM
	        String sql="INSERT  INTO  log_oppo ( laxian,wosn_oppo,link_time,link_user,bigboard,smallboard) VALUES(?,?,?,?,?,?)";
			Object[] vaules={map.get("1"),map.get("2"),map.get("3"),map.get("4"),map.get("0"),xiaobanString};
			try {
				JDBCUtil.getQueryRunner().update(sql,vaules);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//4、删除原文件
			file.delete();
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
	    
	    
//	    private  void upload2ftp(String filename){
//		    try {
//				UploadFileUtil.connect("shuaka", "10.2.0.9", 21, "mes", "mesmes");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			try {
//				UploadFileUtil.upload(new File(filename));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		   
//	   }
	  
}
