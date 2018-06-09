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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.Theme;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

public class Hanjieji extends CommonActivity 
		 {
	private Timer timer ;
	private TimerTask task;
	private boolean   appisok=false;
	private boolean   isread=true;
	private  int  num=1;//标示温控仪handler 处理的数据是哪个值
	private String    onetemp="";
	private  String  onexielv="";
	private  String  oneshijian="";
	private String    twotemp="";
	private  String  twoxielv="";
	private  String  twoshijian="";
	private String    threetemp="";
	private  String  threexielv="";
	private  String  threeshijian="";
	
	
	private  EditText  editkaishiwendu;
	private  EditText  editchuiqilengqueshijian;
	private  EditText  editchaoshishijian;
	private  Button  plcsettingbutton;
	
	private  EditText  editonetemp;
	private  EditText  editonexielv;
	private  EditText  editonebaowenshijian;
	private  EditText  edittwotemp;
	private  EditText  edittwoxielv;
	private  EditText  edittwobaowenshijian;
	private  EditText  editthreetemp;
	private  EditText  editthreexielv;
	private  Button  wenkongyisettingbutton;
	
	private XYSeries series;
	private XYSeries series2;
	private XYMultipleSeriesDataset mDataset;
	private GraphicalView chart;
	private XYMultipleSeriesRenderer renderer;
	private Double addX = -1.0, addY;
	Double[] xv = new Double[30];
	Double[] yv = new Double[30];
	
	private LinearLayout  linearlayout;
	


	private static final String TAG = "Hanjieji";
	
	private static final String OWNER = "boxing";
	private  SerialPort  serialport_1;//单片机
    private  SerialPort  serialport_2;//温控仪

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_hanjieji);
		Log.i(TAG,"oncreate---boxing");
		
		init();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//读取单片机和温控仪设置的参数 ，并且画出图！
		dataOutput(serialport_1, "@R\n");
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
								stopService(new Intent(Hanjieji.this,
										BackgroundService.class));
								unRegisterPort();
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}
    
	private void unRegisterPort() {
		if (serialport_2 != null) {
			serialport_2.removeEventListener();
			serialport_2.close();
		}
		serialport_2 = null;
		
		if (serialport_1 != null) {
			serialport_1.removeEventListener();
			serialport_1.close();
		}
		serialport_1 = null;
	}
	
	public void init() {
//		super.commonActivity = this;
//		super.TASKTYPE = TaskType.biaoqianhedui; // 后台服务静态整形常量
//		super.init();
//
//		common4dmodel=new  Common4dModel();
		
       // common4dmodel.getResourceid(new Task(this,TaskType.common4dmodelgetresourceid,resourcename));
      linearlayout=(LinearLayout) findViewById(R.id.hanjieji_boxingchart);
       
     editkaishiwendu=(EditText) findViewById(R.id.hanjieji_begintem);
     editchuiqilengqueshijian=(EditText) findViewById(R.id.hanjieji_chuiqishijian);
   	 editchaoshishijian=(EditText) findViewById(R.id.hanjieji_chaoshishijian);
     plcsettingbutton=(Button) findViewById(R.id.hanjieji_settoplcbutton);
     plcsettingbutton.setOnClickListener(new  OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(Hanjieji.this);
			builder.setTitle("请确认");
			builder.setMessage("已经配置完毕，提交到单片机？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							String out="@S";
							String str=get4string(editkaishiwendu.getText().toString().trim());
							if(str.equals("0000")){
								Toast.makeText(Hanjieji.this, "开始温度设置异常不能提交！", Toast.LENGTH_SHORT).show();
								return ;
							}
							else
								 out=out+str;
							str=get4string(editchuiqilengqueshijian.getText().toString().trim());
							if(str.equals("0000")){
								Toast.makeText(Hanjieji.this, "冷却时间设置异常不能提交！", Toast.LENGTH_SHORT).show();
								return ;
							}else{
								 out=out+str;
							}
							str=get4string(editchaoshishijian.getText().toString().trim());
							if(str.equals("0000")){
								Toast.makeText(Hanjieji.this, "加热超时时间设置异常不能提交！", Toast.LENGTH_SHORT).show();
								return ;
							}else{
								 out=out+str+"\n";
							}
							dataOutput(serialport_1,out);
							Toast.makeText(Hanjieji.this, "设置成功，请退出程序重新打开！", Toast.LENGTH_SHORT).show();
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create().show();
		}
	});
     setenableonplc();
   	
    editonetemp=(EditText) findViewById(R.id.hanjieji_onetemp);
    editonexielv=(EditText) findViewById(R.id.hanjieji_oneshengwenshijian);
   	editonebaowenshijian=(EditText) findViewById(R.id.hanjieji_onebaowenshijian);
    edittwotemp=(EditText) findViewById(R.id.hanjieji_twotemp);
    edittwoxielv=(EditText) findViewById(R.id.hanjieji_twoshengwenshijian);
    edittwobaowenshijian=(EditText) findViewById(R.id.hanjieji_twobaowenshijian);
    editthreetemp=(EditText) findViewById(R.id.hanjieji_threetemp);
    editthreexielv=(EditText) findViewById(R.id.hanjieji_threexiajiangshijian);
    wenkongyisettingbutton=(Button) findViewById(R.id.hanjieji_settowenkongyibutton);;
    wenkongyisettingbutton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(Hanjieji.this);
			builder.setTitle("请确认！！");
			builder.setMessage("   已经配置完毕，提交到温控仪！请检查：1、值必须为整数，时间一般1~10s!2、第2段温度值最大，第1段和第3段温度大于开始温度");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							String  onetemp=editonetemp.getText().toString();
							String  onexielv=editonexielv.getText().toString();
							onexielv=(Integer.parseInt(onexielv))*10+"";
							String  oneshijian=editonebaowenshijian.getText().toString();
							
							String  twotemp=edittwotemp.getText().toString();
							String  twoxielv=edittwoxielv.getText().toString();
							twoxielv=(Integer.parseInt(twoxielv))*10+"";
							String  twoshijian=edittwobaowenshijian.getText().toString();
							
							String  threetemp=editthreetemp.getText().toString();
							String  threexielv=editthreexielv.getText().toString();
							threexielv=(Integer.parseInt(threexielv))*10+"";
						
							if(onetemp.equals("")||onexielv.equals("")||oneshijian.equals("")||twotemp.equals("")||twoxielv.equals("")||twoshijian.equals("")||threetemp.equals("")||threexielv.equals("")||
							onetemp.contains(".")||onexielv.contains(".")||oneshijian.contains(".")||twotemp.contains(".")||twoxielv.contains(".")||twoshijian.contains(".")||threetemp.contains(".")||threexielv.contains(".")	){
								//如果为空的或者有小数点就直接报错！
								Toast.makeText(Hanjieji.this, "请确保不能为空或者包含小数！", Toast.LENGTH_LONG).show();
							}
							else{
							    ProgressDialog p=new ProgressDialog(Hanjieji.this);
							    p.setTitle("正在设置温控仪的参数请等待....");
							    p.show();
							    
//								String out=getoutstring("1B5D", onetemp);
//								dataOutput(serialport_2,out);
//								SystemClock.sleep(100);
//								
//								out=getoutstring("1B5F", onexielv);
//			                    dataOutput(serialport_2,out);
//			                    SystemClock.sleep(100);
//			                    
//			                    out=getoutstring("1B60", oneshijian);
//			                    dataOutput(serialport_2,out);
//			                    SystemClock.sleep(100);
//			                    
//			                    out=getoutstring("1B61", twotemp);
//			                    dataOutput(serialport_2,out);
//			                    SystemClock.sleep(100);
//			                    
//			                    out=getoutstring("1B63", twoxielv);
//			                    dataOutput(serialport_2,out);
//			                    SystemClock.sleep(100);
//			                    
//			                    out=getoutstring("1B64", twoshijian);
//			                    dataOutput(serialport_2,out);
//			                    SystemClock.sleep(100);
//			                    
//			                    out=getoutstring("1B65", threetemp);
//			                    dataOutput(serialport_2,out);
//			                    SystemClock.sleep(100);
//			                    
//			                    out=getoutstring("1B67", threexielv);
//			                    dataOutput(serialport_2,out);
//			                    SystemClock.sleep(100);
//								String out=getoutstring("1B5D", onetemp);
//								dataOutput(serialport_2,out);
//								SystemClock.sleep(100);
							    
							    
							    String out=getoutstring("5B5D", onetemp);
								dataOutput(serialport_2,out);
								SystemClock.sleep(100);
								
								out=getoutstring("5B5F", onexielv);
			                    dataOutput(serialport_2,out);
			                    SystemClock.sleep(100);
			                    
			                    out=getoutstring("5B60", oneshijian);
			                    dataOutput(serialport_2,out);
			                    SystemClock.sleep(100);
			                    
			                    out=getoutstring("5B61", twotemp);
			                    dataOutput(serialport_2,out);
			                    SystemClock.sleep(100);
			                    
			                    out=getoutstring("5B63", twoxielv);
			                    dataOutput(serialport_2,out);
			                    SystemClock.sleep(100);
			                    
			                    out=getoutstring("5B64", twoshijian);
			                    dataOutput(serialport_2,out);
			                    SystemClock.sleep(100);
			                    
			                    out=getoutstring("5B65", threetemp);
			                    dataOutput(serialport_2,out);
			                    SystemClock.sleep(100);
			                    
			                    out=getoutstring("5B67", threexielv);
			                    dataOutput(serialport_2,out);
			                    SystemClock.sleep(100);
							    
			                    p.dismiss();
			                    Toast.makeText(Hanjieji.this, "设置温控仪成功，请退出程序重新打开！", Toast.LENGTH_SHORT).show();
							}
							
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create().show();
		}
	});
   	 setenableonwenkongyi();
   	
   	
       try {
			
    	    serialport_1 = registerPort("/dev/ttyS1");
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
		setChartSettings(renderer, "X", "Y", 0, 24, 0, 400, Color.WHITE,
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
		renderer.setGridColor(Color.WHITE);
		renderer.setXLabels(12);
		renderer.setYLabels(15);
		renderer.setXTitle("时间(s)");
		renderer.setYTitle("温度");
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float)5);
		renderer.setShowLegend(false);
	}
	
	//初始化目标曲线
	private void updateChart(List<Double[]> list) {
		if(list.size()==0){
			Toast.makeText(Hanjieji.this, "无法初始化目标曲线请看设定的值是否超出了图的最大值！", Toast.LENGTH_SHORT).show();
			return;
			
		}
		Double[] xvaule=list.get(0);
		Double[] yvaule=list.get(1);
		mDataset.removeSeries(series);
		series.clear();
       for(int x=0;x<xvaule.length;x++){
    	   series.add(xvaule[x], yvaule[x]);
       }
		mDataset.addSeries(series);
		// 视图更新，没有这一步，曲线不会呈现动态
		// 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		chart.invalidate();
	}
   
	
//	private void updateChart2(Double y) {
//
//		// 设置好下一个需要增加的节点
//		addX = 0.0;
//		addY = y;
//		// 移除数据集中旧的点集
//		mDataset.removeSeries(series2);
//		// 判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
//		int length = series2.getItemCount();
//		if (length > 30) {
//			length = 30;
//		}
//		// 将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
//		for (int i = 0; i < length; i++) {
//			xv[i] = (Double) series2.getX(i) + 1;
//			yv[i] = (Double) series2.getY(i);
//		}
//		// 点集先清空，为了做成新的点集而准备
//		series2.clear();
//
//		// 将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
//		// 这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点
//		series2.add(addX, addY);
//		for (int k = 0; k < length; k++) {
//			series2.add(xv[k], yv[k]);
//		}
//		// 在数据集中添加新的点集
//		mDataset.addSeries(series2);
//
//		// 视图更新，没有这一步，曲线不会呈现动态
//		// 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
//		chart.invalidate();
//	}
//	
	//第一个点停在第一个位置，第二个点停在第二个 曲线不向右走 不要动态效果！
	private void updateChart2(Double y) {
		int length = series2.getItemCount();
//		if (length > 29) {
//			//不做更新
//		}else{
//			//更新点
//			addX = 1+length;
//			addY = (int) (Math.random() * 90);
//		}
		addX = (double) (1+length);
		addY = y;
		series2.add(addX, addY);
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
 				
 				if ("/dev/ttyS1".equals(who)) {
 					String msg=readMessage();
 					if (!appisok) {
 						//应用程序未准备好  
 						if(msg.contains("@B")||msg.contains("@E")){
 							clearBuff();
 						}
 						//msg=@R006000600120
 						else{
 							
 							Message.obtain(mHandler, 1, msg).sendToTarget();
 						}

 					} else {
 						//应用程序准备好
 						if(msg.contains("@S")){
 							clearBuff();
 						}
 						
// 						if(msg.contains("@B")){
//						  isread=true;
//					      new  Thread(){
//					    	 public void run() {
//					    		 while(true)
//					    			 if(isread){
//					    				 if(serialport_2!=null){
//								 				dataOutput(serialport_2, ":0A03238D000142\r\n");//pv  量程值
//								 				SystemClock.sleep(1000);
//								    		 }
//					    			 }
//					    		
//					    	 }
//					     };
// 						}
//
// 						if(msg.contains("@E")){
// 							Toast.makeText(Hanjieji.this, "接收到@E信号", Toast.LENGTH_SHORT).show();
// 							isread=false;
//// 							timer.cancel();
//// 							task=null;
//// 							timer=null;
// 							mDataset.removeSeries(series2);
// 							series2.clear();
// 							mDataset.addSeries(series2);
// 							chart.invalidate();
//						
//					}
 						
 					   if(msg.contains("@B")){
 						  Message.obtain(bhandler, 1, msg).sendToTarget();
 							  
 						}
 						
 						if(msg.contains("@E")){
 							Message.obtain(ehandler, 1, msg).sendToTarget();
 							
 							
 						}
 					}
 				} 
 				
 				if ("/dev/ttyS2".equals(who)) {
 					
 					if (!appisok) {
 						//应用程序未准备好读到的数据发到wenkongyiHandler,这个handler来处理初始化界面的数据
 						String msg=readMessage();
 						if(msg.matches(":0A0302[A-F0-9]{4,}\r\n")){
	 							msg=msg.substring(7, 11);
	 	 						Log.i(TAG,"收到的消息是:"+msg);
	 	 						Message.obtain(wenkongyiHandler, 1, msg).sendToTarget();
	 					}
						else{
							clearBuff();
						}
 						
 				    } 
 					else {
 						//应用程序已初始化好！
 						String msg=readMessage();
 						if(msg.matches("(:0A0302[A-F0-9]{4,}\r\n){1,}")){
 	 							msg=msg.substring(7, 11);
 	 	 						Log.i(TAG,"收到的消息是:"+msg);
 	 	 						Message.obtain(mHandler2, 1, msg).sendToTarget();
 	 					}
 						else{
 							clearBuff();
 						}
 						
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
 	
 	
 
 	private  void setenableonplc(){
 		 editkaishiwendu.setEnabled(false);
 	     editchuiqilengqueshijian.setEnabled(false);
 	   	 editchaoshishijian.setEnabled(false);
 	     plcsettingbutton.setEnabled(false);
 	}
 	
	private  void setenableonwenkongyi(){
		editonetemp.setEnabled(false);
	    editonexielv.setEnabled(false);
	   	editonebaowenshijian.setEnabled(false);
	    edittwotemp.setEnabled(false);
	   	 edittwoxielv.setEnabled(false);
	     edittwobaowenshijian.setEnabled(false);
	   	  editthreetemp.setEnabled(false);
	      editthreexielv.setEnabled(false);
	   	  wenkongyisettingbutton.setEnabled(false);
	}
	private   void  setenable(){
		 editkaishiwendu.setEnabled(true);
 	     editchuiqilengqueshijian.setEnabled(true);
 	   	 editchaoshishijian.setEnabled(true);
 	     plcsettingbutton.setEnabled(true);
 	     
 	    editonetemp.setEnabled(true);
	    editonexielv.setEnabled(true);
	   	editonebaowenshijian.setEnabled(true);
	    edittwotemp.setEnabled(true);
	   	 edittwoxielv.setEnabled(true);
	     edittwobaowenshijian.setEnabled(true);
	   	  editthreetemp.setEnabled(true);
	      editthreexielv.setEnabled(true);
	   	  wenkongyisettingbutton.setEnabled(true);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hanjieji_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.hanjieji_menu1:
			android.app.AlertDialog.Builder builder2 = new AlertDialog.Builder(
					this);
			LinearLayout loginForm = (LinearLayout) getLayoutInflater()
					.inflate(R.layout.passworddialog, null);
			builder2.setView(loginForm);
			builder2.setTitle("打开设定项");
			final EditText password = (EditText) loginForm
					.findViewById(R.id.Edit_password);
			builder2.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if ("warn".equalsIgnoreCase(
									password.getText().toString().trim())) {
								setenable();
							}
							else
							Toast.makeText(Hanjieji.this, "输入密码不对请重新输入", Toast.LENGTH_LONG).show();
						}
					});
			builder2.setNegativeButton("取消", null);
			builder2.create().show();
			
			break;
		case R.id.hanjieji_menu2:
			
			
			
			break;
		}
		return  true;
	}
	//这个handler 用于处理单片机的消息  程序初始化 appisok=false;
   private  Handler	mHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String message=msg.obj.toString();
			//程序获取用户响应时候的 读取单片机内设置信息，串口返回的消息
			//如果单片机里面没信息的话，应该返回的是@R000000000000
			if(message.contains("R")){
		    	String  begintem=Integer.parseInt(message.substring(2,6))+"";
		    	String  lengqueshijian=Integer.parseInt(message.substring(6,10))+"";
		    	String  chaoshishijian=Integer.parseInt(message.substring(10,14))+"";
		    	 editkaishiwendu.setText(begintem);
		    	 editchuiqilengqueshijian.setText(lengqueshijian);
		    	 editchaoshishijian.setText(chaoshishijian);
		    	 dataOutput(serialport_2, ":0A031B5D00017A\r\n");//第一段温度的指令
			}
		
			super.handleMessage(msg);
		}
	};
	//串口1读到了@B消息
	
	private  Handler	bhandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mDataset.removeSeries(series2);
			series2.clear();
			mDataset.addSeries(series2);
			chart.invalidate();
			  isread=true;
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
				timer.schedule(task, 400, 1000);
		
			super.handleMessage(msg);
		}
	};
	//读到E消息
	private  Handler	ehandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(Hanjieji.this, "接收到@E信号", Toast.LENGTH_SHORT).show();
				isread=false;
				timer.cancel();
				task=null;
				timer=null;
				
		
			super.handleMessage(msg);
		}
	};
	
	  private Handler	handler = new Handler() {
			public void handleMessage(Message msg) {
				//循环发送到温控器的指令！
				if(serialport_2!=null){
				dataOutput(serialport_2, ":0A03238D000142\r\n");//pv  量程值
				SystemClock.sleep(250);
				super.handleMessage(msg);
				}
			}
	   };
	//处理温控仪串口返回的信息,应用程序未准备好！
	  private  Handler	wenkongyiHandler= new Handler() {
			@Override
			public void handleMessage(Message msg) {
			String	message=msg.obj.toString();
		    int vaule=Integer.valueOf(message, 16);
		    switch(num){
		    case 1:
		    	onetemp=vaule+"";
		    	editonetemp.setText(onetemp);
		    	num++;
		    	dataOutput(serialport_2, ":0A031B5F000178\r\n");
		    	break;
		    case 2:
		    	onexielv=vaule/10+"";
		    	editonexielv.setText(onexielv);
		    	num++;
		    	dataOutput(serialport_2, ":0A031B60000177\r\n");
		      break;
		    case 3:
		    	oneshijian=vaule+"";
		    	editonebaowenshijian.setText(oneshijian);
		    	num++;
		    	dataOutput(serialport_2, ":0A031B61000176\r\n");
		      break;
		    case 4:
		    	twotemp=vaule+"";
		    	edittwotemp.setText(twotemp);
		    	num++;
		    	dataOutput(serialport_2, ":0A031B63000174\r\n");
		      break;
		    case 5:
		    	twoxielv=vaule/10+"";
		    	edittwoxielv.setText(twoxielv);
		    	num++;
		    	dataOutput(serialport_2, ":0A031B64000173\r\n");
		      break;
		    case 6:
		    	twoshijian=vaule+"";
		    	edittwobaowenshijian.setText(twoshijian);
		    	num++;
		    	dataOutput(serialport_2, ":0A031B65000172\r\n");
		      break;
		    case 7:
		    	threetemp=vaule+"";
		    	editthreetemp.setText(threetemp);
		    	num++;
		    	dataOutput(serialport_2, ":0A031B67000170\r\n");
		      break;
		    case 8:
		    	threexielv=vaule/10+"";
		    	editthreexielv.setText(threexielv);
		    	num++;
		    	dataOutput(serialport_2, ":0A031B6800016F\r\n");
		      break;
		    case 9:
		    	threeshijian=vaule+"";
		    	num=1;
		    	appisok=true;
		    	//开始绘制理想曲线，1、各文本框值进行更新，2、判断文本框的值 如果 比较各段温度值，如异常则提示
		       List<Double[]> list=gettargetvaule(editkaishiwendu.getText().toString(), onetemp, onexielv, oneshijian, twotemp, twoxielv, twoshijian, threetemp, threexielv, threeshijian);
		       updateChart(list);
		    	 
		      break;
		      
		    }
				  
		    super.handleMessage(msg);
			}
	};
	
	 private  Handler	mHandler2= new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String  message=msg.obj.toString();
				int yvaule=Integer.parseInt(message, 16);
				  Double y=Double.valueOf(yvaule+"");
				  updateChart2(y);
				super.handleMessage(msg);
			}
		};
	
	private  List<Double[]>  gettargetvaule(String originallytemp,String onetemp,String onexielv,String oneshijian,String twotemp,String twoxielv,String twoshijian,
			String threetemp,String threexielv,String threeshijian){
		List<Double[]> list=new ArrayList<Double[]>();
		if(Double.parseDouble(twotemp)>Double.parseDouble(onetemp)&&Double.parseDouble(twotemp)>Double.parseDouble(threetemp)&&
				Double.parseDouble(onetemp)>Double.parseDouble(originallytemp)&&Double.parseDouble(threetemp)>Double.parseDouble(originallytemp))
				{
			Double[]  x=new Double[7];
			Double[]  y=new Double[7];
			double num=0.0;
			x[0]=0.0;
			y[0]=Double.parseDouble(originallytemp);
			
			editonetemp.setText(onetemp);
			editonexielv.setText(onexielv);
			double oneshengwenshijian=(Double.parseDouble(onetemp)-Double.parseDouble(originallytemp))/Double.parseDouble(onexielv);
			num=num+oneshengwenshijian;
			x[1]=num;
			y[1]=Double.parseDouble(onetemp);
			
			editonebaowenshijian.setText(oneshijian);
			double onebaowenshijian=Double.parseDouble(oneshijian);
			num=num+onebaowenshijian;
			x[2]=num;
			y[2]=Double.parseDouble(onetemp);
			
			edittwotemp.setText(twotemp);
			edittwoxielv.setText(twoxielv);
			double twoshengwenshijian=(Double.parseDouble(twotemp)-Double.parseDouble(onetemp))/Double.parseDouble(twoxielv);
			num=num+twoshengwenshijian;
			x[3]=num;
			y[3]=Double.parseDouble(twotemp);
			
			edittwobaowenshijian.setText(twoshijian);
			double twobaowenshijian=Double.parseDouble(twoshijian);
			num=num+twobaowenshijian;
			x[4]=num;
			y[4]=Double.parseDouble(twotemp);
			
			editthreetemp.setText(threetemp);
			editthreexielv.setText(threexielv);
			double threelengqueshijian=(Double.parseDouble(twotemp)-Double.parseDouble(threetemp))/Double.parseDouble(threexielv);
			num=num+threelengqueshijian;
			x[5]=num;
			y[5]=Double.parseDouble(threetemp);
			
			double  chuiqishijian=(Double.parseDouble(editchuiqilengqueshijian.getText().toString()))/100;
			x[6]=num+chuiqishijian;
			y[6]=Double.parseDouble(originallytemp);
			
			list.add(x);
			list.add(y);
			
			
			return  list;
		}
		else{
			Toast.makeText(this, "请确保第2段温度最高，第1和第3段的温度值大于开始温度,所有时间的和不超过坐标轴最大值！！", Toast.LENGTH_LONG).show();
			return  list;
		}
				
		
	}
	//执行此方法首先要检查下温控仪的设置界面的各值是否为“”或NULL!
	static   String  getoutstring(String address,String shuju){
		String out=":0A10";
		out=out+address+"000102";
		String hexshuju=Integer.toHexString(Integer.parseInt(shuju)).toUpperCase();
		int  length=hexshuju.length();
		if(length==1){
			hexshuju="000"+hexshuju;
		}
		else if(length==2){
			hexshuju="00"+hexshuju;
		}
		else if(length==3){
			hexshuju="0"+hexshuju;
		}
		else if(length==4){
			hexshuju=hexshuju;

		}
		out=out+hexshuju;
		String checknumber=getchecknumber(out);
		out=out+checknumber+"\r\n";
				
		return  out;
	}
	
	//根据山武自动设备方法计算校验和！
	static  String  getchecknumber(String s){
    	String checknumber="";
    	//:0A0303E90002   :0A10238D0001020180
    	if(s.length()==13){
    		//int 相加  然后转成十六进制 字符串 取最后2位  然后 取反加1
    		int n1=Integer.parseInt(s.substring(1, 3),16);
    		int n2=Integer.parseInt(s.substring(3, 5),16);
    		int n3=Integer.parseInt(s.substring(5, 7),16);
    		int n4=Integer.parseInt(s.substring(7, 9),16);
    		int n5=Integer.parseInt(s.substring(9, 11),16);
    		int n6=Integer.parseInt(s.substring(11, 13),16);
    		
    		int  num=n1+n2+n3+n4+n5+n6;
    		String res=Integer.toHexString(num);
    		res= res.substring(res.length()-2, res.length());
    		int n=Integer.parseInt(res,16);
    		String zuihou=Integer.toHexString(~n+1);
    		checknumber=zuihou.substring(zuihou.length()-2, zuihou.length()).toUpperCase();
    		return  checknumber;
    	}
    	else  {
    		int n1=Integer.parseInt(s.substring(1, 3),16);
    		int n2=Integer.parseInt(s.substring(3, 5),16);
    		int n3=Integer.parseInt(s.substring(5, 7),16);
    		int n4=Integer.parseInt(s.substring(7, 9),16);
    		int n5=Integer.parseInt(s.substring(9, 11),16);
    		int n6=Integer.parseInt(s.substring(11, 13),16);
    		int n7=Integer.parseInt(s.substring(13, 15),16);
    		int n8=Integer.parseInt(s.substring(15, 17),16);
    		int n9=Integer.parseInt(s.substring(17, 19),16);
    		
    		int  num=n1+n2+n3+n4+n5+n6+n7+n8+n9;
    		String res=Integer.toHexString(num);
    		res= res.substring(res.length()-2, res.length());
    		int n=Integer.parseInt(res,16);
    		String zuihou=Integer.toHexString(~n+1);
    		checknumber=zuihou.substring(zuihou.length()-2, zuihou.length()).toUpperCase();
    		return  checknumber;
    	}
    	
    }	
	
	private  String  get4string(String str){
		String  s="";
	if(str.length()==4){
		return  s=str;
	}
	else  if(str.length()==3){
		s="0"+str;
		return  s;
		
	}
	else  if(str.length()==2){
		s="00"+str;
		return  s;
		
	}
	else  if(str.length()==1){
		s="000"+str;
		return  s;
		
	}
	else{
		return  s="0000";
	}
		

	
}
	
}
