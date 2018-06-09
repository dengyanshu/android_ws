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
 * ջ�������ʽ��
 * submit�ύ��Ȼ���ʽ�ڲ������ʼ��ӿڣ�
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
   private  TextView  tv;//��ʾջ�����

   private GetMOnameModel GetMonamemodel; // ��������
   private PalletOutModel palletOutModel; // ��������
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
   
	// ���˼��ص����activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("ȷ���˳�����?")
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
		super.TASKTYPE = TaskType.smtipqcscan; // ��̨����̬���γ���
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
						Toast.makeText(PalletOut.this, "SN���Ȳ��ԣ�", 1).show();
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
							Toast.makeText(PalletOut.this, "ջ���Ų���Ϊ�գ���", 1).show();
							return  false;
						}
						submit();
					}
					else{
						if(lotnsn.equals("")||lotnsn.length()<6){
							Toast.makeText(PalletOut.this, "ջ��SN���Ȳ��ԣ�", 1).show();
							return  false;
						}
						else  if("".equals(moOrProduct)){
							Toast.makeText(PalletOut.this, "��ѡ��������ͣ�", 1).show();
							return  false;
						}
						palletsn=lotnsn;
						submit(1);
//						logSysDetails("ջ���SN��"+palletsn+"���ɼ��ɹ�����ɨ��SUBMIT�����ύ��","�ɹ�");
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
				if("������".equals(selected_str)){
					moOrProduct="0";
				}
				else if("���Ϻ�".equals(selected_str)){
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
	/**����������
	 * */
	private void getMO(String sn) {
		super.progressDialog.setMessage("Get MO....");
		super.showProDia();	 
		
		Task task = new Task(this, 10010, sn);	
		palletOutModel.getMO(task);
		
	}
	
	public void submit(){
		
		
		super.progressDialog.setMessage("�ύ��......");
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
		
		
		super.progressDialog.setMessage("У��ջ����......");
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
		
		
		case 10010:
			super.closeProDia();
			String lotsn = mo_edit.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					
					moname = getdata.get("MOName");
					

					mo_edit.setText(moname);
					mo_edit.setEnabled(false);
					//editcartoonsn.requestFocus();
					String scantext = "ͨ�����κţ�[" + lotsn + "]�ɹ��Ļ�ù���:"
							+ moname + "!";
					logSysDetails(scantext, "�ɹ�");
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					lotsn_edit.requestFocus();

				} else {
					logSysDetails(
							"ͨ�����κţ�[" + lotsn + "]��MES��ȡ������ϢΪ�ջ��߽������Ϊ�գ�����SN!"
									+ getdata.get("Error"), "�ɹ�");
					mo_edit.requestFocus();
				}
				closeProDia();
			} else {
				logSysDetails(lotsn + "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
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
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						if(sns.contains(palletsn)){
							logSysDetails("�ظ�ɨ��", "�ɹ�");
							return;
						}
						count++;
						String scantext = "�ɹ����Ѳɼ�"+spinner.getSelectedItem().toString()+"["+count+"]:"+palletsn;
						logSysDetails(scantext, "�ɹ�");
						tv.setText(count+"");
						sns=sns+palletsn+",";
						SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}
					//lotsn2_edit.requestFocus();
					lotsn_edit.requestFocus();
					lotsn_edit.setText("");
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
		
		

		case 200:
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
					//lotsn2_edit.requestFocus();
					tv.setText("��ɨ��");
					count=0;
					sns="";
					lotsn_edit.requestFocus();
					lotsn_edit.setText("");
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
