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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.AnxuzhuangxiangModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SmtxgModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;


/**
 * COPY  FOR  XIAOMI
 * */
public class SMTXGXM extends CommonActivity implements
		android.view.View.OnKeyListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private  EditText editMO;
	private  String moid; // 工单选择保存变量
	private  String moname;

	private EditText lotsn_edit;
	private EditText xglotsn_edit;
	
   
	private  RadioGroup  radioGroup;
	
	

	private EditText editscan;

	

	private SmtxgModel smtxgModel; // 任务处理类
	private  Common4dModel  common4dmodel;
	private static final String TAG = "SMTXG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_smtxgxm);
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
								stopService(new Intent(SMTXGXM.this,
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

		smtxgModel = new SmtxgModel();
		common4dmodel=new  Common4dModel();
		
		editMO = (EditText) findViewById(R.id.smtxgxm_mo);
		editMO.requestFocus();
		
	

		lotsn_edit = (EditText) findViewById(R.id.smtxgxm_lot);
		xglotsn_edit = (EditText) findViewById(R.id.smtxgxm_xglot);
		

		editscan = (EditText) findViewById(R.id.smtxgxm_editscan);
		editscan.setFocusable(false);
		
		
		
		radioGroup=(RadioGroup) findViewById(R.id.smtxgxm_absidegroup);
		
		/*radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				  if(id)
			}
		});*/
        
		editMO.setOnKeyListener(this);
		lotsn_edit.setOnKeyListener(this);
		xglotsn_edit.setOnKeyListener(this);
		
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
		
		
		case TaskType.common4dmodelgetmobylotsn:
			super.closeProDia();
			String lotsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					moname = getdata.get("MOName");
					moid = getdata.get("MOId");
					

					editMO.setText(moname);
					editMO.setEnabled(false);
					xglotsn_edit.requestFocus();
					String scantext = "通过批次号：[" + lotsn + "]成功的获得工单:"
							+ moname + ",工单id:"+moid+"!";
					logSysDetails(scantext, "成功");
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);

				} else {
					logSysDetails(
							"通过批次号：[" + lotsn + "]在MES获取工单信息为空或者解析结果为空，请检查SN!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails(lotsn + "在MES获取工单信息失败，请检查输入的条码", "成功");
			}

			break;
			
		case TaskType.anxuzhuangxiang:
			Clear();
			lotsn_edit.setText("");
			lotsn_edit.requestFocus();
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
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
					}

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
   
	public void Clear() {
		if (editscan.getLineCount() > 20)
			editscan.setText("");
	}
	
	
	@SuppressLint("DefaultLocale")
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case  R.id.smtxgxm_xglot:
			if (KeyEvent.KEYCODE_ENTER == keycode
			&& event.getAction() == KeyEvent.ACTION_DOWN) {
				xglotsn_edit.setEnabled(false);
				//xglotsn_edit.requestFocus();
				lotsn_edit.requestFocus();
			}
			break;
		
			
		case R.id.smtxgxm_mo:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String param = editMO.getText().toString().toUpperCase().trim();
			   if(param.length()<8){
				   Toast.makeText(this, "批次号长度不对", Toast.LENGTH_SHORT).show();
				   return false;
			   }
			 
			   if(  radioGroup.getCheckedRadioButtonId()==R.id.smtxgxm_baside){
				   super.progressDialog.setMessage("正在数据库获取工单");
					super.showProDia();
				    common4dmodel.getMObylotsnxm(new Task(this,TaskType.common4dmodelgetmobylotsn,param));
			   }
			   else{
				   super.progressDialog.setMessage("正在数据库获取工单");
					super.showProDia();
				    common4dmodel.getMObylotsnxm2(new Task(this,TaskType.common4dmodelgetmobylotsn,param));
			   }
				    
			}
		break;

		case R.id.smtxgxm_lot:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String xg=xglotsn_edit.getText().toString().trim();
				String lotsn=lotsn_edit.getText().toString().trim();
				
				if(moid==null||"".equals(moid)||xg==null||"".equals(xg)||lotsn==null||"".equals(lotsn)){
					Toast.makeText(this, "工单和锡膏条码信息不能为空", Toast.LENGTH_LONG).show();
					return  false;
				}
				String[]  params=new String[7];
				params[0] = useid;
				params[1] = usename;
				params[2] = resourceid;
				params[3] = resourcename;
				params[4] = moid;
				params[5] = xg;
				params[6] = lotsn;
				
				
					super.progressDialog.setMessage("装箱提交中");
					super.showProDia();
					
					
					smtxgModel.xgxm(new  Task(this,TaskType.anxuzhuangxiang,params));
				
			}
			break;
		}
		return false;
	}



}
