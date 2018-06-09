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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtIPQCModel;
import com.zowee.mes.model.SmtreceivematerialModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class Smtreceivematerial extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	private GetMOnameModel GetMonamemodel; // 任务处理类
	
   private   Spinner spinner;
   private  EditText  usenameedit;
   private  EditText  mzlotsnedit;
   private  ListView listview;
   private  Button button;
   private   ListView listview2;
   
   private  String  workcenterid;
   
   List<HashMap<String, String>> mzs;
   List<HashMap<String, String>> mz;
   
  
	private SmtreceivematerialModel smtreceivematerialModel; // 任务处理类
	private static final String TAG = "Smtreceivematerial";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_smt_shouliao);
		init();
	}

	protected void onResume() {
		super.onResume();
		super.progressDialog.setMessage("正在获取线体资源...");
		super.showProDia();
		smtreceivematerialModel.getworkcenter(new Task(this,TaskType.smtreceivematerialgetworkcenter," "));
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
								stopService(new Intent(Smtreceivematerial.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.smtreceivematerialscan; // 后台服务静态整形常量
		
		super.init();
        
		smtreceivematerialModel = new SmtreceivematerialModel();
		GetMonamemodel=new GetMOnameModel();

		spinner=(Spinner) findViewById(R.id.smtshouliao_spinner);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				String workentername= (String) arg0.getItemAtPosition(arg2);
				smtreceivematerialModel.getworkcenteridbyworkcentername(new Task(Smtreceivematerial.this,TaskType.smtreceivematerialgetworkcenterid,workentername));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		usenameedit=(EditText) findViewById(R.id.smtshouliao_usenameedit);
		mzlotsnedit = (EditText) findViewById(R.id.smtshouliao_mzedit);
		listview=(ListView) findViewById(R.id.smtshouliao_listview);
		listview.setCacheColorHint(Color.TRANSPARENT);
		listview2=(ListView) findViewById(R.id.smtshouliao_listview2);
		listview2.setCacheColorHint(Color.TRANSPARENT);
		
		button = (Button) findViewById(R.id.smtshouliao_button);
		button.setFocusable(false);
		button.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
				builder.setTitle("确认接收，请注意！");
				builder.setMessage("    请核对物料信息？");
				builder.setPositiveButton("确定",new  DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
//						if(mz==null||mz.size()==0){
//							Toast.makeText(Smtreceivematerial.this, "不能提交，因为未得到核对信息", Toast.LENGTH_LONG).show();
//							return;
//						}
//						for(HashMap<String, String> map:mz){
//						   if(	map.containsValue("NO"))
//						   {
//							   Toast.makeText(Smtreceivematerial.this, "不能提交，因为有错误物料条码", Toast.LENGTH_LONG).show();
//							   return;
//						   }
//						}
					
						// TODO Auto-generated method stub
						progressDialog.setMessage("正在获取出货单号信息...");
						showProDia();
						String[]  paras=new String[7];
						paras[0]=workcenterid;
						paras[1]=usenameedit.getText().toString();
						paras[2]=mzlotsnedit.getText().toString();
						paras[3]=resourceid;
						paras[4]=resourcename;
						paras[5]=useid;
						paras[6]=usename;
						
						smtreceivematerialModel.check(new Task(Smtreceivematerial.this,TaskType.smtreceivematerialcheck,paras));
					}

					
				});
				builder.setNegativeButton("取消", null);
				builder.create().show();
				
			}
		});

		
		
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
		
		case TaskType.GetResourceId:
			resourceid = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				//logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
				Toast.makeText(this, "资源名称获取失败，请检查", 0).show();
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			resourceid = getresult.get(0).get("ResourceId");
			if(resourceid.isEmpty())	 
				//logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
				Toast.makeText(this, "资源名称获取失败，请检查", 0).show();
			else{
				//logSysDetails("成功获取到该设备的资源ID:"+resourceid,"成功");
				//Toast.makeText(this, "资源名称获取失败，请检查", 0).show();
			}
		break;
		
		case TaskType.smtreceivematerialscan:
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
			break;
			
		case TaskType.smtreceivematerialgetworkcenter:
			super.closeProDia();
			if (null != task.getTaskResult()) {
			    List<HashMap<String, String>> workcenters = (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + workcenters);
				List<String>  workcenterstrings=new  ArrayList<String>();
				for(HashMap<String, String> map:workcenters){
					workcenterstrings.add(map.get("workcentername"));
				}
				ArrayAdapter  adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, workcenterstrings);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);
			} else {
				
			}
			break;
			
		case TaskType.smtreceivematerialgetworkcenterid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null&&getdata.size()!=0) {
					workcenterid = getdata.get("workcenterid");
					Log.i(TAG,"你选择的工单线体id是："+workcenterid);
				} else {
					
				}
				closeProDia();
			} else {
				
			}
			break;	
			
//		case TaskType.smtreceivematerialgetmzs:
//			super.closeProDia();
//			if (null != task.getTaskResult()) {
//			    mzs = (List<HashMap<String, String>>) task.getTaskResult();
//			   
//			    Log.d(TAG, "task的结果数据是：" + mzs);
//				SimpleAdapter  adapter=new   SimpleAdapter(this,mzs,R.layout.activity_smtshouliao_adapter,
//						new String[]{"lotsn","qty","productname"},new  int[]{R.id.smtshouliao_adapter_1,R.id.smtshouliao_adapter_2,R.id.smtshouliao_adapter_3});
//				listview.setAdapter(adapter);
//			
//				   mz = new  ArrayList<HashMap<String,String>>();
//				   HashMap<String, String>  map=new HashMap<String, String>();
//				   map.put("lotsn", mzlotsnedit.getText().toString().toUpperCase());
//				   map.put("torf", "OK");
//				   mz.add(map);
//					SimpleAdapter  adapter2=new   SimpleAdapter(this,mz,R.layout.activity_smtshouliao_adapter2,
//							new String[]{"lotsn","torf",},new  int[]{R.id.smtshouliao_adapter2_1,R.id.smtshouliao_adapter2_2});
//					listview2.setAdapter(adapter2);
//				
//			} else {
//				Toast.makeText(this, "连接MES信息为空，请检查WEBSERVICE设置!", 1).show();
//				usenameedit.requestFocus();
//				mzlotsnedit.requestFocus();
//				mzlotsnedit.setSelectAllOnFocus(true);
//			}
//		break;
			
		
		case TaskType.smtreceivematerialcheck:
			
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				String stockoutname = null;
				if(getdata.containsKey("stockoutname"))
					stockoutname=getdata.get("stockoutname");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
                        Toast.makeText(this, "核对成功出库单号"+stockoutname, 0).show();
//						SoundEffectPlayService
//								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					
						String[]  paras=new String[8];
						paras[0]=workcenterid;
						paras[1]=usenameedit.getText().toString();
						paras[2]=mzlotsnedit.getText().toString();
						paras[3]=stockoutname;
						
						paras[4]=resourceid;
						paras[5]=resourcename;
						paras[6]=useid;
						paras[7]=usename;
						
						smtreceivematerialModel.submit(new Task(Smtreceivematerial.this,TaskType.smtreceivematerialsubmit,paras));
					
					}
					else{
						super.closeProDia();
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
//						 mzs.clear();
//	                    listview.setAdapter(null);
//	                    mz.clear();
//	                    listview2.setAdapter(null);
		                    
						AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
						builder.setTitle("物料核对情况，请注意！");
						builder.setMessage(getdata.get("I_ReturnMessage"));
						builder.setPositiveButton("确定",null) ;
						builder.setNegativeButton("取消", null);
						builder.create().show();
						 
						
						usenameedit.requestFocus();
						mzlotsnedit.requestFocus();
						
//						String scantext ="失败！"+getdata.get("I_ReturnMessage");
//						logSysDetails(scantext, "成功");
					}
					

				} else {
					//logSysDetails("在MES获取信息为空或者解析结果为空，请检查再试!"+ getdata.get("Error"), "成功");
				}
				
			} else {
				//logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
		break;
			
		case TaskType.smtreceivematerialsubmit:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
				
						AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
						builder.setTitle("物料接收情况，请注意！");
						builder.setMessage(getdata.get("I_ReturnMessage"));
						builder.setPositiveButton("确定",null) ;
						builder.setNegativeButton("取消", null);
						builder.create().show();
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
						builder.setTitle("物料核对情况，请注意！");
						builder.setMessage(getdata.get("I_ReturnMessage"));
						builder.setPositiveButton("确定",null) ;
						builder.setNegativeButton("取消", null);
						builder.create().show();
					}
					usenameedit.requestFocus();
					mzlotsnedit.requestFocus();
					
				} else {
//					logSysDetails(
//							"在MES获取信息为空或者解析结果为空，请检查再试!"
//									+ getdata.get("Error"), "成功");
				}
			} else {
//				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;	
		
//		case TaskType.smtreceivematerialsubmit:
//			super.closeProDia();
//			if (null != task.getTaskResult()) {
//				getdata = (HashMap<String, String>) task.getTaskResult();
//				Log.d(TAG, "task的结果数据是：" + getdata);
//				if (getdata.get("Error") == null&&getdata.size()!=0) {
//
//                    //task的结果数据是：{I_ReturnMessage=ServerMessage:此领料单产品接收成功，产生领料单号为： L0602150912915, I_ReturnValue=0}				
////                    mzs.clear();
////                    listview.setAdapter(null);
////                    mz.clear();
////                    listview2.setAdapter(null);
//					SoundEffectPlayService
//							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
//					
//					AlertDialog.Builder builder = new AlertDialog.Builder(Smtreceivematerial.this);
//					builder.setTitle("物料接收情况，请注意！");
//					builder.setMessage(getdata.get("I_ReturnMessage"));
//					builder.setPositiveButton("确定",null) ;
//					builder.setNegativeButton("取消", null);
//					builder.create().show();
//					
//					usenameedit.requestFocus();
//					mzlotsnedit.requestFocus();
//					
//				} else {
//					
//				}
//			} else {
//				
//			}
//
//			break;
			
			
		
			
		
			
		
		
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
		    if(usenameedit.isFocused()){
		    	usenameedit.setText(scanRes);
		    	mzlotsnedit.requestFocus();
		    	
		    }
		    else if(mzlotsnedit.isFocused()){
		    	mzlotsnedit.setText(scanRes);
//		    	    if(mzs==null||mzs.size()==0){
//					
//		    	    	super.progressDialog.setMessage("正在获取信息...");
//		    	    	super.showProDia();
//		    	    	smtreceivematerialModel.getmzs(new Task(this,TaskType.smtreceivematerialgetmzs,mzlotsnedit.getText().toString()));
//					}
//					else{
//						
//						HashMap<String, String>  map=new HashMap<String, String>();
//						map.put("lotsn", mzlotsnedit.getText().toString().toUpperCase());
//						String okorno="NO";
//						for(HashMap<String, String>  hashmap:mzs){
//							hashmap.containsValue(mzlotsnedit.getText().toString().toUpperCase());
//							okorno="OK";
//						}
//						map.put("torf", okorno);
//						mz.add(map);
//						SimpleAdapter  adapter2=new   SimpleAdapter(this,mz,R.layout.activity_smtshouliao_adapter2,
//								new String[]{"lotsn","torf",},new  int[]{R.id.smtshouliao_adapter2_1,R.id.smtshouliao_adapter2_2});
//						listview2.setAdapter(adapter2);
//					}
		    }
		    
	}
		

//	@Override
//	public boolean onKey(View v, int keycode, KeyEvent event) {
//		switch (v.getId()) {
//
//		case R.id.smtshouliao_mzedit:
//			if (KeyEvent.KEYCODE_ENTER == keycode
//			&& event.getAction() == KeyEvent.ACTION_DOWN) {
//				
//				
////				if(mzs==null||mzs.size()==0){
////				
////				super.progressDialog.setMessage("正在获取信息...");
////				super.showProDia();
////				smtreceivematerialModel.getmzs(new Task(this,TaskType.smtreceivematerialgetmzs,mzlotsnedit.getText().toString()));
////				}
////				else{
////					String okorno="NO";
////					for(HashMap<String, String>  hashmap:mzs){
////						if(hashmap.containsValue(mzlotsnedit.getText().toString().toUpperCase()))
////						  okorno="OK";
////						else{
////							
////						}
////					}
////					HashMap<String, String>  map=new HashMap<String, String>();
////					map.put("lotsn", mzlotsnedit.getText().toString().toUpperCase());
////					map.put("torf", okorno);
////					mz.add(map);
////					SimpleAdapter  adapter2=new   SimpleAdapter(this,mz,R.layout.activity_smtshouliao_adapter2,
////							new String[]{"lotsn","torf",},new  int[]{R.id.smtshouliao_adapter2_1,R.id.smtshouliao_adapter2_2});
////					listview2.setAdapter(adapter2);
////				}
//				
//				
//				//不要物料核对功能了 直接收料
//				
//			}			
//			break;		
//		}
//		return  false;
//	}
	
	

}
