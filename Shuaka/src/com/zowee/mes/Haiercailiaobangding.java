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
import com.zowee.mes.model.BiaoqianheduiModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.HaiercailiaobangdingModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
/**
 * HAIER 平板程序 第一站  各材料用料绑定
 * */
public class Haiercailiaobangding extends CommonActivity implements
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

	private EditText editzoweesn;
	private EditText editlcdsn;
	private EditText editpcbasn;
	private EditText editdianchisn;

	
	private Button okbutton;
	private Button removebindbutton;
	

	private EditText editscan;

	

	private HaiercailiaobangdingModel haiercailiaobangdingModel; // 任务处理类
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Haiercailiaobangding";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heier_cailiaobangding);
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
								stopService(new Intent(Haiercailiaobangding.this,
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

		haiercailiaobangdingModel = new HaiercailiaobangdingModel();
		common4dmodel=new  Common4dModel();
		
		editMO = (EditText) findViewById(R.id.haiercailiaobangding_moedit);
		tvmo=(TextView) findViewById(R.id.haiercailiaobangding_motextview);
		editMOdescri = (EditText) findViewById(R.id.haiercailiaobangding_modescri);
		editMOProduct = (EditText) findViewById(R.id.haiercailiaobangding_moproduct);
		editMO.requestFocus();

		editzoweesn = (EditText) findViewById(R.id.haiercailiaobangding_zoweesnedit);
		editlcdsn = (EditText) findViewById(R.id.haiercailiaobangding_lcdsnedit);
		editpcbasn = (EditText) findViewById(R.id.haiercailiaobangding_pcbasnedit);
		editdianchisn = (EditText) findViewById(R.id.haiercailiaobangding_dianchisnedit);

	
	

		okbutton = (Button) findViewById(R.id.haiercailiaobangding_okbutton);
		okbutton.setFocusable(false);
		removebindbutton = (Button) findViewById(R.id.haiercailiaobangding_removebindbutton);
		removebindbutton.setFocusable(false);

		editscan = (EditText) findViewById(R.id.haiercailiaobangding_editscan);
		editscan.setFocusable(false);
		
		

		okbutton.setOnClickListener(this);
		removebindbutton.setOnClickListener(this);
		tvmo.setOnClickListener(this);
        
		editMO.setOnKeyListener(this);
		editdianchisn.setOnKeyListener(this);
		
		
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
					editzoweesn.requestFocus();
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
			
		case TaskType.haierbangdingcailiao:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String exception=getdata.get("I_ExceptionFieldName");
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						whorequestFocus(exception);
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						whorequestFocus(exception);
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
		//haierremovebangdingcailiao
			
		case TaskType.haierremovebangdingcailiao:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String exception=getdata.get("I_ExceptionFieldName");
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						whorequestFocus(exception);
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						whorequestFocus(exception);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.haiercailiaobangding_motextview:
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
			
		case R.id.haiercailiaobangding_removebindbutton:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("解除绑定关系");
			builder2.setMessage("是否需要解除条码间的绑定关系？");
			builder2.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							String[]  params=new String[6];
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = moname;
							params[5] = editzoweesn.getText().toString().trim();
							if(editzoweesn.getText().toString().equals("")){
								Toast.makeText(Haiercailiaobangding.this, "请先输入需要解绑的ZOWEE_SN", Toast.LENGTH_SHORT).show();
							}
							else{
								haiercailiaobangdingModel.removebangding(new Task(Haiercailiaobangding.this,TaskType.haierremovebangdingcailiao,params));
							}
						}
					});
			builder2.setNegativeButton("取消", null);
			builder2.create().show();
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
		case R.id.haiercailiaobangding_moedit:
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

		case R.id.haiercailiaobangding_dianchisnedit:
			String[]  params=new String[9];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(editzoweesn.getText().toString().trim().equals("")||editlcdsn.getText().toString().trim().equals("")||
						editpcbasn.getText().toString().trim().equals("")||editdianchisn.getText().toString().trim().equals("")){
					Toast.makeText(this, "确保需要的核对所有条码已全部扫描", Toast.LENGTH_LONG).show();
				}
				else{
					super.progressDialog.setMessage("材料绑定提交中...");
					super.showProDia();
					params[0] = useid;
					params[1] = usename;
					params[2] = resourceid;
					params[3] = resourcename;
					params[4] = moname;
					params[5] = editzoweesn.getText().toString().trim();
					params[6] = editlcdsn.getText().toString().trim();
					params[7] = editpcbasn.getText().toString().trim();
					params[8] = editdianchisn.getText().toString().trim();
					haiercailiaobangdingModel.bangding(new  Task(this,TaskType.haierbangdingcailiao,params));
				}
				
				
		}

		break;
		}
		return false;
	}

	private  void whorequestFocus(String exception){
		if(exception.equalsIgnoreCase("ZW_SN")){
			editzoweesn.requestFocus();
		}
		if(exception.equalsIgnoreCase("LCD_SN")){
			editlcdsn.requestFocus();
		}
		if(exception.equalsIgnoreCase("PCBA_SN")){
			editpcbasn.requestFocus();
		}
		if(exception.equalsIgnoreCase("BAT_SN")){
			editdianchisn.requestFocus();
		}
	}

}
