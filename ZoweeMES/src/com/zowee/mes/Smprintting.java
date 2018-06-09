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
import java.util.TooManyListenersException;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
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
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SmprinttingModel;
import com.zowee.mes.model.SmtTPModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class Smprintting extends CommonActivity implements  OnClickListener,OnKeyListener,OnItemSelectedListener
		 {
	/***
	 * 华技产品的专用的 锡膏印刷2小时  管控！
	 * 单轨道！
	 * */
	
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
	
	private  Spinner  spinner;
	private  String  abside;
	private  EditText  numedit;//数量统计
	
	private EditText sn;
    private  String  lastlotsn="";
    
    private String[] paras=new String[7];
	
	private Button okbutton;
	private Button cancelbutton;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
	

	private static EditText editscan;

	private  Common4dModel  common4dmodel;
	private  SmprinttingModel  smprinttingModel;
	private static final String TAG = "Smprintting";
	
	private boolean canScan = false; // 标记是否确认设置，未确认则不进行扫描等操作
	private boolean manualScan = true; // 标记手动扫描
	
	private static int reScanTimes = 1;//上下扫描头重扫次数
	
	private  SerialPort  serialport_1;
	private  SerialPort  serialport_2;
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
		setContentView(R.layout.activity_smtprinting);
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
								stopService(new Intent(Smprintting.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
    
	private void unRegisterPort() {
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
	}
	
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.GetResourceId; // 后台服务静态整形常量
		super.init();

		common4dmodel=new  Common4dModel();
		smprinttingModel=new SmprinttingModel();
		
		
		editMO = (EditText) findViewById(R.id.smtprinting_moedit);
		editMOdescri = (EditText) findViewById(R.id.smtprinting_modescription);
		editMOProduct = (EditText) findViewById(R.id.smtprinting_moproduct);
		editMO.requestFocus();
		
		
		spinner=(Spinner) findViewById(R.id.smtprinting_spinner);
		String []  absides={"A","B"};
		ArrayAdapter<String>  adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, absides);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		numedit=(EditText) findViewById(R.id.smtprinting_numedit);
        
		sn=(EditText) findViewById(R.id.smtprintting_sn);

		cancelbutton = (Button) findViewById(R.id.smtprintting_cancelbutton);
		okbutton = (Button) findViewById(R.id.smtprinting_okbutton);

		editscan = (EditText) findViewById(R.id.smtprintting_editscan);
		editscan.setFocusable(false);
		
		

		okbutton.setOnClickListener(this);
		cancelbutton.setOnClickListener(this);
        
		editMO.setOnKeyListener(this);
		sn.setOnKeyListener(this);
		spinner.setOnItemSelectedListener(this);
		
        common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));
        try {
			serialport_1 = registerPort("/dev/ttyS1");
			serialport_2 = registerPort("/dev/ttyS2");
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
 					
					if (canScan) {
						//String msg=readMessage();
						//logSysDetails("串口1读到的数据是："+msg, "串口");
						Message.obtain(mHandler2, 1, readMessage()).sendToTarget();
						//Toast.makeText(SmtTP.this, who+"读到的数据是"+msg, Toast.LENGTH_LONG).show();
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
 	//开始扫描的方法
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
 

	public static void ClearshowInfo() {
		if (editscan.getLineCount() > 30)
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
		case R.id.smtprintting_cancelbutton:
			editMO.setEnabled(true);
			okbutton.setEnabled(true);
			sn.setEnabled(true);
			spinner.setEnabled(true);

			canScan=false;
			manualScan=true;
			logSysDetails("点击了【重新设定按钮】，不接受接驳台发送的信息，设定完成点击【ok按钮】", "正确");
			break;
			
		case R.id.smtprinting_okbutton:
			editMO.setEnabled(false);
			okbutton.setEnabled(false);
			spinner.setEnabled(false);
			canScan=true;
			manualScan=false;
			logSysDetails("ok！开始准备接受接驳台的指令！你选择的工单的是："+moname+",选择的A/B面是："+abside, "ok");
			okbutton.requestFocus();
			sn.setEnabled(false);
			paras[0]=resourceid;
			paras[1]=resourcename;
			paras[2]=useid;
			paras[3]=usename;
			
			paras[4]=moname;
			paras[5]=abside;
			
			break;
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.smtprinting_moedit:
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
		case R.id.smtprintting_sn:
			String param1 = sn.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keyCode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(param1.length()<8){
					Toast.makeText(this, "批次号长度不对", Toast.LENGTH_SHORT).show();
				}
				else{
					paras[0]=resourceid;
					paras[1]=resourcename;
					paras[2]=useid;
					paras[3]=usename;
					
					paras[4]=moname;
					paras[5]=abside;
					paras[6]=param1;
					super.progressDialog.setMessage("正在手动扫描提交...");
					super.showProDia();
					smprinttingModel.printtingscan(new Task(Smprintting.this,TaskType.smtprintting,paras));
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
					editMO.setEnabled(false);
					String scantext = "通过批次号：[" + lotsn + "]成功的获得工单:"
							+ moname + ",工单id:"+moid+",产品信息:" + productdescri + ",产品料号："
							+ material + "!";
					logSysDetails(scantext, "成功");
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
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
		//mes  返回信息
		case TaskType.smtprintting:
			super.closeProDia();
			sn.setText("");
			//Toast.makeText(this, "批次号长度不对" + task.getTaskResult() + "chenyun", Toast.LENGTH_SHORT).show();
			if (null != task.getTaskResult()) {
				ClearshowInfo();
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						//发送0  失败 1表示  成功  2轨道滚动  3报警  4复位
						if(canScan){
						dataOutput(serialport_1, "2\r\n");
						  //logSysDetails("串口1写的数据是：2\r\n", "成功");
						}
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						if(scantext.contains("过站的总数为:")){
							String num=scantext.substring(scantext.indexOf("过站的总数为:")+"过站的总数为:".length(),scantext.length());
							numedit.setText(num);
						}
					}
					else{
						//扫描失败，要求是停线报警
						if(canScan){
						dataOutput(serialport_1, "310\r\n");
						  //logSysDetails("串口1写的数据是：310\r\n", "成功");
						}
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setTitle("-----印刷扫描失败，请注意！-----");
						builder.setMessage("     请仔细核对信息!"+getdata.get("I_ReturnMessage"));
						builder.setPositiveButton("确定",null);
						builder.setNegativeButton("取消", null);
						builder.create().show();
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
				logSysDetails("提交MES失败请检查网络，请检查输入的条码_2", "成功");
			}

			break;
		}
	}
	
	// 串口1读到数据发送到主线程 mhandler
	private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String messsge=msg.obj.toString();
				//logSysDetails("PLC指令是"+messsge, "红");
				if(messsge.contains("1")){
					//dataOutput(serialport_2, new  String(HexString2Bytes()));
					startscan(serialport_2);
					//logSysDetails("串口2开启扫描", "成功");
				}
				super.handleMessage(msg);
			}
	};
    
	//串口2读到数据发送到主线程 mhandler2
	private Handler mHandler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String messsge=msg.obj.toString();
			
			if(messsge.contains(SCAN_TIMEOUT_COMMAND)){
				logSysDetails("扫描超时:", "红");
				handlerTimeOut.sendEmptyMessage(4);
				//假如扫描枪读到“Noread”表示扫描超时
				
			}
			else{
				logSysDetails("扫描:"+messsge, "扫描");
				if(messsge.equals(lastlotsn)){
					//重复扫描不提交
				}else{
					lastlotsn=messsge;
					paras[6]=messsge;
					smprinttingModel.printtingscan(new Task(Smprintting.this,TaskType.smtprintting,paras));
					progressDialog.setMessage("正在提交中....");
					progressDialog.show();
				}
				
			}
			super.handleMessage(msg);
		}
   };
		


   //超时处理
	private Handler handlerTimeOut = new Handler() {
		@Override
	   public void handleMessage(Message msg) {
			
			 // 上扫描超时
				if (reScanTimes <= SCAN_RESCAN) {
					logSysDetails( "扫描头第 " + reScanTimes + " 次重新扫描","正确");
					startscan(serialport_2);
					logSysDetails("串口2开启扫描", "成功");
					reScanTimes++;
				} else {
					logSysDetails("扫描失败", "正确");
					reScanTimes = 1;
				    SystemClock.sleep(200);
	
				}
			super.handleMessage(msg);
		}
	};
	  
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		 abside=(String) arg0.getItemAtPosition(arg2);
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
