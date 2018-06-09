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
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.QuantityAdjustModel;
import com.zowee.mes.model.SmtQtyChangeModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.utils.StringUtils;

public class SmtqtyChange extends CommonActivity 
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
   private  EditText  lotsn_edit;
   private  TextView  lotsn_tv;
   
   private  EditText  lotqty_edit;
   
  
 
   private  EditText  editscan;
   
   

  
   private GetMOnameModel GetMonamemodel; // ��������
	private SmtQtyChangeModel smtQtyChangeModel; // ��������
	private QuantityAdjustModel  quantityAdjustModel;
	private static final String TAG = "SmtChaoling";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_smt_qtychange);
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
								stopService(new Intent(SmtqtyChange.this,
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
		smtQtyChangeModel = new SmtQtyChangeModel();
		quantityAdjustModel=new QuantityAdjustModel();
		
		
		
		
		   lotsn_edit=(EditText) findViewById(R.id.smtqtychange_lotsnedit);
		   lotqty_edit=(EditText) findViewById(R.id.smtqtychange_lotqtyedit);
		   
		   lotsn_tv =(TextView) findViewById(R.id.smtqtychange_lotsntv);
		
		   
		   
		editscan=(EditText) findViewById(R.id.smtipqc_editscan);


		lotsn_edit.setOnKeyListener(new  OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if (KeyEvent.KEYCODE_ENTER == keyCode&& event.getAction() == KeyEvent.ACTION_DOWN) {
					    
					String lotnsn= lotsn_edit.getText().toString().trim();
					if(lotnsn.equals("")||lotnsn.length()<8){
						Toast.makeText(SmtqtyChange.this, "�������벻��Ϊ�ջ��߳��Ȳ���", 1).show();
						return  false;
					}
				
					
					
					progressDialog.setMessage("���ڻ�ȡ��Ϣ...");
					showProDia();
					smtQtyChangeModel.getQty(new Task(SmtqtyChange.this,100,lotnsn));
				   
				}			
				
				return false;
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
	public void submit(View view){
		
		
		super.progressDialog.setMessage("��ѯ��......");
		super.showProDia();	 
		
		
		String[] taskDatas = new String[]{this.lotsn_edit.getText().toString().trim(),this.lotqty_edit.getText().toString().trim()};
		Task task = new Task(this, 200,taskDatas);
		quantityAdjustModel.quantityAdjust(task);
		
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
			    List<HashMap<String, String>> qty = (List<HashMap<String, String>>) task.getTaskResult();
				
				String qty_va=qty.get(0).get("qty");
				Log.d(TAG, "task�Ľ�������ǣ�" + qty_va);
				lotsn_tv.setText(qty_va);
				
			} else {
				logSysDetails("�����������ȡ����������ϢΪ��","�ɹ�");
			}
			break;
			
			
			
		case 200:
			closeProDia();
			if(super.isNull)
				return;
			String[] reses = (String[])task.getTaskResult();
			String res = reses[0];
			String retInfor = reses[1];
//			if(StringUtils.isEmpty(res)||StringUtils.isEmpty(retInfor))
//				return;
			StringBuffer sbf = new StringBuffer(this.editscan.getText().toString());
			if(!StringUtils.isEmpty(res)&&res.trim().equals("0"))
				sbf.append(getString(R.string.quantityAdjust_suc)+"\n");
			else
				sbf.append(getString(R.string.quantityAdjust_fail)+"\n");
			if(StringUtils.isEmpty(retInfor))
				retInfor = "";
			sbf.append(retInfor+"\n\n");
			editscan.setText(sbf.toString());
			
			  lotsn_edit.setText("");
			   lotsn_tv.setText("");
			   lotqty_edit.setText("");
			   
			   
			   lotsn_edit.requestFocus(); 
			  SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
			   
			
			break;
		

//		case 100:
//			super.closeProDia();
//			if (null != task.getTaskResult()) {
//				getdata = (HashMap<String, String>) task.getTaskResult();
//				String value=getdata.get("I_ReturnValue");
//				
//				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
//				if (getdata.get("Error") == null) {
//					if(Integer.parseInt(value)==0){
//						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
//						logSysDetails(scantext, "�ɹ�");
//						SoundEffectPlayService
//								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
//					}
//					else{
//						SoundEffectPlayService
//						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
//						String scantext ="ʧ�ܣ�"+getdata.get("I_ReturnMessage");
//						logSysDetails(scantext, "�ɹ�");
//					}
//					slotedit.requestFocus();
//					slotedit.setText("");
//					numedit.setText("");
//					mzedit.setText("");
//
//				} else {
//					logSysDetails(
//							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
//									+ getdata.get("Error"), "�ɹ�");
//				}
//				closeProDia();
//			} else {
//				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
//			}
//			break;

		
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
	
	
	
	
}
