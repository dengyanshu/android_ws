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
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.EquipmentMaintenancekModel;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtIPQCModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class SmtIPQC extends CommonActivity implements
		 OnClickListener,OnKeyListener
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
	private EditText editMO;
	private  TextView  tvmo;//�����ȡ����
	
   private   EditText carsnedit;
   private  EditText  carnumedit;
   
   private  EditText  snedit;
   private  EditText  errorcodeedit;
   private  Button passbutton;
   private  Button failbutton;
   private  EditText  editscan;
   
   

  
   private GetMOnameModel GetMonamemodel; // ��������
	private SmtIPQCModel smtIPQCModel; // ��������
	private static final String TAG = "SmtIPQC";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_smtipqc);
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
								stopService(new Intent(SmtIPQC.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.smtipqcscan; // ��̨����̬���γ���
		//ɨ������Ҳ�Ǹ���̨����
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		smtIPQCModel = new SmtIPQCModel();
		
		 editMO=(EditText) findViewById(R.id.smtipqc_moedit);
		  tvmo=(TextView) findViewById(R.id.smtipqc_motextview);
		  editMO.requestFocus();
		carsnedit=(EditText) findViewById(R.id.smtipqc_carsnedit);
		carnumedit = (EditText) findViewById(R.id.smtipqc_carnumedit);
		snedit=(EditText) findViewById(R.id.smtipqc_snedit);
		errorcodeedit = (EditText) findViewById(R.id.smtipqc_errorcodeedit);
		passbutton = (Button) findViewById(R.id.smtipqc_okbutton);
		failbutton = (Button) findViewById(R.id.smtipqc_failbutton);
		passbutton.setFocusable(false);
		failbutton.setFocusable(false);
		editscan=(EditText) findViewById(R.id.smtipqc_editscan);

		passbutton.setOnClickListener(this);
		failbutton.setOnClickListener(this);
		tvmo.setOnClickListener(this);
		
		
		editMO.setOnKeyListener(this);
		carsnedit.setOnKeyListener(this);
		errorcodeedit.setOnKeyListener(this);
		
        GetResourceId();
	}
	private void GetResourceId()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
		GetMonamemodel.GetResourceId(task);

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
		case TaskType.smtipqcscan:
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
			break;
			
		case TaskType.GetResourceId:
			resourceid = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			resourceid = getresult.get(0).get("ResourceId");
			if(resourceid.isEmpty())	 
				logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
			else{
				logSysDetails("�ɹ���ȡ�����豸����ԴID:"+resourceid,"�ɹ�");
			}
		break;
		
		case TaskType.smtipqcgetmoname:
			super.closeProDia();
			String lotsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null&&getdata.size()!=0) {

					
					String moname = getdata.get("MOName");
					
					

					editMO.setText(moname);
					editMO.setEnabled(false);
					editMO.clearFocus();
					carsnedit.requestFocus();
					String scantext = "ͨ����[" + lotsn + "]�ɹ��Ļ�ù���:"+moname;
					logSysDetails(scantext, "�ɹ�");
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);

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
			
			
		case TaskType.smtipqgetcar:
			super.closeProDia();
			//String car = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					String num = getdata.get("InCarLotSNQty");
					
					carnumedit.setText(num);
					if(num.equals("0")){
						logSysDetails( "��MES��ȡ������ϢΪ0����������ĳ���", "�ɹ�");
					}
					else{
						logSysDetails( "��MES��ȡ������Ϣ�ɹ���", "�ɹ�");
						snedit.requestFocus();
					}
					
				} else {
					
				}
				closeProDia();
			} else {
				//logSysDetails(lotsn + "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
			}

			break;
			
		case TaskType.smtipqcpcba:
			super.closeProDia();
			String sn = snedit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}
					snedit.requestFocus();
					snedit.setText("");
					errorcodeedit.setText("");

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
			
		case TaskType.smtipqccar:
			super.closeProDia();
			//String sn = snedit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}
					snedit.requestFocus();
					snedit.setText("");
					errorcodeedit.setText("");

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
		
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
		    if(editMO.isFocused()){
		    	editMO.setText(scanRes);
		    	super.progressDialog.setMessage("���ڻ�ȡ������Ϣ...");
				super.showProDia();
				smtIPQCModel.getmonamebylotsn(new Task(this,TaskType.smtipqcgetmoname,editMO.getText().toString()));
		    }
		    else if(carsnedit.isFocused()){
		    	carsnedit.setText(scanRes);
		    	  super.progressDialog.setMessage("���ڻ�ȡװ��������Ϣ...");
					super.showProDia();
					smtIPQCModel.getpcbanumincar(new Task(this,TaskType.smtipqgetcar,carsnedit.getText().toString()));
		    }
		    else if(snedit.isFocused()){
		    	snedit.setText(scanRes);
		    	errorcodeedit.requestFocus();
		    }
		    else if(errorcodeedit.isFocused()){
		    	errorcodeedit.setText(scanRes);
		    	String[] paras=new String[8];
				paras[0]=resourceid;
				paras[1]=resourcename;
				paras[2]=useid;
				paras[3]=usename;
				
				paras[4]=editMO.getText().toString().trim();
				paras[5]=carsnedit.getText().toString().trim();
				paras[6]=snedit.getText().toString().trim();
				paras[7]=errorcodeedit.getText().toString().trim();
				
				
						
				super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
				super.showProDia();
				smtIPQCModel.ipqcpcba(new Task(this,TaskType.smtipqcpcba,paras));

		    }
	}
		

	@Override
	public void onClick(View v) {
		final String[] paras=new String[7];
		paras[0]=resourceid;
		paras[1]=resourcename;
		paras[2]=useid;
		paras[3]=usename;
		
		paras[4]=editMO.getText().toString().trim();
		paras[5]=carsnedit.getText().toString().trim();
		paras[6]="";
		
		
		switch (v.getId()) {
		case R.id.smtipqc_okbutton:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(getResources().getDrawable(R.drawable.logo3));
			builder.setMessage("***************************\n" +
					"     ����ͨ�����ύ��MES������MES��Ϣ��\n****************************");
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@SuppressLint("DefaultLocale")
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							paras[6]="OK";
							smtIPQCModel.ipqccar(new Task(SmtIPQC.this,TaskType.smtipqccar,paras));
						}
					});
			builder.setNegativeButton("ȡ��", null);
			builder.create().show();
			break;
			
		case R.id.smtipqc_failbutton:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			
			builder1.setIcon(getResources().getDrawable(R.drawable.logo3));
			builder1.setMessage("**************************\n" +
					"    �������ˣ��ύ��MES������MES��Ϣ��\n*************************");
			builder1.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@SuppressLint("DefaultLocale")
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							paras[6]="NG";
							 smtIPQCModel.ipqccar(new Task(SmtIPQC.this,TaskType.smtipqccar,paras));
						}
					});
			builder1.setNegativeButton("ȡ��", null);
			builder1.create().show();
			break;
		
		case R.id.smtipqc_motextview:
			AlertDialog.Builder builder11 = new AlertDialog.Builder(this);
			builder11.setTitle("��������");
			builder11.setMessage("�Ƿ���Ҫ���¸���������");
			builder11.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							editMO.setText("");
							editMO.setEnabled(true);
							editMO.requestFocus();
						}
					});
			builder11.setNegativeButton("ȡ��", null);
			builder11.create().show();
			break;
			
			
		}		
			
	 		
	}
	
	

	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		
		case R.id.smtipqc_moedit:
			String sn = editMO.getText().toString().toUpperCase().trim();//
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
					super.showProDia();
					smtIPQCModel.getmonamebylotsn(new Task(this,TaskType.smtipqcgetmoname,sn));
			   
			}			
		break;
		
		case R.id.smtipqc_carsnedit:
			String car = carsnedit.getText().toString().toUpperCase().trim();//
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
					super.showProDia();
					smtIPQCModel.getpcbanumincar(new Task(this,TaskType.smtipqgetcar,car));
			   
			}			
		break;
		
		case R.id.smtipqc_errorcodeedit:
			String[] paras=new String[8];
			paras[0]=resourceid;
			paras[1]=resourcename;
			paras[2]=useid;
			paras[3]=usename;
			
			paras[4]=editMO.getText().toString().trim();
			paras[5]=carsnedit.getText().toString().trim();
			paras[6]=snedit.getText().toString().trim();
			paras[7]=errorcodeedit.getText().toString().trim();
			
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
					super.showProDia();
					smtIPQCModel.ipqcpcba(new Task(this,TaskType.smtipqcpcba,paras));
			   
			}			
		break;

		
		}
	return  false;
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
