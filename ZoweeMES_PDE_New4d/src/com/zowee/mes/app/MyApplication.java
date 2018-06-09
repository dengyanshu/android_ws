package com.zowee.mes.app;

import java.io.Serializable;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;

import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.laser.SerialPortOper.SerialParams;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.ws.MesSoapParser;
import com.zowee.mes.ws.MesWebService;
import com.zowee.mes.ws.WebService.Soap;
/**
 * @author Administrator
 * @description 自定义一个属于自己的应用程序 主要用于全局数据的保存 以及程序上下文获取 
 */
public class MyApplication extends Application 
{
	private static MyApplication myApp;
	private static SerialParams serialParams;
	//public static final String ERROR_BARCODESIGN ="04EFBFBD0000EFBFBD2C";//未扫描到条形码 激光头返回的信息
	private static final String TAG = "App";
	private static  LaserScanOperator laserScanOperator;
	public static final int SCAN_KEYCODE = 1;	
	//public static final int SCAN_KEYCODE1 = 1;	
	private static User mseUser;//当前登入MSE系统的用户
	private static  String userPassword="";  // add by ybj 20130722
	private static  String owner ; // add by ybj 20130906
	private static String MOID; // add by ybj 20130918
	private static String DeviceTypeId;
//	public static  String userName = "";//用户名
//	public static String userId = "";//用户ID
	//private static final Soap EMPTYSOAP ;//该soap从配置文件wsconfig.properties中被赋值 作为MesWebService的全局拷贝
//	
	static
	{
//		String name = "/dev/ttyMSM2";
//		String owner = "system";
//		int timeOut = 2000;
//		int baudRate = 9600;
//		int dataBits = SerialPort.DATABITS_8;
//		int endBits = SerialPort.STOPBITS_1;
//		int parity = SerialPort.PARITY_NONE;
//		int flowControl = SerialPort.FLOWCONTROL_NONE;
//		serialParams = new SerialParams(name, owner, timeOut, baudRate, dataBits, endBits, parity, flowControl);
		mseUser = new User("", "");//
	
		owner = "";

	//	EMPTYSOAP = MesSoapParser.getSoap();
	}
	public static void setAppOwner(String AppOwner)  // add by ybj 20130906
	{
		owner = AppOwner;
	}
	public static String getAppOwner()  // add by ybj 20130906
	{
		return owner;
	}	
	public static void setAppMoid(String AppMoid)  // add by ybj 20130918
	{
		MOID = AppMoid;
	}
	public static void setAppDeviceTypeId(String deviceTypeId)  // add by ybj 20130918
	{
		DeviceTypeId = deviceTypeId;
	}
	public static String getAppDeviceTypeId()  // add by ybj 20130918
	{
		return DeviceTypeId;
	}
	public static String getAppMoid()  // add by ybj 20130918
	{
		return MOID;
	}
	public MyApplication()
	{
		super();
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		myApp = this;
//		try
//		{
//			laserScanOperator = LaserScanOperator.getLaserScanOperator();
//		} 
//		catch (Exception e)
//		{
//			// TODO: handle exception
//		}
		
		
	}
	public static String getpasswrod() // add by ybj 20130722
	{
		return userPassword;
	}	
	public static void  setpasswrod(String password) // add by ybj 20130722
	{
		userPassword = password;
	}		
	public static MyApplication getApp()
	{
		
		return myApp;
	}

	/* 
	 *当程序结束销毁时 会调用该方法 
	 */
	@Override
	public void onTerminate() 
	{
		laserScanOperator.closeLaserScanOperator();//关闭激光扫描器 并销毁其占用的串口资源		
		super.onTerminate();	
	}
	
	/**
	 * @return
	 * @description 对外激光扫描器调用接口 
	 * 	 */
	public static LaserScanOperator getLaserScanOperator()
	{
		laserScanOperator = LaserScanOperator.getLaserScanOperator();
		
		return laserScanOperator;
	}
	
//	private static void initLaserScanOperator()
//	{
//		Task initLaser = new Task(null,TaskType.INIT_LASER,null);
//		TaskOperator taskOperator = new TaskOperator() 
//		{
//			@Override
//			public Task doTask(Task task) 
//			{
//				laserScanOperator = LaserScanOperator.getLaserScanOperator();
//				task.setObtainMsg(false);
//				return task;
//			}
//		};
//		initLaser.setTaskOperator(taskOperator);
//		BackgroundService.addTask(initLaser);		
//	} 
	public static class User implements Serializable 
	{
		private String userId;
		private String userName;
		
		public User(String userId, String userName)
		{
			super();
			this.userId = userId;
			this.userName = userName;
		}

		public User() 
		{
			super();
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
		
	}
	
	/**
	 * @return
	 * @description 获得当前登入MES的用户 
	 */
	public static User getMseUser()
	{
		
		return mseUser;
	}
	
	/**
	 * @return
	 * @description 设置当前登入MES的用户 
	 */
	public static void setMseUser(User user)
	{
		
		mseUser = user;
	}
	
	/**
	 * @return
	 * @description 获得当前程序的版本号
	 */
	public static int getCurAppVersion()
	{
		
		try 
		{
			return myApp.getPackageManager().getPackageInfo(myApp.getPackageName(), 0).versionCode;
		} 
		catch (NameNotFoundException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i(TAG, e.toString());
		}
		
		return -1;
	}
	
	
}
