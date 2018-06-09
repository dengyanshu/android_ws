package com.zowee.mes.test;

import gnu.io.SerialPort;

import com.zowee.mes.app.MyApplication;
import com.zowee.mes.laser.LaserConfigParser;
import com.zowee.mes.laser.SerialPortOper.SerialParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.test.AndroidTestCase;
import android.util.Log;
/**
 * @author Administrator
 * @description 和激光扫描有关的测试类
 */
public class LasorScanTest extends AndroidTestCase
{

	private static final String TAG = "LaserScanOperator";
	
	public void testGetParamsValue()throws Exception
	{
		Log.i(TAG, "datebit: "+SerialPort.DATABITS_8);
		Log.i(TAG, "stopbit: "+SerialPort.STOPBITS_1);
		Log.i(TAG, "privitbit: "+SerialPort.PARITY_NONE);//
		Log.i(TAG, "flowControl: "+SerialPort.FLOWCONTROL_NONE);
	}
	
	public void testGetLaserConfig()throws Exception
	{
		SerialParams serialParams = LaserConfigParser.getFinalSerialParams();
		Log.i(TAG, "name: "+serialParams.name);
		Log.i(TAG, "owner: "+serialParams.owner);
		Log.i(TAG, "bauRate: "+serialParams.baudRate);
		Log.i(TAG, "timeout: "+serialParams.timeOut);
		Log.i(TAG, "dataBits: "+serialParams.dataBits);
		Log.i(TAG, "stopBit: "+serialParams.endBits);
		Log.i(TAG, "privatyBit: "+serialParams.parity);
		Log.i(TAG, "flowControl: "+serialParams.flowControl);
	}
	
	public void testGetLaserEnable()
	{
		SharedPreferences sharedPreferences = MyApplication.getApp().getSharedPreferences("one", Context.MODE_PRIVATE);
		boolean isContaining = sharedPreferences.contains("oneKey");
		Log.i(TAG, ""+isContaining);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("oneKey", false);
		editor.commit();
		isContaining = sharedPreferences.contains("oneKey");
		Log.i(TAG, ""+isContaining);
		
	}
	
	
	
}
