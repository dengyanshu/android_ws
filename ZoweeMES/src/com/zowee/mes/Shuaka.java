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
	
	private ShuakaModel shuakaModel; // 任务处理类
	private static final String TAG = "Shuaka";
	
	private  String[] paras;
	
	private  Dialog  d;
	
	private  boolean  appisok=true;
	private  boolean  carmachine_check=true;
	private  SerialPort  serialport_1;
    // ============== SharedPreferences default setting ======================
 	public static String SCAN_COMMAND = "1B31"; // 扫描枪扫描指令
 	public static String SCAN_TIMEOUT_COMMAND = "Noread"; // 扫描中断指令
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
		//获取今日排产线别
		shuakaModel.getworkcentername(new  Task(this,TaskType.shuaka_getworkcenter," "));
	}
   
	
	public void onBackPressed() {
		killMainProcess();
		
		
	}

	// 回退键关掉这个activity
	private void killMainProcess() {
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.login_logo)
				.setTitle("确定退出程序?")
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
		super.TASKTYPE = TaskType.biaoqianhedui; // 后台服务静态整形常量
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
					if(shanghuoxia.equals("上岗"))
					   paras[5]="1";
					else if(shanghuoxia.equals("下岗")) {
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
				Toast.makeText(this, "注册串口失败", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, "串口注册这里有执行！得到输入流失败", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(this, "串口注册出现异常", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
			Toast.makeText(this, "串口注册出现异常", Toast.LENGTH_SHORT).show();
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			Toast.makeText(this, "串口注册出现异常", Toast.LENGTH_SHORT).show();
		}
		return serialPort;
	}
   
   
 //串口监听
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
 					//logSysDetails("串口1读到的数据是："+msg, "串口");
 					Message.obtain(mHandler, 1, readMessage()).sendToTarget();
 					//Toast.makeText(SmtTP.this, who+"读到的数据是"+msg, Toast.LENGTH_LONG).show();
 				} 
 			
			
 			break;
 			}
 		}
 	 
 		private void clearBuff() {
 			// 接收消息以清空缓存
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
			//logSysDetails(portName+"写的数据是："+out, "数据");
			dataOutput.flush();
			//Log.i(TAG,portName+"写的数据是:"+out );
			//Toast.makeText(this, portName+"串口写数据成功！写的数据是"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			//,,,
			Toast.makeText(this, "串口写数据失败这里有执行！", Toast.LENGTH_SHORT).show();
		}
	}
 	
 

	/**
	 * 十六进制字符串转Byte[]
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
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		 List<HashMap<String, String>>  listgetdata;
		/**
		 * 具体根据提交服务器的返回的结果进行UI界面的更新！！
		 * 
		 * */
		 switch (task.getTaskType()) {
		 case TaskType.shuaka_getworkcenter:
			 super.closeProDia();
			 if (null != task.getTaskResult()) {
				 listgetdata = (List<HashMap<String, String>>) task.getTaskResult();
				 List<String>  workcenterstrings=new  ArrayList<String>();
				 workcenterstrings.add("请选择");
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
					 Toast.makeText(this, "人力查询结果LIST集合SIZE为0", 1).show();
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
					 standard_zhijie=res.get("标准直接人力");
					  standard_jianjie=res.get("标准间接人力");
					  shiji_zhijie=res.get("实际直接人力");
					 shiji_jianjie=res.get("实际间接人力");
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
  //			LinearLayout layout=(LinearLayout) getLayoutInflater().inflate(R.layout.renlidialog, null);  不能这样使用
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
				 monames.add("请选择");
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
				 zhichengs.add("请选择");
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

				 Log.d(TAG, "task的结果数据是：" + getdata);
				 if (getdata.get("Error") == null) {
					 if(Integer.parseInt(value)==0){
						 String msg=getdata.get("I_ReturnMessage");
						 try{
							 HashMap<String, String> map=new HashMap<String, String>();
							 map.put("num", res.size()+1+"");
							 map.put("name", msg.substring(0, msg.indexOf("于")));
							 map.put("moname", msg.substring(msg.indexOf("工单")+2, msg.indexOf("的制程")));
							 map.put("zhicheng", msg.substring(msg.indexOf("制程")+2, msg.indexOf("岗")-2));
							 map.put("shangorxia", msg.substring(msg.indexOf("岗")-1, msg.indexOf("成功")+2));
							 res.add(map);
							 adapter.notifyDataSetChanged();
							 
						
							 SoundEffectPlayService
							 .playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
							 d=new MydismissDialog(Shuaka.this,R.style.MyDialog);
							 d.setContentView(R.layout.mydialog);
							 TextView tv=(TextView) d.findViewById(R.id.mydialog_message);
							 tv.setTextColor(getResources().getColor(R.color.green));
							 tv.setTextSize(60);
							 String name=msg.substring(0, msg.indexOf("于"));
							 String gang=msg.substring(msg.indexOf("岗")-1, msg.indexOf("成功")+2);
							 
							 tv.setText("	"+name+"  "+gang+"!!");
							 d.show();
						 }
						 catch(Exception  e){
							 Toast.makeText(this, "返回信息无法字符串截取", 1000).show();
							 throw new MysubstringException("返回消息 无法按照字符进行截取！");
							
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
					 Toast.makeText(this, "在MES获取信息为空或者解析结果为空，请检查再试!"
							 + getdata.get("Error"), Toast.LENGTH_SHORT).show();
							
				 }
				 closeProDia();
			 } else {
				 Toast.makeText(this, "提交MES失败请检查网络，请检查输入的条码", Toast.LENGTH_SHORT).show();
						
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
			if(!item.contains("选择")){
				paras[0]=item;
				super.progressDialog.setMessage("获取信息中....");
				progressDialog.show();
				//获取卡机
				shuakaModel.getmachinenumber(new Task(this,TaskType.shuaka_getmachinnumber,paras[0]));
				//获取工单
				shuakaModel.getmoname(new Task(this,TaskType.shuaka_getmoname,paras[0]));
				//选取制程为请选择
				 paras[3]="";
				 String[] zhichengs={"请选择"};
				 ArrayAdapter  adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, zhichengs);
				 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 zhichengSpinner.setAdapter(adapter);
			}
		break;
		case  R.id.shuaka_moname:
			 item=arg0.getItemAtPosition(arg2).toString();
			if(!item.contains("选择")){
				paras[2]=item;
				//获取制程
				shuakaModel.getzhicheng(new Task(this,TaskType.shuaka_getzhicheng,new  String[]{paras[0],paras[2]}));
			}
		break;
		case  R.id.shuaka_zhicheng:
			 item=arg0.getItemAtPosition(arg2).toString();
			if(!item.contains("选择")){
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
    
	
	 // 消息处理，更新UI
	 private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String message=msg.obj.toString();
				switch (msg.what) {
				case 1:
					 
					  if(message.contains("@c")){
						// logSysDetails("读刷卡机的内容是！"+message, "指令");
						  Toast.makeText(Shuaka.this, "刷卡机的编号是！"+message, Toast.LENGTH_LONG).show();
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
						  //刷卡信息过来
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
							  tv.setText("   请检查以上线别、工单、制程、上下岗信息是否全部选定！");
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
								  tv.setText("   卡机编号不正确，请确认卡机编号！");
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
		menu.add(0, 1, 0, "设置");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		android.app.AlertDialog.Builder builder2 = new AlertDialog.Builder(
				this);
		LinearLayout  linearlayout= (LinearLayout) getLayoutInflater().inflate(R.layout.passworddialog, null);
		builder2.setView(linearlayout);
		builder2.setTitle("输入密码，不管控卡机编号");
		final EditText password = (EditText) linearlayout
				.findViewById(R.id.Edit_password);
		builder2.setPositiveButton("确认",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (MyApplication.getpasswrod().equals(
								password.getText().toString().trim())) {
							String title=activity_title.getText().toString()+"(不管控卡机编号)";
							title=title.replace("(不管控卡机编号)", "");
							activity_title.setText(title+"(不管控卡机编号)");
							
							carmachine_check=false;
							Toast.makeText(Shuaka.this, "设定成功，不管控卡机编号！", Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(Shuaka.this, "密码错误，请重试！", Toast.LENGTH_SHORT).show();
						}
					}
				});
		builder2.setNegativeButton("取消", null);
		builder2.create().show();
		return super.onOptionsItemSelected(item);
	}
	
	private  final  void  getRenli(String[] para){
		shuakaModel.getRenli(new Task(this,TaskType.shuaka_getrenli,para));
	}

}
