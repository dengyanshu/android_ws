package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.AssyqidongModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SmtTPModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//������������ 
public class SMTTP_MANUAL extends CommonActivity implements
		android.view.View.OnKeyListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	

	private  Spinner  abside_spinner;
	private  String abside;
	private EditText editsn;  

	private static EditText editscan;

	
	private  SmtTPModel  smtTPModel;
	private  Common4dModel  common4dmodel;
	private static final String TAG = "SMTTP_MANUAL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_smttp_manual);
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
								stopService(new Intent(SMTTP_MANUAL.this,
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

		smtTPModel = new SmtTPModel();
		common4dmodel=new  Common4dModel();
		
		
        
		abside_spinner=(Spinner) findViewById(R.id.smttp_manual_spinner);
		String[] absides={"A","B"};
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, absides);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		abside_spinner.setAdapter(adapter);
		abside_spinner.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				abside=(String) arg0.getItemAtPosition(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		editsn = (EditText) findViewById(R.id.smttp_manual_sn);
		
		editscan = (EditText) findViewById(R.id.smttp_manual_editscan);
		editscan.setFocusable(false);
		
		editsn.setOnKeyListener(this);
		
        common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));

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
							+ " ],��ԴID: [ " + resourceid + " ],�û�ID: [ " + useid + " ]!!";
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
							
							//2015-4-4 by chenyun
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("����©ɨ�裬��ע�⣡1");
							builder.setMessage("    ��˶�������Ϣ��"+scantext);
							builder.setPositiveButton("ȷ��",null);
							builder.setNegativeButton("ȡ��", null);
							builder.create().show();
							
							logSysDetails(scantext, "�ɹ�");
						}

					} else {
						logSysDetails(
								"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
										+ getdata.get("Error"), "�ɹ�");
					}
					editsn.requestFocus();
					editsn.setText("");
				} else {
					logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
				}
				break;	
		}
	}

	public static void ClearshowInfo() {
		if (editscan.getLineCount() > 20)
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

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.smttp_manual_sn:
			String[] params=new String[6];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String param2 = editsn.getText().toString().toUpperCase().trim();
			   if(param2.length()<8){
					   Toast.makeText(this, "���κų��Ȳ���", Toast.LENGTH_SHORT).show();
					   return  false;
				}
			   
					    super.progressDialog.setMessage("�����ύ��MES....");
						super.showProDia();
						params[0] = useid;
						params[1] = usename;
						params[2] = resourceid;
						params[3] = resourcename;
						params[4] = abside;
						params[5] = param2;
						smtTPModel.tiePianold(new Task(this,TaskType.smttiepianrengong,params));
			}
			
		break;
		}
		return false;
	}
   
	
	

	
	

}
