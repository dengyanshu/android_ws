package com.zowee.mes.laser;

import gnu.io.CommPortOwnershipListener;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.zowee.mes.R;
import com.zowee.mes.activity.StorageScanActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.exceptions.SerialPortInitException;
import com.zowee.mes.laser.SerialPortOper.SerialParams;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.HexStrConver;
import com.zowee.mes.utils.MesUtils;
import com.zowee.mes.utils.StringUtils;

/**
 * @author Administrator
 * @description 激光扫描执行器
 */
public class LaserScanOperator implements SerialPortEventListener {

	private static SerialPortOper.SerialParams serialParams;
	private static LaserScanOperator laserScanOperator;
	private static SerialPortOper serialPortOper;
	private final static String TAG = "LaserScanOperator";
	private static final String ERROR_BARCODESIGN = "04BFBD0000EFBFBD2C";// 未扫描到条形码
																			// 激光头返回的信息
	public static final byte[] ERROR_BARCODEBYTE = HexStrConver
			.HexString2Bytes(ERROR_BARCODESIGN);// 未扫描到条形码 激光头返回的信息的字节数组
	private static Activity activity;
	private static int taskType;
	private static boolean laserLisState = false;
	private static boolean laserEnable = true;// 是否启用激光扫描的功能 默认开启状态

	Handler mHandler = new Handler();

	static {
		// String name = "/dev/ttyMSM2";
		// String owner = "system";
		// int timeOut = 2000;
		// int baudRate = 9600;
		// int dataBits = SerialPort.DATABITS_8;
		// int endBits = SerialPort.STOPBITS_1;
		// int parity = SerialPort.PARITY_NONE;
		// int flowControl = SerialPort.FLOWCONTROL_NONE;
		// serialParams =
		// LaserConfigParser.getFinalSerialParams();//获得从配置文件中解析过来的激光串口参数
		serialParams = LaserConfigParser.getFinalSerialParams();
		laserEnable = LaserConfigParser.getLaserFunctionEnable();
	}

	public static void setSerialParams(SerialParams finalSerialParams) {
		closeLaserScanOperator();
		serialParams = finalSerialParams;

		// laserScanOperator = null;
	}

	private LaserScanOperator() {
		if (!getLaserEnable())
			return;// 如果激光扫描的功能被停用的话 那么该语句将会被执行
		createLaser();
	}

	/**
	 * @return
	 * @description 单例模式获取一个激光扫描器
	 */
	public static LaserScanOperator getLaserScanOperator() {

		if (!getCurLaserScanState())
			laserScanOperator = new LaserScanOperator();// 修改

		return laserScanOperator;
	}

	// /**
	// *
	// * @description 开启一条线程 执行serialPortOper初始化
	// */
	// @Override
	// public void run()
	// {
	// try
	// {
	// serialPortOper = new SerialPortOper(serialParams);
	// serialPortOper.getSerialPort().addEventListener(this);
	// serialPortOper.getSerialPort().notifyOnDataAvailable(true);
	// laserLisState = true;
	// Log.i(TAG,"start laser scan");
	// }
	// catch (Exception e)
	// {
	// laserLisState = false;
	// Log.i(TAG, e.toString());
	// Log.i(TAG,
	// serialParams+""+serialParams.name+" "+serialParams.baudRate+" "+serialParams.owner+" "+serialParams.dataBits+" "+serialParams.endBits+" "+serialParams.flowControl+" "+serialParams.parity);
	// laserInitFailNotify();//通知程序 错误消息
	// }
	//
	// }

	/*
	 * 修改方法 只应用于测试阶段
	 */
	public void createLaser() {
		try {
			serialPortOper = new SerialPortOper(serialParams);
			serialPortOper.getSerialPort().addEventListener(this);
			serialPortOper.getSerialPort().notifyOnDataAvailable(true);
			laserLisState = true;
			Log.i(TAG, "start laser scan");
		} catch (Exception e) {
			laserLisState = false;
			Log.i(TAG, e.toString());
			Log.i(TAG, serialParams + "" + serialParams.name + " "
					+ serialParams.baudRate + " " + serialParams.owner + " "
					+ serialParams.dataBits + " " + serialParams.endBits + " "
					+ serialParams.flowControl + " " + serialParams.parity);
			laserInitFailNotify();
		}

	}

	/**
	 * 
	 * @description 获取串口对象
	 */
	public SerialPort getSerialPort() {
		if (null != serialPortOper)
			return serialPortOper.getSerialPort();

		return null;
	}

	/**
	 * 
	 * @description 读取激光头扫描到的数据
	 */
	public void readBarCode(Activity activity, int taskType) {
		Task readBarTask = new Task(activity, taskType, null);
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				String val = serialPortOper.readInStream();
				if (!StringUtils.isEmpty(val)) {
					try {
						boolean isContainErrorCode = HexStrConver
								.isContainByte(ERROR_BARCODEBYTE,
										val.getBytes("UTF-8"));
						if (isContainErrorCode) {
							task.setObtainMsg(false);
						} else {
							task.setTaskResult(val);
							SoundEffectPlayService
									.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
						}

					} catch (Exception e) {
						Log.i(TAG, e.toString());
					}
				} else {
					task.setObtainMsg(false);
				}

				return task;
			}
		};

		readBarTask.setTaskOperator(taskOperator);
		BackgroundService.addTask(readBarTask);

	}

	/**
	 * @description 开启一个激光扫描任务
	 */
	public void startLaserScan(Activity activity) {
		if (null != serialPortOper && !serialPortOper.getSerialState()) {
			Task scanTask = new Task(activity, TaskType.SCAN_BAR, null);
			TaskOperator taskOperator = new TaskOperator() {
				@Override
				public Task doTask(Task task) {
					laserScan();
					task.setObtainMsg(false);

					return task;
				}
			};
			scanTask.setTaskOperator(taskOperator);
			BackgroundService.addTask(scanTask);
		}

	}

	private void setScanGpioState(boolean state) {
		FileWriter fstream;
		try {
			fstream = new FileWriter("/dev/scan");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(state ? '0' : '1');
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean getScanGpioState() {
		FileReader fstream;
		int scanState = '1';
		try {
			fstream = new FileReader("/dev/scan");
			BufferedReader in = new BufferedReader(fstream);
			scanState = in.read();
			in.close();
			fstream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scanState == '0';
	}

	private volatile boolean mRestoreGpioStateHandled = false;
	private final Runnable mRestoreGpioState = new Runnable() {
		public void run() {
			if (!mRestoreGpioStateHandled) {
				mRestoreGpioStateHandled = true;
				setScanGpioState(false);
			}
		}
	};

	/**
	 * 
	 * @description 激光扫描
	 */
	private void laserScan() {
		boolean isEnable = getScanGpioState();
		if (isEnable) {
			setScanGpioState(false);
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i(TAG, e.toString());
			}
		}

		setScanGpioState(true);
		mRestoreGpioStateHandled = false;
		mHandler.postDelayed(mRestoreGpioState, 3000);

	}

	/**
	 * @param activity
	 * @param taskType
	 * @description 如果调用者是android组建中activity 那么这个方法必须被调用
	 */
	/*
	 * public void userInitLaser(Activity activity,int taskType) {
	 * if(null==activity) throw new
	 * NullPointerException(MyApplication.getApp().getString
	 * (R.string.user_initLaserPara_error)); this.activity = activity;
	 * this.taskType = taskType;
	 * 
	 * }
	 */

	/**
	 * @author Administrator
	 * @description 激光扫描监听器
	 */
	/*
	 * public class LaserScanLis implements SerialPortEventListener { private
	 * Activity activity; private int taskType;
	 * 
	 * public LaserScanLis(Activity activity,int taskType) { this.activity =
	 * activity; this.taskType = taskType; }
	 * 
	 * @Override public void serialEvent(SerialPortEvent arg0) {
	 * switch(arg0.getEventType()) { case SerialPortEvent.DATA_AVAILABLE:
	 * Log.i(TAG,"DATA_AVAILABLE"); readBarCode(activity, taskType); break; }
	 * 
	 * }
	 * 
	 * }
	 */

	public void reSetLaserLis(SerialPortEventListener laserLis) {
		// if(null==laserLis) throw new
		// NullPointerException(MyApplication.getApp().getString(R.string.laserLis_notNull));

		if (null != serialPortOper && !serialPortOper.getSerialState()) {
			// serialPortOper.getSerialPort().removeEventListener();//移除原有的串口监听器
			try {
				serialPortOper.getSerialPort().addEventListener(laserLis);
				serialPortOper.getSerialPort().notifyOnDataAvailable(true);
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}

			return;
		}
		Log.i(TAG, MyApplication.getApp().getString(R.string.serialInit_fail));
	}

	@Override
	public void serialEvent(SerialPortEvent arg0) {

		switch (arg0.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			// Log.i(TAG,"DATA_AVAILABLE");
			if (!mRestoreGpioStateHandled) {
				mHandler.removeCallbacks(mRestoreGpioState);
				setScanGpioState(false);
			}
			readBarCode(activity, taskType);
			break;
		}

	}

	/**
	 * @param activity
	 * @param taskType
	 * @description
	 */
	public static void initLaserUserInfor(Activity activity, int taskType) {
		LaserScanOperator.activity = activity;
		LaserScanOperator.taskType = taskType;

	}

	public boolean getLaserLisState() {

		return laserLisState;
	}

	/**
	 * @param msg
	 * @description 激光器启动出错的话 调用该方法通知程序错误消息
	 */
	public void laserInitFailNotify() {
		Task notifyTask = new Task(activity, TaskType.LASER_INIT_FAIL,
				MyApplication.getApp()
						.getString(R.string.laser_serial_initfail));
		// BackgroundService.addTask(notifyTask);
		MesUtils.notifyToastMsg(notifyTask);
	}

	/**
	 * 
	 * @description 关闭激光扫描器
	 */
	public static void closeLaserScanOperator() {
		// Log.i(TAG, ""+laserLisState);
		try {
			if (laserLisState)// 该激光扫描器成功开启
			{
				laserScanOperator.serialPortOper.closeSerialPort();
				laserScanOperator.serialPortOper = null;
				laserScanOperator = null;
				laserLisState = false;
				Log.i(TAG, "close laser scanner");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Log.i(TAG, e.toString());
		}

	}

	/**
	 * 获取当前激光头状态 应用该方法主要是确保程序的高强壮性
	 */
	public static boolean getCurLaserScanState() {
		if (null == laserScanOperator
				|| null == laserScanOperator.serialPortOper
				|| !laserScanOperator.laserLisState
				|| laserScanOperator.serialPortOper.getSerialState())
			return false;

		return true;
	}

	/**
	 * 
	 * @description 激光扫描功能开启状态
	 */
	public static boolean getLaserEnable() {

		return laserEnable;
	}

	/**
	 * @description 关闭激光扫描功能
	 */
	private static void disableLaserScan() {
		if (laserEnable)// 在禁用激光扫描的同时 如有激光扫描器处于开启状态 则 关闭激光扫描器
			if (laserLisState)
				closeLaserScanOperator();

		laserEnable = false;
	}

	/**
	 * 开启激光扫描的功能
	 */
	private static void enableLaserScan() {

		laserEnable = true;
	}

	/**
	 * @description 配置激光扫描功能 开启或关闭
	 */
	public static void changeLaserEnable(boolean isEnable) {
		if (isEnable)
			enableLaserScan();
		else
			disableLaserScan();

	}

}
