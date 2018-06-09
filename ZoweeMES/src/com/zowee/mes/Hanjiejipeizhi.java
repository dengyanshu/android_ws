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
import java.util.HashMap;
import java.util.TooManyListenersException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
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
import com.zowee.mes.model.SmtFvmiModel;
import com.zowee.mes.model.SntocarModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class Hanjiejipeizhi extends CommonActivity implements
		 OnClickListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	
	private EditText jiareshijianedit;
	private EditText lengqueshijianedit;
	private EditText jiarechaoshiedit;

	
	private Button okbutton;
	private Button selectbutton;
	

	private EditText editscan;
	
	private static final String OWNER = "peizhi";
	private  SerialPort  serialport_1;
    private  SerialPort  serialport_2;
    private  SerialPort  serialport_3;

	

	private  Common4dModel  common4dmodel;
	private static final String TAG = "Hanjiejipeizhi";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hanjieji_configtoplc);
		Log.i(TAG,"oncreate---peizhi");
		init();
	}

	protected void onResume() {
		super.onResume();
		Log.i(TAG,"onresume---peizhi");
	}
	@Override
	public void onDestroy() {
		// ����������ʱ�ص�Timer
		Log.i(TAG,"ondestory---peizhi");
		unRegisterPort();
		super.onDestroy();
	}
     
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG,"onstart---peizhi");
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i(TAG,"onrestart---peizhi");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(TAG,"onstop---peizhi");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG,"onPause---peizhi");
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
								stopService(new Intent(Hanjiejipeizhi.this,
										BackgroundService.class));
								//stopService(new Intent(new Hanjiejiboxing(),BackgroundService.class));
								unRegisterPort();
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
	private void unRegisterPort() {
//		if (dataOutput != null) {
//			try {
//				dataOutput.close();
//			} catch (IOException e) {
//				Log.e(TAG, "unRegisterPort dataOutput", e);
//			}
//			dataOutput = null;
//		}
		
		if (serialport_1 != null) {
			serialport_1.removeEventListener();
			serialport_1.close();
		}
		serialport_1 = null;
		
	}

	public void init() {
//		super.commonActivity = this;
//		super.TASKTYPE = TaskType.biaoqianhedui; // ��̨����̬���γ���
//		super.init();
           
		
		try {
			serialport_1 = registerPort("/dev/ttyS1");
			//serialport_2 = registerPort("/dev/ttyS2");
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "ע�ᴮ��ʧ��", Toast.LENGTH_SHORT).show();
		}
		
		common4dmodel=new  Common4dModel();
		
	     

		jiareshijianedit = (EditText) findViewById(R.id.hanjieji_configtoplc_jiareedit);
		lengqueshijianedit = (EditText) findViewById(R.id.hanjieji_configtoplc_lenqueedit);
		jiarechaoshiedit = (EditText) findViewById(R.id.hanjieji_configtoplc_jiarechaoshiedit);

	
	

		okbutton = (Button) findViewById(R.id.hanjieji_configtoplc_okbutton);
		selectbutton = (Button) findViewById(R.id.hanjieji_configtoplc_selectbutton);

		editscan = (EditText) findViewById(R.id.hanjieji_configtoplc_editscan);
		//editscan.setFocusable(false);
		
		

		okbutton.setOnClickListener(this);
		selectbutton.setOnClickListener(this);
        
		
      // common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));

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
 					
 					if (true) {
 						String msg=readMessage();
 						//Log.i(TAG,"�յ���"+receivetime+"��Ϣ��:"+msg);
 						Message.obtain(mHandler, 1, msg).sendToTarget();
 						//Toast.makeText(Tjzimipowertest.this, who+"������������1��"+msg, Toast.LENGTH_LONG).show();
 						
 					} else {
 						clearBuff();
 					}
 				} else if ("/dev/ttyS2".equals(who)) {
 					
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
 					numBytes = dataInput.read(readBuffer);
 					
 						for (i = 0; i < numBytes; i++) {
 							//Log.d(TAG, "serialEvent: byte[" + i + "]"
 								//	+ (readBuffer[i]));
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
 	
 	private void dataOutput(SerialPort portName, String out) {
		try {
			Log.i(TAG,"���͵���Ϣ��:"+out);
		   OutputStream	dataOutput = portName.getOutputStream();
			dataOutput.write(out.getBytes("US-ASCII"));
			dataOutput.flush();
			
			Toast.makeText(this, portName+"����д���ݳɹ���д��������"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "����д����ʧ��������ִ�У�", Toast.LENGTH_SHORT).show();
		}
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
		if("COM1".equals(tty)){
			String str1=msg.substring(2, 6);
			String str2=msg.substring(6, 10);
			String str3=msg.substring(10, 14);
			editscan.append("��ȡ���������趨:����ʱ��Ϊ"+str1+",��ȴʱ��Ϊ"+str2+",���ȳ�ʱʱ��Ϊ"+str3+"!\r\n");
		}
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
//		case TaskType.common4dmodelgetresourceid:
//			if (null != task.getTaskResult()) {
//				getdata = (HashMap<String, String>) task.getTaskResult();
//				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
//				if (getdata.get("Error") == null) {
//					if (getdata.containsKey("ResourceId")) {
//						resourceid = getdata.get("ResourceId");
//					}
//					String logText = "����������!��⵽��ƽ�����Դ����:[ " + resourcename
//							+ " ],��ԴID: [ " + resourceid + " ],�û�ID: [ " + useid + " ]!!�����������������������2�֣���";
//					logSysDetails(logText, "����");
//				} else {
//					logSysDetails(
//							"ͨ����Դ���ƻ�ȡ��MES��ȡ��ԴIDʧ�ܣ��������õ���Դ�����Ƿ���ȷ", "�ɹ�");
//				}
//				closeProDia();
//			} else {
//				logSysDetails( "��MES��ȡ��Դid��Ϣʧ�ܣ�������������Դ�����Ƿ���ȷ", "�ɹ�");
//			}
//
//			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hanjieji_configtoplc_okbutton:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("��ȷ��");
			builder.setMessage("�Ѿ�������ϣ��ύ����Ƭ����");
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							String out="@S";
							String str=get4string(jiareshijianedit.getText().toString().trim());
							if(str.equals("0000")){
								Toast.makeText(Hanjiejipeizhi.this, "����ʱ�������쳣�����ύ��", Toast.LENGTH_SHORT).show();
								return ;
							}
							else
								 out=out+str;
							str=get4string(lengqueshijianedit.getText().toString().trim());
							if(str.equals("0000")){
								Toast.makeText(Hanjiejipeizhi.this, "��ȴʱ�������쳣�����ύ��", Toast.LENGTH_SHORT).show();
								return ;
							}else{
								 out=out+str;
							}
							str=get4string(jiarechaoshiedit.getText().toString().trim());
							if(str.equals("0000")){
								Toast.makeText(Hanjiejipeizhi.this, "���ȳ�ʱʱ�������쳣�����ύ��", Toast.LENGTH_SHORT).show();
								return ;
							}else{
								 out=out+str+"\n";
							}
									
							dataOutput(serialport_1,out);
						}
					});
			builder.setNegativeButton("ȡ��", null);
			builder.create().show();
			break;
			
		case R.id.hanjieji_configtoplc_selectbutton:
		
			dataOutput(serialport_1, "@R\n");
			break;
		}
	}

	private  String  get4string(String str){
			String  s="";
		if(str.length()==4){
			return  s=str;
		}
		else  if(str.length()==3){
			s="0"+str;
			return  s;
			
		}
		else  if(str.length()==2){
			s="00"+str;
			return  s;
			
		}
		else  if(str.length()==1){
			s="000"+str;
			return  s;
			
		}
		else{
			return  s="0000";
		}
			
	
		
	}

	

	

}
