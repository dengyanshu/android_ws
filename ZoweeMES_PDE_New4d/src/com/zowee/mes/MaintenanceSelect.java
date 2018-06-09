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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.ImeicartoonboxcheckModel;
import com.zowee.mes.model.MaintenanceSelectModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class MaintenanceSelect extends CommonActivity  {
		
	
	private  String ResourceID;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	private GetMOnameModel GetMonamemodel;
	
   private  ListView  listview;
   
   private List<HashMap<String,String>>  datas;
   private SimpleAdapter adapter;
   
   
   


	private MaintenanceSelectModel maintenanceSelectModel; // 任务处理类
	private static final String TAG = "MaintenanceSelect";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_maintenanceselect);
		init();
	}

	protected void onResume() {
		super.onResume();
		getdatas();
		
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
								stopService(new Intent(MaintenanceSelect.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.imeicartoonboxcheckscan; // 后台服务静态整形常量
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		maintenanceSelectModel = new MaintenanceSelectModel();
		
		
		listview=(ListView) findViewById(R.id.maintenanceselect_listview);
		
		listview.setCacheColorHint(Color.TRANSPARENT);
		
		
//		cartoonboxedit.setOnKeyListener(this);
//		imeiedit.setOnKeyListener(this);
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
				
			}
			else{
			}
		break;
		
		case TaskType.maintenanceselectgetdatas:
			super.closeProDia();
			if (null != task.getTaskResult()) {
			
				datas = (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + datas);
				 adapter = new SimpleAdapter(this, datas,
						R.layout.maintenancelistview, new String[]{"EquipmentNumber",
								"FixedAssetNumber", "EquipmentLocation","MaintenancePeriod","LastMaintenanceDate","NextMaintenanceDate" }, 
								new int[] {R.id.maintenancelistview_1, R.id.maintenancelistview_2, R.id.maintenancelistview_3,
						 R.id.maintenancelistview_4,R.id.maintenancelistview_5,R.id.maintenancelistview_6});
				 listview.setAdapter(adapter);
			} else {
				
				
			}

			break;
			
		}
	}
	
	
	private  void  getdatas(){
		super.progressDialog.setMessage("获取数据中....");
		super.progressDialog.show();
		maintenanceSelectModel.getinformation(new  Task(this,TaskType.maintenanceselectgetdatas,""));
	}
	
		
	

	

	
	

}
