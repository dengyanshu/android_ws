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
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.BiaoqianheduiModel;
import com.zowee.mes.model.Common4dModel;
import com.zowee.mes.model.SmtFvmiModel;
import com.zowee.mes.model.SntocarModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class Hanjiejiboxing extends CommonActivity 
		 {
	
	private Timer timer ;
	private TimerTask task;
	private XYSeries series;
	private XYSeries series2;
	private XYMultipleSeriesDataset mDataset;
	private GraphicalView chart;
	private XYMultipleSeriesRenderer renderer;
	private int addX = -1, addY;
	int[] xv = new int[100];
	int[] yv = new int[100];
	
	private LinearLayout  linearlayout;
	

	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();

	private  Common4dModel  common4dmodel;
	private static final String TAG = "Hanjiejiboxing";
	
	private static final String OWNER = "boxing";
	private  SerialPort  serialport_1;
    private  SerialPort  serialport_2;
    private  SerialPort  serialport_3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hanjieji_boxing);
		Log.i(TAG,"oncreate---boxing");
		
		init();
	}

	protected void onResume() {
		super.onResume();
		Log.i(TAG,"onresume---boxing");
		timer= new Timer();
		if(task==null){
			task = new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
					
					
				}
			};
		}
		
		timer.schedule(task, 400, 600);
	}
	
    @SuppressLint("HandlerLeak")
	private  Handler	handler = new Handler() {
		public void handleMessage(Message msg) {
			//循环发送到温控器的指令！
			if(serialport_2!=null){
			dataOutput(serialport_2, ":0A03238D000142\r\n");//pv  量程值
			SystemClock.sleep(250);
			dataOutput(serialport_2, ":0A03238E000240\r\n");//sp 目标值
			super.handleMessage(msg);
			}
		}
	};
	@Override
	public void onDestroy() {
		// 当结束程序时关掉Timer
		Log.i(TAG,"ondestory---boxing");
		unRegisterPort();
		super.onDestroy();
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG,"onstart---boxing");
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i(TAG,"onrestart---boxing");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(TAG,"onstop---boxing");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timer.cancel();
		task=null;
		timer=null;
		System.out.println("task==null的真假:"+task==null);
		Log.i(TAG,"onPause---boxing");
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
								stopService(new Intent(Hanjiejiboxing.this,
										BackgroundService.class));
								//stopService(new Intent(new Hanjiejipeizhi(),BackgroundService.class));
								timer.cancel();
								unRegisterPort();
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
    
	private void unRegisterPort() {
//		if (dataOutput != null) {
//			try {
//				dataOutput.close();
//			} catch (IOException e) {
//				Log.e(TAG, "unRegisterPort dataOutput", e);
//			}
//			dataOutput = null;
//		}
		if (serialport_2 != null) {
			serialport_2.removeEventListener();
			serialport_2.close();
		}
		serialport_2 = null;
		
	}
	
	public void init() {
//		super.commonActivity = this;
//		super.TASKTYPE = TaskType.biaoqianhedui; // 后台服务静态整形常量
//		super.init();
//
//		common4dmodel=new  Common4dModel();
		
       // common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));
       linearlayout=(LinearLayout) findViewById(R.id.hanjieji_boxingchartyuan);
       try {
			
			serialport_2 = registerPort("/dev/ttyS2");
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "注册串口失败", Toast.LENGTH_SHORT).show();
		}
       
       series = new XYSeries("波形图");
       series2 = new XYSeries("波形图");

		// 创建一个数据集的实例，这个数据集将被用来创建图表
		mDataset = new XYMultipleSeriesDataset();

		// 将点集添加到这个数据集中
		mDataset.addSeries(series);
		mDataset.addSeries(series2);

		// 以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
		int[] color = new int[]{Color.GREEN,Color.RED};
		PointStyle[] style =new  PointStyle[]{PointStyle.CIRCLE,PointStyle.DIAMOND};
		renderer = buildRenderer(color, style, true);

		// 设置好图表的样式
		setChartSettings(renderer, "X", "Y", 0, 100, 0, 400, Color.WHITE,
				Color.WHITE);

		// 生成图表
		chart = ChartFactory.getLineChartView(this, mDataset, renderer);

		// 将图表添加到布局中去
		linearlayout.addView(chart, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

	}
	
	protected XYMultipleSeriesRenderer buildRenderer(int[] color,
			PointStyle[] style, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		// 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		for(int  x=0;x<color.length;x++){
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(color[x]);
			r.setPointStyle(style[x]);
			r.setFillPoints(fill);
			r.setLineWidth(3);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
    
	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String xTitle, String yTitle, double xMin, double xMax,
			double yMin, double yMax, int axesColor, int labelsColor) {
		// 有关对图表的渲染可参看api文档
		renderer.setChartTitle("温度波形图");
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.GREEN);
		renderer.setXLabels(20);
		renderer.setYLabels(10);
		renderer.setXTitle("Time");
		renderer.setYTitle("温度");
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float)5);
		renderer.setShowLegend(false);
	}
	
	private void updateChart(int y) {

		// 设置好下一个需要增加的节点
		addX = 0;
		addY = y;
		// 移除数据集中旧的点集
		mDataset.removeSeries(series);
		// 判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
		int length = series.getItemCount();
		if (length > 100) {
			length = 100;
		}
		// 将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
		for (int i = 0; i < length; i++) {
			xv[i] = (int) series.getX(i) + 1;
			yv[i] = (int) series.getY(i);
		}
		// 点集先清空，为了做成新的点集而准备
		series.clear();

		// 将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
		// 这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点
		series.add(addX, addY);
		for (int k = 0; k < length; k++) {
			series.add(xv[k], yv[k]);
		}
		// 在数据集中添加新的点集
		mDataset.addSeries(series);

		// 视图更新，没有这一步，曲线不会呈现动态
		// 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		chart.invalidate();
	}
   
	
	private void updateChart2(int y) {

		// 设置好下一个需要增加的节点
		addX = 0;
		addY = y;
		// 移除数据集中旧的点集
		mDataset.removeSeries(series2);
		// 判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
		int length = series2.getItemCount();
		if (length > 100) {
			length = 100;
		}
		// 将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
		for (int i = 0; i < length; i++) {
			xv[i] = (int) series2.getX(i) + 1;
			yv[i] = (int) series2.getY(i);
		}
		// 点集先清空，为了做成新的点集而准备
		series2.clear();

		// 将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
		// 这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点
		series2.add(addX, addY);
		for (int k = 0; k < length; k++) {
			series2.add(xv[k], yv[k]);
		}
		// 在数据集中添加新的点集
		mDataset.addSeries(series2);

		// 视图更新，没有这一步，曲线不会呈现动态
		// 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		chart.invalidate();
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
 				
 				if ("/dev/ttyS2".equals(who)) {
 					
 					if (true) {
 						String msg=readMessage();
 						if(msg.matches(":0A030[2,4][A-F0-9]{4,}\r\n")){
 							if((msg.charAt(6)+"").equals("2")){
 	 							//读一个字节读取的是量程值
 	 							msg=msg.substring(7, 11);
 	 	 						Log.i(TAG,"收到的消息是:"+msg);
 	 	 						Message.obtain(mHandler, 1, msg).sendToTarget();
 	 	 						//Toast.makeText(Tjzimipowertest.this, who+"读到的数据是1口"+msg, Toast.LENGTH_LONG).show();
 	 						}
 	 						
 	 						else{
 	 							msg=msg.substring(7, 11);
 	 	 						Log.i(TAG,"收到的消息是:"+msg);
 	 	 						Message.obtain(mHandler2, 1, msg).sendToTarget();
 	 						}
 						}else{
 							clearBuff();
 						}
 					
 						
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
 	
 	private void dataOutput(SerialPort portName, String out) {
		try {
		   OutputStream	dataOutput = portName.getOutputStream();
			dataOutput.write(out.getBytes("US-ASCII"));
			dataOutput.flush();
			Log.i(TAG,portName+"写的数据是:"+out );
			//Toast.makeText(this, portName+"串口写数据成功！写的数据是"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "串口写数据失败这里有执行！", Toast.LENGTH_SHORT).show();
		}
	}
 	
   private  Handler	mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			  int y=Integer.valueOf(msg.obj.toString(), 16);
			  updateChart(y);
		     
			
		
			super.handleMessage(msg);
		}
	};
	
	  private  Handler	mHandler2= new Handler() {
			@Override
			public void handleMessage(Message msg) {
				  int y=Integer.valueOf(msg.obj.toString(), 16);
				  updateChart2(y);
				super.handleMessage(msg);
			}
		};
	
	
}
