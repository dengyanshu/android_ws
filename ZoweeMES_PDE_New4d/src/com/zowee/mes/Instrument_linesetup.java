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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.InstrumentModel;
import com.zowee.mes.model.TjchuhuoscanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class Instrument_linesetup extends CommonActivity implements
		OnKeyListener
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
   private  EditText  snedit;
   private  EditText  workcenteredit;
   private  String  workcenterid;
 
   private  EditText  editscan;
   
    private GetMOnameModel GetMonamemodel; // 任务处理类
	private InstrumentModel instrumentModel; // 任务处理类
	private static final String TAG = "Instrument_linesetup";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_instrument_linesetup);
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
								stopService(new Intent(Instrument_linesetup.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.instrumentscan; // 后台服务静态整形常量
		//扫描数据也是个后台任务
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		instrumentModel = new InstrumentModel();
		
	    snedit=(EditText) findViewById(R.id.instrument_linesetup_sn);
	    workcenteredit=(EditText) findViewById(R.id.instrument_linesetup_workcenter);
	   
		editscan=(EditText) findViewById(R.id.instrument_linesetup_editscan);
		editscan.setText("使用说明：\n1、请检查该电脑的设置的资源名称是否正确、合法、有注册！\n2、系统会自动根据资源名称检测工作中心，请先获取正确的工作中心，再开始设备绑定！");

		snedit.setOnKeyListener(this);
		
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
		HashMap<String, String>  getdata;
		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		switch (task.getTaskType()) {
		case TaskType.instrumentscan:
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
			break;
			
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
				getworkcenter(resourceid);
			}
		break;
		
		case TaskType.instrumentgetworkcenter:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					
					workcenterid = getdata.get("WorkcenterId");
					String workcentername = getdata.get("WorkcenterName");
					workcenteredit.setText(workcentername);
					String scantext = "通过电脑资源名：[" + resourcename + "]成功的获得该工作中心:"+ workcentername+ "!";
					logSysDetails(scantext, "成功");

				} else {
					logSysDetails(
							"在MES获取工单信息为空或者解析结果为空，请检查!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取信息失败，请检查！", "成功");
			}

			break;
		
		
		
		case TaskType.instrumentlinesetup:
			super.closeProDia();
			String sn = snedit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					clean();
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
					snedit.requestFocus();
					snedit.setText("");
					
				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
			} else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;
		
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
		    if(snedit.isFocused()){
		    	snedit.setText(scanRes);
		    	if(workcenterid!=null&&!workcenterid.equals("")){
		    	String[] paras=new String[6];
				paras[0]=resourceid;
				paras[1]=resourcename;
				paras[2]=useid;
				paras[3]=usename;
				paras[4]=workcenterid;
				paras[5]=snedit.getText().toString().trim();
				
				super.progressDialog.setMessage("正在获取信息...");
				super.showProDia();
				instrumentModel.linesetup(new Task(this,TaskType.instrumentlinesetup,paras));
		    	}	
		    }
		    
		    
	}

	
	private  void  clean(){
		if(editscan.getLineCount()>10){
			editscan.setText("");
		}
	}
	
   private void getworkcenter(String resourceid){
	   super.progressDialog.setMessage("正在获取信息...");
	super.showProDia();
	   instrumentModel.getworkcenter(new  Task(this,TaskType.instrumentgetworkcenter,resourceid));
   }
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.instrument_linesetup_sn:
			String[] paras=new String[6];
			paras[0]=resourceid;
			paras[1]=resourcename;
			paras[2]=useid;
			paras[3]=usename;
			paras[4]=workcenterid;
			paras[5]=snedit.getText().toString().trim();
			
			
			
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(workcenterid!=null&&!workcenterid.equals("")){
				    super.progressDialog.setMessage("正在获取信息...");
					super.showProDia();
				    instrumentModel.linesetup(new Task(this,TaskType.instrumentlinesetup,paras));
				}
			}			
		break;

		
		}
	return  false;
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
