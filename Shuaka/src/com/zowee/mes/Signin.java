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
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SigningModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
/**��ͷˢ���������ڹܿ� ���ߵ���������ʱ����Ա��������
 * ���� ��Ʒ���롢������ְ�����Ϣ
 * */
@SuppressLint("ResourceAsColor")
public class Signin extends CommonActivity implements
		 OnClickListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	private  TextView  linetv;
	private  TextView  emptv;
	
	private Button okbutton;

	private EditText editscan;

	private static final String OWNER = "system";
	private  SerialPort  serialport;
	
	private  SigningModel  SigningModel;
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Signin";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daka);
		init();
	}

	protected void onResume() {
		super.onResume();
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
								stopService(new Intent(Signin.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.biaoqianhedui; // ��̨����̬���γ���
		super.init();
		
		try {
			serialport = registerPort("/dev/ttyS1");
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			serialport.addEventListener(new SerialPortEventListener() {
				
				@Override
				public void serialEvent(SerialPortEvent ev) {
					// TODO Auto-generated method stub
					switch(ev.getEventType()){
					case SerialPortEvent.DATA_AVAILABLE:
								String msg=readMessage();
								Log.i(TAG,"�յ�����Ϣ��:"+msg);
								Message.obtain(mHandler, 1, msg).sendToTarget();
					break;
					}
				}
			} );
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		common4dmodel=new  Common4dModel();
		SigningModel=new SigningModel();
		
		linetv = (TextView) findViewById(R.id.signin_linetv);
		
		
	
		emptv = (TextView) findViewById(R.id.signin_emptv);

		okbutton = (Button) findViewById(R.id.signin_button);
		okbutton.setFocusable(false);
		

		editscan = (EditText) findViewById(R.id.signin_editscan);
		editscan.setFocusable(false);
		
		okbutton.setOnClickListener(this);
        
        common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));

	}
	    
	
	
		//����com��·�����ش��ڶ���
		private SerialPort registerPort(String tty) throws TooManyListenersException{
			SerialPort serialPort = null;
			try {
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
	

		public String readMessage() {
			String msg = "";
			byte[] buff=new byte[1024];
			int len=0;
			InputStream is=null;
			try {
				is = serialport.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				while((len=is.read(buff))!=0){
					msg=msg+new String(buff,0,len);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return  msg;
	    }
		
		
		private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case 1:
					analysisMessage(msg.obj.toString());
					break;
				}
				super.handleMessage(msg);
			}
		};
		
	public void analysisMessage(String msg){
			emptv.setText(msg);
			
	}
	/*
	 * ˢ��UI����
	 */
	@SuppressLint("ResourceAsColor")
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
							+ " ],��ԴID: [ " + resourceid + " ],�û�ID: [ " + useid + " ]��";
					logSysDetails(logText, "����");
					String line=resourcename;
					linetv.requestFocus();
					
					SpannableStringBuilder ssb=new SpannableStringBuilder(line);
					CharacterStyle  cs=new ForegroundColorSpan(R.color.blue);
					ssb.setSpan(cs, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					linetv.setText(ssb);
					
				} else {
					logSysDetails(
							"ͨ����Դ���ƻ�ȡ��MES��ȡ��ԴIDʧ�ܣ�����������õ���Դ�����Ƿ���ȷ", "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Դid��Ϣʧ�ܣ�������������Դ�����Ƿ���ȷ", "�ɹ�");
			}

			break;
			
		case TaskType.anxuzhuangxiang:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String exception=getdata.get("I_ExceptionFieldName");
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "����װ��ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "����װ��ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ������������߹������������������", "�ɹ�");
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.anxuzhuangxiang_cancelbutton:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle("�����");
			builder1.setMessage("�Ƿ���Ҫ��������SN�İ󶨣�");
			builder1.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							String[]  params=new String[8];
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = linetv.getText().toString().trim();
							params[5] = emptv.getText().toString().trim();
							SigningModel.assyqidong(new Task(Signin.this,TaskType.anxuzhuangxiangremovebind,params));
						}
					});
			builder1.setNegativeButton("ȡ��", null);
			builder1.create().show();
			break;
		}
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

	

	

}
