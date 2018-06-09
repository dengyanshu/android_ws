package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.model.SmtshangliaoslotModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

public class Smtshangliaoslot extends CommonActivity 
		 {
	
	private Spinner spinnermo;
	private  String moid;
	
	private   Spinner  spinnerline;
	private  String line;
 
    private  EditText  mzedit;
 
    private  EditText  editscan;

	private SmtshangliaoslotModel smtshangliaoslotModel; // 任务处理类
	private static final String TAG = "Smtshangliaoslot";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_smtshangliao_slot);
		init();
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
								stopService(new Intent(Smtshangliaoslot.this,
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
        
		smtshangliaoslotModel = new SmtshangliaoslotModel();
		
		spinnermo=(Spinner) findViewById(R.id.smtshangliaoslot_mospinner);
		spinnermo.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				HashMap<String, String>  map=(HashMap<String, String>) arg0.getItemAtPosition(arg2);
				moid=map.get("MOId");
				System.out.println("moid="+moid);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		final EditText  moedit=(EditText) findViewById(R.id.smtshangliaoslot_moedit);
		moedit.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String paras=moedit.getText().toString().trim();
				if(paras.length()<=2)
					return;
				progressDialog.setMessage("正在获取信息...");
				showProDia();
				smtshangliaoslotModel.getMOName(new Task(Smtshangliaoslot.this,100,paras));
			}
		});
		
		
		spinnerline=(Spinner) findViewById(R.id.smtshangliaoslot_linespinner);
		spinnerline.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				line=arg0.getItemAtPosition(arg2).toString();
				System.out.println("line="+line);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	    mzedit = (EditText) findViewById(R.id.smtshangliaoslot_mzedit);
	    mzedit.setOnKeyListener(new  OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == keyCode&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    
				
				String[] paras=new String[3];
				
				paras[0]=moid;
				paras[1]=line;
				paras[2]=mzedit.getText().toString();
				progressDialog.setMessage("正在获取信息...");
				showProDia();
				smtshangliaoslotModel.shouliao(new Task(Smtshangliaoslot.this,200,paras));
			   
			}			
				
				return false;
			}
		});
		   
		editscan=(EditText) findViewById(R.id.smtjitchaoling_editscan);


		
	}
	


	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		List<HashMap<String,String>> getdatalist;
		switch (task.getTaskType()) {	
		case 100:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdatalist = (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdatalist);
				spinnermo.setAdapter(new MOadapter(getdatalist));
			} else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;
		case 200:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdatalist = (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdatalist);
				if(getdatalist.size()==0){
					editscan.setText("");
					logSysDetails("获取该槽位信息失败，请检查工单和物料条码", "成功");
					mzedit.selectAll();
					mzedit.requestFocus();
					SoundEffectPlayService
					.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
				     return;
				}  
				String slot = "";
				for(Map<String,String> map:getdatalist){
					slot=slot+map.get("slot")+",";
				}
				editscan.setText("");
				logSysDetails("成功获取该槽位信息为："+slot, "成功");
				SoundEffectPlayService
				.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
				mzedit.selectAll();
				mzedit.requestFocus();
				//spinnermo.setAdapter(new MOadapter(getdatalist));
			} else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;
		}
	}


		
   class  MOadapter extends  BaseAdapter{
	   List<HashMap<String, String>> list;
	   MOadapter(List<HashMap<String, String>> list){
		   this.list=list;
	   }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
	
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null){
				convertView=getLayoutInflater().inflate(R.layout.smtshangliaoslot_listview, null);
			}
			TextView tv=(TextView) convertView.findViewById(R.id.textView1);
			tv.setText(list.get(position).get("MOName"));
			return convertView;
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
