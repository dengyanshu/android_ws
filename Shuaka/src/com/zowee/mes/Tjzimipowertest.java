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
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
/**
 * 1、气缸打开1秒后，开始读电流值
 * 2、每个通道读3次，其中有一次通过就自动跳转下一通道，如3次都fail就显示此通道失败并跳转下一通道
 * 3、每次读电流值之间间隔0.5S
 * 4、与单片机通讯串口固定为/dev/ttyS1，波特率为9600  每个指令后面加\n为指令结束符，选择通讯端口1！
 * 5、串口接收监听在开启另一个线程，轮询读数据，读到的数据通过handler发送到主线程处理！
 * 6、测试完成弹起气缸，弹出对话框记录下信息！如果选择重测 就再重测那几个工位！
 * */
@SuppressLint("ShowToast")
public class Tjzimipowertest extends CommonActivity implements  View.OnClickListener {
	
	 private  static final String TAG="Tjzimipowertest";
	 private static final String OWNER = "system";
	 private  SerialPort  serialport_1;
     private  SerialPort  serialport_2;
     private  SerialPort  serialport_3;
     private OutputStream dataOutput;
     
     
     private  float i;//电流值
     private   int  testtimes=1;//测试次数计数
     private  int   linenum=1;//1~16通道通道计数器
     
     private List<Integer>  channel_list;
     private  Iterator<Integer>  channel_iterator;
     
	private   TextView  tv1;
	private   TextView  tv2;
	private   Button  button2;//测试button
	private   Button  button3;//测试button2

	private  RelativeLayout   relayout;
	private  Button  button;
	private  EditText  ed1;
	private  EditText  ed2;
	private   String  imin="1.6";
	private   String  imax="1.8";
	private   boolean  isshow=false; //下面隐藏布局的现在是否可见的标示
	
	private  boolean  isexittest=false;
	private  boolean isdebug=false;
	private  boolean isretest=false;
	private  TextView  debugtv;
	
	private  SharedPreferences  sharedpreferences;
	private  Editor  editor;
	
	private  LinearLayout linearlayout1;
	private  TextView  restv1;
	private  TextView   ceshizhi1;
	private  LinearLayout linearlayout2;
	private  TextView  restv2;
	private  TextView   ceshizhi2;
	private  LinearLayout linearlayout3;
	private  TextView  restv3;
	private  TextView   ceshizhi3;
	private  LinearLayout linearlayout4;
	private  TextView  restv4;
	private  TextView   ceshizhi4;
	private  LinearLayout linearlayout5;
	private  TextView  restv5;
	private  TextView   ceshizhi5;
	private  LinearLayout linearlayout6;
	private  TextView  restv6;
	private  TextView   ceshizhi6;
	private  LinearLayout linearlayout7;
	private  TextView  restv7;
	private  TextView   ceshizhi7;
	private  LinearLayout linearlayout8;
	private  TextView  restv8;
	private  TextView   ceshizhi8;
	private  LinearLayout linearlayout9;
	private  TextView  restv9;
	private  TextView   ceshizhi9;
	private  LinearLayout linearlayout10;
	private  TextView  restv10;
	private  TextView   ceshizhi10;
	private  LinearLayout linearlayout11;
	private  TextView  restv11;
	private  TextView   ceshizhi11;
	private  LinearLayout linearlayout12;
	private  TextView  restv12;
	private  TextView   ceshizhi12;
	private  LinearLayout linearlayout13;
	private  TextView  restv13;
	private  TextView   ceshizhi13;
	private  LinearLayout linearlayout14;
	private  TextView  restv14;
	private  TextView   ceshizhi14;
	private  LinearLayout linearlayout15;
	private  TextView  restv15;
	private  TextView   ceshizhi15;
	private  LinearLayout linearlayout16;
	private  TextView  restv16;
	private  TextView   ceshizhi16;

	private  int[] laylouts={R.id.tjzimipowertest_layout1,R.id.tjzimipowertest_layout2,R.id.tjzimipowertest_layout3,R.id.tjzimipowertest_layout4,
			R.id.tjzimipowertest_layout5,R.id.tjzimipowertest_layout6,R.id.tjzimipowertest_layout7,R.id.tjzimipowertest_layout8,
			R.id.tjzimipowertest_layout9,R.id.tjzimipowertest_layout10,R.id.tjzimipowertest_layout11,R.id.tjzimipowertest_layout12,
			R.id.tjzimipowertest_layout13,R.id.tjzimipowertest_layout14,R.id.tjzimipowertest_layout15,R.id.tjzimipowertest_layout16};
	
	private  int[] restvs={R.id.tjzimipowertest_res1,R.id.tjzimipowertest_res2,R.id.tjzimipowertest_res3,R.id.tjzimipowertest_res4,
			R.id.tjzimipowertest_res5,R.id.tjzimipowertest_res6,R.id.tjzimipowertest_res7,R.id.tjzimipowertest_res8,
			R.id.tjzimipowertest_res9,R.id.tjzimipowertest_res10,R.id.tjzimipowertest_res11,R.id.tjzimipowertest_res12,
			R.id.tjzimipowertest_res13,R.id.tjzimipowertest_res14,R.id.tjzimipowertest_res15,R.id.tjzimipowertest_res16};
	
	private  int[] ceshizhis={R.id.tjzimipowertest_ceshizhi1,R.id.tjzimipowertest_ceshizhi2,R.id.tjzimipowertest_ceshizhi3,R.id.tjzimipowertest_ceshizhi4,
			R.id.tjzimipowertest_ceshizhi5,R.id.tjzimipowertest_ceshizhi6,R.id.tjzimipowertest_ceshizhi7,R.id.tjzimipowertest_ceshizhi8,
			R.id.tjzimipowertest_ceshizhi9,R.id.tjzimipowertest_ceshizhi10,R.id.tjzimipowertest_ceshizhi11,R.id.tjzimipowertest_ceshizhi12,
			R.id.tjzimipowertest_ceshizhi13,R.id.tjzimipowertest_ceshizhi14,R.id.tjzimipowertest_ceshizhi15,R.id.tjzimipowertest_ceshizhi16};
	
	private  LinearLayout[] view_laylouts={
			linearlayout1,linearlayout2,linearlayout3,linearlayout4,linearlayout5,linearlayout6,linearlayout7,linearlayout8,
			linearlayout9,linearlayout10,linearlayout11,linearlayout12,linearlayout13,linearlayout14,linearlayout15,linearlayout16
	};
	
	private  TextView[] view_restvs={restv1,restv2,restv3,restv4,restv5,restv6,restv7,restv8,
			restv9,restv10,restv11,restv12,restv13,restv14,restv15,restv16};
	
	private  TextView[] view_ceshizhis={ceshizhi1,ceshizhi2,ceshizhi3,ceshizhi4,ceshizhi5,ceshizhi6,ceshizhi7,ceshizhi8,
			ceshizhi9,ceshizhi10,ceshizhi11,ceshizhi12,ceshizhi13,ceshizhi14,ceshizhi15,ceshizhi16};
	
	private  String[] channels={"@o001\n","@o011\n","@o021\n","@o031\n","@o041\n","@o051\n","@o061\n","@o071\n","@o081\n",
			"@o091\n","@o101\n","@o111\n","@o121\n","@o131\n","@o141\n","@o151\n"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pb05powertest);
		init();
		
	}
	protected void onResume() {
		super.onResume();

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
								stopService(new Intent(Tjzimipowertest.this,
										BackgroundService.class));
								unRegisterPort();
								finish();

							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
	private void unRegisterPort() {
			if (dataOutput != null) {
				try {
					dataOutput.close();
				} catch (IOException e) {
					Log.e(TAG, "unRegisterPort dataOutput", e);
				}
				dataOutput = null;
			}
			if (serialport_1 != null) {
				serialport_1.removeEventListener();
				serialport_1.close();
			}
			serialport_1 = null;
			if (serialport_2 != null) {
				serialport_2.removeEventListener();
				serialport_2.close();
			}
			serialport_2 = null;
			if (serialport_3 != null) {
				serialport_3.removeEventListener();
				serialport_3.close();
			}
		}
	public void init() {
		super.commonActivity = this;
		super.init();
		
		try {
			registerPorts();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "注册串口失败", Toast.LENGTH_SHORT).show();
		}
		
		sharedpreferences=PreferenceManager.getDefaultSharedPreferences(Tjzimipowertest.this);
		editor=sharedpreferences.edit();
		
		channel_list=new ArrayList<Integer>();
		
		for(int x=1;x<=16;x++){
			view_laylouts[x-1]=(LinearLayout) findViewById(laylouts[x-1]);
			view_restvs[x-1]=(TextView) findViewById(restvs[x-1]);
			view_ceshizhis[x-1]=(TextView) findViewById(ceshizhis[x-1]);
		}
		
		tv1=(TextView) findViewById(R.id.tjzimipowertest_biaozhunzhixiatv);
		tv1.setText(sharedpreferences.getString("min_i", "1.6"));
		imin=sharedpreferences.getString("min_i", "1.6");
		tv2=(TextView) findViewById(R.id.tjzimipowertest_biaozhunzhishangtv);
		tv2.setText(sharedpreferences.getString("max_i", "1.8"));
		imax=sharedpreferences.getString("max_i", "1.8");
		
		ed1=(EditText) findViewById(R.id.tjzimipowertest_biaozhunxiaedit);
		ed2=(EditText) findViewById(R.id.tjzimipowertest_biaozhunshangedit);
		relayout=(RelativeLayout) findViewById(R.id.tjzimipowertest_relative);
		relayout.setVisibility(View.GONE);
		button=(Button) findViewById(R.id.tjzimipowertest_button);
		button.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				imin=ed1.getText().toString().trim();
				imax=ed2.getText().toString().trim();
				tv1.setText(imin);
				tv2.setText(imax);
				
				editor.putString("min_i", imin);
				editor.putString("max_i", imax);
				editor.commit();
				
				if(isshow){
					relayout.setVisibility(View.GONE);
					isshow=false;
				}
			}
		});
		debugtv=(TextView) findViewById(R.id.tjzimipowertest_debugtv);
		
		/********测试用而已*********************************************************************************************************************************/
		button2=(Button) findViewById(R.id.tjzimipowertest_testbutton);
		button2.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
                 
                 
				restore();
				testtimes=1;
				linenum=1;
				channel_list.clear();
				dataOutput(serialport_1,"@o001\n");
				SystemClock.sleep(10000);
				test_i();
				SystemClock.sleep(5000);
			}
		});
		
		
		button3=(Button) findViewById(R.id.tjzimipowertest_testbutton2);
		button3.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
                 
                 
				retest(channel_list);
			}
		});
		/*****测试用而已**************************************************************************************************************************************/
	}
  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	// TODO Auto-generated method stub
	  menu.add(0,Menu.FIRST,0,"设置");
	  menu.add(0,Menu.FIRST+1,0,"进入DEBUG");
	  menu.add(0,Menu.FIRST+2,0,"退出DEBUG");
	  menu.add(0,Menu.FIRST+3,0,"退出");
	return super.onCreateOptionsMenu(menu);
}
  
@SuppressLint("ResourceAsColor")
@Override
   public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	 
	  switch (item.getItemId()) {
		case Menu.FIRST+3:
			break;
		case Menu.FIRST:
			final android.app.AlertDialog.Builder builder2 = new AlertDialog.Builder(
					this);
			LinearLayout loginForm = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.passworddialog, null);
			builder2.setView(loginForm);
			builder2.setTitle("设置电流上下限");
			final EditText password = (EditText) loginForm
					.findViewById(R.id.Edit_password);
			builder2.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (MyApplication.getpasswrod().equals(
									password.getText().toString().trim())) {
								relayout.setVisibility(View.VISIBLE);
								isshow=true;
							}
							else{
								Toast.makeText(Tjzimipowertest.this, "密码错误，请重试！", Toast.LENGTH_SHORT).show();
							}
						}
					});
			builder2.setNegativeButton("取消", null);
			builder2.create().show();
			break;
			
		case Menu.FIRST+1:
			//进入debug模式，背景全红，点击通道整个布局 会响应点击事件，打开通道，开始读电流
		    isdebug=true;
		    debugtv.setVisibility(View.VISIBLE);
		    for(LinearLayout l:view_laylouts){
		    	l.setBackgroundResource(R.color.red);
		    	l.setOnClickListener(this);
		    }
			break;
			
		case Menu.FIRST+2:
			//退出debug模式，恢复原始界面，布局点击不可用
		    isdebug=false;
		    debugtv.setVisibility(View.GONE);
		    restore();
		    for(LinearLayout l:view_laylouts){
		    	l.setOnClickListener(null);
		    }
		    break;
		}

	  
	  
	return super.onOptionsItemSelected(item);
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
					
					if (true) {
						String msg=readMessage();
						//Log.i(TAG,"收到的"+receivetime+"消息是:"+msg);
						Message.obtain(mHandler, 1, msg).sendToTarget();
						//Toast.makeText(Tjzimipowertest.this, who+"读到的数据是1口"+msg, Toast.LENGTH_LONG).show();
						
					} else {
						clearBuff();
					}
				} else if ("/dev/ttyS2".equals(who)) {
					if (true) {
						readMessage();
						Toast.makeText(Tjzimipowertest.this, who+"读到的数据是2口"+readMessage(), Toast.LENGTH_LONG).show();
					} else {
						clearBuff();
					}
				} else if ("/dev/ttyS3".equals(who)) {
					if (true) {
						readMessage();
						Toast.makeText(Tjzimipowertest.this, who+"读到的数据是3口"+readMessage(), Toast.LENGTH_LONG).show();
					} else {
						clearBuff();
					}
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
					
						for (i = 0; i < numBytes; i++) {
							//Log.d(TAG, "serialEvent: byte[" + i + "]"
								//	+ (readBuffer[i]));
						}
					
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
	//注册串口
	private void registerPorts() throws TooManyListenersException {
		serialport_1 = registerPort("/dev/ttyS1");
	}
	//根据com口路径返回串口对象
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
	
	@SuppressWarnings("unused")
	private void dataOutput(SerialPort portName, String out) {
		try {
			//Log.i(TAG,"发送的"+sendtime+"消息是:"+out);
			dataOutput = portName.getOutputStream();
			dataOutput.write(out.getBytes("US-ASCII"));
			dataOutput.flush();
			
			//Toast.makeText(this, portName+"串口写数据成功！写的数据是"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "串口写数据失败这里有执行！", Toast.LENGTH_SHORT).show();
		}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				analysisMessage("COM1",msg.obj.toString());
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public void analysisMessage(String tty, String msg){
		if("COM1".equals(tty)){
			/************************************************************************************************/
			if(msg.contains("@R")){
				
				if(isdebug){
					isexittest=false;
					debugtv.setText("DEBUG模式：检测到开始测试指令，请点击通道框进行电流读取！！");
				}
				else  if(isretest){
					 retest(channel_list);
				}
				else{
				//Toast.makeText(this, "正常模式，检测到启动指令，开始测试！", Toast.LENGTH_LONG).show();
				restore();
				testtimes=1;
				linenum=1;
				channel_list.clear();
				isexittest=false;
				isretest=false;
				SystemClock.sleep(1000);
				dataOutput(serialport_1,"@o001\n");
				SystemClock.sleep(1000);
				test_i();
				}
			}
			/************************************************************************************************/
			else if(msg.contains("@o")){  
				Log.i(TAG,"第"+linenum+"通道执行");
				if(isdebug){
					debugtv.setText("DEBUG模式：1、打开通道成功,\r\n2、读到的电流值是");
				}
			}
			/************************************************************************************************/
			else if(msg.contains("@U")){  
				Toast.makeText(this, "测试结束气缸弹起", Toast.LENGTH_SHORT).show();
            }
			/************************************************************************************************/
			else if(msg.contains("@S")){
				isexittest=true;
				Toast.makeText(this, "读到@S急停信息,停止测试", Toast.LENGTH_LONG).show();
            }
			/************************************************************************************************/
			//串口中有电流消息返回
			//“@A31123142331”A为命令311为电池电压3.11V,231为电池电流2.31A,423为充电电流4.23A,表示串口读到的是电流信息
			else if(msg.contains("@A")){  
				String	str_a=msg.substring(5, 8);
				i= (Float.parseFloat(str_a))/100;
				if(isexittest){
                  //按下了急停按钮！
					//Toast.makeText(this, "返回电流信息"+msg+",选项是退出测试为真", Toast.LENGTH_LONG).show();
					if(isdebug){
						debugtv.append(msg);
					}
				}
				else{
					if(isdebug){
						debugtv.append(msg);
						
						Animation animation=new  AlphaAnimation(0.5f,1.0f);
						debugtv.setAnimation(animation);
						
						animation.setDuration(1000);
						animation.setAnimationListener(new  AnimationListener() {
							
							@Override
							public void onAnimationStart(Animation arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationRepeat(Animation arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animation arg0) {
								// TODO Auto-generated method stub
								debugtv.setText("");
							}
						});
					}
					else{
						
						if(isretest){
							//是重测的！
							chulidudaoibyretest(linenum,i);
						}
						else{
							
							chulidudaoi(linenum,testtimes,i);
						}
					}
				}
			}
		/************************************************************************************************/
	   }
	}
	//开始测试函数
	public void test_i(){
			dataOutput(serialport_1,"@A\n");
		
	}
	
	//读取到串口返回的电流值 进行处理！！
	public  void  chulidudaoi(int line,int times,float i){
   
	 
		view_ceshizhis[line-1].append(i+",");
		 /************************************************************************************************************************************/
		if(times==3){
			if(Float.parseFloat(imin)<=i&&i<=Float.parseFloat(imax)){
				view_restvs[line-1].setText("PASS");
				view_restvs[line-1].setTextColor(Color.BLUE);
			    view_laylouts[line-1].setBackgroundResource(R.color.lightblue);
			}
			else{
				view_restvs[line-1].setText("FAIL");
				view_restvs[line-1].setTextColor(Color.RED);
				view_laylouts[line-1].setBackgroundResource(R.color.lightblue);
				channel_list.add(line);
			}
			
			if(line!=16){
				testtimes=1;
				linenum++;
				dataOutput(serialport_1,channels[line]);
				SystemClock.sleep(1000);
				test_i();
				SystemClock.sleep(500);
			}
			else{
				finalchannel(16);
			}
		}
		 /************************************************************************************************************************************/
		else{
			if(Float.parseFloat(imin)<=i&&i<=Float.parseFloat(imax)){
				view_restvs[line-1].setText("PASS");
				view_restvs[line-1].setTextColor(Color.BLUE);
				view_laylouts[line-1].setBackgroundResource(R.color.lightblue);
				if(line!=16){
					testtimes=1;
					linenum++;
					dataOutput(serialport_1,channels[line]);
					SystemClock.sleep(1000);
					test_i();
					SystemClock.sleep(500);
				}
				else{
					finalchannel(16);
				}
			}
			else{
					testtimes++;
					test_i();
					SystemClock.sleep(500);
			}
				
				
		
		}
	 }
	
	 /**重置通道界面 只是把背景设置白，结果设为wait.. 测试值清空 */
	public  void  restore(){
		for(int  x=1;x<=16;x++){
			view_laylouts[x-1].setBackgroundResource(R.color.white);
			view_restvs[x-1].setText("WAIT..");
			view_restvs[x-1].setTextColor(Color.BLUE);
			view_ceshizhis[x-1].setText("");
		}
	}
	public  void  restorebychannel(int channel){
		
			view_laylouts[channel-1].setBackgroundResource(R.color.white);
			view_restvs[channel-1].setText("WAIT..");
			view_restvs[channel-1].setTextColor(Color.BLUE);
			view_ceshizhis[channel-1].setText("");
		
	}
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		for(int x=0;x<laylouts.length;x++){
			if(laylouts[x]==arg0.getId()){
				
				debugtv.setText("");
				dataOutput(serialport_1, channels[x]);
				SystemClock.sleep(1000);
				dataOutput(serialport_1, "@A\n");
				SystemClock.sleep(500);
				Log.i(TAG,"你点击的是第"+x+1+"布局");
				break;
			}
			else{
			   continue;
			}
		}

	}
    
	public  void finalchannel(int  channel){
		
		dataOutput(serialport_1,"@o150\n");
		SystemClock.sleep(500);
		dataOutput(serialport_1,"@U\n");
		SystemClock.sleep(500);
		
		if(channel_list.size()!=0){
			//测试失败
			SoundEffectPlayService
			.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJFAIL_MUSIC);
			
			AlertDialog.Builder  builder=new AlertDialog.Builder(this);
			builder.setTitle("★★★★★★测试失败★★★★★★");
			builder.setMessage("★★★★★★★★★★★★★★★★★★★★★★★★★★★★\r\n"+
					           "★★                                                                                           ★★\r\n"+
					           "★★                                                                                           ★★\r\n"+
					           "★★                        是否需要重新测试？？                        ★★\r\n"+
					           "★★                                                                                          ★★\r\n"+
					           "★★                                                                                          ★★\r\n"+
					           "★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
			
			builder.setPositiveButton("重测!", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
				       isretest=true;
				       channel_iterator=channel_list.iterator();
					   //retest(channel_list);
					
				}});
			builder.setNegativeButton("不重测!",null) ;
			builder.create().show();
		}
		else{
			//测试成功
			SoundEffectPlayService
			.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.TJOK_MUSIC);
		}
	}


	 public  void retest(List<Integer> list){
		        
		        		  
		 if(channel_iterator.hasNext()){
			 linenum=channel_iterator.next();
			 restorebychannel(linenum);
			 dataOutput(serialport_1,channels[linenum-1]);
			 SystemClock.sleep(1000);
			 test_i();
			 SystemClock.sleep(500);
		 }
		        	
		        
	    }
	    
	    public  void  chulidudaoibyretest(int line,float i){
			    view_ceshizhis[line-1].append(i+"");
				if(Float.parseFloat(imin)<=i&&i<=Float.parseFloat(imax)){
					view_restvs[line-1].setText("RT--PASS");
					view_restvs[line-1].setTextColor(Color.BLUE);
				    view_laylouts[line-1].setBackgroundResource(R.color.lightblue);
				}
				else{
					view_restvs[line-1].setText("RT--FAIL");
					view_restvs[line-1].setTextColor(Color.RED);
					view_laylouts[line-1].setBackgroundResource(R.color.lightblue);
				}
				
				
				if(line==channel_list.get(channel_list.size()-1)){
					dataOutput(serialport_1,"@o"+(line-1)+"0\n");
					SystemClock.sleep(500);
					dataOutput(serialport_1,"@U\n");
					SystemClock.sleep(500);
					isretest=false;
				}
				else{
					 if(channel_iterator.hasNext()){
						 linenum=channel_iterator.next();
						 restorebychannel(linenum);
						 dataOutput(serialport_1,channels[linenum-1]);
						 SystemClock.sleep(1000);
						 test_i();
						 SystemClock.sleep(500);
					 }
				}
				
			
		 }
}
