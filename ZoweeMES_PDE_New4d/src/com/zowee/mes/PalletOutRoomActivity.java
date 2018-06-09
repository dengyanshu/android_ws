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
		this.setTitle("�ֻ�ջ�����ɨ�����");
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
		buttcancel.setText("ȡ��");
		buttsubmit.setText("�ύ");
		edtPalletOutRoomPalletNumber.setOnClickListener(this);
		buttscan.setOnClickListener(this);
		buttrefresh.setVisibility(View.GONE);

		final Builder builder = new AlertDialog.Builder(this);
		buttsubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.check);	
				builder.setTitle("ȷ���ύ");
				builder.setMessage("��ȷ���Ƿ��ύ?");
				builder.setPositiveButton("ȷ��"
						//Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								if(CustomerCode.isEmpty())
								{									
									logDetails(edtSysDatails, "����ɨ��ջ��ţ���ö�����");
									return;
								}
								if( ScanedPalletQty < 1 )
								{
									logDetails(edtSysDatails, "����ɨ��ջ������ύ");
									return;
								}
								PalletOutRoomexecute(2,AllPalletSN);
							}
						});
				// Ϊ�Ի�������һ����ȡ������ť
				builder.setNegativeButton("ȡ��"
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
				builder2.setTitle("ȷ��ȡ��");
				builder2.setMessage("��ȷ���Ƿ�Ҫȡ��?");
				builder2.setPositiveButton("ȷ��"
						//Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								clearAll();
							}
						});
				// Ϊ�Ի�������һ����ȡ������ť
				builder2.setNegativeButton("ȡ��"
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
		logDetails(edtSysDatails, "���ȡ�������֮ǰɨ�����Ϣ");		
		logDetails(edtSysDatails, "ʹ��˵����");
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
				Toast.makeText(this, "ջ��ų��Ȳ���ȷ", 3).show();
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
					logDetails(edtSysDatails,"Success_Msg: " + ScanedPalletQty + ":" + PalletSN + "ջ��ɨ��OK" );
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
					logDetails(edtSysDatails, "�������ɨ�����ݣ�������ɨ��");
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
				logDetails(edtSysDatails, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
				return;
			}
			getresult = (List<HashMap<String,String>>)task.getTaskResult();
			ResourceId = getresult.get(0).get("ResourceId");
			logDetails(edtSysDatails, "��ԴID:" + ResourceId);
			if(ResourceId.isEmpty())	 
				logDetails(edtSysDatails, "δ�ܻ�ȡ����ԴID��������Դ���Ƿ�������ȷ");
			Params[0] = ResourceId; 
			break;

		}

	}
	private void applyScanData(String ScanedSN)
	{
		if(ScanedSN.length() <8 )
		{
			Toast.makeText(this, "ջ��ų��Ȳ���ȷ", 3).show();
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

			logDetails(edtSysDatails, "��Դ����Ϊ�գ������趨��������"  );
			return;
		}
		super.showProDia();	 
		Params[1] = MyApplication.getAppOwner().toString().toUpperCase(); 
		logDetails(edtSysDatails, "��Դ���ƣ�" + MyApplication.getAppOwner().toString() );
		Task task = new Task(this, TaskType.GetResourceId, MyApplication.getAppOwner().toString());	
		GetMonamemodel.GetResourceId(task);	
	}
	private void PalletOutRoomexecute(int cmdType,String inputSN) //cmdType:1 ScanPallet , 2 = Submit
	{
		if(submiting)
		{
			edtPalletOutRoomPalletNumber.setText("");
			PalletSN = "";
			logDetails(edtSysDatails, "�ύ�У����Ժ���ɨ"  );
			return;
		}
		if(Params[1].isEmpty()){

			logDetails(edtSysDatails, "��Դ����Ϊ�գ������趨��������"  );
			return;
		}
		if(ResourceId.isEmpty())
		{
			logDetails(edtSysDatails, "��Դ���Ʋ���ȷ��û�л�ȡ����ԴID"  );
			return;
		}

		if(CustomerCode.isEmpty())
		{
			if( ScanedPalletQty==0 && cmdType ==1 )
				Params[2] = "";
			else
			{
				logDetails(edtSysDatails, "û�л�ȡ����Ʒ������"  );
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
				logDetails(edtSysDatails, "�ظ�ɨ��"  );
				edtPalletOutRoomPalletNumber.setText("");
				return;				
			}
			super.progressDialog.setMessage("ջ��ų���ɨ��");
		}
		else if(cmdType == 2)
		{
			Params[3] = "Submit";	
			super.progressDialog.setMessage("ջ��ų����ύ");
		}
		else
		{
			logDetails(edtSysDatails, "���ò�������ȷ"  );
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
		.setTitle("ȷ���˳��ֻ�ջ�����ɨ�����?")
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
