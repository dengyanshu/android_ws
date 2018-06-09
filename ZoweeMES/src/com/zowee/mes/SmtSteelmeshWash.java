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

public class SmtSteelmeshWash extends CommonActivity 
		 {
	/***
	 *������ϴ����
	 * */
	
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	private EditText sn1;
	private EditText sn2;
	
    private String[] paras=new String[7];

	private static EditText editscan;

	private  Common4dModel  common4dmodel;
	private  SmprinttingModel  smprinttingModel;
	private static final String TAG = "SmtSteelmeshWash";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_smt_steelmesh_wash);
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
								stopService(new Intent(SmtSteelmeshWash.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
    
	
	
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.GetResourceId; // ��̨����̬���γ���
		super.init();

		common4dmodel=new  Common4dModel();
		smprinttingModel=new SmprinttingModel();
		
        
		sn1=(EditText) findViewById(R.id.smtsteelmesh_wash_sn1);
		sn2=(EditText) findViewById(R.id.smtsteelmesh_wash_sn2);

	
		editscan = (EditText) findViewById(R.id.smtprintting_editscan);
		editscan.setFocusable(false);
		
		
        common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));
      
	}
	
	
	

	public static void ClearshowInfo() {
		if (editscan.getLineCount() > 30)
			editscan.setText("");
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
	
	
	public  void  onclick(View v){
		paras[0]=resourceid;
		paras[1]=resourcename;
		paras[2]=useid;
		paras[3]=usename;
		
		paras[4]=sn1.getText().toString().trim();
		paras[5]=sn2.getText().toString().trim();
		super.progressDialog.setMessage("�����ύ...");
		super.showProDia();
		smprinttingModel.wash(new Task(SmtSteelmeshWash.this,100,paras));
		
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
		
		
		
		case 100:
			super.closeProDia();
			sn1.setText("");
			sn2.setText("");
			if (null != task.getTaskResult()) {
				ClearshowInfo();
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						//����0  ʧ�� 1��ʾ  �ɹ�  2�������  3����  4��λ
						
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						
					}
					else{
					
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
	
	
	
	
}
