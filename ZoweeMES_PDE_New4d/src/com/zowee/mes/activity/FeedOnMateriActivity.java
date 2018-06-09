package com.zowee.mes.activity;

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
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.AlteredCharSequence;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zowee.mes.DeviceListActivity;
import com.zowee.mes.JitmzSpilt;
import com.zowee.mes.R;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.FeedOnMateriModel;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.SmtPeoplescanModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.BluetoothService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;
import com.zowee.mes.utils.StringUtils;
/**
 * @author Administrator
 * @description 上料对料
 */
public class FeedOnMateriActivity extends CommonActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener
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
	    private  String jb;
	    // Name of the connected device
	    private String mConnectedDeviceName = null;
	    // String buffer for outgoing messages
	    private StringBuffer mOutStringBuffer;
	    // Local Bluetooth adapter
	    private BluetoothAdapter mBluetoothAdapter = null;
	    // Member object for the services
	    private com.zowee.mes.service.BluetoothService mService = null;
		////////////////////////////////////////////////
	
	
	
    private EditText  edtFeida;
	private EditText edtLotNumber ;
	private EditText edtMaterialSN;
	private Spinner spinMoNames;//工单
	private EditText edtLineNo;//产线
	private EditText edtStationNo;//机台
	private View btnGroups;
	private Button btnEnsure;//确认键
	private EditText edtSysDatails;
	private FeedOnMateriModel model;
	private List<HashMap<String,String>> moNames;//工单下拉框的数据源
	private static final  String MONAME = "MOName";
	private GetMOnameModel GetMonamemodel;
	//private HashMap<String,String> adaSelItem;//工单下拉框被选择的项
	private Button btnClearStalot;
	private Button btnCancel;
	private Button btnScan;
	private Button btnRef;
	private Button btnBluetoothtest;
	private String ResourceId="" ;
	private String staLotSN = "";//站位条码
	private String moName;//订单名字
	private String smtMountId;// 
	private String DeviceTypeID="";// 
	
	

	private   EditText  moselectedit;
	
	
	
	private   boolean  ischeck_fristmz=false;
	private   boolean  isalert=false;
	private   String isalert_collect="";
	@Override
	public void init()
	{
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		 if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.feeding_onmateri);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.bt_custom_title);

        // Set up the custom title
      mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText("SMT上料");
        mTitle = (TextView) findViewById(R.id.title_right_text);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
		init();
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
        if(null==moNames)
			getMONames();

     }

	    private void setupChat() {
	    	
	    	model = new FeedOnMateriModel();
			GetMonamemodel = new GetMOnameModel();
			super.commonActivity = this;
			super.TASKTYPE = TaskType.FeedOnMaterial_SCAN;
			super.init();
			edtFeida = (EditText)this.findViewById(R.id.edt_feida);
			edtLotNumber = (EditText)this.findViewById(R.id.edt_slotScan);
			edtMaterialSN = (EditText)this.findViewById(R.id.edt_materialScan);
			spinMoNames = (Spinner)this.findViewById(R.id.spin_workorder);
			edtLineNo = (EditText)this.findViewById(R.id.edt_productline);
			edtStationNo = (EditText)this.findViewById(R.id.edt_machine);

			btnGroups = this.findViewById(R.id.btnGroups);
			btnEnsure = (Button)btnGroups.findViewById(R.id.btn_ensure);
			btnCancel = (Button)btnGroups.findViewById(R.id.btn_cancel);
			btnScan = (Button)btnGroups.findViewById(R.id.btn_scan);
			btnRef = (Button)btnGroups.findViewById(R.id.btn_refreshData);

			btnEnsure.setVisibility(View.GONE);
			btnRef.setVisibility(View.GONE);
			btnScan.setVisibility(View.GONE);
			btnCancel.setVisibility(View.GONE);

			btnClearStalot = (Button)this.findViewById(R.id.btn_cancel_stalotsn);
			edtSysDatails = (EditText)this.findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
	        
			
			 moselectedit=(EditText) findViewById(R.id.feeding_onmateri_moselectedit);
			 moselectedit.setOnClickListener(this);
			
			
			 
			edtLotNumber.requestFocus();
			edtLotNumber.setOnClickListener(this);
			edtMaterialSN.setOnClickListener(this);
			btnClearStalot.setOnClickListener(this);
			btnScan.setOnClickListener(this);
			btnCancel.setOnClickListener(this);	 
			spinMoNames.setOnItemSelectedListener(this);	

			edtSysDatails.setText(	"说明: \n ");
			edtSysDatails.append("	\'清空\'：清空选择的工单号、产线与机台号.\n");
			edtSysDatails.append("	\'清除\'：清空槽位编号与物料编号.\n");
			edtSysDatails.append("	清空工单号后需要重新选择工单号.\n");	
			edtSysDatails.append("	槽位编号与物料编号字体绿色表示上料成功 .\n ");
			edtSysDatails.append("	槽位编号与物料编号字体紫色表示操作重复、上料正确.\n ");
			//下面2条语句是注释的
			//GetResourceId();  // changed by ybj 2014-02-17  SMT机台号为资源名称 
			//GetDeviceTypeId(MyApplication.getAppOwner().toString()); 修改资源ID，资源类型方式
	    	
	        Log.d(TAG, "setupChat()");
	       	   	        				
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
			
	      
	       
	        
			jb="^XA\n"+
					"^MMT\n"+
					"^PW480\n"+
					"^LL0160\n"+
					"^LS0\n"+
					"^CI28\n"+
					"^FO18,5^GB447,148,1^FS\n"+
			        "^FO19,126^GB444,0,1^FS\n"+
			        "^FO20,72^GB445,0,2^FS\n"+
			        "^FO19,99^GB445,0,1^FS\n"+
			        "^FT295,133^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDZhanWei^FS\n"+
			        "^FO241,74^GB0,24,1^FS\n"+
			        "^FO102,100^GB0,51,1^FS\n"+
			        "^FO302,128^GB0,23,1^FS\n"+
			        "^FT98,134^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDA/B^FS\n"+
			        "^FT460,22^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDGuige3^FS\n"+
			        "^FT461,38^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDGuige2^FS\n"+
			        "^FT460,54^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDGuige1^FS\n"+
			        "^FT460,134^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDGongDan^FS\n"+
			        "^FT459,80^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDTime^FS\n"+
			        "^FT238,80^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDOrderNo^FS\n"+
			        "^FT97,107^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDQTY^FS\n"+
			        "^FT460,106^A@I,14,14,FZLTCXHJW.TTF^FH\\^FDLiaoHao^FS\n"+
			        "^PQ1,0,1,Y^XZ\n";
			
			
			

					

	        System.out.println("jiaoben="+jb);
	        // Initialize the BluetoothService to perform bluetooth connections
	        mService = new BluetoothService(this, mHandler);

	        // Initialize the buffer for outgoing messages
	        mOutStringBuffer = new StringBuffer("");
	        
	    }
	
	
	private void GetResourceId(String station)
	{
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		logDetails(edtSysDatails, "机台号：" + station );
		if(station.trim().length() != 4){

			logDetails(edtSysDatails, "机台号不正确"  );
			return;
		}

		Task task = new Task(this, TaskType.GetResourceId, station);	
		GetMonamemodel.GetResourceId(task);	
	}
	/*
	 * 
	 * get fd
	 * if  exist  (where moname='' and slot='') show in this UI
	 * else  need  manual to input
	 * 
	 * **/
	private void GetFD(String[] paras)
	{
		super.progressDialog.setMessage("Get resource ID");
		super.showProDia();	 
		

		Task task = new Task(this, 1008611, paras);	
		GetMonamemodel.GetFD(task);	
	}
	
	@Override
	public void refresh(Task task) 
	{
		super.refresh(task);
		switch(task.getTaskType())
		{
		case TaskType.FeedOnMaterial_SCAN:
			//Log.i(TAG, "COME IN");
			if(super.isNull) return; 
			String scanRes = task.getTaskResult().toString().trim();		 
			analyseScanneddataAndExecute(scanRes);
			break;
		case TaskType.FeedOnMaterial_GETMONames:
			super.closeProDia();
			this.edtLotNumber.requestFocus();
			if(super.isNull|| 10 > ((List<HashMap<String,String>>)task.getTaskResult()).size())
			{
				clearWidget(spinMoNames, null);
				if(null!=moNames)moNames.clear();
				moName = "";
				return;
			}
			moNames = (List<HashMap<String,String>>)task.getTaskResult();
			FeedOnMateriAdapter adapter = new FeedOnMateriAdapter(this, moNames);
			spinMoNames.setAdapter(adapter);
			break;
		
			
		case TaskType.FeedOnMaterial_GETMONames2:
			super.closeProDia();
			this.edtLotNumber.requestFocus();
			moNames = (List<HashMap<String,String>>)task.getTaskResult();
//			if(super.isNull|| 10 > ((List<HashMap<String,String>>)task.getTaskResult()).size())
//			{
//				clearWidget(spinMoNames, null);
//				if(null!=moNames)moNames.clear();
//				moName = "";
//				return;
//			}
			
			if(moNames!=null){
				FeedOnMateriAdapter adapter2 = new FeedOnMateriAdapter(this, moNames);
				spinMoNames.setAdapter(adapter2);
			}
			else{
				
				List<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
				HashMap<String,String> map=new HashMap<String,String>();
				map.put("MOName", "不存在");
				list.add(map);
				moNames=list;
				FeedOnMateriAdapter adapter2 = new FeedOnMateriAdapter(this, moNames);
				spinMoNames.setAdapter(adapter2);
				
			}
			
			
			break;
			
		//获取槽位号	
		case TaskType.smtfeedgetslot:
			super.closeProDia();
			HashMap<String, String> getdata;
			getdata = new HashMap<String, String>();
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				logDetails(this.edtSysDatails,"物料条码再系统中查询，无MES返回信息。");
				edtMaterialSN.setText("");
				edtMaterialSN.requestFocus();
				return;
			}
			getdata = (HashMap<String,String>)task.getTaskResult();
			if( getdata.containsKey("StationNO")&&getdata.containsKey("SLotNO"))
			{
				this.edtMaterialSN.setTextColor(Color.BLUE);
				this.edtLotNumber.setTextColor(Color.BLUE);
				String station=getdata.get("StationNO");
				String slot=getdata.get("SLotNO");
				String ma=getdata.get("LotSN");
				edtLotNumber.setText(station+slot);
				edtMaterialSN.setText("");
				edtMaterialSN.requestFocus();
			}
			else
			{
				logDetails(edtSysDatails, "该物料条码未找到对应的产线机台的历史记录，请核对！");
				edtMaterialSN.selectAll();
				edtMaterialSN.requestFocus();
				
			}	

			edtMaterialSN.selectAll();
			edtMaterialSN.requestFocus();
			
			
		break;
		//add alert.buildler for hw_mz	
		case TaskType.FeedOnMaterial:
			HashMap<String, String> getdata1;
			getdata1 = new HashMap<String, String>();
			super.closeProDia();
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				logDetails(this.edtSysDatails,"无MES返回信息。");
				clearLineAndStation();
				return;
			}
			getdata1 = (HashMap<String,String>)task.getTaskResult();
			
			if(Integer.parseInt(getdata1.get("result").toString()) == -2){
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setTitle("华为A/B料且与前次供应商不同，请核对?");
				builder.setPositiveButton("取消核对",null);
				builder.setNegativeButton("核对OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						String[] paras2 = new String[]{ ResourceId ,moName,edtLotNumber.getText().toString(),edtMaterialSN.getText().toString(),edtFeida.getText().toString()};
						Task task = new Task(FeedOnMateriActivity.this, TaskType.FeedOnMaterial,paras2);
						progressDialog.setMessage(getString(R.string.TransationData));
						showProDia();
						model.feedOnMaterial_reset_needlert(task);
					}
				});
				builder.show();
			}
			
			else{
				ischeck_fristmz=false;
				isalert=false;
				isalert_collect="";
				if( Integer.parseInt(getdata1.get("result").toString()) == 0)
				{
					
					this.edtMaterialSN.setTextColor(Color.GREEN);
					this.edtLotNumber.setTextColor(Color.GREEN);
					logDetails(edtSysDatails,"Success_Msg:"+ getdata1.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n");
					
					/*
					 * 屏蔽蓝牙20180511
					 * //蓝牙打印去后台获取打印信息
					 if (mService.getState() != BluetoothService.STATE_CONNECTED) {
				           Toast.makeText(FeedOnMateriActivity.this, "未连接蓝牙打印机.....", Toast.LENGTH_SHORT).show();
				     }else{
				    	   String[]   bt_paras = new String[]{
									MyApplication.getMseUser().getUserName() ,
									moName,
									edtLotNumber.getText().toString(),
									edtMaterialSN.getText().toString()};
							Task task_bt = new Task(FeedOnMateriActivity.this, 10000,bt_paras);
							progressDialog.setMessage(getString(R.string.TransationData));
							showProDia();
							model.feedOnMaterial_bt(task_bt);
				     }
					//蓝牙打印去后台获取打印信息
*/					
					SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
					clearLineAndStation();
				}
				else
				{
					ischeck_fristmz=false;
					isalert=false;
					isalert_collect="";
					if( getdata1.get("ReturnMsg").toString().contains("物料批号已上料")){
						this.edtMaterialSN.setTextColor(Color.MAGENTA);
						this.edtLotNumber.setTextColor(Color.MAGENTA);	
					}else{				
						this.edtMaterialSN.setTextColor(Color.RED);
						this.edtLotNumber.setTextColor(Color.RED);							
					}
					logDetails(edtSysDatails, getdata1.get("ReturnMsg").toString());
				}
				edtFeida.setText("");
	            edtLotNumber.setText("");
				edtMaterialSN.setText("");
				
				edtLotNumber.requestFocus();
			}
		break;	
			
	
		/*2017-10-10
		 * case TaskType.FeedOnMaterial:
			HashMap<String, String> getdata1;
			getdata1 = new HashMap<String, String>();
			super.closeProDia();
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				logDetails(this.edtSysDatails,"无MES返回信息。");
				return;
			}
			getdata1 = (HashMap<String,String>)task.getTaskResult();
			ischeck_fristmz=false;
			isalert=false;
			isalert_collect="";
			if( Integer.parseInt(getdata1.get("result").toString()) == 0)
			{
				this.edtMaterialSN.setTextColor(Color.GREEN);
				this.edtLotNumber.setTextColor(Color.GREEN);
				logDetails(edtSysDatails,"Success_Msg:"+ getdata1.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n");
				SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
				clearLineAndStation();
			}
			else
			{
				if( getdata1.get("ReturnMsg").toString().contains("物料批号已上料")){
					this.edtMaterialSN.setTextColor(Color.MAGENTA);
					this.edtLotNumber.setTextColor(Color.MAGENTA);	

				}else{				
					this.edtMaterialSN.setTextColor(Color.RED);
					this.edtLotNumber.setTextColor(Color.RED);							
				}
				logDetails(edtSysDatails, getdata1.get("ReturnMsg").toString());
			}
			edtFeida.setText("");
            edtLotNumber.setText("");
			edtMaterialSN.setText("");
			
			edtLotNumber.requestFocus();
		break;*/
		
		case 10000:
			HashMap<String, String> getdata_bt;
			getdata_bt = new HashMap<String, String>();
			super.closeProDia();
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				logDetails(this.edtSysDatails,"无MES返回信息。");
				return;
			}
			getdata_bt = (HashMap<String,String>)task.getTaskResult();
			if( Integer.parseInt(getdata_bt.get("result").toString()) == 0)
			{
				logDetails(edtSysDatails,"Success_Msg:"+ getdata_bt.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n");
				SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
			}
			else
			{
				logDetails(edtSysDatails, getdata_bt.get("ReturnMsg").toString());
			}
			 String msg=getStr(jb,getdata_bt,1);
	         fontGrayscaleSet(4);
	         sendMessage(msg);
	         Log.i(TAG, "msg1="+msg);
		break;
		
		
		case 10010:
			HashMap<String, String> getdata2;
			getdata2 = new HashMap<String, String>();
			super.closeProDia();
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				logDetails(this.edtSysDatails,"无MES返回信息。");
				return;
			}
			getdata2 = (HashMap<String,String>)task.getTaskResult();
			if( Integer.parseInt(getdata2.get("result").toString()) == 0)
			{
				this.edtMaterialSN.setTextColor(Color.GREEN);
				this.edtLotNumber.setTextColor(Color.GREEN);
				logDetails(edtSysDatails,"Success_Msg:"+ getdata2.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n");
				SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
				
			}
			else
			{
				if( getdata2.get("ReturnMsg").toString().contains("物料批号已上料")){
					this.edtMaterialSN.setTextColor(Color.MAGENTA);
					this.edtLotNumber.setTextColor(Color.MAGENTA);	

				}else{				
					this.edtMaterialSN.setTextColor(Color.RED);
					this.edtLotNumber.setTextColor(Color.RED);							
				}
				logDetails(edtSysDatails, getdata2.get("ReturnMsg").toString());
			}	
            //edtLotNumber.setText("");
			edtMaterialSN.setText("");
			edtMaterialSN.requestFocus();
			break;
			
			
		case 55:
			HashMap<String, String> getdata11;
			getdata11 = new HashMap<String, String>();
			super.closeProDia();
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				logDetails(this.edtSysDatails,"无MES返回信息。");
				return;
			}
			getdata11 = (HashMap<String,String>)task.getTaskResult();
			String isalert_str=getdata11.get("isalert");
			if(isalert_str.equals("1")){
				isalert=true;
			}
			if( Integer.parseInt(getdata11.get("result").toString()) == 0)//直接上料
			{
				logDetails(edtSysDatails,"Success_Msg:"+ getdata11.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n");
				feedOnmaterialCheck();
				
			}
			else if( Integer.parseInt(getdata11.get("result").toString()) == 1)//核对旧料成功
			{
				ischeck_fristmz=true;
				isalert_collect=isalert_collect+edtMaterialSN.getText().toString();
				logDetails(edtSysDatails,"Success_Msg:"+ getdata11.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n");
				SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
				edtMaterialSN.setText("");
				edtLotNumber.requestFocus();
				edtMaterialSN.requestFocus();
			}
			
			else  if( Integer.parseInt(getdata11.get("result").toString()) == 2)//检测到未核对旧物料盘想上新料
			{
				logDetails(edtSysDatails, getdata11.get("ReturnMsg").toString());
				edtMaterialSN.setText("");
				edtLotNumber.requestFocus();
				edtMaterialSN.requestFocus();
			}
			else{
				logDetails(edtSysDatails, getdata11.get("ReturnMsg").toString());
				edtMaterialSN.setText("");
				edtLotNumber.requestFocus();
				edtMaterialSN.requestFocus();
			}
			
		break;
		
		
		case TaskType.GetResourceId:
			super.closeProDia();
			ResourceId = "";
			ischeck_fristmz=false;
			isalert=false;
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logDetails(edtSysDatails, "未能获取到资源ID");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			ResourceId = getresult.get(0).get("ResourceId");
			logDetails(edtSysDatails, "资源ID:" + ResourceId);
			if(ResourceId.isEmpty())
			{
				logDetails(edtSysDatails, "未能获取到资源ID");
				this.edtLotNumber.setText("");
				logDetails(edtSysDatails, "请重新获取资源ID");
			}
			else
				GetDeviceTypeId(edtLotNumber.getText().toString().trim().substring(0,4));

			break;
			//fd信息
		case 1008611:
			super.closeProDia();
			List<HashMap<String,String>> getresult1 = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult1).size())
			{
				logDetails(edtSysDatails, "未能找到飞达编号,需扫描飞达编号");
				edtFeida.setText("");
				return;
			}
			getresult1 = (List<HashMap<String,String>>)task.getTaskResult();
			String fdsn = getresult1.get(0).get("DevicePartsNum");
			edtFeida.setText(fdsn);
			edtMaterialSN.requestFocus();
			break;
			
		case TaskType.FeedOnMaterial_GETSMTMOUNTID:
			closeProDia();
			if(super.isNull)
			{
				logDetails(edtSysDatails, "请重新获取SMTMountId");
				this.edtLotNumber.setText("");
				return;
			}
			smtMountId = task.getTaskResult().toString();
			if(smtMountId.length() > 5)
			{
				this.edtLineNo.setText(staLotSN.substring(0, 3));
				this.edtStationNo.setText(staLotSN.substring(3, 4));
				addImforToEdtSysDetai("SMTMountId:"+smtMountId,true);
				this.edtFeida.requestFocus();
			}
			else
			{
				this.edtLotNumber.setText("");
				logDetails(edtSysDatails, "工单与线别是否对应,请重新获取SMTMountId");
			}
			//不自动带出飞达了 需要
			//GetFD(new String[]{moName,edtLotNumber.getText().toString()});		

			//Log.i(TAG, smtMountId);
			//Toast.makeText(this,smtMountId, Toast.LENGTH_SHORT).show();
			break;
		case TaskType.SMT_GetDeviceTypeId:
			super.closeProDia();
			DeviceTypeID = "";
			String[] getString =  (String[])task.getTaskResult();
			if(super.isNull|| getString == null)
			{
				logDetails(edtSysDatails, "未能获取到设备类型ID");
				this.edtLotNumber.setText("");
				return;
			}
			DeviceTypeID = getString[1];
			logDetails(edtSysDatails, "设备类型ID" + DeviceTypeID + "\r\n设备类型名称" + getString[0]);
			MyApplication.setAppDeviceTypeId(DeviceTypeID);
			getSMTMountId(edtLotNumber.getText().toString().trim().substring(0,4));		
			break;

		}

	}

	private void addImforToEdtSysDetai(String str,boolean success)
	{
		CharacterStyle ssStyle=null;
		if(success){
			ssStyle=new ForegroundColorSpan(Color.GREEN);
		}else{
			ssStyle=new ForegroundColorSpan(Color.RED);
		}
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String sysLog="["+df.format(new Date())+"]"+str+"\n";		
		SpannableStringBuilder ssBuilder=new SpannableStringBuilder(sysLog);
		ssBuilder.setSpan(ssStyle, 0, sysLog.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssBuilder.append(edtSysDatails.getText());
		edtSysDatails.setText(ssBuilder);	
	}
	private void clearLineAndStation(){

		staLotSN = "";
		clearWidget(edtLineNo, null);
		clearWidget(edtStationNo, null);
		edtFeida.setText("");
		edtLotNumber.requestFocus();
		smtMountId = "";
		super.clearWidget(this.edtLotNumber, null);
		super.clearWidget(this.edtMaterialSN, null);
		//修改了上面的清除按钮不请工单
		//MyApplication.setAppMoid("");
	}
	
	/*
	 * 屏蔽蓝牙打印 20180511
	 * public void  clickbt(View v){
		//蓝牙打印去后台获取打印信息
		 if (mService.getState() != BluetoothService.STATE_CONNECTED) {
	           Toast.makeText(FeedOnMateriActivity.this, "未连接蓝牙打印机.....", Toast.LENGTH_SHORT).show();
	     }else{
	    	   String[]   bt_paras = new String[]{
						MyApplication.getMseUser().getUserName() ,
						moName,
						edtLineNo.getText().toString()+edtStationNo.getText().toString()+edtLotNumber.getText().toString(),
						edtMaterialSN.getText().toString()};
				Task task_bt = new Task(FeedOnMateriActivity.this, 10000,bt_paras);
				progressDialog.setMessage(getString(R.string.TransationData));
				showProDia();
				model.feedOnMaterial_bt(task_bt);
	     }
		//蓝牙打印去后台获取打印信息
	}*/
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.btn_cancel_stalotsn:
			clearLineAndStation();
			break;
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.btn_cancel:
			super.clearWidget(this.edtLotNumber, null);
			super.clearWidget(this.edtMaterialSN, null);
			break; 
		case R.id.edt_slotScan:
			if(StringUtils.isScannedSlotNumber(edtLotNumber.getText().toString().trim()))
				analyseScanneddataAndExecute(edtLotNumber.getText().toString().toUpperCase());
			else
			{
				edtLotNumber.setText("");
				Toast.makeText(this, "输入槽位条码不正确", 5).show();
			}
			break;
		case R.id.edt_materialScan:
			if(edtMaterialSN.getText().toString().trim().length() >= 8)
				analyseScanneddataAndExecute(edtMaterialSN.getText().toString().toUpperCase());
			break;
	
		case R.id.feeding_onmateri_moselectedit:
			super.progressDialog.setMessage("正在数据库筛选工单");
			super.showProDia();
			Task task = new Task(this, TaskType.FeedOnMaterial_GETMONames2, moselectedit.getText().toString().trim());
			model.getNeceParams2(task);	
			break;	
		
		}

	}

	private void analyseScanneddataAndExecute(String str) 
	{

		this.edtLotNumber.setTextColor(Color.BLUE);
		this.edtMaterialSN.setTextColor(Color.BLUE);

		if(moName.isEmpty()){				
			Toast.makeText(this,"请先选择生产工单号!", Toast.LENGTH_SHORT).show();
			this.edtMaterialSN.setText("");
			this.edtLotNumber.setText("");
			return;
		}			
		if( edtLineNo.getText().toString().trim().isEmpty()){
			if(StringUtils.isScannedSlotNumber(str))
			{
				GetResourceId(str.trim().substring(0,4));//S1022-10-L  的意思是  S10线 2大机台  2机台 截取 S102  取资源表获取资源ID！
				this.edtMaterialSN.setText("");
				this.edtLotNumber.setText(str);
			}else
				Toast.makeText(this,"请扫描槽位条码！", Toast.LENGTH_SHORT).show();

		}else{
			if( StringUtils.isScannedSlotNumber(str))
			{
				if(	 !(edtLineNo.getText().toString().equalsIgnoreCase(str.substring(0,3)) 
						&& edtStationNo.getText().toString().equalsIgnoreCase(str.substring(3,4)))) 			    		
				{
					logDetails(edtSysDatails, "变更扫描产线或机台，请清空重扫");
					Toast.makeText(this,"变更扫描产线或机台，请清空重扫", Toast.LENGTH_SHORT).show(); 
					return;
				}
				this.edtLotNumber.setText(str);
				this.edtMaterialSN.requestFocus();
				this.edtMaterialSN.setText("");
			}else
			{
				if(edtLotNumber.isFocused())
				{
					this.edtMaterialSN.setText("");
					this.edtLotNumber.setText("");
					this.edtLotNumber.requestFocus();
					Toast.makeText(this,"请扫描槽位条码！", Toast.LENGTH_SHORT).show(); 
				}
				else if(StringUtils.isScannedSlotNumber(edtLotNumber.getText().toString()))
				{
					   this.edtMaterialSN.setText(str);
					  /* 
					   * 屏蔽蓝牙20180511
					   * //强制检测蓝牙连接
					   if (mService.getState() != BluetoothService.STATE_CONNECTED) {
				            Toast.makeText(this, "蓝牙未连接", Toast.LENGTH_SHORT).show();
				            return;
				        }*/
					   if(ischeck_fristmz){
						   feedOnmaterialCheck();
					   }
					   else{
						   feedOnmaterialCheck(1);//参数无意义 仅用于重载而已
					   }
						   
					   
					/************************************************************************/
					/**原始只有2句代码：this.edtMaterialSN.setText(str);
					   feedOnmaterialCheck();**/
					/************************************************************************/
//					this.edtMaterialSN.setText(str);
//					//开始通过MZ条码获取到此条码对应的槽位，更新槽位条码，然后自动上料提交
//				   model.getslot(new  Task(this,TaskType.smtfeedgetslot,str));
//				
//					
//					//feedOnmaterialCheck();
					
				}
				else
				{
					  this.edtMaterialSN.setText(str);
					  model.getslot(new  Task(this,TaskType.smtfeedgetslot,str));
					//this.edtLotNumber.requestFocus();
					//Toast.makeText(this,"请扫描槽位编号!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private void getSMTMountId(String tempStaLotSN)
	{	
		if(moName.isEmpty())
		{
			logDetails(edtSysDatails, "请先获取订单号");
			return;
		}
		if(DeviceTypeID.isEmpty())
		{
			logDetails(edtSysDatails, "未能获取到设备类型ID，请检查资源名是否设置正确");
			return;
		}
		String[] paras= new String[]{moName,DeviceTypeID,tempStaLotSN};
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		showProDia();
		staLotSN =  tempStaLotSN.trim();  
		smtMountId = "";
		Task getSmtMountId = new Task(this, TaskType.FeedOnMaterial_GETSMTMOUNTID,paras );
		model.getSMTMountId(getSmtMountId);
	} 
	private void GetDeviceTypeId(String DeviceName)
	{

		super.progressDialog.setMessage("获取设备类型ID");
		showProDia();
		DeviceTypeID = "" ;
		Task GetDeviceTypeId = new Task(this, TaskType.SMT_GetDeviceTypeId,DeviceName );
		model.CheckDeviceTypeNameAndDeviceTypeId(GetDeviceTypeId);
	} 


	private void getMONames()
	{
		super.progressDialog.setMessage(getString(R.string.loadAppNecessData));
		super.showProDia();
		Task task = new Task(this, TaskType.FeedOnMaterial_GETMONames, null);
		model.getNeceParams(task);		
	}
	private void feedOnmaterialCheck()
	{
		boolean isPass = subWidget();
		if(!isPass)return;
		if(isalert&&!isalert_collect.contains(edtMaterialSN.getText().toString())){
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("请确认潮敏指示卡是否变色?")
			.setPositiveButton("未变色",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					String[] paras = new String[]{ ResourceId ,moName,edtLotNumber.getText().toString(),edtMaterialSN.getText().toString()};
					//Log.i(TAG, this.smtMountId+""+this.moName+"");
					model.feedOnMaterial3(new Task(FeedOnMateriActivity.this, 10086,paras));
					
					String[] paras2 = new String[]{ ResourceId ,moName,edtLotNumber.getText().toString(),edtMaterialSN.getText().toString(),edtFeida.getText().toString()};
					Task task = new Task(FeedOnMateriActivity.this, TaskType.FeedOnMaterial,paras2);
					progressDialog.setMessage(getString(R.string.TransationData));
					showProDia();
					model.feedOnMaterial(task);
				}
			})
			.setNegativeButton("变色",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					String[] paras = new String[]{ ResourceId ,edtMaterialSN.getText().toString()};
					//Log.i(TAG, this.smtMountId+""+this.moName+"");
					isalert_collect=isalert_collect+edtMaterialSN.getText().toString();
					Task task = new Task(FeedOnMateriActivity.this, 10010,paras);
					progressDialog.setMessage(getString(R.string.TransationData));
					showProDia();
					model.feedOnMaterial2(task);
					isalert=false;
				}
			}).show();
//			if(edtMaterialSN.getText().toString().toUpperCase().startsWith("MZD")){
//				
//			}
		}
		else{
			String[] paras = new String[]{ ResourceId ,moName,edtLotNumber.getText().toString(),edtMaterialSN.getText().toString(),edtFeida.getText().toString()};
			//Log.i(TAG, this.smtMountId+""+this.moName+"");
			Task task = new Task(FeedOnMateriActivity.this, TaskType.FeedOnMaterial,paras);
			progressDialog.setMessage(getString(R.string.TransationData));
			showProDia();
			model.feedOnMaterial(task);
		}
		
	}
	
	private void feedOnmaterialCheck(int num)
	{
		boolean isPass = subWidget();
		if(!isPass)return;
		String[] paras = new String[]{ ResourceId ,moName,edtLotNumber.getText().toString(),edtMaterialSN.getText().toString()};
		//Log.i(TAG, this.smtMountId+""+this.moName+"");
		Task task = new Task(this, 55,paras);
		super.progressDialog.setMessage("核对前盘或新盘物料中......");
		showProDia();
		model.feedOnMaterial_4d(task);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3)
	{
		//HashMap<String,String>  selItem = (HashMap<String,String>)paraMapLis.get(arg2);
		if(null==moNames||0==moNames.size()) return;
		clearLineAndStation();
		moName = moNames.get(arg2).get(MONAME);
		MyApplication.setAppMoid(moName);
	 
		Log.i(TAG, ""+moName);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) 
	{
		// TODO Auto-generated method stub

	}

	private boolean subWidget()
	{
		boolean isPass = true;
		if(StringUtils.isEmpty(moName))
		{
			isPass = false;
			Toast.makeText(this,getString(R.string.moName_isnull), Toast.LENGTH_SHORT).show();//工单
			return isPass;
		}
		if(StringUtils.isEmpty(edtLineNo.getText().toString()))//产线为空
		{
			isPass = false;
			Toast.makeText(this, getString(R.string.proLine_isnull), Toast.LENGTH_SHORT).show();
			return isPass;		
		}
		if(3!=edtLineNo.getText().toString().trim().length())
		{
			isPass = false;
			Toast.makeText(this, R.string.lineNo_notLegal, Toast.LENGTH_SHORT).show();
			return isPass;		
		}
		if(StringUtils.isEmpty(edtStationNo.getText().toString()))//站位条码
		{
			isPass = false;
			Toast.makeText(this, getString(R.string.staNO_isnull), Toast.LENGTH_SHORT).show();
			return isPass;		
		}
		if(1!=edtStationNo.getText().toString().trim().length())
		{
			isPass = false;
			Toast.makeText(this,R.string.stationNo_notLegal, Toast.LENGTH_SHORT).show();
			return isPass;		
		}
		if(StringUtils.isEmpty(edtMaterialSN.getText().toString()))//物料批号
		{
			isPass = false;
			Toast.makeText(this,getString(R.string.materialNum_notNull), Toast.LENGTH_SHORT).show();
			return isPass;
		}
		if(StringUtils.isEmpty(smtMountId))//数据错误
		{
			isPass = false;
			Toast.makeText(this,getString(R.string.repScan_stalot), Toast.LENGTH_SHORT).show();
			return isPass;
		} 
		if(StringUtils.isEmpty(this.edtLotNumber.getText().toString()))//数据错误
		{
			isPass = false;
			Toast.makeText(this,"槽位条码为空", Toast.LENGTH_SHORT).show();
			return isPass;
		} 
		return isPass;
	}
	@Override
	public void onBackPressed() {
		killMainProcess();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		MyApplication.setAppMoid("");
		 if (mService != null) mService.stop();
	     if(D) Log.e(TAG, "--- ON DESTROY ---");
	}
	private void killMainProcess() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("确定退出上料对料吗?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(FeedOnMateriActivity.this,
						BackgroundService.class));
				finish();

			}
		})
		.setNegativeButton(getString(android.R.string.no), null).show();
	}
	
	
     private  String getStr(String msg,Map<String,String> getdata,int order){
    	 
    	/* "^XA\n"+
					"^MMT\n"+
					"^PW591\n"+
					"^LL0200\n"+
					"^LS0\n"+
					"^FO129,84^GB447,109,1^FS\n"+
					"^FO130,166^GB444,0,1^FS\n"+
					"^FO130,106^GB445,0,1^FS\n"+
					"^FO130,134^GB445,0,1^FS\n"+
					"^FT406,173^A0I,14,14^FH\\^FDZhanWei^FS\n"+
					"^FO352,109^GB0,24,1^FS\n"+
					"^FO213,135^GB0,56,1^FS\n"+
					"^FO413,168^GB0,23,1^FS\n"+
					"^FT209,174^A0I,14,14^FH\\^FDA/B^FS\n"+
					"^FT570,90^A0I,14,14^FH\\^FDGuige^FS\n"+
					"^FT571,174^A0I,14,14^FH\\^FDGongDan^FS\n"+
					"^FT570,115^A0I,14,14^FH\\^FDTime^FS\n"+
					"^FT350,116^A0I,14,14^FH\\^FDOrderNo^FS\n"+
					"^FT209,145^A0I,14,14^FH\\^FDQTY^FS\n"+
					"^FT571,144^A0I,14,14^FH\\^FDLiaoHao^FS\n"+
					"^PQ1,0,1,Y^XZ";*/
					
//    	 SELECT @MOname  AS moname,@slotsn AS slot ,'B' AS side,
//    		@LotSN AS mzlotsn ,@qty AS qty,
//    		 REPLACE(REPLACE( REPLACE(CONVERT(VARCHAR(20),GETDATE(),120),'-',''),':',''),' ','') AS timenow, @I_OrBitUserName AS gonghao,
//    		 @mzdescri AS  guige
//    	 
		
		String ZhanWei=getdata.get("slot");
		if(ZhanWei==null) ZhanWei="站位:";
		
		String AB=getdata.get("side");
		if(AB==null) AB="A/B:";
		
		String Guige=getdata.get("guige");
		if(Guige==null) Guige="物料规格:物料描述01   物料描述02 物料描述03 物料描述04 物料描述05 物料描述06 物料描述07 物料描述08 物料描述09 物料描述10 物料描述11";
		
		String GongDan=getdata.get("moname");
		if(GongDan==null) GongDan="mo:";
		
		String Time=getdata.get("timenow");
		if(Time==null) Time="time:";
		
		String OrderNo=getdata.get("gonghao");
		if(OrderNo==null) OrderNo="gonghao:";
		
		String QTY=getdata.get("qty");
		if(QTY==null) QTY="QTY:";
		
		String LiaoHao=getdata.get("mzlotsn");
		if(LiaoHao==null) LiaoHao="mzlotsn:";
		
		
		msg=msg.replace("ZhanWei", ZhanWei).replace("A/B", AB).replace("GongDan", GongDan)
				.replace("Time", Time).replace("OrderNo", OrderNo).replace("QTY", QTY).replace("LiaoHao", LiaoHao);
		
		for(int n=1;n<=3;n++){
			if(Guige.length()>=30){
				msg=msg.replace("Guige"+n, Guige.substring(0, 40));
				Guige=Guige.substring(40);
			}else{
				msg=msg.replace("Guige"+n, Guige.substring(0, Guige.length()));
				if(n==1)msg=msg.replace("Guige2", "").replace("Guige3", "");
				if(n==2)msg=msg.replace("Guige3", "");
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
            	send = new String(message.getBytes(),"UTF-8").getBytes("UTF-8");
            	//send = message.getBytes("gbk");
            	
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
