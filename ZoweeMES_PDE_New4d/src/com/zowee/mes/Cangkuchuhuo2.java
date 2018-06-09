/**
 * wang �ֿ������
 * ��ӦPZHD  ���ߺ˶�
 * */

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
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.EquipmentMaintenancekModel;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtIPQCModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
public class Cangkuchuhuo2 extends CommonActivity implements
		OnKeyListener
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
	private  EditText   editPO;
	private  EditText  editPOnum;
	private  EditText  editzhanbannum;
	
	
	private  EditText  eidtCheckPO;
	private  EditText editzhanbansn;
	private  EditText  box1;
	private  EditText  box2;
	private  EditText  box3;
	private  EditText  box4;
	
	
  
   private  EditText  editscan;
   
   

  
    private GetMOnameModel GetMonamemodel; // ��������
	private SmtIPQCModel smtIPQCModel; // ��������
	private static final String TAG = "SmtIPQC";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_ck_chuhuo);
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
								stopService(new Intent(Cangkuchuhuo2.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.smtipqcscan; // ��̨����̬���γ���
		//ɨ������Ҳ�Ǹ���̨����
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		smtIPQCModel = new SmtIPQCModel();

		
	
		editPO=(EditText) findViewById(R.id.ckchuhuo_poedit);
		editPOnum=(EditText) findViewById(R.id.ckchuhuo_ponumedit);
		editzhanbannum=(EditText) findViewById(R.id.ckchuhuo_zhanbannumedit);
		
		eidtCheckPO=(EditText) findViewById(R.id.ckchuhuo_pocheckedit);
		editzhanbansn=(EditText) findViewById(R.id.ckchuhuo_zhanbanedit);
		box1=(EditText) findViewById(R.id.ckchuhuo_box1);
		box2=(EditText) findViewById(R.id.ckchuhuo_box2);
		box3=(EditText) findViewById(R.id.ckchuhuo_box3);
		box4=(EditText) findViewById(R.id.ckchuhuo_box4);
		
		editscan=(EditText) findViewById(R.id.smtipqc_editscan);
		

		editPO.setOnKeyListener(this);
     	box4.setOnKeyListener(this);
		
        GetResourceId();
	}
	private void GetResourceId()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
		GetMonamemodel.GetResourceId(task);

	}
	
	//���po ������
	public void click(View view){
		AlertDialog.Builder builder11 = new AlertDialog.Builder(this);
		builder11.setTitle("����PO");
		builder11.setMessage("�Ƿ���Ҫ���¸���PO��");
		builder11.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						editPO.setText("");
						editPO.setEnabled(true);
						editPO.requestFocus();
						editPOnum.setText("");
						editzhanbannum.setText("");
						clearWorkUI();
					}
				});
		builder11.setNegativeButton("ȡ��", null);
		builder11.create().show();
	}
	/*
	 * ˢ��UI����
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		
		/**
		 * ��������ύ�������ķ��صĽ������UI����ĸ��£���
		 * 
		 * */
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
		
		case 10010:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						editPO.setText(getdata.get("Po"));
						//PO����
						if(getdata.containsKey("PO����")){
							editPOnum.setText(getdata.get("PO����"));
						}
						//SumPalletCount
						if(getdata.containsKey("SumPalletCount")){
							editzhanbannum.setText(getdata.get("SumPalletCount"));
						}
						editPO.setEnabled(false);
						eidtCheckPO.requestFocus();
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
						editPO.setText("");
						editPO.requestFocus();
					}
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
			//String sn = snedit.getText().toString().toUpperCase().trim();
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
					eidtCheckPO.requestFocus();
					clearWorkUI();

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
		
		}
	}
	//����²��ֵ�ui
	private   void  clearWorkUI(){
		eidtCheckPO.setText("");
		editzhanbansn.setText("");
		box1.setText("");
		box2.setText("");
		box3.setText("");
		box4.setText("");
	}

	/*private void analyseScanneddataAndExecute(String scanRes) {
		    if(editMO.isFocused()){
		    	editMO.setText(scanRes);
		    	super.progressDialog.setMessage("���ڻ�ȡ������Ϣ...");
				super.showProDia();
				smtIPQCModel.getmonamebylotsn(new Task(this,TaskType.smtipqcgetmoname,editMO.getText().toString()));
		    }
		    else if(carsnedit.isFocused()){
		    	carsnedit.setText(scanRes);
		    	  super.progressDialog.setMessage("���ڻ�ȡװ��������Ϣ...");
					super.showProDia();
					smtIPQCModel.getpcbanumincar(new Task(this,TaskType.smtipqgetcar,carsnedit.getText().toString()));
		    }
		    else if(snedit.isFocused()){
		    	snedit.setText(scanRes);
		    	errorcodeedit.requestFocus();
		    }
		    else if(errorcodeedit.isFocused()){
		    	errorcodeedit.setText(scanRes);
		    	String[] paras=new String[8];
				paras[0]=resourceid;
				paras[1]=resourcename;
				paras[2]=useid;
				paras[3]=usename;
				
				paras[4]=editMO.getText().toString().trim();
				paras[5]=carsnedit.getText().toString().trim();
				paras[6]=snedit.getText().toString().trim();
				paras[7]=errorcodeedit.getText().toString().trim();
				
				
						
				super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
				super.showProDia();
				smtIPQCModel.ipqcpcba(new Task(this,TaskType.smtipqcpcba,paras));

		    }
	}*/
		

	
		
		
			
	 		
	
	

	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		
		case R.id.ckchuhuo_poedit:
			String sn = editPO.getText().toString().toUpperCase().trim();//
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				    super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
					super.showProDia();
					smtIPQCModel.getPO2(new Task(this,10010,sn));
			   
			}			
		break;
		
		case R.id.ckchuhuo_box4:
			//String car = carsnedit.getText().toString().toUpperCase().trim();//
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				
				if("".equals(editPO.getText().toString())){
					Toast.makeText(this, "�����Ȼ�ȡ��PO���ύ��ҵ��", Toast.LENGTH_LONG).show();
					return false;
				}
				
				String[] paras=new String[15];
				paras[0]=resourceid;
				paras[1]=resourcename;
				paras[2]=useid;
				paras[3]=usename;
				
				paras[4]=editPO.getText().toString();
				paras[5]=eidtCheckPO.getText().toString();
				paras[6]=editzhanbansn.getText().toString();
				paras[7]=box1.getText().toString();
				paras[8]=box2.getText().toString();
				paras[9]=box3.getText().toString();
				paras[10]=box4.getText().toString();
				
				
				  super.progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
					super.showProDia();
					smtIPQCModel.canzhanban2(new Task(this,10086,paras));
			   
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
