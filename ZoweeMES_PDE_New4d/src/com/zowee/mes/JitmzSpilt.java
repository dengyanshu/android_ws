package com.zowee.mes;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtChaolingModel;
import com.zowee.mes.model.SmtPeoplescanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.service.BluetoothService;
import com.zowee.mes.service.SoundEffectPlayService;

public class JitmzSpilt extends CommonActivity 
		 {
	
	// private static final String TAG = "BTPrinter";
	 private static final boolean D = true;

	 // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    
    private TextView mTitle;
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private com.zowee.mes.service.BluetoothService mService = null;
	////////////////////////////////////////////////
	private  String resourceid;
	private  String resourcename=MyApplication.getAppOwner().toString()
			.trim();
	private  String  useid=MyApplication.getMseUser().getUserId();
	private  String   usename=MyApplication.getMseUser().getUserName();
	
	
	

  private  String jb;

   private  EditText  mzEdit;

   private  EditText  qtyEdit;
 
   private  EditText  editscan;
   
   private GetMOnameModel GetMonamemodel; // 任务处理类
   private SmtPeoplescanModel smtPeoplescanModel; // 任务处理类
   private static final String TAG = "JitmzSpilt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_jit_mzsplit);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.bt_custom_title);

        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText("JIT物料拆分");
        mTitle = (TextView) findViewById(R.id.title_right_text);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
	}
    
	@SuppressLint("NewApi") 
	@Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the session
        } else {
            if (mService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
       
        super.commonActivity = this;
		super.TASKTYPE = TaskType.smtipqcscan; // 后台服务静态整形常量
		super.init();
        
		GetMonamemodel=new GetMOnameModel();
		smtPeoplescanModel = new SmtPeoplescanModel();
		
		mzEdit=(EditText) findViewById(R.id.jitmzspilt_mz);
		qtyEdit=(EditText) findViewById(R.id.jitmzspilt_qty);
		editscan=(EditText) findViewById(R.id.jitmzspilt_editscan);
		
		/**
		 * 以前jit物料拆分脚本
		jb="SIZE 51 mm,71 mm\n"+
"GAP 2 mm\n"+
"OFFSET 0 mm\n"+
"DIRECTION 1\n"+
"CODE PAGE 437\n"+
"DENSITY 8\n"+
"SET PEEL OFF\n"+
"CLS\n"+
"TEXT 350,10,\"2\",90,1,1,\"$StockUserCode$\"\n"+
"TEXT 380,100,\"TSS24.BF2\",90,2,1,\"ZOWEE物料标识卡\"\n"+
"TEXT 350,420,\"TSS24.BF2\",90,2,1,\"$Month$\"\n"+
"TEXT 330,10,\"TSS24.BF2\",90,1,1,\"库位:\"\n"+
"TEXT 330,80,\"3\",90,1,1,\"$Stock$\"\n"+
"TEXT 300,330,\"TSS24.BF2\",90,1,1,\"$FactoryCode$\"\n"+
"TEXT 300,10,\"TSS24.BF2\",90,1,1,\"料号:\"\n"+
"TEXT 300,80,\"3\",90,1,1,\"$ProductName$\"\n"+
"TEXT 270,10,\"3\",90,1,1,\"PO:$PoName$\"\n"+
"TEXT 270,330,\"TSS24.BF2\",90,1,1,\"数量:\"\n"+
"TEXT 270,390,\"3\",90,1,1,\"$Qty$\"\n"+
"TEXT 240,10,\"TSS24.BF2\",90,1,1,\"供应商：$VendorName$\"\n"+
"TEXT 210,10,\"3\",90,1,1,\"D/C:$DateCode$\"\n"+
"TEXT 210,300,\"3\",90,1,1,\"L/C:$LotCode$\"\n"+
"BARCODE 180,90,\"128\",60,1,90,2,1,\"$LotSN$\"\n"+
"TEXT 120,10,\"3\",90,1,1,\"S/N:\"\n"+
"TEXT 90,10,\"TSS24.BF2\",90,1,1,\"品名/规格：\"\n"+
"TEXT 90,140,\"TSS24.BF2\",90,1,1,\"$ProductDesc1$\"\n"+
"TEXT 60,20,\"TSS24.BF2\",90,1,1,\"$ProductDesc2$\"\n"+
"TEXT 30,20,\"TSS24.BF2\",90,1,1,\"$ProductDesc3$\"\n"+
"PRINT 1,1\n";
*/     
		
		jb="^XA \n"+
"^MMT\n"+
"^PW591\n"+
"^LL0200\n"+
"^LS0\n"+
"^BY3,3,41^FT422,148^BCI,,Y,N\n"+
"^FD>12345678^FS\n"+
"^FT430,41^A0I,20,19^FH\\^FDdata1^FS\n"+
"^FT385,18^A0I,14,14^FH\\^FDdata2^FS\n"+
"^PQ1,0,1,Y^XZ\n";
				

        System.out.println("jiaoben="+jb);
        // Initialize the BluetoothService to perform bluetooth connections
        mService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        
        GetResourceId();
    }
    
	private void GetResourceId()
	{
		
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		
		Task task = new Task(this, TaskType.GetResourceId, resourcename);	
		GetMonamemodel.GetResourceId(task);

	}
    
    //确定事件处理
    public void clickme(View v){
    	AlertDialog.Builder builder= new AlertDialog.Builder(this);
    	builder	.setTitle("JIT物料拆分");
    	builder.setIcon(R.drawable.check);
    	builder.setMessage("确定需要拆分物料吗，请检查拆分数量是否正确？？？");
		builder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0,
							int arg1) {
						String[] paras=new String[11];
						paras[0]=resourceid;
						paras[1]=resourcename;
						paras[2]=useid;
						paras[3]=usename;
						paras[4]=JitmzSpilt.this.mzEdit.getText().toString().trim();
						paras[5]=JitmzSpilt.this.qtyEdit.getText().toString().trim();
				    
						 if (mService.getState() != BluetoothService.STATE_CONNECTED) {
					            Toast.makeText(JitmzSpilt.this, "未连接蓝牙打印机.....", Toast.LENGTH_SHORT).show();
					            return;
					     }
				    	
						Task task = new Task(JitmzSpilt.this, 200,paras);
						smtPeoplescanModel.jit_split(task);
					}
				});
		builder.setNegativeButton("取消", null).show();
    	
    	

    }
    
    
    //测试脚本打印机能否识别
    public void clicktest(View v){
         String message = jb;
         System.out.println("jb="+jb);
         //fontGrayscaleSet(4);
         sendMessage(message);
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
								stopService(new Intent(JitmzSpilt.this,
										BackgroundService.class));
								finish();
							}
						})
				.setNegativeButton(getString(android.R.string.no), null).show();
	}



	/*
	 * 刷新UI界面
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Task task) {
		super.refresh(task);
		HashMap<String, String> getdata;
		switch (task.getTaskType()) {	
		case TaskType.GetResourceId:
			resourceid = "";
			super.closeProDia();
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			resourceid = getresult.get(0).get("ResourceId");
			if(resourceid.isEmpty())	 
				logSysDetails("未能获取到资源ID，请检查资源名是否设置正确","shibai");
			else{
				logSysDetails("成功获取到该设备的资源ID:"+resourceid,"成功");
			}
		break;
		

			
	

		case 200:
			super.closeProDia();
			if (null != task.getTaskResult()) {
				getdata = (HashMap<String, String>) task.getTaskResult();
				Log.i(TAG,"getdata="+getdata);
				String value=getdata.get("I_ReturnValue");
				
				Log.d(TAG, "task的结果数据是：" + getdata);
				if (getdata.get("Error") == null) {
					if(Integer.parseInt(value)==0){
						String scantext = "成功！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						SoundEffectPlayService
								.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
						
						 String msg=getStr(jb,getdata,1);
				         fontGrayscaleSet(4);
				         sendMessage(msg);
				         Log.i(TAG, "msg1="+msg);
				         SystemClock.sleep(100);
				          msg=getStr(jb,getdata,2);
				          fontGrayscaleSet(4);
				          sendMessage(msg);
				          Log.i(TAG, "msg2="+msg);
				          
				          mzEdit.setText("");
				          qtyEdit.setText("");
				          mzEdit.requestFocus();
				          
						
					}
					else{
						SoundEffectPlayService
						.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.ERROR_MUSIC);
						String scantext ="失败！"+getdata.get("I_ReturnMessage");
						logSysDetails(scantext, "成功");
						
						
						//test  shibai dayin
						
//						 String msg=getStr(jb,getdata,1);
//				         fontGrayscaleSet(4);
//				         sendMessage(msg);
//				         Log.i(TAG, "msg1="+msg);
//				         SystemClock.sleep(100);
//				          msg=getStr(jb,getdata,2);
//				          fontGrayscaleSet(4);
//				          sendMessage(msg);
//				          Log.i(TAG, "msg2="+msg);
					}
					

				} else {
					logSysDetails(
							"在MES获取信息为空或者解析结果为空，请检查再试!"
									+ getdata.get("Error"), "成功");
				}
				closeProDia();
			} else {
				logSysDetails("提交MES失败请检查网络，请检查输入的条码", "成功");
			}
			break;
		}
	}
    
	
	private  String getStr(String msg,Map<String,String> getdata,int order){
		
		String Stock=getdata.get("Stock");
		if(Stock==null) Stock="$Stock$";
		String StockUserCode=getdata.get("StockUserCode");
		if(StockUserCode==null) StockUserCode="$StockUserCode$";
		String ProductName=getdata.get("ProductName");
		if(ProductName==null) StockUserCode="$ProductName$";
		String VendorName=getdata.get("VendorName");
		if(VendorName==null) StockUserCode="$VendorName$";
		String ProductDesc=getdata.get("ProductDesc");
		if(ProductDesc==null) StockUserCode="$ProductDesc$";
		String PoName=getdata.get("PoName");
		if(PoName==null) StockUserCode="$PoName$";
		String DateCode=getdata.get("DateCode");
		if(DateCode==null) StockUserCode="$DateCode$";
		String LotCode=getdata.get("LotCode");
		if(LotCode==null) StockUserCode="$LotCode$";
		String FactoryCode=getdata.get("FactoryCode");
		if(FactoryCode==null) StockUserCode="$FactoryCode$";
		String Month=getdata.get("Month");
		if(Month==null) StockUserCode="$Month$";
		String LotSN="";
		String Qty="";
		if(order==1){
			LotSN=getdata.get("LotSN1");
			 Qty=getdata.get("Qty1");
		}
		if(order==2){
			LotSN=getdata.get("LotSN2");
			 Qty=getdata.get("Qty2");
		}
		//[
		//{FactoryCode=第二事业部（智造）, 
		//VendorName=第一事业部(由华为专卖), 
		//LotSN2=MZ01168B0FZ500O3, 
		//DateCode=12D20160418, 
		//LotCode=1TZ529912634,
		//LotSN1=MZD3168GNBZY002J, 
		//ProductDesc=贴片电容/SMD陶瓷电容-6.3V-22000nF-+/-20%-X5R-0805 -环保-华为客供,
		//Qty2=1480, 
		//ProductName=K004-08070784, 
		//Month=8, Stock=SMT-B-01-175, 
		//PoName=K201608110025, Qty1=10}, 
		//{I_ReturnMessage=ServerMessage:Code:000017-010 批号【MZ01168B0FZ500O3】成功将数量10拆分至新批号【MZD3168GNBZY002J】中!, I_ReturnValue=0}]
		
		msg=msg.replace("$Stock$", Stock).replace("$StockUserCode$", StockUserCode).replace("$ProductName$", ProductName).replace("$VendorName$", VendorName)
				.replace("$PoName$", PoName).replace("$DateCode$", DateCode).replace("$LotCode$", LotCode).replace("$FactoryCode$", FactoryCode)
				.replace("$Month$", Month+"月").replace("$LotSN$", LotSN).replace("$Qty$", Qty);
		
		for(int n=1;n<=3;n++){
			if(ProductDesc.length()>=30){
				msg=msg.replace("$ProductDesc"+n+"$", ProductDesc.substring(0, 30));
				ProductDesc=ProductDesc.substring(30);
			}else{
				msg=msg.replace("$ProductDesc"+n+"$", ProductDesc.substring(0, ProductDesc.length()));
				if(n==1)msg=msg.replace("$ProductDesc2$", "").replace("$ProductDesc3$", "");
				if(n==2)msg=msg.replace("$ProductDesc3$", "");
				break;
			}
		}
		
		return msg;
	}
	
	  @Override
	    public synchronized void onPause() {
	        super.onPause();
	        if(D) Log.e(TAG, "- ON PAUSE -");
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	        if(D) Log.e(TAG, "-- ON STOP --");
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        // Stop the Bluetooth services
	        if (mService != null) mService.stop();
	        if(D) Log.e(TAG, "--- ON DESTROY ---");
	    }
	
	/**
     * Set font gray scale
     */    
    private void fontGrayscaleSet(int ucFontGrayscale)
    {
        // Check that we're actually connected before trying anything
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ucFontGrayscale<1)
        	ucFontGrayscale = 4;
        if(ucFontGrayscale>8)
        	ucFontGrayscale = 8;
        byte[] send = new byte[3];//ESC m n
        send[0] = 0x1B;
        send[1] = 0x6D;
    	send[2] = (byte) ucFontGrayscale;
    	mService.write(send);	
    }
    /**
     * Sends a message.
     * @param message  A string of text to send.
     * 
     */
    private void sendMessage(String message){
        // Check that we're actually connected before trying anything
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "未连接", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send;            
            try{
            	//send = new String(message.getBytes(),"UTF-8").getBytes("UTF-8");
            	send = message.getBytes("gbk");
            	
            }
            catch(UnsupportedEncodingException e)
            { 
            	System.out.println("编码异常");
            	send = message.getBytes();
            }
            mService.write(send);
//
//            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
        }
    }
	
	
	 private  String data="";
	    // The Handler that gets information back from the BluetoothService
	    private final Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case MESSAGE_STATE_CHANGE:
	                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                switch (msg.arg1) {
	                case BluetoothService.STATE_CONNECTED:
	                    mTitle.setText("已连接");
	                    mTitle.append(mConnectedDeviceName);
	                    break;
	                case BluetoothService.STATE_CONNECTING:
	                    mTitle.setText("连接中...");
	                    break;
	                case BluetoothService.STATE_LISTEN:
	                case BluetoothService.STATE_NONE:
	                    mTitle.setText("未连接");
	                    break;
	                }
	                break;
	            case MESSAGE_WRITE:
	                //byte[] writeBuf = (byte[]) msg.obj;
	                // construct a string from the buffer
	                //String writeMessage = new String(writeBuf);
	                break;
	            case MESSAGE_READ:
	            	byte[] readBuf = (byte[]) msg.obj;
	                // construct a string from the valid bytes in the buffer
	                String readMessage = new String(readBuf, 0, msg.arg1);
	                data+=readMessage.trim();
	                byte bt0='\r';
	                byte bt1='\n';
	                if(msg.arg1>1&&readBuf[msg.arg1-2]==bt0&&readBuf[msg.arg1-1]==bt1)
	                {
	                   //mOutEditText.setText(data);
	                   data="";
	                   
	                }
	                break;
	            case MESSAGE_DEVICE_NAME:
	                // save the connected device's name
	                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
	                Toast.makeText(getApplicationContext(), "Connected to "
	                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
	                break;
	            case MESSAGE_TOAST:
	                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
	                               Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };
	

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
	
	 @SuppressLint("NewApi") 
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if(D) Log.d(TAG, "onActivityResult " + resultCode);
	        switch (requestCode) {
	        case REQUEST_CONNECT_DEVICE:
	            // When DeviceListActivity returns with a device to connect
	            if (resultCode == Activity.RESULT_OK) {
	                // Get the device MAC address
	                String address = data.getExtras()
	                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	                // Get the BLuetoothDevice object
	                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
	                // Attempt to connect to the device
	                mService.connect(device);
	            }
	            break;
	        case REQUEST_ENABLE_BT:
	            // When the request to enable Bluetooth returns
	            if (resultCode == Activity.RESULT_OK) {
	                // Bluetooth is now enabled, so set up a session
	                setupChat();
	            } else {
	                // User did not enable Bluetooth or an error occured
	                Log.d(TAG, "BT not enabled");
	                Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_SHORT).show();
	                finish();
	            }
	        }
	    }
	
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.bt_option_menu, menu);
	        return true;
	    }
	
	 
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case R.id.scan:
	            // Launch the DeviceListActivity to see devices and do scan
	            Intent serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	            return true;
	        case R.id.disconnect:
	            // disconnect
	        	mService.stop();
	            return true;
	        }
	        return false;
	    }
}
