package com.zowee.mes;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TooManyListenersException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.InstrumentModel;
import com.zowee.mes.model.TjchuhuoscanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.SoundEffectPlayService;

public class  testscan extends CommonActivity implements  OnClickListener
		 {
	
	
   private  Button    scan_btn;
   private  TextView  tv;
   
   
   private boolean mRestoreGpioStateHandled = false;
   private  SerialPort  serialPort;
   private InputStream dataInput;
   private OutputStream dataOutput;
 
	private static final String TAG = "testscan";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_testscan);
		init();
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
								stopService(new Intent(testscan.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}

	public void init() {
		
		scan_btn=(Button) findViewById(R.id.testscan_button1);
		scan_btn.setOnClickListener(this);
		tv=(TextView) findViewById(R.id.testscan_tv);
	}
	
	private Handler mHandler = new Handler()
	  {
	    public void handleMessage(Message msg)
	    {
	      tv.setText(msg.obj.toString());
	    }
	  };
	  
	  private  Runnable mRestoreGpioState = new Runnable()
	  {
		public void run()
	    {
	      if (!mRestoreGpioStateHandled)
	      {
	       mRestoreGpioStateHandled = true;
	       setScanGpioState(false);
	      }
	    }
	  };
	  

//	  private void laserScaninthisactivity()
//	  {
//	    setScanGpioState(false);
//	    try
//	    {
//	      Thread.sleep(10L);
//	      setScanGpioState(true);
//	      mRestoreGpioStateHandled = false;
//	      mHandler.postDelayed(mRestoreGpioState, 3000L);
//	      return;
//	    }
//	    catch (Exception localException)
//	    {
//	      while (true)
//	      {
//	        localException.printStackTrace();
//	        Log.i("ScanTest", localException.toString());
//	      }
//	    }
//	  }

	  private void registerPort()
	  {
	    try
	    {
//	      this.serialPort = super.serialPortOper.getSerialPort();
//	      this.dataInput = this.serialPort.getInputStream();
//	      this.dataOutput = this.serialPort.getOutputStream();
//	      this.serialPort.addEventListener(new SerialEventsListener());
//	      this.serialPort.notifyOnDataAvailable(true);
//	      this.serialPort.setSerialPortParams(9600, 8, 1, 0);
//	      this.serialPort.setFlowControlMode(0);
	      return;
	    }
	    catch (Exception localIOException)
	    {
	      Log.e("ScanTest", "I/O Exception " + localIOException.getMessage());
	      return;
	    }
	    
	  }

	  private void setScanGpioState(boolean paramBoolean)
	  {
	    //if (this.cmdOutput != null);
	    try
	    {
	    	FileWriter  fw= new FileWriter("/dev/scan");
	      if (paramBoolean);
	      for (int i = 48; ; i = 49)
	      {
	    	  fw.write(i);
	       fw.flush();
	        return;
	      }
	    }
	    catch (IOException localIOException)
	    {
	      localIOException.printStackTrace();
	    }
	  }

	  private void unRegisterPort()
	  {
	    if (this.dataInput != null);
	    try
	    {
	      this.dataInput.close();
	      this.dataInput = null;
	      if (this.dataOutput == null);
	    }
	    catch (IOException localIOException2)
	    {
	      try
	      {
	        this.dataOutput.close();
	        this.dataOutput = null;
	        //if (this.cmdOutput == null);
	      }
	      catch (IOException localIOException21)
	      {
	        while (true)
	          {
	            //this.cmdOutput.close();
	            if (this.serialPort != null)
	            {
	              this.serialPort.removeEventListener();
	              this.serialPort.close();
	            }
	            this.serialPort = null;
	            return;
	            //localIOException3 = localIOException3;
	            //Log.e("ScanTest", "unRegisterPort Input", localIOException3);
	          }
	          //localIOException21 = localIOException21;
	         // Log.e("ScanTest", "unRegisterPort dataOutput", localIOException21);
	      }
	    }
	  }

	  public void onClick(View paramView)
	  {
	    if (paramView.getId() == R.id.testscan_button1)
	    {
	      this.tv.setText("");
	      ttyScan();
	      laserScan();
	      return;
	    }
	    setScanGpioState(false);
	    unRegisterPort();
	    Process.killProcess(Process.myPid());
	  }

	 

	 

	  protected void onPause()
	  {
	    super.onPause();
	    unRegisterPort();
	  }

	  protected void onResume()
	  {
	    super.onResume();
	    registerPort();
	  }

	  public void ttyScan()
	  {
	    if (this.dataOutput == null)
	    {
	      Log.e("ScanTest", "dataOutput is null!");
	      
	      return;
	    }
	    byte[] arrayOfByte = { 85 };
	    try
	    {
	      this.dataOutput.write(arrayOfByte);
	      this.dataOutput.flush();
	      return;
	    }
	    catch (UnsupportedEncodingException localUnsupportedEncodingException)
	    {
	      localUnsupportedEncodingException.printStackTrace();
	      return;
	    }
	    catch (IOException localIOException)
	    {
	      localIOException.printStackTrace();
	    }
	  }

	  private final class SerialEventsListener implements SerialPortEventListener
	  {
	    public void serialEvent(SerialPortEvent paramSerialPortEvent)
	    {
	      byte[] arrayOfByte = null;
	      Object localObject = null;
	      if (paramSerialPortEvent.getEventType() == 1)
	      {
	        arrayOfByte = new byte[512];
	        localObject = "";
	      }
	      try
	      {
	        while (true)
	        {
	          if (dataInput.available() <= 0)
	          {
	            Log.d("ScanTest", "read data:" + (String)localObject);
	            Message.obtain(mHandler, 1, localObject).sendToTarget();
	            if (!mRestoreGpioStateHandled)
	            {
	             mHandler.removeCallbacks(mRestoreGpioState);
	              setScanGpioState(false);
	            }
	            return;
	          }
	          String str1 = new String(arrayOfByte, 0, dataInput.read(arrayOfByte), "UTF-8");
	          String str2 = localObject + str1;
	          localObject = str2;
	        }
	      }
	      catch (IOException localIOException)
	      {
	        while (true)
	          Log.e("ScanTest", "Error Reading from serial port", localIOException);
	      }
	    }
	  }
}
