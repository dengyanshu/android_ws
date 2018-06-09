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
import com.zowee.mes.model.AnxuzhuangxiangModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.LenovotocarModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//联想客户要求 SMT关联单板条码和SMT板子上的 小芯片 ！扫描枪扫不到条码后续自动化开发的设备在生产！！
public class Lenovotocar extends CommonActivity implements
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

	private EditText editcarsn;
	private EditText editcarnum;
	private EditText editpcbasn;
	private EditText editxinpiansn;
	private  EditText  editxinpiannum;

	
	private Button okbutton;
	private Button clearbutton;
	
	
	
	private  TextView pcbatextview;
	private  TextView xinpiantextview;

	private EditText editscan;

	private  int  numononepcba=3;//一个单板上拥有的芯片SN数目
	private  String  str="";//提交到数据库的字符串（格式为  pcbasn,xinsn1,xinsn2,xinsn3|pcbasn,xinsn4,xinsn5,xinsn6|）
	private  int num=1  ;//计数器 芯片数目计数起始为1

	private LenovotocarModel lenovotocarModel; // 任务处理类
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Lenovotocar";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lenovosmt_tocar);
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
								stopService(new Intent(Lenovotocar.this,
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

		lenovotocarModel = new LenovotocarModel();
		common4dmodel=new  Common4dModel();
		
		editMO = (EditText) findViewById(R.id.lenovotocar_moedit);
		tvmo=(TextView) findViewById(R.id.lenovotocar_motextview);
		editMOdescri = (EditText) findViewById(R.id.lenovotocar_modescri);
		editMOProduct = (EditText) findViewById(R.id.lenovotocar_moproduct);
		editMO.requestFocus();

		editcarsn = (EditText) findViewById(R.id.lenovotocar_caredit);
		editcarnum= (EditText) findViewById(R.id.lenovotocar_incarnumedit);
		editcarnum.setFocusable(false);
		editcarnum.setEnabled(false);
		editpcbasn = (EditText) findViewById(R.id.lenovotocar_pcbaedit);
		editxinpiansn = (EditText) findViewById(R.id.lenovotocar_xinpianedit);
		editxinpiannum= (EditText) findViewById(R.id.lenovotocar_xinpiannumedit);
		editxinpiannum.setFocusable(false);
		editxinpiannum.setEnabled(false);
		

		okbutton = (Button) findViewById(R.id.lenovotocar_okbutton);
		okbutton.setFocusable(false);
		clearbutton = (Button) findViewById(R.id.lenovotocar_clearbutton);
		clearbutton.setFocusable(false);
		
		pcbatextview=(TextView) findViewById(R.id.lenovotocar_pcbatextview);
		xinpiantextview=(TextView) findViewById(R.id.lenovotocar_xinpiantextview);

		editscan = (EditText) findViewById(R.id.lenovotocar_editscan);
		editscan.setFocusable(false);
		
		okbutton.setOnClickListener(this);
		tvmo.setOnClickListener(this);
        
		editMO.setOnKeyListener(this);
		editpcbasn.setOnKeyListener(this);
		editxinpiansn.setOnKeyListener(this);
		
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
				logSysDetails( "在MES获取资源id信息失败，请检查配置则资源名称是否正确!", "成功");
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
					editcarsn.requestFocus();
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
			
		case TaskType.lenovotocar:
			super.closeProDia();
			String exception="";
			str="";
			numononepcba=3;
			num=1;
			editxinpiannum.setText("0");
			pcbatextview.setText("");
			xinpiantextview.setText("");
			if (null != task.getTaskResult()) {
				editpcbasn.setText("");
				editxinpiansn.setText("");
				getdata = (HashMap<String, String>) task.getTaskResult();
				if(getdata.get("CountCar")!=null){
					 String inthecarnum=getdata.get("CountCar");
					 editcarnum.setText(inthecarnum);
				}
				if(getdata.get("I_ExceptionFieldName")!=null){
					 exception=getdata.get("I_ExceptionFieldName");
				}
				else{
					exception="LotSN";
				}
					
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
			
		case TaskType.lenovotocartoclosecar:
			super.closeProDia();
			String exception1="";
			editcarsn.setText("");
			if (null != task.getTaskResult()) {
				editcarsn.setText("");
				editcarsn.requestFocus();
				getdata = (HashMap<String, String>) task.getTaskResult();
				if(getdata.get("I_ExceptionFieldName")!=null){
					 exception1=getdata.get("I_ExceptionFieldName");
				}
				else{
					exception1="LotSN";
				}
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						whorequestFocus(exception1);
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						whorequestFocus(exception1);
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
		case R.id.lenovotocar_motextview:
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
			
		case R.id.lenovotocar_okbutton:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("强制车号");
			builder2.setIcon(R.drawable.app);
			builder2.setMessage("是否需要重新更换车号？");
			builder2.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							progressDialog.setMessage("强制装车提交中");
							showProDia();
							String[]  params=new String[9];
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = "";
							params[5] = "";
							params[6] = editcarsn.getText().toString().trim();
							params[7] ="+SubmitCar+";
							params[8] ="3";
							lenovotocarModel.lenovotocar(new  Task(Lenovotocar.this,TaskType.lenovotocartoclosecar,params));
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
		case R.id.lenovotocar_moedit:
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

		case R.id.lenovotocar_pcbaedit:
			
			if (KeyEvent.KEYCODE_ENTER == keycode&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String pcba=editpcbasn.getText().toString().toUpperCase().trim();
				if(moname==null||editcarsn.getText().toString().isEmpty()){
					Toast.makeText(this, "工单和车号不能为空！", Toast.LENGTH_LONG).show();
				    return  false;
				}
				else if(pcba.equalsIgnoreCase("+submit+")){
					super.progressDialog.setMessage("装车提交中");
					super.showProDia();
					String[]  params=new String[9];
					params[0] = useid;
					params[1] = usename;
					params[2] = resourceid;
					params[3] = resourcename;
					params[4] = moid;
					params[5] = moname;
					params[6] = editcarsn.getText().toString().trim();
					params[7] =str;
					params[8] =numononepcba+"";
					lenovotocarModel.lenovotocar(new  Task(this,TaskType.lenovotocar,params));
				}
				else{
					if(!(str.contains(pcba))){
						str=str+pcba+",";
						editxinpiansn.requestFocus();
						logSysDetails("成功扫描到单板条码【"+pcba+"】","成功");
						pcbatextview.setText(pcba);
						xinpiantextview.setText("");
						editxinpiannum.setText("0");
					}
					
					else{
						logSysDetails("单板条码重复扫描","成功");
						//editpcbasn.requestFocus();
						editcarsn.requestFocus();
						return  false;
					}
					
					
				}
			}
		break;
			
        case R.id.lenovotocar_xinpianedit:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String xinpinsn=editxinpiansn.getText().toString().toUpperCase().trim();
				if(moname==null||editcarsn.getText().toString().isEmpty()){
					Toast.makeText(this, "工单和车号不能为空！", Toast.LENGTH_LONG).show();
				    return  false;
				}
				else if(xinpinsn.equalsIgnoreCase("+submit+")){
					super.progressDialog.setMessage("装箱提交中");
					super.showProDia();
					String[]  params=new String[9];
					params[0] = useid;
					params[1] = usename;
					params[2] = resourceid;
					params[3] = resourcename;
					params[4] = moid;
					params[5] = moname;
					params[6] = editcarsn.getText().toString().trim();
					params[7] =str;
					params[8] =numononepcba+"";
					lenovotocarModel.lenovotocar(new  Task(this,TaskType.lenovotocar,params));
				}
				else{
					if(!str.contains(xinpinsn)&&num!=numononepcba){
						logSysDetails("成功扫描芯片条码【"+num+"】"+xinpinsn,"成功");
						editxinpiannum.setText(num+"");
						xinpiantextview.append(xinpinsn+"\n");
						num++;
						str=str+xinpinsn+",";
						editxinpiansn.requestFocus();
						editxinpiansn.setText("");
						return  false;
						
					}
					else  if(!str.contains(xinpinsn)&&num==numononepcba){
						
						logSysDetails("成功扫描芯片条码【"+num+"】"+xinpinsn,"成功");
						editxinpiannum.setText(num+"");
						xinpiantextview.append(xinpinsn+"\n");
						str=str+xinpinsn+"|";
						editcarsn.requestFocus();
						editxinpiansn.setText("");
						num=1;
						return  false;
					}
					else{
						logSysDetails("芯片条码重复扫描","成功");
						editxinpiansn.requestFocus();
						editxinpiansn.setText("");
					}
					
				}
			}
			break;
		}
		return false;
	}

	private  void whorequestFocus(String exception){
		if(exception.equalsIgnoreCase("LotSN")){
			editpcbasn.requestFocus();
		}
		if(exception.equalsIgnoreCase("CarInfo")){
			editcarsn.requestFocus();
		}
		if(exception.equalsIgnoreCase("SNScan")){
			editpcbasn.requestFocus();
		}
		
		
	}

}
