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
import java.util.Random;
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

import android.R.layout;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.Window;
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
import com.zowee.mes.model.SMTSCNewModel;
import com.zowee.mes.model.SmtFvmiModel;
import com.zowee.mes.model.SmtTPModel;
import com.zowee.mes.model.SntocarModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class SMTTP_New extends CommonActivity implements  OnKeyListener,OnItemSelectedListener
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString().trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();

	private  Button snbtn1,snbtn2,snbtn3,snbtn4,snbtn5,snbtn6,snbtn7,snbtn8,snbtn9,snbtn10,snbtn11,snbtn12;
	private Button[] snbtns;
	private  int up_count=0;
	private  int down_count=0;
	
	private EditText sn;
	private Button button;
	private EditText editscan;
	

	private  Common4dModel  common4dmodel;
	private  SmtTPModel  smtTPModel;
	private static final String TAG = "SMTTP_New";
	
    private  boolean  autoscan=false;
    private  boolean  autoscan_iscaning=false;
	private int AreScanTimes = 1;//����ɨ��ͷ��ɨ����
	private int BreScanTimes = 1;//����ɨ��ͷ��ɨ����
	
	private  SerialPort  serialport_1;
    private  SerialPort  serialport_2;
    private  SerialPort  serialport_3;
   // ============== SharedPreferences default setting ======================
 	public static String SCAN_COMMAND = "1B31"; // ɨ��ǹɨ��ָ��
 	public static String SCAN_TIMEOUT_COMMAND = "Noread"; // ɨ���ж�ָ��
 	public static int SCAN_RESCAN = 2;
 	public static String OWNER = "system";
 	// ============== SharedPreferences default setting end ==================

 	
 	private Spinner spinner_onePCBQTY;//��������ѡ��
 	private Spinner spinner_A_PCBSide;//AB��
 	private int OnePCBQty;//��������
 	private String[] params = new String[8];//��Ų���
 	private String ABSide;
 	private SMTSCNewModel smtScModel;
 	
 	
 	private  Spinner  up_or_down_spinner;
 	private  String  up_or_down;
 	private  View  clearlayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_smttp_new);
		//setDefaultValues();
		init();
	}

	public void onBackPressed() {
		killMainProcess();
	}
    
	// ���˼��ص����activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("ȷ���˳�����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								
								unRegisterPort();
								stopService(new Intent(SMTTP_New.this,
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
	
	public  void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.GetResourceId; // ��̨����̬���γ���
		super.init();

		common4dmodel=new  Common4dModel();
		smtTPModel=new SmtTPModel();
		smtScModel = new SMTSCNewModel();
		
		up_or_down_spinner = (Spinner) findViewById(R.id.smttp_new_banbian_upordown);
		String[] str2 = new String[] { "��", "��" };
		ArrayAdapter adapter2 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, str2);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		up_or_down_spinner.setAdapter(adapter2);
		up_or_down_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				up_or_down=(String) arg0.getItemAtPosition(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		clearlayout=findViewById(R.id.smttp_new_banbian_clearfous);
		clearlayout.requestFocus();
		
		sn=(EditText) findViewById(R.id.smttp_new_sn);
		button = (Button) findViewById(R.id.smttp_new_btn);
		button.setOnClickListener(new  View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!autoscan){
					autoscan=true;
					button.setText("�ѽ����Զ�ɨ��ģʽ����");
				    button.setEnabled(false);
				    sn.setEnabled(false);
				    spinner_onePCBQTY.setEnabled(false);
				    spinner_A_PCBSide.setEnabled(false);
				}
			}
		});
		editscan = (EditText) findViewById(R.id.smttp_new_editscan);
		editscan.setFocusable(false);
		
		snbtns=new Button[]{snbtn1,snbtn2,snbtn3,snbtn4,snbtn5,snbtn6,snbtn7,snbtn8,snbtn9,snbtn10,snbtn11,snbtn12};
		int[] btnids={R.id.smttp_new_snbtn1,R.id.smttp_new_snbtn2,R.id.smttp_new_snbtn3,R.id.smttp_new_snbtn4,
				R.id.smttp_new_snbtn5,R.id.smttp_new_snbtn6,R.id.smttp_new_snbtndown1,R.id.smttp_new_snbtndown2,
				R.id.smttp_new_snbtndown3,R.id.smttp_new_snbtndown4,R.id.smttp_new_snbtndown5,R.id.smttp_new_snbtndown6};
		for(int x=0;x<snbtns.length; x++){
			snbtns[x]=(Button) findViewById(btnids[x]);
		}
		setallbutton_invisible();
		sn.setOnKeyListener(this);
		
		ArrayAdapter<CharSequence> OnePCBQtyAdapter = ArrayAdapter
				.createFromResource(this, R.array.SMTFirstScanPCBQuantity,
						android.R.layout.simple_spinner_item);
		OnePCBQtyAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_onePCBQTY = (Spinner) findViewById(R.id.spinner_onePCBQTY1);
		spinner_onePCBQTY.setAdapter(OnePCBQtyAdapter);
		spinner_onePCBQTY.setSelection(5);
		spinner_onePCBQTY.setOnItemSelectedListener(this);
		params[7] = 6 + "";
		
		ArrayAdapter<CharSequence> SMTStickPCBSide = ArrayAdapter
				.createFromResource(this, R.array.SMTStickPCBSide,
						android.R.layout.simple_spinner_item);
		SMTStickPCBSide
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_A_PCBSide = (Spinner) findViewById(R.id.spinner_A_PCBSide_bb);
		spinner_A_PCBSide.setAdapter(SMTStickPCBSide);
		spinner_A_PCBSide.setSelection(0);
		spinner_A_PCBSide.setOnItemSelectedListener(this);
		params[5] = "A";
				
        common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));
        try {
			
			serialport_2 = registerPort("/dev/ttyS2");
			serialport_1 = registerPort("/dev/ttyS1");
			serialport_3 = registerPort("/dev/ttyS3");
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "ע�ᴮ��ʧ��", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private  void  setallbutton_invisible(){
		for(Button b :snbtns)
			b.setVisibility(View.INVISIBLE);
	}
	
	private  void  setbutton_visible(int num,String sn){
		snbtns[num].setVisibility(View.VISIBLE);
		snbtns[num].setText(sn);
		snbtns[num].setBackgroundResource(choosecorlor(Math.random()*10));
		
	}
	private  int choosecorlor(double num){
		int corlorid;
		if(num>=0&&num<3)
			corlorid=R.color.gold;
		else if(num>=3&&num<6){
			corlorid=R.color.red;
		}
		else
			corlorid=R.color.white;
		
		return  corlorid;
		  
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
				Toast.makeText(this, "����ע��������ִ�У��õ�������ʧ��", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(this, "����ע������쳣", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
			Toast.makeText(this, "����ע������쳣", Toast.LENGTH_SHORT).show();
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			Toast.makeText(this, "����ע������쳣", Toast.LENGTH_SHORT).show();
		}
		return serialPort;
	}
   
   
 //���ڼ���
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
				
					if (autoscan) {
						//"@USCAN:\r\n"or"@DSCAN:\r\n
						//String msg=readMessage();
						//logSysDetails("����1�����������ǣ�"+msg, "����");
						Message.obtain(mHandler, 1, readMessage()).sendToTarget();
						//Toast.makeText(SmtTP.this, who+"������������"+msg, Toast.LENGTH_LONG).show();
					} 
 					else {
						clearBuff();
					}
				} 
 				else if ("/dev/ttyS2".equals(who)) {
						Message.obtain(mHandler, 2, readMessage())
								.sendToTarget();
				} 
 			    else if ("/dev/ttyS3".equals(who)) {
						Message.obtain(mHandler, 3, readMessage())
								.sendToTarget();
				}
			
 			break;
 			}
 		}
 	 
 		private void clearBuff() {
 			// ������Ϣ����ջ���
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
 					//ԭ��50ms���������ڵ��Թ��� ���ֶ���ָ�ȫ
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
			logSysDetails(portName+"д�������ǣ�"+out, "����");
			dataOutput.flush();
			//Log.i(TAG,portName+"д��������:"+out );
			//Toast.makeText(this, portName+"����д���ݳɹ���д��������"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			//,,,
			Toast.makeText(this, "����д����ʧ��������ִ�У�", Toast.LENGTH_SHORT).show();
		}
	}
 	
 	private void startscan(SerialPort portName) {
		try {
		   OutputStream	dataOutput = portName.getOutputStream();
			dataOutput.write(HexString2Bytes());
			dataOutput.flush();
			//Toast.makeText(this, portName+"����д���ݳɹ���д��������"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "����д����ʧ��������ִ�У�", Toast.LENGTH_SHORT).show();
		}
	}
 	
	public void ClearshowInfo() {
		if (editscan.getLineCount() > 20)
			editscan.setText("");
	}
	
	/**
	 * ʮ�������ַ���תByte[]
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
	
	

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.smttp_new_sn:
			String param1 = sn.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keyCode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(param1.length()<8){
					Toast.makeText(this, "���κų��Ȳ���", Toast.LENGTH_SHORT).show();
				}
				else{
					super.progressDialog.setMessage("���ڻ�ȡ...");
					super.showProDia();
					commitToMes2(new String[]{resourceid,resourcename,useid,usename,param1});
				}
			}
			break;
	   }
		return false;
	}
	/*
	 * ˢ��UI����
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		/**
		 * ��������ύ�������ķ��صĽ������UI����ĸ��£���
		 * 
		 * */
		switch (task.getTaskType()) {
		// ��ȡ����ID
		case TaskType.common4dmodelgetresourceid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("ResourceId")) {
						resourceid = getdata.get("ResourceId");
					}
					String logText = "����������!��⵽��ƽ�����Դ����:[ " + resourcename
							+ " ],��ԴID: [ " + resourceid + " ],�û�ID: [ " + useid + " ]!!�����������������������2�֣���";
					logSysDetails(logText, "����");
				} else {
					logSysDetails(
							"ͨ����Դ���ƻ�ȡ��MES��ȡ��ԴIDʧ�ܣ��������õ���Դ�����Ƿ���ȷ", "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Դid��Ϣʧ�ܣ�������������Դ�����Ƿ���ȷ", "�ɹ�");
			}
		break;
		
		case TaskType.smttp_new_upauto:
			super.closeProDia();
			ClearshowInfo();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "��Ƭ�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						dataOutput(serialport_1,"@1:\r\n");
						SystemClock.sleep(50);
					
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						
						if(getdata.get("I_ReturnMessage").contains("���㵥λ����")||getdata.get("I_ReturnMessage").contains("����"))
						{
							
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("����©ɨ�裬��ע�⣡");
							builder.setMessage("    ��˶�������Ϣ��"+scantext);
							builder.setPositiveButton("ȷ��",null);
							builder.setNegativeButton("ȡ��", null);
							builder.create().show();
							dataOutput(serialport_1,"@0:\r\n");
						}
							
						else 
							dataOutput(serialport_1,"@0:\r\n");
						SystemClock.sleep(50);
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
					dataOutput(serialport_1,"@0:\r\n");
					SystemClock.sleep(50);
				}
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
				dataOutput(serialport_1,"@0:\r\n");
				SystemClock.sleep(50);
			}
			break;
			
		case TaskType.smttp_new_downauto:
			super.closeProDia();
			ClearshowInfo();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "��Ƭ�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						dataOutput(serialport_1,"@3:\r\n");
						SystemClock.sleep(50);
						
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						
						if(getdata.get("I_ReturnMessage").contains("���㵥λ����"))
						{
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("����©ɨ�裬��ע�⣡2");
							builder.setMessage("    ��˶�������Ϣ��"+scantext);
							builder.setPositiveButton("ȷ��",null);
							builder.setNegativeButton("ȡ��", null);
							builder.create().show();
							dataOutput(serialport_1,"@2:\r\n");
						}
						else{
							dataOutput(serialport_1,"@2:\r\n");
						}
						SystemClock.sleep(50);
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
					dataOutput(serialport_1,"@2:\r\n");
					SystemClock.sleep(50);
					
				}
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
				dataOutput(serialport_1,"@2:\r\n");
				SystemClock.sleep(50);
				
			}
			break;
			

		case 100:
			super.closeProDia();
			ClearshowInfo();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "��Ƭ�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						dataOutput(serialport_1,"@1:\r\n");
						SystemClock.sleep(50);
					
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						
						if(getdata.get("I_ReturnMessage").contains("���㵥λ����"))
						{
							
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("����©ɨ�裬��ע�⣡");
							builder.setMessage("    ��˶�������Ϣ��"+scantext);
							builder.setPositiveButton("ȷ��",null);
							builder.setNegativeButton("ȡ��", null);
							builder.create().show();
							dataOutput(serialport_1,"@0:\r\n");
						}
							
						else 
							dataOutput(serialport_1,"@0:\r\n");
						SystemClock.sleep(50);
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
					dataOutput(serialport_1,"@0:\r\n");
					SystemClock.sleep(50);
				}
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
				dataOutput(serialport_1,"@0:\r\n");
				SystemClock.sleep(50);
			}
			break;
			
		case 200://�ϰ��
			super.closeProDia();
			ClearshowInfo();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "��Ƭ�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						dataOutput(serialport_1,"@3:\r\n");
						SystemClock.sleep(50);
						
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						
						if(getdata.get("I_ReturnMessage").contains("���㵥λ����"))
						{
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("����©ɨ�裬��ע�⣡2");
							builder.setMessage("    ��˶�������Ϣ��"+scantext);
							builder.setPositiveButton("ȷ��",null);
							builder.setNegativeButton("ȡ��", null);
							builder.create().show();
							dataOutput(serialport_1,"@2:\r\n");
						}
						else{
							dataOutput(serialport_1,"@2:\r\n");
						}
						SystemClock.sleep(50);
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
					dataOutput(serialport_1,"@2:\r\n");
					SystemClock.sleep(50);
					
				}
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
				dataOutput(serialport_1,"@2:\r\n");
				SystemClock.sleep(50);
				
			}
			break;
			
		case TaskType.smttiepianrengong:
			super.closeProDia();
			ClearshowInfo();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "�ֶ�ɨ����Ƭ�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "�ֶ�ɨ��ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				sn.requestFocus();
				sn.setText("");
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
			
			
		case TaskType.smttp_new_banbian:
			super.closeProDia();
			ClearshowInfo();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "��Ƭ�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						if(up_or_down.equals("��"))
							dataOutput(serialport_1,"@1:\r\n");
						else
							dataOutput(serialport_1,"@3:\r\n");
						SystemClock.sleep(50);
						
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						
						if(getdata.get("I_ReturnMessage").contains("���㵥λ����"))
						{
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("����©ɨ�裬��ע�⣡2");
							builder.setMessage("    ��˶�������Ϣ��"+scantext);
							builder.setPositiveButton("ȷ��",null);
							builder.setNegativeButton("ȡ��", null);
							builder.create().show();
							
							if(up_or_down.equals("��"))
								dataOutput(serialport_1,"@0:\r\n");
							else
								dataOutput(serialport_1,"@2:\r\n");
						}
						else{
							if(up_or_down.equals("��"))
								dataOutput(serialport_1,"@0:\r\n");
							else
								dataOutput(serialport_1,"@2:\r\n");
						}
						SystemClock.sleep(50);
						
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
					dataOutput(serialport_1,"@2:\r\n");
					SystemClock.sleep(50);
					
				}
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
				dataOutput(serialport_1,"@2:\r\n");
				SystemClock.sleep(50);
				
			}
			
		break;
			
			
			
					
		}
	}
	  // ��Ϣ��������UI
	 private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String message=msg.obj.toString();
				switch (msg.what) {
				case 1:
					     logSysDetails("ZOWEE������ָ�"+message, "ָ��");
						//@USCAN:\r\n"or"@DSCAN:\r\n
						 if(message.contains("@RUN")){
								autoscan_iscaning=true;
						 }
					     else if(autoscan_iscaning&&message.contains("@USCAN")){
							startscan(serialport_2);
						}
						 else if(autoscan_iscaning&&message.contains("@DSCAN")){
							startscan(serialport_3);
						}
						 else if(autoscan_iscaning&&message.contains("@URE")){
								dataOutput(serialport_1,"@1:\r\n");
								SystemClock.sleep(50);
						}
						 else if(autoscan_iscaning&&message.contains("@DRE")){
							 dataOutput(serialport_1,"@3:\r\n");
							 SystemClock.sleep(50);
						 }
						 else if(message.contains("@END")){
							setallbutton_invisible();
							up_count=0;
							down_count=0;
							autoscan_iscaning=false;
						}
						
					
					
					break;
				case 2:
					 if (message.contains(SCAN_TIMEOUT_COMMAND)) {
						   //logSysDetails("��ɨ��ͷ: ��һ��ɨ��ʧ�ܣ�", "��ȷ");
						   handlerTimeOut.sendEmptyMessage(4);
						   return;
					  }
					  logSysDetails("��ɨ��ͷ: " + message, "");
					  setbutton_visible(up_count, message);
					  up_count++;
						if(up_count>5)
							up_count=0;
					  commitToMes(new  String[]{resourceid,resourcename,useid,usename,message},TaskType.smttp_new_upauto);
				break;
			    case 3:
				 if (message.contains(SCAN_TIMEOUT_COMMAND)) {
					   //logSysDetails("��ɨ��ͷ: ��һ��ɨ��ʧ�ܣ�", "��ȷ");
					   handlerTimeOut.sendEmptyMessage(5);
					   return;
				  }
				  logSysDetails("��ɨ��ͷ: " + message, "");
				  setbutton_visible(down_count+6, message);
				  down_count++;
					if(down_count>5)
					 down_count=0;
				  commitToMes(new  String[]{resourceid,resourcename,useid,usename,message},TaskType.smttp_new_downauto);
			      break;
			}
				super.handleMessage(msg);
			}
	};


   //��ʱ����
	private Handler handlerTimeOut = new Handler() {
		@Override
	   public void handleMessage(Message msg) {
			switch (msg.what) {
			case 4: // ��ɨ�賬ʱ
				if (AreScanTimes < SCAN_RESCAN) {
					logSysDetails( "��ɨ��ͷ�� " + AreScanTimes + " ������ɨ��","��ȷ");
					startscan(serialport_2);
					AreScanTimes++;
				} else {
					logSysDetails("��ɨ��ͷ��2��ɨ��ʧ��", "��ȷ");
					dataOutput(serialport_1,"@0:\r\n");
					SystemClock.sleep(50);
					AreScanTimes = 1;
	
				}
				break;
				
			case 5: // ��ɨ�賬ʱ
				if (BreScanTimes <SCAN_RESCAN) {
					logSysDetails( "��ɨ��ͷ�� " + BreScanTimes + " ������ɨ��","��ȷ");
					startscan(serialport_3);
					BreScanTimes++;
				} else {
					logSysDetails("��ɨ��ͷ��2��ɨ��ʧ��", "��ȷ");
					dataOutput(serialport_1,"@2:\r\n");
					SystemClock.sleep(50);
					BreScanTimes = 1;
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
		
		private void commitToMes(String[] par,int  tasktype) {
			String[] p = par.clone();
		
		
			
			if(tasktype==TaskType.smttp_new_upauto){
				if (p[4].substring(0, 2).equalsIgnoreCase("MO")  ) {
					params[0] = useid;
					params[1] = "1";
					params[2] = resourcename;
					params[3] = p[4];
					params[4] = "Rail" + params[5];
					params[6] = "";
					params[7] = OnePCBQty+"";
					Task mScan = new Task(this, 100, params);
					progressDialog.setMessage("SMT��Ƭ���ɨ��");
					showProDia();
					smtScModel.SMTSC_New(mScan);
					return;
				}
			
				Task mScan = new Task(this, tasktype, p);
				progressDialog.setMessage("SMT��Ƭɨ��");
				showProDia();
				smtTPModel.tiePianup(mScan);
			}
			else{
				if (p[4].substring(0, 2).equalsIgnoreCase("MO")  ) {
					params[0] = useid;
					params[1] = "1";
					params[2] = resourcename;
					params[3] = p[4];
					params[4] = "Rail" + params[5];
					params[6] = "";
					params[7] = OnePCBQty+"";
					Task mScan = new Task(this, 200, params);
					progressDialog.setMessage("SMT��Ƭ���ɨ��");
					showProDia();
					smtScModel.SMTSC_New(mScan);
					return;
				}
				Task mScan = new Task(this, tasktype, p);
				progressDialog.setMessage("SMT��Ƭɨ��");
				showProDia();
				smtTPModel.tiePiandown(mScan);
			}
			
		
			
		}
		
	   private void commitToMes2(String[] par) {
			
			String[] p = par.clone();
			
			Task mScan = new Task(this, TaskType.smttiepianrengong, p);
			progressDialog.setMessage("SMT��Ƭɨ�裬�˹��ύ");
			showProDia();
			smtTPModel.tiePianup(mScan);
	 }

	@Override
	public void onItemSelected(AdapterView<?> spinner, View arg1, int itemId,
			long arg3) {
			int id = spinner.getId();
			switch (id) {
			case R.id.spinner_onePCBQTY1:
				OnePCBQty = spinner_onePCBQTY.getSelectedItemPosition() + 1;

				params[7] = OnePCBQty + "";
				Log.e(TAG, "params[7] = " + params[7]);
			
				break;
			case R.id.spinner_A_PCBSide_bb:
				ABSide = spinner_A_PCBSide.
						getItemAtPosition(spinner_A_PCBSide.getSelectedItemPosition()).toString();

				params[5] = ABSide;
				Log.e(TAG, "params[5] = " + params[5]);
				break;
			default:
				break;
			}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void setDefaultValues() {
		SharedPreferences pref = getSharedPreferences("smtstick_setting",
				Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		if (!pref.contains("setting")) { // ��һ�����У���ʼ��Ĭ��ֵ
			editor.putBoolean("setting", false);
			editor.putString("owner", OWNER);
/*			editor.putString("scan_command", SCAN_COMMAND);
			editor.putString("scan_to_command", SCAN_TIMEOUT_COMMAND);
			editor.putInt("rescan", SCAN_RESCAN);
			editor.commit();
			if (DEBUG)
				Log.i(TAG, "editor commit");*/
		}

		OWNER = pref.getString("owner", OWNER);
/*		SCAN_COMMAND = pref.getString("scan_command", "");
		SCAN_TIMEOUT_COMMAND = pref.getString("scan_to_command", "");
		
		 * if (pref.getBoolean("addEnter", true)) { SCAN_TIMEOUT_COMMAND =
		 * SCAN_TIMEOUT_COMMAND + "\r\n"; }
		 
		SCAN_RESCAN = pref.getInt("rescan", 3);
		if (DEBUG)
			Log.i(TAG, "read setting");*/
	}
}
