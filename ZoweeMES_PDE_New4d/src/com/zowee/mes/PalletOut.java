package com.zowee.mes;
import java.text.SimpleDateFormat;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.PalletOutModel;
import com.zowee.mes.model.QuantityAdjustModel;
import com.zowee.mes.model.SmtQtyChangeModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.utils.StringUtils;
/*
 * 栈板出货程式：
 * submit提交，然后程式内部调用邮件接口！
 * */
public class PalletOut extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
   private  EditText  lotsn_edit;
   private  String    palletsn="";
   private  String    sns="";
   private  EditText  lotsn2_edit;
  
   private  EditText  mo_edit;
   private String moname="";
   private  Spinner  spinner;
 
   private  EditText  editscan;
   private  int count=0;
   private  TextView  tv;//显示栈板计数

   private GetMOnameModel GetMonamemodel; // 任务处理类
   private PalletOutModel palletOutModel; // 任务处理类
   private static final String TAG = "SmtChaoling";
  
   
   private RadioGroup  shifRadiogroup;
   private  String moOrProduct="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_pallet_out);
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
								stopService(new Intent(PalletOut.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.smtipqcscan; // 后台服务静态整形常量
		super.init();
        
		palletOutModel=new PalletOutModel();
		GetMonamemodel=new  GetMOnameModel();
		
		
		
		lotsn_edit=(EditText) findViewById(R.id.palletout_lotsnedit);
		lotsn2_edit=(EditText) findViewById(R.id.palletout_lotsn2edit);
		editscan=(EditText) findViewById(R.id.smtipqc_editscan);
         
		
		mo_edit=(EditText) findViewById(R.id.palletout_moedit);
		mo_edit.setOnKeyListener(new  OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode&& event.getAction() == KeyEvent.ACTION_DOWN) {
					String lotnsn= mo_edit.getText().toString().trim();
					
					if(lotnsn.equals("")||lotnsn.length()<8){
						Toast.makeText(PalletOut.this, "SN长度不对！", 1).show();
						return  false;
					}
					else{
						getMO(lotnsn);
					}
					
				}			
				
				return false;
			}

		
		});
		mo_edit.requestFocus();
		spinner=(Spinner) findViewById(R.id.palletout_spinner);

		lotsn_edit.setOnKeyListener(new  OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if (KeyEvent.KEYCODE_ENTER == keyCode&& event.getAction() == KeyEvent.ACTION_DOWN) {
					String lotnsn= lotsn_edit.getText().toString().trim();
					if(lotnsn.contains("submit")){
						if("".equals(sns)){
							Toast.makeText(PalletOut.this, "栈板编号不能为空！！", 1).show();
							return  false;
						}
						submit();
					}
					else{
						if(lotnsn.equals("")||lotnsn.length()<6){
							Toast.makeText(PalletOut.this, "栈板SN长度不对！", 1).show();
							return  false;
						}
						else  if("".equals(moOrProduct)){
							Toast.makeText(PalletOut.this, "请选择规则类型！", 1).show();
							return  false;
						}
						palletsn=lotnsn;
						submit(1);
//						logSysDetails("栈板号SN【"+palletsn+"】采集成功！请扫描SUBMIT进行提交！","成功");
//						lotsn2_edit.requestFocus();
//						lotsn_edit.setText("");
					}
				}			
				
				return false;
			}
		});
		tv=(TextView) findViewById(R.id.palletout_tv);
		
		
		shifRadiogroup=(RadioGroup) findViewById(R.id.shuaka_banci_radiogroup);
		 
	    shifRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int id=group.getCheckedRadioButtonId();
				RadioButton  radiobutton=(RadioButton) findViewById(id);
				RadioButton  radiobutton1=(RadioButton) findViewById(R.id.shuaka_banci_radiobtn1);
				RadioButton  radiobutton3=(RadioButton) findViewById(R.id.shuaka_banci_radiobtn3);
				String selected_str=radiobutton.getText().toString();
				if("按工单".equals(selected_str)){
					moOrProduct="0";
				}
				else if("按料号".equals(selected_str)){
					moOrProduct="1";
				}
				
				radiobutton1.setEnabled(false);
				radiobutton1.setClickable(false);
				radiobutton3.setEnabled(false);
				radiobutton3.setClickable(false);
			}
		});
		
		
        GetResourceId();
	}
	private void GetResourceId()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
		GetMonamemodel.GetResourceId(task);

	}
	/**处理工单事宜
	 * */
	private void getMO(String sn) {
		super.progressDialog.setMessage("Get MO....");
		super.showProDia();	 
		
		Task task = new Task(this, 10010, sn);	
		palletOutModel.getMO(task);
		
	}
	
	public void submit(){
		
		
		super.progressDialog.setMessage("提交中......");
		super.showProDia();	 
		
		String[] paras=new String[9];
		paras[0]=resourceid;
		paras[1]=resourcename;
		paras[2]=useid;
		paras[3]=usename;
		
		paras[4]=sns;
		paras[5]=moname;
		paras[6]=spinner.getSelectedItem().toString();
		paras[7]="1";
		paras[8]=moOrProduct;
		Task task = new Task(this, 200,paras);
		palletOutModel.chaoling(task);
	}
	
	
	private void submit(int num){
		
		
		super.progressDialog.setMessage("校验栈板中......");
		super.showProDia();	 
		
		String[] paras=new String[9];
		paras[0]=resourceid;
		paras[1]=resourcename;
		paras[2]=useid;
		paras[3]=usename;
		
		paras[4]=palletsn;
		paras[5]=moname;
		paras[6]=spinner.getSelectedItem().toString();
		paras[7]="0";
		paras[8]=moOrProduct;
		Task task = new Task(this, 10086,paras);
		palletOutModel.chaoling(task);
	}
	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		switch (task.getTaskType()) {	
		case TaskType.GetResourceId:
			resourceid = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			resourceid = getresult.get(0).get("ResourceId");
			if(resourceid.isEmpty())	 
				logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
			else{
				logSysDetails("成功获取到该设备的资源ID:"+resourceid,"成功");
			}
		break;
		
		
		case 10010:
			super.closeProDia();
			String lotsn = mo_edit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {

					
					moname = getdata.get("MOName");
					

					mo_edit.setText(moname);
					mo_edit.setEnabled(false);
					//editcartoonsn.requestFocus();
					String scantext = "通过批次号：[" + lotsn + "]成功的获得工单:"
							+ moname + "!";
					logSysDetails(scantext, "成功");
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					lotsn_edit.requestFocus();

				} else {
					logSysDetails(
							"通过批次号：[" + lotsn + "]在MES获取工单信息为空或者解析结果为空，请检查SN!"
									+ getdata.get("Error"), "成功");
					mo_edit.requestFocus();
				}
				closeProDia();
			} else {
				logSysDetails(lotsn + "在MES获取工单信息失败，请检查输入的条码", "成功");
			}

		break;

		case 10086:
			super.closeProDia();
			if(count==0){
				ClearshowInfo();
			}
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						if(sns.contains(palletsn)){
							logSysDetails("重复扫描", "成功");
							return;
						}
						count++;
						String scantext = "成功！已采集"+spinner.getSelectedItem().toString()+"["+count+"]:"+palletsn;
						logSysDetails(scantext, "成功");
						tv.setText(count+"");
						sns=sns+palletsn+",";
						SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
					}
					//lotsn2_edit.requestFocus();
					lotsn_edit.requestFocus();
					lotsn_edit.setText("");
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
		
		

		case 200:
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
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
					}
					//lotsn2_edit.requestFocus();
					tv.setText("已扫描");
					count=0;
					sns="";
					lotsn_edit.requestFocus();
					lotsn_edit.setText("");
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
	
	
	public void ClearshowInfo() {
		
			editscan.setText("");
	}
	
	
	
}
