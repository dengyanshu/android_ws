package com.zowee.mes;
import java.text.SimpleDateFormat;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.AssyqidongModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//联想启动条码 
public class Assyqidong extends CommonActivity implements
		android.view.View.OnKeyListener,OnClickListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private EditText editMO;
	private TextView textviewmo;
	private EditText editMOProduct;
	private EditText editMOdescri;
	private String moid;    //工单选择保存变量
	private String moname;  //工单名字

	private EditText editsnstart;  //启动的SN开始
	private EditText editsnend;  //启动的SN结束
	private EditText editsn;  //启动的SN

	private EditText editscan;

	
	private AssyqidongModel assyqidongModel; // 任务处理类
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Anxuzhuangxiang";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_liulou_assyqidong);
		Log.i(TAG,"oncreate--------有执行");
		init();
	}

	protected void onResume() {
		super.onResume();
		Log.i(TAG,"onresume--------有执行");
//		super.progressDialog.show();
//		super.progressDialog.setMessage("开始从MES获取工单信息。。。。");
//		assyqidongModel.getmo(new Task(this,TaskType.liulouassyqidonggetmo,""));
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
								stopService(new Intent(Assyqidong.this,
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

		assyqidongModel = new AssyqidongModel();
		common4dmodel=new  Common4dModel();
		
		editMO = (EditText) findViewById(R.id.assyqidong_moedit);
		textviewmo = (TextView) findViewById(R.id.assyqidong_motextview);
		editMOdescri = (EditText) findViewById(R.id.assyqidong_modescri);
		editMOProduct = (EditText) findViewById(R.id.assyqidong_moproduct);
        
		editsnstart=(EditText) findViewById(R.id.assyqidong_snstartedit);
		editsnend=(EditText) findViewById(R.id.assyqidong_snendedit);
		editsn = (EditText) findViewById(R.id.assyqidong_snedit);
		
		editscan = (EditText) findViewById(R.id.assyqidong_editscan);
		editscan.setFocusable(false);
		
		editsn.setOnKeyListener(this);
		editMO.setOnKeyListener(this);
		textviewmo.setOnClickListener(this);
		
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
							+ " ],资源ID: [ " + resourceid + " ],用户ID: [ " + useid + " ]!!";
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
			
		case TaskType.liulouassyqidonggetmobymoname:
			super.closeProDia();
			String lotsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					
					moname = getdata.get("MOName");
					String productdescri = getdata.get("ProductDescription");
					String material = getdata.get("ProductName");
					moid = getdata.get("MOId");
					

					editMO.setText(moname);
					editMOdescri.setText(productdescri);
					editMOProduct.setText(material);
					editMO.setEnabled(false);
					editsn.requestFocus();
					String scantext = "通过：[" + lotsn + "]成功的获得工单:"
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
		
		case TaskType.liulouassyqidong:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "条码启动成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext =editsn.getText().toString()+ "失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
					}
					editsn.requestFocus();
					editsn.setText("");

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

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		 case R.id.assyqidong_moedit:
			String param = editMO.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
			   if(param.length()<8){
				   Toast.makeText(this, "批次号长度不对", Toast.LENGTH_SHORT).show();
				   
			   }
			   else{
				    super.progressDialog.setMessage("正在数据库获取工单");
					super.showProDia();
					assyqidongModel.getmobymoname(new Task(this,TaskType.liulouassyqidonggetmobymoname,param));
			   }
			}
			break;
		
		
		case R.id.assyqidong_snedit:
			String[] params=new String[7];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String param2 = editsn.getText().toString().toUpperCase().trim();
			  
			   if(moname==null||moid==null){
				   Toast.makeText(this, "请先获取工单信息！", Toast.LENGTH_SHORT).show();
				   return  false;
			   }
			   else  if(param2.length()<8){
					   Toast.makeText(this, "批次号长度不对", Toast.LENGTH_SHORT).show();
					   return  false;
				}
			   else{
					   super.progressDialog.setMessage("正在提交到MES....");
						super.showProDia();
						params[0] = useid;
						params[1] = usename;
						params[2] = resourceid;
						params[3] = resourcename;
						params[4] = moid;
						params[5] = moname;
						//params[6] = editsnstart.getText().toString().trim();
						//params[7] = editsnend.getText().toString().trim();
						params[6] = param2;
						assyqidongModel.assyqidong(new Task(this,TaskType.liulouassyqidong,params));
			  }
			}
		break;
		}
		return false;
	}
   
	
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.assyqidong_motextview:
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

	
	

}
