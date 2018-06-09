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
import com.zowee.mes.model.AnxuzhuangxiangModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.LenovotocarModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
//����ͻ�Ҫ�� SMT�������������SMT�����ϵ� СоƬ ��ɨ��ǹɨ������������Զ����������豸����������
public class Lenovotocar extends CommonActivity implements
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

	private EditText editcarsn;
	private EditText editcarnum;
	private EditText editpcbasn;
	private EditText editxinpiansn;
	private  EditText  editxinpiannum;

	
	private Button okbutton;
	private Button clearbutton;
	
	
	
	private  TextView pcbatextview;
	private  TextView xinpiantextview;

	private EditText editscan;

	private  int  numononepcba=3;//һ��������ӵ�е�оƬSN��Ŀ
	private  String  str="";//�ύ�����ݿ���ַ�������ʽΪ  pcbasn,xinsn1,xinsn2,xinsn3|pcbasn,xinsn4,xinsn5,xinsn6|��
	private  int num=1  ;//������ оƬ��Ŀ������ʼΪ1

	private LenovotocarModel lenovotocarModel; // ��������
	private  Common4dModel  common4dmodel;
	private static final String TAG = "Lenovotocar";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lenovosmt_tocar);
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
								stopService(new Intent(Lenovotocar.this,
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

		lenovotocarModel = new LenovotocarModel();
		common4dmodel=new  Common4dModel();
		
		editMO = (EditText) findViewById(R.id.lenovotocar_moedit);
		tvmo=(TextView) findViewById(R.id.lenovotocar_motextview);
		editMOdescri = (EditText) findViewById(R.id.lenovotocar_modescri);
		editMOProduct = (EditText) findViewById(R.id.lenovotocar_moproduct);
		editMO.requestFocus();

		editcarsn = (EditText) findViewById(R.id.lenovotocar_caredit);
		editcarnum= (EditText) findViewById(R.id.lenovotocar_incarnumedit);
		editcarnum.setFocusable(false);
		editcarnum.setEnabled(false);
		editpcbasn = (EditText) findViewById(R.id.lenovotocar_pcbaedit);
		editxinpiansn = (EditText) findViewById(R.id.lenovotocar_xinpianedit);
		editxinpiannum= (EditText) findViewById(R.id.lenovotocar_xinpiannumedit);
		editxinpiannum.setFocusable(false);
		editxinpiannum.setEnabled(false);
		

		okbutton = (Button) findViewById(R.id.lenovotocar_okbutton);
		okbutton.setFocusable(false);
		clearbutton = (Button) findViewById(R.id.lenovotocar_clearbutton);
		clearbutton.setFocusable(false);
		
		pcbatextview=(TextView) findViewById(R.id.lenovotocar_pcbatextview);
		xinpiantextview=(TextView) findViewById(R.id.lenovotocar_xinpiantextview);

		editscan = (EditText) findViewById(R.id.lenovotocar_editscan);
		editscan.setFocusable(false);
		
		okbutton.setOnClickListener(this);
		tvmo.setOnClickListener(this);
        
		editMO.setOnKeyListener(this);
		editpcbasn.setOnKeyListener(this);
		editxinpiansn.setOnKeyListener(this);
		
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
				logSysDetails( "��MES��ȡ��Դid��Ϣʧ�ܣ�������������Դ�����Ƿ���ȷ!", "�ɹ�");
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
					editcarsn.requestFocus();
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
			
		case TaskType.lenovotocar:
			super.closeProDia();
			String exception="";
			str="";
			numononepcba=3;
			num=1;
			editxinpiannum.setText("0");
			pcbatextview.setText("");
			xinpiantextview.setText("");
			if (null != task.getTaskResult()) {
				editpcbasn.setText("");
				editxinpiansn.setText("");
				getdata = (HashMap<String, String>) task.getTaskResult();
				if(getdata.get("CountCar")!=null){
					 String inthecarnum=getdata.get("CountCar");
					 editcarnum.setText(inthecarnum);
				}
				if(getdata.get("I_ExceptionFieldName")!=null){
					 exception=getdata.get("I_ExceptionFieldName");
				}
				else{
					exception="LotSN";
				}
					
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
			
		case TaskType.lenovotocartoclosecar:
			super.closeProDia();
			String exception1="";
			editcarsn.setText("");
			if (null != task.getTaskResult()) {
				editcarsn.setText("");
				editcarsn.requestFocus();
				getdata = (HashMap<String, String>) task.getTaskResult();
				if(getdata.get("I_ExceptionFieldName")!=null){
					 exception1=getdata.get("I_ExceptionFieldName");
				}
				else{
					exception1="LotSN";
				}
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						whorequestFocus(exception1);
						String scantext = "�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);
					}
					else{
						whorequestFocus(exception1);
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
		case R.id.lenovotocar_motextview:
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
			
		case R.id.lenovotocar_okbutton:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("ǿ�Ƴ���");
			builder2.setIcon(R.drawable.app);
			builder2.setMessage("�Ƿ���Ҫ���¸������ţ�");
			builder2.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							progressDialog.setMessage("ǿ��װ���ύ��");
							showProDia();
							String[]  params=new String[9];
							params[0] = useid;
							params[1] = usename;
							params[2] = resourceid;
							params[3] = resourcename;
							params[4] = "";
							params[5] = "";
							params[6] = editcarsn.getText().toString().trim();
							params[7] ="+SubmitCar+";
							params[8] ="3";
							lenovotocarModel.lenovotocar(new  Task(Lenovotocar.this,TaskType.lenovotocartoclosecar,params));
						}
					});
			builder2.setNegativeButton("ȡ��", null);
			builder2.create().show();
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
		case R.id.lenovotocar_moedit:
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

		case R.id.lenovotocar_pcbaedit:
			
			if (KeyEvent.KEYCODE_ENTER == keycode&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String pcba=editpcbasn.getText().toString().toUpperCase().trim();
				if(moname==null||editcarsn.getText().toString().isEmpty()){
					Toast.makeText(this, "�����ͳ��Ų���Ϊ�գ�", Toast.LENGTH_LONG).show();
				    return  false;
				}
				else if(pcba.equalsIgnoreCase("+submit+")){
					super.progressDialog.setMessage("װ���ύ��");
					super.showProDia();
					String[]  params=new String[9];
					params[0] = useid;
					params[1] = usename;
					params[2] = resourceid;
					params[3] = resourcename;
					params[4] = moid;
					params[5] = moname;
					params[6] = editcarsn.getText().toString().trim();
					params[7] =str;
					params[8] =numononepcba+"";
					lenovotocarModel.lenovotocar(new  Task(this,TaskType.lenovotocar,params));
				}
				else{
					if(!(str.contains(pcba))){
						str=str+pcba+",";
						editxinpiansn.requestFocus();
						logSysDetails("�ɹ�ɨ�赽�������롾"+pcba+"��","�ɹ�");
						pcbatextview.setText(pcba);
						xinpiantextview.setText("");
						editxinpiannum.setText("0");
					}
					
					else{
						logSysDetails("���������ظ�ɨ��","�ɹ�");
						//editpcbasn.requestFocus();
						editcarsn.requestFocus();
						return  false;
					}
					
					
				}
			}
		break;
			
        case R.id.lenovotocar_xinpianedit:
			if (KeyEvent.KEYCODE_ENTER == keycode
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				String xinpinsn=editxinpiansn.getText().toString().toUpperCase().trim();
				if(moname==null||editcarsn.getText().toString().isEmpty()){
					Toast.makeText(this, "�����ͳ��Ų���Ϊ�գ�", Toast.LENGTH_LONG).show();
				    return  false;
				}
				else if(xinpinsn.equalsIgnoreCase("+submit+")){
					super.progressDialog.setMessage("װ���ύ��");
					super.showProDia();
					String[]  params=new String[9];
					params[0] = useid;
					params[1] = usename;
					params[2] = resourceid;
					params[3] = resourcename;
					params[4] = moid;
					params[5] = moname;
					params[6] = editcarsn.getText().toString().trim();
					params[7] =str;
					params[8] =numononepcba+"";
					lenovotocarModel.lenovotocar(new  Task(this,TaskType.lenovotocar,params));
				}
				else{
					if(!str.contains(xinpinsn)&&num!=numononepcba){
						logSysDetails("�ɹ�ɨ��оƬ���롾"+num+"��"+xinpinsn,"�ɹ�");
						editxinpiannum.setText(num+"");
						xinpiantextview.append(xinpinsn+"\n");
						num++;
						str=str+xinpinsn+",";
						editxinpiansn.requestFocus();
						editxinpiansn.setText("");
						return  false;
						
					}
					else  if(!str.contains(xinpinsn)&&num==numononepcba){
						
						logSysDetails("�ɹ�ɨ��оƬ���롾"+num+"��"+xinpinsn,"�ɹ�");
						editxinpiannum.setText(num+"");
						xinpiantextview.append(xinpinsn+"\n");
						str=str+xinpinsn+"|";
						editcarsn.requestFocus();
						editxinpiansn.setText("");
						num=1;
						return  false;
					}
					else{
						logSysDetails("оƬ�����ظ�ɨ��","�ɹ�");
						editxinpiansn.requestFocus();
						editxinpiansn.setText("");
					}
					
				}
			}
			break;
		}
		return false;
	}

	private  void whorequestFocus(String exception){
		if(exception.equalsIgnoreCase("LotSN")){
			editpcbasn.requestFocus();
		}
		if(exception.equalsIgnoreCase("CarInfo")){
			editcarsn.requestFocus();
		}
		if(exception.equalsIgnoreCase("SNScan")){
			editpcbasn.requestFocus();
		}
		
		
	}

}
