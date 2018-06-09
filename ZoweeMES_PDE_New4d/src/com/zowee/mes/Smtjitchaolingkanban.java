package com.zowee.mes;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtPeoplescanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class Smtjitchaolingkanban extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	

    private  EditText  moEdit;
    private   ListView  lv;
    
    private  List<HashMap<String, String>>  listgetdata;
	private   SimpleAdapter  adapter;

   private GetMOnameModel GetMonamemodel; // 任务处理类
	private SmtPeoplescanModel smtPeoplescanModel; // 任务处理类
	private static final String TAG = "Smtjitshouliao";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_jitchaolingkanban);
		init();
	}

	protected void onResume() {
		super.onResume();
	}

	public void onBackPressed() {
		killMainProcess();
	}
   
	public void clickme(View v){
		getdata(moEdit.getText().toString().trim());
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
								stopService(new Intent(Smtjitchaolingkanban.this,
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
		
		moEdit=(EditText) findViewById(R.id.jitchaolingkanban_moname);
		lv=(ListView) findViewById(R.id.jitchaolingkanban_listview);
		lv.setCacheColorHint(Color.TRANSPARENT);
		getdata("");
	}
	

	private void getdata(String moname) {
		// TODO Auto-generated method stub
		
			super.progressDialog.setMessage("Get data....");
			super.showProDia();	 
			
			Task task = new Task(this, 300, moname);	
			smtPeoplescanModel.getchaolingkanban(task);
	}

	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		switch (task.getTaskType()) {	
		case 300:
			 super.closeProDia();
			 if (null != task.getTaskResult()) {
				 listgetdata = (List<HashMap<String, String>>) task.getTaskResult();
				 adapter=new SimpleAdapter(this, listgetdata,R.layout.activity_chaolingkanban_listview,
						 new  String[]{"MOName","PickingListName","StockName","PickingListStates","OA状态"}, new int[]{R.id.chaolinglistview_tv1,R.id.chaolinglistview_tv2,R.id.chaolinglistview_tv3,R.id.chaolinglistview_tv4,R.id.chaolinglistview_tv5});
                 lv.setAdapter(adapter);
			 } else {

			 }
	      break;
		
		}
	}


	

	
	
}
