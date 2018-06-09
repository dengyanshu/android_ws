package com.zowee.mes.laser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.laser.SerialPortOper.SerialParams;
import com.zowee.mes.utils.StringUtils;
import com.zowee.mes.ws.MesSoapParser;
import com.zowee.mes.ws.WebService.Soap;

/**
 * @author Administrator
 * @description 解析并初始化激光扫描配置信息
 */
public class LaserConfigParser {

	private static final String NAME = "NAME";// 串口名
	private static final String OWNER = "OWNER";// 激光扫描器调用者
	private static final String BAURATE = "BAURATE";// 波特率
	private static final String TIMEOUT = "TIMEOUT";// 超时参数
	private static final String DATABITS = "DATABITS";// 数据位数
	private static final String ENDBIT = "ENDBIT";// 结束位
	private static final String PRIVATYBIT = "PRIVATYBIT";// 验证位
	private static final String FLOWCONTROL = "FLOWCONTROL";
	private static final String TAG = "LaserScanOperator";
	private static final String LASERCONFIG_NAME = "laser_config";// 用户定义的激光配置文件名
	private static final String LASERCONFIG_LOCAL = "serialPortConfig.properties";// 原始激光配置
																					// 文件路径
	private static final String LASERFUNCTION_NAME = "laserfunction_config";// 是否启用激光扫描
																			// 用户自定义配置文件
	private static final String LASERENABLE = "laserEnable";// 是否启用激光扫描

	/**
	 * @return
	 * @description 解析原始激光扫描配置文件 并把解析的数据以 SerialParams形式返回
	 */
	public static SerialParams getLaserConfig() {
		SerialParams serialParams = null;
		// InputStream inStream =
		// MesSoapParser.class.getClassLoader().getResourceAsStream(LASERCONFIG_LOCAL);
		InputStream inStream = null;
		Properties pro = new Properties();
		try {
			inStream = MyApplication.getApp().getAssets()
					.open(LASERCONFIG_LOCAL);
			if (null == inStream)
				return null;
			pro.load(inStream);
			String name = pro.getProperty(NAME);
			String owner = pro.getProperty(OWNER);
			String tempTimeOut = pro.getProperty(TIMEOUT);
			String tempBauRate = pro.getProperty(BAURATE);
			String tempDataBits = pro.getProperty(DATABITS);
			String tempEndBit = pro.getProperty(ENDBIT);
			String tempPriorityBit = pro.getProperty(PRIVATYBIT);
			String tempFlowControl = pro.getProperty(FLOWCONTROL);

			if (null == name)
				name = "";
			if (null == owner)
				owner = "";
			if (null == tempTimeOut)
				tempTimeOut = "-1";
			if (null == tempBauRate)
				tempBauRate = "-1";
			if (null == tempDataBits)
				tempDataBits = "-1";
			if (null == tempEndBit)
				tempEndBit = "-1";
			if (null == tempPriorityBit)
				tempPriorityBit = "-1";
			if (null == tempFlowControl)
				tempFlowControl = "-1";
			int timeout = Integer.valueOf(tempTimeOut);
			int bauRate = Integer.valueOf(tempBauRate);
			int dataBits = Integer.valueOf(tempDataBits);
			int endBit = Integer.valueOf(tempEndBit);
			int privatyBit = Integer.valueOf(tempPriorityBit);
			int flowControl = Integer.valueOf(tempFlowControl);

			serialParams = new SerialParams(name, owner, timeout, bauRate,
					dataBits, endBit, privatyBit, flowControl);
			// inStream.close();
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		} finally {
			try {
				inStream.close();
			} catch (IOException e) {
				Log.i(TAG, e.toString());
				// e.printStackTrace();
			}
		}

		return serialParams;
	}

	/**
	 * @description 持久化激光配置
	 */
	public static void mergeLaserConfig(SerialParams serialParams) {
		if (null == serialParams)
			return;
		SharedPreferences preferences = MyApplication.getApp()
				.getSharedPreferences(LASERCONFIG_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();// 清除原有的配置信息
		editor.putString(NAME, serialParams.name);
		editor.putString(OWNER, serialParams.owner);
		editor.putInt(TIMEOUT, serialParams.timeOut);
		editor.putInt(BAURATE, serialParams.baudRate);
		editor.putInt(DATABITS, serialParams.dataBits);
		editor.putInt(ENDBIT, serialParams.endBits);
		editor.putInt(PRIVATYBIT, serialParams.parity);
		editor.putInt(FLOWCONTROL, serialParams.flowControl);
		// editor.putString(USERTICKET,soap.getWsProperty().get(USERTICKET).toString());
		editor.commit();

	}

	// /**
	// * @return
	// * @description 解析激光扫描配置文件 并把解析的参数以 SerialParams形式返回
	// */
	// public static SerialParams getLaserConfig()
	// {
	// SerialParams serialParams = null;
	// InputStream inStream =
	// MesSoapParser.class.getClassLoader().getResourceAsStream("com/yksj/mse/laser/serialPortConfig.properties");
	// Properties pro = new Properties();
	// try
	// {
	// pro.load(inStream);
	// String name= pro.getProperty(NAME);
	// String owner = pro.getProperty(OWNER);
	// int timeout = Integer.valueOf(pro.getProperty(TIMEOUT));
	// int bauRate = Integer.valueOf(pro.getProperty(BAURATE));
	// int dataBits = Integer.valueOf(pro.getProperty(DATABITS));
	// int endBit = Integer.valueOf(pro.getProperty(ENDBIT));
	// int privatyBit = Integer.valueOf(pro.getProperty(PRIVATYBIT));
	// int flowControl = Integer.valueOf(pro.getProperty(FLOWCONTROL));
	// serialParams = new SerialParams(name, owner, timeout, bauRate, dataBits,
	// endBit, privatyBit, flowControl);
	// inStream.close();
	// }
	// catch (Exception e)
	// {
	// Log.i(TAG, e.toString());
	// }
	//
	// return serialParams;
	// }
	/**
	 * 
	 * @description 获取用户的激光扫描配置信息
	 */
	public static SerialParams getFinalSerialParams() {
		SharedPreferences preferences = MyApplication.getApp()
				.getSharedPreferences(LASERCONFIG_NAME, Context.MODE_PRIVATE);
		String name = preferences.getString(NAME, "");
		String owner = preferences.getString(OWNER, "");
		// int bauRate = preferences.getInt(BAURATE, -1);
		if (StringUtils.isEmpty(name) && StringUtils.isEmpty(owner)) {
			// Log.i(TAG, "I am coming in");
			SerialParams tempParams = getLaserConfig();
			mergeLaserConfig(tempParams);
		}

		name = preferences.getString(NAME, "").trim();
		owner = preferences.getString(OWNER, "").trim();
		int timeout = preferences.getInt(TIMEOUT, -1);
		int bauRate = preferences.getInt(BAURATE, -1);
		int dataBits = preferences.getInt(DATABITS, -1);
		int endBit = preferences.getInt(ENDBIT, -1);
		int privatyBit = preferences.getInt(PRIVATYBIT, -1);
		int flowControl = preferences.getInt(FLOWCONTROL, -1);
		SerialParams serialParams = new SerialParams(name, owner, timeout,
				bauRate, dataBits, endBit, privatyBit, flowControl);

		return serialParams;
	}

	/**
	 * @description 获取用户自定义激光扫描功能
	 */
	public static boolean getLaserFunctionEnable() {
		SharedPreferences sharedPreferences = MyApplication.getApp()
				.getSharedPreferences(LASERFUNCTION_NAME, Context.MODE_PRIVATE);
		if (!sharedPreferences.contains(LASERENABLE))// 表明了该用户是第一次使用该应用
		{
			Editor editor = sharedPreferences.edit();
			editor.putBoolean(LASERENABLE, false); // 平板没有LAser设为false, PDE
													// 设有TRUE。 yanbj
			editor.commit();
		}

		boolean isEnable = sharedPreferences.getBoolean(LASERENABLE, true);

		return isEnable;
	}

	/**
	 * @description 配置用户自定义激光扫描功能
	 */
	public static void mergeLaserFunctionEnable(boolean isEnable) {
		SharedPreferences sharedPreferences = MyApplication.getApp()
				.getSharedPreferences(LASERFUNCTION_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.clear();// 清除原有的配置
		editor.putBoolean(LASERENABLE, isEnable);
		editor.commit();

	}

}
