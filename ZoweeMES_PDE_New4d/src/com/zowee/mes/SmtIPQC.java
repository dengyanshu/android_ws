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
	private  TextView  tvmo;//点击换取工单
	
   private   EditText carsnedit;
   private  EditText  carnumedit;
   
   private  EditText  snedit;
   private  EditText  errorcodeedit;
   private  Button passbutton;
   private  Button failbutton;
   private  EditText  editscan;
   
   

  
   private GetMOnameModel GetMonamemodel; // 任务处理类
	private SmtIPQCModel smtIPQCModel; // 任务处理类
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
   
	// 回退键关掉这个activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("确定退出程序?")
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
		super.TASKTYPE = TaskType.smtipqcscan; // 后台服务静态整形常量
		//扫描数据也是个后台任务
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
				logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			resourceid = getresult.get(0).get("ResourceId");
			if(resourceid.isEmpty())	 
				logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
			else{
				logSysDetails("成功获取到该设备的资源ID:"+resourceid,"成功");
			}
		break;
		
		case TaskType.smtipqcgetmoname:
			super.closeProDia();
			String lotsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null&&getdata.size()!=0) {

					
					String moname = getdata.get("MOName");
					
					

					editMO.setText(moname);
					editMO.setEnabled(false);
					editMO.clearFocus();
					carsnedit.requestFocus();
					String scantext = "通过：[" + lotsn + "]成功的获得工单:"+moname;
					logSysDetails(scantext, "成功");
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);

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
			
			
		case TaskType.smtipqgetcar:
			super.closeProDia();
			//String car = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					String num = getdata.get("InCarLotSNQty");
					
					carnumedit.setText(num);
					if(num.equals("0")){
						logSysDetails( "在MES获取车辆信息为0，请检查输入的车号", "成功");
					}
					else{
						logSysDetails( "在MES获取车辆信息成功！", "成功");
						snedit.requestFocus();
					}
					
				} else {
					
				}
				closeProDia();
			} else {
				//logSysDetails(lotsn + "在MES获取工单信息失败，请检查输入的条码", "成功");
			}

			break;
			
		case TaskType.smtipqcpcba:
			super.closeProDia();
			String sn = snedit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
					}
					snedit.requestFocus();
					snedit.setText("");
					errorcodeedit.setText("");

				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;
			
		case TaskType.smtipqccar:
			super.closeProDia();
			//String sn = snedit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
					}
					snedit.requestFocus();
					snedit.setText("");
					errorcodeedit.setText("");

				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;
		
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
		    if(editMO.isFocused()){
		    	editMO.setText(scanRes);
		    	super.progressDialog.setMessage("正在获取工单信息...");
				super.showProDia();
				smtIPQCModel.getmonamebylotsn(new Task(this,TaskType.smtipqcgetmoname,editMO.getText().toString()));
		    }
		    else if(carsnedit.isFocused()){
		    	carsnedit.setText(scanRes);
		    	  super.progressDialog.setMessage("正在获取装车数量信息...");
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
				
				
						
				super.progressDialog.setMessage("正在获取信息...");
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
					"     整车通过！提交到MES，更新MES信息！\n****************************");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@SuppressLint("DefaultLocale")
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							paras[6]="OK";
							smtIPQCModel.ipqccar(new Task(SmtIPQC.this,TaskType.smtipqccar,paras));
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create().show();
			break;
			
		case R.id.smtipqc_failbutton:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			
			builder1.setIcon(getResources().getDrawable(R.drawable.logo3));
			builder1.setMessage("**************************\n" +
					"    整车批退！提交到MES，更新MES信息！\n*************************");
			builder1.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@SuppressLint("DefaultLocale")
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							paras[6]="NG";
							 smtIPQCModel.ipqccar(new Task(SmtIPQC.this,TaskType.smtipqccar,paras));
						}
					});
			builder1.setNegativeButton("取消", null);
			builder1.create().show();
			break;
		
		case R.id.smtipqc_motextview:
			AlertDialog.Builder builder11 = new AlertDialog.Builder(this);
			builder11.setTitle("更换工单");
			builder11.setMessage("是否需要重新更换工单？");
			builder11.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							editMO.setText("");
							editMO.setEnabled(true);
							editMO.requestFocus();
						}
					});
			builder11.setNegativeButton("取消", null);
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
				    super.progressDialog.setMessage("正在获取信息...");
					super.showProDia();
					smtIPQCModel.getmonamebylotsn(new Task(this,TaskType.smtipqcgetmoname,sn));
			   
			}			
		break;
		
		case R.id.smtipqc_carsnedit:
			String car = carsnedit.getText().toString().toUpperCase().trim();//
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    super.progressDialog.setMessage("正在获取信息...");
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
				    super.progressDialog.setMessage("正在获取信息...");
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
