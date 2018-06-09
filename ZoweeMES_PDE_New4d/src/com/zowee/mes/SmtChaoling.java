package com.zowee.mes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtChaolingModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class SmtChaoling extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
	private EditText editMO;
	private  String moid;
	private String moname;
	
   private   Spinner  workcenterSpinner;
   
   
   private   Spinner  StockSpinner;
   private  HashMap<String, String>  stockmap=new HashMap<String, String>();
   {
	   stockmap.put("ԭ�ֱ�", "");
	   stockmap.put("029 �������Ŀ͹��ϲ�", "STOK0000003X");
	   stockmap.put("040 �Բ���", "STOK00000046");
	   stockmap.put("061 OEM �͹������", "STOL0000004V");
   }
   
   private  EditText  numedit;
   private  EditText  mzedit;
   
 
   private  EditText  editscan;
   
   

 
   
   private GetMOnameModel GetMonamemodel; // ��������
	private SmtChaolingModel smtChaolingModel; // ��������
	private static final String TAG = "SmtChaoling";
	private static final int REQUESTCODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_smtjit_chaoling);
		init();
	}

	protected void onResume() {
		super.onResume();
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
								stopService(new Intent(SmtChaoling.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.smtipqcscan; // ��̨����̬���γ���
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		smtChaolingModel = new SmtChaolingModel();
		
		editMO=(EditText) findViewById(R.id.smtjitchaoling_moedit);
		
	
		 workcenterSpinner = (Spinner) findViewById(R.id.smtjitchaoling_workcenterSpinner);
		 StockSpinner = (Spinner) findViewById(R.id.smtjitchaoling_StockSpinner);
		   
		  numedit=(EditText) findViewById(R.id.smtjitchaoling_chaolingnumedit);
		  mzedit = (EditText) findViewById(R.id.smtjitchaoling_mzlotedit);
		   
		editscan=(EditText) findViewById(R.id.smtjitchaoling_editscan);

//		mzedit.setOnKeyListener(new  OnKeyListener() {
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				// TODO Auto-generated method stub
//				
//				if (KeyEvent.KEYCODE_ENTER == keyCode&& event.getAction() == KeyEvent.ACTION_DOWN) {
//					      
//					String[] paras=new String[9];
//					paras[0]=resourceid;
//					paras[1]=resourcename;
//					paras[2]=useid;
//					paras[3]=usename;
//					
//					paras[4]=moid;
//					paras[5]=workcenterSpinner.getSelectedItem().toString();
//					paras[6]=numedit.getText().toString().trim();
//					paras[7]=mzedit.getText().toString().trim();
//					paras[8]=editMO.getText().toString().trim();
//					progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
//					showProDia();
//					smtChaolingModel.chaoling(new Task(SmtChaoling.this,100,paras));
//				   
//				}			
//				
//				return false;
//			}
//		});
		
        GetResourceId();
	}
	private void GetResourceId()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
		GetMonamemodel.GetResourceId(task);

	}
	public  void  moonclick(View v){
		startActivityForResult(new Intent(this,SmtChaolingMoinfo.class), REQUESTCODE);
		
	}
	/*
	 * ˢ��UI����
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
				logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			resourceid = getresult.get(0).get("ResourceId");
			if(resourceid.isEmpty())	 
				logSysDetails("δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ","shibai");
			else{
				logSysDetails("�ɹ���ȡ�����豸����ԴID:"+resourceid,"�ɹ�");
			}
		break;
		

		case 100:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
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
					mzedit.requestFocus();
					numedit.setText("");
					mzedit.setText("");

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;

		
		case 10086:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
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
					mzedit.requestFocus();
					numedit.setText("");
					mzedit.setText("");

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
	
			
			//��ȡ����ˢ��spinner
		case 10010:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				List<HashMap<String, String>> res_list= (List<HashMap<String, String>>) task.getTaskResult();
				Log.d(TAG, "task11111�Ľ�������ǣ�" + res_list);
				List  <String> workcenters=new ArrayList<String>();
				
				for(HashMap<String, String> map:res_list){
					workcenters.add(map.get("WorkcenterName"));
				}
				
				if (res_list.size()!=0) {
					ArrayAdapter<String> adapter=new ArrayAdapter<String>(SmtChaoling.this, android.R.layout.simple_spinner_dropdown_item, workcenters);
					
					//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					workcenterSpinner.setAdapter(adapter);
				    
				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									, "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
		}
	}


		
	  public void submit(View v){
		   String[] paras=new String[12];
			paras[0]=resourceid;
			paras[1]=resourcename;
			paras[2]=useid;
			paras[3]=usename;
			
			paras[4]=moid;
			paras[5]=moname;
			paras[6]=workcenterSpinner.getSelectedItem().toString();
			paras[7]=stockmap.get(StockSpinner.getSelectedItem().toString());//��ID
			paras[8]=mzedit.getText().toString().trim();
			paras[9]=numedit.getText().toString().trim();
			progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
			showProDia();
			smtChaolingModel.chaoling(new Task(SmtChaoling.this,100,paras));
		  
	  }
	  
	  
	  public void oa(View v){
		   String[] paras=new String[12];
			paras[0]=resourceid;
			paras[1]=resourcename;
			paras[2]=useid;
			paras[3]=usename;
			
			paras[4]=moid;
		
			progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
			showProDia();
			smtChaolingModel.oa(new Task(SmtChaoling.this,10086,paras));
		  
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==0){
			if(requestCode==REQUESTCODE){
				moname=data.getStringExtra("MOName");
				moid=data.getStringExtra("MOId");
				editMO.setText(moname);
				mzedit.requestFocus();
				getWorkcenterName(moname);
			}
		}
	}
	
    /**
     * ����ѡ��Ĺ�����������ļ��� ��󴫵��ӿڵ���workcenterName
     * 
     * */
	
	private  void getWorkcenterName(String jitmoname){
		super.progressDialog.setMessage("Get line...");
		super.showProDia();	 
		Task task = new Task(this, 10010 ,jitmoname);	
		smtChaolingModel.getworkcenternamebyjitmoname(task);

	}
	
	
}
