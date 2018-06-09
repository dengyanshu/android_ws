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
	 * ������Ʒ��ר�õ� ����ӡˢ2Сʱ  �ܿأ�
	 * �������
	 * */
	
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private EditText editMO;
	private EditText editMOProduct;
	private EditText editMOdescri;
	private String moid; // ����ѡ�񱣴����
	private  String moname;
	
	private  Spinner  spinner;
	private  String  abside;
	private  EditText  numedit;//����ͳ��
	
	private EditText sn;
    private  String  lastlotsn="";
    
    private String[] paras=new String[7];
	
	private Button okbutton;
	private Button cancelbutton;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
	

	private static EditText editscan;

	private  Common4dModel  common4dmodel;
	private  SmprinttingModel  smprinttingModel;
	private static final String TAG = "Smprintting";
	
	private boolean canScan = false; // ����Ƿ�ȷ�����ã�δȷ���򲻽���ɨ��Ȳ���
	private boolean manualScan = true; // ����ֶ�ɨ��
	
	private static int reScanTimes = 1;//����ɨ��ͷ��ɨ����
	
	private  SerialPort  serialport_1;
	private  SerialPort  serialport_2;
 // ============== SharedPreferences default setting ======================
 	public static String SCAN_COMMAND = "1B31"; // ɨ��ǹɨ��ָ��
 	public static String SCAN_TIMEOUT_COMMAND = "Noread"; // ɨ���ж�ָ��
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
    
	// ���˼��ص����activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("ȷ���˳�����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								//�����޸�������ʱ�򣬱���
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
		super.TASKTYPE = TaskType.GetResourceId; // ��̨����̬���γ���
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
			Toast.makeText(this, "ע�ᴮ��ʧ��", Toast.LENGTH_SHORT).show();
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
				
					if (canScan) {
						//String msg=readMessage();
						//logSysDetails("����1�����������ǣ�"+msg, "����");
						Message.obtain(mHandler, 1, readMessage()).sendToTarget();
						//Toast.makeText(SmtTP.this, who+"������������"+msg, Toast.LENGTH_LONG).show();
					} else {
						clearBuff();
					}
				} 
 				else if ("/dev/ttyS2".equals(who)) {
 					
					if (canScan) {
						//String msg=readMessage();
						//logSysDetails("����1�����������ǣ�"+msg, "����");
						Message.obtain(mHandler2, 1, readMessage()).sendToTarget();
						//Toast.makeText(SmtTP.this, who+"������������"+msg, Toast.LENGTH_LONG).show();
					} else {
						clearBuff();
					}
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
 	//��ʼɨ��ķ���
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
 

	public static void ClearshowInfo() {
		if (editscan.getLineCount() > 30)
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
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.smtprintting_cancelbutton:
			editMO.setEnabled(true);
			okbutton.setEnabled(true);
			sn.setEnabled(true);
			spinner.setEnabled(true);

			canScan=false;
			manualScan=true;
			logSysDetails("����ˡ������趨��ť���������ܽӲ�̨���͵���Ϣ���趨��ɵ����ok��ť��", "��ȷ");
			break;
			
		case R.id.smtprinting_okbutton:
			editMO.setEnabled(false);
			okbutton.setEnabled(false);
			spinner.setEnabled(false);
			canScan=true;
			manualScan=false;
			logSysDetails("ok����ʼ׼�����ܽӲ�̨��ָ���ѡ��Ĺ������ǣ�"+moname+",ѡ���A/B���ǣ�"+abside, "ok");
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
					Toast.makeText(this, "���κų��Ȳ���", Toast.LENGTH_SHORT).show();
				}
				else{
					super.progressDialog.setMessage("���ڻ�ȡ����...");
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
					Toast.makeText(this, "���κų��Ȳ���", Toast.LENGTH_SHORT).show();
				}
				else{
					paras[0]=resourceid;
					paras[1]=resourcename;
					paras[2]=useid;
					paras[3]=usename;
					
					paras[4]=moname;
					paras[5]=abside;
					paras[6]=param1;
					super.progressDialog.setMessage("�����ֶ�ɨ���ύ...");
					super.showProDia();
					smprinttingModel.printtingscan(new Task(Smprintting.this,TaskType.smtprintting,paras));
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
		
		
		case TaskType.common4dmodelgetmobylotsn:
			super.closeProDia();
			String lotsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					
					moname = getdata.get("MOName");
					String productdescri = getdata.get("ProductDescription");
					String material = getdata.get("productName");
					moid = getdata.get("MOId");
					

					editMO.setText(moname);
					editMOdescri.setText(productdescri);
					editMOProduct.setText(material);
					editMO.setEnabled(false);
					String scantext = "ͨ�����κţ�[" + lotsn + "]�ɹ��Ļ�ù���:"
							+ moname + ",����id:"+moid+",��Ʒ��Ϣ:" + productdescri + ",��Ʒ�Ϻţ�"
							+ material + "!";
					logSysDetails(scantext, "�ɹ�");
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
				} else {
					logSysDetails(
							"ͨ�����κţ�[" + lotsn + "]��MES��ȡ������ϢΪ�ջ��߽������Ϊ�գ�����SN!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails(lotsn + "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
			}

			break;
		//mes  ������Ϣ
		case TaskType.smtprintting:
			super.closeProDia();
			sn.setText("");
			//Toast.makeText(this, "���κų��Ȳ���" + task.getTaskResult() + "chenyun", Toast.LENGTH_SHORT).show();
			if (null != task.getTaskResult()) {
				ClearshowInfo();
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						//����0  ʧ�� 1��ʾ  �ɹ�  2�������  3����  4��λ
						if(canScan){
						dataOutput(serialport_1, "2\r\n");
						  //logSysDetails("����1д�������ǣ�2\r\n", "�ɹ�");
						}
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						if(scantext.contains("��վ������Ϊ:")){
							String num=scantext.substring(scantext.indexOf("��վ������Ϊ:")+"��վ������Ϊ:".length(),scantext.length());
							numedit.setText(num);
						}
					}
					else{
						//ɨ��ʧ�ܣ�Ҫ����ͣ�߱���
						if(canScan){
						dataOutput(serialport_1, "310\r\n");
						  //logSysDetails("����1д�������ǣ�310\r\n", "�ɹ�");
						}
						AlertDialog.Builder builder = new AlertDialog.Builder(this);
						builder.setTitle("-----ӡˢɨ��ʧ�ܣ���ע�⣡-----");
						builder.setMessage("     ����ϸ�˶���Ϣ!"+getdata.get("I_ReturnMessage"));
						builder.setPositiveButton("ȷ��",null);
						builder.setNegativeButton("ȡ��", null);
						builder.create().show();
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
			} else {
				logSysDetails("�ύMESʧ���������磬�������������_2", "�ɹ�");
			}

			break;
		}
	}
	
	// ����1�������ݷ��͵����߳� mhandler
	private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String messsge=msg.obj.toString();
				//logSysDetails("PLCָ����"+messsge, "��");
				if(messsge.contains("1")){
					//dataOutput(serialport_2, new  String(HexString2Bytes()));
					startscan(serialport_2);
					//logSysDetails("����2����ɨ��", "�ɹ�");
				}
				super.handleMessage(msg);
			}
	};
    
	//����2�������ݷ��͵����߳� mhandler2
	private Handler mHandler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String messsge=msg.obj.toString();
			
			if(messsge.contains(SCAN_TIMEOUT_COMMAND)){
				logSysDetails("ɨ�賬ʱ:", "��");
				handlerTimeOut.sendEmptyMessage(4);
				//����ɨ��ǹ������Noread����ʾɨ�賬ʱ
				
			}
			else{
				logSysDetails("ɨ��:"+messsge, "ɨ��");
				if(messsge.equals(lastlotsn)){
					//�ظ�ɨ�費�ύ
				}else{
					lastlotsn=messsge;
					paras[6]=messsge;
					smprinttingModel.printtingscan(new Task(Smprintting.this,TaskType.smtprintting,paras));
					progressDialog.setMessage("�����ύ��....");
					progressDialog.show();
				}
				
			}
			super.handleMessage(msg);
		}
   };
		


   //��ʱ����
	private Handler handlerTimeOut = new Handler() {
		@Override
	   public void handleMessage(Message msg) {
			
			 // ��ɨ�賬ʱ
				if (reScanTimes <= SCAN_RESCAN) {
					logSysDetails( "ɨ��ͷ�� " + reScanTimes + " ������ɨ��","��ȷ");
					startscan(serialport_2);
					logSysDetails("����2����ɨ��", "�ɹ�");
					reScanTimes++;
				} else {
					logSysDetails("ɨ��ʧ��", "��ȷ");
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
