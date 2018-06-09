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
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtChaolingModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

public class Xbcunbinding extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
	
	private  String moname;
	private  Spinner  moSpinner;
	private  List<HashMap<String, String>>  moinfo;
	private  List<String>  monames=new ArrayList<String>();
	private ArrayAdapter<String>  moadapter;
	private  EditText   moedit;
	
	private  String line;
	private Spinner  workcenterSpinner;
	private  List<String>  workcenters=new ArrayList<String>();
	private  List<HashMap<String, String>>  workcenterinfo;
	private ArrayAdapter<String>  adapter;
	

	
	private  ListView  lv;
	private  List<HashMap<String, String>>  mzs=new ArrayList<HashMap<String,String>>();
	
   private  EditText  mzedit;
   
   
   private  EditText  editscan;
   

   private GetMOnameModel GetMonamemodel; // 任务处理类
	private SmtChaolingModel smtChaolingModel; // 任务处理类
	private static final String TAG = "Smtjitshouliao";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_xbc_unbinding);
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
								stopService(new Intent(Xbcunbinding.this,
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
		smtChaolingModel = new SmtChaolingModel();
		
	   moSpinner=(Spinner) findViewById(R.id.xbcunbinding_mo);
	   moadapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monames);
	   moadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	   moSpinner.setAdapter(moadapter);	
	   moSpinner.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				 moname=arg0.getItemAtPosition(arg2).toString();
				/*for(HashMap<String, String> map:moinfo){
					if(map.containsValue(moname)){
						moid=map.get("MOId");
						System.out.println("you choose ==="+moname+",moid"+moid);
						break;
					}
				}*/
			}
	
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	   });
	   
	   
	   moedit=(EditText) findViewById(R.id.xbcunbinding_moedit);
	   moedit.setOnKeyListener(new OnKeyListener() {
		@Override
		public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
			// TODO Auto-generated method stub
			if (KeyEvent.KEYCODE_ENTER == arg1&& arg2.getAction() == KeyEvent.ACTION_DOWN){
				 String moseelct_str=moedit.getText().toString();
				 if(moseelct_str.length()<3){
					 Toast.makeText(Xbcunbinding.this, "必须输入尾号3位以上进行筛选", Toast.LENGTH_SHORT).show();
				      return false;
				 }
				 //开始筛选
				 getmo(moseelct_str);
			}
			return false;
		}
	 });
	   
	   
	   
	   workcenterSpinner=(Spinner) findViewById(R.id.xbcunbinding_line);
	   adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, workcenters);
	   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	   workcenterSpinner.setAdapter(adapter);
	   workcenterSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				line=arg0.getItemAtPosition(arg2).toString();
				/*for(HashMap<String, String> map:workcenterinfo){
					if(map.containsValue(workcentername)){
						workcenterid=map.get("WorkcenterId");
						System.out.println("you choose ==="+workcentername+",moid"+workcenterid);
						break;
					}
				}*/
				
			}
	
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	    });
			   
			   
		mzedit = (EditText) findViewById(R.id.xbcunbinding_mz);
		lv=(ListView) findViewById(R.id.xbcunbinding_lv);
		lv.setAdapter(lv_adapter);
		editscan=(EditText) findViewById(R.id.xbcunbinding_editscan);

		mzedit.setOnKeyListener(new  OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if (KeyEvent.KEYCODE_ENTER == keyCode&& event.getAction() == KeyEvent.ACTION_DOWN) {
					      
					String[] paras=new String[9];
					paras[0]=resourceid;
					paras[1]=resourcename;
					paras[2]=useid;
					paras[3]=usename;
					
					paras[4]=moname;
					paras[5]=line;
					paras[6]=mzedit.getText().toString().trim();
					progressDialog.setMessage("正在获取信息...");
					showProDia();
					smtChaolingModel.xbcunbinding(new Task(Xbcunbinding.this,300,paras));
				   
				}			
				
				return false;
			}
		});
		
        GetResourceId();
        getWorkcenter();
        //getmo();
	}
	private void GetResourceId()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
		GetMonamemodel.GetResourceId(task);

	}
	
	private void getWorkcenter()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, 200, "");	
		smtChaolingModel.get_workcenter(task);

	}
	
	private  void  getmo(String str){
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		smtChaolingModel.getmo_jit(new Task(Xbcunbinding.this,100,str));
		
	}

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
		

		case 100:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				moinfo = (List<HashMap<String,String>>)task.getTaskResult();
				//MOId	MOName	ProductName	ProductDescription	MOQtyRequired	ProductId
				monames.clear();
				for(HashMap<String, String>  map :moinfo){
					monames.add(map.get("MOName"));
				}
				moadapter.notifyDataSetChanged();	
			} 
			
			else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;
			
			
		case 200:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				workcenterinfo = (List<HashMap<String,String>>)task.getTaskResult();
				//WorkcenterId	WorkcenterName	SiteId
			
				
				for(HashMap<String, String>  map :workcenterinfo){
					workcenters.add(map.get("WorkcenterName"));
				}
				adapter.notifyDataSetChanged();	
			} 
			
			else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
		break;

		case 300:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				//回刷网格的返回的表 与 result和returnMessage
				List<HashMap<String, String>> binding_list = (List<HashMap<String, String>>) task.getTaskResult();
				int size=binding_list.size();
				if(size==0){
					logSysDetails("提交MES获取mes返回信息为空", "成功");
					return ;
				}
				getdata=binding_list.get(size-1);
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
					

				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
				
				//模拟orbit 刷新网格
				if(size>1){
					binding_list.remove(size-1);
					mzs=binding_list;
					lv_adapter.notifyDataSetChanged();
				}
				else{
					mzs=new ArrayList<HashMap<String,String>>();
					lv_adapter.notifyDataSetChanged();
				}
				
				mzedit.setText("");
				mzedit.requestFocus();
				
				closeProDia();
			} else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;
			
			
			
		case 400:
			super.closeProDia();
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
	
	private  BaseAdapter  lv_adapter=new BaseAdapter() {
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			View   v=null;
			if(arg1==null){
			    v= View.inflate(Xbcunbinding.this, R.layout.list_layout3, null);
			}
			else{
				v=arg1;
			}
			TextView  tv1=(TextView) v.findViewById(R.id.list_layout3_1);
			TextView  tv2=(TextView) v.findViewById(R.id.list_layout3_2);
			TextView  tv3=(TextView) v.findViewById(R.id.list_layout3_3);
			tv1.setText(mzs.get(arg0).get("料号"));
			tv2.setText(mzs.get(arg0).get("库位"));
			tv3.setText(mzs.get(arg0).get("可解绑数量"));
			return v;
		}
		
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mzs.size();
		}
	};
	

	
	
}
