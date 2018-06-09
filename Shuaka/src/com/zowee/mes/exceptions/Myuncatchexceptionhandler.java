package com.zowee.mes.exceptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zowee.mes.activity.MainActivity;
import com.zowee.mes.utils.UploadFileUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class Myuncatchexceptionhandler implements  UncaughtExceptionHandler {

	
	private  Myuncatchexceptionhandler(){}
	private static Myuncatchexceptionhandler INSTANCE = new Myuncatchexceptionhandler(); 
	 public static Myuncatchexceptionhandler getInstance() {  
	       return INSTANCE;  
	}  
	
	 public static final String TAG = "Myuncatchexceptionhandler"; 
	 private Thread.UncaughtExceptionHandler mDefaultHandler;  //ϵͳĬ�ϵ�UncaughtException������   
	 private Context mContext;  
	 private Map<String, String> infos = new HashMap<String, String>();   //�����洢�豸��Ϣ���쳣��Ϣ   
	 private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			 //new SimpleDateFormat();  
	 
	    /** 
	    * ��UncaughtException����ʱ��ת��ú��������� 
	    */  
	    @Override  
	   public void uncaughtException(Thread thread, Throwable ex) {  
	       if (!handleException(ex) && mDefaultHandler != null) {  
	          //����û�û�д�������ϵͳĬ�ϵ��쳣������������   
	          mDefaultHandler.uncaughtException(thread, ex);  
	      } else {  
	          try {  
	               Thread.sleep(3000);  
	          } catch (InterruptedException e) {  
	               Log.e(TAG, "error : ", e);  
	          }  
	          //�˳�����   
	         android.os.Process.killProcess(android.os.Process.myPid());  
	         System.exit(1);  
	      }  
	 }  
	 
	  public void init(Context context) {  
		       mContext = context;  
		     //��ȡϵͳĬ�ϵ�UncaughtException������   
		       mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
		      //���ø�CrashHandlerΪ�����Ĭ�ϴ�����   
		       Thread.setDefaultUncaughtExceptionHandler(this);  
	 }    
	    
	   /** 
	    * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����. 
	    *  
	    * @param ex 
	    * @return true:��������˸��쳣��Ϣ;���򷵻�false. 
	    */  
	   private boolean handleException(Throwable ex) {  
	       if (ex == null) {  
	           return false;  
	       }  
	       //ʹ��Toast����ʾ�쳣��Ϣ   
	      new Thread() {  
	            @Override  
	           public void run() {  
	              Looper.prepare();  
	              Toast.makeText(mContext, "�ܱ�Ǹ,��������쳣,�����˳�.", Toast.LENGTH_LONG).show();  
	              Looper.loop();  
	          }  
	        }.start();  
	       //�ռ��豸������Ϣ    
	       collectDeviceInfo(mContext);  
	       //������־�ļ�    
	       final String filename=saveCrashInfo2File(ex);  
	       Log.i(TAG,"filename="+filename);
	       //ftp�ϴ�
	       if(filename==null||"".equals(filename)){
	    	   return  true;
	       }
	       new Thread(){
	    	   public void run() {
	    		   upload2ftp(filename);
	    	   };
	       }.start();
	       
	       return true;  
	   }  
	    
	   /** 
	   * �ռ��豸������Ϣ 
	   * @param ctx 
	    */  
	   public void collectDeviceInfo(Context ctx) {  
	       try {  
	         PackageManager pm = ctx.getPackageManager();  
	           PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
	          if (pi != null) {  
	              String versionName = pi.versionName == null ? "null" : pi.versionName;  
	              String versionCode = pi.versionCode + "";  
	              infos.put("versionName", versionName);  
	              infos.put("versionCode", versionCode);  
	          }  
	      } catch (NameNotFoundException e) {  
	           Log.e(TAG, "an error occured when collect package info", e);  
	      }  
	       Field[] fields = Build.class.getDeclaredFields();  
	       for (Field field : fields) {  
	          try {  
	              field.setAccessible(true);  
	              infos.put(field.getName(), field.get(null).toString());  
	              Log.d(TAG, field.getName() + " : " + field.get(null));  
	           } catch (Exception e) {  
	             Log.e(TAG, "an error occured when collect crash info", e);  
	          }  
	       }  
	   }  

	   /** 
	    * ���������Ϣ���ļ��� 
	   *  
	    * @param ex 
	     * @return  �����ļ�����,���ڽ��ļ����͵������� 
	    */  
	   private String saveCrashInfo2File(Throwable ex) {  
	         
	       StringBuffer sb = new StringBuffer();  
	       for (Map.Entry<String, String> entry : infos.entrySet()) {  
	           String key = entry.getKey();  
	           String value = entry.getValue();  
	           sb.append(key + "=" + value + "\n");  
	       }  
	        
	       Writer writer = new StringWriter();  
	       PrintWriter printWriter = new PrintWriter(writer);  
	       ex.printStackTrace(printWriter);  
	       Throwable cause = ex.getCause();  
	      while (cause != null) {  
	           cause.printStackTrace(printWriter);  
	           cause = cause.getCause();  
	        }  
	      printWriter.close();  
	       String result = writer.toString();  
	       sb.append(result);  
	      try {  
	           String time = formatter.format(new Date());  
	           String fileName = "crash-" + time + ".log";  
	           if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
	             // String path = "mnt/sdcard/crash/";  
	               File dir = new File(Environment.getExternalStorageDirectory(),"shuaka");  
	                if (!dir.exists()) {  
	                   dir.mkdirs();  
	              }  
	              FileOutputStream fos = new FileOutputStream(dir +File.separator+ fileName);  
	              fos.write(sb.toString().getBytes());  
	              fos.close();  
	           }  
	           return fileName;  
	       } catch (Exception e) {  
	           Log.e(TAG, "an error occured while writing file...", e);  
	       }  
	        return null;  
	    }  
      
	   
	   
	   private  void upload2ftp(String filename){
		    try {
				UploadFileUtil.connect("shuaka", "10.2.0.9", 21, "mes", "mesmes");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				UploadFileUtil.upload(new  File(Environment.getExternalStorageDirectory(),"shuaka"+File.separator+filename));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
	   }
}







   
 
  
 
  
