package com.zowee.mes.app;

import java.io.Serializable;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.zowee.mes.exceptions.Myuncatchexceptionhandler;
import com.zowee.mes.laser.LaserScanOperator;
import com.zowee.mes.laser.SerialPortOper.SerialParams;

/**
 * @author Administrator
 * @description �Զ���һ�������Լ���Ӧ�ó��� ��Ҫ����ȫ�����ݵı��� �Լ����������Ļ�ȡ
 */
public class MyApplication extends Application {
	private static MyApplication myApp;
	private static SerialParams serialParams;
	// public static final String ERROR_BARCODESIGN
	// ="04EFBFBD0000EFBFBD2C";//δɨ�赽������ ����ͷ���ص���Ϣ
	private static final String TAG = "App";
	private static LaserScanOperator laserScanOperator;
	public static final int SCAN_KEYCODE = 1;
	private static User mseUser;// ��ǰ����MSEϵͳ���û�
	private static String userPassword = ""; // add by ybj 20130722
	private static String owner = ""; // add by ybj 20130906
	// public static String userName = "";//�û���
	// public static String userId = "";//�û�ID
	// private static final Soap EMPTYSOAP ;//��soap�������ļ�wsconfig.properties�б���ֵ
	// ��ΪMesWebService��ȫ�ֿ���
	
	static {
		// String name = "/dev/ttyMSM2";
		// String owner = "system";
		// int timeOut = 2000;
		// int baudRate = 9600;
		// int dataBits = SerialPort.DATABITS_8;
		// int endBits = SerialPort.STOPBITS_1;
		// int parity = SerialPort.PARITY_NONE;
		// int flowControl = SerialPort.FLOWCONTROL_NONE;
		// serialParams = new SerialParams(name, owner, timeOut, baudRate,
		// dataBits, endBits, parity, flowControl);
		mseUser = new User("", "");//
		// EMPTYSOAP = MesSoapParser.getSoap();
	}

	public static void setAppOwner(String AppOwner) // add by ybj 20130906
	{
		owner = AppOwner;
	}

	public static String getAppOwner() // add by ybj 20130906
	{
		return owner;
	}

	public MyApplication() {
		super();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		myApp = this;
		Myuncatchexceptionhandler crashHandler = Myuncatchexceptionhandler.getInstance();  
		crashHandler.init(getApplicationContext());  

		// try
		// {
		// laserScanOperator = LaserScanOperator.getLaserScanOperator();
		// }
		// catch (Exception e)
		// {
		// // TODO: handle exception
		// }

	}

	public static String getpasswrod() // add by ybj 20130722
	{
		return userPassword;
	}

	public static void setpasswrod(String password) // add by ybj 20130722
	{
		userPassword = password;
	}

	public static MyApplication getApp() {

		return myApp;
	}

	/*
	 * �������������ʱ ����ø÷���
	 */
	@Override
	public void onTerminate() {
		laserScanOperator.closeLaserScanOperator();// �رռ���ɨ���� ��������ռ�õĴ�����Դ
		super.onTerminate();
	}

	/**
	 * @return
	 * @description ���⼤��ɨ�������ýӿ�
	 * */
	public static LaserScanOperator getLaserScanOperator() {
		laserScanOperator = LaserScanOperator.getLaserScanOperator();

		return laserScanOperator;
	}

	// private static void initLaserScanOperator()
	// {
	// Task initLaser = new Task(null,TaskType.INIT_LASER,null);
	// TaskOperator taskOperator = new TaskOperator()
	// {
	// @Override
	// public Task doTask(Task task)
	// {
	// laserScanOperator = LaserScanOperator.getLaserScanOperator();
	// task.setObtainMsg(false);
	// return task;
	// }
	// };
	// initLaser.setTaskOperator(taskOperator);
	// BackgroundService.addTask(initLaser);
	// }
	public static class User implements Serializable {
		private String userId;
		private String userName;

		public User(String userId, String userName) {
			super();
			this.userId = userId;
			this.userName = userName;
		}

		public User() {
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
	 * @description ��õ�ǰ����MES���û�
	 */
	public static User getMseUser() {

		return mseUser;
	}

	/**
	 * @return
	 * @description ���õ�ǰ����MES���û�
	 */
	public static void setMseUser(User user) {

		mseUser = user;
	}

	/**
	 * @return
	 * @description ��õ�ǰ����İ汾��
	 */
	public static int getCurAppVersion() {

		try {
			return myApp.getPackageManager().getPackageInfo(
					myApp.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Log.i(TAG, e.toString());
		}

		return -1;
	}

}
