package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtChaolingModel;
import com.zowee.mes.model.SmtPeoplescanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class Smtpeoplescan extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
	


   private  Spinner  lineEdit;
   private  Spinner  banciSpinner;
   private  String banci;
   private  EditText  tinoEdit;
   private  Spinner  zhiweiSpinner;
   private  String zhiwu;
   private  EditText  peopleEdit;
   private  EditText  editscan;
   

   private GetMOnameModel GetMonamemodel; // 任务处理类
	private SmtPeoplescanModel smtPeoplescanModel; // 任务处理类
	private static final String TAG = "Smtjitshouliao";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_smtpeoplescan);
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
								stopService(new Intent(Smtpeoplescan.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.smtipqcscan; // 后台服务静态整形常量
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		smtPeoplescanModel = new SmtPeoplescanModel();
		
	  
	   
		
		 

		   
		lineEdit=(Spinner) findViewById(R.id.smtpeoplescan_line);
		banciSpinner=(Spinner) findViewById(R.id.smtpeoplescan_bancispinner);
		tinoEdit=(EditText) findViewById(R.id.smtpeoplescan_tino);
		zhiweiSpinner=(Spinner) findViewById(R.id.smtpeoplescan_zhiweispinner);
		peopleEdit=(EditText) findViewById(R.id.smtpeoplescan_people);
		editscan=(EditText) findViewById(R.id.smtjitchaoling_editscan);
		banciSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				banci=arg0.getItemAtPosition(arg2).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	    });
		zhiweiSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				zhiwu=arg0.getItemAtPosition(arg2).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	    });	   
			   
		

		peopleEdit.setOnKeyListener(new  OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if (KeyEvent.KEYCODE_ENTER == keyCode&& event.getAction() == KeyEvent.ACTION_DOWN) {
					String[] paras=new String[11];
					paras[0]=resourceid;
					paras[1]=resourcename;
					paras[2]=useid;
					paras[3]=usename;
					
					paras[4]=lineEdit.getSelectedItem().toString();
					paras[5]=banci;
					paras[6]=tinoEdit.getText().toString().trim();
					paras[7]=zhiwu;
					paras[8]=peopleEdit.getText().toString().trim();
					progressDialog.setMessage("正在获取信息...");
					showProDia();
					smtPeoplescanModel.check_mo_out(new Task(Smtpeoplescan.this,300,paras));
				   
				}			
				
				return false;
			}
		});
		
	}
//	private void GetResourceId()
//	{
//		
//		super.progressDialog.setMessage("Get resource ID");
//		super.showProDia();	 
//		
//		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
//		GetMonamemodel.GetResourceId(task);
//
//	}
//	
//	private void getWorkcenter()
//	{
//		
//		super.progressDialog.setMessage("Get resource ID");
//		super.showProDia();	 
//		
//		Task task = new Task(this, 200, "");	
//		smtChaolingModel.get_workcenter(task);
//
//	}
	
//	private  void  getmo(){
//		super.progressDialog.setMessage("Get resource ID");
//		super.showProDia();	 
//		smtChaolingModel.getmo_jit(new Task(Smtpeoplescan.this,100,""));
//		
//	}

	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		switch (task.getTaskType()) {	
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
		

			
	

		case 300:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						peopleEdit.requestFocus();
						peopleEdit.selectAll();
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="失败！"+getdata.get("I_ReturnMessage");
						peopleEdit.requestFocus();
						peopleEdit.selectAll();
						logSysDetails(scantext, "成功");
					}
					

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
