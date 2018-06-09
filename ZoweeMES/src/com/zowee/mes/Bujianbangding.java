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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.AssyqidongModel;
import com.zowee.mes.model.BujianbangdingModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//��¥������װ �㲿������� LCD  TP ���  pcbasn ����ͷSN

//[txn_lenovo_PAD_ASS] �ѳ�  ����Ҫ����id�͹�����Ϣ 
//����@LCD NVARCHAR(50),
//@Aftercamera NVARCHAR(50),
//@TP    NVARCHAR(50)
//@PowerSN  NVARCHAR(50),
//@PCBASN NVARCHAR(50)
public class Bujianbangding extends CommonActivity implements
		android.view.View.OnKeyListener,OnClickListener
		 {

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private EditText editMO;
	private TextView textviewmo;
	
	private EditText editMOProduct;
	private EditText editMOdescri;
	private String moid;    //����ѡ�񱣴����
	private String moname;  //��������
    
	
	private  EditText  editlcdsn;
	private  EditText  edittpsn;
	private  EditText  editdianchisn;
	private  EditText  editshexiangtousn;
	private  EditText  editsn;  //������SN

	private EditText editscan;

	private AssyqidongModel assyqidongModel; // ��������
	private  Common4dModel  common4dmodel;
	private   BujianbangdingModel  bujianbangdingModel;
	private static final String TAG = "Bujianbangding";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_liulou_bujianbangding);
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
								stopService(new Intent(Bujianbangding.this,
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

		assyqidongModel = new AssyqidongModel();
		common4dmodel=new  Common4dModel();
		bujianbangdingModel=new BujianbangdingModel();
		
		editMO = (EditText) findViewById(R.id.assyqidong_moedit);
		textviewmo=(TextView) findViewById(R.id.assyqidong_motextview);
		editMOdescri = (EditText) findViewById(R.id.assyqidong_modescri);
		editMOProduct = (EditText) findViewById(R.id.assyqidong_moproduct);

		editlcdsn = (EditText) findViewById(R.id.bujianbangding_lcdsn);
		edittpsn = (EditText) findViewById(R.id.bujianbangding_tpsn);
		editdianchisn = (EditText) findViewById(R.id.bujianbangding_dianchisn);
		editshexiangtousn = (EditText) findViewById(R.id.bujianbangding_shexiangtousn);
		editsn = (EditText) findViewById(R.id.bujianbangding_snedit);
		
		editscan = (EditText) findViewById(R.id.bujianbangding_editscan);
		editscan.setFocusable(false);
		
		editsn.setOnKeyListener(this);
		textviewmo.setOnClickListener(this);
		editMO.setOnKeyListener(this);
		
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
							+ " ],��ԴID: [ " + resourceid + " ],�û�ID: [ " + useid + " ]!!";
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
			
		case TaskType.liulouassyqidonggetmobymoname:
			super.closeProDia();
			String lotsn = editMO.getText().toString().toUpperCase().trim();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {

					
					moname = getdata.get("MOName");
					String productdescri = getdata.get("ProductDescription");
					String material = getdata.get("ProductName");
					moid = getdata.get("MOId");
					

					editMO.setText(moname);
					editMOdescri.setText(productdescri);
					editMOProduct.setText(material);
					editMO.setEnabled(false);
					editlcdsn.requestFocus();
					String scantext = "ͨ����[" + lotsn + "]�ɹ��Ļ�ù���:"
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
		
		
			
			
		case TaskType.liuloubujianbangding:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				String exception=getdata.get("I_ExceptionFieldName");
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						whorequestFocus(exception);
						editlcdsn.setText("");
						edittpsn.setText("");
						editdianchisn.setText("");
						editshexiangtousn.setText("");
						editsn.setText("");
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						whorequestFocus(exception);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}
//					editsn.requestFocus();
//					editsn.setText("");

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

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onKey(View v, int keycode, KeyEvent event) {
		switch (v.getId()) {
		 case R.id.assyqidong_moedit:
				String param = editMO.getText().toString().toUpperCase().trim();
				if (KeyEvent.KEYCODE_ENTER == keycode
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
				   if(param.length()<8){
					   Toast.makeText(this, "���κų��Ȳ���", Toast.LENGTH_SHORT).show();
					   
				   }
				   else{
					    super.progressDialog.setMessage("�������ݿ��ȡ����");
						super.showProDia();
						assyqidongModel.getmobymoname(new Task(this,TaskType.liulouassyqidonggetmobymoname,param));
				   }
				}
		break;
		
		case R.id.bujianbangding_snedit:
			String[] params=new String[9];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String param2 = editsn.getText().toString().toUpperCase().trim();
			   if(param2.length()<8){
				   Toast.makeText(this, "���κų��Ȳ���", Toast.LENGTH_SHORT).show();
				   return  false;
			   }
			   if(moname==null||moid==null){
				   Toast.makeText(this, "���Ȼ�ȡ������Ϣ��", Toast.LENGTH_SHORT).show();
			   }
			   else{
				    super.progressDialog.setMessage("�����ύ��MES....");
					super.showProDia();
					params[0] = useid;
					params[1] = usename;
					params[2] = resourceid;
					params[3] = resourcename;
					params[4] = param2;
					params[5] = editlcdsn.getText().toString().trim();
					params[6] = edittpsn.getText().toString().trim();
					params[7] = editdianchisn.getText().toString().trim();
					params[8] = editshexiangtousn.getText().toString().trim();
					bujianbangdingModel.bujianbangding(new Task(this,TaskType.liuloubujianbangding,params));
			   }
			}
		break;
		}
		return false;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.assyqidong_motextview:
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
	
	private  void whorequestFocus(String exception){
		if(exception.equalsIgnoreCase("LCD")){
			editlcdsn.requestFocus();
		}
		else if(exception.equalsIgnoreCase("Aftercamera")){
			editshexiangtousn.requestFocus();
		}
		else	if(exception.equalsIgnoreCase("PowerSN")){
			editdianchisn.requestFocus();
		}
		else	if(exception.equalsIgnoreCase("TP")){
			edittpsn.requestFocus();
		}
		else	if(exception.equalsIgnoreCase("PCBASN")){
			editlcdsn.requestFocus();
			editsn.requestFocus();
			
		}
		else{
			editlcdsn.requestFocus();
		}
		
	}

}
