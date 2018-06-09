package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.EquipmentMaintenancekModel;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.ImeicartoonboxcheckModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class EquipmentMaintenance extends CommonActivity implements
		 OnClickListener,OnKeyListener
		 {
	private  String ResourceID;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	private GetMOnameModel GetMonamemodel;
	
   private   EditText snedit;
   private  EditText  periodedit;//维护周期
   private  EditText  lastmaintenancedateeedit;//上次维护周期
   private  EditText nextmaintenancedateeedit;//下次维护周期
   private  Button button;
   private  TextView  tv;
   


	private EquipmentMaintenancekModel equipmentMaintenancekModel; // 任务处理类
	private static final String TAG = "EquipmentMaintenance";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_equipmentmaintenance);
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
								stopService(new Intent(EquipmentMaintenance.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.equipmentmaintenancescan; // 后台服务静态整形常量
		//扫描数据也是个后台任务
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		equipmentMaintenancekModel = new EquipmentMaintenancekModel();
		
		snedit=(EditText) findViewById(R.id.equipmentmaintenance_snedit);
		periodedit = (EditText) findViewById(R.id.equipmentmaintenance_periodedit);
		lastmaintenancedateeedit=(EditText) findViewById(R.id.equipmentmaintenance_lastmaintenancedatedit);
		nextmaintenancedateeedit = (EditText) findViewById(R.id.equipmentmaintenance_nextmaintenancedatedit);
		button = (Button) findViewById(R.id.equipmentmaintenance_button);
		button.setFocusable(false);
		tv=(TextView) findViewById(R.id.equipmentmaintenance_tv);
		
		button.setOnClickListener(this);
		snedit.setOnKeyListener(this);
		
        GetResourceId();
	}
	private void GetResourceId()
	{
		if(MyApplication.getAppOwner().toString().isEmpty()){

			return;
		}
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, MyApplication.getAppOwner().toString());	
		GetMonamemodel.GetResourceId(task);

	}
	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		
		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		switch (task.getTaskType()) {
		// 获取工单ID
		case TaskType.equipmentmaintenancescan:
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
			break;
			
		case TaskType.GetResourceId:
			ResourceID = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			ResourceID = getresult.get(0).get("ResourceId");
			if(ResourceID.isEmpty()){
				Log.i(TAG,"resourceid"+ResourceID);
			}
			else{
				Log.i(TAG,"resourceid"+ResourceID);
			}
		break;
		
		case TaskType.equipmentmaintenancegetinformation:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				HashMap<String, String>	getdata = (HashMap<String, String>) task.getTaskResult();;
				Log.d(TAG, "task的结果数据是：" + getdata);
				tv.setText("维护结果：");
				if(getdata.containsKey("MaintenancePeriod")){
					periodedit.setText(getdata.get("MaintenancePeriod"));
				}
				else{
					periodedit.setText("无记录");
				}
				if(getdata.containsKey("LastMaintenanceDate")){
					lastmaintenancedateeedit.setText(getdata.get("LastMaintenanceDate"));
				}
				else{
					lastmaintenancedateeedit.setText("无记录");
				}
				if(getdata.containsKey("NextMaintenanceDate")){
					nextmaintenancedateeedit.setText(getdata.get("NextMaintenanceDate"));
				}
				else{
					nextmaintenancedateeedit.setText("无记录");
				}
				
			} else {
				periodedit.setText("无记录");
				lastmaintenancedateeedit.setText("无记录");
				nextmaintenancedateeedit.setText("无记录");
			}

			break;
			
		case TaskType.equipmentmaintenance:
			super.closeProDia();
			String sn=snedit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
		      HashMap<String, String>	getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "["+sn+"]！"+getdata.get("I_ReturnMessage");
						tv.append(scantext);
						snedit.setText("");
						snedit.requestFocus();
						periodedit.setText("");
						lastmaintenancedateeedit.setText("");
						nextmaintenancedateeedit.setText("");
						//cartoonboxedit.setText("");
						//cartoonboxedit.requestFocus();
						
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						String scantext = "维护失败！"+getdata.get("I_ReturnMessage");
						tv.append(scantext);
						//imeiedit.setText("");
						//imeiedit.requestFocus();
					}

				} else {
				}
				closeProDia();
			} else {
			}
			break;
			
		
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
			 snedit.setText(scanRes);
			 super.progressDialog.setMessage("正在获取信息....");
			 super.showProDia();
			 equipmentMaintenancekModel.getinformation(new Task(this,TaskType.equipmentmaintenancegetinformation,scanRes));
	}
		

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.equipmentmaintenance_button:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提交！");
			builder.setMessage("    已维护完成，提交到MES，更新设备的维护信息！");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@SuppressLint("DefaultLocale")
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							String para=snedit.getText().toString().toUpperCase().trim()+","+usename;
							equipmentMaintenancekModel.equipmentmaintenance(new Task(EquipmentMaintenance.this,TaskType.equipmentmaintenance,para));
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create().show();
			break;
		}		
			
	 		
	}

	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.equipmentmaintenance_snedit:
			String sn = snedit.getText().toString().toUpperCase().trim();//设备条形码
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    super.progressDialog.setMessage("正在获取信息...");
					super.showProDia();
					equipmentMaintenancekModel.getinformation(new Task(this,TaskType.equipmentmaintenancegetinformation,sn));
			   
			}			
		break;

		
		}
	return  false;
	}

}
