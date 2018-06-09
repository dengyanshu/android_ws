package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.OQCPhoneCousmerOQCBindModel;
import com.zowee.mes.model.PalletOutRoomModel;
import com.zowee.mes.service.BackgroundService;
import com.zowee.mes.service.SoundEffectPlayService;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PalletOutRoomActivity  extends CommonActivity implements View.OnClickListener {


	private EditText edtSysDatails;
	private EditText edtPalletOutRoomCustomerCode;
	private EditText edtPalletOutRoomPalletNumber;

	private Button buttscan;
	private Button buttsubmit;
	private Button buttcancel;
	private Button buttrefresh;
	private int ScanedPalletQty=0;
	private String ResourceId="";	 
	private String AllPalletSN ="";
	private boolean submiting= false;
	private String CustomerCode=""; 	 			 
	private String PalletSN="";  
	private PalletOutRoomModel PalletOutmodel;
	private GetMOnameModel GetMonamemodel;
	String[]Params = new String[]{"","","","",""};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pallet_out_room);
		init();
		this.setTitle("手机栈板出货扫描程序");
		this.setTitleColor(Color.GREEN);
	}
	@Override
	public void init()
	{

		super.commonActivity = this;
		super.TASKTYPE = TaskType.PalletOutRoomScan_phone;
		super.init();
		GetMonamemodel = new GetMOnameModel();
		PalletOutmodel = new PalletOutRoomModel();

		edtSysDatails = (EditText)findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
		edtPalletOutRoomCustomerCode = (EditText)findViewById(R.id.edt_PalletOutRoomMoId);
		edtPalletOutRoomPalletNumber =(EditText)findViewById(R.id.edt_PalletOutRoomPalletNumber);

		buttscan =(Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_scan);
		buttsubmit = (Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_ensure);
		buttcancel = (Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_cancel);
		buttrefresh = (Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_refreshData);
		buttcancel.setText("取消");
		buttsubmit.setText("提交");
		edtPalletOutRoomPalletNumber.setOnClickListener(this);
		buttscan.setOnClickListener(this);
		buttrefresh.setVisibility(View.GONE);

		final Builder builder = new AlertDialog.Builder(this);
		buttsubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.check);	
				builder.setTitle("确认提交");
				builder.setMessage("请确认是否提交?");
				builder.setPositiveButton("确定"
						//为列表项的单击事件设置监听器
						, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								if(CustomerCode.isEmpty())
								{									
									logDetails(edtSysDatails, "请先扫描栈板号，获得订单号");
									return;
								}
								if( ScanedPalletQty < 1 )
								{
									logDetails(edtSysDatails, "请先扫描栈板号再提交");
									return;
								}
								PalletOutRoomexecute(2,AllPalletSN);
							}
						});
				// 为对话框设置一个“取消”按钮
				builder.setNegativeButton("取消"
						,  new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	

							}
						});	
				builder.create().show();
			}
		});

		final Builder builder2 = new AlertDialog.Builder(this);
		buttcancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder2.setIcon(R.drawable.check);	
				builder2.setTitle("确认取消");
				builder2.setMessage("请确认是否要取消?");
				builder2.setPositiveButton("确定"
						//为列表项的单击事件设置监听器
						, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								clearAll();
							}
						});
				// 为对话框设置一个“取消”按钮
				builder2.setNegativeButton("取消"
						,  new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{							
							}
						});	
				builder2.create().show();
			}
		});

		GetResourceId();
		logDetails(edtSysDatails, "点击取消将清空之前扫描的信息");		
		logDetails(edtSysDatails, "使用说明：");
	}
	public void clearAll()
	{
		CustomerCode = "";
		AllPalletSN = "";	 
		PalletSN="";  
		edtPalletOutRoomPalletNumber.setText("");
		edtPalletOutRoomCustomerCode.setText("");								 
		ScanedPalletQty = 0;	
		edtSysDatails.setText("");
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.edt_PalletOutRoomPalletNumber:
			PalletSN = edtPalletOutRoomPalletNumber.getText().toString().trim().toUpperCase();
			if(PalletSN.length() < 8)
			{
				PalletSN = "";
				edtPalletOutRoomPalletNumber.setText("");
				Toast.makeText(this, "栈板号长度不正确", 3).show();
				return;
			}
			edtPalletOutRoomPalletNumber.setText(PalletSN);
			PalletOutRoomexecute(1,PalletSN);
			break;

		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Task task)
	{
		super.refresh(task);
		int taskType = task.getTaskType();
		HashMap<String, String> getdata;
		getdata = new HashMap<String, String>();
		switch(taskType)
		{
		case TaskType.PalletOutRoom_phone:
			super.closeProDia();
			edtPalletOutRoomPalletNumber.setText("");
			submiting = false; 
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				return;
			}
			getdata = (HashMap<String,String>)task.getTaskResult();
			if( Integer.parseInt(getdata.get("result").toString()) == 0)
			{
				if(ScanedPalletQty ==0)
				{
					CustomerCode = getdata.get("CustomerCode").toString();	//			 	 
					edtPalletOutRoomCustomerCode.setText(CustomerCode);
					AllPalletSN = PalletSN ;
				}
				else
				{
					AllPalletSN += "," + PalletSN;
				}
				ScanedPalletQty ++;
				if( Params[3].equalsIgnoreCase("ScanPallet"))
					logDetails(edtSysDatails,"Success_Msg: " + ScanedPalletQty + ":" + PalletSN + "栈板扫描OK" );
				else
				{
					AllPalletSN = "";
					logDetails(edtSysDatails,"Success_Msg:"+ getdata.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n" );
				}
				SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
			}else{
				logDetails(edtSysDatails, getdata.get("ReturnMsg").toString());
				if( Params[3].equalsIgnoreCase("Submit"))
				{
					clearAll();
					logDetails(edtSysDatails, "已清空已扫描数据，请重新扫描");
				}
			}
			break;
		case TaskType.PalletOutRoomScan_phone:
			if(super.isNull) return;
			String Scanedlabel;
			Scanedlabel = task.getTaskResult().toString().trim().toUpperCase();
			applyScanData(Scanedlabel);
			break;

		case TaskType.GetResourceId:
			super.closeProDia();
			ResourceId = "";
			List<HashMap<String,String>> getresult = (List<HashMap<String,String>>)task.getTaskResult();
			if(super.isNull||0==(getresult).size())
			{
				logDetails(edtSysDatails, "未能获取到资源ID，请检查资源名是否设置正确");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			ResourceId = getresult.get(0).get("ResourceId");
			logDetails(edtSysDatails, "资源ID:" + ResourceId);
			if(ResourceId.isEmpty())	 
				logDetails(edtSysDatails, "未能获取到资源ID，请检查资源名是否设置正确");
			Params[0] = ResourceId; 
			break;

		}

	}
	private void applyScanData(String ScanedSN)
	{
		if(ScanedSN.length() <8 )
		{
			Toast.makeText(this, "栈板号长度不正确", 3).show();
			return;		
		}
		PalletSN = ScanedSN;
		edtPalletOutRoomPalletNumber.setText(PalletSN);
		PalletOutRoomexecute(1,PalletSN);
	}
	private void GetResourceId()
	{
		super.progressDialog.setMessage("Get resource ID");
		if(MyApplication.getAppOwner().toString().isEmpty()){

			logDetails(edtSysDatails, "资源名称为空，请先设定资料名称"  );
			return;
		}
		super.showProDia();	 
		Params[1] = MyApplication.getAppOwner().toString().toUpperCase(); 
		logDetails(edtSysDatails, "资源名称：" + MyApplication.getAppOwner().toString() );
		Task task = new Task(this, TaskType.GetResourceId, MyApplication.getAppOwner().toString());	
		GetMonamemodel.GetResourceId(task);	
	}
	private void PalletOutRoomexecute(int cmdType,String inputSN) //cmdType:1 ScanPallet , 2 = Submit
	{
		if(submiting)
		{
			edtPalletOutRoomPalletNumber.setText("");
			PalletSN = "";
			logDetails(edtSysDatails, "提交中，请稍后再扫"  );
			return;
		}
		if(Params[1].isEmpty()){

			logDetails(edtSysDatails, "资源名称为空，请先设定资料名称"  );
			return;
		}
		if(ResourceId.isEmpty())
		{
			logDetails(edtSysDatails, "资源名称不正确，没有获取到资源ID"  );
			return;
		}

		if(CustomerCode.isEmpty())
		{
			if( ScanedPalletQty==0 && cmdType ==1 )
				Params[2] = "";
			else
			{
				logDetails(edtSysDatails, "没有获取到产品订单号"  );
				return;
			}
		}
		else
		{
			Params[2] = CustomerCode; 
		}
		Params[4] = inputSN ; 
		if(cmdType == 1)
		{
			Params[3] = "ScanPallet";
			if(AllPalletSN.contains(inputSN))
			{
				logDetails(edtSysDatails, "重复扫描"  );
				edtPalletOutRoomPalletNumber.setText("");
				return;				
			}
			super.progressDialog.setMessage("栈板号出货扫描");
		}
		else if(cmdType == 2)
		{
			Params[3] = "Submit";	
			super.progressDialog.setMessage("栈板号出货提交");
		}
		else
		{
			logDetails(edtSysDatails, "调用参数不正确"  );
			return;
		}

		super.showProDia();	 
		Task task = new Task(this, TaskType.PalletOutRoom_phone, Params);	
		PalletOutmodel.PalletOutRoomScan(task);
		submiting = true;

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.phone_oqcbox_bind, menu);
		return true;
	}


	@Override
	public void onBackPressed() {
		killMainProcess();
	}
	private void killMainProcess() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("确定退出手机栈板出货扫描程序?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(PalletOutRoomActivity.this,BackgroundService.class));
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no),null).show();
	}

} 
