package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.ImeicartoonboxcheckModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//庞颜坤 客户 IMEI和中箱的核对
public class Imeicartoonboxcheck extends CommonActivity implements
		 OnClickListener,OnKeyListener
		 {
	int  imeiscannum=0;//扫描叠加
	StringBuffer  sb=null;
	String  imeilisttostring;
	
	private  String ResourceID;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	private GetMOnameModel GetMonamemodel;
	
   private  TextView  numtextview;
   private  EditText  imeinumedit;
   private  int  num=0;//手动设定的数量
   private  Button numbutton;
   
   private  EditText  cartoonboxedit;
   private ArrayList<HashMap<String,String>>  imeilist;
   private  EditText  imeiedit;
   private  Button  clearbutton;

	private EditText editscan;

	private ImeicartoonboxcheckModel imeicartoonboxcheckmodel; // 任务处理类
	private static final String TAG = "Imeicartoonboxcheck";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_imeicartoonboxcheck);
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
								stopService(new Intent(Imeicartoonboxcheck.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.imeicartoonboxcheckscan; // 后台服务静态整形常量
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		imeicartoonboxcheckmodel = new ImeicartoonboxcheckModel();
		sb=new StringBuffer();
		numtextview=(TextView) findViewById(R.id.imeicartoonboxcheck_numtextview);
		imeinumedit = (EditText) findViewById(R.id.imeicartoonboxcheck_numedit);
		numbutton=(Button) findViewById(R.id.imeicartoonboxcheck_numbutton);
		numbutton.setFocusable(false);
		
		cartoonboxedit = (EditText) findViewById(R.id.imeicartoonboxcheck_xianghaoedit);
		imeiedit = (EditText) findViewById(R.id.imeicartoonboxcheck_imei);
		clearbutton = (Button) findViewById(R.id.imeicartoonboxcheck_clearbutton);
		clearbutton.setFocusable(false);
		
		editscan=(EditText) findViewById(R.id.imeicartoonboxcheck_editscan);
		
		numbutton.setOnClickListener(this);
		clearbutton.setOnClickListener(this);
		cartoonboxedit.setOnKeyListener(this);
		imeiedit.setOnKeyListener(this);
		
		
		
//		cartoonboxedit.setOnKeyListener(this);
//		imeiedit.setOnKeyListener(this);
        logSysDetails("请先设定核对IMEI的数量，在MES获取中箱IMEI的信息，再核对IEMI", "请");
        GetResourceId();
	}
	private void GetResourceId()
	{
		if(MyApplication.getAppOwner().toString().isEmpty()){

			logSysDetails("资源名称为空，请先设定资料名称","失败");
			return;
		}
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, MyApplication.getAppOwner().toString());	
		GetMonamemodel.GetResourceId(task);

	}
	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		
		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		switch (task.getTaskType()) {
		// 获取工单ID
		case TaskType.imeicartoonboxcheckscan:
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
			break;
			
		case TaskType.GetResourceId:
			ResourceID = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			ResourceID = getresult.get(0).get("ResourceId");
			if(ResourceID.isEmpty())	 
				logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
			else{
				logSysDetails("成功获取到该设备的资源ID:"+ResourceID,"成功");
			}
		break;
		
		case TaskType.imeicartoonboxcheckgetimeilist:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				imeilist = (ArrayList<HashMap<String, String>>) task.getTaskResult();
				imeiedit.requestFocus();
				logSysDetails( "在MES获取该箱内产品的主IEMI成功!", "成功");
				Log.d(TAG, "task的结果数据是：" + imeilist);
			} else {
				logSysDetails( "在MES获取资源箱内产品的主IEMI的数据为空，请检查输入的箱号是否正确", "成功");
				cartoonboxedit.requestFocus();
				cartoonboxedit.setText("");
			}

			break;
			
		case TaskType.imeicartoonboxcheck:
			super.closeProDia();
			if (null != task.getTaskResult()) {
		      HashMap<String, String>	getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "IMEI核对中箱成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						cartoonboxedit.setText("");
						cartoonboxedit.requestFocus();
						sb.delete(0, sb.length());
						imeiscannum=0;
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						String scantext = "IEMI核对中箱失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						imeiedit.setText("");
						imeiedit.requestFocus();
					}

				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails("提交MES失败请检查网络或者箱号，请检查输入的条码", "成功");
			}
			break;
			
		
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
		
		if(cartoonboxedit.isFocused()){
			cartoonboxedit.setText(scanRes);
			 super.progressDialog.setMessage("正在数据库获取箱号信息");
				super.showProDia();
				imeicartoonboxcheckmodel.getImeilist(new Task(this,TaskType.imeicartoonboxcheckgetimeilist,scanRes));
		}
		if(imeiedit.isFocused()){
			imeiedit.setText(scanRes);
			imeichecknet(scanRes);
		}
			
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imeicartoonboxcheck_numbutton:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("输入密码进行需要核对的IMEI的数量进行设定？");
			final EditText edittext=	new  EditText(this);
			builder.setView(edittext);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if(edittext.getText().toString().equalsIgnoreCase("123456")){
								numtextview.setVisibility(View.VISIBLE);
								imeinumedit.setVisibility(View.VISIBLE);
							}
							
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create().show();
			break;
			
		case R.id.imeicartoonboxcheck_clearbutton:
			sb.delete(0, sb.length());
			imeiscannum=0;
			imeilist=null;
			cartoonboxedit.requestFocus();
			cartoonboxedit.setText("");
			break;
			
		

		}		
			
	 		
	}
	

	

	private void logSysDetails(String logText, String strflag) {
		CharacterStyle ssStyle = null;
		if (logText.contains(strflag)) {
			ssStyle = new ForegroundColorSpan(Color.GREEN);
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
		case R.id.imeicartoonboxcheck_xianghaoedit:
			String param = cartoonboxedit.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
			   if(param.length()<8){
				   Toast.makeText(this, "长度不对", Toast.LENGTH_SHORT).show();
			   }
			   else{
				    super.progressDialog.setMessage("正在数据库获取箱号信息");
					super.showProDia();
					imeicartoonboxcheckmodel.getImeilist(new Task(this,TaskType.imeicartoonboxcheckgetimeilist,param));
			   }
			}			
		break;

		case R.id.imeicartoonboxcheck_imei:
			
//			if(!imeinumedit.getText().toString().equals("")){
//				
//				num=Integer.parseInt(imeinumedit.getText().toString());
//			}
//			else 
//				num=0;
			
			
		
			if(KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction()== KeyEvent.ACTION_DOWN){
				
				imeichecknet(imeiedit.getText().toString().trim());
			}
		    break;
		}
		return false;
	
	}
	private  void  imeichecknet(String scanres){
		num=Integer.parseInt(imeinumedit.getText().toString());
		if(imeilist!=null){
			imeilisttostring=imeilist.toString();
		}
		if( scanres.length()<8){
			   Toast.makeText(this, "长度不对", Toast.LENGTH_SHORT).show();
			   return ;
		}
		if(num==0){
			Toast.makeText(this, "请先设定需要核对的IMEI的数量", Toast.LENGTH_LONG).show();
			return ;
		}
		if(imeilist==null){
			Toast.makeText(this, "请先输入箱号在MES获取信息", Toast.LENGTH_LONG).show();
			return ;
		}
		else{
//			super.progressDialog.setMessage("IMEI提交中");
//			super.showProDia();
			if(imeilisttostring.contains(scanres)&&!(sb.toString().contains(imeiedit.getText().toString().trim()))){
				
				
					imeiscannum++;
					sb.append(imeiedit.getText().toString().trim());
					logSysDetails("成功核对IMEI["+imeiscannum+"]  "+imeiedit.getText().toString().trim(),"成功");
					imeiedit.setText("");
					imeiedit.requestFocus();
					if(imeiscannum==num){
						super.progressDialog.setMessage("箱号过站提交中");
						super.showProDia();
						String[]  params=new String[5];
						params[0] = useid;
						params[1] = usename;
						params[2] = ResourceID;
						params[3] = resourcename;
						params[4] = cartoonboxedit.getText().toString().toUpperCase().trim();
						imeicartoonboxcheckmodel.iemicartoonboxcheck(new  Task(this,TaskType.imeicartoonboxcheck,params));
					    
				    }
			}
			else if(sb.toString().contains(imeiedit.getText().toString().trim())){
				logSysDetails("重复扫描","成功");
				imeiedit.setText("");
				imeiedit.requestFocus();
			}
			else if(!(imeilisttostring.contains(imeiedit.getText().toString().trim()))){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("IEMI错误");
				builder.setIcon(R.drawable.app);
				builder.setMessage("    IEMI错误不在此中箱内,请仔细检查需要核对的IMEI的正确性！");
				
				builder.setPositiveButton("确定",null);
				builder.setNegativeButton("取消", null);
				builder.create().show();
				imeiedit.setText("");
				imeiedit.requestFocus();
			}
			
		}
	
	}

}
