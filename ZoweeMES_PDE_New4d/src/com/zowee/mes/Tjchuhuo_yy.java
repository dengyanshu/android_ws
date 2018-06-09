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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.TjchuhuoscanModel;
import com.zowee.mes.model.TjchuhuoyyModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class Tjchuhuo_yy extends CommonActivity implements
		OnKeyListener,OnItemSelectedListener
		 {
	private  Spinner   spinner1;//订单
	private ArrayList<String>   dingdan_fordingdanadapter;//适配器数据
	private  ArrayAdapter<List<HashMap<String, String>>>    dingdanadapter;//适配器
	
	private  Spinner   spinner2;//出货单
	private ArrayList<String>   chuhuodan_forchuhuodandapter;//适配器数据
	private  ArrayAdapter<List<HashMap<String, String>>>    chuhuodandapter;//适配器
	private  List<HashMap<String, String>>  chuhuodan_allinfo;
	private  String yypoid;
	private  String qty_shengyu;
	

	private  EditText info_chuhuoliang;
	private  EditText info_chuhuodi;
	private  EditText info_kehujixing;
	private  EditText info_productname;
	
	private  EditText info_xiangrongliang;
	private  EditText info_zhanrongliang;
	
	private  EditText  zhanbansn_edit;
	private  String qty;//传入下个箱号的执行存储过程
	private  Boolean  is_zhanbancheck_ok=false;
    private  EditText  xiangsn_edit;
   
   private  EditText  infocount_chuhuocount_edit;
   private  EditText  infocount_zhanbancount_edit;
   private  EditText  infocount_xiangcount_edit;
   
   private  EditText  editscan;
   
   
	private TjchuhuoyyModel tjchuhuoyyModel; // 任务处理类
	private static final String TAG = "Tjchuhuo_yy";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.setContentView(R.layout.activity_tj_yychuhuo);
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
								stopService(new Intent(Tjchuhuo_yy.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.tjchuhuoscan; // 后台服务静态整形常量
		//扫描数据也是个后台任务
		super.init();
        
	    tjchuhuoyyModel = new TjchuhuoyyModel();
	   
	   spinner1=(Spinner) findViewById(R.id.tj_chuhuoscan_spinner1);
	   dingdan_fordingdanadapter=new ArrayList<String>();
	   dingdanadapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, dingdan_fordingdanadapter);
	   dingdanadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	   spinner1.setAdapter(dingdanadapter);
	   
	   
	   spinner2=(Spinner) findViewById(R.id.tj_chuhuoscan_spinner2);
	   chuhuodan_forchuhuodandapter=new ArrayList<String>();
	   chuhuodandapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, chuhuodan_forchuhuodandapter);
	   chuhuodandapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	   spinner2.setAdapter(chuhuodandapter);
		   
	   
	   
	   info_xiangrongliang=(EditText) findViewById(R.id.tj_chuhuoscan_info_xiangrongliang);
	   info_productname=(EditText) findViewById(R.id.tj_chuhuoscan_info_productname);
	   info_chuhuoliang=(EditText) findViewById(R.id.tj_chuhuoscan_info_chuhuoliang);
	   info_zhanrongliang=(EditText) findViewById(R.id.tj_chuhuoscan_info_zhanrongliang);
	   info_kehujixing=(EditText) findViewById(R.id.tj_chuhuoscan_info_custormerjixing);
	   info_chuhuodi=(EditText) findViewById(R.id.tj_chuhuoscan_info_chuhuodi);
	   
	   zhanbansn_edit=(EditText) findViewById(R.id.tj_chuhuoscan_zhanbansn);
	   xiangsn_edit=(EditText) findViewById(R.id.tj_chuhuoscan_xiangboxsn);
	   
	   
	   infocount_chuhuocount_edit=(EditText) findViewById(R.id.tj_chuhuoscan_chuhuotongji);
	   infocount_zhanbancount_edit=(EditText) findViewById(R.id.tj_chuhuoscan_zhanbantongji);
	   infocount_xiangcount_edit=(EditText) findViewById(R.id.tj_chuhuoscan_xiangtongji);
		  
	   
		editscan=(EditText) findViewById(R.id.tj_chuhuoscan_editscan);

		spinner1.setOnItemSelectedListener(this);
		spinner2.setOnItemSelectedListener(this);
		
		zhanbansn_edit.setOnKeyListener(this);
		xiangsn_edit.setOnKeyListener(this);
		
		
		getdingdan();//获取订单信息
		
		
	}

	
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String>  getdata;
		List<HashMap<String, String> > getdata_list;
		
		
		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		switch (task.getTaskType()) {
		case TaskType.tjchuhuoscan:
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
			break;
		//解析订单信息
		case TaskType.tjchuhuoscangetdingdan:
			super.closeProDia();
			getdata_list= (List<HashMap<String,String>>)task.getTaskResult() ;
			if(getdata_list.isEmpty()){
				logSysDetails( "在MES获取订单信息失败，请检查！", "成功");
			}else{
				for(HashMap<String, String> map :getdata_list){
					String dingdan=map.get("yy");
					dingdan_fordingdanadapter.add(dingdan);
				}
				dingdanadapter.notifyDataSetChanged();
				
			}
		break;
		//通过订单名获取到出货单信息
		case TaskType.tjchuhuoscangetchuhuodan:
			super.closeProDia();
			getdata_list= (List<HashMap<String,String>>)task.getTaskResult() ;
			chuhuodan_allinfo=getdata_list;
			chuhuodan_forchuhuodandapter.clear();
			if(getdata_list.isEmpty()){
				logSysDetails( "在MES获取出货单信息失败，请检查！", "成功");
			}else{
				for(HashMap<String, String> map :getdata_list){
					String chuhuodan=map.get("yypo");
					chuhuodan_forchuhuodandapter.add(chuhuodan);
				}
				chuhuodandapter.notifyDataSetChanged();
			}
		break;
		//校验栈板
		case TaskType.tj_chuhuoyy_checkzhanban:
			super.closeProDia();
			clean();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					clean();
					if(Integer.parseInt(value)==0){
						
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						qty=getdata.get("qty");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
						is_zhanbancheck_ok=true;
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						zhanbansn_edit.requestFocus();
						is_zhanbancheck_ok=false;
					}
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
		
		case TaskType.tjchuhuoscanchuhuoscan:
			super.closeProDia();
			clean();
			//String sn = snedit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					clean();
					if(Integer.parseInt(value)==0){
						
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						
						infocount_zhanbancount_edit.setText(getdata.get("showtoui_zhanban"));
						infocount_xiangcount_edit.setText(getdata.get("showtoui_xianghao"));
						infocount_chuhuocount_edit.setText(getdata.get("showtoui_countqty"));
						String isComplete=getdata.get("IsComplete");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
						if(isComplete.equalsIgnoreCase("1" )){
							//栈板完成
						    zhanbansn_edit.requestFocus();
						}
						else
							xiangsn_edit.requestFocus();
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						xiangsn_edit.requestFocus();
						
					}
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

	private void analyseScanneddataAndExecute(String scanRes) {
//		    if(snedit.isFocused()){
//		    	snedit.setText(scanRes);
//		    	String[] paras=new String[6];
//				paras[0]=resourceid;
//				paras[1]=resourcename;
//				paras[2]=useid;
//				paras[3]=usename;
//				paras[4]=chuhuodanid;
//				//paras[5]=snedit.getText().toString().trim();
//				
//				super.progressDialog.setMessage("正在获取信息...");
//				super.showProDia();
//				tjchuhuoyyModel.chuhuoscan(new Task(this,TaskType.tjchuhuoscanchuhuoscan,paras));
//				   
//		    }
		    
		    
	}
		
   private   void  getdingdan(){
	    super.progressDialog.setMessage("正在获取信息...");
		super.showProDia();
		tjchuhuoyyModel.getdingdan(new  Task(this,TaskType.tjchuhuoscangetdingdan,""));
   }
  
   
   private   void  getchuhuodan(String  para){
	    super.progressDialog.setMessage("正在获取信息...");
		super.showProDia();
		tjchuhuoyyModel.getchuhuodan(new  Task(this,TaskType.tjchuhuoscangetchuhuodan,para));
   }
	
	private  void  clean(){
		if(editscan.getLineCount()>20){
			editscan.setText("");
		}
	}
	

	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.tj_chuhuoscan_zhanbansn:
			String[] paras=new String[2];
			
			paras[0]=yypoid;
		    paras[1]=zhanbansn_edit.getText().toString().trim();
			
			
			
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    super.progressDialog.setMessage("正在获取信息...");
					super.showProDia();
					tjchuhuoyyModel.checkzhanban(new Task(this,TaskType.tj_chuhuoyy_checkzhanban,paras));
			   
			}			
		break;
		
		case R.id.tj_chuhuoscan_xiangboxsn:
			if(Integer.parseInt(qty_shengyu)<=0){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("警告");
				builder.setMessage(" 送货单已达到送货单数量！请检查信息！");
				builder.setPositiveButton("确定",null);
				builder.setNegativeButton("取消", null);
				builder.create().show();
				return false;
			}
			if(!is_zhanbancheck_ok){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("警告");
				builder.setMessage(" 请先校验栈板号通过，在执行下面的作业！");
				builder.setPositiveButton("确定",null);
				builder.setNegativeButton("取消", null);
				builder.create().show();
				return false;
			}
			String[] paras2=new String[6];
			String  qty_in_zhongxiang=info_xiangrongliang.getText().toString();
			String  boxqty_in_zhanban=info_zhanrongliang.getText().toString();
			

			
			paras2[0]=yypoid;
			paras2[1]=zhanbansn_edit.getText().toString().trim();
			paras2[2]=xiangsn_edit.getText().toString().trim();
			paras2[3]=qty_shengyu;//数据库是整形
			paras2[4]=Integer.parseInt(qty_in_zhongxiang)*Integer.parseInt(boxqty_in_zhanban)+"";//栈板总容量
			paras2[5]=qty;//数据库类型是整形，实际栈板容量
			
			
			
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    super.progressDialog.setMessage("正在获取信息...");
					super.showProDia();
					tjchuhuoyyModel.chuhuoscan(new Task(this,TaskType.tjchuhuoscanchuhuoscan,paras2));
			   
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		switch(arg0.getId()){
		case  R.id.tj_chuhuoscan_spinner1:
			String dingdan=arg0.getSelectedItem().toString();
			getchuhuodan(dingdan);
		break;
		
		case  R.id.tj_chuhuoscan_spinner2:
			String  yypo=arg0.getSelectedItem().toString();
			for(HashMap<String, String> map:chuhuodan_allinfo){
				if(map.get("yypo").equals(yypo)){
					yypoid=map.get("yypoid");
					qty_shengyu=map.get("shengyuqty");
							
					info_chuhuoliang.setText(map.get("QtyInput"));
					info_chuhuodi.setText(map.get("destination"));
					info_kehujixing.setText(map.get("CustomerModel"));
					info_productname.setText(map.get("ProductName"));
					info_xiangrongliang.setText(map.get("BoxQty"));
					info_zhanrongliang.setText(map.get("CartoonQty"));
				}
			}
		break;
		
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}
