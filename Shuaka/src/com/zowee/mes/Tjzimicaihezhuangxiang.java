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
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.TjzimicaihezhaungxiangModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//天津紫米彩盒绑定中箱 自动送检
public class Tjzimicaihezhuangxiang extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener,OnItemSelectedListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	private  String  xianghaoid;
	private  String  songjiandan;
	
	private  List<HashMap<String,String>>  list;
	private  Spinner mospinner;
	private EditText editMO;
	//private  TextView  tvmo;//点击换取工单
	private EditText editMOProduct;
	private  EditText  editmozhongxiangqty;
	private  EditText  editmooqcqty;
	private String moid; // 工单选择保存变量

	private EditText editxianghaosn;
	private EditText editcaihesn;

	private Button weishuzhuangxiangbutton;
	private Button clearzhongxiangbutton;
	private Button shougongsongjiangbutton;
	

	private EditText editscan;

	

	private TjzimicaihezhaungxiangModel tjzimicaihezhaungxiangModel; // 任务处理类
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Tjzimicaihezhuangxiang";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tianji_zimicaihezhuangxiang);
		init();
	}

	protected void onResume() {
		super.onResume();
		super.progressDialog.show();
		super.progressDialog.setMessage("开始从MES获取工单信息。。。。");
		tjzimicaihezhaungxiangModel.getmo(new Task(this,TaskType.tjzimicaihezhuangxianggetmo,""));
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
								stopService(new Intent(Tjzimicaihezhuangxiang.this,
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

		tjzimicaihezhaungxiangModel = new TjzimicaihezhaungxiangModel();
		common4dmodel=new  Common4dModel();
		
		mospinner=(Spinner) findViewById(R.id.tjzimicaihezhuangxiang_mospinner);
		editMO = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_moedit);
		//tvmo=(TextView) findViewById(R.id.tjzimicaihezhuangxiang_motextview);
		editMOProduct = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_moliaohaoedit);
		editmozhongxiangqty = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_mozhuangxiangqtyedit);
		editmooqcqty= (EditText) findViewById(R.id.tjzimicaihezhuangxiang_mosongjianqtyedit);
		editMO.requestFocus();

		editxianghaosn = (EditText)findViewById(R.id.tjzimicaihezhuangxiang_xianghaoedit);
		editcaihesn = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_picihaoedit);
		
		editscan = (EditText) findViewById(R.id.tjzimicaihezhuangxiang_editscan);
		editscan.setFocusable(false);
		
		weishuzhuangxiangbutton = (Button) findViewById(R.id.tjzimicaihezhuangxiang_weishubutton);
		weishuzhuangxiangbutton.setFocusable(false);
		clearzhongxiangbutton = (Button) findViewById(R.id.tjzimicaihezhuangxiang_qingkongxianghaobutton);
		clearzhongxiangbutton.setFocusable(false);
		shougongsongjiangbutton = (Button) findViewById(R.id.tjzimicaihezhuangxiang_shougongsongjianbutton);
		shougongsongjiangbutton.setFocusable(false);
		
		weishuzhuangxiangbutton.setOnClickListener(this);
		clearzhongxiangbutton.setOnClickListener(this);
		shougongsongjiangbutton.setOnClickListener(this);
		//tvmo.setOnClickListener(this);
		
		editMO.setOnKeyListener(this);
		editcaihesn.setOnKeyListener(this);
		
		
		mospinner.setOnItemSelectedListener(this);
		
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
		
		
		case TaskType.tjzimicaihezhuangxianggetmo:
			super.closeProDia();
			super.closeProDia();
			if (null != task.getTaskResult()) {
				
				list = (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG,"task的结果数据是："+list);
				SimpleAdapter  adapter=new  SimpleAdapter(this, list, R.layout.activity_tjzimicaihezhuangxiang_moadapter, new String[]{"moid","moname","product","boxqty","oqcqty"}, new int [] {R.id.tjzimicaihezhuangxiang_adapter_1,R.id.tjzimicaihezhuangxiang_adapter_2,R.id.tjzimicaihezhuangxiang_adapter_3,R.id.tjzimicaihezhuangxiang_adapter_4,R.id.tjzimicaihezhuangxiang_adapter_5});
				mospinner.setAdapter(adapter);
			}
		   else {
			  Toast.makeText(this, "MES 返回信息发生异常,请确认工单正确", 5).show();	
			}
				
			 
			
			break;	
			
			
		case TaskType.tjzimicaihezhuangxiangzhuangxiang:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>)task.getTaskResult();
				String exception=getdata.get("I_ExceptionFieldName");
				String value=getdata.get("I_ReturnValue");
				xianghaoid=getdata.get("BoxId");
				String iscomplete=getdata.get("iscomplete");
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					editcaihesn.setText("");
					editcaihesn.requestFocus();
					if(Integer.parseInt(value)==0){
						whorequestFocus(exception);
						if(iscomplete.equals("ture")){
							editxianghaosn.setText("");
							editxianghaosn.requestFocus();
						}
						String scantext = "装箱成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						whorequestFocus(exception);
						String scantext = "装箱失败！"+getdata.get("I_ReturnMessage");
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
			
			
		case TaskType.tjzimicaihezhuangxiangweishuzhuangxiang:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					if(Integer.parseInt(getdata.get("I_ReturnValue"))==0){
						String scantext = "尾数装箱成功 "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "成功");
								SoundEffectPlayService
										.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "尾数装箱失败 "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "成功");
					}
					

				} else {
					logSysDetails(
							 getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取信息失败，请检查！", "成功");
			}

			break;
			
		case TaskType.tjzimicaihezhuangxiangqingkongzhuangxiang:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					if(Integer.parseInt(getdata.get("I_ReturnValue"))==0){
						editxianghaosn.setText("");
						editxianghaosn.requestFocus();
						String scantext = "清空箱号成功 "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "成功");
								SoundEffectPlayService
										.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "清空箱号失败 "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "成功");
					}
					

				} else {
					logSysDetails(
							 getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取信息失败，请检查！", "成功");
			}

			break;	
			
		case TaskType.tjzimicaihezhuangxianggetoqcbyxianghao:
			super.closeProDia();
			String[]  params=new String[5];
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					  if(getdata.containsKey("SampleInspectionSN")){
						  songjiandan=getdata.get("SampleInspectionSN");
						  String scantext = "手工送检获取送检单成功，送检单为 "+songjiandan;
							logSysDetails(scantext, "成功");
							SoundEffectPlayService
									.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = songjiandan;
							
							tjzimicaihezhaungxiangModel.shougongsongjian(new Task(this,TaskType.tjzimicaihezhuangxiangshoudongshoujian,params));
					  }
					  else{
						  String scantext = "手工送检获取送检单失败 ";
							logSysDetails(scantext, "成功");
					  }
				} else {
					logSysDetails(
							 getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取信息失败，请检查！", "成功");
			}

			break;	
			
		case TaskType.tjzimicaihezhuangxiangshoudongshoujian:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(getdata.get("I_ReturnValue"))==0){
						String scantext = "手工送检成功 "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "成功");
								SoundEffectPlayService
										.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						String scantext = "手动送检失败 "+getdata.get("I_ReturnMessage");
								logSysDetails(scantext, "成功");
					}
					

				} else {
					logSysDetails(
							 getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取信息失败，请检查！", "成功");
			}

			break;	
			
		}
	}

	@Override
	public void onClick(View v) {
		final String[]  params=new String[9];
		switch (v.getId()) {
		//点击更换工单
//		case R.id.tjzimicaihezhuangxiang_motextview:
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("更换工单");
//			builder.setMessage("是否需要重新更换工单？");
//			builder.setPositiveButton("确定",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface arg0, int arg1) {
//							// TODO Auto-generated method stub
//							editMO.setText("");
//							editMO.setEnabled(true);
//							editMO.requestFocus();
//							editMOProduct.setText("");
//							editmooqcqty.setText("");
//							editmozhongxiangqty.setText("");
//							moid="";
//						}
//					});
//			builder.setNegativeButton("取消", null);
//			builder.create().show();
//			break;
			//点击尾数装箱按钮
		case R.id.tjzimicaihezhuangxiang_weishubutton:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("尾数装箱");
			builder2.setMessage("是否需要尾数装箱？");
			builder2.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							progressDialog.setMessage("尾数装箱中....");
							showProDia();
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = moid;
							params[5] = editmooqcqty.getText().toString().trim();
							params[6] = xianghaoid;
							tjzimicaihezhaungxiangModel.weishuzhuangxiang(new Task(Tjzimicaihezhuangxiang.this,TaskType.tjzimicaihezhuangxiangweishuzhuangxiang,params));
						}
					});
			builder2.setNegativeButton("取消", null);
			builder2.create().show();
			break;
			//点击清空箱号按钮
		case R.id.tjzimicaihezhuangxiang_qingkongxianghaobutton:
			AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
			builder3.setTitle("清空箱号");
			builder3.setMessage("是否需要清空装箱？");
			builder3.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							progressDialog.setMessage("清空箱号中....");
							showProDia();
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = moid;
							params[5] = editmooqcqty.getText().toString().trim();
							params[6] = xianghaoid;
							tjzimicaihezhaungxiangModel.qingkongxianghao(new Task(Tjzimicaihezhuangxiang.this,TaskType.tjzimicaihezhuangxiangqingkongzhuangxiang,params));
						}
					});
			builder3.setNegativeButton("取消", null);
			builder3.create().show();
			break;
			//手动送检
		case R.id.tjzimicaihezhuangxiang_shougongsongjianbutton:
			AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
			builder4.setTitle("手动送检");
			builder4.setMessage("是否需要手动送检？");
			builder4.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							progressDialog.setMessage("手动送检中....");
							showProDia();
							
							tjzimicaihezhaungxiangModel.getoqcdanhao(new Task(Tjzimicaihezhuangxiang.this,TaskType.tjzimicaihezhuangxianggetoqcbyxianghao,editxianghaosn.getText().toString().trim()));
						}
					});
			builder4.setNegativeButton("取消", null);
			builder4.create().show();
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
		
//		case R.id.tjzimicaihezhuangxiang_moedit:
//			if (KeyEvent.KEYCODE_ENTER == keycode
//					&& event.getAction() == KeyEvent.ACTION_DOWN) {
//				String param = editMO.getText().toString().toUpperCase().trim();
//			   if(param.length()<8){
//				   Toast.makeText(this, "批次号长度不对", Toast.LENGTH_SHORT).show();
//			   }
//			   else{
//				    super.progressDialog.setMessage("正在数据库获取工单");
//					super.showProDia();
//					tjzimicaihezhaungxiangModel.getmo(new Task(this,TaskType.tjzimicaihezhuangxianggetmo,param));
//			   }
//			}
//		break;

		case R.id.tjzimicaihezhuangxiang_picihaoedit:
			String[]  params=new String[9];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(moid.equals("")||moid==null||editxianghaosn.getText().toString().trim().equals("")){
					Toast.makeText(this, "请先获取工单和箱号信息", Toast.LENGTH_LONG).show();
				}
				else{
					if(editmooqcqty.getText().toString().equals("")||editmozhongxiangqty.getText().toString().equals("")){
						Toast.makeText(this, "请确保工单维护了装箱数量与送检箱数的信息！", Toast.LENGTH_LONG).show();
					}
					else{
						super.progressDialog.setMessage("彩盒装箱中....");
						super.showProDia();
						params[0] = useid;
						params[1] = usename;
						params[2] = resourceid;
						params[3] = resourcename;
						params[4] = moid;
						params[5] = editmozhongxiangqty.getText().toString().trim();
						params[6] = editmooqcqty.getText().toString().trim();
						params[7] = editxianghaosn.getText().toString().trim();
						params[8] = editcaihesn.getText().toString().trim();
						
						tjzimicaihezhaungxiangModel.zhuangxiang(new  Task(this,TaskType.tjzimicaihezhuangxiangzhuangxiang,params));
					}
				}
				
				
				
			}

			break;
		}
		return false;
	}
	private  void whorequestFocus(String exception){
		if(exception.equalsIgnoreCase("BoxSN")){
			editxianghaosn.requestFocus();
		}
		if(exception.equalsIgnoreCase("LotSN")){
			editcaihesn.requestFocus();
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
	  

		switch (arg0.getId()) {
		case R.id.tjzimicaihezhuangxiang_mospinner:
			 HashMap<String,String> map= (HashMap<String, String>) arg0.getItemAtPosition(arg2);
			   moid=map.get("moid");
			   String  moname=map.get("moname");
			   String  productu=map.get("product");
			   String  boxqty=map.get("boxqty");
			   String  oqcqty=map.get("oqcqty");
		       editMO.setText(moname);  
		       editMOProduct.setText(productu);  
		       editmozhongxiangqty.setText(boxqty);  
		        editmooqcqty.setText(oqcqty);  
			break;

		
		}
	  
      
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
		

}
