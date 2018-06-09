package com.zowee.mes;

import java.util.HashMap;
import java.util.List;

import com.zowee.mes.activity.CommonActivity;
import com.zowee.mes.adapter.FeedOnMateriAdapter;
import com.zowee.mes.adapter.SpecificationAdapter;
import com.zowee.mes.adapter.workcenterAdapter;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.model.DIPLoadpartsModel;
import com.zowee.mes.model.GetMOnameModel;
import com.zowee.mes.model.GetSpecificationIdAndProductionLine;
import com.zowee.mes.model.OQCPhoneCousmerOQCBindModel;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("unused")
public class PhoneOQCBoxBindActivity  extends CommonActivity implements View.OnClickListener {


	private EditText edtSysDatails;
	private EditText edtoqcboxbindMoId;
	private EditText edtoqcboxbindSpecificationName;
	private EditText edtoqcboxbindBoxNumber;

	private Button buttscan;
	private Button buttsubmit;
	private Button buttcancel;
	private Button buttrefresh;
	private int ScanedBoxQty=0;
	private String ResourceId="";	 
	private String AllBoxSN ="";
	private boolean submiting= false;
	private String MoID=""; 	 			 
	private String CartoonBoxSN="";  
	private String FirstSpecification="";
	private OQCPhoneCousmerOQCBindModel CousmerOQCBindmodel;
	private GetMOnameModel GetMonamemodel;
	String[]Params = new String[]{"","","","","",""};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_oqcbox_bind);
		init();
		this.setTitle("OQC�ͼ쵥���ͼ����");
		this.setTitleColor(Color.GREEN);
	}
	@Override
	public void init()
	{

		super.commonActivity = this;
		super.TASKTYPE = TaskType.OQC_PhoneOQCBoxBindScan;
		super.init();
		GetMonamemodel = new GetMOnameModel();
		CousmerOQCBindmodel = new OQCPhoneCousmerOQCBindModel();

		edtSysDatails = (EditText)findViewById(R.id.common_edtView_operateDetails).findViewById(R.id.edt_sysdetail);
		edtoqcboxbindMoId = (EditText)findViewById(R.id.edt_oqcboxbindMoId);
		edtoqcboxbindSpecificationName = (EditText)findViewById(R.id.edt_oqcboxbindSpecificationName);
		edtoqcboxbindBoxNumber =(EditText)findViewById(R.id.edt_oqcboxbindBoxNumber);

		buttscan =(Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_scan);
		buttsubmit = (Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_ensure);
		buttcancel = (Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_cancel);
		buttrefresh = (Button)findViewById(R.id.btnGroups).findViewById(R.id.btn_refreshData);
		buttcancel.setText("ȡ��");
		buttsubmit.setText("�ͼ�");
		edtoqcboxbindBoxNumber.setOnClickListener(this);
		buttscan.setOnClickListener(this);
		buttrefresh.setVisibility(View.GONE);

		final Builder builder = new AlertDialog.Builder(this);
		buttsubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.check);	
				builder.setTitle("ȷ���ͼ�");
				builder.setMessage("��ȷ���Ƿ�Ҫ�ͼ�?");
				builder.setPositiveButton("ȷ��"
						//Ϊ�б���ĵ����¼����ü�����
						, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{	
								if(MoID.isEmpty())
								{									
									logDetails(edtSysDatails, "����ɨ����ţ���ù���");
									return;
								}
								if( ScanedBoxQty < 2 )
								{
									logDetails(edtSysDatails, "ɨ���������������");
									return;
								}
								PhoneCousmerOQCBinding(2,AllBoxSN);
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
								ClearAll();
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
	public void ClearAll()
	{
		MoID = "";
		AllBoxSN = "";	 
		CartoonBoxSN="";  
		FirstSpecification="";
		edtoqcboxbindMoId.setText("");
		edtoqcboxbindSpecificationName.setText("");
		edtoqcboxbindBoxNumber.setText("");
		ScanedBoxQty = 0;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_scan:
			super.laserScan();
			break;
		case R.id.edt_oqcboxbindBoxNumber:
			CartoonBoxSN = edtoqcboxbindBoxNumber.getText().toString().trim().toUpperCase();
			if(CartoonBoxSN.length() < 8)
			{
				CartoonBoxSN = "";
				edtoqcboxbindBoxNumber.setText("");
				Toast.makeText(this, "��ų��Ȳ���ȷ", 3).show();
				return;
			}
			edtoqcboxbindBoxNumber.setText(CartoonBoxSN);
			PhoneCousmerOQCBinding(1,CartoonBoxSN);
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
		case TaskType.OQC_PhoneOQCBoxBind:
			super.closeProDia();
			edtoqcboxbindBoxNumber.setText("");
			submiting = false; 
			if(super.isNull||0==((HashMap<String,String>)task.getTaskResult()).size())
			{
				return;
			}
			getdata = (HashMap<String,String>)task.getTaskResult();
			if( Integer.parseInt(getdata.get("result").toString()) == 0)
			{
				if(ScanedBoxQty ==0)
				{
					MoID = getdata.get("moid").toString();				 	 
					FirstSpecification= getdata.get("Specificationid").toString();   
					edtoqcboxbindMoId.setText(MoID);
					edtoqcboxbindSpecificationName.setText(FirstSpecification);
					AllBoxSN = CartoonBoxSN ;
				}
				else
				{
					AllBoxSN += "," + CartoonBoxSN  ;
				}
				ScanedBoxQty ++;
				if( Params[4].equalsIgnoreCase("ScanBox"))
					logDetails(edtSysDatails,"Success_Msg: " + ScanedBoxQty + ":" + CartoonBoxSN + "ɨ��OK" );
				else
				{
					AllBoxSN = "";
					logDetails(edtSysDatails,"Success_Msg:"+ getdata.get("ReturnMsg").toString().replaceFirst("ServerMessage:", "")+"\r\n" );
				}
				SoundEffectPlayService.playLaserSoundEffect(SoundEffectPlayService.SoundPoolResource.OK_MUSIC);
			}else{
				logDetails(edtSysDatails, getdata.get("ReturnMsg").toString());
				if( Params[4].equalsIgnoreCase("BindingSBN"))
				{
					ClearAll();
					logDetails(edtSysDatails, "�������ɨ�����ݣ�������ɨ��");
				}
			}
			break;
		case TaskType.OQC_PhoneOQCBoxBindScan:
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
			Toast.makeText(this, "��ų��Ȳ���ȷ", 3).show();
			return;		
		}
		CartoonBoxSN = ScanedSN;
		edtoqcboxbindBoxNumber.setText(ScanedSN);
		PhoneCousmerOQCBinding(1,ScanedSN);
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
	private void PhoneCousmerOQCBinding(int cmdType,String inputSN) //cmdType:1 ScanBox , 2 =BindingSBN
	{
		if(submiting)
		{
			edtoqcboxbindBoxNumber.setText("");
			CartoonBoxSN = "";
			logDetails(edtSysDatails, "ɨ���ύ�У����Ժ���ɨ"  );
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

		if(MoID.isEmpty())
		{
			Params[2] = "";
			Params[5] = ""; 
		}
		else
		{
			Params[2] = MoID;
			Params[5] = FirstSpecification;
			if(Params[5].isEmpty())
			{
				logDetails(edtSysDatails, "û�л�ȡ����Ʒ���"  );
				return;
			}
		}
		Params[3] = inputSN ; 
		if(cmdType == 1)
		{
			Params[4] = "ScanBox";
			if(AllBoxSN.contains(inputSN))
			{
				logDetails(edtSysDatails, "�ظ�ɨ��"  );
				edtoqcboxbindBoxNumber.setText("");
				return;				
			}
		}
		else if(cmdType == 2)
		{
			Params[4] = "BindingSBN";			
		}
		else
		{
			logDetails(edtSysDatails, "���ò�������ȷ"  );
			return;
		}
		super.progressDialog.setMessage("��Ű��ͼ�");
		super.showProDia();	 
		Task task = new Task(this, TaskType.OQC_PhoneOQCBoxBind, Params);	
		CousmerOQCBindmodel.OQCCousmerOQCBinding(task);
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
		.setTitle("ȷ���˳��ͼ쵥��?")
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
					int which) {
				stopService(new Intent(PhoneOQCBoxBindActivity.this,BackgroundService.class));
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no),null).show();
	}

} 
