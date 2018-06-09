package com.zowee.mes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.TinoModel;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
//处理线体！！
public class Lineselect extends CommonActivity implements OnItemClickListener {
	private static final int RESULTCODE = 2;
	private ListView listview;
	private HashMap<String, String> getdata;
	private final static String TAG = "Lineselect";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lineselect);

		listview = (ListView) findViewById(R.id.line_listview);
		listview.setOnItemClickListener(this);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		List<HashMap<String, Object>> listData = (List) bundle
				.getSerializable("bundle");
		SimpleAdapter adapter = new SimpleAdapter(this, listData,
				R.layout.linelistview, new String[] { "WorkcenterId",
						"WorkcenterName", "WorkcenterDescription" }, new int[] {
						R.id.line_list_1, R.id.line_list_2, R.id.line_list_3 });
		listview.setAdapter(adapter);
	}

	// public void refresh(Task task) {
	// super.refresh(task);
	// /**
	// * 具体根据提交服务器的返回的结果进行UI界面的更新！！
	// *
	// * */
	//
	// switch (task.getTaskType()) {
	// //获取工单ID
	// case TaskType.getline:
	//
	//
	// List<HashMap<String, String>> listResult=(List<HashMap<String, String>>)
	// task.getTaskResult();
	//
	// Log.i(TAG,"任务结果是2：："+listResult);
	//
	//
	// SimpleAdapter adapter=new SimpleAdapter
	// (this,listResult,R.layout.linelistview,new
	// String[]{"WorkcenterId","WorkcenterName","WorkcenterDescription"},new
	// int[]{R.id.line_list_1,R.id.line_list_2,R.id.line_list_3});
	// listview.setAdapter(adapter);
	//
	// break;
	// }
	//
	// }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		HashMap<String, String> item = (HashMap<String, String>) arg0
				.getItemAtPosition(arg2);
		String WorkcenterId = item.get("WorkcenterId");
		String WorkcenterName = item.get("WorkcenterName");
		String WorkcenterDescription = item.get("WorkcenterDescription");

		Intent intent = new Intent();
		intent.putExtra("WorkcenterId", WorkcenterId);
		intent.putExtra("WorkcenterName", WorkcenterName);
		intent.putExtra("WorkcenterDescription", WorkcenterDescription);
		setResult(RESULTCODE, intent);

		finish();
	}
}
