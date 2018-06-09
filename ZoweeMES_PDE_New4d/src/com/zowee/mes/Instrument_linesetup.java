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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.InstrumentModel;
import com.zowee.mes.model.TjchuhuoscanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class Instrument_linesetup extends CommonActivity implements
		OnKeyListener
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
   private  EditText  snedit;
   private  EditText  workcenteredit;
   private  String  workcenterid;
 
   private  EditText  editscan;
   
    private GetMOnameModel GetMonamemodel; // ��������
	private InstrumentModel instrumentModel; // ��������
	private static final String TAG = "Instrument_linesetup";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_instrument_linesetup);
		init();
	}

	public void onBackPressed() {
		killMainProcess();
	}
   
	// ���˼��ص����activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("ȷ���˳�����?")
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								stopService(new Intent(Instrument_linesetup.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.instrumentscan; // ��̨����̬���γ���
		//ɨ������Ҳ�Ǹ���̨����
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		instrumentModel = new InstrumentModel();
		
	    snedit=(EditText) findViewById(R.id.instrument_linesetup_sn);
	    workcenteredit=(EditText) findViewById(R.id.instrument_linesetup_workcenter);
	   
		editscan=(EditText) findViewById(R.id.instrument_linesetup_editscan);
		editscan.setText("ʹ��˵����\n1������õ��Ե����õ���Դ�����Ƿ���ȷ���Ϸ�����ע�ᣡ\n2��ϵͳ���Զ�������Դ���Ƽ�⹤�����ģ����Ȼ�ȡ��ȷ�Ĺ������ģ��ٿ�ʼ�豸�󶨣�");

		snedit.setOnKeyListener(this);
		
        GetResourceId();
	}
	private void GetResourceId()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
		GetMonamemodel.GetResourceId(task);
	}
	/*
	 * ˢ��UI����
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String>  getdata;
		/**
		 * ��������ύ�������ķ��صĽ������UI����ĸ��£���
		 * 
		 * */
		switch (task.getTaskType()) {
		case TaskType.instrumentscan:
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
			break;
			
		case TaskType.GetResourceId:
			resourceid = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			resourceid = getresult.get(0).get("ResourceId");
			if(resourceid.isEmpty())	 
				logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
			else{
				logSysDetails("�ɹ���ȡ�����豸����ԴID:"+resourceid,"�ɹ�");
				getworkcenter(resourceid);
			}
		break;
		
		case TaskType.instrumentgetworkcenter:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					
					workcenterid = getdata.get("WorkcenterId");
					String workcentername = getdata.get("WorkcenterName");
					workcenteredit.setText(workcentername);
					String scantext = "ͨ��������Դ����[" + resourcename + "]�ɹ��Ļ�øù�������:"+ workcentername+ "!";
					logSysDetails(scantext, "�ɹ�");

				} else {
					logSysDetails(
							"��MES��ȡ������ϢΪ�ջ��߽������Ϊ�գ�����!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Ϣʧ�ܣ����飡", "�ɹ�");
			}

			break;
		
		
		
		case TaskType.instrumentlinesetup:
			super.closeProDia();
			String sn = snedit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					clean();
					if(Integer.parseInt(value)==0){
						
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}
					snedit.requestFocus();
					snedit.setText("");
					
				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
		
		}
	}

	private void analyseScanneddataAndExecute(String scanRes) {
		    if(snedit.isFocused()){
		    	snedit.setText(scanRes);
		    	if(workcenterid!=null&&!workcenterid.equals("")){
		    	String[] paras=new String[6];
				paras[0]=resourceid;
				paras[1]=resourcename;
				paras[2]=useid;
				paras[3]=usename;
				paras[4]=workcenterid;
				paras[5]=snedit.getText().toString().trim();
				
				super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
				super.showProDia();
				instrumentModel.linesetup(new Task(this,TaskType.instrumentlinesetup,paras));
		    	}	
		    }
		    
		    
	}

	
	private  void  clean(){
		if(editscan.getLineCount()>10){
			editscan.setText("");
		}
	}
	
   private void getworkcenter(String resourceid){
	   super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
	super.showProDia();
	   instrumentModel.getworkcenter(new  Task(this,TaskType.instrumentgetworkcenter,resourceid));
   }
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.instrument_linesetup_sn:
			String[] paras=new String[6];
			paras[0]=resourceid;
			paras[1]=resourcename;
			paras[2]=useid;
			paras[3]=usename;
			paras[4]=workcenterid;
			paras[5]=snedit.getText().toString().trim();
			
			
			
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(workcenterid!=null&&!workcenterid.equals("")){
				    super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
					super.showProDia();
				    instrumentModel.linesetup(new Task(this,TaskType.instrumentlinesetup,paras));
				}
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

	
}