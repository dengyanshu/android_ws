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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.BiaoqianheduiModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SmtFvmiModel;
import com.zowee.mes.model.SntocarModel;
import com.zowee.mes.model.TiebiaozhuandsnModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//天津紫米OQC程序
public class Tjzimioqc extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private EditText editMO;
	private  TextView  tvmo;//点击换取工单
	private EditText editMOProduct;
	private EditText editMOdescri;
	private String moid; // 工单选择保存变量
	private  String moname;

	private EditText editdanbansn;
	private EditText editkehujixing;
	private EditText editmac;
	private EditText editdsn;
	private EditText editkehusn;

	
	private Button okbutton;
	

	private EditText editscan;

	

	private TiebiaozhuandsnModel tiebiaozhuandsnmodel; // 任务处理类
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Tiebiaozhuandsn";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tianji_zimioqc);
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
								stopService(new Intent(Tjzimioqc.this,
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

		tiebiaozhuandsnmodel = new TiebiaozhuandsnModel();
		common4dmodel=new  Common4dModel();
		
		editMO = (EditText) findViewById(R.id.tiebiaozhuandsn_moedit);
		tvmo=(TextView) findViewById(R.id.tiebiaozhuandsn_motextview);
		editMOdescri = (EditText) findViewById(R.id.tiebiaozhuandsn_modecripedit);
		editMOProduct = (EditText) findViewById(R.id.tiebiaozhuandsn_moproductedit);
		editMO.requestFocus();

		editdanbansn = (EditText) findViewById(R.id.tiebiaozhuandsn_danbansnedit);
		editkehujixing = (EditText) findViewById(R.id.tiebiaozhuandsn_kehujixingedit);
		editmac = (EditText) findViewById(R.id.tiebiaozhuandsn_macedit);
		editdsn = (EditText) findViewById(R.id.tiebiaozhuandsn_dsnedit);
		editkehusn = (EditText) findViewById(R.id.tiebiaozhuandsn_kehusnedit);

		okbutton = (Button) findViewById(R.id.tiebiaozhuandsn_okbutton);
		okbutton.setFocusable(false);

		editscan = (EditText) findViewById(R.id.tiebiaozhuandsn_editscan);
		editscan.setFocusable(false);
		
		okbutton.setOnClickListener(this);
		tvmo.setOnClickListener(this);
        
		editMO.setOnKeyListener(this);
		editkehusn.setOnKeyListener(this);
		
		
		
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
					String productdescri = getdata.get("ProductDescription");
					String material = getdata.get("productName");
					moid = getdata.get("MOId");
					

					editMO.setText(moname);
					editMOdescri.setText(productdescri);
					editMOProduct.setText(material);
					editMO.setEnabled(false);
					editdanbansn.requestFocus();
					String scantext = "通过批次号：[" + lotsn + "]成功的获得工单:"
							+ moname + ",工单id:"+moid+",产品信息:" + productdescri + ",产品料号："
							+ material + "!";
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
			
		case TaskType.tiebiaozhuandsn:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String exception=getdata.get("I_ExceptionFieldName");
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						whorequestFocus(exception);
						String scantext = "贴标成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						whorequestFocus(exception);
						String scantext = "贴标转DSN失败！"+getdata.get("I_ReturnMessage");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tiebiaozhuandsn_motextview:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("更换工单");
			builder.setMessage("是否需要重新更换工单？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							editMO.setText("");
							editMO.setEnabled(true);
							editMO.requestFocus();
							editMOdescri.setText("");
							editMOProduct.setText("");
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create().show();
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

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.tiebiaozhuandsn_moedit:
			String param = editMO.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
			   if(param.length()<8){
				   Toast.makeText(this, "批次号长度不对", Toast.LENGTH_SHORT).show();
			   }
			   else{
				    super.progressDialog.setMessage("正在数据库获取工单");
					super.showProDia();
				   common4dmodel.getMObylotsn(new Task(this,TaskType.common4dmodelgetmobylotsn,param));
			   }
			}
			break;

		case R.id.tiebiaozhuandsn_kehusnedit:
			String[]  params=new String[11];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(editdanbansn.getText().toString().trim().equals("")||editkehujixing.getText().toString().trim().equals("")||
						editmac.getText().toString().trim().equals("")||editdsn.getText().toString().trim().equals("")||editkehusn.getText().toString().trim().equals("")){
					Toast.makeText(this, "确保需要的所有条码已全部扫描", Toast.LENGTH_LONG).show();
				}
				else{
					super.progressDialog.setMessage("光模块转DSN提交中");
					super.showProDia();
					params[0] = useid;
					params[1] = usename;
					params[2] = resourceid;
					params[3] = resourcename;
					params[4] = moid;
					params[5] = moname;
					params[6] = editdanbansn.getText().toString().trim();
					params[7] = editkehujixing.getText().toString().trim();
					params[8] = editmac.getText().toString().trim();
					params[9] = editdsn.getText().toString().trim();
					params[10] = editkehusn.getText().toString().trim();
					
					tiebiaozhuandsnmodel.tiebiaozhuandsn(new  Task(this,TaskType.tiebiaozhuandsn,params));
				}
				
				
			}

			break;
		}
		return false;
	}
	private  void whorequestFocus(String exception){
		if(exception.equalsIgnoreCase("PCBASN")){
			editdanbansn.requestFocus();
		}
		if(exception.equalsIgnoreCase("CustModel")){
			editkehujixing.requestFocus();
		}
		if(exception.equalsIgnoreCase("MAC")){
			editmac.requestFocus();
		}
		if(exception.equalsIgnoreCase("DSN")){
			editdsn.requestFocus();
		}
		if(exception.equalsIgnoreCase("CustSN")){
			editkehusn.requestFocus();
		}
	}
	

}
