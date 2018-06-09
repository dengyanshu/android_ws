package com.zowee.mes.laser;


import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Enumeration;


import com.zowee.mes.utils.ArrayUtils;
import com.zowee.mes.utils.IOUtils;
import com.zowee.mes.utils.StringUtils;


import android.util.Log;

/**
 * @author Administrator
 * @description 串口操作类  
 */
public class SerialPortOper
{
	private SerialParams serialParams;//串口参数
	public InputStream inStream;//输入流
	public OutputStream outStream;//输出流
	private static boolean isClose = true;//串口是否关闭
	private SerialPort serialPort;//串口
	private final static String TAG = "SerialPort";
	private final static int[] DATA_BITS = {SerialPort.DATABITS_5,SerialPort.DATABITS_6,SerialPort.DATABITS_7,SerialPort.DATABITS_8};
	private final static int[] FLOW_CONTROL = {SerialPort.FLOWCONTROL_NONE,SerialPort.FLOWCONTROL_RTSCTS_IN,SerialPort.FLOWCONTROL_RTSCTS_OUT,SerialPort.FLOWCONTROL_XONXOFF_IN,SerialPort.FLOWCONTROL_XONXOFF_OUT};
	private final static int[] PARITYS = {SerialPort.PARITY_EVEN,SerialPort.PARITY_MARK,SerialPort.PARITY_NONE,SerialPort.PARITY_ODD,SerialPort.PARITY_SPACE};
	private final static int[] STOP_BITS = {SerialPort.STOPBITS_1,SerialPort.STOPBITS_1_5,SerialPort.STOPBITS_2};
	
	public SerialPortOper(SerialParams serialParams) throws Exception
	{
		
		this.serialParams = serialParams;
		init(serialParams);
	}
		
	private void init(SerialParams serialParams) throws Exception
	{
		if(!subSerialParam())
			throw new IllegalArgumentException("argument serialParams error");
	//	CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(serialParams.name);  //  change by ybj 
	//	serialPort = (SerialPort)portId.open(serialParams.owner, serialParams.timeOut);
		serialPort = new RXTXPort(serialParams.name);   //  change by ybj  20140314
		inStream = serialPort.getInputStream();
		outStream = serialPort.getOutputStream();
		serialPort.setSerialPortParams(serialParams.baudRate, serialParams.dataBits, serialParams.endBits, serialParams.flowControl);
		isClose = false;		
		
	}
	
	/**
	 * @author Administrator
	 * @description 封装了开启初始化串口所必须的参数 
	 */
	public final static class SerialParams implements Serializable 
	{
		public String name;//串口名字
		public String owner;//使用端口的程序
		public int timeOut;//打开端口超时设置
		public int baudRate;//波特率
		public int dataBits;//数据位数
		public int endBits;//结束位数
		public int parity;//数据验证
		public int flowControl;//流控制
		
		public SerialParams(String name, String owner, int timeOut, int baudRate,int dataBits, int endBits, int parity, int flowControl) 
		{
			super();
			this.name = name;
			this.owner = owner;
			this.timeOut = timeOut;
			this.baudRate = baudRate;
			this.dataBits = dataBits;
			this.endBits = endBits;
			this.parity = parity;
			this.flowControl = flowControl;
		}
	}
	/**
	 * 
	 * @description 关闭串口 
	 */
	public  void closeSerialPort() throws Exception
	{
		
//		try 
//		{
			if(isClose||null==serialPort)
				return;
			serialPort.close();
			serialPort.removeEventListener();
			if(null!=inStream)inStream.close();
			if(null!=outStream)outStream.close();
			inStream = null;
			outStream = null;
			serialPort = null;
			isClose = true;
//		} 
//		catch (IOException e)
//		{
//			//e.printStackTrace();
//			Log.i(TAG, e.toString());
//		}
		
	}
	
//	/**
//	 * 
//	 * @description 开启串口 
//	 */
//	public void startSerialPort()throws Exception
//	{
//		//Log.i(TAG, isClose+"");
//		if(!isClose)
//			return;
//		this.init(serialParams);
//		//isClose = false;
//		//Log.i(TAG, "start");
//	}
	
	/**
	 * @return
	 * @description 获取当前SerialPortOper持有的SerialPort 
	 */
	public SerialPort getSerialPort()
	{
		
		return this.serialPort;
	}
	
	public String readInStream()
	{
		
		return IOUtils.parBloInStreamToStr(inStream, null);
	}
	
	/**
	 * @return
	 * @description 返回串口当前的状态 
	 */
	public static boolean getSerialState()
	{
	
		return isClose;
	}
	
	/**
	 * @return
	 * @description 对当前串口参数的合法性进行验证 
	 */
	private boolean subSerialParam()
	{
		if(null==serialParams) return false;
		if(StringUtils.isEmpty(serialParams.name)) return false;
		if(StringUtils.isEmpty(serialParams.owner)) return false;
		if(serialParams.timeOut<=-1) return false;
		if(serialParams.baudRate<=0) return false;
		if(!ArrayUtils.findValInIntArr(serialParams.dataBits, DATA_BITS)) return false;
		if(!ArrayUtils.findValInIntArr(serialParams.endBits, STOP_BITS)) return false;
		if(!ArrayUtils.findValInIntArr(serialParams.parity, PARITYS)) return false;
		if(!ArrayUtils.findValInIntArr(serialParams.flowControl, FLOW_CONTROL)) return false;
		
		return true;
	}
	
	
}
