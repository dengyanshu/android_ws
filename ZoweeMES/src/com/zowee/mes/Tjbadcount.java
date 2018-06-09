package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.TjbadcountModel;
import com.zowee.mes.model.TjdipstartModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
/**
 * 天津程序
 * 1、统计不良计数 扫描固定代码ERRORCODE,自动提交到数据库
 * 2、没有条码，只是代替作业员不良数目的统计
 * */
public class Tjbadcount extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private EditText editMO;
	private  Button  mobutton;//点击跳转到另一个界面选择工单
	private String moid; // 工单选择保存变量
	private EditText editLine;
	private  Button  linebutton;//点击跳转到另一个界面选择xianti 
	private String lineid; 
	
	
	private EditText edithour;
	private EditText editnum;
	private int num=1;
	
	private EditText editsn;
	
	private EditText editscan;

	
	private TjbadcountModel tjbadcountModel; // 任务处理类
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Tjbadcount";
	private static final int REQUESTCODE = 0;
	private static final int REQUESTCODE2 = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tj_badcount);
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
								stopService(new Intent(Tjbadcount.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.biaoqianhedui; // 后台服务静态整形常量
		super.init();

		tjbadcountModel = new TjbadcountModel();
		common4dmodel=new  Common4dModel();
		
		
		editMO = (EditText) findViewById(R.id.tjbadcount_moedit);
		mobutton=(Button) findViewById(R.id.tjbadcount_mobutton);
		editLine = (EditText) findViewById(R.id.tjbadcount_lineedit);
		linebutton=(Button) findViewById(R.id.tjbadcount_linebutton);
		
		edithour = (EditText) findViewById(R.id.tjbadcount_houredit);
		editnum = (EditText) findViewById(R.id.tjbadcount_countnumedit);
		
		editsn = (EditText) findViewById(R.id.tjbadcount_snedit);

		editscan = (EditText) findViewById(R.id.tjbadcount_editscan);
		editscan.setFocusable(false);
		
		
		mobutton.setOnClickListener(this);
		linebutton.setOnClickListener(this);
		editsn.setOnKeyListener(this);
		
        common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));

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
		// 获取工单ID
		case TaskType.common4dmodelgetresourceid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("ResourceId")) {
						resourceid = getdata.get("ResourceId");
					}
					String logText = "程序已启动!检测到该平板的资源名称:[ " + resourcename
							+ " ],资源ID: [ " + resourceid + " ],用户ID: [ " + useid + " ]!!如需更换工单请点击“工单”2字！！";
					logSysDetails(logText, "程序");
				} else {
					logSysDetails(
							"通过资源名称获取在MES获取资源ID失败，请检查配置的资源名称是否正确", "成功");
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取资源id信息失败，请检查配置则资源名称是否正确", "成功");
			}

			break;
		
		
	
		case TaskType.tjbadcount:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "不良采集成功！";
						logSysDetails(scantext, "成功");
						if(edithour.getText().toString().trim().equals(new  Date().getHours()+"")){
							edithour.setText(new  Date().getHours()+"");
							editnum.setText(num+"");
							num++;
						}
						else{
							edithour.setText(new  Date().getHours()+"");
							num=1;
							editnum.setText(num+"");
							num++;
						}
							
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					
					}
					else{
						String scantext = "失败！";
						logSysDetails(scantext, "成功");
					}
					editsn.setText("");
					editsn.requestFocus();

				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails("提交MES失败请检查网络或者工单，请检查输入的条码", "成功");
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tjbadcount_mobutton:
			Intent  mointent=new Intent(this,Tjbadcountmoselect.class);
			startActivityForResult(mointent, REQUESTCODE);
			break;
		case R.id.tjbadcount_linebutton:
			Intent  lineintent=new Intent(this,Tjbadcountlineselect.class);
			startActivityForResult(lineintent, REQUESTCODE2);
			break;
		}
	}
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0) {
			if (requestCode == REQUESTCODE) {
				moid = data.getStringExtra("MOID");
				String moname = data.getStringExtra("MOName");
				String modescription = data.getStringExtra("MODescription");
				Log.i(TAG, "另一个界面返回的数据：：" + moid + "::" + moname);

				editMO.setText(moname);

			}

		}
		if (resultCode == 1) {
			if (requestCode == REQUESTCODE2) {
				lineid = data.getStringExtra("WorkcenterId");
				String WorkcenterName = data.getStringExtra("WorkcenterName");
				String WorkcenterDescription = data.getStringExtra("WorkcenterDescription");
				Log.i(TAG, "另一个界面返回的数据：：" + lineid + "::" + WorkcenterName);

				editLine.setText(WorkcenterName);

			}

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

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.tjbadcount_snedit:
			String[]  params=new String[2];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(moid==null||moid.equals("")||lineid==null||lineid.equals("")){
					Toast.makeText(this, "请先获取工单和线体信息 再启动条码！", Toast.LENGTH_LONG).show();
				   return  false;
				}
				else{
					if(editsn.getText().toString().trim().equalsIgnoreCase("ERRORCODE")){
						super.progressDialog.setMessage("不良提交中...");
						super.showProDia();
						
						params[0] = moid;
						params[1] = lineid;
						tjbadcountModel.badcount(new  Task(this,TaskType.tjbadcount,params));
					}
					else{
						Toast.makeText(this, "不良固定代码错误！", Toast.LENGTH_LONG).show();
					}
					
				}
			}
			break;
		}
		return false;
	}

	

}
