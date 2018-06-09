package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.model.TjbadcountModel;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

public class Tjbadcountmoselect extends  CommonActivity  implements OnItemClickListener{
	private static final int RESULTCODE = 0;
	private TjbadcountModel tjbadcountModel; // 任务处理类
	private ListView listview;
	private  SimpleAdapter adapter;
	private  List<HashMap<String, String>>  molist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tjbadcountmoselect);
		super.commonActivity = this;
		super.TASKTYPE = TaskType.biaoqianhedui; // 后台服务静态整形常量
		super.init();

		tjbadcountModel = new TjbadcountModel();
		listview = (ListView) findViewById(R.id.tjbadcount_molistview);
		listview.setOnItemClickListener(this);
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		super.progressDialog.setMessage("查询中...");
		super.progressDialog.show();
		tjbadcountModel.getmo(new Task(this,TaskType.tjbadcountgetmo,""));
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		HashMap<String, String> item = (HashMap<String, String>) arg0
				.getItemAtPosition(arg2);
		String moid = item.get("MOID");
		String moname = item.get("MOName");
		String modescription = item.get("MODescription");

		Intent intent = new Intent();
		intent.putExtra("MOID", moid);
		intent.putExtra("MOName", moname);
		intent.putExtra("MODescription", modescription);
		setResult(RESULTCODE, intent);

		finish();
		
	}
    
	public void refresh(Task task) {
		super.refresh(task);
		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		switch (task.getTaskType()) {
		// 获取工单ID
		case TaskType.tjbadcountgetmo:
			super.progressDialog.dismiss();
			if (null != task.getTaskResult()) {
				molist = (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + molist);
				adapter=new SimpleAdapter(this, molist, R.layout.listviewfor3, new String[] {"MOID",
						"MOName", "MODescription" }, new int[] {
						R.id.list_1, R.id.list_2, R.id.list_3 });
				listview.setAdapter(adapter);
			} else {
				
			}

			break;
		
		
		}
	}
	
}
