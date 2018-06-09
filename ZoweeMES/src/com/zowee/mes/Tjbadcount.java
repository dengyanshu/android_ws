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
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.TjbadcountModel;
import com.zowee.mes.model.TjdipstartModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
/**
 * ������
 * 1��ͳ�Ʋ������� ɨ��̶�����ERRORCODE,�Զ��ύ�����ݿ�
 * 2��û�����룬ֻ�Ǵ�����ҵԱ������Ŀ��ͳ��
 * */
public class Tjbadcount extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private EditText editMO;
	private  Button  mobutton;//�����ת����һ������ѡ�񹤵�
	private String moid; // ����ѡ�񱣴����
	private EditText editLine;
	private  Button  linebutton;//�����ת����һ������ѡ��xianti 
	private String lineid; 
	
	
	private EditText edithour;
	private EditText editnum;
	private int num=1;
	
	private EditText editsn;
	
	private EditText editscan;

	
	private TjbadcountModel tjbadcountModel; // ��������
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Tjbadcount";
	private static final int REQUESTCODE = 0;
	private static final int REQUESTCODE2 = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tj_badcount);
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
								stopService(new Intent(Tjbadcount.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.biaoqianhedui; // ��̨����̬���γ���
		super.init();

		tjbadcountModel = new TjbadcountModel();
		common4dmodel=new  Common4dModel();
		
		
		editMO = (EditText) findViewById(R.id.tjbadcount_moedit);
		mobutton=(Button) findViewById(R.id.tjbadcount_mobutton);
		editLine = (EditText) findViewById(R.id.tjbadcount_lineedit);
		linebutton=(Button) findViewById(R.id.tjbadcount_linebutton);
		
		edithour = (EditText) findViewById(R.id.tjbadcount_houredit);
		editnum = (EditText) findViewById(R.id.tjbadcount_countnumedit);
		
		editsn = (EditText) findViewById(R.id.tjbadcount_snedit);

		editscan = (EditText) findViewById(R.id.tjbadcount_editscan);
		editscan.setFocusable(false);
		
		
		mobutton.setOnClickListener(this);
		linebutton.setOnClickListener(this);
		editsn.setOnKeyListener(this);
		
        common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));

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
		// ��ȡ����ID
		case TaskType.common4dmodelgetresourceid:
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if (getdata.containsKey("ResourceId")) {
						resourceid = getdata.get("ResourceId");
					}
					String logText = "����������!��⵽��ƽ�����Դ����:[ " + resourcename
							+ " ],��ԴID: [ " + resourceid + " ],�û�ID: [ " + useid + " ]!!�����������������������2�֣���";
					logSysDetails(logText, "����");
				} else {
					logSysDetails(
							"ͨ����Դ���ƻ�ȡ��MES��ȡ��ԴIDʧ�ܣ��������õ���Դ�����Ƿ���ȷ", "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails( "��MES��ȡ��Դid��Ϣʧ�ܣ�������������Դ�����Ƿ���ȷ", "�ɹ�");
			}

			break;
		
		
	
		case TaskType.tjbadcount:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "�����ɼ��ɹ���";
						logSysDetails(scantext, "�ɹ�");
						if(edithour.getText().toString().trim().equals(new  Date().getHours()+"")){
							edithour.setText(new  Date().getHours()+"");
							editnum.setText(num+"");
							num++;
						}
						else{
							edithour.setText(new  Date().getHours()+"");
							num=1;
							editnum.setText(num+"");
							num++;
						}
							
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					
					}
					else{
						String scantext = "ʧ�ܣ�";
						logSysDetails(scantext, "�ɹ�");
					}
					editsn.setText("");
					editsn.requestFocus();

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails("�ύMESʧ������������߹������������������", "�ɹ�");
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tjbadcount_mobutton:
			Intent  mointent=new Intent(this,Tjbadcountmoselect.class);
			startActivityForResult(mointent, REQUESTCODE);
			break;
		case R.id.tjbadcount_linebutton:
			Intent  lineintent=new Intent(this,Tjbadcountlineselect.class);
			startActivityForResult(lineintent, REQUESTCODE2);
			break;
		}
	}
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0) {
			if (requestCode == REQUESTCODE) {
				moid = data.getStringExtra("MOID");
				String moname = data.getStringExtra("MOName");
				String modescription = data.getStringExtra("MODescription");
				Log.i(TAG, "��һ�����淵�ص����ݣ���" + moid + "::" + moname);

				editMO.setText(moname);

			}

		}
		if (resultCode == 1) {
			if (requestCode == REQUESTCODE2) {
				lineid = data.getStringExtra("WorkcenterId");
				String WorkcenterName = data.getStringExtra("WorkcenterName");
				String WorkcenterDescription = data.getStringExtra("WorkcenterDescription");
				Log.i(TAG, "��һ�����淵�ص����ݣ���" + lineid + "::" + WorkcenterName);

				editLine.setText(WorkcenterName);

			}

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
		case R.id.tjbadcount_snedit:
			String[]  params=new String[2];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(moid==null||moid.equals("")||lineid==null||lineid.equals("")){
					Toast.makeText(this, "���Ȼ�ȡ������������Ϣ ���������룡", Toast.LENGTH_LONG).show();
				   return  false;
				}
				else{
					if(editsn.getText().toString().trim().equalsIgnoreCase("ERRORCODE")){
						super.progressDialog.setMessage("�����ύ��...");
						super.showProDia();
						
						params[0] = moid;
						params[1] = lineid;
						tjbadcountModel.badcount(new  Task(this,TaskType.tjbadcount,params));
					}
					else{
						Toast.makeText(this, "�����̶��������", Toast.LENGTH_LONG).show();
					}
					
				}
			}
			break;
		}
		return false;
	}

	

}
