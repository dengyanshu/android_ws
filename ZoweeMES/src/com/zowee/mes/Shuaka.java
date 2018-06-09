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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TooManyListenersException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
import com.zowee.mes.dialogutil.MydismissDialog;
import com.zowee.mes.exceptions.MysubstringException;
import com.zowee.mes.model.ShuakaModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;


public final class Shuaka extends CommonActivity implements
		OnItemSelectedListener
		 {
	private  CountDownTimer countdowntimer;
	private  Dialog  renli_dialog;
	private  Thread  thread;
	private  boolean  isactive_renlidialog=false;
	private  EditText  renli_stand_zhijieedit;
	private  EditText  renli_stand_jianjieedit;
	private  EditText  renli_fact_zhijieedit;
	private  EditText  renli_fact_jianjieedit;
	private  EditText  renli_chayi_zhijieedit;
	private  EditText  renli_chayi_jianjieedit;
	private  TextView  title;
	private  Handler  handler=new Handler();
	 String standard_zhijie;
	 String standard_jianjie;
	 String shiji_zhijie;
	 String shiji_jianjie;
	
	
	
	private  TextView  activity_title;
	private Spinner lineSpinner;
	private EditText machineEdit;
	private Spinner moanmeSpinner;
	private Spinner zhichengSpinner;
	private RadioGroup shifRadiogroup;
	private RadioGroup shangxiabanRadiogroup;
    

	private ListView lv;
	private List<HashMap<String, String>>  res;
	private  SimpleAdapter  adapter;
	
	private ShuakaModel shuakaModel; // ��������
	private static final String TAG = "Shuaka";
	
	private  String[] paras;
	
	private  Dialog  d;
	
	private  boolean  appisok=true;
	private  boolean  carmachine_check=true;
	private  SerialPort  serialport_1;
    // ============== SharedPreferences default setting ======================
 	public static String SCAN_COMMAND = "1B31"; // ɨ��ǹɨ��ָ��
 	public static String SCAN_TIMEOUT_COMMAND = "Noread"; // ɨ���ж�ָ��
 	public static int SCAN_RESCAN = 2;
 	public static String OWNER = "system";
 	// ============== SharedPreferences default setting end ==================

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_shuaka);
		init();
		paras=new  String[]{"","","","","","",""};
	}

	protected void onResume() {
		super.onResume();
		//��ȡ�����Ų��߱�
		shuakaModel.getworkcentername(new  Task(this,TaskType.shuaka_getworkcenter," "));
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
								unRegisterPort();
								stopService(new Intent(Shuaka.this,
										BackgroundService.class));
								
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
	
	
	private void unRegisterPort() {
		
		if (serialport_1 != null) {
			serialport_1.removeEventListener();
			serialport_1.close();
		}
		serialport_1 = null;
	}

	public void init() {
		super.commonActivity = this;
		super.TASKTYPE = TaskType.biaoqianhedui; // ��̨����̬���γ���
		super.init();

		shuakaModel = new ShuakaModel();
		
		activity_title=(TextView) findViewById(R.id.shuaka_activitytitle);
		
		 lineSpinner=(Spinner) findViewById(R.id.shuaka_linename);
		 lineSpinner.setOnItemSelectedListener(this);
//		 lineSpinner.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				shuakaModel.getworkcentername(new  Task(Shuaka.this,TaskType.shuaka_getworkcenter," "));
//			}
//			
//			
//		});
		 
		 machineEdit=(EditText) findViewById(R.id.shuaka_cardmachine);
		 
		 moanmeSpinner=(Spinner) findViewById(R.id.shuaka_moname);
		 moanmeSpinner.setOnItemSelectedListener(this);
		 zhichengSpinner=(Spinner) findViewById(R.id.shuaka_zhicheng);
		 zhichengSpinner.setOnItemSelectedListener(this);
		 
		 shifRadiogroup=(RadioGroup) findViewById(R.id.shuaka_banci_radiogroup);
		 
		 shifRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int id=group.getCheckedRadioButtonId();
				RadioButton  radiobutton=(RadioButton) findViewById(id);
				paras[4]=radiobutton.getText().toString();
			}
		});
		 shangxiabanRadiogroup=(RadioGroup) findViewById(R.id.shuaka_shanghuoxia_radiogroup);
		 shangxiabanRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub
					int id=group.getCheckedRadioButtonId();
					RadioButton  radiobutton=(RadioButton) findViewById(id);
					String  shanghuoxia=radiobutton.getText().toString();
					if(shanghuoxia.equals("�ϸ�"))
					   paras[5]="1";
					else if(shanghuoxia.equals("�¸�")) {
						paras[5]="2";
					}
				}
			});
		
		 lv=(ListView) findViewById(R.id.shuaka_listview);
		 res=new ArrayList<HashMap<String,String>>();
		 adapter=new  SimpleAdapter(this, res, R.layout.activity_shuaka_adapter,new String[]{"num","name","moname","zhicheng","shangorxia"},new  int[]{R.id.shuaka_adapter_1,R.id.shuaka_adapter_2,
				 R.id.shuaka_adapter_3,R.id.shuaka_adapter_4,R.id.shuaka_adapter_5});
		 lv.setAdapter(adapter);
		 
		  try {
				
				serialport_1 = registerPort("/dev/ttyS1");
			} catch (TooManyListenersException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "ע�ᴮ��ʧ��", Toast.LENGTH_SHORT).show();
				finish();
			}
	}
    
	
	private SerialPort registerPort(String tty) throws TooManyListenersException{
		SerialPort serialPort = null;
		InputStream mInputStream = null;
		try {
			Log.i(TAG, "registerPort: tty = " + tty);
			CommPortIdentifier portId = CommPortIdentifier
					.getPortIdentifier(tty);
			serialPort = (SerialPort) portId.open(OWNER, 5000);
			try {
				mInputStream=serialPort.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(this, "����ע��������ִ�У��õ�������ʧ��", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			serialPort.addEventListener(new SerialEventsListener(mInputStream,
					tty));
			serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.notifyOnBreakInterrupt(true);
		} catch (PortInUseException e) {
			Log.e(TAG, "registerPort: " + e.currentOwner);
			Toast.makeText(this, "����ע������쳣", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
			Toast.makeText(this, "����ע������쳣", Toast.LENGTH_SHORT).show();
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			Toast.makeText(this, "����ע������쳣", Toast.LENGTH_SHORT).show();
		}
		return serialPort;
	}
   
   
 //���ڼ���
   private final class SerialEventsListener implements SerialPortEventListener {
 		private InputStream dataInput;
 		private String who;

 		public SerialEventsListener(InputStream dataInput, String tty) {
 			this.who = tty;
 			this.dataInput = dataInput;
 		}
 		@SuppressWarnings("unused")
 		@Override
 		public void serialEvent(SerialPortEvent ev) {
 			int type = ev.getEventType();
 			switch (type) {
 			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
 				break;
 			case SerialPortEvent.BI:
 				break;
 			case SerialPortEvent.DATA_AVAILABLE:
 				if ("/dev/ttyS1".equals(who)) {
 					//"@USCAN:\r\n"or"@DSCAN:\r\n
 					//String msg=readMessage();
 					//logSysDetails("����1�����������ǣ�"+msg, "����");
 					Message.obtain(mHandler, 1, readMessage()).sendToTarget();
 					//Toast.makeText(SmtTP.this, who+"������������"+msg, Toast.LENGTH_LONG).show();
 				} 
 			
			
 			break;
 			}
 		}
 	 
 		private void clearBuff() {
 			// ������Ϣ����ջ���
 			try {
 				if (dataInput.available() > 0) {
 					dataInput.read(new byte[512]);
 				}
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		}

 		public String readMessage() {
 			String sReadBuff = "";
 			int i;
 			byte[] readBuffer = new byte[512];
 			int numBytes;
 			try {
 				while (dataInput.available() > 0) {
 					SystemClock.sleep(50);
 					
 					numBytes = dataInput.read(readBuffer);
// 						for (i = 0; i < numBytes; i++) {
// 							//Log.d(TAG, "serialEvent: byte[" + i + "]"
// 								//	+ (readBuffer[i]));
// 						}
 					
 					String tmpR = new String(readBuffer, 0, numBytes, "UTF-8");
 					sReadBuff += tmpR;
 				}
 				
 					Log.d(TAG, "serialEvent: read data:" + sReadBuff);
 			} catch (IOException e) {
 				Log.e(TAG, "serialEvent: Error Reading from serial port", e);
 			}
 			return sReadBuff;
 	  }
 	}
 	
 	private void dataOutput(SerialPort portName, String out) {
		try {
		   OutputStream	dataOutput = portName.getOutputStream();
			dataOutput.write(out.getBytes("US-ASCII"));
			//logSysDetails(portName+"д�������ǣ�"+out, "����");
			dataOutput.flush();
			//Log.i(TAG,portName+"д��������:"+out );
			//Toast.makeText(this, portName+"����д���ݳɹ���д��������"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			//,,,
			Toast.makeText(this, "����д����ʧ��������ִ�У�", Toast.LENGTH_SHORT).show();
		}
	}
 	
 

	/**
	 * ʮ�������ַ���תByte[]
	 * 
	 * @return
	 */
	public static byte[] HexString2Bytes() {
		byte[] ret = new byte[SCAN_COMMAND.length() / 2];
		byte[] tmp = SCAN_COMMAND.getBytes();
		for (int i = 0; i < tmp.length / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
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
		 case TaskType.shuaka_getworkcenter:
			 super.closeProDia();
			 if (null != task.getTaskResult()) {
				 listgetdata = (List<HashMap<String, String>>) task.getTaskResult();
				 List<String>  workcenterstrings=new  ArrayList<String>();
				 workcenterstrings.add("��ѡ��");
				 for(HashMap<String, String> map:listgetdata){
					 workcenterstrings.add(map.get("WorkcenterName"));
				 }

				 ArrayAdapter  adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, workcenterstrings);
				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 lineSpinner.setAdapter(adapter);
			 } else {

			 }
			 break;
        
		 case TaskType.shuaka_getrenli:
			 super.closeProDia();
			 if (null != task.getTaskResult()) {
				 listgetdata = (List<HashMap<String, String>>) task.getTaskResult();
				 Log.i(TAG,"renli data="+listgetdata);
				 if(listgetdata.size()==0){
					 Toast.makeText(this, "������ѯ���LIST����SIZEΪ0", 1).show();
					 return;
				 }
				 HashMap<String, String>  res=listgetdata.get(0);
                 if(res.isEmpty()){
                	 standard_zhijie="0";
					  standard_jianjie="0";
					  shiji_zhijie="0";
					 shiji_jianjie="0";;
				 }
				 else{
					 standard_zhijie=res.get("��׼ֱ������");
					  standard_jianjie=res.get("��׼�������");
					  shiji_zhijie=res.get("ʵ��ֱ������");
					 shiji_jianjie=res.get("ʵ�ʼ������");
				 }
                 
				 renli_dialog=new Dialog(Shuaka.this,R.style.renliDialog){
					public void onBackPressed() {
						isactive_renlidialog=false;
						renli_dialog.dismiss();
						renli_dialog=null;
					}
				 };
				 Window window=renli_dialog.getWindow();
				window.setWindowAnimations(R.style.renliAnimations);
				WindowManager.LayoutParams lp=	window.getAttributes();
				lp.alpha=0.8f;
				window.setAttributes(lp);
				renli_dialog.setCanceledOnTouchOutside(false);
				renli_dialog.setContentView(R.layout.renlidialog);
				renli_stand_zhijieedit=(EditText) renli_dialog.findViewById(R.id.activity_renlidialog_stand_zhijiejie);
  //			LinearLayout layout=(LinearLayout) getLayoutInflater().inflate(R.layout.renlidialog, null);  ��������ʹ��
				renli_stand_jianjieedit=(EditText)renli_dialog. findViewById(R.id.activity_renlidialog_stand_jianjie);
				renli_fact_zhijieedit=(EditText)renli_dialog. findViewById(R.id.activity_renlidialog_fact_zhijiejie);
				renli_fact_jianjieedit=(EditText)renli_dialog. findViewById(R.id.activity_renlidialog_fact_jianjie);
				renli_chayi_zhijieedit=(EditText)renli_dialog. findViewById(R.id.activity_renlidialog_chayi_zhijie);
				renli_chayi_jianjieedit=(EditText)renli_dialog. findViewById(R.id.activity_renlidialog_chayi_jianjie);
				title=(TextView) renli_dialog.findViewById(R.id.activity_renlidialog_title);
				renli_stand_zhijieedit.setText(standard_zhijie);
				renli_stand_jianjieedit.setText(standard_jianjie);
				renli_fact_zhijieedit.setText(shiji_zhijie);
				renli_fact_jianjieedit.setText(shiji_jianjie);
				float zhijiechayi=Float.parseFloat(standard_zhijie)-Float.parseFloat(shiji_zhijie);
				float jianjiechayi=Float.parseFloat(standard_jianjie)-Float.parseFloat(shiji_jianjie);
				String zhijiechai_str=zhijiechayi+"";
				String jianjiechayi_str=jianjiechayi+"";
				if(zhijiechai_str.length()>=4){
					zhijiechai_str=zhijiechai_str.substring(0, 4);
				}
				if(jianjiechayi_str.length()>=4){
					jianjiechayi_str=jianjiechayi_str.substring(0, 4);
				}
				renli_chayi_zhijieedit.setText(zhijiechai_str);
				renli_chayi_jianjieedit.setText(jianjiechayi_str);
				if(zhijiechayi<0){
					renli_chayi_zhijieedit.setBackgroundColor(Color.RED);
				}
				if(jianjiechayi<0){
					renli_chayi_jianjieedit.setBackgroundColor(Color.RED);
				}
				
				
				
				renli_dialog.show();
				isactive_renlidialog=true;
				thread=new  Thread(){
					public void run() {
						try {
							while(isactive_renlidialog){
								Thread.sleep(2000);
								handler.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										//renli_dialog.dismiss();
										if(renli_dialog!=null){
											Window window=renli_dialog.getWindow();
											WindowManager.LayoutParams lp=	window.getAttributes();
											int  num;
											if(Math.random()>=0.5){
												num=1;
											}
											else
												num=-1;
											lp.x=(int) (Math.random()*300*num);
											lp.y=(int) (Math.random()*300*num);
											window.setAttributes(lp);
											renli_dialog.show();
										}
									}
								});
							}
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				thread.start();
				 

			 } else {

			 }
			 break;	 
			 
		 case TaskType.shuaka_getmachinnumber:
			 if (null != task.getTaskResult()) {
				 getdata = (HashMap<String, String>) task.getTaskResult();
				 if (getdata.get("Error") == null) {
					 paras[1] = getdata.get("name");
					 machineEdit.setText(paras[1]);
					 dataOutput(serialport_1, "@c\n");
				 } else {
					 paras[1] = "";
					 machineEdit.setText(paras[1]);
					 dataOutput(serialport_1, "@c\n");
				 }
			 } else {
				 paras[1] = "";
				 machineEdit.setText(paras[1]);
				 dataOutput(serialport_1, "@c\n");
				 
			 }

			 break;

		 case TaskType.shuaka_getmoname:
			 super.closeProDia();
			 if (null != task.getTaskResult()) {
				 listgetdata = (List<HashMap<String, String>>) task.getTaskResult();
				 List<String>  monames=new  ArrayList<String>();
				 monames.add("��ѡ��");
				 for(HashMap<String, String> map:listgetdata){
					 monames.add(map.get("MOName"));
				 }
				 ArrayAdapter  adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, monames);
				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 moanmeSpinner.setAdapter(adapter);
			 } else {

			 }
			 break;


		 case TaskType.shuaka_getzhicheng:
			 if (null != task.getTaskResult()) {
				 listgetdata = (List<HashMap<String, String>>) task.getTaskResult();
				 List<String>  zhichengs=new  ArrayList<String>();
				 zhichengs.add("��ѡ��");
				 for(HashMap<String, String> map:listgetdata){
					 zhichengs.add(map.get("WorkprocedureFlowNameList"));
				 }
				 ArrayAdapter  adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, zhichengs);
				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 zhichengSpinner.setAdapter(adapter);
			 } else {

			 }
			 break;


		 case TaskType.shuaka_shuaka:
			 super.closeProDia();
			 if (null != task.getTaskResult()) {
				 getdata = (HashMap<String, String>) task.getTaskResult();
				 String value=getdata.get("I_ReturnValue");

				 Log.d(TAG, "task�Ľ�������ǣ�" + getdata);
				 if (getdata.get("Error") == null) {
					 if(Integer.parseInt(value)==0){
						 String msg=getdata.get("I_ReturnMessage");
						 try{
							 HashMap<String, String> map=new HashMap<String, String>();
							 map.put("num", res.size()+1+"");
							 map.put("name", msg.substring(0, msg.indexOf("��")));
							 map.put("moname", msg.substring(msg.indexOf("����")+2, msg.indexOf("���Ƴ�")));
							 map.put("zhicheng", msg.substring(msg.indexOf("�Ƴ�")+2, msg.indexOf("��")-2));
							 map.put("shangorxia", msg.substring(msg.indexOf("��")-1, msg.indexOf("�ɹ�")+2));
							 res.add(map);
							 adapter.notifyDataSetChanged();
							 
						
							 SoundEffectPlayService
							 .playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
							 d=new MydismissDialog(Shuaka.this,R.style.MyDialog);
							 d.setContentView(R.layout.mydialog);
							 TextView tv=(TextView) d.findViewById(R.id.mydialog_message);
							 tv.setTextColor(getResources().getColor(R.color.green));
							 tv.setTextSize(60);
							 String name=msg.substring(0, msg.indexOf("��"));
							 String gang=msg.substring(msg.indexOf("��")-1, msg.indexOf("�ɹ�")+2);
							 
							 tv.setText("	"+name+"  "+gang+"!!");
							 d.show();
						 }
						 catch(Exception  e){
							 Toast.makeText(this, "������Ϣ�޷��ַ�����ȡ", 1000).show();
							 throw new MysubstringException("������Ϣ �޷������ַ����н�ȡ��");
							
						 }
					 }
					 else{
						 SoundEffectPlayService
						 .playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
						 d=new MydismissDialog(Shuaka.this,R.style.MyDialog);
						 d.setContentView(R.layout.mydialog);
						 TextView tv=(TextView) d.findViewById(R.id.mydialog_message);
						 tv.setTextColor(getResources().getColor(R.color.red));
						 tv.setTextSize(30);
						 tv.setText("	"+getdata.get("I_ReturnMessage"));
						 d.show();
					 }
				 } else {
					 Toast.makeText(this, "��MES��ȡ��ϢΪ�ջ��߽������Ϊ�գ���������!"
							 + getdata.get("Error"), Toast.LENGTH_SHORT).show();
							
				 }
				 closeProDia();
			 } else {
				 Toast.makeText(this, "�ύMESʧ���������磬�������������", Toast.LENGTH_SHORT).show();
						
			 }
			 break;
			
		
	  }
	}



	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		String item;
		switch (arg0.getId()) {
		case R.id.shuaka_linename:
			 item=arg0.getItemAtPosition(arg2).toString();
			if(!item.contains("ѡ��")){
				paras[0]=item;
				super.progressDialog.setMessage("��ȡ��Ϣ��....");
				progressDialog.show();
				//��ȡ����
				shuakaModel.getmachinenumber(new Task(this,TaskType.shuaka_getmachinnumber,paras[0]));
				//��ȡ����
				shuakaModel.getmoname(new Task(this,TaskType.shuaka_getmoname,paras[0]));
				//ѡȡ�Ƴ�Ϊ��ѡ��
				 paras[3]="";
				 String[] zhichengs={"��ѡ��"};
				 ArrayAdapter  adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, zhichengs);
				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 zhichengSpinner.setAdapter(adapter);
			}
		break;
		case  R.id.shuaka_moname:
			 item=arg0.getItemAtPosition(arg2).toString();
			if(!item.contains("ѡ��")){
				paras[2]=item;
				//��ȡ�Ƴ�
				shuakaModel.getzhicheng(new Task(this,TaskType.shuaka_getzhicheng,new  String[]{paras[0],paras[2]}));
			}
		break;
		case  R.id.shuaka_zhicheng:
			 item=arg0.getItemAtPosition(arg2).toString();
			if(!item.contains("ѡ��")){
				paras[3]=item;
			}
		break;
		
	
		default:
			break;
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
    
	
	 // ��Ϣ��������UI
	 private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String message=msg.obj.toString();
				switch (msg.what) {
				case 1:
					 
					  if(message.contains("@c")){
						// logSysDetails("��ˢ�����������ǣ�"+message, "ָ��");
						  Toast.makeText(Shuaka.this, "ˢ�����ı���ǣ�"+message, Toast.LENGTH_LONG).show();
						  String cardid_of_line=machineEdit.getText().toString().trim();
						  if(cardid_of_line==null||!message.contains(cardid_of_line)){
							  appisok=false;
							  machineEdit.setTextColor(getResources().getColor(R.color.red));
						  }
						  else{
							   appisok=true;
							   machineEdit.setTextColor(getResources().getColor(R.color.green));
						  }
					  }
					  else{
						  //ˢ����Ϣ����
						  if(renli_dialog!=null&&renli_dialog.isShowing()){
							  isactive_renlidialog=false;
							  renli_dialog.dismiss();
							  renli_dialog=null;
							  
						  }
						  if(countdowntimer==null){
							
						  }
						  else{
							  countdowntimer.cancel(); 
							  countdowntimer=null;
						  }
						  countdowntimer=new CountDownTimer(20*1000, 1000) {
								@Override
								public void onFinish() {
									// TODO Auto-generated method stub
								  Log.i(TAG,"20 SECONDS IS  FINISH!");
								 
								  if(paras[5].equals("")||paras[3].equals(""))
									  Toast.makeText(Shuaka.this, "20 second finish But require parameters wrong", 1).show();
								  else{
									  String line=lineSpinner.getSelectedItem().toString();
									  String moname=moanmeSpinner.getSelectedItem().toString();
									  String zhicheng=zhichengSpinner.getSelectedItem().toString();
									  getRenli(new String[]{line,moname,zhicheng,paras[4]});
									  Log.i(TAG,"INFO="+line+","+moname+","+zhicheng+","+paras[4]);
								  }
								  countdowntimer=null;
								}
								@Override
								public void onTick(long millisUntilFinished) {
									// TODO Auto-generated method stub
									Log.i(TAG,"20 SECONDS IS  start!");
								}
							}.start(); 
						
					      String car=message.substring(2, 16);
						  int  card=Integer.parseInt(car);
						  car=card+"";
						  car=car.replaceAll("0{1,}$", "");
						  paras[6]=car;

						  if(paras[5].equals("")||paras[3].equals("")){
							  d=new MydismissDialog(Shuaka.this,R.style.MyDialog);
							  d.setContentView(R.layout.mydialog);
							  TextView tv=(TextView) d.findViewById(R.id.mydialog_message);
							  tv.setTextColor(getResources().getColor(R.color.red));
							  tv.setTextSize(30);
							  tv.setText("   ���������߱𡢹������Ƴ̡����¸���Ϣ�Ƿ�ȫ��ѡ����");
							  d.show();
							  return;
						  }
						  if(!carmachine_check){
							  shuakaModel.shuaka(new  Task(Shuaka.this,TaskType.shuaka_shuaka,paras));
						  }
						  else{
							  if(appisok){
								  shuakaModel.shuaka(new  Task(Shuaka.this,TaskType.shuaka_shuaka,paras)); 
							  }
							  else{
								  d=new MydismissDialog(Shuaka.this,R.style.MyDialog);
								  d.setContentView(R.layout.mydialog);
								  TextView tv=(TextView) d.findViewById(R.id.mydialog_message);
								  tv.setTextColor(getResources().getColor(R.color.red));
								  tv.setTextSize(30);
								  tv.setText("   ������Ų���ȷ����ȷ�Ͽ�����ţ�");
								  d.show();
								  return;
							  }
						  }
						 

					  }   
				break;
			    }
				super.handleMessage(msg);
			}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 0, "����");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		android.app.AlertDialog.Builder builder2 = new AlertDialog.Builder(
				this);
		LinearLayout  linearlayout= (LinearLayout) getLayoutInflater().inflate(R.layout.passworddialog, null);
		builder2.setView(linearlayout);
		builder2.setTitle("�������룬���ܿؿ������");
		final EditText password = (EditText) linearlayout
				.findViewById(R.id.Edit_password);
		builder2.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (MyApplication.getpasswrod().equals(
								password.getText().toString().trim())) {
							String title=activity_title.getText().toString()+"(���ܿؿ������)";
							title=title.replace("(���ܿؿ������)", "");
							activity_title.setText(title+"(���ܿؿ������)");
							
							carmachine_check=false;
							Toast.makeText(Shuaka.this, "�趨�ɹ������ܿؿ�����ţ�", Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(Shuaka.this, "������������ԣ�", Toast.LENGTH_SHORT).show();
						}
					}
				});
		builder2.setNegativeButton("ȡ��", null);
		builder2.create().show();
		return super.onOptionsItemSelected(item);
	}
	
	private  final  void  getRenli(String[] para){
		shuakaModel.getRenli(new Task(this,TaskType.shuaka_getrenli,para));
	}

}
