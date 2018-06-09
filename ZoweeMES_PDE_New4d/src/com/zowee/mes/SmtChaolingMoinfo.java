package com.zowee.mes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.model.SmtChaolingModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
public class SmtChaolingMoinfo extends CommonActivity 
		 {
	protected static final int RESULTCODE = 0;
	private ViewSwitcher  switcher;
	private EditText editMOselect;
	private  ListView  listview;
	private SmtChaolingModel smtChaolingModel; // 任务处理类
	private  List<HashMap<String, String>>  listgetdata;
	private   SimpleAdapter  adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		 super.onCreate(savedInstanceState);
		 //requestWindowFeature(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 this.setContentView(R.layout.activity_smt_shouliao2);
		 init();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		smtChaolingModel.getmo(new Task(this,TaskType.smtchaolinggetmo,""));
		//获取工单信息
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
								stopService(new Intent(SmtChaolingMoinfo.this,
										BackgroundService.class));
								Intent  intent=new Intent(SmtChaolingMoinfo.this,SmtChaoling.class);
								intent.putExtra("MOName", "未选择工单");
								intent.putExtra("MOId", "");
								setResult(RESULTCODE, intent);
								finish();
//								Toast.makeText(SmtChaolingMoinfo.this, "选择工单才能退出", 1).show();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
	


	public void init() {
//		super.commonActivity = this;
//		super.TASKTYPE = TaskType.smtipqcscan; // 后台服务静态整形常量
//		super.init();
        
		smtChaolingModel = new SmtChaolingModel();
		
		
		switcher=(ViewSwitcher) findViewById(R.id.shouliao2_switch);
		LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout  layout=(LinearLayout) inflater.inflate(R.layout.linear, null);
		listview=new ListView(this);
		listview.setOnItemClickListener(new  OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> map=(HashMap<String, String>) arg0.getItemAtPosition(arg2);
				String moname=map.get("MOName");
				String moid=map.get("MOId");
				//System.out.println("moname="+moname+",moid="+moid);
				Intent  intent=new Intent(SmtChaolingMoinfo.this,SmtChaoling.class);
				intent.putExtra("MOName", moname);
				intent.putExtra("MOId", moid);
				setResult(RESULTCODE, intent);
				finish();
			}
		});
		switcher.addView(layout);
		switcher.addView(listview);
		
		editMOselect=(EditText) findViewById(R.id.shouliao2_edit);
		editMOselect.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode==KeyEvent.KEYCODE_ENTER&&event.getAction()==KeyEvent.ACTION_DOWN){
					 String code=editMOselect.getText().toString().trim();
					 if("".equals(code))
						 return  false;
					 if(listgetdata.size()>0){
						//迭代的时候是不允许删除修改操作的  解决办法先临时缓存 最后遍历后删除
						 //adapter 更新数据 还有个要注意的需要在主线程 而且 如果getview在绘制时候 数据一直在更新 可能会报状态异常
						 //解决办法 类似迭代 先临时记录 最后异步线程数据全部加载完毕 在一次添加到adapter数据里面 然后更新
						List<HashMap<String, String>>  temp_list=new ArrayList<HashMap<String, String>>();
						 for(HashMap<String, String> map:listgetdata){
							 if(!(map.get("MOName").contains(code))){
								 temp_list.add(map);
							 }
								 
						 }
						 listgetdata.removeAll(temp_list);
						 adapter.notifyDataSetChanged();
						 
					 }
				}
				return false;
			}
		});
		
		  
	}
	
	
	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		 
		switch (task.getTaskType()) {	
		case TaskType.smtchaolinggetmo:
			// super.closeProDia();
			 if (null != task.getTaskResult()) {
				 switcher.showNext();
				 listgetdata = (List<HashMap<String, String>>) task.getTaskResult();
				 adapter=new SimpleAdapter(this, listgetdata,R.layout.activity_smtjit_chaolinglistview,
						 new  String[]{"MOName","productName"}, new int[]{R.id.chaolinglistview_tv1,R.id.chaolinglistview_tv2});
                 listview.setAdapter(adapter);
                 
				 
//				 List<String>  workcenterstrings=new  ArrayList<String>();
//				 workcenterstrings.add("请选择");
//				 for(HashMap<String, String> map:listgetdata){
//					 workcenterstrings.add(map.get("WorkcenterName"));
//				 }
//
//				 ArrayAdapter  adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, workcenterstrings);
//				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//				 lineSpinner.setAdapter(adapter);
			 } else {

			 }
	      break;
		
		

		
		}
	}


		

	
	 		

	

	
	
	
}
