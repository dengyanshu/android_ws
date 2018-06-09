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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.AssyqidongModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.LiuloufvmiModel;
import com.zowee.mes.model.LiuloulaohuaModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//��¥�ϻ�ͨ�ó���  �ϻ������ϻ�����
public class Liuloulaohua extends CommonActivity implements
		android.view.View.OnKeyListener, OnClickListener
		 {
    private  String laohuatype;
     //2վ ����2�� ����������ͼ ��
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
    
	
	private  TextView  titletextview;//������ʾ
	
	private EditText editMO;
	private  TextView  tvmo;//�����ȡ����
	private EditText editMOProduct;
	private EditText editMOdescri;
	private String moid; // ����ѡ�񱣴����
	private  String moname;

	private EditText editsn;
	
	private  LinearLayout  layout;
	private  EditText  editpassorfail;
    
	
	private Button okbutton;
	private TextView  resulttextview;

	private EditText editscan;

	
	private AssyqidongModel assyqidongModel; // ��������
	private LiuloulaohuaModel liuloulaohuaModel; // ��������
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Liuloufvmi";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_liulou_laohua);
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
								stopService(new Intent(Liuloulaohua.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.liuloulaohua; // ��̨����̬���γ���
		super.init();
		
		Intent intent=Liuloulaohua.this.getIntent();
		laohuatype=intent.getStringExtra("laohua");

		common4dmodel=new  Common4dModel();
		liuloulaohuaModel=new LiuloulaohuaModel();
		assyqidongModel= new AssyqidongModel();
		
		titletextview=(TextView)findViewById(R.id.liuloulaohua_titletextview);
		
		editMO = (EditText) findViewById(R.id.liuloulaohua_moedit);
		tvmo=(TextView) findViewById(R.id.liuloulaohua_motextview);
		editMOdescri = (EditText) findViewById(R.id.liuloulaohua_modescri);
		editMOProduct = (EditText) findViewById(R.id.liuloulaohua_moproduct);
		editMO.requestFocus();

		editsn = (EditText) findViewById(R.id.liuloulaohua_snedit);
		layout=(LinearLayout) findViewById(R.id.liuloulaohua_layout);
		editpassorfail=(EditText) findViewById(R.id.liuloulaohua_passorfailedit);
	   
	   

		okbutton = (Button) findViewById(R.id.liuloulaohua_button);
		okbutton.setFocusable(false);
		resulttextview=(TextView) findViewById(R.id.liuloulaohua_resulttextview);

		editscan = (EditText) findViewById(R.id.liuloulaohua_editscan);
		editscan.setFocusable(false);
		
		

		tvmo.setOnClickListener(this);
        
		editMO.setOnKeyListener(this);
		editsn.setOnKeyListener(this);
		editpassorfail.setOnKeyListener(this);
		//��������
		if(laohuatype.equals("1")){
			titletextview.setText("�ϻ����");
			okbutton.setText("�ϻ����");
			layout.setVisibility(View.GONE);
		}
		if(laohuatype.equals("2")){
			titletextview.setText("�ϻ�����");
			okbutton.setText("�ϻ�����");
			layout.setVisibility(View.VISIBLE);
		}
		
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
		
		
//		case TaskType.common4dmodelgetmobylotsn:
//			super.closeProDia();
//			String lotsn = editMO.getText().toString().toUpperCase().trim();
//			if (null != task.getTaskResult()) {
//				getdata = (HashMap<String, String>) task.getTaskResult();
//				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
//				if (getdata.get("Error") == null) {
//
//					
//					moname = getdata.get("MOName");
//					String productdescri = getdata.get("ProductDescription");
//					String material = getdata.get("productName");
//					moid = getdata.get("MOId");
//					
//
//					editMO.setText(moname);
//					editMOdescri.setText(productdescri);
//					editMOProduct.setText(material);
//					editMO.setEnabled(false);
//					editsn.requestFocus();
//					String scantext = "ͨ�����κţ�[" + lotsn + "]�ɹ��Ļ�ù���:"
//							+ moname + ",����id:"+moid+",��Ʒ��Ϣ:" + productdescri + ",��Ʒ�Ϻţ�"
//							+ material + "!";
//					logSysDetails(scantext, "�ɹ�");
//					SoundEffectPlayService
//							.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
//
//				} else {
//					logSysDetails(
//							"ͨ�����κţ�[" + lotsn + "]��MES��ȡ������ϢΪ�ջ��߽������Ϊ�գ�����SN!"
//									+ getdata.get("Error"), "�ɹ�");
//				}
//				closeProDia();
//			} else {
//				logSysDetails(lotsn + "��MES��ȡ������Ϣʧ�ܣ��������������", "�ɹ�");
//			}
//
//			break;
			
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
					editsn.requestFocus();
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
			
		case TaskType.liuloulaohua:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						resulttextview.setText("PASS");
						resulttextview.setTextColor(Color.GREEN);
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						resulttextview.setText("FAIL");
						resulttextview.setTextColor(Color.RED);
						String scantext = getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
					}
                    editsn.requestFocus();
                    editsn.setText("");
                    editpassorfail.setText("");
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
		case R.id.liuloulaohua_motextview:
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
		case R.id.liuloulaohua_moedit:
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

		case R.id.liuloulaohua_snedit:
			String[]  params=new String[6];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
					if(laohuatype.equals("1")){
						super.progressDialog.setMessage("�ύMES��...");
						super.showProDia();
						params[0] = useid;
						params[1] = usename;
						params[2] = resourceid;
						params[3] = resourcename;
						
						params[4] = moid;
						params[5] = editsn.getText().toString().trim();
						
						liuloulaohuaModel.liuloulaohuaruku(new  Task(this,TaskType.liuloulaohua,params));
					}
					
			}

			break;
			
		case R.id.liuloulaohua_passorfailedit:
			String[]  params1=new String[7];
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				
				
					super.progressDialog.setMessage("�ύMES��...");
					super.showProDia();
					params1[0] = useid;
					params1[1] = usename;
					params1[2] = resourceid;
					params1[3] = resourcename;
					
					params1[4] = moid;
					params1[5] = editsn.getText().toString().trim();
					params1[6] = editpassorfail.getText().toString().trim();
					
						
						liuloulaohuaModel.liuloulaohuachuku(new  Task(this,TaskType.liuloulaohua,params1));
					
					
			}

			break;
		}
		return false;
	}

//	private  void whorequestFocus(String exception){
//		if(exception.equalsIgnoreCase("BoxSN")){
//			editcaihesn.requestFocus();
//		}
//		if(exception.equalsIgnoreCase("BoxMAC")){
//			editcaihemac.requestFocus();
//		}
//		if(exception.equalsIgnoreCase("ProductSN")){
//			editzhengjisn.requestFocus();
//		}
//		if(exception.equalsIgnoreCase("ProductMAC")){
//			editchanpingmac.requestFocus();
//		}
//	}

}
