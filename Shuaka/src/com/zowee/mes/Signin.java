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
/**线头刷卡程序，用于管控 该线的人力、工时、人员技能问题
 * 关联 产品编码、人力、职务等信息
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

	// 回退键关掉这个activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("确定退出程序?")
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
		super.TASKTYPE = TaskType.biaoqianhedui; // 后台服务静态整形常量
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
								Log.i(TAG,"收到的消息是:"+msg);
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
	    
	
	
		//根据com口路径返回串口对象
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
	 * 刷新UI界面
	 */
	@SuppressLint("ResourceAsColor")
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
							+ " ],资源ID: [ " + resourceid + " ],用户ID: [ " + useid + " ]！";
					logSysDetails(logText, "程序");
					String line=resourcename;
					linetv.requestFocus();
					
					SpannableStringBuilder ssb=new SpannableStringBuilder(line);
					CharacterStyle  cs=new ForegroundColorSpan(R.color.blue);
					ssb.setSpan(cs, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					linetv.setText(ssb);
					
				} else {
					logSysDetails(
							"通过资源名称获取在MES获取资源ID失败，请检查电脑配置的资源名称是否正确", "成功");
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取资源id信息失败，请检查配置则资源名称是否正确", "成功");
			}

			break;
			
		case TaskType.anxuzhuangxiang:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String exception=getdata.get("I_ExceptionFieldName");
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "按序装箱成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "按序装箱失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
					}

				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails("提交MES失败请检查网络或者工单，请检查输入的条码", "成功");
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.anxuzhuangxiang_cancelbutton:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle("解除绑定");
			builder1.setMessage("是否需要解除箱号与SN的绑定？");
			builder1.setPositiveButton("确定",
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
			builder1.setNegativeButton("取消", null);
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
