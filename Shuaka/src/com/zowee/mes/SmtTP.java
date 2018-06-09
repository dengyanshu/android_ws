package com.zowee.mes;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.BiaoqianheduiModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SMTSCModel;
import com.zowee.mes.model.SmtFvmiModel;
import com.zowee.mes.model.SmtTPModel;
import com.zowee.mes.model.SntocarModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class SmtTP extends CommonActivity implements  OnClickListener,OnKeyListener
		 {
	
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private EditText editMO;
	private EditText editMOProduct;
	private EditText editMOdescri;
	private String moid; // 工单选择保存变量
	private  String moname;
	
	private  TextView  tvzuobiaoweihu;
	
	private  CheckBox  box11;
	private  EditText  zuobiao11;
	private  CheckBox  box12;
	private  EditText  zuobiao12;
	private  CheckBox  box13;
	private  EditText  zuobiao13;
	private  CheckBox  box14;
	private  EditText  zuobiao14;
	
	private  CheckBox  box21;
	private  EditText  zuobiao21;
	private  CheckBox  box22;
	private  EditText  zuobiao22;
	private  CheckBox  box23;
	private  EditText  zuobiao23;
	private  CheckBox  box24;
	private  EditText  zuobiao24;
	
	private  CheckBox  box31;
	private  EditText  zuobiao31;
	private  CheckBox  box32;
	private  EditText  zuobiao32;
	private  CheckBox  box33;
	private  EditText  zuobiao33;
	private  CheckBox  box34;
	private  EditText  zuobiao34;
	
	private  CheckBox  box41;
	private  EditText  zuobiao41;
	private  CheckBox  box42;
	private  EditText  zuobiao42;
	private  CheckBox  box43;
	private  EditText  zuobiao43;
	private  CheckBox  box44;
	private  EditText  zuobiao44;
	
	private CheckBox[] checkboxes=new  CheckBox[]{box11,box12,box13,box14,
			box21,box22,box23,box24,box31,box32,box33,box34,box41,box42,box43,box44};
	
	private  int[]  boxids={R.id.smttp_checkbox11,R.id.smttp_checkbox12,R.id.smttp_checkbox13,R.id.smttp_checkbox14,
			R.id.smttp_checkbox21,R.id.smttp_checkbox22,R.id.smttp_checkbox23,R.id.smttp_checkbox24,
			R.id.smttp_checkbox31,R.id.smttp_checkbox32,R.id.smttp_checkbox33,R.id.smttp_checkbox34,
			R.id.smttp_checkbox41,R.id.smttp_checkbox42,R.id.smttp_checkbox43,R.id.smttp_checkbox44};
	
	
	private EditText[] zuobiao=new  EditText[]{zuobiao11,zuobiao12,zuobiao13,zuobiao14,
			zuobiao21,zuobiao22,zuobiao23,zuobiao24,zuobiao31,zuobiao32,zuobiao33,zuobiao34
			,zuobiao41,zuobiao42,zuobiao43,zuobiao44};
	
	private  int[]  zuobiaoids={R.id.smttp_edit11,R.id.smttp_edit12,R.id.smttp_edit13,R.id.smttp_edit14,
			R.id.smttp_edit21,R.id.smttp_edit22,R.id.smttp_edit23,R.id.smttp_edit24,
			R.id.smttp_edit31,R.id.smttp_edit32,R.id.smttp_edit33,R.id.smttp_edit34,
			R.id.smttp_edit41,R.id.smttp_edit42,R.id.smttp_edit43,R.id.smttp_edit44};
	
	private EditText sn;

	
	private Button okbutton;
	private Button cancelbutton;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
	

	private static EditText editscan;

	private  Common4dModel  common4dmodel;
	private  SmtTPModel  smtTPModel;
	private static final String TAG = "SmtTP";
	
	private boolean canScan = false; // 标记是否确认设置，未确认则不进行扫描等操作
	private boolean manualScan = false; // 标记手动扫描
	private  String  zuobiaostr="";
	private  String[] dots;
	private  int  num=0;//第几个点计数
	private  int  maxnum;//有几个点;
	private  String lots="";
	private static int reScanTimes = 1;//上下扫描头重扫次数
	
	private  SerialPort  serialport_1;
    private  SerialPort  serialport_2;
    private  SerialPort  serialport_3;
 // ============== SharedPreferences default setting ======================
 	public static String SCAN_COMMAND = "1B31"; // 扫描枪扫描指令
 	public static String SCAN_TIMEOUT_COMMAND = "Noread"; // 扫描中断指令
 	public static int SCAN_RESCAN = 2;
 	public static String OWNER = "system";
 	// ============== SharedPreferences default setting end ==================


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_smttp);
		init();
	}

	public void onBackPressed() {
		killMainProcess();
	}
    
	// 回退键关掉这个activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("确定退出程序?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								//回退修改无网络时候，报错！
								unRegisterPort();
								stopService(new Intent(SmtTP.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
    
	private void unRegisterPort() {
		if (serialport_2 != null) {
			serialport_2.removeEventListener();
			serialport_2.close();
		}
		serialport_2 = null;
		if (serialport_1 != null) {
			serialport_1.removeEventListener();
			serialport_1.close();
		}
		serialport_1 = null;
		if (serialport_3 != null) {
			serialport_3.removeEventListener();
			serialport_3.close();
		}
		serialport_3 = null;
		
	}
	
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.GetResourceId; // 后台服务静态整形常量
		super.init();

		common4dmodel=new  Common4dModel();
		smtTPModel=new SmtTPModel();
		
		getLayoutInflater().inflate(R.layout.activity_smttp_zuobiao, null);
		
		editMO = (EditText) findViewById(R.id.smttp_moedit);
		editMOdescri = (EditText) findViewById(R.id.smttp_modescription);
		editMOProduct = (EditText) findViewById(R.id.smttp_moproduct);
		editMO.requestFocus();

		tvzuobiaoweihu = (TextView) findViewById(R.id.smttp_tvweihuzhuangtai);
		sn=(EditText) findViewById(R.id.smttp_sn);
		
//		for(int x=0;x<16;x++){
//			checkboxes[x]=(CheckBox) findViewById(boxids[x]);
//			zuobiao[x]=(EditText) findViewById(zuobiaoids[x]);
//			
//		}
		
		
       box11=(CheckBox) findViewById(R.id.smttp_checkbox11);
       zuobiao11=(EditText) findViewById(R.id.smttp_edit11);
       box12=(CheckBox) findViewById(R.id.smttp_checkbox12);
       zuobiao12=(EditText) findViewById(R.id.smttp_edit12);
       box13=(CheckBox) findViewById(R.id.smttp_checkbox13);
       zuobiao13=(EditText) findViewById(R.id.smttp_edit13);
       box14=(CheckBox) findViewById(R.id.smttp_checkbox14);
       zuobiao14=(EditText) findViewById(R.id.smttp_edit14);
       Log.i(TAG,"box11==null为"+(box11==null));
       
       box21=(CheckBox) findViewById(R.id.smttp_checkbox21);
       zuobiao21=(EditText) findViewById(R.id.smttp_edit21);
       box22=(CheckBox) findViewById(R.id.smttp_checkbox22);
       zuobiao22=(EditText) findViewById(R.id.smttp_edit22);
       box23=(CheckBox) findViewById(R.id.smttp_checkbox23);
       zuobiao23=(EditText) findViewById(R.id.smttp_edit23);
       box24=(CheckBox) findViewById(R.id.smttp_checkbox24);
       zuobiao24=(EditText) findViewById(R.id.smttp_edit24);
       
       box31=(CheckBox) findViewById(R.id.smttp_checkbox31);
       zuobiao31=(EditText) findViewById(R.id.smttp_edit31);
       box32=(CheckBox) findViewById(R.id.smttp_checkbox32);
       zuobiao32=(EditText) findViewById(R.id.smttp_edit32);
       box33=(CheckBox) findViewById(R.id.smttp_checkbox33);
       zuobiao33=(EditText) findViewById(R.id.smttp_edit33);
       box34=(CheckBox) findViewById(R.id.smttp_checkbox34);
       zuobiao34=(EditText) findViewById(R.id.smttp_edit34);
       
       box41=(CheckBox) findViewById(R.id.smttp_checkbox41);
       zuobiao41=(EditText) findViewById(R.id.smttp_edit41);
       box42=(CheckBox) findViewById(R.id.smttp_checkbox42);
       zuobiao42=(EditText) findViewById(R.id.smttp_edit42);
       box43=(CheckBox) findViewById(R.id.smttp_checkbox43);
       zuobiao43=(EditText) findViewById(R.id.smttp_edit43);
       box44=(CheckBox) findViewById(R.id.smttp_checkbox44);
       zuobiao44=(EditText) findViewById(R.id.smttp_edit44);
	    
	
		cancelbutton = (Button) findViewById(R.id.smttp_cancelbutton);
		okbutton = (Button) findViewById(R.id.smttp_okbutton);

		editscan = (EditText) findViewById(R.id.smttp_editscan);
		editscan.setFocusable(false);
		
		

		okbutton.setOnClickListener(this);
		cancelbutton.setOnClickListener(this);
        
		editMO.setOnKeyListener(this);
		sn.setOnKeyListener(this);
		
        common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));
        try {
			
			serialport_2 = registerPort("/dev/ttyS2");
			serialport_1 = registerPort("/dev/ttyS1");
			serialport_3 = registerPort("/dev/ttyS3");
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "注册串口失败", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
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
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
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
				
					if (canScan) {
						//String msg=readMessage();
						//logSysDetails("串口1读到的数据是："+msg, "串口");
						Message.obtain(mHandler, 1, readMessage()).sendToTarget();
						//Toast.makeText(SmtTP.this, who+"读到的数据是"+msg, Toast.LENGTH_LONG).show();
					} else {
						clearBuff();
					}
				} 
 				else if ("/dev/ttyS2".equals(who)) {
						Message.obtain(mHandler, 2, readMessage())
								.sendToTarget();
						//logSysDetails("串口2读到的数据是："+readMessage(), "串口");
				} 
 			    else if ("/dev/ttyS3".equals(who)) {
						Message.obtain(mHandler, 3, readMessage())
								.sendToTarget();
						//logSysDetails("串口3读到的数据是："+readMessage(), "串口");
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
 					//原是50ms，后来串口调试工具 发现读的指令不全
 					numBytes = dataInput.read(readBuffer);
// 						for (i = 0; i < numBytes; i++) {
// 							//Log.d(TAG, "serialEvent: byte[" + i + "]"
// 								//	+ (readBuffer[i]));
// 						}
 					
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
 	
 	private void dataOutput(SerialPort portName, String out) {
		try {
		   OutputStream	dataOutput = portName.getOutputStream();
			dataOutput.write(out.getBytes("US-ASCII"));
			logSysDetails(portName+"写的数据是："+out, "数据");
			dataOutput.flush();
			//Log.i(TAG,portName+"写的数据是:"+out );
			//Toast.makeText(this, portName+"串口写数据成功！写的数据是"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			//,,,
			Toast.makeText(this, "串口写数据失败这里有执行！", Toast.LENGTH_SHORT).show();
		}
	}
 	
 	private void startscan(SerialPort portName) {
		try {
		   OutputStream	dataOutput = portName.getOutputStream();
			dataOutput.write(HexString2Bytes());
			dataOutput.flush();
			//Toast.makeText(this, portName+"串口写数据成功！写的数据是"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "串口写数据失败这里有执行！", Toast.LENGTH_SHORT).show();
		}
	}
 
	
 	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smttp, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.smttp_clear:
			editscan.setText("");
			break;
			
		case R.id.smttp_sheding:
			android.app.AlertDialog.Builder builder2 = new AlertDialog.Builder(
					this);
			LinearLayout loginForm = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.activity_smttp_zuobiao1, null);
			
			builder2.setView(loginForm);
			builder2.setTitle("*************************设定坐标***************************");
			
			final CheckBox  tomes_box11=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox11);
			final EditText  tomes_zuobiao11=(EditText) loginForm.findViewById(R.id.smttp1_edit11);
			final CheckBox  tomes_box12=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox12);
			final EditText  tomes_zuobiao12=(EditText) loginForm.findViewById(R.id.smttp1_edit12);
			final CheckBox  tomes_box13=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox13);
			final EditText  tomes_zuobiao13=(EditText) loginForm.findViewById(R.id.smttp1_edit13);
			final CheckBox  tomes_box14=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox14);
			final EditText  tomes_zuobiao14=(EditText) loginForm.findViewById(R.id.smttp1_edit14);
			
			final CheckBox  tomes_box21=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox21);
			final EditText  tomes_zuobiao21=(EditText) loginForm.findViewById(R.id.smttp1_edit21);
			final CheckBox  tomes_box22=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox22);
			final EditText  tomes_zuobiao22=(EditText) loginForm.findViewById(R.id.smttp1_edit22);
			final CheckBox  tomes_box23=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox23);
			final EditText  tomes_zuobiao23=(EditText) loginForm.findViewById(R.id.smttp1_edit23);
			final CheckBox  tomes_box24=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox24);
			final EditText  tomes_zuobiao24=(EditText) loginForm.findViewById(R.id.smttp1_edit24);
			
			
			final CheckBox  tomes_box31=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox31);
			final EditText  tomes_zuobiao31=(EditText) loginForm.findViewById(R.id.smttp1_edit31);
			final CheckBox  tomes_box32=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox32);
			final EditText  tomes_zuobiao32=(EditText) loginForm.findViewById(R.id.smttp1_edit32);
			final CheckBox  tomes_box33=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox33);
			final EditText  tomes_zuobiao33=(EditText) loginForm.findViewById(R.id.smttp1_edit33);
			final CheckBox  tomes_box34=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox34);
			final EditText  tomes_zuobiao34=(EditText) loginForm.findViewById(R.id.smttp1_edit34);
			
			
			final CheckBox  tomes_box41=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox41);
			final EditText  tomes_zuobiao41=(EditText) loginForm.findViewById(R.id.smttp1_edit41);
			final CheckBox  tomes_box42=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox42);
			final EditText  tomes_zuobiao42=(EditText) loginForm.findViewById(R.id.smttp1_edit42);
			final CheckBox  tomes_box43=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox43);
			final EditText  tomes_zuobiao43=(EditText) loginForm.findViewById(R.id.smttp1_edit43);
			final CheckBox  tomes_box44=(CheckBox) loginForm.findViewById(R.id.smttp1_checkbox44);
			final EditText  tomes_zuobiao44=(EditText) loginForm.findViewById(R.id.smttp1_edit44);
			
			builder2.setPositiveButton("确认",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
						String coordinate="";
						String str="";
						
						if(tomes_box11.isChecked()){
						    str=tomes_zuobiao11.getText().toString().trim();
							coordinate=coordinate+str+",";
							
							if(tomes_box12.isChecked()){
								String str1=tomes_zuobiao12.getText().toString().trim();
								coordinate=coordinate+str1+",";
								
								if(tomes_box13.isChecked()){
									str=tomes_zuobiao13.getText().toString().trim();
									coordinate=coordinate+str+",";
									
									if(tomes_box14.isChecked()){
										str=tomes_zuobiao14.getText().toString().trim();
										coordinate=coordinate+str+",|";
									}
									else {
										coordinate=coordinate+"|";
									}
								}
								else {
									coordinate=coordinate+"|";
								}
								
							}
							else {
								coordinate=coordinate+"|";
								
							}
							
						}
						else{
							
						}
						
						//...............................................................................................
						if(tomes_box21.isChecked()){
						    str=tomes_zuobiao21.getText().toString().trim();
							coordinate=coordinate+str+",";
							
							if(tomes_box22.isChecked()){
								String str1=tomes_zuobiao22.getText().toString().trim();
								coordinate=coordinate+str1+",";
								
								if(tomes_box23.isChecked()){
									str=tomes_zuobiao23.getText().toString().trim();
									coordinate=coordinate+str+",";
									
									if(tomes_box24.isChecked()){
										str=tomes_zuobiao24.getText().toString().trim();
										coordinate=coordinate+str+",|";
									}
									else {
										coordinate=coordinate+"|";
									}
								}
								else {
									coordinate=coordinate+"|";
								}
								
							}
							else {
								coordinate=coordinate+"|";
								
							}
							
						}
						else{
							
						}
						//................................................................................
						
						
						//...............................................................................................
						if(tomes_box31.isChecked()){
						    str=tomes_zuobiao31.getText().toString().trim();
							coordinate=coordinate+str+",";
							
							if(tomes_box32.isChecked()){
								String str1=tomes_zuobiao32.getText().toString().trim();
								coordinate=coordinate+str1+",";
								
								if(tomes_box33.isChecked()){
									str=tomes_zuobiao33.getText().toString().trim();
									coordinate=coordinate+str+",";
									
									if(tomes_box34.isChecked()){
										str=tomes_zuobiao34.getText().toString().trim();
										coordinate=coordinate+str+",|";
									}
									else {
										coordinate=coordinate+"|";
									}
								}
								else {
									coordinate=coordinate+"|";
								}
								
							}
							else {
								coordinate=coordinate+"|";
								
							}
							
						}
						else{
							
						}
						//................................................................................
						
						
						//...............................................................................................
						if(tomes_box41.isChecked()){
						    str=tomes_zuobiao41.getText().toString().trim();
							coordinate=coordinate+str+",";
							
							if(tomes_box42.isChecked()){
								String str1=tomes_zuobiao42.getText().toString().trim();
								coordinate=coordinate+str1+",";
								
								if(tomes_box43.isChecked()){
									str=tomes_zuobiao43.getText().toString().trim();
									coordinate=coordinate+str+",";
									
									if(tomes_box44.isChecked()){
										str=tomes_zuobiao44.getText().toString().trim();
										coordinate=coordinate+str+",|";
									}
									else {
										coordinate=coordinate+"|";
									}
								}
								else {
									coordinate=coordinate+"|";
								}
								
							}
							else {
								coordinate=coordinate+"|";
								
							}
							
						}
						else{
							
						}
						String[] p=new  String[]{coordinate,editMOProduct.getText().toString().trim()};
						if(editMOProduct.getText().toString().trim().equals("")){
							 Toast.makeText(SmtTP.this, "请确保产品料号已获取到！", Toast.LENGTH_LONG).show();
						}else{
//						     progressDialog.show();
//						     progressDialog.setTitle("提交.....");
							 smtTPModel.upload(new  Task(SmtTP.this,TaskType.smttiepianupload ,p));
							 Toast.makeText(SmtTP.this, "成功上传MES的坐标是"+coordinate+"产品料号是："+editMOProduct.getText().toString(), Toast.LENGTH_LONG).show();
						}
						
					    
				}
			});
			builder2.setNegativeButton("取消", null);
			builder2.create().show();
			break;
		}

		return true;
	}

	public static void ClearshowInfo() {
		if (editscan.getLineCount() > 20)
			editscan.setText("");
	}
	
	
	/**
	 * 十六进制字符串转Byte[]
	 * 
	 * @return
	 */
	public static byte[] HexString2Bytes() {
		byte[] ret = new byte[SCAN_COMMAND.length() / 2];
		byte[] tmp = SCAN_COMMAND.getBytes();
		for (int i = 0; i < tmp.length / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
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
	
	private void logSysDetails(String logText, String strflag) {
		CharacterStyle ssStyle = null;
		if (logText.contains(strflag)) {
			ssStyle = new ForegroundColorSpan(Color.BLUE);
		} else {
			ssStyle = new ForegroundColorSpan(Color.RED);
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sysLog = "[" + df.format(new Date()) + "]" + logText + "\n";
		SpannableStringBuilder ssBuilder = new SpannableStringBuilder(sysLog);
		ssBuilder.setSpan(ssStyle, 0, sysLog.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssBuilder.append(editscan.getText());
		editscan.setText(ssBuilder);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.smttp_cancelbutton:
			editMO.setEnabled(true);
			okbutton.setEnabled(true);
			sn.setEnabled(true);
//			for(CheckBox checkbox: checkboxes){
//				checkbox.setEnabled(true);
//			}
//			for(EditText edittext: zuobiao){
//				edittext.setEnabled(true);
//			}
			box11.setEnabled(true);
			box12.setEnabled(true);
			box13.setEnabled(true);
			box14.setEnabled(true);
			box21.setEnabled(true);
			box22.setEnabled(true);
			box23.setEnabled(true);
			box24.setEnabled(true);
			box31.setEnabled(true);
			box32.setEnabled(true);
			box33.setEnabled(true);
			box34.setEnabled(true);
			box41.setEnabled(true);
			box42.setEnabled(true);
			box43.setEnabled(true);
			box44.setEnabled(true);
			
			zuobiao11.setEnabled(true);
			zuobiao12.setEnabled(true);
			zuobiao13.setEnabled(true);
			zuobiao14.setEnabled(true);
			zuobiao21.setEnabled(true);
			zuobiao22.setEnabled(true);
			zuobiao23.setEnabled(true);
			zuobiao24.setEnabled(true);
			zuobiao31.setEnabled(true);
			zuobiao32.setEnabled(true);
			zuobiao33.setEnabled(true);
			zuobiao34.setEnabled(true);
			zuobiao41.setEnabled(true);
			zuobiao42.setEnabled(true);
			zuobiao43.setEnabled(true);
			zuobiao44.setEnabled(true);
			canScan=false;
			logSysDetails("点击了【重新设定】，不能读取PLC信息作业，设定完成点击【设置完毕按钮】", "正确");
			break;
			
		case R.id.smttp_okbutton:
			editMO.setEnabled(false);
			okbutton.setEnabled(false);
			
			box11.setEnabled(false);
			box12.setEnabled(false);
			box13.setEnabled(false);
			box14.setEnabled(false);
			box21.setEnabled(false);
			box22.setEnabled(false);
			box23.setEnabled(false);
			box24.setEnabled(false);
			box31.setEnabled(false);
			box32.setEnabled(false);
			box33.setEnabled(false);
			box34.setEnabled(false);
			box41.setEnabled(false);
			box42.setEnabled(false);
			box43.setEnabled(false);
			box44.setEnabled(false);
			
			zuobiao11.setEnabled(false);
			zuobiao12.setEnabled(false);
			zuobiao13.setEnabled(false);
			zuobiao14.setEnabled(false);
			zuobiao21.setEnabled(false);
			zuobiao22.setEnabled(false);
			zuobiao23.setEnabled(false);
			zuobiao24.setEnabled(false);
			zuobiao31.setEnabled(false);
			zuobiao32.setEnabled(false);
			zuobiao33.setEnabled(false);
			zuobiao34.setEnabled(false);
			zuobiao41.setEnabled(false);
			zuobiao42.setEnabled(false);
			zuobiao43.setEnabled(false);
			zuobiao44.setEnabled(false);
			
			canScan=true;
			zuobiaostr=getcoordinate();
			if(zuobiaostr.equalsIgnoreCase("")){
				canScan=false;
			}else{
			dots=zuobiaostr.split(",");
			maxnum=dots.length;
			}
			if(!canScan){
			logSysDetails("不能开始读取PLC信息作业，请检查上方中的坐标信息", "正确");
			}else
				logSysDetails("ok！开始准备接受PLC的板到达指令！需要扫描的坐标点是"+zuobiaostr+"点值是："+maxnum, "ok");
			okbutton.requestFocus();
			sn.setEnabled(false);
			
			break;
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.smttp_moedit:
			String param = editMO.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keyCode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(param.length()<8){
					Toast.makeText(this, "批次号长度不对", Toast.LENGTH_SHORT).show();
				}
				else{
					super.progressDialog.setMessage("正在获取工单...");
					super.showProDia();
					common4dmodel.getMObylotsn(new Task(this,TaskType.common4dmodelgetmobylotsn,param));
				}
			}
			break;
		case R.id.smttp_sn:
			String param1 = sn.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keyCode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(param1.length()<8){
					Toast.makeText(this, "批次号长度不对", Toast.LENGTH_SHORT).show();
				}
				else{
					super.progressDialog.setMessage("正在获取...");
					super.showProDia();
					commitToMes2(new String[]{resourceid,resourcename,useid,usename,param1});
				}
			}
			break;
	   }
		return false;
	}
	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		switch (task.getTaskType()) {
		// 获取工单ID
		case TaskType.common4dmodelgetresourceid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("ResourceId")) {
						resourceid = getdata.get("ResourceId");
					}
					String logText = "程序已启动!检测到该平板的资源名称:[ " + resourcename
							+ " ],资源ID: [ " + resourceid + " ],用户ID: [ " + useid + " ]!!如需更换工单请点击“工单”2字！！";
					logSysDetails(logText, "程序");
				} else {
					logSysDetails(
							"通过资源名称获取在MES获取资源ID失败，请检查配置的资源名称是否正确", "成功");
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取资源id信息失败，请检查配置则资源名称是否正确", "成功");
			}

			break;
		
		
		case TaskType.common4dmodelgetmobylotsn:
			super.closeProDia();
			String lotsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					
					moname = getdata.get("MOName");
					String productdescri = getdata.get("ProductDescription");
					String material = getdata.get("productName");
					moid = getdata.get("MOId");
					

					editMO.setText(moname);
					editMOdescri.setText(productdescri);
					editMOProduct.setText(material);
					tvzuobiaoweihu.setText("  如果该产品料号未维护条码所贴位置的坐标信息，可以在如下界面手动设置!");
					editMO.setEnabled(false);
					String scantext = "通过批次号：[" + lotsn + "]成功的获得工单:"
							+ moname + ",工单id:"+moid+",产品信息:" + productdescri + ",产品料号："
							+ material + "!";
					logSysDetails(scantext, "成功");
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);

				    smtTPModel.download(new Task(this,TaskType.smttiepiandownload,material));
				} else {
					logSysDetails(
							"通过批次号：[" + lotsn + "]在MES获取工单信息为空或者解析结果为空，请检查SN!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails(lotsn + "在MES获取工单信息失败，请检查输入的条码", "成功");
			}

			break;
		
			
		case TaskType.smttiepiandownload:
			super.closeProDia();
			String productname = editMOProduct.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				//获取的SNLocation==null,解析返回的任务结果是{“ERROR”,"连接MES失败"}
				if (getdata.get("Error") == null) {

					
					String zuobiao = getdata.get("SNLocation");
				    if(zuobiao==null||zuobiao.equals("")){
				    	
				    }
				    else{
				    	shezhijiemianbyzuobiao(zuobiao);
				    	String scantext = "通过产品料号：[" + productname + "]成功的获得坐标信息:"+zuobiao;
				    	logSysDetails(scantext, "成功");
				    	SoundEffectPlayService
				    	.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
				    }

				} else {
					logSysDetails(
							"通过产品料号：[" + productname + "]在MES获取坐标信息为空或者解析结果为空，请检查SN!"
									, "成功");
					box11.setChecked(false);
					zuobiao11.setText("");
					box12.setChecked(false);
					zuobiao12.setText("");
					box13.setChecked(false);
					zuobiao13.setText("");
					box14.setChecked(false);
					zuobiao14.setText("");
					box21.setChecked(false);
					zuobiao21.setText("");
					box22.setChecked(false);
					zuobiao22.setText("");
					box23.setChecked(false);
					zuobiao23.setText("");
					box24.setChecked(false);
					zuobiao24.setText("");
					box31.setChecked(false);
					zuobiao31.setText("");
					box32.setChecked(false);
					zuobiao32.setText("");
					box33.setChecked(false);
					zuobiao33.setText("");
					box34.setChecked(false);
					zuobiao34.setText("");
					box41.setChecked(false);
					zuobiao41.setText("");
					box42.setChecked(false);
					zuobiao42.setText("");
					box43.setChecked(false);
					zuobiao43.setText("");
					box44.setChecked(false);
					zuobiao44.setText("");
				}
			} else {
				logSysDetails(productname + "在MES获取工单信息失败，请检查输入的条码", "成功");
			}

			break;
	
			
		case TaskType.SMT_SC:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "贴片成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
					}

				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
			} else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			if(num==maxnum-1){
				dataOutput(serialport_1, "@END:\r\n");
			}
			else{
				num++;
				SystemClock.sleep(200);
				dataOutput(serialport_1, dots[num]);
			}
			break;
			
		case TaskType.smttiepianrengong:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "手动扫描贴片成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "手动扫描失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
					}

				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
				sn.requestFocus();
				sn.setText("");
			} else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;
			
			
		}
	}
	
	// 消息处理，更新UI
	private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					analysisMessage("COM1", msg.obj.toString().trim()); // 20140119
																		// 去掉\r\n
					break;
				case 2:
					analysisMessage("COM2", msg.obj.toString().trim()); // 20140119
																		// 去掉\r\n
					break;
				case 3:
					analysisMessage("COM3", msg.obj.toString().trim()); // 20140119
																		// 去掉\r\n
					break;
				}
				super.handleMessage(msg);
			}
	};

		

   public void analysisMessage(String tty, String msg) {
	   if ("COM1".equals(tty)) { // 读取到的是PLC的信息
		   logSysDetails("ZOWEE控制器指令！"+msg, "指令");
		   if (msg.contains("RUN")) {         
			   //串口1接受到 "@RUN:\r\n"指令  开始发送第一个点的坐标
			   num=0;
			   lots="";
			   dataOutput(serialport_1, dots[num]);
		   } else if (msg.contains("RESCAN")){
			   //点位不对，重新发次坐标
			   SystemClock.sleep(200);
			   dataOutput(serialport_1, dots[num]);
		 

		   } else if (msg.contains("SCAN")){
			   //开始扫描
			   if(dots[num].substring(2, 7).equals("00000")){
				   //需要上扫描头扫描
				   startscan(serialport_3);
			   }
			   else if(dots[num].substring(dots[num].length()-5, dots[num].length()).equals("00000")){
				   //下扫描头扫描
				   startscan(serialport_2);
			   }
			   else{
//				   startscan(serialport_2);
//				   startscan(serialport_3);
			   }
		   } 
		   else {
			   //logSysDetails("ZOWEE控制器指令！"+msg, "指令");
		   }
	   } 
	   
	   
	   else if ("COM2".equals(tty)) { // 读取到的是上扫描头的信息，扫描结果
		   if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
			   //logSysDetails("上扫描头: 扫描超时。", "正确");
			   handlerTimeOut.sendEmptyMessage(4);
			   return;
		   }
		   logSysDetails("上扫描头: " + msg, "");
		   reScanTimes=1;
		   if(lots.contains(msg)){
			   logSysDetails("重复扫描"+msg, "");
			   
		   }
		   else{
		   lots=lots+msg+",";
		   commitToMes(new  String[]{resourceid,resourcename,useid,usename,msg});
		   }
		

	   }

     else if ("COM3".equals(tty)) { // 读取到的是下扫描头的信息，扫描结果
    	 if (msg.contains(SCAN_TIMEOUT_COMMAND)) {
			  // logSysDetails("下扫描头: 扫描超时。", "正确");
			   handlerTimeOut.sendEmptyMessage(5);
			   return;
		  }
    	   logSysDetails("下扫描头: " + msg, "");
		   reScanTimes=1;
		   if(lots.contains(msg)){
			   logSysDetails("重复扫描"+msg, "");
		   }
		   else{
		   lots=lots+msg+",";
		   commitToMes(new  String[]{resourceid,resourcename,useid,usename,msg});
		   }
		   
     }
  }


   //超时处理
	private Handler handlerTimeOut = new Handler() {

		@Override
	   public void handleMessage(Message msg) {
			switch (msg.what) {
			case 4: // 上扫描超时
				if (reScanTimes < SCAN_RESCAN) {
					logSysDetails( "上扫描头第 " + reScanTimes + " 次重新扫描","正确");
					startscan(serialport_2);
					reScanTimes++;
				} else {
					logSysDetails("上扫描头第2次扫描失败", "正确");
					reScanTimes = 1;
					//scanResultToPLC(false, "RailA", false);
					 if(num==maxnum-1){
						   dataOutput(serialport_1, "@END:\r\n");
					   }
					   else{
					   num++;
					   SystemClock.sleep(200);
					   dataOutput(serialport_1, dots[num]);
					   }
				}
				break;
				
			case 5: // 下扫描超时
				if (reScanTimes <SCAN_RESCAN) {
					logSysDetails( "下扫描头第 " + reScanTimes + " 次重新扫描","正确");
					startscan(serialport_3);
					reScanTimes++;
				} else {

					logSysDetails("下扫描头第2次扫描失败", "正确");
					reScanTimes = 1;
					if(num==maxnum-1){
						   dataOutput(serialport_1, "@END:\r\n");
					   }
					   else{
					   num++;
					   SystemClock.sleep(200);
					   dataOutput(serialport_1, dots[num]);
					   }
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
		
		
		public   String  getcoordinate(){
			String coordinate="";
			if(box11.isChecked()){
				String str=zuobiao11.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@1"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@1"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box12.isChecked()){
				String str=zuobiao12.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@1"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@1"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box13.isChecked()){
				String str=zuobiao13.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@1"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@1"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box14.isChecked()){
				String str=zuobiao14.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@1"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@1"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			
			if(box21.isChecked()){
				String str=zuobiao21.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@2"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@2"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box22.isChecked()){
				String str=zuobiao22.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@2"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@2"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box23.isChecked()){
				String str=zuobiao23.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@2"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@2"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box24.isChecked()){
				String str=zuobiao24.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@2"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@2"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			
			if(box31.isChecked()){
				String str=zuobiao31.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@3"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@3"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box32.isChecked()){
				String str=zuobiao32.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@3"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@3"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box33.isChecked()){
				String str=zuobiao33.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@3"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@3"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box34.isChecked()){
				String str=zuobiao34.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@3"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@3"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			
			if(box41.isChecked()){
				String str=zuobiao41.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@4"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@4"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box42.isChecked()){
				String str=zuobiao42.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@4"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@4"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box43.isChecked()){
				String str=zuobiao43.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@4"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@4"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			if(box44.isChecked()){
				String str=zuobiao14.getText().toString().trim();
				if(str.equals("")||str.length()!=6){
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				else  if(str.startsWith("+")){
					str="@4"+str.substring(1)+"00000,";
					coordinate=coordinate+str;
				}
				else  if(str.startsWith("-")){
					str="@4"+"00000"+str.substring(1)+",";
					coordinate=coordinate+str;
				}
				else{
					Log.i(TAG,box11+"坐标配置错误");
					canScan=false;
				}
				
			}
			
			return  coordinate;
		}
      
		
		private void commitToMes(String[] par) {
			
			String[] p = par.clone();
			Task mScan = new Task(this, TaskType.SMT_SC, p);
			progressDialog.setMessage("SMT贴片扫描");
			ClearshowInfo(); // 大于100行清除。
			showProDia();
			smtTPModel.tiePian(mScan);
		}
		
	  private void commitToMes2(String[] par) {
			
			String[] p = par.clone();
			Task mScan = new Task(this, TaskType.smttiepianrengong, p);
			progressDialog.setMessage("SMT贴片扫描");
			ClearshowInfo(); // 大于100行清除。
			showProDia();
			smtTPModel.tiePian(mScan);
	 }
	  
	  private  void  shezhijiemianbyzuobiao(String zuobiao){
		  String[] pai=zuobiao.split("\\|");
		  for(int x=0;x<pai.length;x++){
			  if(x==0){
				  String[] zuobiaos= pai[x].split(",");
					 int dianshu=zuobiaos.length;
					 if(dianshu==0){
						 
					 }
					 else  if(dianshu==1){
						 box11.setChecked(true);
						 zuobiao11.setText(zuobiaos[0]);
					 }
					 else  if(dianshu==2){
						 box11.setChecked(true);
						 zuobiao11.setText(zuobiaos[0]);
						 box12.setChecked(true);
						 zuobiao12.setText(zuobiaos[1]);
					 }
					 else  if(dianshu==3){
						 box11.setChecked(true);
						 zuobiao11.setText(zuobiaos[0]);
						 box12.setChecked(true);
						 zuobiao12.setText(zuobiaos[1]);
						 box13.setChecked(true);
						 zuobiao13.setText(zuobiaos[2]);
						 
					 }
					 else{
						 box11.setChecked(true);
						 zuobiao11.setText(zuobiaos[0]);
						 box12.setChecked(true);
						 zuobiao12.setText(zuobiaos[1]);
						 box13.setChecked(true);
						 zuobiao13.setText(zuobiaos[2]); 
						 box14.setChecked(true);
						 zuobiao14.setText(zuobiaos[3]); 
					 }
			  }
			 
			  if(x==1){
				  String[] zuobiaos= pai[x].split(",");
					 int dianshu=zuobiaos.length;
					 if(dianshu==0){
						 
					 }
					 else  if(dianshu==1){
						 box21.setChecked(true);
						 zuobiao21.setText(zuobiaos[0]);
					 }
					 else  if(dianshu==2){
						 box21.setChecked(true);
						 zuobiao21.setText(zuobiaos[0]);
						 box22.setChecked(true);
						 zuobiao22.setText(zuobiaos[1]);
					 }
					 else  if(dianshu==3){
						 box21.setChecked(true);
						 zuobiao21.setText(zuobiaos[0]);
						 box22.setChecked(true);
						 zuobiao22.setText(zuobiaos[1]);
						 box23.setChecked(true);
						 zuobiao23.setText(zuobiaos[2]);
						 
					 }
					 else{
						 box21.setChecked(true);
						 zuobiao21.setText(zuobiaos[0]);
						 box22.setChecked(true);
						 zuobiao22.setText(zuobiaos[1]);
						 box23.setChecked(true);
						 zuobiao23.setText(zuobiaos[2]); 
						 box24.setChecked(true);
						 zuobiao24.setText(zuobiaos[3]); 
					 }
			  }
			  if(x==2){
				  String[] zuobiaos= pai[x].split(",");
					 int dianshu=zuobiaos.length;
					 if(dianshu==0){
						 
					 }
					 else  if(dianshu==1){
						 box31.setChecked(true);
						 zuobiao31.setText(zuobiaos[0]);
					 }
					 else  if(dianshu==2){
						 box31.setChecked(true);
						 zuobiao31.setText(zuobiaos[0]);
						 box32.setChecked(true);
						 zuobiao32.setText(zuobiaos[1]);
					 }
					 else  if(dianshu==3){
						 box31.setChecked(true);
						 zuobiao31.setText(zuobiaos[0]);
						 box32.setChecked(true);
						 zuobiao32.setText(zuobiaos[1]);
						 box33.setChecked(true);
						 zuobiao33.setText(zuobiaos[2]);
						 
					 }
					 else{
						 box31.setChecked(true);
						 zuobiao31.setText(zuobiaos[0]);
						 box32.setChecked(true);
						 zuobiao32.setText(zuobiaos[1]);
						 box33.setChecked(true);
						 zuobiao33.setText(zuobiaos[2]); 
						 box34.setChecked(true);
						 zuobiao34.setText(zuobiaos[3]); 
					 }
			  }
			  if(x==3){
				  String[] zuobiaos= pai[x].split(",");
					 int dianshu=zuobiaos.length;
					 if(dianshu==0){
						 
					 }
					 else  if(dianshu==1){
						 box41.setChecked(true);
						 zuobiao41.setText(zuobiaos[0]);
					 }
					 else  if(dianshu==2){
						 box41.setChecked(true);
						 zuobiao41.setText(zuobiaos[0]);
						 box42.setChecked(true);
						 zuobiao42.setText(zuobiaos[1]);
					 }
					 else  if(dianshu==3){
						 box41.setChecked(true);
						 zuobiao41.setText(zuobiaos[0]);
						 box42.setChecked(true);
						 zuobiao42.setText(zuobiaos[1]);
						 box43.setChecked(true);
						 zuobiao43.setText(zuobiaos[2]);
						 
					 }
					 else{
						 box41.setChecked(true);
						 zuobiao41.setText(zuobiaos[0]);
						 box42.setChecked(true);
						 zuobiao42.setText(zuobiaos[1]);
						 box43.setChecked(true);
						 zuobiao43.setText(zuobiaos[2]); 
						 box44.setChecked(true);
						 zuobiao44.setText(zuobiaos[3]); 
					 }
			  }
			 
		  }
	  }
		
}
