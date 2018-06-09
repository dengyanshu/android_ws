package com.zowee.mes;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TooManyListenersException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.dialogutil.MyScrollDialog;
import com.zowee.mes.dialogutil.MydismissDialog;
import com.zowee.mes.exceptions.MysubstringException;
import com.zowee.mes.interfaces.UseSerialActivityinInterface;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.ShuakaModel;
import com.zowee.mes.model.SmttpforCommonModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.utils.SerialportUtil;

/**
 * ZOWEE-ͨ�ô���ģʽ
 * ����1��ײ��������
 * ����2 A�죨������ɨ��ͷ��
 * ����3 B�죨������ɨ��ͷ��
 * ������ 9600
 * */
public final class SmtTPforCommon extends CommonActivity implements
		UseSerialActivityinInterface
		 {
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString().trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
	private  TextView  mo_tv;
	private EditText mo_edit;
	private EditText moprod_edit;
	private EditText modes_edit;
	
	private Spinner tp_type_sp;
	private Spinner ispure_sp;
	private Spinner raila_sp;
	private Spinner railb_sp;
	
	
	private  Button  okbutton;
	
	private EditText editscan;
	
	private static final String TAG = "SmtTPforCommon";
	
	
	private  SmttpforCommonModel  smttpforCommonModel;
	private  Common4dModel  common4dModel;
	
	private  String moid;
	
	private  String  tptype_str;
	private  String  ispure_str;
	private  String  railaside_str;
	private  String  railbside_str;
	
	private  SerialPort  sp1;
	private  SerialPort  sp2;
	private  SerialPort  sp3;
	
	
	private  int AreScanTimes=1;
	private  int BreScanTimes=1;
	
	private  boolean  isappok=false;
    // ============== SharedPreferences default setting ======================
 	public static String SCAN_COMMAND = "1B31"; // ɨ��ǹɨ��ָ��
 	public static String SCAN_TIMEOUT_COMMAND = "Noread"; // ɨ���ж�ָ��
 	public static int SCAN_RESCAN = 3;
 	public static String OWNER = "system";
 	// ============== SharedPreferences default setting end ==================
   
 	
 	private  Handler  handler=new Handler(){
		public void handleMessage(Message msg) {
			String  info=msg.obj.toString();
			switch (msg.what) {
			case 1:
				 Toast.makeText(SmtTPforCommon.this, info, 1000).show();
				if(isappok){
					if(info.contains("1")){
						SerialportUtil.startScan(sp2);
					}
					else if(info.contains("2")){
						SerialportUtil.startScan(sp3);
					}
				}
				else
				{
					SerialportUtil.clearBuff(sp1);
					
				}
				//Toast.makeText(SmtTPforCommon.this, info, Toast.LENGTH_SHORT).show();
			break;
			
			case 2:
				 Toast.makeText(SmtTPforCommon.this, info, 1000).show();
				if(isappok){
					 Toast.makeText(SmtTPforCommon.this, "2kou appok"+info, 1000).show();
					if(info.contains(SCAN_TIMEOUT_COMMAND)){
						if (AreScanTimes < SCAN_RESCAN) {
							logSysDetails( "A��� " + AreScanTimes + " ������ɨ��","��ȷ");
							SerialportUtil.startScan(sp2);
							AreScanTimes++;
						} else {
							logSysDetails("A���3��ɨ��ʧ��", "��ȷ");
							SystemClock.sleep(50);
							AreScanTimes = 1;
			
						}
					}
					else{//ɨ����������д���
						tomes("RailA",info);
					}
				}
				else{
					Toast.makeText(SmtTPforCommon.this, "2kou appnotok"+info, 1000).show();
					if(!info.equalsIgnoreCase(SCAN_TIMEOUT_COMMAND))
					getMoinfo(info);
				}
				
				break;
				
			case 3:
	            if(isappok){
	            	if(info.contains(SCAN_TIMEOUT_COMMAND)){
						if (BreScanTimes < SCAN_RESCAN) {
							logSysDetails( "B��� " + AreScanTimes + " ������ɨ��","��ȷ");
							SerialportUtil.startScan(sp3);
							BreScanTimes++;
						} else {
							logSysDetails("B���3��ɨ��ʧ��", "��ȷ");
							SystemClock.sleep(50);
							BreScanTimes = 1;
						}
					}
					else{//ɨ����������д���
						tomes("RailB",info);
					}
	            	
	            	
				}
				else{
					if(!info.equalsIgnoreCase(SCAN_TIMEOUT_COMMAND))
					   getMoinfo(info);
				}
				break;

			default:
				break;
			}
			
			
		}
	};
	
	
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_smttp_all);
		registerSerial();
	    try {
			SerialportUtil.addEvent(sp1,1, handler);
			SerialportUtil.addEvent(sp2,2, handler);
			SerialportUtil.addEvent(sp3,3, handler);
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		init();
	}

	protected void onResume() {
		super.onResume();
		
		
	    common4dModel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));
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
								stopService(new Intent(SmtTPforCommon.this,
										BackgroundService.class));
								SerialportUtil.closeSerialPort(sp1);
								SerialportUtil.closeSerialPort(sp2);
								SerialportUtil.closeSerialPort(sp3);
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
	
	
	
	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.biaoqianhedui; // ��̨����̬���γ���
		super.init();
		
		
		smttpforCommonModel=new SmttpforCommonModel();
		common4dModel=new Common4dModel();
		
		mo_tv=(TextView) findViewById(R.id.smttp_common_mo_tv);
		mo_edit=(EditText) findViewById(R.id.smttp_common_mo_edit);
		moprod_edit=(EditText) findViewById(R.id.smttp_common_moprod_edit);
		modes_edit=(EditText) findViewById(R.id.smttp_common_modescriptinon_edit);
		
	   tp_type_sp=(Spinner) findViewById(R.id.smttp_common_type_sp);
	   tp_type_sp.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				tptype_str=arg0.getItemAtPosition(arg2).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	   });
	  
	   ispure_sp=(Spinner) findViewById(R.id.smttp_common_ispure_sp);
	   ispure_sp.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ispure_str=arg0.getItemAtPosition(arg2).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	   });
	   raila_sp=(Spinner) findViewById(R.id.smttp_common_raila_side_sp);
	   raila_sp.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				railaside_str=arg0.getItemAtPosition(arg2).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	   });
	   railb_sp=(Spinner) findViewById(R.id.smttp_common_railb_side_sp);
	   railb_sp.setOnItemSelectedListener(new  OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				railbside_str=arg0.getItemAtPosition(arg2).toString();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	   });
		
	   okbutton=(Button) findViewById(R.id.smttp_common_btn);
	   okbutton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 isappok=true;
			 okbutton.setText("--------Ŀǰ���Զ���Ƭģʽ����--------");
			 okbutton.setTextColor(getResources().getColor(R.color.green));
			 tp_type_sp.setEnabled(false);
			 ispure_sp.setEnabled(false);
			 raila_sp.setEnabled(false);
			 railb_sp.setEnabled(false);
		}
	 });
		
	   editscan = (EditText) findViewById(R.id.smttp_common_editscan);
	   editscan.setFocusable(false); 
		 
	}
    
	private void ClearshowInfo() {
		if (editscan.getLineCount() > 20)
			editscan.setText("");
	}
	
	/*
	 * ˢ��UI����
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		 List<HashMap<String, String>>  listgetdata;
		/**
		 * ��������ύ�������ķ��صĽ������UI����ĸ��£���
		 * 
		 * */
		switch (task.getTaskType()) {
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
		
		
		
        case TaskType.smttpcommon_getmo:
		super.closeProDia();
		//String lotsn = mo_edit.getText().toString().toUpperCase().trim();
		if (null != task.getTaskResult()) {
			getdata = (HashMap<String, String>) task.getTaskResult();
			Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
			if (getdata.get("Error") == null) {
				
				String moname = getdata.get("MOName");
				String productdescri = getdata.get("ProductDescription");
				String material = getdata.get("ProductName");
				moid = getdata.get("MOId");
				
				mo_edit.setText(moname);
				moprod_edit.setText(material);
				modes_edit.setText(productdescri);
				mo_edit.setEnabled(false);
				
				clearspinfo();
				String scantext = "ͨ������ɹ��Ļ�ù���:"
						+ moname + ",����id:"+moid+",��Ʒ��Ϣ:" + productdescri + ",��Ʒ�Ϻţ�"
						+ material + "!";
				logSysDetails(scantext, "�ɹ�");
				SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.PASS_MUSIC);

			} else {
				
			}
			closeProDia();
		} else {
			
		}

		break;
		
		
		
        case TaskType.smttpcommon_tp_raila:
			super.closeProDia();
			ClearshowInfo();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "��Ƭ�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						SerialportUtil.dataOutput(sp1, "2\r\n");
						SystemClock.sleep(150);
					
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						
						
						
						if(getdata.get("I_ReturnMessage").contains("���㵥λ����"))
						{
							
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("����©ɨ�裬��ע�⣡");
							builder.setMessage("    ��˶�������Ϣ��"+scantext);
							builder.setPositiveButton("ȷ��",null);
							builder.setNegativeButton("ȡ��", null);
							builder.create().show();
						}
							
						SystemClock.sleep(50);
					}

				} else {
					logSysDetails(
							"��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
									+ getdata.get("Error"), "�ɹ�");
				}
			} else {
				logSysDetails("�ύMESʧ���������磬�������������", "�ɹ�");
			}
			break;
			
			
        case TaskType.smttpcommon_tp_railb:
			super.closeProDia();
			ClearshowInfo();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				String value=getdata.get("I_ReturnValue");
				Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "��Ƭ�ɹ���"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
						SerialportUtil.dataOutput(sp1, "7\r\n");
						SystemClock.sleep(150);
					
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						String scantext = "ʧ�ܣ�"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "�ɹ�");
						
						if(getdata.get("I_ReturnMessage").contains("���㵥λ����"))
						{
							
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle("����©ɨ�裬��ע�⣡");
							builder.setMessage("    ��˶�������Ϣ��"+scantext);
							builder.setPositiveButton("ȷ��",null);
							builder.setNegativeButton("ȡ��", null);
							builder.create().show();
						}
							
						SystemClock.sleep(50);
					}

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
	public void registerSerial() {
		// TODO Auto-generated method stub
		try {
			sp1=SerialportUtil.registSerialPort("/dev/ttyS1", this);
			sp2=SerialportUtil.registSerialPort("/dev/ttyS2", this);
			sp3=SerialportUtil.registSerialPort("/dev/ttyS3", this);
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(SmtTPforCommon.this, "����ע������쳣��", Toast.LENGTH_SHORT).show();
		}
	}
	
	private  void  getMoinfo(String lotsn){
		progressDialog.setMessage("��ȡ������Ϣ��....");
		progressDialog.show();
		common4dModel.getMObysn(new  Task(this,TaskType.smttpcommon_getmo,lotsn));
	}
	
	
	private  void  clearspinfo(){
		 tp_type_sp.setSelection(0);
		 raila_sp.setSelection(0);
		 railb_sp.setSelection(0);
		 ispure_sp.setSelection(0);
	}
	
	private  void  tomes(String rail,String lotsn){
		int   tasktype = 0;
		String[] paras=new String[10];
		paras[0]=moid;
		paras[1]=tptype_str;
		paras[2]=ispure_str;
		paras[3]=rail;
		if(rail.equalsIgnoreCase("RailA")){
			paras[4]=railaside_str;
			tasktype=TaskType.smttpcommon_tp_raila;
		}
		if(rail.equalsIgnoreCase("RailB")){
			paras[4]=railbside_str;
			tasktype=TaskType.smttpcommon_tp_railb;
		}
		paras[5]=resourceid;
		paras[6]=resourcename;
		paras[7]=useid;
		paras[8]=usename;
		paras[9]=lotsn;
		
		if(moid==null||moid.equals("")||tptype_str.contains("ѡ��")){
		   logSysDetails("������Ϣ����Ƭģʽ��A���B����Ƭ�治��Ϊ��", "ʧ��");
		   return;
		}
		
		if(rail.equalsIgnoreCase("RailA")&&railaside_str.contains("ѡ��")){
			 logSysDetails("A�����Ƭ,����ѡ��Ҫ��Ƭ��A��B", "ʧ��");
			  return;
		}
		if(rail.equalsIgnoreCase("RailB")&&railbside_str.contains("ѡ��")){
			 logSysDetails("B�����Ƭ,����ѡ��Ҫ��Ƭ��A��B", "ʧ��");
			  return;
		}
		
		progressDialog.setMessage(rail+"��Ƭ�ύ��....");
		progressDialog.show();
		smttpforCommonModel.tp(new  Task(this,tasktype,paras));
		
	}

}
