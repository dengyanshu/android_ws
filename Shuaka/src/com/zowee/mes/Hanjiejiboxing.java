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
			//ѭ�����͵��¿�����ָ�
			if(serialport_2!=null){
			dataOutput(serialport_2, ":0A03238D000142\r\n");//pv  ����ֵ
			SystemClock.sleep(250);
			dataOutput(serialport_2, ":0A03238E000240\r\n");//sp Ŀ��ֵ
			super.handleMessage(msg);
			}
		}
	};
	@Override
	public void onDestroy() {
		// ����������ʱ�ص�Timer
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
		System.out.println("task==null�����:"+task==null);
		Log.i(TAG,"onPause---boxing");
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
//		super.TASKTYPE = TaskType.biaoqianhedui; // ��̨����̬���γ���
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
			Toast.makeText(this, "ע�ᴮ��ʧ��", Toast.LENGTH_SHORT).show();
		}
       
       series = new XYSeries("����ͼ");
       series2 = new XYSeries("����ͼ");

		// ����һ�����ݼ���ʵ����������ݼ�������������ͼ��
		mDataset = new XYMultipleSeriesDataset();

		// ���㼯��ӵ�������ݼ���
		mDataset.addSeries(series);
		mDataset.addSeries(series2);

		// ���¶������ߵ���ʽ�����Եȵȵ����ã�renderer�൱��һ��������ͼ������Ⱦ�ľ��
		int[] color = new int[]{Color.GREEN,Color.RED};
		PointStyle[] style =new  PointStyle[]{PointStyle.CIRCLE,PointStyle.DIAMOND};
		renderer = buildRenderer(color, style, true);

		// ���ú�ͼ�����ʽ
		setChartSettings(renderer, "X", "Y", 0, 100, 0, 400, Color.WHITE,
				Color.WHITE);

		// ����ͼ��
		chart = ChartFactory.getLineChartView(this, mDataset, renderer);

		// ��ͼ����ӵ�������ȥ
		linearlayout.addView(chart, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

	}
	
	protected XYMultipleSeriesRenderer buildRenderer(int[] color,
			PointStyle[] style, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		// ����ͼ�������߱������ʽ��������ɫ����Ĵ�С�Լ��ߵĴ�ϸ��
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
		// �йض�ͼ�����Ⱦ�ɲο�api�ĵ�
		renderer.setChartTitle("�¶Ȳ���ͼ");
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
		renderer.setYTitle("�¶�");
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float)5);
		renderer.setShowLegend(false);
	}
	
	private void updateChart(int y) {

		// ���ú���һ����Ҫ���ӵĽڵ�
		addX = 0;
		addY = y;
		// �Ƴ����ݼ��оɵĵ㼯
		mDataset.removeSeries(series);
		// �жϵ�ǰ�㼯�е����ж��ٵ㣬��Ϊ��Ļ�ܹ�ֻ������100�������Ե���������100ʱ��������Զ��100
		int length = series.getItemCount();
		if (length > 100) {
			length = 100;
		}
		// ���ɵĵ㼯��x��y����ֵȡ��������backup�У����ҽ�x��ֵ��1�������������ƽ�Ƶ�Ч��
		for (int i = 0; i < length; i++) {
			xv[i] = (int) series.getX(i) + 1;
			yv[i] = (int) series.getY(i);
		}
		// �㼯����գ�Ϊ�������µĵ㼯��׼��
		series.clear();

		// ���²����ĵ����ȼ��뵽�㼯�У�Ȼ����ѭ�����н�����任���һϵ�е㶼���¼��뵽�㼯��
		// �����������һ�°�˳��ߵ�������ʲôЧ������������ѭ���壬������²����ĵ�
		series.add(addX, addY);
		for (int k = 0; k < length; k++) {
			series.add(xv[k], yv[k]);
		}
		// �����ݼ�������µĵ㼯
		mDataset.addSeries(series);

		// ��ͼ���£�û����һ�������߲�����ֶ�̬
		// ����ڷ�UI���߳��У���Ҫ����postInvalidate()������ο�api
		chart.invalidate();
	}
   
	
	private void updateChart2(int y) {

		// ���ú���һ����Ҫ���ӵĽڵ�
		addX = 0;
		addY = y;
		// �Ƴ����ݼ��оɵĵ㼯
		mDataset.removeSeries(series2);
		// �жϵ�ǰ�㼯�е����ж��ٵ㣬��Ϊ��Ļ�ܹ�ֻ������100�������Ե���������100ʱ��������Զ��100
		int length = series2.getItemCount();
		if (length > 100) {
			length = 100;
		}
		// ���ɵĵ㼯��x��y����ֵȡ��������backup�У����ҽ�x��ֵ��1�������������ƽ�Ƶ�Ч��
		for (int i = 0; i < length; i++) {
			xv[i] = (int) series2.getX(i) + 1;
			yv[i] = (int) series2.getY(i);
		}
		// �㼯����գ�Ϊ�������µĵ㼯��׼��
		series2.clear();

		// ���²����ĵ����ȼ��뵽�㼯�У�Ȼ����ѭ�����н�����任���һϵ�е㶼���¼��뵽�㼯��
		// �����������һ�°�˳��ߵ�������ʲôЧ������������ѭ���壬������²����ĵ�
		series2.add(addX, addY);
		for (int k = 0; k < length; k++) {
			series2.add(xv[k], yv[k]);
		}
		// �����ݼ�������µĵ㼯
		mDataset.addSeries(series2);

		// ��ͼ���£�û����һ�������߲�����ֶ�̬
		// ����ڷ�UI���߳��У���Ҫ����postInvalidate()������ο�api
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
 				
 				if ("/dev/ttyS2".equals(who)) {
 					
 					if (true) {
 						String msg=readMessage();
 						if(msg.matches(":0A030[2,4][A-F0-9]{4,}\r\n")){
 							if((msg.charAt(6)+"").equals("2")){
 	 							//��һ���ֽڶ�ȡ��������ֵ
 	 							msg=msg.substring(7, 11);
 	 	 						Log.i(TAG,"�յ�����Ϣ��:"+msg);
 	 	 						Message.obtain(mHandler, 1, msg).sendToTarget();
 	 	 						//Toast.makeText(Tjzimipowertest.this, who+"������������1��"+msg, Toast.LENGTH_LONG).show();
 	 						}
 	 						
 	 						else{
 	 							msg=msg.substring(7, 11);
 	 	 						Log.i(TAG,"�յ�����Ϣ��:"+msg);
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
			Log.i(TAG,portName+"д��������:"+out );
			//Toast.makeText(this, portName+"����д���ݳɹ���д��������"+out, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "����д����ʧ��������ִ�У�", Toast.LENGTH_SHORT).show();
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
