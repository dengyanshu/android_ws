package com.zowee.mes.utils;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import com.zowee.mes.eventlistener.MySerialEventListener;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
/**
 * 赶仅供此PAD用写的很死，波特率9600!
 * 注册串口
 * 关闭串口
 * 监听串口
 * */
public final class SerialportUtil {
	
	
	private  static final  String 	TAG="SerialportUtil";
	public static String OWNER = "system";
	public  static  final  String SCAN_COMMAND="1B31";
	private  SerialportUtil(){
	}
	
	public static SerialPort registSerialPort(String tty,Context  context)throws TooManyListenersException{
	
			SerialPort serialPort = null;
			//InputStream mInputStream = null;
			try {
				Log.i(TAG, "registerPort: tty = " + tty);
				CommPortIdentifier portId = CommPortIdentifier
						.getPortIdentifier(tty);
				serialPort = (SerialPort) portId.open(OWNER, 5000);

				
				serialPort.notifyOnDataAvailable(true);
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				serialPort.notifyOnBreakInterrupt(true);
			} catch (PortInUseException e) {
				Log.e(TAG, "registerPort: " + e.currentOwner);
				Toast.makeText(context, "串口正在使用异常..", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
				Toast.makeText(context, "串口注册出现异常", Toast.LENGTH_SHORT).show();
			} catch (NoSuchPortException e) {
				e.printStackTrace();
				Toast.makeText(context, "未找到这个串口注册出现异常", Toast.LENGTH_SHORT).show();
			}
			return serialPort;
		}
	   
	public static SerialPort registSerialPortNew(String tty,Context  context,String jijiaoyan)throws TooManyListenersException{
		
		SerialPort serialPort = null;
		//InputStream mInputStream = null;
		try {
			Log.i(TAG, "registerPort: tty = " + tty);
			CommPortIdentifier portId = CommPortIdentifier
					.getPortIdentifier(tty);
			serialPort = (SerialPort) portId.open(OWNER, 5000);

			
			serialPort.notifyOnDataAvailable(true);
			if(jijiaoyan==null ||"".equals(jijiaoyan)){
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			}
			else{
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, 1);
			}
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.notifyOnBreakInterrupt(true);
		} catch (PortInUseException e) {
			Log.e(TAG, "registerPort: " + e.currentOwner);
			Toast.makeText(context, "串口正在使用异常..", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
			Toast.makeText(context, "串口注册出现异常", Toast.LENGTH_SHORT).show();
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			Toast.makeText(context, "未找到这个串口注册出现异常", Toast.LENGTH_SHORT).show();
		}
		return serialPort;
	}
	
	
	  public static  void  closeSerialPort(SerialPort  sp){
		  try{
			  if(sp!=null){
				  sp.close();
				  sp=null;
			  }
		  }
		  catch (Exception e) {
			// TODO: handle exception
			  e.printStackTrace();
		 }
	  }
	  
	  
	public static void dataOutput(SerialPort portName,String out) {
			try {
			   OutputStream	dataOutput = portName.getOutputStream();
				dataOutput.write(out.getBytes("US-ASCII"));
				dataOutput.flush();
				//Toast.makeText(this, portName+"串口写数据成功！写的数据是"+out, Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	public static void clearBuff(SerialPort portName) {
			// 接收消息以清空缓存
		     InputStream  is=null;
			try {
				is=portName.getInputStream();
				if (is.available() > 0) {
					is.read(new byte[512]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	  
	
	public static  void  addEvent(SerialPort  sp,int handlerwhat,Handler handler) throws TooManyListenersException{
		if(sp!=null)
		sp.addEventListener(new MySerialEventListener(handler,handlerwhat,sp));
		
		
	}
	
	public  static  void  startScan(SerialPort  sp){
		OutputStream  os=null;
		try {
			os=sp.getOutputStream();
			os.write(HexString2Bytes());
			os.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			if(os!=null)
			{
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private  final static byte[] HexString2Bytes() {
		byte[] ret = new byte[SCAN_COMMAND.length() / 2];
		byte[] tmp = SCAN_COMMAND.getBytes();
		for (int i = 0; i < tmp.length / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	private final static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}
	  
}

