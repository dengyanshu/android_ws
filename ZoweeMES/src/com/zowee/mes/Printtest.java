package com.zowee.mes;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TooManyListenersException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.BiaoqianheduiModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.PrinttestModel;
import com.zowee.mes.model.SmtFvmiModel;
import com.zowee.mes.model.SntocarModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//打印测试程序
public class Printtest extends CommonActivity  {

	 private static final String OWNER = "system";
	 private  static  final String SCAN_COMMAND="1B31";
	 
	 private OutputStream dataOutput;
	    
	
	private  PrinttestModel printtestModel;
     private  EditText  tv;
     private  Button button;
     private  Button printbutton;
     private  EditText ed;
     
     private  String  jiaobenString="未获取值";
     private  String  jiaoben="";
     private  String sn;
     private  String mac;
     private  static  final  String TAG="MainActivity";
     private  Button  canshubutton;
     private  TextView  tv2;
     StringBuffer sb;
     /**********************************************************************************************/
     private  SerialPort  serialport_1;
     private  SerialPort  serialport_2;
     private  SerialPort  serialport_3;
     /**********************************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		printtestModel=new PrinttestModel();
		try {
			registerPorts();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "注册串口失败", Toast.LENGTH_SHORT).show();
		}
		tv=(EditText) findViewById(R.id.textView1);
		button=(Button) findViewById(R.id.button1);
		button.setOnClickListener(mybuttononclicklistener);
		printbutton=(Button) findViewById(R.id.button2);
		printbutton.setOnClickListener(printclicklistener);
		ed=(EditText) findViewById(R.id.printtest_snedit);
		tv2=(TextView) findViewById(R.id.textView2);
		canshubutton=(Button) findViewById(R.id.button3);
		canshubutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				// TODO Auto-generated method stub
				tv2.setText("串口=com1,波特率=9600");
				
			}
		});
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				analysisMessage("COM1",msg.obj.toString());
				break;
			
			}
			super.handleMessage(msg);
		}
	};
	
	public void analysisMessage(String tty, String msg){
		if("COM1".equals(tty)){  //读取到的是PLC的信息，操作扫描枪
		  tv.setText("saodaode"+msg);
		}
	}
	//获取脚本监听
//    private  View.OnClickListener  mybuttononclicklistener=new View.OnClickListener() {
//		
//		@Override
//		public void onClick(View arg0) {
//			
//						//http://10.2.0.9:8080/update.xml  ok
//						//http://10.2.0.9:8080/Android_apk/
//						//ftp://10.2.0.9/PackTemplate/11.txt
//						//ftp://mes:mesmes@10.2.0.9/PackTemplate/H108NS A规下盖贴纸900000-1485 2014-4-30.txt
//						//String  str=new  String("ftp://mes:mesmes@10.2.0.9/PackTemplate/10-14彩盒打印 (3).txt".getBytes(),"UTF-8");
//						//String path=URLEncoder.encode(str,"GBK");
//				
//			new  Thread(){
//				public void run() {
//					 String str = null;
//					 ByteArrayOutputStream  bais=null;
//					 URL url=null;
//					 URLConnection  con=null;
//					 InputStream isr=null;
//					 InputStreamReader i=null;
//						try {
//							//str = "ftp://mes:mesmes@10.2.0.9/PackTemplate/"+URLEncoder.encode("1.4 BP脚本.txt","GB2312");
//							str="ftp://mes:mesmes@10.2.0.9/PackTemplate/1.4 BP脚本.txt";
//							url = new URL(str);
//							con=url.openConnection();
//							isr=con.getInputStream();
//							//isr = url.openStream();
//							i=new InputStreamReader(isr, "GBK");
//							bais= new ByteArrayOutputStream();
//							char[]  buf=new char[1024];
//							int  len=0;
//
//							while((len=i.read(buf))!=-1){
//
//								//bais.write(buf,0,len);
//								//bais.flush();
//								
//								jiaobenString=jiaobenString+new String(buf,0,len);
//								
//							}
//							 //jiaobenString=bais.toString();
//							 Log.i(TAG,"zhe zhixing"+jiaobenString);
//							 Message msg=  myhandler.obtainMessage();
//							 msg.what=1;
//							 msg.obj=jiaobenString;
//							 myhandler.sendMessage(msg);
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//							   
//				}
//			}.start();
//						
//			
//		}
//	};
	
	   private  View.OnClickListener  mybuttononclicklistener=new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
							//http://10.2.0.9:8080/update.xml  ok
							//http://10.2.0.9:8080/Android_apk/
							//ftp://10.2.0.9/PackTemplate/11.txt
							//ftp://mes:mesmes@10.2.0.9/PackTemplate/H108NS A规下盖贴纸900000-1485 2014-4-30.txt
							//String  str=new  String("ftp://mes:mesmes@10.2.0.9/PackTemplate/10-14彩盒打印 (3).txt".getBytes(),"UTF-8");
							//String path=URLEncoder.encode(str,"GBK");
					
				new  Thread(){
					public void run() {
						 String str = null;
						 ByteArrayOutputStream  bais=null;
						 URL url=null;
						 InputStream isr=null;
							try {
								//str = "ftp://mes:mesmes@10.2.0.9/PackTemplate/"+new  String("1.4 BP脚本.txt".getBytes("GBK"),"iso-8859-1");
								str="http://10.2.0.9:8080/Android_apk/snmac.txt";
								url=new URL(str);
								isr = url.openStream();
								bais= new ByteArrayOutputStream();
								byte[]  buf=new byte[1024];
								int  len=0;

								while((len=isr.read(buf))!=-1){

									bais.write(buf,0,len);
									bais.flush();
									
								}
								 jiaoben=bais.toString();
								 Log.i(TAG,"zhe zhixing"+jiaobenString);
								 Message msg=  myhandler.obtainMessage();
								 msg.what=1;
								 msg.obj=jiaoben;
								 myhandler.sendMessage(msg);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
								   
					}
				}.start();
							
				
			}
		};
		
	
	
	private  Handler myhandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==1){
				tv.setText(jiaoben);
			}
		}
	};
	
	
	private  View.OnClickListener  printclicklistener=new View.OnClickListener (){
		  public void onClick(View arg0) {
			  try {
				 dataOutput=serialport_1.getOutputStream();
				 sn= ed.getText().toString().trim().toUpperCase();
				 printtestModel.getmacbysn(new  Task(Printtest.this,TaskType.m8000cpgetpath,sn));
			   } catch (Exception e) {
				e.printStackTrace();
			    }
			  
				 
				 //jiaobenString=jiaobenString.replace(oldChar, newChar);
				//dataOutput.write(jiaobenString.getBytes());
			
	    }
	};
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		switch (task.getTaskType()) {
		// 获取工单ID
		case TaskType.m8000cpgetpath:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("LotSN")&&getdata.containsKey("MAC")) {
						sn=getdata.get("LotSN");
						mac=getdata.get("MAC");
						jiaobenString=jiaoben;
						jiaobenString=jiaobenString.replace("$SN$", sn).replace("$MAC_SC$", mac);
						try {
							dataOutput.write(jiaobenString.getBytes());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					Toast.makeText(this,"该SN对应的MAC为空，请检查SN和MAC是否存在", Toast.LENGTH_LONG).show();
				} 
				closeProDia();
			} else {
			}

			break;
		}
	}
	//串口监听
	private final class SerialEventsListener implements SerialPortEventListener {
		private InputStream dataInput;
		private String who;

		public SerialEventsListener(InputStream dataInput, String tty) {
			this.who = tty;
			this.dataInput = dataInput;
		}

		@SuppressWarnings("unused")
		@Override
		public void serialEvent(SerialPortEvent ev) {
			int type = ev.getEventType();
			switch (type) {
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;
			case SerialPortEvent.BI:
				break;
			case SerialPortEvent.DATA_AVAILABLE:
				
				if ("/dev/ttyS1".equals(who)) {
					
					if (true) {
						Message.obtain(mHandler, 1, readMessage()).sendToTarget();
						
					} else {
						clearBuff();
					}
				} else if ("/dev/ttyS2".equals(who)) {
					if (true) {
						readMessage();
						Toast.makeText(Printtest.this, who+"读到的数据是2口"+readMessage(), Toast.LENGTH_LONG).show();
					} else {
						clearBuff();
					}
				} else if ("/dev/ttyS3".equals(who)) {
					if (true) {
						readMessage();
						Toast.makeText(Printtest.this, who+"读到的数据是3口"+readMessage(), Toast.LENGTH_LONG).show();
					} else {
						clearBuff();
					}
				}
				break;
			}
		}

		private void clearBuff() {
			// 接收消息以清空缓存
			try {
				if (dataInput.available() > 0) {
					dataInput.read(new byte[512]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public String readMessage() {
			String sReadBuff = "";
			int i;
			byte[] readBuffer = new byte[512];
			int numBytes;
			try {
				while (dataInput.available() > 0) {
					SystemClock.sleep(50);
					numBytes = dataInput.read(readBuffer);
					
						for (i = 0; i < numBytes; i++) {
							Log.d(TAG, "serialEvent: byte[" + i + "]"
									+ (readBuffer[i]));
						}
					
					String tmpR = new String(readBuffer, 0, numBytes, "UTF-8");
					sReadBuff += tmpR;
				}
				
					Log.d(TAG, "serialEvent: read data:" + sReadBuff);
			} catch (IOException e) {
				Log.e(TAG, "serialEvent: Error Reading from serial port", e);
			}
			return sReadBuff;
	}
  }
	//注册串口
	private void registerPorts() throws TooManyListenersException {
		serialport_3 = registerPort("/dev/ttyS3");
		serialport_1 = registerPort("/dev/ttyS1");
		serialport_2 = registerPort("/dev/ttyS2");
	}
	//根据com口路径返回串口对象
	private SerialPort registerPort(String tty) throws TooManyListenersException{
		SerialPort serialPort = null;
		InputStream mInputStream = null;
		try {
			Log.i(TAG, "registerPort: tty = " + tty);
			CommPortIdentifier portId = CommPortIdentifier
					.getPortIdentifier(tty);
			serialPort = (SerialPort) portId.open(OWNER, 5000);
			try {
				mInputStream=serialPort.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(this, "串口注册这里有执行！得到输入流失败", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			serialPort.addEventListener(new SerialEventsListener(mInputStream,
					tty));
			serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.notifyOnBreakInterrupt(true);
		} catch (PortInUseException e) {
			Log.e(TAG, "registerPort: " + e.currentOwner);
			Toast.makeText(this, "串口注册出现异常", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
			Toast.makeText(this, "串口注册出现异常", Toast.LENGTH_SHORT).show();
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			Toast.makeText(this, "串口注册出现异常", Toast.LENGTH_SHORT).show();
		}
		return serialPort;
	}
	@SuppressWarnings("unused")
	private void dataOutput(SerialPort portName, String out) {
		try {
		OutputStream	dataOutput = portName.getOutputStream();
			dataOutput.write(out.getBytes("US-ASCII"));
			dataOutput.flush();
			Toast.makeText(this, portName+"串口写数据成功！写的数据是"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "串口写数据失败这里有执行！", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**十六进制转成字节数组*************************************************************************/
	public static byte[] HexString2Bytes() {
		byte[] ret = new byte[SCAN_COMMAND.length() / 2];
		byte[] tmp = SCAN_COMMAND.getBytes();
		for (int i = 0; i < tmp.length / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
			
				Log.d(TAG, "HexString2Bytes: ret[" + i + "]=" + ret[i]);
		}
		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}
	/**十六进制转成字节数组*************************************************************************/
	@Override
	public void onBackPressed() {
		killMainProcess();
	}

	private void killMainProcess() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("确定退出SMT贴片扫描吗?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				unRegisterPort();
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no), null).show();
	}
	
	private void unRegisterPort() {
		if (dataOutput != null) {
			try {
				dataOutput.close();
			} catch (IOException e) {
				Log.e(TAG, "unRegisterPort dataOutput", e);
			}
			dataOutput = null;
		}
		if (serialport_1 != null) {
			serialport_1.removeEventListener();
			serialport_1.close();
		}
		serialport_1 = null;
		if (serialport_2 != null) {
			serialport_2.removeEventListener();
			serialport_2.close();
		}
		serialport_2 = null;
		if (serialport_3 != null) {
			serialport_3.removeEventListener();
			serialport_3.close();
		}
	}
}