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
import com.zowee.mes.model.HaiercailiaobangdingModel;
import com.zowee.mes.model.HaierzhengjisnbangdingModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
/**
 * HAIER ƽ����� �ڶ�վ  ���������ϰ�
 * */
public class Haierzhengjisnbangding extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private EditText editMO;
	private  TextView  tvmo;//�����ȡ����
	private EditText editMOProduct;
	private EditText editMOdescri;
	private String moid; // ����ѡ�񱣴����
	private  String moname;

	private EditText editzoweesn;
	private EditText editzhengjisn;
	private EditText editdianyuansn;

	
	private Button okbutton;
	

	private EditText editscan;

	

	private HaierzhengjisnbangdingModel haierzhengjisnbangdingModel; // ��������
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Haierzhengjisnbangding";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heier_zhengjisnbangding);
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
								stopService(new Intent(Haierzhengjisnbangding.this,
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

		haierzhengjisnbangdingModel = new HaierzhengjisnbangdingModel();
		common4dmodel=new  Common4dModel();
		
		editMO = (EditText) findViewById(R.id.haierzhengjisnbangding_moedit);
		tvmo=(TextView) findViewById(R.id.haierzhengjisnbangding_motextview);
		editMOdescri = (EditText) findViewById(R.id.haierzhengjisnbangding_modescri);
		editMOProduct = (EditText) findViewById(R.id.haierzhengjisnbangding_moproduct);
		editMO.requestFocus();

		editzoweesn = (EditText) findViewById(R.id.haierzhengjisnbangding_zoweesn);
		editzhengjisn = (EditText) findViewById(R.id.haierzhengjisnbangding_zhengjisn);
		editdianyuansn = (EditText) findViewById(R.id.haierzhengjisnbangding_dianyuansn);

		okbutton = (Button) findViewById(R.id.haierzhengjisnbangding_okbutton);
		okbutton.setFocusable(false);
		

		editscan = (EditText) findViewById(R.id.haierzhengjisnbangding_editscan);
		editscan.setFocusable(false);
		
		okbutton.setOnClickListener(this);
		tvmo.setOnClickListener(this);
        
		editMO.setOnKeyListener(this);
		editdianyuansn.setOnKeyListener(this);
		
		
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
		
		
		case TaskType.common4dmodelgetmobylotsn:
			super.closeProDia();
			String lotsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					
					moname = getdata.get("MOName");
					String productdescri = getdata.get("ProductDescription");
					String material = getdata.get("productName");
					moid = getdata.get("MOId");
					

					editMO.setText(moname);
					editMOdescri.setText(productdescri);
					editMOProduct.setText(material);
					editMO.setEnabled(false);
					editzoweesn.requestFocus();
					String scantext = "ͨ�����κţ�[" + lotsn + "]�ɹ��Ļ�ù���:"
							+ moname + ",����id:"+moid+",��Ʒ��Ϣ:" + productdescri + ",��Ʒ�Ϻţ�"
							+ material + "!";
					logSysDetails(scantext, "�ɹ�");
					SoundEffectPlayService
							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);

				} else {
					logSysDetails(
							"ͨ�����κţ�[" + lotsn + "]��MES��ȡ������ϢΪ�ջ��߽������Ϊ�գ�����SN!"
									+ getdata.get("Error"), "�ɹ�");
				}
				closeProDia();
			} else {
				logSysDetails(lotsn + "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
			}

			break;
			//��
		case TaskType.haierzhengjisnbangding:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String exception=getdata.get("I_ExceptionFieldName");
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						whorequestFocus(exception);
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						whorequestFocus(exception);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}

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
		case R.id.haierzhengjisnbangding_motextview:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("��������");
			builder.setMessage("�Ƿ���Ҫ���¸���������");
			builder.setPositiveButton("ȷ��",
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
			builder.setNegativeButton("ȡ��", null);
			builder.create().show();
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

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		case R.id.haierzhengjisnbangding_moedit:
			String param = editMO.getText().toString().toUpperCase().trim();
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
			   if(param.length()<8){
				   Toast.makeText(this, "���κų��Ȳ���", Toast.LENGTH_SHORT).show();
			   }
			   else{
				    super.progressDialog.setMessage("�������ݿ��ȡ����");
					super.showProDia();
				   common4dmodel.getMObylotsn(new Task(this,TaskType.common4dmodelgetmobylotsn,param));
			   }
			}
			break;

		case R.id.haierzhengjisnbangding_dianyuansn:
			String[]  params=new String[8];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if(editzoweesn.getText().toString().trim().equals("")||editzhengjisn.getText().toString().trim().equals("")||
						editdianyuansn.getText().toString().trim().equals("")){
					Toast.makeText(this, "ȷ����Ҫ�ĺ˶�����������ȫ��ɨ��", Toast.LENGTH_LONG).show();
				}
				else{
					super.progressDialog.setMessage("���ύ��...");
					super.showProDia();
					params[0] = useid;
					params[1] = usename;
					params[2] = resourceid;
					params[3] = resourcename;
					params[4] = moname;
					params[5] = editzoweesn.getText().toString().trim();
					params[6] = editzhengjisn.getText().toString().trim();
					params[7] = editdianyuansn.getText().toString().trim();
					haierzhengjisnbangdingModel.bangding(new  Task(this,TaskType.haierzhengjisnbangding,params));
				}
				
				
		}

		break;
		}
		return false;
	}

	private  void whorequestFocus(String exception){
		if(exception.equalsIgnoreCase("ZoweeSN")){
			editzoweesn.requestFocus();
		}
		if(exception.equalsIgnoreCase("ProductSN")){
			editzhengjisn.requestFocus();
		}
		if(exception.equalsIgnoreCase("PowerSN")){
			editdianyuansn.requestFocus();
		}
		
	}

}