package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.AnxuzhuangxiangModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SmtxgModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class SMTXG extends CommonActivity implements
		android.view.View.OnKeyListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private  EditText editMO;
	private  String moid; // ����ѡ�񱣴����
	private  String moname;

	private EditText lotsn_edit;
	private EditText xglotsn_edit;
	

	
	

	private EditText editscan;

	

	private SmtxgModel smtxgModel; // ��������
	private  Common4dModel  common4dmodel;
	private static final String TAG = "SMTXG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_smtxg);
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
								stopService(new Intent(SMTXG.this,
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

		smtxgModel = new SmtxgModel();
		common4dmodel=new  Common4dModel();
		
		editMO = (EditText) findViewById(R.id.smtxg_mo);
		editMO.requestFocus();
		
	

		lotsn_edit = (EditText) findViewById(R.id.smtxg_lot);
		xglotsn_edit = (EditText) findViewById(R.id.smtxg_xglot);
		

		editscan = (EditText) findViewById(R.id.smtxg_editscan);
		editscan.setFocusable(false);
		
		
        
		editMO.setOnKeyListener(this);
		lotsn_edit.setOnKeyListener(this);
		xglotsn_edit.setOnKeyListener(this);
		
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
					moid = getdata.get("MOId");
					

					editMO.setText(moname);
					editMO.setEnabled(false);
					xglotsn_edit.requestFocus();
					String scantext = "ͨ�����κţ�[" + lotsn + "]�ɹ��Ļ�ù���:"
							+ moname + ",����id:"+moid+"!";
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
			
		case TaskType.anxuzhuangxiang:
			Clear();
			lotsn_edit.setText("");
			lotsn_edit.requestFocus();
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
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
   
	public void Clear() {
		if (editscan.getLineCount() > 20)
			editscan.setText("");
	}
	
	
	@SuppressLint("DefaultLocale")
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case  R.id.smtxg_xglot:
			if (KeyEvent.KEYCODE_ENTER == keycode
			&& event.getAction() == KeyEvent.ACTION_DOWN) {
				xglotsn_edit.setEnabled(false);
				//xglotsn_edit.requestFocus();
				lotsn_edit.requestFocus();
			}
			break;
		
			
		case R.id.smtxg_mo:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String param = editMO.getText().toString().toUpperCase().trim();
			   if(param.length()<8){
				   Toast.makeText(this, "���κų��Ȳ���", Toast.LENGTH_SHORT).show();
				   return false;
			   }
			  
				    super.progressDialog.setMessage("�������ݿ��ȡ����");
					super.showProDia();
				    common4dmodel.getMObylotsn(new Task(this,TaskType.common4dmodelgetmobylotsn,param));
			}
		break;

		case R.id.smtxg_lot:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String xg=xglotsn_edit.getText().toString().trim();
				String lotsn=lotsn_edit.getText().toString().trim();
				
				if(moid==null||"".equals(moid)||xg==null||"".equals(xg)||lotsn==null||"".equals(lotsn)){
					Toast.makeText(this, "����������������Ϣ����Ϊ��", Toast.LENGTH_LONG).show();
					return  false;
				}
				String[]  params=new String[7];
				params[0] = useid;
				params[1] = usename;
				params[2] = resourceid;
				params[3] = resourcename;
				params[4] = moid;
				params[5] = xg;
				params[6] = lotsn;
				
				
					super.progressDialog.setMessage("װ���ύ��");
					super.showProDia();
					
					
					smtxgModel.xg(new  Task(this,TaskType.anxuzhuangxiang,params));
				
			}
			break;
		}
		return false;
	}



}
