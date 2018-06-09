package com.zowee.mes;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.LenovofvmiModel;
import com.zowee.mes.model.M8000cpModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//希腊H108  料号为900000-1485  lp站打印程序
public class M8000cp extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener
		 {
     
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private  Spinner  spinner1;
	private  Spinner  spinner2;
	
	
	private  String jiaobenString="";
	private  String path;
	
	
	private EditText editMO;
	private  TextView  tvmo;//点击换取工单
	private EditText editMOProduct;
	private EditText editMOdescri;
	private String moid; // 工单选择保存变量
	private  String moname;

	private EditText editsn;
    
	
	private TextView  resulttextview;

	private EditText editscan;

	

	private M8000cpModel M8000cpModel; // 任务处理类
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Lenovofvmi";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_m800_cp);
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
								stopService(new Intent(M8000cp.this,
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
		
		common4dmodel=new  Common4dModel();
		M8000cpModel=new M8000cpModel();
		
		String[] coms={"COM1","COM2","COM3"};
		String[] bate={"4800","9600","19200","57600","115200"};
		spinner1=(Spinner) findViewById(R.id.lenovofvmi_spinner1);
		spinner2=(Spinner) findViewById(R.id.lenovofvmi_spinner2);
		ArrayAdapter adapter1 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, coms);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter adapter11 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, bate);
		adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter1);
		spinner2.setAdapter(adapter11);
		
		editMO = (EditText) findViewById(R.id.lenovofvmi_moedit);
		tvmo=(TextView) findViewById(R.id.lenovofvmi_motextview);
		editMOdescri = (EditText) findViewById(R.id.lenovofvmi_modescri);
		editMOProduct = (EditText) findViewById(R.id.lenovofvmi_moproduct);
		editMO.requestFocus();

		editsn = (EditText) findViewById(R.id.lenovofvmi_snedit);
		
		resulttextview=(TextView) findViewById(R.id.lenovofvmi_resultview);

		editscan = (EditText) findViewById(R.id.lenovofvmi_editscan);
		editscan.setFocusable(false);
		
		

		tvmo.setOnClickListener(this);
        
		editMO.setOnKeyListener(this);
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
					logSysDetails(logText, true);
				} else {
					logSysDetails(
							"通过资源名称获取在MES获取资源ID失败，请检查配置的资源名称是否正确", false);
				}
				closeProDia();
			} else {
				logSysDetails( "在MES获取资源id信息失败，请检查配置则资源名称是否正确", false);
			}

			break;
			
		case TaskType.m8000cpgetpath:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("Path")) {
						path = getdata.get("Path");
						String logText = "已获取到脚本路径！"+path;
						logSysDetails(logText, true);
						getstringbypath(path);
					}
					else{
						logSysDetails("获取脚本路径失败", false);
					}
					
				} else {
					logSysDetails(
							"该工单对应的料号的脚本的路径未找到！请确认工单料号信息，脚本是否维护正确", false);
				}
			} else {
				logSysDetails( "在MES获取资源id信息失败，请检查配置则资源名称是否正确", false);
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
					editsn.requestFocus();
					String scantext = "通过批次号：[" + lotsn + "]成功的获得工单:"
							+ moname + ",工单id:"+moid+",产品信息:" + productdescri + ",产品料号："
							+ material + "!";
					logSysDetails(scantext, true);
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					
					getpath(moid);

				} else {
					logSysDetails(
							"通过批次号：[" + lotsn + "]在MES获取工单信息为空或者解析结果为空，请检查SN!"
									+ getdata.get("Error"), false);
				}
				closeProDia();
			} else {
				logSysDetails(lotsn + "在MES获取工单信息失败，请检查输入的条码", false);
			}

			break;
			
		case TaskType.lenovofvmi:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				if(getdata.get("DefectcodeSn")!=null){
				}
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						resulttextview.setText("PASS");
						resulttextview.setTextColor(Color.GREEN);
						logSysDetails(scantext, true);
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						resulttextview.setText("FAIL");
						resulttextview.setTextColor(Color.RED);
						String scantext = getdata.get("I_ReturnMessage");
						logSysDetails(scantext, false);
					}
                    editsn.requestFocus();
                    editsn.setText("");
				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), false);
				}
				closeProDia();
			} else {
				logSysDetails("提交MES失败请检查网络或者工单，请检查输入的条码", false);
			}

			break;
		
		}

	}
	
	
	private  void getpath(String moid){
		super.progressDialog.setMessage("获取脚本路径中...");
		super.showProDia();
		M8000cpModel.getpath(new  Task(this,TaskType.m8000cpgetpath,moid));
		
	}
	private void  getstringbypath(String path){
		
		
		// TODO Auto-generated method stub
		//String urlpath=URLEncoder.encode(path);
		String urlpath=path.replace("//", "//mes:mesmes@");
		URL url=null;
		URLConnection con = null;
		InputStream  inputstream = null;
		ByteArrayOutputStream  bos = null;
		try {
			url=new URL(urlpath);
			con=url.openConnection();
			inputstream=con.getInputStream();
			bos=new ByteArrayOutputStream();
			byte[]  buf=new byte[1024];
			int  len;
			while  ((len=inputstream.read(buf))!=-1){
				bos.write(buf,0,len);
				bos.flush();
			}
			jiaobenString=bos.toString();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally{
			try{
				if(inputstream!=null){
					inputstream.close();
				}
				if(bos!=null){
					bos.close();
				}
			}
			catch(Exception e){
				
			}
			
		}
		
	}

	

//	private  void  getstringbypath( String path){
//		
//			
//				// TODO Auto-generated method stub
//				String urlpath=path.replace("//", "//mes:mesmes@");;
//				
//				URL url=null;
//				URLConnection con = null;
//				InputStream  inputstream = null;
//				ByteArrayOutputStream  bos = null;
//				try {
//					url=new URL(urlpath);
//					con=url.openConnection();
//					inputstream=con.getInputStream();
//					bos=new ByteArrayOutputStream();
//					byte[]  buf=new byte[1024];
//					int  len;
//					while  ((len=inputstream.read(buf))!=-1){
//						bos.write(buf,0,len);
//						bos.flush();
//					}
//					jiaobenString=bos.toString();
////					Message msg=myhandler.obtainMessage();
////					msg.what=1;
////					msg.obj=jiaobenString;
////					myhandler.sendMessage(msg);
//					
//					Log.i(TAG,"脚本具体内容是"+jiaobenString);
//					
//				} 
//				catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				finally{
//					try{
//						if(inputstream!=null){
//							inputstream.close();
//						}
//						if(bos!=null){
//							bos.close();
//						}
//					}
//					catch(Exception e){
//						
//					}
//					
//				}
//				
//	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lenovofvmi_motextview:
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

	private void logSysDetails(String logText, Boolean pass) {
		CharacterStyle ssStyle = null;
		if (pass) {
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
		case R.id.lenovofvmi_moedit:
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

		case R.id.lenovofvmi_snedit:
			String[]  params=new String[7];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(editsn.getText().toString().trim().equals("")){
					Toast.makeText(this, "确保需要的提交的条码已全部扫描", Toast.LENGTH_LONG).show();
				}
				else{
					super.progressDialog.setMessage("提交MES中...");
					super.showProDia();
					params[0] = useid;
					params[1] = usename;
					params[2] = resourceid;
					params[3] = resourcename;
					
					params[4] = editsn.getText().toString().trim();
					params[6] = moid;
					
				}
				
				
			}

			break;
		}
		return false;
	}

  

}
